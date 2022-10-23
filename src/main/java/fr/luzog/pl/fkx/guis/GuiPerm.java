package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPermissions;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

    public static Inventory getPermsInv(GPermissions perms, ItemStack main, ItemStack second, String commandSpecification, String back) {
        Inventory inv = Guis.getBaseInventory("§fPermissions", 54, back, main, null);

        int i = 1;
        for (GPermissions.Type t : GPermissions.Type.values()) {
            GPermissions.Definition def = perms.getPermission(t);
            Items.Builder b = Items.builder(def == GPermissions.Definition.ON ? Items.lime()
                            : def == GPermissions.Definition.OFF ? Items.red()
                            : Items.gray())
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
            Utils.fill(inv, Utils.posOf(i, 1), Utils.posOf(i, 4), b.build());
            inv.setItem(Utils.posOf(i, i % 2 == 0 ? 4 : 3), b.setType(t == GPermissions.Type.PVP ?
                    Material.WOOD_SWORD : t == GPermissions.Type.FRIENDLY_FIRE ? Material.DIAMOND_SWORD
                    : t == GPermissions.Type.BREAK ? Material.WOOD_PICKAXE : t == GPermissions.Type.BREAKSPE ?
                    Material.DIAMOND_PICKAXE : t == GPermissions.Type.MOBS ? Material.SKULL_ITEM
                    : t == GPermissions.Type.PLACE ? Material.COBBLESTONE : t == GPermissions.Type.PLACESPE ?
                    Material.TNT : Material.FIREWORK).setDurability((short) (t == GPermissions.Type.MOBS ? 4 : 0)).build());
            i++;
            if (i > 7)
                break; // Security
        }

        inv.setItem(Utils.posOf(4, 1), second);
        return inv;
    }

}
