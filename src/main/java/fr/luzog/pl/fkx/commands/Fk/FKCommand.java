package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.GUIs.GuiFK;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FKCommand implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/fk [(? || help) | activations | (bc || broadcast) | date | locks | game | (perm || permissions) | players | portal | stats | teams | title | warp | zone] [<args...>]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            if (FKManager.getCurrentGame() != null)
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiFK.getInv(u.getPlayer(), null));
                else
                    u.synt();
            else
                u.err("Aucune partie n'est en cours."
                        + (sender.isOp() ? "Utilisez /fk game new <id> pour créer une partie."
                        : "Patientez un peu, le temps que les admins créent une partie."));

        else
            switch (args[0].toLowerCase()) {
                case "help":
                case "?":
                    FKCHelp.onCommand(sender, command, msg, args);
                    break;
                case "activations":
                    FKCActivations.onCommand(sender, command, msg, args);
                    break;
                case "bc":
                case "broadcast":
                    FKCBroadcast.onCommand(sender, command, msg, args);
                    break;
                case "date":
                    FKCDate.onCommand(sender, command, msg, args);
                    break;
                case "game":
                    FKCGame.onCommand(sender, command, msg, args);
                    break;
                case "locks":
                    FKCLocks.onCommand(sender, command, msg, args);
                    break;
                case "perm":
                case "permissions":
                    FKCPermissions.onCommand(sender, command, msg, args);
                    break;
                case "players":
                    FKCPlayers.onCommand(sender, command, msg, args);
                    break;
                case "portal":
                    FKCPortal.onCommand(sender, command, msg, args);
                    break;
                case "stats":
                    FKCStats.onCommand(sender, command, msg, args);
                    break;
                case "teams":
                    FKCTeams.onCommand(sender, command, msg, args);
                    break;
                case "title":
                    FKCTitle.onCommand(sender, command, msg, args);
                    break;
                case "warp":
                    FKCWarp.onCommand(sender, command, msg, args);
                    break;
                case "zone":
                    FKCZone.onCommand(sender, command, msg, args);
                    break;
                default:
                    u.err("Unknown command");
                    u.synt();
                    break;
            }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        ArrayList<String> temp = new ArrayList<>();

        if (args.length == 1) {
            temp.add("?");
            temp.add("help");
            temp.add("activations");
            temp.add("bc");
            temp.add("broadcast");
            temp.add("date");
            temp.add("game");
            temp.add("locks");
            temp.add("perm");
            temp.add("permissions");
            temp.add("players");
            temp.add("portal");
            temp.add("stats");
            temp.add("teams");
            temp.add("title");
            temp.add("warp");
            temp.add("zone");
        } else if (args.length > 1)
            switch (args[0].toLowerCase()) {
                case "help":
                case "?":
                    temp.addAll(FKCHelp.onTabComplete(sender, command, msg, args));
                    break;
                case "activations":
                    temp.addAll(FKCActivations.onTabComplete(sender, command, msg, args));
                    break;
                case "bc":
                case "broadcast":
                    temp.addAll(FKCBroadcast.onTabComplete(sender, command, msg, args));
                    break;
                case "date":
                    temp.addAll(FKCDate.onTabComplete(sender, command, msg, args));
                    break;
                case "game":
                    temp.addAll(FKCGame.onTabComplete(sender, command, msg, args));
                    break;
                case "locks":
                    temp.addAll(FKCLocks.onTabComplete(sender, command, msg, args));
                    break;
                case "perm":
                case "permissions":
                    temp.addAll(FKCPermissions.onTabComplete(sender, command, msg, args));
                    break;
                case "players":
                    temp.addAll(FKCPlayers.onTabComplete(sender, command, msg, args));
                    break;
                case "portal":
                    temp.addAll(FKCPortal.onTabComplete(sender, command, msg, args));
                    break;
                case "stats":
                    temp.addAll(FKCStats.onTabComplete(sender, command, msg, args));
                    break;
                case "teams":
                    temp.addAll(FKCTeams.onTabComplete(sender, command, msg, args));
                    break;
                case "title":
                    temp.addAll(FKCTitle.onTabComplete(sender, command, msg, args));
                    break;
                case "warp":
                    temp.addAll(FKCWarp.onTabComplete(sender, command, msg, args));
                    break;
                case "zone":
                    temp.addAll(FKCZone.onTabComplete(sender, command, msg, args));
                    break;
            }

        return new ArrayList<String>() {{
            for (String arg : temp)
                if (arg.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    add(arg);
        }};
    }
}
