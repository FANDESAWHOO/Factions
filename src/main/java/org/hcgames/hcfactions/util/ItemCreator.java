package org.hcgames.hcfactions.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemCreator {

	    private ItemStack item;
	    private ItemMeta meta;

	    // -------------------------------
	    // Constructors
	    // -------------------------------
	    public ItemCreator(Material material) {
	        this(material, 1);
	    }

	    public ItemCreator(Material material, int amount) {
	        this.item = new ItemStack(material, amount);
	        this.meta = item.getItemMeta();
	    }

	    public static ItemCreator of(Material material) {
	        return new ItemCreator(material);
	    }

	    public static ItemCreator of(Material material, String name, String... lore) {
	        return new ItemCreator(material).name(name).lore(lore);
	    }

	    public static ItemCreator copy(ItemStack base) {
	        ItemCreator builder = new ItemCreator(base.getType(), base.getAmount());
	        builder.item = base.clone();
	        builder.meta = base.getItemMeta();
	        return builder;
	    }

	    // -------------------------------
	    // Core methods
	    // -------------------------------
	    public ItemCreator name(String name) {
	        meta.setDisplayName(color(name));
	        return this;
	    }

	    public ItemCreator lore(String... lines) {
	        List<String> lore = new ArrayList<>();
	        for (String line : lines) lore.add(color(line));
	        meta.setLore(lore);
	        return this;
	    }

	    public ItemCreator addLore(String... lines) {
	        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
	        for (String line : lines) lore.add(color(line));
	        meta.setLore(lore);
	        return this;
	    }

	    public ItemCreator enchant(Enchantment ench, int level) {
	        meta.addEnchant(ench, level, true);
	        return this;
	    }

	    public ItemCreator flag(ItemFlag... flags) {
	        meta.addItemFlags(flags);
	        return this;
	    }

	    public ItemCreator unbreakable(boolean state) {
	        try {
	            meta.spigot().setUnbreakable(state);;
	        } catch (Throwable ignored) {}
	        return this;
	    }

	    public ItemCreator color(Color color) {
	        if (meta instanceof LeatherArmorMeta) {
	            ((LeatherArmorMeta) meta).setColor(color);
	        }
	        return this;
	    }

	    public ItemCreator amount(int amount) {
	        item.setAmount(amount);
	        return this;
	    }

	    public ItemCreator owner(String name) {
	        if (meta instanceof SkullMeta) {
	            ((SkullMeta) meta).setOwner(name);
	        }
	        return this;
	    }

	    public ItemCreator book(String title, String author, String... pages) {
	        if (meta instanceof BookMeta) {
	            BookMeta book = (BookMeta) meta;
	            book.setTitle(color(title));
	            book.setAuthor(color(author));
	            for (String page : pages) {
	                book.addPage(color(page));
	            }
	            meta = book;
	        }
	        return this;
	    }

	    // -------------------------------
	    // Finalization
	    // -------------------------------
	    public ItemStack make() {
	        item.setItemMeta(meta);
	        return item;
	    }

	    public ItemStack build() {
	        return make();
	    }

	    public void give(Player player) {
	        player.getInventory().addItem(make());
	    }

	    // -------------------------------
	    // Utility
	    // -------------------------------
	    private String color(String text) {
	        return text == null ? null : text.replace('&', '§');
	    }

	    public String toString() {
	        StringBuilder sb = new StringBuilder();

	        sb.append(item.getType().name()).append(";");
	        sb.append(item.getAmount()).append(";");

	        if (meta.hasDisplayName())
	            sb.append(meta.getDisplayName().replace("§", "&"));
	        sb.append(";");

	        if (meta.hasLore()) {
	            sb.append(String.join("|", meta.getLore()).replace("§", "&"));
	        }
	        sb.append(";");

	        if (!meta.getEnchants().isEmpty()) {
	            List<String> list = new ArrayList<>();
	            meta.getEnchants().forEach((e,l)-> list.add(e.getName() + ":" + l));
	            sb.append(String.join(",", list));
	        }

	        return sb.toString();
	    }
	    
	    public static String toString(ItemStack item) {
	        if (item == null) return "";

	        ItemMeta meta = item.getItemMeta();

	        StringBuilder sb = new StringBuilder();

	        sb.append(item.getType().name()).append(";");
	        sb.append(item.getAmount()).append(";");

	        // name
	        if (meta != null && meta.hasDisplayName())
	            sb.append(meta.getDisplayName().replace("§", "&"));
	        sb.append(";");

	        // lore
	        if (meta != null && meta.hasLore())
	            sb.append(String.join("|", meta.getLore()).replace("§", "&"));
	        sb.append(";");

	        // enchants
	        if (meta != null && !meta.getEnchants().isEmpty()) {
	            List<String> list = new ArrayList<>();
	            meta.getEnchants().forEach((e, l) -> list.add(e.getName() + ":" + l));
	            sb.append(String.join(",", list));
	        }

	        return sb.toString();
	    }

	    
	    public static ItemCreator fromString(String data) {
	        String[] a = data.split(";", -1);

	        Material mat = Material.matchMaterial(a[0]);
	        int amount = Integer.parseInt(a[1]);

	        ItemCreator ib = new ItemCreator(mat, amount);

	        if (!a[2].isEmpty()) ib.name(a[2]);

	        if (!a[3].isEmpty()) {
	            String[] lore = a[3].split("\\|");
	            ib.lore(lore);
	        }

	        if (!a[4].isEmpty()) {
	            String[] ench = a[4].split(",");
	            for (String s : ench) {
	                String[] kv = s.split(":");
	                Enchantment e = Enchantment.getByName(kv[0]);
	                int lvl = Integer.parseInt(kv[1]);
	                if (e != null) ib.enchant(e, lvl);
	            }
	        }

	        return ib;
	    }

	    
	
}