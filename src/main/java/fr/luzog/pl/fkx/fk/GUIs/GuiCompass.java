package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.fk.*;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.Utils;
import net.minecraft.server.v1_8_R3.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public class GuiCompass {

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        return Items.builder(Material.COMPASS)
                .setName("§6Compass")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §6Cette option permet aux joueurs",
                        "  §6 de naviguer plus facilement dans",
                        "  §6 l'entièreté de la map...",
                        "  §6N'hésitez pas à l'utiliser !",
                        " ",
                        "  §6Si vous avez la moindre question,",
                        "  §6 n'oubliez pas le  §5§l§n[§2§l§n/AD§2§l§5§l§n]§6 ...",
                        "  §6 Nous serons ravie de vous aider !",
                        " ",
                        "  §6Bonne aventure !  xp",
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getNothingItem() {
        return Items.builder(Material.BARRIER)
                .setName("§cReset Compass")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §cVous n'êtes pas obligé de",
                        "  §c vous faire déranger par le",
                        "  §c compas.",
                        " ",
                        "  §cAlors si vous souhaitez",
                        "  §c libérer votre ActionBar,",
                        "  §c vous ête au bon endroit !",
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour réinitialiser le compas"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick("fk compass nothing")
                .build();
    }

    public static ItemStack getCompassItem(ItemStack base, String name, String commandArgs, Location from, Location loc,
                                           boolean showPosXZ, boolean showPosY, double radius) {
        return Items.builder(base)
                .setName("§f" + name)
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §8Distance : §6" + (Utils.safeDistance(from, loc, true, 2, radius)
                                + "m  §7-  §e" + (from == null ? "" : FKListener.getOrientationChar(from.getYaw(),
                                from.getX(), from.getZ(), loc.getX(), loc.getZ(), radius))),
                        " " + (showPosXZ || showPosY ? "\n  §8Position :" : "")
                                + (showPosXZ ? "\n  §8  > X : §f" + (loc == null ? "§cnull" : loc.getX()) : "")
                                + (showPosY ? "\n  §8  > Y : §f" + (loc == null ? "§cnull" : loc.getY()) : "")
                                + (showPosXZ ? "\n  §8  > Z : §f" + (loc == null ? "§cnull" : loc.getZ()) : "")
                                + (showPosXZ || showPosY ? "\n " : ""),
                        "  §8Monde : §f" + (loc == null ? "§cnull" : Utils.getFormattedWorld(loc.getWorld().getName())),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clique pour suivre §f" + name
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(loc == null ? "" : "fk compass " + commandArgs)
                .build();
    }

    public static Inventory getInventory(Location from, String back, String navigationBaseCommand, int page) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        Inventory inv = Guis.getPagedInventory("§6§lCustom Compass", 54, back,
                getMainItem("Clic pour rafraîchir", navigationBaseCommand + " " + page),
                getNothingItem(),
                navigationBaseCommand, page, new ArrayList<ItemStack>() {{
                    add(getCompassItem(new ItemStack(Material.GOLD_BLOCK), "§6Lobby", "lobby",
                            from, FKManager.getCurrentGame().getLobby().getSpawn(), true, true, FKZone.LOBBY_RADIUS));
                    add(getCompassItem(new ItemStack(Material.REDSTONE_BLOCK), "§4Spawn", "spawn",
                            from, FKManager.getCurrentGame().getSpawn().getSpawn(), true, true, FKZone.SPAWN_RADIUS));
                    addAll(FKManager.getCurrentGame().getTeams().stream().map(t ->
                                    getCompassItem(FKManager.getBanner(t.getColor()), t.getColor() + t.getName(),
                                            "team " + t.getId(), from, t.getSpawn(), true,
                                            false, FKTeam.TEAM_RADIUS))
                            .collect(Collectors.toList()));
                    add(getCompassItem(new ItemStack(Material.OBSIDIAN), "§bPortails du Nether", "nether",
                            from, FKManager.getCurrentGame().getNether().getOverSpawn(), true, false, Portal.RADIUS));
                    add(getCompassItem(new ItemStack(Material.ENDER_PORTAL_FRAME), "§5Portails de l'End", "end",
                            from, FKManager.getCurrentGame().getEnd().getOverSpawn(), true, false, Portal.RADIUS));
                    addAll(FKManager.getCurrentGame().getNormalZones().stream().map(z -> getCompassItem(
                                    new ItemStack(Material.LONG_GRASS, 1, (short) 2), "§2" + z.getId(),
                                    "zone " + z.getId(), from, z.getSpawn(), true, false, FKZone.ZONE_RADIUS))
                            .collect(Collectors.toList()));
                    addAll(FKManager.getCurrentGame().getPickableLocks().getPickableLocks().stream()
                            .filter(l -> l.getLocation() != null && l.isPickable() && !l.isPicked())
                            .map(l ->
                                    getCompassItem(GuiLocks.getLockItem(l, from, null, "null"),
                                            "§9Coffre §b" + l.getId(), "lock " + l.getId(),
                                            from, l.getLocation(), false, false, FKPickableLocks.RADIUS))
                            .collect(Collectors.toList()));
                }});
        inv.setItem(Utils.posOf(5, 5), Items.builder(Material.COMPASS)
                .setName("§aCompass Custom")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §aVous pouvez suivre une direction",
                        "  §a complètement personnalisée.",
                        "  §aPour cela, cliquez sur le compas",
                        "  §a et indiquez les 3 coordonnées",
                        "  §a auxquelles vous souhaitez vous",
                        "  §a rendre.",
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour définir un compas perso"
                )
                .setLeftRightCommandOnClick(
                        "input 3 fk compass custom %s %s %s%nfk compass",
                        "fk compass"
                )
                .setCantClickOn(true)
                .build());
        return inv;
    }

}
