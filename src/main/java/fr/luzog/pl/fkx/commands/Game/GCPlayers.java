package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.guis.GuiPlayers;
import fr.luzog.pl.fkx.guis.GuiTeams;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GCPlayers {
    public static final String syntaxe = "/" + Main.CMD + " players [help | list | <player> [info | init | team | teams [<page>]] | page <page>]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        if (args.length == 1)
            Bukkit.dispatchCommand(sender, Main.CMD + " players page 0");

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Joueurs G (§f" + (GManager.getCurrentGame() == null ? "null" : GManager.getCurrentGame().getPlayers().size()) + "§r) :");
            if (GManager.getCurrentGame() == null)
                u.err(" - Aucun jeu en cours");
            else
                GManager.getCurrentGame().getPlayers().forEach(gPlayer ->
                        u.succ(" - §6" + gPlayer.getName() + "§r §7" + gPlayer.getLastUuid() + " : §f" + (gPlayer.getPlayer() != null ? "§2" + SpecialChars.STAR_4_FILLED + " here" : "§4" + SpecialChars.STAR_4_EMPTY + " off")));
        } else if (args[1].equalsIgnoreCase("page")) {
            if (args.length == 2)
                Bukkit.dispatchCommand(sender, Main.CMD + " players page 0");
            else
                try {
                    if (sender instanceof Player)
                        u.getPlayer().openInventory(GuiPlayers.getPlayersInventory(Main.CMD,
                                Main.CMD + " players page", Integer.parseInt(args[2])));
                    else
                        u.synt();
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format + " (" + args[2] + ")");
                }
        } else {
            GPlayer gPlayer = GManager.getCurrentGame().getPlayer(args[1], false);
            Player p = gPlayer == null || gPlayer.getPlayer() == null ? Bukkit.getPlayerExact(args[1]) : gPlayer.getPlayer();
            if (args.length >= 3)
                if (args[2].equalsIgnoreCase("info")) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    u.succ("Joueur :");
                    u.succ(" - Nom : §f" + (p == null ? gPlayer == null ? "§cnull" : gPlayer.getName() : p.getName()));
                    u.succ(" - UUID : §7" + (p == null ? gPlayer == null ? "§cnull" : gPlayer.getLastUuid() : p.getUniqueId()));
                    u.succ(" - Team : §f" + (gPlayer == null ? "§cHors Jeu" : gPlayer.getTeam() == null ? "§4§lAucune" : gPlayer.getTeam().getName()));
                    u.succ(" - Nom d'Affichage : §f" + (gPlayer == null ? p == null ? "§cnull" : p.getDisplayName() : gPlayer.getDisplayName()));
                    u.succ(" - Vie : §c" + (p == null ? "0.0§7 /0.0" : df.format(p.getHealth()) + "§7 /" + p.getMaxHealth()));
                    u.succ(" - Nourriture : §a" + (p == null ? "0.0§7 /0.0" : df.format(p.getFoodLevel()) + "§7 /20.0"));
                    u.succ(" - Saturation : §e" + (p == null ? "0.0§7 /0.0" : df.format(p.getSaturation()) + "§7 /20.0"));
                    u.succ(" - Localisation : §f" + (p == null ? "§cAucune" : Utils.locToString(p.getLocation(), true, true, false)));
                    u.succ(" - Monde : §f" + (p == null ? "§cAucun" : (p.getWorld().getName().equalsIgnoreCase("world") ? "§2OverWorld"
                            : p.getWorld().getName().equalsIgnoreCase("world_nether") ? "§dNether"
                            : p.getWorld().getName().equalsIgnoreCase("world_the_end") ? "§5End"
                            : p.getWorld().getName())));
                    u.succ(" - Zone : §f" + (gPlayer == null ? "§cHors Jeu" : gPlayer.getZone() == null ? "§cAucune" : gPlayer.getZone().getId() + "§7 (" + gPlayer.getZone().getType() + ")"));
                } else if (args[2].equalsIgnoreCase("init")) {
                    if (gPlayer == null) {
                        gPlayer = GManager.getCurrentGame().getPlayer(args[1], true);
                        u.succ("Joueur créé : §f" + gPlayer.getDisplayName());
                    } else {
                        u.err("Joueur déjà créé : §f" + gPlayer.getDisplayName());
                    }
                } else if (args[2].equalsIgnoreCase("team")) {
                    if (gPlayer == null || gPlayer.getTeam() == null)
                        u.err("Le joueur n'a pas de team");
                    else if (sender instanceof Player)
                            u.getPlayer().openInventory(GuiTeams.getTeamInventory(u.getPlayer(), gPlayer.getTeam(),
                                    Main.CMD + " players " + u.getPlayer().getName()));
                    else
                        u.err(CmdUtils.err_not_player);
                } else if (args[2].equalsIgnoreCase("teams")) {
                    if (args.length == 3)
                        Bukkit.dispatchCommand(sender, Main.CMD + " players " + args[1] + " teams 0");
                    else if (sender instanceof Player)
                        try {
                            u.getPlayer().openInventory(GuiPlayers.getPlayerChangeTeamInventory(args[1],
                                    Main.CMD + " players " + args[1], Main.CMD + " players " + args[1] + " teams",
                                    Integer.parseInt(args[3])));
                        } catch (NumberFormatException e) {
                            Bukkit.dispatchCommand(sender, Main.CMD + " players " + args[1] + " teams 0");
                        }
                    else
                        u.err(CmdUtils.err_not_player);
                } else
                    u.synt();
            else if (sender instanceof Player)
                u.getPlayer().openInventory(GuiPlayers.getPlayerInventory(p == null ? Bukkit.getOfflinePlayer(args[1]).getName() : p.getName(), u.getPlayer(), Main.CMD + " players"));
            else
                u.succ(CmdUtils.err_not_player);

        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return args.length == 2 ? new ArrayList<String>() {{
            add("help");
            add("list");
            addAll(Utils.getAllPlayers());
            add("page");
        }} : args.length == 3 ? new ArrayList<String>() {{
            if (Bukkit.getOfflinePlayer(args[1]).isOnline()) {
                add("info");
                add("init");
                add("team");
            }
        }} : new ArrayList<>();
    }
}
