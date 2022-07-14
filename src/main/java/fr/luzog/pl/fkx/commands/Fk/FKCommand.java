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
                        + (sender.isOp() ? "\nUtilisez /fk game new <id> pour créer une partie."
                        : "Patientez un peu, le temps que les admins créent une partie."));

        else {
            switch (args[0].toLowerCase()) {
                case "help":
                case "?":
                    FKCHelp.onCommand(sender, command, msg, args);
                    return false;
                case "activations":
                    if (isNull())
                        break;
                    FKCActivations.onCommand(sender, command, msg, args);
                    return false;
                case "bc":
                case "broadcast":
                    FKCBroadcast.onCommand(sender, command, msg, args);
                    return false;
                case "date":
                    if (isNull())
                        break;
                    FKCDate.onCommand(sender, command, msg, args);
                    return false;
                case "game":
                    FKCGame.onCommand(sender, command, msg, args);
                    return false;
                case "locks":
                    if (isNull())
                        break;
                    FKCLocks.onCommand(sender, command, msg, args);
                    return false;
                case "perm":
                case "permissions":
                    if (isNull())
                        break;
                    FKCPermissions.onCommand(sender, command, msg, args);
                    return false;
                case "players":
                    if (isNull())
                        break;
                    FKCPlayers.onCommand(sender, command, msg, args);
                    return false;
                case "portal":
                    if (isNull())
                        break;
                    FKCPortal.onCommand(sender, command, msg, args);
                    return false;
                case "stats":
                    if (isNull())
                        break;
                    FKCStats.onCommand(sender, command, msg, args);
                    return false;
                case "teams":
                    if (isNull())
                        break;
                    FKCTeams.onCommand(sender, command, msg, args);
                    return false;
                case "title":
                    FKCTitle.onCommand(sender, command, msg, args);
                    return false;
                case "warp":
                    if (isNull())
                        break;
                    FKCWarp.onCommand(sender, command, msg, args);
                    return false;
                case "zone":
                    if (isNull())
                        break;
                    FKCZone.onCommand(sender, command, msg, args);
                    return false;
                default:
                    u.err("Unknown command");
                    u.synt();
                    break;
            }

            u.err("Aucune partie n'est en cours."
                    + (sender.isOp() ? "\nUtilisez /fk game new <id> pour créer une partie."
                    : "Patientez un peu, le temps que les admins créent une partie."));
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        ArrayList<String> temp = new ArrayList<>();

        if (args.length == 1) {
            temp.add("?");
            temp.add("help");
            if (!isNull())
                temp.add("activations");
            temp.add("bc");
            temp.add("broadcast");
            if (!isNull())
                temp.add("date");
            temp.add("game");
            if (!isNull())
                temp.add("locks");
            if (!isNull())
                temp.add("perm");
            if (!isNull())
                temp.add("permissions");
            if (!isNull())
                temp.add("players");
            if (!isNull())
                temp.add("portal");
            if (!isNull())
                temp.add("stats");
            if (!isNull())
                temp.add("teams");
            temp.add("title");
            if (!isNull())
                temp.add("warp");
            if (!isNull())
                temp.add("zone");
        } else if (args.length > 1)
            switch (args[0].toLowerCase()) {
                case "help":
                case "?":
                    temp.addAll(FKCHelp.onTabComplete(sender, command, msg, args));
                    break;
                case "activations":
                    if (!isNull())
                        temp.addAll(FKCActivations.onTabComplete(sender, command, msg, args));
                    break;
                case "bc":
                case "broadcast":
                    temp.addAll(FKCBroadcast.onTabComplete(sender, command, msg, args));
                    break;
                case "date":
                    if (!isNull())
                        temp.addAll(FKCDate.onTabComplete(sender, command, msg, args));
                    break;
                case "game":
                    temp.addAll(FKCGame.onTabComplete(sender, command, msg, args));
                    break;
                case "locks":
                    if (!isNull())
                        temp.addAll(FKCLocks.onTabComplete(sender, command, msg, args));
                    break;
                case "perm":
                case "permissions":
                    if (!isNull())
                        temp.addAll(FKCPermissions.onTabComplete(sender, command, msg, args));
                    break;
                case "players":
                    if (!isNull())
                        temp.addAll(FKCPlayers.onTabComplete(sender, command, msg, args));
                    break;
                case "portal":
                    if (!isNull())
                        temp.addAll(FKCPortal.onTabComplete(sender, command, msg, args));
                    break;
                case "stats":
                    if (!isNull())
                        temp.addAll(FKCStats.onTabComplete(sender, command, msg, args));
                    break;
                case "teams":
                    if (!isNull())
                        temp.addAll(FKCTeams.onTabComplete(sender, command, msg, args));
                    break;
                case "title":
                    temp.addAll(FKCTitle.onTabComplete(sender, command, msg, args));
                    break;
                case "warp":
                    if (!isNull())
                        temp.addAll(FKCWarp.onTabComplete(sender, command, msg, args));
                    break;
                case "zone":
                    if (!isNull())
                        temp.addAll(FKCZone.onTabComplete(sender, command, msg, args));
                    break;
            }

        return new ArrayList<String>() {{
            for (String arg : temp)
                if (arg.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    add(arg);
        }};
    }

    public static boolean isNull() {
        return FKManager.getCurrentGame() == null;
    }
}
