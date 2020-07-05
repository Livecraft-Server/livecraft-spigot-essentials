package com.gmail.mediusecho.livecraft_spigot_essentials.modules.markdown;

import com.gmail.mediusecho.livecraft_spigot_essentials.util.StringUtil;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownFormat implements Comparable<MarkdownFormat> {

    private final String name;
    private final String replacement;

    private final int priority;
    private final Pattern pattern;

    public MarkdownFormat (final String name, final String regex, final String replacement, final int priority)
    {
        this.name = name;
        this.replacement = ChatColor.translateAlternateColorCodes('&',replacement);
        this.priority = priority;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public int compareTo(@NotNull MarkdownFormat o) {
        return Integer.compare(priority, o.priority);
    }

    public String getName () {
        return name;
    }

    public String parseMarkdown (String string)
    {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find())
        {
            String g0 = matcher.group(0);
            String g1 = matcher.group(1);

            String prefix = string.substring(0, matcher.start());
            String suffix = StringUtil.getLastUsedColorCode(prefix);

            string = string.replace(g0, replacement.replace("{1}", g1) + suffix);
        }
        return string;
    }
}
