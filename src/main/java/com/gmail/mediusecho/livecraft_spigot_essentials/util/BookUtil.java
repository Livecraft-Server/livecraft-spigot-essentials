package com.gmail.mediusecho.livecraft_spigot_essentials.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

import com.gmail.mediusecho.livecraft_spigot_essentials.util.ReflectionUtil.PackageType;
import org.jetbrains.annotations.Nullable;

public class BookUtil {

    private static boolean initialized = false;
    private static Method getHandle;
    private static Method openBook;

    static
    {
        try
        {
            getHandle = ReflectionUtil.getMethod("CraftPlayer", PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
            openBook = ReflectionUtil.getMethod("EntityPlayer", PackageType.MINECRAFT_SERVER, "openBook", PackageType.MINECRAFT_SERVER.getClass("ItemStack"), PackageType.MINECRAFT_SERVER.getClass("EnumHand"));
            initialized = true;
        }

        catch (ReflectiveOperationException e) {
            initialized = false;
        }
    }

    /**
     * Checks to see if the book reflection was
     * initialized successfully
     *
     * @return
     */
    public static boolean isInitialized () {
        return initialized;
    }

    /**
     * Opens this book for the player.
     *
     * Does not give the player this book, but simply opens the book gui
     * as if the player was right clicking a book in their toolbar.
     *
     * @param book
     *      The book to open
     * @param player
     *      The player that is opening the book
     * @return
     *      True if the book was opened
     */
    public static boolean openBook (ItemStack book, Player player)
    {
        if (!initialized) {
            return false;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        try
        {
            player.getInventory().setItemInMainHand(book);
            sendPacket(book, player);
        }

        catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        player.getInventory().setItemInMainHand(heldItem);
        return initialized;
    }

    private static void sendPacket (ItemStack book, Player player) throws ReflectiveOperationException
    {
        Object entityPlayer = getHandle.invoke(player);
        Class<?> enumHand = PackageType.MINECRAFT_SERVER.getClass("EnumHand");
        Object[] enumArray = enumHand.getEnumConstants();

        openBook.invoke(entityPlayer, getItemStack(book), enumArray[0]);
    }

    @Nullable
    private static Object getItemStack (ItemStack book)
    {
        try
        {
            Method itemMethod = ReflectionUtil.getMethod(PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), "asNMSCopy", ItemStack.class);
            return itemMethod.invoke(PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), book);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
