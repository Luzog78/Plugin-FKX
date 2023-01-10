package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.game.GZone;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
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
                        Stream.of(inv.getArmor()).map(is -> is == null || is.getType() == Material.AIR ?
                                        "    §8- §cnull" : "    §8- §7" + is.getType().toString().charAt(0)
                                        + is.getType().toString().substring(1).toLowerCase() + " §bx" + is.getAmount())
                                .collect(Collectors.joining("\n")),
                        " ",
                        "  §8Contenu (§d" + inv.getContent().stream().filter(predicate).count()
                                + "§8/§c" + inv.getContent().size() + "§8) :   §8§o(aperçu)",
                        inv.getContent().stream().filter(predicate)
                                .limit(maxToShow).map(is -> "    §8- §7" + is.getType().toString().charAt(0)
                                        + is.getType().toString().substring(1).toLowerCase() + " §bx" + is.getAmount())
                                .collect(Collectors.joining("\n"))
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
        Inventory inventory = Guis.getInvInventory("§3Inventaires §f>§b " + inv.getId() + ":" + index, back,
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
                .setMiddleCommandOnClick(Main.CMD + " players " + owner + " inv " + inv.getId() + ":" + index + " del\n" + back)
                .build());
        return inventory;
    }

    public static Inventory getMainInventory(String opener, String player, String back, String navigationBaseCommand,
                                             int page, Map<Integer, Integer> options, String refreshCommand) {
        GPlayer gPlayer = GManager.getCurrentGame().getPlayer(player, false);
        if (gPlayer == null)
            return Guis.getErrorInventory("Le joueur est hors-jeu.", back);

        ArrayList<Utils.Pair<Integer, List<ItemStack>>> content = new ArrayList<>();
        LinkedHashMap<String, ArrayList<Utils.SavedInventory>> invs = new LinkedHashMap<>();
        gPlayer.getInventories().forEach(inv -> {
            if (!invs.containsKey(inv.getId()))
                invs.put(inv.getId(), new ArrayList<>());
            invs.get(inv.getId()).add(inv);
        });
        invs.forEach((id, inventories) -> {
            ArrayList<ItemStack> items = new ArrayList<>();
            inventories.forEach(inv -> {
                int index = inventories.indexOf(inv);
                String cmd = Main.CMD + " players " + player + " inv " + inv.getId() + ":" + index;
                items.add(getInvItem(inv, index + 1 == inventories.size(), index,
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
                        .setName("§3Sauvegarder un inventaire !")
                        .setLore(
                                "§8" + Guis.loreSeparator,
                                "§7Clic Gauche : save son propre inventaire",
                                "§7  (Shift pour se clear après)",
                                " ",
                                "§7Clic Droit : save son propre inventaire sous...",
                                "§7  (Shift pour se clear après)",
                                " ",
                                "§7Clic Molette : save celui d'un autre sous...",
                                " ",
                                "§7Commande :",
                                "§7/" + Main.CMD + " players " + player + " inv §8(§bnull§8|§f<id>§8)§7 save §f<player> §8(§2true§8|§4false§8)"
                        )
                        .setCantClickOn(true)
                        .setLeftRightShiftCommandOnClick(
                                Main.CMD + " players " + player + " inv null save " + opener + " false\n" + refreshCommand,
                                Main.CMD + " players " + player + " inv null save " + opener + " true\n" + refreshCommand,
                                "input " + Main.CMD + " players " + player + " inv %s save " + opener + " false%n" + refreshCommand,
                                "input " + Main.CMD + " players " + player + " inv %s save " + opener + " true%n" + refreshCommand

                        )
                        .setMiddleCommandOnClick("input " + Main.CMD + " players " + player + " inv %s save gui")
                        .build(),
                navigationBaseCommand, page, content
        );
    }

    public static Inventory getLoadInventory(String player, String completeId, boolean delete,
                                             List<String> players, String back,
                                             String navigationBaseCommand, int page) {
        LinkedHashMap<String, Boolean> options = new LinkedHashMap<>();
        Bukkit.getOnlinePlayers().forEach(p -> options.put(p.getName(), players.stream()
                .anyMatch(n -> n.equalsIgnoreCase(p.getName()))));
        players.stream().filter(p -> !options.entrySet().stream().anyMatch(e ->
                e.getKey().equalsIgnoreCase(p))).forEach(p -> options.put(p, true));

        String exeCmd = players.stream().map(p ->
                        Main.CMD + " players " + player + " inv " + completeId + " load on " + p + " false")
                .collect(Collectors.joining("\n"));

        if (delete)
            exeCmd += (exeCmd.length() > 0 ? "\n" : "") + Main.CMD + " players " + player + " inv " + completeId + " del\n"
                    + Main.CMD + " players " + player + " inv";

        return Guis.getSelectorInventory(
                "§3Inventaires §f>§b " + completeId + " §7» §aCharger", false, back,
                GuiPlayers.getHead(player, null, "null"),
                Items.builder(delete ? Material.LAVA_BUCKET : Material.BUCKET)
                        .setName(delete ? "§4Suppression de la sauvegarde" : "§2Conservation de la sauvegarde")
                        .setLore(
                                "§8" + Guis.loreSeparator,
                                "§7Clic pour " + (delete ? "§aconserver" : "§csupprimer"),
                                "§7 ensuite la sauvegarde (après chargement)",
                                " ",
                                "§7Commande :",
                                "§7/" + Main.CMD + " players " + player + " inv " + completeId + " del"
                        )
                        .setCantClickOn(true)
                        .setGlobalCommandOnClick(navigationBaseCommand + " " + !delete + " "
                                + Guis.generateSelectorOptions(page, options.entrySet().stream()
                                .map(entry -> new Utils.Pair<>(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toCollection(ArrayList::new))))
                        .build(),
                navigationBaseCommand + " " + delete, page, options,
                exeCmd, false
        );
    }

    public static Inventory getSaveInventory(String id, String player, boolean clear, String back,
                                             String navigationBaseCommand, int page) {
        return Guis.getPagedInventory("§3Inventaires §f>§b " + id + " §7» §6Sauvegarder", 54, back,
                GuiPlayers.getHead(player, null, "null"),
                Items.builder(clear ? Material.LAVA_BUCKET : Material.BUCKET)
                        .setName(clear ? "§4Clear du stuff" : "§2Conservation du stuff")
                        .setLore(
                                "§8" + Guis.loreSeparator,
                                "§7Clic pour " + (clear ? "§agarder" : "§cclear") + "§7 le stuff",
                                "§7 après la sauvegarde",
                                " ",
                                "§7Commande :",
                                "§7/" + Main.CMD + " players " + player + " inv " + id + " save §8(§f<player>§8) "
                                        + (clear ? "§ctrue" : "§afalse")
                        )
                        .setCantClickOn(true)
                        .setGlobalCommandOnClick(navigationBaseCommand + " " + !clear + " " + page)
                        .build(),
                navigationBaseCommand + " " + clear, page,
                Bukkit.getOnlinePlayers().stream().map(p -> {
                            String cmd = Main.CMD + " players " + player + " inv " + id + " save " + p.getName() + " " + clear;
                            return Items.builder(Heads.getSkullOf(p.getName()))
                                    .setName("§3Sauvegarder §e" + p.getDisplayName())
                                    .setLore(
                                            "§8" + Guis.loreSeparator,
                                            "§7Clic pour sauvegarder.",
                                            " ",
                                            "§7Commande :",
                                            "§7/" + cmd
                                    )
                                    .setCantClickOn(true)
                                    .setGlobalCommandOnClick(cmd + "\n" + back)
                                    .build();
                        })
                        .collect(Collectors.toList())
        );
    }
}
