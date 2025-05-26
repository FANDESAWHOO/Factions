package org.hcgames.hcfactions.util.text;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;

public class Trans extends ChatMessage {

    public Trans() {
        super("", new Object[0]);
    }

    public Trans(String string, Object... objects) {
        super(string, objects);
    }

    public static Trans fromItemStack(ItemStack stack) {
        return ChatUtil.fromItemStack(stack);
    }

    public IChatBaseComponent f() {
        return this; // ChatMessage ya implementa IChatBaseComponent en 1.8.9
    }

    public Trans append(Object object) {
        return this.append(String.valueOf(object));
    }

    public Trans append(String text) {
        return this.append(new ChatComponentText(text));
    }

    public Trans append(IChatBaseComponent node) {
        return (Trans) this.addSibling(node);
    }

    public Trans append(IChatBaseComponent... nodes) {
        for (IChatBaseComponent node : nodes) {
            this.addSibling(node);
        }
        return this;
    }

    public Trans appendItem(ItemStack stack) {
        return this.append(ChatUtil.fromItemStack(stack).f());
    }

    public Trans localText(ItemStack stack) {
        return this.append(ChatUtil.localFromItem(stack).f());
    }

    public Trans setBold(boolean bold) {
        this.getChatModifier().setBold(bold);
        return this;
    }

    public Trans setItalic(boolean italic) {
        this.getChatModifier().setItalic(italic);
        return this;
    }

    public Trans setUnderline(boolean underline) {
        this.getChatModifier().setUnderline(underline);
        return this;
    }

    public Trans setRandom(boolean random) {
        this.getChatModifier().setRandom(random);
        return this;
    }

    public Trans setStrikethrough(boolean strikethrough) {
        this.getChatModifier().setStrikethrough(strikethrough);
        return this;
    }

    public Trans setColor(ChatColor color) {
        this.getChatModifier().setColor(EnumChatFormat.valueOf(color.name()));
        return this;
    }

    public Trans setClick(EnumClickAction action, String value) {
        this.getChatModifier().setChatClickable(new ChatClickable(action, value));
        return this;
    }

    public Trans setHover(EnumHoverAction action, IChatBaseComponent value) {
    	this.getChatModifier().setChatHoverable(new ChatHoverable(action, value));
      //  this.getChatModifier().a(new ChatHoverable(action.getNMS(), value));
        return this;
    }

    public Trans setHoverText(String text) {
        return this.setHover(EnumHoverAction.SHOW_TEXT, new ChatComponentText(text));
    }

    public Trans reset() {
        ChatUtil.reset(this);
        return this;
    }

    public String toRawText() {
        return this.c();
    }

    public void send(CommandSender sender) {
        ChatUtil.send(sender, this);
    }
}