package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKTeam;
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
    public static final String syntaxe = "/fk [(? || help) | activations | banner | (bc || broadcast) | compass | date "
            + "| locks | game | (perm || permissions) | players | portal | stats | teams | title | warp | zone] [<args...>]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0) {
            if (hasPerm(sender, false, true) && isNotNull(sender, true))
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiFK.getInv(u.getPlayer(), null));
                else
                    u.synt();

        } else {
            switch (args[0].toLowerCase()) {
                case "help":
                case "?":
                    FKCHelp.onCommand(sender, command, msg, args);
                    break;
                case "activations":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCActivations.onCommand(sender, command, msg, args);
                    break;
                case "banner":
                    if (hasPerm(sender, false, true))
                        FKCBanner.onCommand(sender, command, msg, args);
                    break;
                case "bc":
                case "broadcast":
                    if (hasPerm(sender, true, true))
                        FKCBroadcast.onCommand(sender, command, msg, args);
                    break;
                case "compass":
                    if (isNotNull(sender, true))
                        FKCCompass.onCommand(sender, command, msg, args);
                    break;
                case "date":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCDate.onCommand(sender, command, msg, args);
                    break;
                case "game":
                    if (sender.isOp())
                        FKCGame.onCommand(sender, command, msg, args);
                    else
                        u.err("Vous n'avez pas la permission d'utiliser cette commande. (Vous n'êtes pas op)");
                    break;
                case "locks":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCLocks.onCommand(sender, command, msg, args);
                    break;
                case "perm":
                case "permissions":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCPermissions.onCommand(sender, command, msg, args);
                    break;
                case "players":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCPlayers.onCommand(sender, command, msg, args);
                    break;
                case "portal":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCPortal.onCommand(sender, command, msg, args);
                    break;
                case "stats":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCStats.onCommand(sender, command, msg, args);
                    break;
                case "teams":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCTeams.onCommand(sender, command, msg, args);
                    break;
                case "title":
                    if (hasPerm(sender, false, true))
                        FKCTitle.onCommand(sender, command, msg, args);
                    break;
                case "warp":
                    if (hasPerm(sender, false, true) && isNotNull(sender, true))
                        FKCWarp.onCommand(sender, command, msg, args);
                    break;
                case "zone":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        FKCZone.onCommand(sender, command, msg, args);
                    break;
                default:
                    u.err("Unknown command");
                    u.synt();
                    break;
            }
        }

        return true;
    }

    public static boolean isNotNull(CommandSender sender, boolean out) {
        boolean r = FKManager.getCurrentGame() != null;
        if (!r && out)
            sender.sendMessage("Aucune partie n'est en cours."
                    + (sender.isOp() ? "\nUtilisez /fk game new <id> pour créer une partie."
                    : "Patientez un peu, le temps que les admins créent une partie."));
        return r;
    }

    public static boolean hasPerm(CommandSender sender, boolean god, boolean out) {
        boolean r = !(sender instanceof Player) || sender.isOp()
                || (FKManager.getCurrentGame() != null && FKManager.getCurrentGame()
                .getPlayer(sender.getName(), false) != null && FKManager.getCurrentGame()
                .getPlayer(sender.getName(), false).getTeam() != null && (FKManager.getCurrentGame()
                .getPlayer(sender.getName(), false).getTeam().getId().equals(FKTeam.GODS_ID)
                || (!god && FKManager.getCurrentGame().getPlayer(sender.getName(), false)
                .getTeam().getId().equals(FKTeam.SPECS_ID))));
        if (!r && out)
            sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
        return r;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        ArrayList<String> temp = new ArrayList<>();

        if (args.length == 1) {
            temp.add("?");
            temp.add("help");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("activations");
            if (hasPerm(sender, false, false))
                temp.add("banner");
            if (hasPerm(sender, true, false))
                temp.add("bc");
            if (hasPerm(sender, true, false))
                temp.add("broadcast");
            if (isNotNull(sender, false))
                temp.add("compass");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("date");
            if (sender.isOp())
                temp.add("game");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("locks");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("perm");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("permissions");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("players");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("portal");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("stats");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("teams");
            if (hasPerm(sender, false, false))
                temp.add("title");
            if (hasPerm(sender, false, false) && isNotNull(sender, false))
                temp.add("warp");
            if (hasPerm(sender, true, false) && isNotNull(sender, false))
                temp.add("zone");
        } else if (args.length > 1)
            switch (args[0].toLowerCase()) {
                case "?":
                case "help":
                    temp.addAll(FKCHelp.onTabComplete(sender, command, msg, args));
                    break;
                case "activations":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(FKCActivations.onTabComplete(sender, command, msg, args));
                    break;
                case "banner":
                    if (hasPerm(sender, false, false))
                        temp.addAll(FKCBanner.onTabComplete(sender, command, msg, args));
                    break;
                case "bc":
                case "broadcast":
                    if (hasPerm(sender, true, false))
                        temp.addAll(FKCBroadcast.onTabComplete(sender, command, msg, args));
                    break;
                case "compass":
                    if (isNotNull(sender, false))
                        temp.addAll(FKCCompass.onTabComplete(sender, command, msg, args));
                    break;
                case "date":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(FKCDate.onTabComplete(sender, command, msg, args));
                    break;
                case "game":
                    if (sender.isOp())
                        temp.addAll(FKCGame.onTabComplete(sender, command, msg, args));
                    break;
                case "locks":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(FKCLocks.onTabComplete(sender, command, msg, args));
                    break;
                case "perm":
                case "permissions":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(FKCPermissions.onTabComplete(sender, command, msg, args));
                    break;
                case "players":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(FKCPlayers.onTabComplete(sender, command, msg, args));
                    break;
                case "portal":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(FKCPortal.onTabComplete(sender, command, msg, args));
                    break;
                case "stats":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(FKCStats.onTabComplete(sender, command, msg, args));
                    break;
                case "teams":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(FKCTeams.onTabComplete(sender, command, msg, args));
                    break;
                case "title":
                    if (hasPerm(sender, false, false))
                        temp.addAll(FKCTitle.onTabComplete(sender, command, msg, args));
                    break;
                case "warp":
                    if (hasPerm(sender, false, false) && isNotNull(sender, false))
                        temp.addAll(FKCWarp.onTabComplete(sender, command, msg, args));
                    break;
                case "zone":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
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
