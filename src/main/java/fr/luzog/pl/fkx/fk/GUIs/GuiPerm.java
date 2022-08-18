package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPermissions;
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

    public static ItemStack getPermsItem(FKPermissions perms, Material mat, String permsName, String lastLoreLine, String command) {
        if (perms == null)
            return Items.builder(mat)
                    .setName(permsName)
                    .setLore("§8" + Guis.loreSeparator, "  §cAucune permission", " ", "§8" + Guis.loreSeparator)
                    .setCantClickOn(true)
                    .build();

        Items.Builder b = Items.builder(mat)
                .setName(permsName)
                .setLore("§8" + Guis.loreSeparator, " ");

        for (FKPermissions.Type t : FKPermissions.Type.values())
            b.addLore("  §6" + t.name() + " : §f" + perms.getPermission(t).toFormattedString());

        return b
                .addLore(" ", "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getInv(String back) {
        FKManager fk = FKManager.getCurrentGame();
        if (fk == null)
            return Guis.getErrorInventory("No Game.", back);
        Inventory inv = Guis.getBaseInventory("§fPermissions", 54, back, getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 2), getPermsItem(fk.getPriority(), Material.BEACON,
                "§bPrioritaires", "§7Clic pour voir plus\n \n§7Commande :\n§7/fk perm priority", "fk perm priority"));
        inv.setItem(Utils.posOf(2, 3), getPermsItem(fk.getGlobal(), Material.BEDROCK,
                "§fGlobal", "§7Clic pour voir plus\n \n§7Commande :\n§7/fk perm global", "fk perm global"));
        inv.setItem(Utils.posOf(3, 4), getPermsItem(fk.getNeutral(), Material.GRASS,
                "§fZones Neutres", "§7Clic pour voir plus\n \n§7Commande :\n§7/fk perm neutral", "fk perm neutral"));
        inv.setItem(Utils.posOf(5, 4), getPermsItem(fk.getFriendly(), Material.APPLE,
                "§fZones Amicales", "§7Clic pour voir plus\n \n§7Commande :\n§7/fk perm friendly", "fk perm friendly"));
        inv.setItem(Utils.posOf(6, 3), getPermsItem(fk.getHostile(), Material.DIAMOND_SWORD,
                "§fZones Hostiles", "§7Clic pour voir plus\n \n§7Commande :\n§7/fk perm hostile", "fk perm hostile"));

        return inv;
    }

    public static Inventory getPermsInv(FKPermissions perms, ItemStack main, ItemStack second, String commandSpecification, String back) {
        Inventory inv = Guis.getBaseInventory("§fPermissions", 54, back, main, null);

        int i = 1;
        for (FKPermissions.Type t : FKPermissions.Type.values()) {
            FKPermissions.Definition def = perms.getPermission(t);
            Items.Builder b = Items.builder(def == FKPermissions.Definition.ON ? Items.lime()
                            : def == FKPermissions.Definition.OFF ? Items.red()
                            : Items.gray())
                    .setName("§6" + t.name() + " :  §f" + def.toFormattedString())
                    .setLore(
                            "§8" + Guis.loreSeparator,
                            "§7Clic Gauche pour " + (def == FKPermissions.Definition.ON ? "§4OFF" : "§2ON")
                                    + (def == FKPermissions.Definition.DEFAULT ? "" : "\n§7Clic Droit pour §8DEFAULT"),
                            " ",
                            "§7Commandes :",
                            "§7/fk perm " + commandSpecification + " " + t.name() + " §8(§2ON §8| §4OFF§8)",
                            "§7/fk perm " + commandSpecification + " " + t.name() + " §8DEFAULT"
                    )
                    .setCantClickOn(true)
                    .setLeftRightCommandOnClick("fk perm " + commandSpecification + " " + t.name() + " "
                                    + (perms.getPermission(t) == FKPermissions.Definition.ON ? "off" : "on")
                                    + "\nfk perm " + commandSpecification,
                            (def == FKPermissions.Definition.DEFAULT ? "" : "fk perm " + commandSpecification
                                    + " " + t.name() + " default\n") + "fk perm " + commandSpecification);
            Utils.fill(inv, Utils.posOf(i, 1), Utils.posOf(i, 4), b.build());
            inv.setItem(Utils.posOf(i, i % 2 == 0 ? 4 : 3), b.setType(t == FKPermissions.Type.PVP ?
                    Material.WOOD_SWORD : t == FKPermissions.Type.FRIENDLY_FIRE ? Material.DIAMOND_SWORD
                    : t == FKPermissions.Type.BREAK ? Material.WOOD_PICKAXE : t == FKPermissions.Type.BREAKSPE ?
                    Material.DIAMOND_PICKAXE : t == FKPermissions.Type.MOBS ? Material.SKULL_ITEM
                    : t == FKPermissions.Type.PLACE ? Material.COBBLESTONE : t == FKPermissions.Type.PLACESPE ?
                    Material.TNT : Material.FIREWORK).setDurability((short) (t == FKPermissions.Type.MOBS ? 4 : 0)).build());
            i++;
            if (i > 7)
                break; // Security
        }

        inv.setItem(Utils.posOf(4, 1), second);
        return inv;
    }

}
