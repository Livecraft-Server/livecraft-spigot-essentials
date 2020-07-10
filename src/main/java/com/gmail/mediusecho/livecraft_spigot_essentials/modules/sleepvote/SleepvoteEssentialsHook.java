/*
 * Copyright (c) 2020 Jacob (MediusEcho)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.sleepvote;

import com.earth2me.essentials.Essentials;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class SleepvoteEssentialsHook implements Listener {

    private final SleepvoteModule sleepvoteModule;
    private Essentials essentials;

    public SleepvoteEssentialsHook (final SleepvoteModule sleepvoteModule)
    {
        this.sleepvoteModule = sleepvoteModule;
        essentials = (Essentials) Essentials.getProvidingPlugin(Essentials.class);
    }

    /**
     * Checks to see if this player is AFK
     *
     * @param player
     *      The player to check
     * @return
     *      Boolean
     */
    public boolean isPlayerAfk (Player player) {
        return essentials.getUser(player).isAfk();
    }

    @EventHandler
    public void onPlayerAfkStatusChange (@NotNull AfkStatusChangeEvent event) {
        sleepvoteModule.onPlayerAfkStatusToggle(event.getAffected().getBase(), event.getValue());
    }

}
