package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.game.GZone;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiInv {

    public static ItemStack getInvItem(Utils.SavedInventory inv, boolean isLast, int index,
                                       String lastLoreLine, String command) {
        int maxToShow = 5;
        Predicate<ItemStack> predicate = is -> is != null && is.getType() != Material.AIR;
        return Items.builder(isLast ? Material.ENDER_CHEST : Material.CHEST)
                .setName("§b" + inv.getName())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §8§lID : §b" + inv.getId() + "§8 — §f" + index,
                        " ",
                        "  §8Créateur : §b" + inv.getCreator(),
                        "  §8Date : §b" + new Date(inv.getCreation()),
                        " ",
                        "  §8---",
                        " ",
                        "  §8Armure (§d" + Stream.of(inv.getArmor()).filter(predicate).count() + "§8/§c4§8) : §7",
                        "    §8- " + Stream.of(inv.getArmor()).map(is -> is == null || is.getType() == Material.AIR ?
                                        "§cnull" : "§7" + is.getType().toString().charAt(0)
                                        + is.getType().toString().substring(1).toLowerCase() + " §bx" + is.getAmount())
                                .collect(Collectors.joining("\n    §8- ")),
                        " ",
                        "  §8Contenu (§d" + inv.getContent().stream().filter(predicate).count()
                                + "§8/§c" + inv.getContent().size() + "§8) :   §8§o(aperçu)",
                        "    §8- " + inv.getContent().stream().filter(predicate)
                                .limit(maxToShow).map(is -> "§7" + is.getType().toString().charAt(0)
                                        + is.getType().toString().substring(1).toLowerCase() + " §bx" + is.getAmount())
                                .collect(Collectors.joining("\n    §8- "))
                                + (inv.getContent().stream().filter(predicate).count() > maxToShow ? "\n    §8- §7..." : ""),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getInvInventory(ItemStack main, String owner, Utils.SavedInventory inv,
                                            boolean isLast, int index, String back) {
        Inventory inventory = Guis.getInvInventory("§3Inventaires §f>§b " + inv.getName(), back,
                main, getInvItem(inv, isLast, index, null, "null"),
                inv.getArmor(), inv.getContent().toArray(new ItemStack[0]));
        inventory.setItem(Utils.posOf(0, 5), Items.builder(Material.NETHER_STAR)
                .setName("§aCharger l'inventaire")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour s'équiper",
                        "§7  (Shift pour supprimer après)",
                        "§7Clic Droit pour voir les autres",
                        " ",
                        "§7Commandes :",
                        "§7/" + Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " load §8(§2true§8|§4false§8)",
                        "§7/" + Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " load gui"
                )
                .setCantClickOn(true)
                .setLeftRightShiftCommandOnClick(
                        Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " load false",
                        Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " load true\nexit",
                        Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " load gui",
                        Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " load gui"
                )
                .build());
        inventory.setItem(Utils.posOf(8, 5), Items.builder(Material.STAINED_GLASS_PANE)
                .setDurability((short) 14)
                .setName("§cSupprimer l'inventaire")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Molette pour §csupprimer",
                        " ",
                        "§7Commandes :",
                        "§7/" + Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " del"
                )
                .setCantClickOn(true)
                .setMiddleCommandOnClick(Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " del")
                .build());
        return inventory;
    }

    public static Inventory getMainInventory(String player, String back, String navigationBaseCommand,
                                             int page, Map<Integer, Integer> options, String refreshCommand) {
        GPlayer gPlayer = GManager.getCurrentGame().getPlayer(player, false);
        if (gPlayer == null)
            return Guis.getErrorInventory("Le joueur est hors-jeu.", back);

        ArrayList<Utils.Pair<Integer, List<ItemStack>>> content = new ArrayList<>();
        LinkedHashMap<String, ArrayList<Utils.SavedInventory>> invs = new LinkedHashMap<>();
        gPlayer.getInventories().forEach(inv -> {
            if (!invs.containsKey(inv.getName()))
                invs.put(inv.getName(), new ArrayList<>());
            invs.get(inv.getName()).add(inv);
        });
        invs.forEach((id, inventories) -> {
            ArrayList<ItemStack> items = new ArrayList<>();
            inventories.forEach(inv -> {
                int index = inventories.indexOf(inv);
                String cmd = Main.CMD + " players " + player + " inv " + inv.getId() + ":" + index;
                items.add(getInvItem(inv, index + 1 == gPlayer.getInventories().size(), index,
                        "§7Clic pour voir plus\n \n§7Commande :\n§7/" + cmd, cmd));
            });
            Collections.reverse(items);
            content.add(new Utils.Pair<>(0, items));
        });
        options.forEach((idx, sub) -> {
            try {
                content.get(idx).setKey(sub);
            } catch (IndexOutOfBoundsException ignored) {
            }
        });

        return Guis.getComplexScrollingInventory(
                "§aJoueurs §f- §e" + player + " §f» §3Inventaires", 54, back,
                GuiPlayers.getHead(player, "§7Clic pour rafraîchir", refreshCommand + ""),
                Items.builder(Material.NETHER_STAR)
                        .setName("§3Sauvegarder ici son inventaire !")
                        .setLore(
                                "§8" + Guis.loreSeparator,
                                "§7Clic Gauche pour save son inventaire",
                                "§7  (Shift pour se clear après)",
                                " ",
                                "§7Clic Droit pour save son inventaire sous...",
                                "§7  (Shift pour se clear après)",
                                " ",
                                "§7Clic Molette pour save celui de §e" + player + "§7 sous...",
                                "§7  §oIl faut qu'il en ait les droits !",
                                " ",
                                "§7Commande :",
                                "§7/" + Main.CMD + " players " + player + " inv §8(§bnull§8|§f<id>§8)§7 save §8(§2true§8|§4false§8)"
                        )
                        .setCantClickOn(true)
                        .setLeftRightShiftCommandOnClick(
                                Main.CMD + " players " + player + " inv null save false\n" + refreshCommand,
                                Main.CMD + " players " + player + " inv null save true\n" + refreshCommand,
                                "input " + Main.CMD + " players " + player + " inv %s save false\n" + refreshCommand,
                                "input " + Main.CMD + " players " + player + " inv %s save true\n" + refreshCommand

                        )
                        .setMiddleCommandOnClick("input execute " + player + " " + Main.CMD + " players "
                                + player + " inv %s save\n" + refreshCommand)
                        .build(),
                navigationBaseCommand, page, content
        );
    }
}
