package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.*;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class GuiZones {

    public static ItemStack getZoneItem(GZone z, String lastLoreLine, String command) {
        return Items.builder(Material.LONG_GRASS)
                .setDurability((short) 2)
                .setName("§2" + z.getId())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2Id : §f" + z.getId(),
                        "  §2Type : §7" + z.getType().name(),
                        " ",
                        "  §2---",
                        " ",
                        "  §2Permissions :",
                        "    " + z.getPermissions().getItems().stream().map(i ->
                                        "§7> §6" + i.getType().name() + " §f" + i.getDefinition().toFormattedString())
                                .collect(Collectors.joining("\n    ")),
                        " ",
                        "  §2---",
                        " ",
                        "  §2Spawn : §f" + Utils.locToString(z.getSpawn(), true, true, true),
                        " ",
                        "  §2Pos1 : §7" + Utils.locToString(z.getPos1(), true, false, true),
                        "  §2Pos2 : §7" + Utils.locToString(z.getPos2(), true, false, true),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        if (GManager.getCurrentGame() == null || Main.world == null)
            return Items.builder(Material.CHEST)
                    .setName("§2Zones")
                    .setLore("§8" + Guis.loreSeparator
                            + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                    .setCantClickOn(true)
                    .setGlobalCommandOnClick(command)
                    .build();
        GManager game = GManager.getCurrentGame();
        return Items.builder(Material.LONG_GRASS)
                .setDurability((short) 1)
                .setName("§2Zones")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §9Nombre de zones : §f" + game.getZones().size(),
                        "  §8  > Normales : §3" + game.getNormalZones().size(),
                        "  §8  > + Lobby + Spawn",
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getZoneInventory(GZone z, Location from, String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);

        String refresh = Main.CMD + " zone " + z.getId();

        Inventory inv = Guis.getBaseInventory("§2Zones §f- §a" + z.getId(), 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1), getZoneItem(z,
                "§7Clic pour rafraîchir", refresh));

        String c1 = z.getType().ordinal() == GZone.Type.values().length - 1 ?
                GZone.Type.values()[0].name() : GZone.Type.values()[z.getType().ordinal() + 1].name(),
                c2 = z.getType().ordinal() == 0 ?
                        GZone.Type.values()[GZone.Type.values().length - 1].name()
                        : GZone.Type.values()[z.getType().ordinal() - 1].name();
        inv.setItem(Utils.posOf(2, 2), Items.builder(Material.SIGN)
                .setName("§2Type : §8[" + Arrays.stream(GZone.Type.values())
                        .map(t -> (t.equals(z.getType()) ? "§f§l" : "§8") + t.name())
                        .collect(Collectors.joining("§8, ")) + "§8]")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2§nAttention :",
                        " ",
                        "  §2Si vous changez le type de la zone,",
                        "  §2 les permissions custom ne seront pas",
                        "  §2 prises en compte.",
                        " ",
                        "  §2Type par défaut : §a" + GZone.Type.ZONE.name(),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour trier par le suivant",
                        "§7Clic Droit pour trier par le précédent",
                        "§7Clic Molette pour §fdéfinir§7 le type",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " zone " + z.getId() + " options --t §8" + c1,
                        "§7/" + Main.CMD + " zone " + z.getId() + " options --t §8" + c2,
                        "§7/" + Main.CMD + " zone " + z.getId() + " options --t §f<type>"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        Main.CMD + " zone " + z.getId() + " options --t " + c1 + "\n" + refresh,
                        Main.CMD + " zone " + z.getId() + " options --t " + c2 + "\n" + refresh)
                .setMiddleCommandOnClick("input 1 " + Main.CMD + " zone " + z.getId() + " options --t %s%n" + Main.CMD + " zone " + z.getId())
                .build());
        inv.setItem(Utils.posOf(4, 3), Items.builder(Material.NAME_TAG)
                .setName("§2Identifiant : §6" + z.getId())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour modifier",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " zone " + z.getId() + " id §f<id>"
                )
                .setLeftRightCommandOnClick(
                        "input 1 " + Main.CMD + " zone " + z.getId() + " id %s%n" + Main.CMD + " zone",
                        Main.CMD + " zone " + z.getId()
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(6, 2), GuiPerm.getPermsItem(z.getPermissions(),
                Material.IRON_SWORD, "§fPermissions",
                "§7Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " perm zone " + z.getId(),
                Main.CMD + " perm zone " + z.getId()));

        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        inv.setItem(Utils.posOf(2, 3), Items.builder(Material.BRICK)
                .setName("§2Position 1")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2X : §f" + df.format(z.getPos1().getX()),
                        "  §2Y : §f" + df.format(z.getPos1().getY()),
                        "  §2Z : §f" + df.format(z.getPos1().getZ()),
                        " ",
                        "  §2Monde : §f" + z.getPos1().getWorld().getName(),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " zone " + z.getId() + " options --pos1 §f<x> <y> <z> §8[§f<world>§8]"
                )
                .setLeftRightCommandOnClick(
                        "tp " + z.getPos1().getX() + " " + z.getPos1().getY()
                                + " " + z.getPos1().getZ() + " " + z.getPos1().getWorld().getName(),
                        from == null ? "null" : Main.CMD + " zone " + z.getId() + " options --pos1 " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getWorld().getName() + "\n" + refresh
                )
                .setMiddleCommandOnClick("input 4 " + Main.CMD + " zone " + z.getId() + " options --pos1 %s %s %s %s%n" + Main.CMD + " zone " + z.getId())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(6, 3), Items.builder(Material.BRICK)
                .setName("§2Position 2")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2X : §f" + df.format(z.getPos2().getX()),
                        "  §2Y : §f" + df.format(z.getPos2().getY()),
                        "  §2Z : §f" + df.format(z.getPos2().getZ()),
                        " ",
                        "  §2Monde : §f" + z.getPos2().getWorld().getName(),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " zone " + z.getId() + " options --pos2 §f<x> <y> <z> §8[§f<world>§8]"
                )
                .setLeftRightCommandOnClick(
                        "tp " + z.getPos2().getX() + " " + z.getPos2().getY()
                                + " " + z.getPos2().getZ() + " " + z.getPos2().getWorld().getName(),
                        from == null ? "null" : Main.CMD + " zone " + z.getId() + " options --pos2 " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getWorld().getName() + "\n" + refresh
                )
                .setMiddleCommandOnClick("input 4 " + Main.CMD + " zone " + z.getId() + " options --pos2 %s %s %s %s%n" + Main.CMD + " zone " + z.getId())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(4, 4), Items.builder(Material.NETHER_STAR)
                .setName("§2Spawn")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2X : §f" + df.format(z.getSpawn().getX()),
                        "  §2Y : §f" + df.format(z.getSpawn().getY()),
                        "  §2Z : §f" + df.format(z.getSpawn().getZ()),
                        " ",
                        "  §2Yaw : §7" + df.format(z.getSpawn().getYaw()),
                        "  §2Pitch : §7" + df.format(z.getSpawn().getPitch()),
                        " ",
                        "  §2Monde : §f" + z.getSpawn().getWorld().getName(),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " zone " + z.getId() + " options --s §f<x> <y> <z> §8[§f<yw> <pi>§8] [§f<world>§8]"
                )
                .setLeftRightCommandOnClick(
                        "tp " + z.getSpawn().getX() + " " + z.getSpawn().getY() + " " + z.getSpawn().getZ()
                                + " " + z.getSpawn().getYaw() + " " + z.getSpawn().getPitch()
                                + " " + z.getSpawn().getWorld().getName(),
                        from == null ? "null" : Main.CMD + " zone " + z.getId() + " options --s " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getYaw()
                                + " " + from.getPitch() + " " + from.getWorld().getName() + "\n" + refresh
                )
                .setMiddleCommandOnClick("input 6 " + Main.CMD + " zone " + z.getId() + " options --s %s %s %s %s %s %s%n" + Main.CMD + " zone " + z.getId())
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(7, 4), Items.builder(Material.STAINED_GLASS_PANE)
                .setDurability(DyeColor.RED.getData())
                .setName("§cSupprimer la Zone")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §cCette action est irréversible !",
                        "  §cRéfléchissez-y bien avant",
                        "  §c d'effectuer l'action...",
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Molette pour supprimer la zone",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " zone " + z.getId() + " remove"
                )
                .setMiddleCommandOnClick(Main.CMD + " zone " + z.getId() + " remove\n" + back)
                .setCantClickOn(true)
                .build());

        return inv;
    }

    public static Inventory getMainInventory(String back, String navigationBaseCommand, int page) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("Game null", back);

        return Guis.getPagedInventory("§2Zones", 54, back,
                getMainItem("Clic pour rafraîchir", navigationBaseCommand + " " + page),
                Items.builder(Material.NETHER_STAR)
                        .setName("§2Créer une Zone")
                        .setLore(
                                "§8" + Guis.loreSeparator,
                                "§7Clic Gauche pour créer une zone",
                                " ",
                                "§7Commande :",
                                "§7/" + Main.CMD + " zone create §f<id> §8[§f<opts...>§8]"
                        )
                        .setLeftRightCommandOnClick(
                                "input 2 " + Main.CMD + " zone create %s %s%n" + navigationBaseCommand + " " + page,
                                navigationBaseCommand + " " + page
                        )
                        .setCantClickOn(true)
                        .build(), navigationBaseCommand, page, GManager.getCurrentGame().getZones()
                        .stream().map(z -> getZoneItem(z,
                                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " zone " + z.getId(),
                                Main.CMD + " zone " + z.getId())).collect(Collectors.toList()));
    }
}
