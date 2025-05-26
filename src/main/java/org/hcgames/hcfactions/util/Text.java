package org.hcgames.hcfactions.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;

@SuppressWarnings("deprecation")
public class Text extends ChatComponentText {

    public Text() {
        super("");
    }

    public Text(String string) {
        super(string);
    }

    public Text(Object object) {
        super(String.valueOf(object));
    }

    public static Trans fromItemStack(ItemStack stack) {
        return ChatUtil.fromItemStack(stack);
    }

    public Text append(Object object) {
        return this.append(String.valueOf(object));
    }

    public Text append(String text) {
        return (Text) this.a(text);
    }

    public Text append(IChatBaseComponent node) {
        return (Text) this.addSibling(node);
    }

    public Text append(IChatBaseComponent... nodes) {
        for (IChatBaseComponent node : nodes) {
            this.addSibling(node);
        }
        return this;
    }

    public Text localText(ItemStack stack) {
        return this.append(ChatUtil.localFromItem(stack).f());
    }

    public Text appendItem(ItemStack stack) {
        return this.append(ChatUtil.fromItemStack(stack).f());
    }

    public Text setBold(boolean bold) {
        this.getChatModifier().setBold(bold);
        return this;
    }

    public Text setItalic(boolean italic) {
        this.getChatModifier().setItalic(italic);
        return this;
    }

    public Text setUnderline(boolean underline) {
        this.getChatModifier().setUnderline(underline);
        return this;
    }

    public Text setRandom(boolean random) {
        this.getChatModifier().setRandom(random);
        return this;
    }

    public Text setStrikethrough(boolean strikethrough) {
        this.getChatModifier().setStrikethrough(strikethrough);
        return this;
    }

    public Text setColor(ChatColor color) {
        this.getChatModifier().setColor(EnumChatFormat.valueOf(color.name()));
        return this;
    }

    public Text setClick(EnumClickAction action, String value) {
        this.getChatModifier().setChatClickable(new ChatClickable(action, value));
        return this;
    }

    public Text setHover(EnumHoverAction action, IChatBaseComponent value) {
        this.getChatModifier().setChatHoverable(new ChatHoverable(action, value));
        return this;
    }

    public Text setHoverText(String text) {
        return this.setHover(EnumHoverAction.SHOW_TEXT, new ChatComponentText(text));
    }

    public Text reset() {
        ChatUtil.reset(this);
        return this;
    }

    public IChatBaseComponent f() {
        return this; // Simplificado para 1.8.9
    }

    public String toRawText() {
        return this.c();
    }

    public void send(CommandSender sender) {
        ChatUtil.send(sender, this);
    }

    public void broadcast() {
        this.broadcast(null);
    }

    public void broadcast(String permission) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (permission == null || player.hasPermission(permission)) {
                this.send(player);
            }
        }
        this.send(Bukkit.getConsoleSender());
    }
}