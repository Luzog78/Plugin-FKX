package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.commands.Cheat.Freeze;
import fr.luzog.pl.fkx.commands.Fun.Head;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class GuiPlayers {

    public static ItemStack getSimplifiedMain(String lastLoreLine, String command) {
        return getMain(null, lastLoreLine, command, null, null, null);
    }

    public static ItemStack getMain(@Nullable String from, String lastLoreLine, String command,
                                    Integer size, Integer online, Integer max) {
        return Items.builder(Heads.MISC_EARTH.getSkull())
                .setName("§aListe des joueurs")
                .setLore((from == null ? "" : "§a de " + from + "\n")
                        + "§8" + Guis.loreSeparator
                        + (size == null && online == null && max == null ? "" : "\n ")
                        + (size == null ? "" : "\n  §aJoueurs : §f" + size)
                        + (online == null ? "" : "\n  §aJoueurs en ligne : §3" + online)
                        + (size == null || online == null ? "" : "\n  §aJoueurs hors ligne : §c" + (size - online))
                        + (max == null ? "" : "\n  §aJoueurs max : §7" + max)
                        + (size == null && online == null && max == null ? "" : "\n ")
                        + (size == null && online == null && max == null ? "" : "\n§8" + Guis.loreSeparator)
                        + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getHead(String player, String lastLoreLine, String command) {
        if (player == null)
            return Items.builder(Heads.CHAR_QUESTION_MARK_BLUE.getSkull()).setName("§c???").setLore(new ArrayList<>()).setCantClickOn(true).build();
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GPlayer gPlayer = GManager.getCurrentGame().getPlayer(player, false);
        DecimalFormat df = new DecimalFormat("0.00");
        return Items.builder(Heads.getSkullOf(player, player))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§8Dans le jeu : §6" + (gPlayer == null ? "§cAucun" : gPlayer.getManager().getId()),
                        " ",
                        "  §aNom : §f" + (gPlayer == null ? player : gPlayer.getName()),
                        "  §aUUID : §7" + (op.isOnline() ? op.getUniqueId() : gPlayer == null ? "null" : gPlayer.getLastUuid()),
                        "  §aTeam : §f" + (gPlayer == null ? "§4§lHors Jeu" : gPlayer.getTeam() == null ? "§cAucune" : gPlayer.getTeam().getName()),
                        "  §aNom d'Affichage : §f" + (gPlayer == null ? op.isOnline() ? op.getPlayer().getDisplayName() : "§7null" : gPlayer.getDisplayName()),
                        "    ---",
                        "  §aVie : §c" + (op.isOnline() ? df.format(op.getPlayer().getFoodLevel()) + "§7 /20.0" : "§cHors Ligne"),
                        "  §aNourriture : §a" + (op.isOnline() ? df.format(op.getPlayer().getFoodLevel()) + "§7 /20.0" : "§cHors Ligne"),
                        "  §aSaturation : §e" + (op.isOnline() ? df.format(op.getPlayer().getSaturation()) + "§7 /20.0" : "§cHors Ligne"),
                        "    ---",
                        "  §aLocalisation : §f" + (op.isOnline() ? Utils.locToString(op.getPlayer().getLocation(), true, true, false) : "§cHors Ligne"),
                        "  §aMonde : §f" + ((op.isOnline() ?
                                op.getPlayer().getWorld().getName().equalsIgnoreCase("world") ? "§2OverWorld"
                                        : op.getPlayer().getWorld().getName().equalsIgnoreCase("world_nether") ? "§dNether"
                                        : op.getPlayer().getWorld().getName().equalsIgnoreCase("world_the_end") ? "§5End"
                                        : op.getPlayer().getWorld().getName()
                                : "§cHors Ligne")),
                        "  §aZone : §f" + (gPlayer == null ? "§cHors Jeu" : gPlayer.getZone() == null ? "§cAucune" : gPlayer.getZone().getId() + "§7 (" + gPlayer.getZone().getType() + ")"),
                        " ",
                        "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getStats(GPlayer gPlayer, String lastLoreLine, String command) {
        if (gPlayer == null)
            return Items.builder(Material.PAPER).setName("§fStatistiques")
                    .setLore("§8" + Guis.loreSeparator, " ", "§cNe fait partie d'aucune partie")
                    .setCantClickOn(true).build();
        DecimalFormat df = new DecimalFormat("0.00");
        Items.Builder b = Items.builder(Material.PAPER)
                .setName("§fStatistiques")
                .setLore("§8" + Guis.loreSeparator, " ");

        for (Field f : gPlayer.getStats().getClass().getDeclaredFields()) {
            Object o = gPlayer.getStats().get(f.getName());
            if (o != null)
                b.addLore("  §f" + f.getName() + " : §6" +
                        (o.getClass().equals(Double.class) || o.getClass().equals(Float.class) ? df.format(o) : o));
        }

        return b.addLore(" ", "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getPlayerChangeTeamInventory(String player, String back, String navigationBaseCommand, int page) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("§cAucune partie en cours", back);
        GPlayer gPlayer = GManager.getCurrentGame().getPlayer(player, false);
        return Guis.getPagedInventory("§aJoueurs §f-§e " + player + "§f » §6Équipes", 54, back,
                getHead(player, "Clic pour rafraîchir", navigationBaseCommand + " " + page),
                GuiTeams.getTeamItem(gPlayer == null ? null : gPlayer.getTeam(), gPlayer == null ? null
                        : "Clic pour voir plus", gPlayer == null ? "null" : Main.CMD + " players " + gPlayer.getName() + " team"),
                navigationBaseCommand, page, GManager.getCurrentGame().getTeams().stream().map(t ->
                        Items.builder(GuiTeams.getTeamItem(t, null, "null"))
                                .addLore(
                                        "§7Clic Gauche pour ajouter à l'équipe",
                                        "§7Clic Droit pour supprimer de l'équipe",
                                        "§7Clic Molette pour voir l'équipe",
                                        " ",
                                        "§7Commandes :",
                                        "§7/" + Main.CMD + " teams " + t.getId(),
                                        "§7/" + Main.CMD + " teams " + t.getId() + " §8(§7add §8|§7 remove§8)§7 " + player
                                )
                                .setCantClickOn(true)
                                .setLeftRightCommandOnClick(
                                        (gPlayer == null ? Main.CMD + " players " + player + " init\n"
                                                : gPlayer.getTeam() != null ? Main.CMD + " teams " + gPlayer.getTeam().getId() + " remove " + player + "\n" : "")
                                                + Main.CMD + " teams " + t.getId() + " add " + player + "\n" + navigationBaseCommand + " " + page,
                                        (gPlayer != null && gPlayer.getTeam() != null && gPlayer.getTeam().getId().equals(t.getId()) ?
                                                Main.CMD + " teams " + t.getId() + " remove " + player + "\n" : "") + navigationBaseCommand + " " + page
                                )
                                .setMiddleCommandOnClick(Main.CMD + " teams " + t.getId())
                                .build()).collect(Collectors.toList()));
    }

    public static Inventory getPlayersInventory(String back, String navigationBaseCommand, int page) {
        ArrayList<String> l = new ArrayList<>(new HashSet<String>() {{
            addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
            addAll(GManager.getCurrentGame().getPlayers().stream().map(GPlayer::getName).collect(Collectors.toList()));
        }});
        return Guis.getPagedInventory("§aJoueurs", 54, back,
                GuiPlayers.getMain(null, "Clic pour rafraîchir",
                        navigationBaseCommand + " " + page, l.size(),
                        (int) l.stream().filter(p -> Bukkit.getOfflinePlayer(p).isOnline()).count(),
                        Bukkit.getMaxPlayers()), null, navigationBaseCommand,
                page, l.stream().map(p ->
                        GuiPlayers.getHead(p, "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " players "
                                + p, Main.CMD + " players " + p)).collect(Collectors.toList()));
    }

    public static Inventory getPlayerInventory(String player, @Nonnull Player opener, String back) {
        if (player == null)
            return Guis.getErrorInventory("Joueur Nul.", back);

        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GPlayer gPlayer = GManager.getCurrentGame().getPlayer(player, false);
        Inventory inv = Guis.getBaseInventory("§aJoueurs §f- §e" + player, 54, back,
                getSimplifiedMain(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1), getHead(player,
                "§7Cliquez pour rafraichir", Main.CMD + " players " + player));

        inv.setItem(Utils.posOf(3, 2), GuiPerm.getPermsItem(gPlayer == null ? null : gPlayer.getPersonalPermissions(),
                Material.IRON_SWORD, "§fPermissions Personnelles",
                "§7Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " perm player " + player,
                Main.CMD + " perm player " + player));
        inv.setItem(Utils.posOf(4, 2), Items.builder(GuiTeams.getTeamItem(gPlayer == null ? null
                        : gPlayer.getTeam(), null, "null"))
                .addLore(
                        gPlayer == null || gPlayer.getTeam() == null ?
                                "§7Clic pour changer l'équipe"
                                        + "\n "
                                        + "\n§7Commande :"
                                        + "\n§7/" + Main.CMD + " players " + player + " teams"
                                : "§7Clic Gauche pour voir plus"
                                + "\n§7Clic Droit pour changer l'équipe"
                                + "\n "
                                + "\n§7Commandes :"
                                + "\n§7/" + Main.CMD + " players " + player + " team"
                                + "\n§7/" + Main.CMD + " players " + player + " teams"
                )
                .setLeftRightCommandOnClick(
                        gPlayer == null || gPlayer.getTeam() == null ?
                                Main.CMD + " players " + player + " teams"
                                : Main.CMD + " players " + player + " team",
                        Main.CMD + " players " + player + " teams"
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(5, 2), getStats(gPlayer,
                "§7Cliquez pour rafraichir", Main.CMD + " players " + player));

        inv.setItem(Utils.posOf(6, 3), Guis.tp(false, "tp " + player));
        inv.setItem(Utils.posOf(7, 3), Guis.tp(true, "tp " + player + " " + opener.getName()));

        inv.setItem(Utils.posOf(1, 3), Items.builder(Material.WOOD_PICKAXE)
                .setName("§cKick / Ban / Ban-IP")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic gauche pour Expulser",
                        "§7  (Shift pour préciser la raison)",
                        "§7Clic droit pour Bannir",
                        "§7  (Shift pour préciser la raison)",
                        "§7Clic molette pour Bannir l'IP",
                        " ",
                        "§7Commandes :",
                        "§7/kick " + player + " §8[§f<raison>§8]",
                        "§7/ban " + player + " §8[§f<raison>§8]",
                        "§7/ban-ip " + player + " §8[§f<raison>§8]"
                )
                .setCantClickOn(true)
                .setLeftRightShiftCommandOnClick(
                        "kick " + player, "input kick " + player + " %s%n" + Main.CMD + " players " + player,
                        "ban " + player, "input ban " + player + " %s%n" + Main.CMD + " players " + player)
                .setMiddleCommandOnClick("input ban-ip " + player + " %s%n" + Main.CMD + " players " + player)
                .build());
        inv.setItem(Utils.posOf(2, 3), Items.builder(Material.GHAST_TEAR)
                .setName("§6Warn")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour donner un",
                        " §7avertissement au joueur",
                        " ",
                        "§cNe fonctionne pas encore :p"
                )
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(1, 4), Items.builder(Material.BED)
                .setName("§cSpawn")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §cLocalisation : §f" + (op.getBedSpawnLocation() == null ? "§cAucun" : Utils.locToString(op.getBedSpawnLocation(), true, false, true)),
                        "  §cZone : §f" + (gPlayer == null ? "§4§lHors Jeu" : op.getBedSpawnLocation() == null ? "§cAucune"
                                : gPlayer.getZone(op.getBedSpawnLocation()) == null ? "null" :
                                gPlayer.getZone(op.getBedSpawnLocation()).getId()
                                        + "§7 (" + gPlayer.getZone(op.getBedSpawnLocation()).getType() + ")"),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se téléporter",
                        "§7Clic Droit pour redéfinir ici",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/spawnpoint " + player + " §f<x> <y> <z> §8[§f<world>§8]"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(op.getBedSpawnLocation() == null ? "null" : "tp " + opener.getName()
                                + " " + op.getBedSpawnLocation().getX() + " " + op.getBedSpawnLocation().getY() + " "
                                + op.getBedSpawnLocation().getZ(),
                        "spawnpoint " + player + " " + opener.getLocation().getX() + " "
                                + opener.getLocation().getY() + " " + opener.getLocation().getZ()
                                + " " + opener.getLocation().getWorld().getName() + "\n" + Main.CMD + " players " + player)
                .setMiddleCommandOnClick("input spawnpoint " + player + " %l{x,y,z,w}%n" + Main.CMD + " players " + player)
                .build());
        inv.setItem(Utils.posOf(2, 4), Items.builder(Material.COOKED_BEEF)
                .setName("§2Nourriture")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2Niveau : §a" + (op.isOnline() ? op.getPlayer().getFoodLevel() + "§7 /20.0" : "§cHors Ligne"),
                        "  §2Saturation : §e" + (op.isOnline() ? op.getPlayer().getSaturation() + "§7 /20.0" : "§cHors Ligne"),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour le nourrir",
                        "§7Clic Droit pour l'affamer",
                        " ",
                        "§7Commandes :",
                        "§7/feed " + player,
                        "§7/effect " + player + " hunger 3 255 true"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick("feed " + player, "effect " + player + " hunger 3 255 true")
                .build());
        inv.setItem(Utils.posOf(3, 4), Items.builder(Material.GOLDEN_APPLE)
                .setName("§4Vie")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §4Points : §c" + (op.isOnline() ? op.getPlayer().getHealth() : "§cHors Ligne"),
                        "  §4Maximum : §7" + (op.isOnline() ? op.getPlayer().getMaxHealth() : "§cHors Ligne"),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour le guérir",
                        "  §7(Shift pour gapple)",
                        "§7Clic Droit pour le blesser",
                        "  §7(Shift pour  §4-3.0 hp§7)",
                        "§7Clic Molette pour le tuer",
                        " ",
                        "§7Commandes :",
                        "§7/heal " + player,
                        "§7/damage " + player + " §f<hp>",
                        "§7/kill " + player
                )
                .setCantClickOn(true)
                .setCompleteCommandOnClick("heal " + player, "effect " + player + " absorption 120 1\neffect " + player + " regeneration 5 1",
                        "damage " + player + " 1", "damage " + player + " 3", "kill " + player)
                .build());
        inv.setItem(Utils.posOf(4, 4), Items.builder(Material.CHEST)
                .setName("§eInventaire")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour le voir",
                        "§7  (Shift pour mélanger la hotbar)",
                        "§7Clic Droit pour voir l'EnderChest",
                        "§7  (Shift pour mélanger tout)",
                        "§7Clic Molette pour le supprimer",
                        " ",
                        "§7Commandes :",
                        "§7/invsee " + player,
                        "§7/shuffle hotbar " + player,
                        "§7/ec " + player,
                        "§7/shuffle inv " + player,
                        "§7/clear " + player
                )
                .setCantClickOn(true)
                .setCompleteCommandOnClick("invsee " + player, "shuffle hotbar " + player,
                        "ec " + player, "shuffle inv " + player, "clear " + player)
                .build());
        inv.setItem(Utils.posOf(5, 4), Items.builder(Material.BLAZE_POWDER)
                .setName("§cBrûlure")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour brûler §c10 secondes",
                        "  §7(Shift pour §c30 secondes§7)",
                        "§7Clic Droit pour foudroyer §91 fois",
                        "  §7(Shift pour §910 fois§7 - 10 secondes)",
                        "§7Clic Molette pour §6arrêter§7 le feu",
                        " ",
                        "§7Commandes :",
                        "§7/burn " + player + " §f<seconds>",
                        "§7/lightning " + player + " §f<times>",
                        "§7/burn " + player + " stop"
                )
                .setCantClickOn(true)
                .setCompleteCommandOnClick("burn " + player + " 10", "burn " + player + " 30",
                        "lightning " + player, "lightning " + player + " 10", "burn " + player + " stop")
                .build());
        inv.setItem(Utils.posOf(6, 4), Items.builder(Material.SLIME_BALL)
                .setName("§aBounce")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour faire voler le joueur",
                        "§7  (§7Vélocité de §f0.0 50.0 0.0§7)",
                        "§7Clic Droit pour lui appliquer une vélocité",
                        " ",
                        "§7Commandes :",
                        "§7/bounce " + player,
                        "§7/bounce -v §f<dx> <dy> <dz>§7 " + player
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        "bounce " + player,
                        "input bounce -v %f %f %f " + player + "%n" + Main.CMD + " players " + player
                )
                .build());
        inv.setItem(Utils.posOf(7, 4), Items.builder(Freeze.isFrozen(player) ? Material.PACKED_ICE : Material.ICE)
                .setName("§9Freeze")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §9Status : §c" + (Freeze.isFrozen(player) ? "§1Congelé !" : "§eTempérature ambiante"),
                        "  §9Nombre de refroidis : §f" + Freeze.frozen.size(),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour " + (Freeze.isFrozen(player) ? "§9décongeler" : "§1congeler"),
                        " ",
                        "§7Commande :",
                        "§7/freeze " + player + " §8[(§7on §8|§7 off§8)]"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick("freeze " + player + "\n" + Main.CMD + " players " + player)
                .build());

        inv.setItem(Utils.posOf(7, 1), Items.builder(Heads.GREEN_CHEST.getSkull())
                .setName("§3Inventaires")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour voir les",
                        "§7 inventaires sauvegardés",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " players " + player + " inv"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(Main.CMD + " players " + player + " inv")
                .build());

        return inv;
    }

}
