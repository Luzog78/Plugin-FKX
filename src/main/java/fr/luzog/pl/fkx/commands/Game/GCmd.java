package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GTeam;
import fr.luzog.pl.fkx.guis.GuiGlobal;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GCmd implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/" + Main.CMD + " [(? || help) | activations | banner | (bc || broadcast) | compass | date "
            + "| locks | game | (perm || permissions) | players | portal | stats | teams | title | warp | zone] [<args...>]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0) {
            if (hasPerm(sender, false, true) && isNotNull(sender, true))
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiGlobal.getInv(u.getPlayer(), null));
                else
                    u.synt();

        } else {
            switch (args[0].toLowerCase()) {
                case "help":
                case "?":
                    GCHelp.onCommand(sender, command, msg, args);
                    break;
                case "activations":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCActivations.onCommand(sender, command, msg, args);
                    break;
                case "banner":
                    if (hasPerm(sender, false, true))
                        GCBanner.onCommand(sender, command, msg, args);
                    break;
                case "bc":
                case "broadcast":
                    if (hasPerm(sender, true, true))
                        GCBroadcast.onCommand(sender, command, msg, args);
                    break;
                case "compass":
                    if (isNotNull(sender, true))
                        GCCompass.onCommand(sender, command, msg, args);
                    break;
                case "date":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCDate.onCommand(sender, command, msg, args);
                    break;
                case "game":
                    if (sender.isOp())
                        GCGame.onCommand(sender, command, msg, args);
                    else
                        u.err("Vous n'avez pas la permission d'utiliser cette commande. (Vous n'êtes pas op)");
                    break;
                case "locks":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCLocks.onCommand(sender, command, msg, args);
                    break;
                case "perm":
                case "permissions":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCPermissions.onCommand(sender, command, msg, args);
                    break;
                case "players":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCPlayers.onCommand(sender, command, msg, args);
                    break;
                case "portal":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCPortal.onCommand(sender, command, msg, args);
                    break;
                case "stats":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCStats.onCommand(sender, command, msg, args);
                    break;
                case "teams":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCTeams.onCommand(sender, command, msg, args);
                    break;
                case "title":
                    if (hasPerm(sender, false, true))
                        GCTitle.onCommand(sender, command, msg, args);
                    break;
                case "warp":
                    if (hasPerm(sender, false, true) && isNotNull(sender, true))
                        GCWarp.onCommand(sender, command, msg, args);
                    break;
                case "zone":
                    if (hasPerm(sender, true, true) && isNotNull(sender, true))
                        GCZone.onCommand(sender, command, msg, args);
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
        boolean r = GManager.getCurrentGame() != null;
        if (!r && out)
            sender.sendMessage("Aucune partie n'est en cours."
                    + (sender.isOp() ? "\nUtilisez /" + Main.CMD + " game new <id> pour créer une partie."
                    : "Patientez un peu, le temps que les admins créent une partie."));
        return r;
    }

    public static boolean hasPerm(CommandSender sender, boolean god, boolean out) {
        boolean r = !(sender instanceof Player) || sender.isOp()
                || (GManager.getCurrentGame() != null && GManager.getCurrentGame()
                .getPlayer(sender.getName(), false) != null && GManager.getCurrentGame()
                .getPlayer(sender.getName(), false).getTeam() != null && (GManager.getCurrentGame()
                .getPlayer(sender.getName(), false).getTeam().getId().equals(GTeam.GODS_ID)
                || (!god && GManager.getCurrentGame().getPlayer(sender.getName(), false)
                .getTeam().getId().equals(GTeam.SPECS_ID))));
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
                    temp.addAll(GCHelp.onTabComplete(sender, command, msg, args));
                    break;
                case "activations":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCActivations.onTabComplete(sender, command, msg, args));
                    break;
                case "banner":
                    if (hasPerm(sender, false, false))
                        temp.addAll(GCBanner.onTabComplete(sender, command, msg, args));
                    break;
                case "bc":
                case "broadcast":
                    if (hasPerm(sender, true, false))
                        temp.addAll(GCBroadcast.onTabComplete(sender, command, msg, args));
                    break;
                case "compass":
                    if (isNotNull(sender, false))
                        temp.addAll(GCCompass.onTabComplete(sender, command, msg, args));
                    break;
                case "date":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCDate.onTabComplete(sender, command, msg, args));
                    break;
                case "game":
                    if (sender.isOp())
                        temp.addAll(GCGame.onTabComplete(sender, command, msg, args));
                    break;
                case "locks":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCLocks.onTabComplete(sender, command, msg, args));
                    break;
                case "perm":
                case "permissions":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCPermissions.onTabComplete(sender, command, msg, args));
                    break;
                case "players":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCPlayers.onTabComplete(sender, command, msg, args));
                    break;
                case "portal":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCPortal.onTabComplete(sender, command, msg, args));
                    break;
                case "stats":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCStats.onTabComplete(sender, command, msg, args));
                    break;
                case "teams":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCTeams.onTabComplete(sender, command, msg, args));
                    break;
                case "title":
                    if (hasPerm(sender, false, false))
                        temp.addAll(GCTitle.onTabComplete(sender, command, msg, args));
                    break;
                case "warp":
                    if (hasPerm(sender, false, false) && isNotNull(sender, false))
                        temp.addAll(GCWarp.onTabComplete(sender, command, msg, args));
                    break;
                case "zone":
                    if (hasPerm(sender, true, false) && isNotNull(sender, false))
                        temp.addAll(GCZone.onTabComplete(sender, command, msg, args));
                    break;
            }

        return new ArrayList<String>() {{
            for (String arg : temp)
                if (arg.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    add(arg);
        }};
    }
}
