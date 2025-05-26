package org.hcgames.hcfactions.util.text;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

@SuppressWarnings("deprecation")
public class ChatUtil {
    public static String getName(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().hasKeyOfType("display", 10)) {
            NBTTagCompound nbttagcompound = stack.getTag().getCompound("display");
            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return nbttagcompound.getString("Name");
            }
        }
        return stack.getItem().a(stack) + ".name"; // a(stack) podría ser getName() en algunas versiones
    }

    public static Trans localFromItem(org.bukkit.inventory.ItemStack stack) {
        if (stack.getType() == Material.POTION && stack.getDurability() == 0) {
            Potion potion = Potion.fromItemStack(stack);
            if (potion != null) {
                PotionType type = potion.getType();
                if (type != null && type != PotionType.WATER) {
                    String effectName = (potion.isSplash() ? "Splash " : "")
                            + WordUtils.capitalizeFully(type.name().replace('_', ' ')) + " L" + potion.getLevel();
                    return fromItemStack(stack).append(" of " + effectName);
                }
            }
        }
        return fromItemStack(stack);
    }

    public static Trans fromItemStack(org.bukkit.inventory.ItemStack stack) {
        ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = new NBTTagCompound();
        nms.save(tag);
        return new Trans(getName(nms), new Object[0])
                .setColor(ChatColor.getByChar(nms.getItem().j(nms)))
                .setHover(EnumHoverAction.SHOW_ITEM, new ChatComponentText(tag.toString()));
    }

    public static void reset(IChatBaseComponent text) {
        ChatModifier modifier = text.getChatModifier();
        modifier.setChatHoverable(null);
        modifier.setChatClickable(null);
        modifier.setBold(false);
        modifier.setColor(EnumChatFormat.RESET);
        modifier.setItalic(false);
        modifier.setRandom(false);
        modifier.setStrikethrough(false);
        modifier.setUnderline(false);
    }

    public static void send(CommandSender sender, IChatBaseComponent text) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // En 1.8.9, el constructor de PacketPlayOutChat usa un byte para el tipo de chat
            PacketPlayOutChat packet = new PacketPlayOutChat(text, (byte) 1);
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.playerConnection.sendPacket((Packet) packet);
        } else {
            sender.sendMessage(text.c());
        }
    }
}