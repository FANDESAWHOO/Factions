package org.hcgames.hcfactions.util.text;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public final class SimpleComponent {

    private final TextComponent root = new TextComponent();

    public SimpleComponent(String text) {
        root.setText(color(text));
    }

    public static SimpleComponent of(String text) {
        return new SimpleComponent(text);
    }

    /* ---------------- Hover ---------------- */

    public SimpleComponent onHover(String... lines) {
        String joined = color(String.join("\n", lines));
        root.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new BaseComponent[]{ new TextComponent(joined) }
        ));
        return this;
    }

    /* ---------------- Click ---------------- */

    public SimpleComponent onClickRunCmd(String cmd) {
        root.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
        return this;
    }

    public SimpleComponent onClickSuggestCmd(String cmd) {
        root.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
        return this;
    }

    public SimpleComponent onClickOpenUrl(String url) {
        root.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return this;
    }

    /* ---------------- Append ---------------- */

    public SimpleComponent append(String text) {
        root.addExtra(new TextComponent(color(text)));
        return this;
    }

    /* ---------------- Send ---------------- */

    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(root);
        } else {
            sender.sendMessage(root.toLegacyText());
        }
    }

    public void broadcast() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(root);
        }
    }

    /* ---------------- Utils ---------------- */

    private static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
