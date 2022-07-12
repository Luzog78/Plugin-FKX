package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.commands.Cheat.Freeze;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class GuiPlayers {

    public static ItemStack getSimplifiedMain(String lastLoreLine, String command) {
        return getMain(null, lastLoreLine, command, null, null, null);
    }

    public static ItemStack getMain(@Nullable String from, String lastLoreLine, String command,
                                    Integer size, Integer online, Integer max) {
        return Items.builder(Heads.MISC_EARTH1.getSkull())
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
        FKPlayer fkp = FKManager.getCurrentGame().getPlayer(player, false);
        DecimalFormat df = new DecimalFormat("0.00");
        return Items.builder(Heads.getSkullOf(player, player))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§8Dans le jeu : §6" + (fkp == null ? "§cAucun" : fkp.getManager().getId()),
                        " ",
                        "  §aNom : §f" + (fkp == null ? player : fkp.getName()),
                        "  §aUUID : §7" + (op.isOnline() ? op.getUniqueId() : fkp == null ? "null" : fkp.getLastUuid()),
                        "  §aTeam : §f" + (fkp == null ? "§4§lHors Jeu" : fkp.getTeam() == null ? "§cAucune" : fkp.getTeam().getName()),
                        "  §aNom d'Affichage : §f" + (fkp == null ? op.isOnline() ? op.getPlayer().getDisplayName() : "§7null" : fkp.getDisplayName()),
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
                        "  §aZone : §f" + (fkp == null ? "§cHors Jeu" : fkp.getZone() == null ? "§cAucune" : fkp.getZone().getId() + "§7 (" + fkp.getZone().getType() + ")"),
                        " ",
                        "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getStats(FKPlayer fkp, String lastLoreLine, String command) {
        if (fkp == null)
            return Items.builder(Material.PAPER).setName("§fStatistiques")
                    .setLore("§8" + Guis.loreSeparator, " ", "§cNe fait partie d'aucune partie")
                    .setCantClickOn(true).build();
        DecimalFormat df = new DecimalFormat("0.00");
        Items.Builder b = Items.builder(Material.PAPER)
                .setName("§fStatistiques")
                .setLore("§8" + Guis.loreSeparator, " ");

        for (Field f : fkp.getStats().getClass().getDeclaredFields()) {
            Object o = fkp.getStats().get(f.getName());
            if (o != null)
                b.addLore("  §f" + f.getName() + " : §6" +
                        (o.getClass().equals(Double.class) || o.getClass().equals(Float.class) ? df.format(o) : o));
        }

        return b.addLore(" ", "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getPlayer(String player, @Nonnull Player opener, String back) {
        if (player == null)
            return Guis.getErrorInventory("Joueur Nul.", back);
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        FKPlayer fkp = FKManager.getCurrentGame().getPlayer(player, false);
        Inventory inv = Guis.getBaseInventory("", 54, back, getSimplifiedMain("Clic pour rafraichir", "fk players " + player), null);
        inv.setItem(Utils.posOf(4, 1), getHead(player, "§7Cliquez pour rafraichir", "fk players " + player));

        inv.setItem(Utils.posOf(3, 2), GuiPerm.getPermsItem(fkp == null ? null : fkp.getPersonalPermissions(),
                Material.IRON_SWORD, "§fPermissions Personnelles", "§7Clic pour voir plus", "fk perm player " + player));
        inv.setItem(Utils.posOf(5, 2), getStats(fkp, "§7Cliquez pour rafraichir", "fk players " + player));

        inv.setItem(Utils.posOf(6, 3), Guis.tp(false, "tp " + player));
        inv.setItem(Utils.posOf(7, 3), Guis.tp(true, "tp " + player + " " + opener.getName()));

        inv.setItem(Utils.posOf(1, 3), Items.builder(Material.WOOD_PICKAXE)
                .setName("§cKick / Ban / Ban-IP")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic gauche pour Expulser",
                        "§7Clic droit pour Bannir",
                        "§7Clic molette pour Bannir l'IP"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick("kick " + player, "ban " + player)
                .setMiddleCommandOnClick("ban-ip " + player)
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
                        "  §cZone : §f" + (fkp == null ? "§4§lHors Jeu" : op.getBedSpawnLocation() == null ? "§cAucune"
                                : fkp.getZone(op.getBedSpawnLocation()) == null ? "null" :
                                fkp.getZone(op.getBedSpawnLocation()).getId()
                                        + "§7 (" + fkp.getZone(op.getBedSpawnLocation()).getType() + ")"),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se téléporter",
                        "§7Clic Droit pour redéfinir"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(op.getBedSpawnLocation() == null ? "null" : "tp " + opener.getName()
                                + " " + op.getBedSpawnLocation().getX() + " " + op.getBedSpawnLocation().getY() + " "
                                + op.getBedSpawnLocation().getZ(),
                        "spawnpoint " + player + " " + opener.getLocation().getX() + " "
                                + opener.getLocation().getY() + " " + opener.getLocation().getZ()
                                + " " + opener.getLocation().getWorld().getName() + "\nfk players " + player)
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
                        "§7Clic Droit pour l'affamer"
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
                        "  §7(Shift pour -3.0 hp)",
                        "§7Clic Molette pour le tuer"
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
                        "§7Clic Droit pour voir l'armure",
                        "§7Shift Clic Gauche pour mélanger la hotbar",
                        "§7Shift Clic Droit pour mélanger tout",
                        "§7Clic Molette pour le supprimer"
                )
                .setCantClickOn(true)
                .setCompleteCommandOnClick("invsee " + player, "shuffle hotbar " + player,
                        "invsee armor " + player, "shuffle inv " + player, "clear " + player)
                .build());
        inv.setItem(Utils.posOf(5, 4), Items.builder(Material.BLAZE_POWDER)
                .setName("§cBrûlure")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour brûler 10 secondes",
                        "  §7(Shift pour 30 secondes)",
                        "§7Clic Droit pour foudroyer 1 fois",
                        "  §7(Shift pour 10 fois - 10 secondes)",
                        "§7Clic Molette pour arrêter le feu"
                )
                .setCantClickOn(true)
                .setCompleteCommandOnClick("burn " + player + " 10", "burn " + player + " 30",
                        "lightning " + player, "lightning " + player + " 10", "burn " + player + " stop")
                .build());
        inv.setItem(Utils.posOf(6, 4), Items.builder(Material.SLIME_BALL)
                .setName("§aBounce")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour faire voler le joueur"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick("bounce " + player)
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
                        "§7Clic pour " + (Freeze.isFrozen(player) ? "§9décongeler" : "§1congeler")
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick("freeze " + player + "\nfk players " + player)
                .build());

        return inv;
    }

}
