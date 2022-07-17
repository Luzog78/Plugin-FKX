package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.fk.FKListener;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.Items;
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
                .setName("§cCompass")
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

    public static ItemStack getCompassItem(ItemStack base, String name, String commandArgs, Location from, Location loc) {
        return Items.builder(base)
                .setName("§f" + name)
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §8Distance : §6" + (loc != null ? new DecimalFormat("0.00",
                                DecimalFormatSymbols.getInstance(Locale.ENGLISH))
                                .format(from.distance(loc)) + "m  §7-  §e" + FKListener.getOrientationChar(
                                from.getYaw(), from.getX(), from.getZ(), loc.getX(), loc.getZ())
                                : "§cAucune"),
                        " ",
                        "  §8Position :",
                        "  §8  > X : §f" + (loc == null ? "§cnull" : loc.getX()),
//                        "  §8  > Y : §f" + (loc == null ? "§cnull" : loc.getY()),
                        "  §8  > Z : §f" + (loc == null ? "§cnull" : loc.getZ()),
                        " ",
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
        return Guis.getPagedInventory("§6§lCustom Compass", 54, back,
                getMainItem("Clic pour rafraîchir", navigationBaseCommand + " " + page),
                getNothingItem(),
                navigationBaseCommand, page, new ArrayList<ItemStack>() {{
                    add(getCompassItem(new ItemStack(Material.GOLD_BLOCK), "§6Lobby", "lobby",
                            from, FKManager.getCurrentGame().getLobby().getSpawn()));
                    add(getCompassItem(new ItemStack(Material.REDSTONE_BLOCK), "§4Spawn", "spawn",
                            from, FKManager.getCurrentGame().getSpawn().getSpawn()));
                    addAll(FKManager.getCurrentGame().getTeams().stream().map(t ->
                            getCompassItem(FKManager.getBanner(t.getColor()), t.getColor() + t.getName(),
                                    "team " + t.getId(), from, t.getSpawn()))
                            .collect(Collectors.toList()));
                    add(getCompassItem(new ItemStack(Material.OBSIDIAN), "§bPortails du Nether", "nether",
                            from, FKManager.getCurrentGame().getNether().getOverSpawn()));
                    add(getCompassItem(new ItemStack(Material.ENDER_PORTAL_FRAME), "§5Portails de l'End", "end",
                            from, FKManager.getCurrentGame().getEnd().getOverSpawn()));
                    addAll(FKManager.getCurrentGame().getNormalZones().stream().map(z -> getCompassItem(
                            new ItemStack(Material.LONG_GRASS, 1, (short) 2), "§2" + z.getId(),
                            "zone " + z.getId(), from, z.getSpawn())).collect(Collectors.toList()));
                }});
    }

}
