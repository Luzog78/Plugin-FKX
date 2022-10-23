package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class GuiPortals {

    public static enum PortalType {
        NETHER(Material.OBSIDIAN, Material.NETHERRACK, Material.NETHER_BRICK),
        END(Material.ENDER_PORTAL_FRAME, Material.ENDER_STONE, Material.BEDROCK);

        private Material mat, sec, ter;

        private PortalType(Material mat, Material sec, Material ter) {
            this.mat = mat;
            this.sec = sec;
            this.ter = ter;
        }

        public Material getMat() {
            return this.mat;
        }

        public void setMat(Material mat) {
            this.mat = mat;
        }

        public Material getSec() {
            return this.sec;
        }

        public void setSec(Material sec) {
            this.sec = sec;
        }

        public Material getTer() {
            return this.ter;
        }

        public void setTer(Material ter) {
            this.ter = ter;
        }
    }

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        if (GManager.getCurrentGame() == null || Main.world == null)
            return Items.builder(Material.ENDER_PORTAL_FRAME)
                    .setName("§dPortails")
                    .setLore("§8" + Guis.loreSeparator
                            + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                    .setCantClickOn(true)
                    .setGlobalCommandOnClick(command)
                    .build();
        return Items.builder(Material.ENDER_PORTAL_FRAME)
                .setName("§dPortails")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §dNether :  " + (GManager.getCurrentGame().getNether().isOpened() ?
                                "§2§l" + SpecialChars.YES + "§2  Ouvert" : "§4§l" + SpecialChars.NO + "§4  Fermé"),
                        " ",
                        "  §5End :  " + (GManager.getCurrentGame().getEnd().isOpened() ?
                                "§2§l" + SpecialChars.YES + "§2  Ouvert" : "§4§l" + SpecialChars.NO + "§4  Fermé"),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getPortalItem(Portal p, PortalType t, String lastLoreLine, String command) {
        return Items.builder(t.getMat())
                .setName("§d" + p.getName())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §dStatus :  " + (p.isOpened() ? "§2§l" + SpecialChars.YES + "§2  Ouvert"
                                : "§4§l" + SpecialChars.NO + "§4  Fermé"),
                        "  §dCooldown : §7" + (p.getCoolDown() / 20.0) + "s",
                        "  §dConstru :",
                        "  §d  > Fermeture : §f" + p.getClosedMat().name(),
                        "  §d  > Ouverture : §f" + p.getOpenedMat().name(),
                        "  §d  > Data : §f" + p.getData(),
                        " ",
                        "  §d---",
                        " ",
                        "  §2Over :",
                        "  §2  > Spawn : §f" + Utils.locToString(p.getOverSpawn(), true, true, true),
                        "  §2  > Pos1 : §7" + Utils.locToString(p.getOverPortal1(), false, false, true),
                        "  §2  > Pos2 : §7" + Utils.locToString(p.getOverPortal2(), false, false, true),
                        " ",
                        "  §d---",
                        " ",
                        "  §5Dim :",
                        "  §5  > Spawn : §f" + Utils.locToString(p.getDimSpawn(), true, true, true),
                        "  §5  > Pos1 : §7" + Utils.locToString(p.getDimPortal1(), false, false, true),
                        "  §5  > Pos2 : §7" + Utils.locToString(p.getDimPortal2(), false, false, true),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getSideItem(PortalType t, String lastLoreLine, String command) {
        return Items.builder(t.getSec())
                .setName(" ")
                .setLore("§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getMainInventory(String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("Game null", back);

        Inventory inv = Guis.getBaseInventory("§dPortails", 54, back,
                getMainItem("Clic pour rafraîchir", Main.CMD + " portal"), null);

        // "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " zone " + z.getId()

        Utils.fill(inv, Utils.posOf(1, 1), Utils.posOf(3, 3), false, getSideItem(
                PortalType.NETHER, "§7Commande :\n§7/" + Main.CMD + " portal nether", Main.CMD + " portal nether"));
        inv.setItem(Utils.posOf(2, 2), getPortalItem(GManager.getCurrentGame().getNether(),
                PortalType.NETHER, "§7Commande :\n§7/" + Main.CMD + " portal nether", Main.CMD + " portal nether"));

        Utils.fill(inv, Utils.posOf(5, 2), Utils.posOf(7, 4), false, getSideItem(
                PortalType.END, "§7Commande :\n§7/" + Main.CMD + " portal end", Main.CMD + " portal end"));
        inv.setItem(Utils.posOf(6, 3), getPortalItem(GManager.getCurrentGame().getEnd(),
                PortalType.END, "§7Commande :\n§7/" + Main.CMD + " portal end", Main.CMD + " portal end"));

        return inv;
    }

    public static Inventory getPortalInventory(Portal p, PortalType t, Location from, String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);

        String refresh = Main.CMD + " portal " + t.name();

        Inventory inv = Guis.getBaseInventory("§dPortails §f- §5" + p.getName(), 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1), getPortalItem(p, t,
                "§7Clic pour rafraîchir", refresh));

        inv.setItem(Utils.posOf(4, 3), Items.builder(Material.NETHER_STAR)
                .setName("§dStatus :  " + (p.isOpened() ? "§2§l" + SpecialChars.YES + "§2  Ouvert"
                        : "§4§l" + SpecialChars.NO + "§4  Fermé"))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour " + (p.isOpened() ? "§4fermer" : "§2ouvrir"),
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " §8(§2open §8|§4 close§8)"
                )
                .setLeftRightCommandOnClick(
                        Main.CMD + " portal " + t.name() + " " + (p.isOpened() ? "close" : "open") + "\n" + refresh,
                        refresh
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(3, 4), Items.builder(Material.WATCH)
                .setName("§dCooldown : §7" + (p.getCoolDown() / 20.0) + "s")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour délai §2 + 1 §7sec",
                        "§7  (Shift pour délai §2 + 5 §7sec)",
                        "§7Clic Droit pour délai §4 - 1 §7sec",
                        "§7  (Shift pour délai §4 - 5 §7sec)",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " cooldown §f<délai>"
                )
                .setLeftRightShiftCommandOnClick(
                        Main.CMD + " portal " + t.name() + " cooldown " + ((p.getCoolDown() / 20.0) + 1) + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " cooldown " + ((p.getCoolDown() / 20.0) + 5) + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " cooldown " + Math.max((p.getCoolDown() / 20.0) - 1, 0.05) + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " cooldown " + Math.max((p.getCoolDown() / 20.0) - 5, 0.05) + "\n" + refresh
                )
                .setMiddleCommandOnClick("input 1 " + Main.CMD + " portal " + t.name() + " cooldown %s%n" + refresh)
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(5, 4), Items.builder(Material.BRICK)
                .setName("§dConstruction")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §d> Fermeture : §f" + p.getClosedMat().name(),
                        "  §d> Ouverture : §f" + p.getOpenedMat().name(),
                        "  §d> Data : §f" + p.getData(),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour §fdéfinir§7 le",
                        "§7 materiel de fermeture",
                        "§7Clic Droit pour §fdéfinir§7 le",
                        "§7 materiel d'ouverture",
                        "§7Clic Molette pour §fdéfinir§7 la",
                        "§7 data des blocs (orientation)",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " closingMat §f<materiel>",
                        "§7/" + Main.CMD + " portal " + t.name() + " openingMat §f<materiel>",
                        "§7/" + Main.CMD + " portal " + t.name() + " data §f<data>"
                )
                .setLeftRightCommandOnClick(
                        "input 1 " + Main.CMD + " portal " + t.name() + " closingMat %s%n" + refresh,
                        "input 1 " + Main.CMD + " portal " + t.name() + " openingMat %s%n" + refresh
                )
                .setMiddleCommandOnClick("input 1 " + Main.CMD + " portal " + t.name() + " data %s%n" + refresh)
                .setCantClickOn(true)
                .build());

        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        inv.setItem(Utils.posOf(1, 1), Items.builder(Material.GRASS)
                .setName("§2Over Position 1")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2X : §f" + (p.getOverPortal1() == null ? "§cnull" : df.format(p.getOverPortal1().getX())),
                        "  §2Y : §f" + (p.getOverPortal1() == null ? "§cnull" : df.format(p.getOverPortal1().getY())),
                        "  §2Z : §f" + (p.getOverPortal1() == null ? "§cnull" : df.format(p.getOverPortal1().getZ())),
                        " ",
                        "  §2Monde : §f" + (p.getOverPortal1() == null ? "§cnull" : p.getOverPortal1().getWorld().getName()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7  (Shift pour réinitialiser)",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " over pos1 set §f<x> <y> <z> §8[§f<world>§8]",
                        "§7/" + Main.CMD + " portal " + t.name() + " over pos1 reset"
                )
                .setLeftRightShiftCommandOnClick(
                        p.getOverPortal2() == null ? "null"
                                : "tp " + p.getOverPortal1().getX() + " " + p.getOverPortal1().getY()
                                + " " + p.getOverPortal1().getZ() + " " + p.getOverPortal1().getWorld().getName(),
                        refresh,
                        from == null ? "null" : Main.CMD + " portal " + t.name() + " over pos1 set " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getWorld().getName() + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " over pos1 reset\n" + refresh
                )
                .setMiddleCommandOnClick("input 4 " + Main.CMD + " portal " + t.name() + " over pos1 set %s %s %s %s%n" + Main.CMD + " portal " + t.name())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(1, 3), Items.builder(Material.GRASS)
                .setName("§2Over Position 2")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2X : §f" + (p.getOverPortal2() == null ? "§cnull" : df.format(p.getOverPortal2().getX())),
                        "  §2Y : §f" + (p.getOverPortal2() == null ? "§cnull" : df.format(p.getOverPortal2().getY())),
                        "  §2Z : §f" + (p.getOverPortal2() == null ? "§cnull" : df.format(p.getOverPortal2().getZ())),
                        " ",
                        "  §2Monde : §f" + (p.getOverPortal2() == null ? "§cnull" : p.getOverPortal2().getWorld().getName()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7  (Shift pour réinitialiser)",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " over pos2 set §f<x> <y> <z> §8[§f<world>§8]",
                        "§7/" + Main.CMD + " portal " + t.name() + " over pos2 reset"
                )
                .setLeftRightShiftCommandOnClick(
                        p.getOverPortal2() == null ? "null"
                                : "tp " + p.getOverPortal2().getX() + " " + p.getOverPortal2().getY()
                                + " " + p.getOverPortal2().getZ() + " " + p.getOverPortal2().getWorld().getName(),
                        refresh,
                        from == null ? "null" : Main.CMD + " portal " + t.name() + " over pos2 set " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getWorld().getName() + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " over pos2 reset\n" + refresh
                )
                .setMiddleCommandOnClick("input 4 " + Main.CMD + " portal " + t.name() + " over pos2 set %s %s %s %s%n" + Main.CMD + " portal " + t.name())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(2, 2), Items.builder(Material.WOOD)
                .setName("§2Over Spawn")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2X : §f" + (p.getOverSpawn() == null ? "§cnull" : df.format(p.getOverSpawn().getX())),
                        "  §2Y : §f" + (p.getOverSpawn() == null ? "§cnull" : df.format(p.getOverSpawn().getY())),
                        "  §2Z : §f" + (p.getOverSpawn() == null ? "§cnull" : df.format(p.getOverSpawn().getZ())),
                        " ",
                        "  §2Yaw : §7" + (p.getOverSpawn() == null ? "§cnull" : df.format(p.getOverSpawn().getYaw())),
                        "  §2Pitch : §7" + (p.getOverSpawn() == null ? "§cnull" : df.format(p.getOverSpawn().getPitch())),
                        " ",
                        "  §2Monde : §f" + (p.getOverSpawn() == null ? "§cnull" : p.getOverSpawn().getWorld().getName()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7  (Shift pour réinitialiser)",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " over spawn set §f<x> <y> <z>  §8[§f<yw> <pi>§8] [§f<world>§8]",
                        "§7/" + Main.CMD + " portal " + t.name() + " over spawn reset"
                )
                .setLeftRightShiftCommandOnClick(
                        p.getOverSpawn() == null ? "null"
                                : "tp " + p.getOverSpawn().getX() + " " + p.getOverSpawn().getY()
                                + " " + p.getOverSpawn().getZ() + " " + p.getOverSpawn().getYaw()
                                + " " + p.getOverSpawn().getPitch() + " " + p.getOverSpawn().getWorld().getName(),
                        refresh,
                        from == null ? "null" : Main.CMD + " portal " + t.name() + " over spawn set " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getYaw()
                                + " " + from.getPitch() + " " + from.getWorld().getName() + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " over spawn reset\n" + refresh
                )
                .setMiddleCommandOnClick("input 6 " + Main.CMD + " portal " + t.name() + " over spawn set %s %s %s %s %s %s%n" + Main.CMD + " portal " + t.name())
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(7, 1), Items.builder(t.getSec())
                .setName("§5Dim Position 1")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §5X : §f" + (p.getDimPortal1() == null ? "§cnull" : df.format(p.getDimPortal1().getX())),
                        "  §5Y : §f" + (p.getDimPortal1() == null ? "§cnull" : df.format(p.getDimPortal1().getY())),
                        "  §5Z : §f" + (p.getDimPortal1() == null ? "§cnull" : df.format(p.getDimPortal1().getZ())),
                        " ",
                        "  §5Monde : §f" + (p.getDimPortal1() == null ? "§cnull" : p.getDimPortal1().getWorld().getName()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7  (Shift pour réinitialiser)",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " dim pos1 set §f<x> <y> <z> §8[§f<world>§8]",
                        "§7/" + Main.CMD + " portal " + t.name() + " dim pos1 reset"
                )
                .setLeftRightShiftCommandOnClick(
                        p.getDimPortal1() == null ? "null"
                                : "tp " + p.getDimPortal1().getX() + " " + p.getDimPortal1().getY()
                                + " " + p.getDimPortal1().getZ() + " " + p.getDimPortal1().getWorld().getName(),
                        refresh,
                        from == null ? "null" : Main.CMD + " portal " + t.name() + " dim pos1 set " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getWorld().getName() + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " dim pos1 reset\n" + refresh
                )
                .setMiddleCommandOnClick("input 4 " + Main.CMD + " portal " + t.name() + " dim pos1 set %s %s %s %s%n" + Main.CMD + " portal " + t.name())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(7, 3), Items.builder(t.getSec())
                .setName("§5Dim Position 2")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §5X : §f" + (p.getDimPortal2() == null ? "§cnull" : df.format(p.getDimPortal2().getX())),
                        "  §5Y : §f" + (p.getDimPortal2() == null ? "§cnull" : df.format(p.getDimPortal2().getY())),
                        "  §5Z : §f" + (p.getDimPortal2() == null ? "§cnull" : df.format(p.getDimPortal2().getZ())),
                        " ",
                        "  §5Monde : §f" + (p.getDimPortal2() == null ? "§cnull" : p.getDimPortal2().getWorld().getName()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7  (Shift pour réinitialiser)",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " dim pos2 set §f<x> <y> <z> §8[§f<world>§8]",
                        "§7/" + Main.CMD + " portal " + t.name() + " dim pos2 reset"
                )
                .setLeftRightShiftCommandOnClick(
                        p.getDimPortal2() == null ? "null"
                                : "tp " + p.getDimPortal2().getX() + " " + p.getDimPortal2().getY()
                                + " " + p.getDimPortal2().getZ() + " " + p.getDimPortal2().getWorld().getName(),
                        refresh,
                        from == null ? "null" : Main.CMD + " portal " + t.name() + " dim pos2 set " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getWorld().getName() + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " dim pos2 reset\n" + refresh
                )
                .setMiddleCommandOnClick("input 4 " + Main.CMD + " portal " + t.name() + " dim pos2 set %s %s %s %s%n" + Main.CMD + " portal " + t.name())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(6, 2), Items.builder(t.getTer())
                .setName("§5Dim Spawn")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §5X : §f" + (p.getDimSpawn() == null ? "§cnull" : df.format(p.getDimSpawn().getX())),
                        "  §5Y : §f" + (p.getDimSpawn() == null ? "§cnull" : df.format(p.getDimSpawn().getY())),
                        "  §5Z : §f" + (p.getDimSpawn() == null ? "§cnull" : df.format(p.getDimSpawn().getZ())),
                        " ",
                        "  §5Yaw : §7" + (p.getDimSpawn() == null ? "§cnull" : df.format(p.getDimSpawn().getYaw())),
                        "  §5Pitch : §7" + (p.getDimSpawn() == null ? "§cnull" : df.format(p.getDimSpawn().getPitch())),
                        " ",
                        "  §5Monde : §f" + (p.getDimSpawn() == null ? "§cnull" : p.getDimSpawn().getWorld().getName()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se tp",
                        "§7Clic Droit pour redéfinir ici",
                        "§7  (Shift pour réinitialiser)",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " portal " + t.name() + " dim spawn set §f<x> <y> <z>  §8[§f<yw> <pi>§8] [§f<world>§8]",
                        "§7/" + Main.CMD + " portal " + t.name() + " dim spawn reset"
                )
                .setLeftRightShiftCommandOnClick(
                        p.getDimSpawn() == null ? "null"
                                : "tp " + p.getDimSpawn().getX() + " " + p.getDimSpawn().getY()
                                + " " + p.getDimSpawn().getZ() + " " + p.getDimSpawn().getYaw()
                                + " " + p.getDimSpawn().getPitch() + " " + p.getDimSpawn().getWorld().getName(),
                        refresh,
                        from == null ? "null" : Main.CMD + " portal " + t.name() + " dim spawn set " + from.getX()
                                + " " + from.getY() + " " + from.getZ() + " " + from.getYaw()
                                + " " + from.getPitch() + " " + from.getWorld().getName() + "\n" + refresh,
                        Main.CMD + " portal " + t.name() + " dim spawn reset\n" + refresh
                )
                .setMiddleCommandOnClick("input 6 " + Main.CMD + " portal " + t.name() + " dim spawn set %s %s %s %s %s %s%n" + Main.CMD + " portal " + t.name())
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(4, 4), Items.builder(Material.STAINED_GLASS_PANE)
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
                        "§7Commande :"
                )
                .setCantClickOn(true)
                .build());

        return inv;
    }
}
