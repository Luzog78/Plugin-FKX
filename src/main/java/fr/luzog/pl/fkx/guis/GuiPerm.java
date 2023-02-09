package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPermissions;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiPerm {

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        return Items.builder(Material.IRON_SWORD)
                .setName("§fPermissions")
                .setLore("§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getPermsItem(GPermissions perms, Material mat, String permsName, String lastLoreLine, String command) {
        if (perms == null)
            return Items.builder(mat)
                    .setName(permsName)
                    .setLore("§8" + Guis.loreSeparator, "  §cAucune permission", " ", "§8" + Guis.loreSeparator)
                    .setCantClickOn(true)
                    .build();

        Items.Builder b = Items.builder(mat)
                .setName(permsName)
                .setLore("§8" + Guis.loreSeparator, " ");

        for (GPermissions.Type t : GPermissions.Type.values())
            b.addLore("  §6" + t.name() + " : §f" + perms.getPermission(t).toFormattedString());

        return b
                .addLore(" ", "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getInv(String back) {
        GManager game = GManager.getCurrentGame();
        if (game == null)
            return Guis.getErrorInventory("No Game.", back);
        Inventory inv = Guis.getBaseInventory("§fPermissions", 54, back, getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 2), getPermsItem(game.getPriority(), Material.BEACON,
                "§bPrioritaires", "§7Clic pour voir plus\n \n§7Commande :\n§7" + Main.CMD + " perm priority", Main.CMD + " perm priority"));
        inv.setItem(Utils.posOf(2, 3), getPermsItem(game.getGlobal(), Material.BEDROCK,
                "§fGlobal", "§7Clic pour voir plus\n \n§7Commande :\n§7" + Main.CMD + " perm global", Main.CMD + " perm global"));
        inv.setItem(Utils.posOf(3, 4), getPermsItem(game.getNeutral(), Material.GRASS,
                "§fZones Neutres", "§7Clic pour voir plus\n \n§7Commande :\n§7" + Main.CMD + " perm neutral", Main.CMD + " perm neutral"));
        inv.setItem(Utils.posOf(5, 4), getPermsItem(game.getFriendly(), Material.APPLE,
                "§fZones Amicales", "§7Clic pour voir plus\n \n§7Commande :\n§7" + Main.CMD + " perm friendly", Main.CMD + " perm friendly"));
        inv.setItem(Utils.posOf(6, 3), getPermsItem(game.getHostile(), Material.DIAMOND_SWORD,
                "§fZones Hostiles", "§7Clic pour voir plus\n \n§7Commande :\n§7" + Main.CMD + " perm hostile", Main.CMD + " perm hostile"));

        return inv;
    }

    public static Utils.Pair<ItemStack, ItemStack> getPermItem(GPermissions perms, GPermissions.Type t, String commandSpecification) {
        GPermissions.Definition def = perms.getPermission(t);
        Items.Builder b = Items.builder(Material.INK_SACK)
                .setDurability((short) (def == GPermissions.Definition.ON ? 10 : def == GPermissions.Definition.OFF ? 1 : 8))
                .setName("§6" + t.name() + " :  §f" + def.toFormattedString())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour " + (def == GPermissions.Definition.ON ? "§4OFF" : "§2ON")
                                + (def == GPermissions.Definition.DEFAULT ? "" : "\n§7Clic Droit pour §8DEFAULT"),
                        " ",
                        "§7Commandes :",
                        "§7" + Main.CMD + " perm " + commandSpecification + " " + t.name() + " §8(§2ON §8| §4OFF§8)",
                        "§7" + Main.CMD + " perm " + commandSpecification + " " + t.name() + " §8DEFAULT"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(Main.CMD + " perm " + commandSpecification + " " + t.name() + " "
                                + (perms.getPermission(t) == GPermissions.Definition.ON ? "off" : "on")
                                + "\n" + Main.CMD + " perm " + commandSpecification,
                        (def == GPermissions.Definition.DEFAULT ? "" : Main.CMD + " perm " + commandSpecification
                                + " " + t.name() + " default\n") + Main.CMD + " perm " + commandSpecification);
        ItemStack temp = b.build().clone();
        b.setDurability((short) 0);
        switch (t) {
            case PVP:
                b.setType(Material.WOOD_SWORD);
                break;

            case FRIENDLY_FIRE:
                b.setType(Material.DIAMOND_SWORD);
                break;

            case MOBS:
                b.setType(Material.SKULL_ITEM).setDurability((short) 4);
                break;

            case BREAK:
                b.setType(Material.WOOD_PICKAXE);
                break;

            case BREAKSPE:
                b.setType(Material.DIAMOND_PICKAXE);
                break;

            case PLACE:
                b.setType(Material.COBBLESTONE);
                break;

            case PLACESPE:
                b.setType(Material.TNT);
                break;

            case CHAT_GLOBAL:
                b.setType(Material.PAPER);
                break;

            case CHAT_TEAM:
                b.setType(Material.EMPTY_MAP);
                break;

            case CHAT_PRIVATE:
                b.setType(Material.MAP);
                break;

            case FK:
                ItemStack fk = GManager.getBanner();
                BannerMeta fkM = (BannerMeta) fk.getItemMeta(),
                        m = (BannerMeta) b.setType(fk.getType()).setDurability(fk.getDurability()).update().getMeta();
                if (fkM.getBaseColor() != null)
                    m.setBaseColor(fkM.getBaseColor());
                m.setPatterns(fkM.getPatterns());
                b.setMeta(m);
                break;

            case KICK_WARN:
                b.setType(Material.BOOK_AND_QUILL);
                break;

            case BAN:
                b.setType(Material.NETHER_STAR);
                break;
        }
        return new Utils.Pair<>(b.build(), temp);
    }

    public static Inventory getPermsInv(GPermissions perms, ItemStack main, ItemStack second, String commandSpecification, String back) {
        Inventory inv = Guis.getBaseInventory("§fPermissions", 54, back, main, null);

        int[] j = {10, 11, 12, 13, 14, 15, 16, 28, 29, 30, 32, 33, 34};
        for (int i = 0; i < j.length; i++)
            try {
                Utils.Pair<ItemStack, ItemStack> pair = getPermItem(perms, GPermissions.Type.values()[i], commandSpecification);
                inv.setItem(j[i], pair.getKey());
                inv.setItem(j[i] + 9, pair.getValue());
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

        inv.setItem(Utils.posOf(4, 1), second);
        return inv;
    }

}
