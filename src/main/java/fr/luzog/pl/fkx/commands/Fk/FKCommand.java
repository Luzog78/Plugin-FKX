package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FKCommand implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/fk [(help || ?) | teams | players | game | (bc || broadcast) | title | warp | authorizations | activations | date | event] [<args...>]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            if (sender instanceof Player)
                u.succ("TODO -> Open GUI");
            else
                u.synt();

        else
            switch (args[0].toLowerCase()) {
                case "help":
                case "?":
                    FKCHelp.onCommand(sender, command, msg, args);
                    break;
                case "teams":
                    FKCTeams.onCommand(sender, command, msg, args);
                    break;
                case "players":
                    FKCPlayers.onCommand(sender, command, msg, args);
                    break;
                case "game":
                    FKCGame.onCommand(sender, command, msg, args);
                    break;
                case "bc":
                case "broadcast":
                    FKCBroadcast.onCommand(sender, command, msg, args);
                    break;
                case "title":
                    FKCTitle.onCommand(sender, command, msg, args);
                    break;
                case "warp":
                    FKCWarp.onCommand(sender, command, msg, args);
                    break;
                case "authorizations":
                    FKCAuthorizations.onCommand(sender, command, msg, args);
                    break;
                case "activations":
                    FKCActivations.onCommand(sender, command, msg, args);
                    break;
                case "date":
                    FKCDate.onCommand(sender, command, msg, args);
                    break;
                case "event":
                    FKCEvent.onCommand(sender, command, msg, args);
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
            temp.add("teams");
            temp.add("players");
            temp.add("game");
            temp.add("bc");
            temp.add("broadcast");
            temp.add("title");
            temp.add("warp");
            temp.add("authorizations");
            temp.add("activations");
            temp.add("date");
            temp.add("event");
        } else if (args.length > 1)
            switch (args[0].toLowerCase()) {
                case "help":
                case "?":
                    temp.addAll(FKCHelp.onTabComplete(sender, command, msg, args));
                    break;
                case "teams":
                    temp.addAll(FKCTeams.onTabComplete(sender, command, msg, args));
                    break;
                case "players":
                    temp.addAll(FKCPlayers.onTabComplete(sender, command, msg, args));
                    break;
                case "game":
                    temp.addAll(FKCGame.onTabComplete(sender, command, msg, args));
                    break;
                case "bc":
                case "broadcast":
                    temp.addAll(FKCBroadcast.onTabComplete(sender, command, msg, args));
                    break;
                case "title":
                    temp.addAll(FKCTitle.onTabComplete(sender, command, msg, args));
                    break;
                case "warp":
                    temp.addAll(FKCWarp.onTabComplete(sender, command, msg, args));
                    break;
                case "authorizations":
                    temp.addAll(FKCAuthorizations.onTabComplete(sender, command, msg, args));
                    break;
                case "activations":
                    temp.addAll(FKCActivations.onTabComplete(sender, command, msg, args));
                    break;
                case "date":
                    temp.addAll(FKCDate.onTabComplete(sender, command, msg, args));
                    break;
                case "event":
                    temp.addAll(FKCEvent.onTabComplete(sender, command, msg, args));
                    break;
            }

        return new ArrayList<String>() {{
            for (String arg : temp)
                if (arg.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    add(arg);
        }};
    }
}
