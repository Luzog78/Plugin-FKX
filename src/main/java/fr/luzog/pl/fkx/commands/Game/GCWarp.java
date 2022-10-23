package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GTeam;
import fr.luzog.pl.fkx.game.GZone;
import fr.luzog.pl.fkx.guis.GuiGlobal;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GCWarp {
    public static final String syntaxe = "/" + Main.CMD + " warp [? || help | list | page <page> | lobby | spawn | nether | end | team <id> | zone <id>] [[!]<players...>]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0) {
            u.synt();
        } else if (args.length == 1) {
            Bukkit.dispatchCommand(sender, Main.CMD + " warp page 0");
        } else if (args[1].equals("?") || args[1].equalsIgnoreCase("help")) {
            u.synt();
        } else if (args[1].equalsIgnoreCase("list")) {
            GManager game = u.getPlayer() == null || GManager.getGlobalPlayer(u.getPlayer().getName()).isEmpty() ?
                    GManager.getCurrentGame() : GManager.getGlobalPlayer(u.getPlayer().getName()).get(0).getManager();
            if (game == null) {
                u.err("Aucune partie en cours.");
                return true;
            }
            u.succ("Liste des warps :");
            u.succ(" - §6lobby");
            u.succ(" - §6spawn");
            u.succ(" - §6nether");
            u.succ(" - §6end");
            game.getTeams().forEach(t -> u.succ(" - §6team §f" + t.getId()));
            game.getZones().forEach(z -> u.succ(" - §6zone §f" + z.getId()));
        } else if (args[1].equalsIgnoreCase("page")) {
            if (args.length == 2)
                Bukkit.dispatchCommand(sender, Main.CMD + " warp page 0");
            else
                try {
                    if (sender instanceof Player)
                        u.getPlayer().openInventory(GuiGlobal.getWarpsInventory(GManager.getCurrentGame(),
                                u.getPlayer().getLocation(), Main.CMD, Main.CMD + " warp page",
                                Integer.parseInt(args[2])));
                    else
                        u.synt();
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format + " (" + args[2] + ")");
                }
        } else {
            GManager game = u.getPlayer() == null || GManager.getGlobalPlayer(u.getPlayer().getName()).isEmpty() ?
                    GManager.getCurrentGame() : GManager.getGlobalPlayer(u.getPlayer().getName()).get(0).getManager();
            if (game == null) {
                u.err("Aucune partie en cours.");
                return true;
            }
            Location loc = null;
            int substring = 2;
            switch (args[1].toLowerCase()) {
                case "lobby":
                    loc = game.getLobby().getSpawn();
                    break;
                case "spawn":
                    loc = game.getSpawn().getSpawn();
                    break;
                case "nether":
                    loc = game.getNether().getDimSpawn();
                    break;
                case "end":
                    loc = game.getEnd().getDimSpawn();
                    break;
                case "team":
                    substring++;
                    if (args.length > 2)
                        if (game.getTeam(args[2]) != null)
                            loc = game.getTeam(args[2]).getSpawn();
                        else
                            u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
                    break;
                case "zone":
                    substring++;
                    if (args.length > 2)
                        if (game.getZone(args[2]) != null)
                            loc = game.getZone(args[2]).getSpawn();
                        else
                            u.err("Zone introuvable (" + args[2] + ")");
                    break;
            }
            if (loc == null) {
                u.err("Warp inconnu.");
                return false;
            }
            if (args.length == substring)
                if (u.getPlayer() != null)
                    u.getPlayer().teleport(loc);
                else
                    u.err(CmdUtils.err_not_player);
            else if (args[substring].equals("info"))
                u.succ("Warp §6" + args[substring - 1] + "§r : §f" + Utils.locToString(loc, true, true, true));
            else
                for (Player player : CmdUtils.getPlayersFromArray(args, substring)) {
                    player.teleport(loc);
                    u.succ(String.format("Joueur §6%s§r téléporté.",
                            game.getPlayer(player.getName(), false) != null ?
                                    game.getPlayer(player.getName(), false).getDisplayName() : player.getName()));
                }
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);
            GManager game = u.getPlayer() == null || GManager.getGlobalPlayer(u.getPlayer().getName()).isEmpty() ?
                    GManager.getCurrentGame() : GManager.getGlobalPlayer(u.getPlayer().getName()).get(0).getManager();
            if (game != null)
                if (args.length == 2) {
                    add("help");
                    add("list");
                    add("lobby");
                    add("spawn");
                    add("nether");
                    add("end");
                    add("team");
                    add("zone");
                } else if (args[1].equalsIgnoreCase("lobby") || args[1].equalsIgnoreCase("spawn")
                        || args[1].equalsIgnoreCase("nether") || args[1].equalsIgnoreCase("end")) {
                    if (args.length == 3) {
                        add("gui");
                        add("info");
                    }
                    addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("team")) {
                    if (args.length == 3)
                        addAll(game.getTeams().stream().map(GTeam::getId).collect(Collectors.toList()));
                    else {
                        if (args.length == 4) {
                            add("gui");
                            add("info");
                        }
                        addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                    }
                } else if (args[1].equalsIgnoreCase("zone")) {
                    if (args.length == 3)
                        addAll(game.getZones().stream().map(GZone::getId).collect(Collectors.toList()));
                    else {
                        if (args.length == 4) {
                            add("gui");
                            add("info");
                        }
                        addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                    }
                }
        }};
    }
}
