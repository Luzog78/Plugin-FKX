package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.events.Events;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Items {

    public static ItemStack air() {
        return i(Material.AIR);
    }

    public static ItemStack orange() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 1, " ", false, true, true);
    }

    public static ItemStack l_blue() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 3, " ", false, true, true);
    }

    public static ItemStack yellow() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 4, " ", false, true, true);
    }

    public static ItemStack lime() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 5, " ", false, true, true);
    }

    public static ItemStack gray() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 7, " ", false, true, true);
    }

    public static ItemStack l_gray() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 8, " ", false, true, true);
    }

    public static ItemStack cyan() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 9, " ", false, true, true);
    }

    public static ItemStack blue() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 11, " ", false, true, true);
    }

    public static ItemStack red() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 14, " ", false, true, true);
    }

    public static ItemStack black() {
        return i(Material.STAINED_GLASS_PANE, 1, (short) 15, " ", false, true, true);
    }

    public static ItemStack stone_sword() {
        return builder(Material.STONE_SWORD).setName("§7Épée en Pierre").build();
    }

    public static ItemStack stone_pickaxe() {
        return builder(Material.STONE_PICKAXE).setName("§7Pioche en Pierre").addEnchant(Enchantment.DIG_SPEED, 1).build();
    }

    public static ItemStack stone_axe() {
        return builder(Material.STONE_AXE).setName("§7Hache en Pierre").addEnchant(Enchantment.DIG_SPEED, 1).build();
    }

    public static ItemStack stone_shovel() {
        return builder(Material.STONE_SPADE).setName("§7Pelle en Pierre").addEnchant(Enchantment.DIG_SPEED, 1).build();
    }

    public static ItemStack stone_hoe() {
        return builder(Material.STONE_HOE).setName("§7Houe en Pierre").addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1).build();
    }

    public static ItemStack leather_helmet() {
        return builder(Material.LEATHER_HELMET).setName("§cCasque en Cuir").build();
    }

    public static ItemStack leather_chestplate() {
        return builder(Material.LEATHER_CHESTPLATE).setName("§cPlastron en Cuir").build();
    }

    public static ItemStack leather_leggings() {
        return builder(Material.LEATHER_LEGGINGS).setName("§cJambières en Cuir").build();
    }

    public static ItemStack leather_boots() {
        return builder(Material.LEATHER_BOOTS).setName("§cBottines en Cuir").build();
    }

    public static ItemStack iron_sword() {
        return builder(Material.IRON_SWORD).setName("§fÉpée en Fer").build();
    }

    public static ItemStack iron_pickaxe() {
        return builder(Material.IRON_PICKAXE).setName("§fPioche en Fer").build();
    }

    public static ItemStack iron_axe() {
        return builder(Material.IRON_AXE).setName("§fHache en Fer").build();
    }

    public static ItemStack iron_shovel() {
        return builder(Material.IRON_SPADE).setName("§fPelle en Fer").build();
    }

    public static ItemStack iron_hoe() {
        return builder(Material.IRON_HOE).setName("§fHoue en Fer").build();
    }

    public static ItemStack iron_helmet() {
        return builder(Material.IRON_HELMET).setName("§fCasque en Fer").build();
    }

    public static ItemStack iron_chestplate() {
        return builder(Material.IRON_CHESTPLATE).setName("§fPlastron en Fer").build();
    }

    public static ItemStack iron_leggings() {
        return builder(Material.IRON_LEGGINGS).setName("§fJambières en Fer").build();
    }

    public static ItemStack iron_boots() {
        return builder(Material.IRON_BOOTS).setName("§fBottines en Fer").build();
    }

    public static ItemStack gold_sword() {
        return builder(Material.GOLD_SWORD).setName("§6Épée en Or").build();
    }

    public static ItemStack gold_pickaxe() {
        return builder(Material.GOLD_PICKAXE).setName("§6Pioche en Or").build();
    }

    public static ItemStack gold_axe() {
        return builder(Material.GOLD_AXE).setName("§6Hache en Or").build();
    }

    public static ItemStack gold_shovel() {
        return builder(Material.GOLD_SPADE).setName("§6Pelle en Or").build();
    }

    public static ItemStack gold_hoe() {
        return builder(Material.GOLD_HOE).setName("§6Houe en Or").build();
    }

    public static ItemStack gold_helmet() {
        return builder(Material.GOLD_HELMET).setName("§6Casque en Or").build();
    }

    public static ItemStack gold_chestplate() {
        return builder(Material.GOLD_CHESTPLATE).setName("§6Plastron en Or").build();
    }

    public static ItemStack gold_leggings() {
        return builder(Material.GOLD_LEGGINGS).setName("§6Jambières en Or").build();
    }

    public static ItemStack gold_boots() {
        return builder(Material.GOLD_BOOTS).setName("§6Bottines en Or").build();
    }

    public static ItemStack diamond_sword() {
        return builder(Material.DIAMOND_SWORD).setName("§bÉpée en Diamant").build();
    }

    public static ItemStack diamond_pickaxe() {
        return builder(Material.DIAMOND_PICKAXE).setName("§bPioche en Diamant").build();
    }

    public static ItemStack diamond_axe() {
        return builder(Material.DIAMOND_AXE).setName("§bHache en Diamant").build();
    }

    public static ItemStack diamond_shovel() {
        return builder(Material.DIAMOND_SPADE).setName("§bPelle en Diamant").build();
    }

    public static ItemStack diamond_hoe() {
        return builder(Material.DIAMOND_HOE).setName("§bHoue en Diamant").build();
    }

    public static ItemStack diamond_helmet() {
        return builder(Material.DIAMOND_HELMET).setName("§bCasque en Diamant").build();
    }

    public static ItemStack diamond_chestplate() {
        return builder(Material.DIAMOND_CHESTPLATE).setName("§bPlastron en Diamant").build();
    }

    public static ItemStack diamond_leggings() {
        return builder(Material.DIAMOND_LEGGINGS).setName("§bJambières en Diamant").build();
    }

    public static ItemStack diamond_boots() {
        return builder(Material.DIAMOND_BOOTS).setName("§bBottines en Diamant").build();
    }

    public static ItemStack bow() {
        return builder(Material.BOW).setName("§fArc").build();
    }

    public static ItemStack fishing_rod() {
        return builder(Material.FISHING_ROD).setName("§fCanne à pêche").build();
    }

    public static ItemStack tnt() {
        return builder(Material.TNT).setName("§cSainte TNT").build();
    }

    public static ItemStack arrow() {
        return builder(Material.ARROW).setName("§fFlèche").build();
    }

    public static ItemStack snowball() {
        return builder(Material.SNOW_BALL).setName("§fBoule de Neige").build();
    }

    public static ItemStack egg() {
        return builder(Material.EGG).setName("§fOeuf").build();
    }

    public static ItemStack getBanner(DyeColor color) {
        return i(Material.BANNER, 1, (short) (15 - color.getData()), "Bannière : " + color.name() + " " + color.getData());
    }

    @SafeVarargs
    public static ItemStack i(Material material, Utils.Pair<Enchantment, Integer>... enchants) {
        return i(material, 1, enchants);
    }

    @SafeVarargs
    public static ItemStack i(Material material, int amount, Utils.Pair<Enchantment, Integer>... enchants) {
        return i(material, amount, null, enchants);
    }

    @SafeVarargs
    public static ItemStack i(Material material, int amount, @Nullable String name, Utils.Pair<Enchantment, Integer>... enchants) {
        return i(material, amount, (short) 0, name, enchants);
    }

    @SafeVarargs
    public static ItemStack i(Material material, int amount, short damage, @Nullable String name, Utils.Pair<Enchantment, Integer>... enchants) {
        return i(material, amount, damage, name, false, enchants);
    }

    @SafeVarargs
    public static ItemStack i(Material material, int amount, short damage, @Nullable String name, boolean unbreakable, Utils.Pair<Enchantment, Integer>... enchants) {
        return i(material, amount, damage, name, unbreakable, false, enchants);
    }

    @SafeVarargs
    public static ItemStack i(Material material, int amount, short damage, @Nullable String name, boolean unbreakable, boolean hideAttributes, Utils.Pair<Enchantment, Integer>... enchants) {
        return i(material, amount, damage, name, unbreakable, hideAttributes, false, enchants);
    }

    @SafeVarargs
    public static ItemStack i(Material material, int amount, short damage, @Nullable String name, boolean unbreakable, boolean hideAttributes, boolean cantClickOn, Utils.Pair<Enchantment, Integer>... enchants) {
        ItemStack is = new ItemStack(material, amount, damage);
        ItemMeta meta = is.getItemMeta();
        if (meta == null)
            return is;
        if (name != null)
            meta.setDisplayName(name);
        if (hideAttributes)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON);
        meta.spigot().setUnbreakable(unbreakable);
        for (Utils.Pair<Enchantment, Integer> e : enchants)
            meta.addEnchant(e.getKey(), e.getValue(), true);
        is.setItemMeta(meta);
        return cantClickOn ? new CustomNBT(is).setBoolean(Events.cantClickOnTag, true).build() : is;
    }

    public static Builder builder(Material mat) {
        return new Builder(mat);
    }

    public static Builder builder(ItemStack item) {
        return new Builder(item.clone());
    }

    public static class Builder {
        private ItemStack item;
        private ItemMeta meta;

        public Builder(ItemStack item) {
            this.item = item;
            meta = this.item.getItemMeta();
        }

        public Builder(Material mat) {
            item = new ItemStack(mat);
            meta = item.getItemMeta();
        }

        public Builder update() {
            item.setItemMeta(meta);
            return this;
        }

        public ItemStack build() {
            update();
            return item;
        }

        public CustomNBT getNBT() {
            return new CustomNBT(build());
        }

        public Builder setItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public ItemMeta getMeta() {
            return meta;
        }

        public Builder setMeta(ItemMeta meta) {
            this.meta = meta;
            return this;
        }

        public Builder setName(String displayName) {
            meta.setDisplayName(displayName);
            return this;
        }

        public Builder setLore(String... loreLines) {
            ArrayList<String> lore = new ArrayList<>();
            for (String l : loreLines)
                lore.addAll(Arrays.asList(l.split("\n")));
            meta.setLore(lore);
            return this;
        }

        public Builder setLore(List<String> lore) {
            meta.setLore(lore);
            return this;
        }

        public Builder addLore(String... loreLines) {
            meta.setLore(new ArrayList<String>(meta.getLore()) {{
                for (String l : loreLines)
                    addAll(Arrays.asList(l.split("\n")));
            }});
            return this;
        }

        public Builder addLore(String loreLine) {
            meta.setLore(new ArrayList<String>(meta.getLore()) {{
                addAll(Arrays.asList(loreLine.split("\n")));
            }});
            return this;
        }

        public Builder addLore(int index, String loreLine) {
            meta.setLore(new ArrayList<String>(meta.getLore()) {{
                addAll(index, Arrays.asList(loreLine.split("\n")));
            }});
            return this;
        }

        public Builder addLore(int index, String... loreLines) {
            meta.setLore(new ArrayList<String>(meta.getLore()) {{
                ArrayList<String> lore = new ArrayList<>();
                for (String l : loreLines)
                    lore.addAll(Arrays.asList(l.split("\n")));
                this.addAll(index, lore);
            }});
            return this;
        }

        public Builder remLore(String loreLine) {
            meta.setLore(new ArrayList<String>(meta.getLore()) {{
                remove(loreLine);
            }});
            return this;
        }

        public Builder remLore(int index) {
            meta.setLore(new ArrayList<String>(meta.getLore()) {{
                remove(index);
            }});
            return this;
        }

        public Builder addEnchant(Enchantment type, @Nullable Integer lvl) {
            if (lvl == null || meta.hasEnchant(type))
                meta.removeEnchant(type);
            else
                meta.addEnchant(type, lvl, true);
            return this;
        }

        public Builder remEnchant(Enchantment type) {
            meta.removeEnchant(type);
            return this;
        }

        public Builder setUnbreakable(boolean unbreakable) {
            meta.spigot().setUnbreakable(unbreakable);
            return this;
        }

        public Builder addFlag(ItemFlag... flags) {
            meta.addItemFlags(flags);
            return this;
        }

        public Builder remFlag(ItemFlag... flags) {
            meta.removeItemFlags(flags);
            return this;
        }

        @Deprecated
        public Builder setType(Material mat) {
            item.setType(mat);
            return this;
        }

        @Deprecated
        public Builder setType(int id) {
            item.setTypeId(id);
            return this;
        }

        public Builder setAmount(int amount) {
            item.setAmount(amount);
            return this;
        }

        public Builder setDurability(short durability) {
            item.setDurability(durability);
            return this;
        }

        public Builder setData(MaterialData data) {
            item.setData(data);
            return this;
        }

        public boolean getCantClickOn() {
            return getNBT().getBoolean(Events.cantClickOnTag);
        }

        public Builder setCantClickOn(boolean cantClickOn) {
            item = getNBT().setBoolean(Events.cantClickOnTag, cantClickOn).build();
            meta = item.getItemMeta();
            return this;
        }

        public boolean getCloseOnClick() {
            return getNBT().getBoolean(Events.closeTag);
        }

        public Builder setCloseOnClick(boolean closeOnClick) {
            item = getNBT().setBoolean(Events.closeTag, closeOnClick).build();
            meta = item.getItemMeta();
            return this;
        }

        public Builder setGlobalCommandOnClick(String cmd) {
            item = getNBT()
                    .setString(Events.exeLeftTag, cmd)
                    .setString(Events.exeShiftLeftTag, cmd)
                    .setString(Events.exeRightTag, cmd)
                    .setString(Events.exeShiftRightTag, cmd)
                    .setString(Events.exeMiddleTag, cmd)
                    .build();
            meta = item.getItemMeta();
            return this;
        }

        public Builder setMiddleCommandOnClick(String cmd) {
            item = getNBT()
                    .setString(Events.exeMiddleTag, cmd)
                    .build();
            meta = item.getItemMeta();
            return this;
        }

        public Builder setShiftCommandOnClick(String cmd, String cmdShift) {
            item = getNBT()
                    .setString(Events.exeLeftTag, cmd)
                    .setString(Events.exeShiftLeftTag, cmdShift)
                    .setString(Events.exeRightTag, cmd)
                    .setString(Events.exeShiftRightTag, cmdShift)
                    .build();
            meta = item.getItemMeta();
            return this;
        }

        public Builder setLeftRightCommandOnClick(String cmdLeft, String cmdRight) {
            item = getNBT()
                    .setString(Events.exeLeftTag, cmdLeft)
                    .setString(Events.exeShiftLeftTag, cmdLeft)
                    .setString(Events.exeRightTag, cmdRight)
                    .setString(Events.exeShiftRightTag, cmdRight)
                    .build();
            meta = item.getItemMeta();
            return this;
        }

        public Builder setLeftRightShiftCommandOnClick(String cmdLeft, String cmdShiftLeft, String cmdRight, String cmdShiftRight) {
            item = getNBT()
                    .setString(Events.exeLeftTag, cmdLeft)
                    .setString(Events.exeShiftLeftTag, cmdShiftLeft)
                    .setString(Events.exeRightTag, cmdRight)
                    .setString(Events.exeShiftRightTag, cmdShiftRight)
                    .build();
            meta = item.getItemMeta();
            return this;
        }

        public Builder setCompleteCommandOnClick(String cmdLeft, String cmdShiftLeft, String cmdRight, String cmdShiftRight, String cmdMiddle) {
            item = getNBT()
                    .setString(Events.exeLeftTag, cmdLeft)
                    .setString(Events.exeShiftLeftTag, cmdShiftLeft)
                    .setString(Events.exeRightTag, cmdRight)
                    .setString(Events.exeShiftRightTag, cmdShiftRight)
                    .setString(Events.exeMiddleTag, cmdMiddle)
                    .build();
            meta = item.getItemMeta();
            return this;
        }
    }

}
