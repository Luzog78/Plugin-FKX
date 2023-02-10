package fr.luzog.pl.fkx.commands.Admin;

import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPermissions;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Pardon implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/pardon <player>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (sender instanceof Player && !sender.isOp()) {
            if (GManager.getCurrentGame() == null
                    || GManager.getCurrentGame().getPlayer(sender.getName(), false) == null
                    || !GManager.getCurrentGame().getPlayer(sender.getName(), false)
                    .hasPermission(GPermissions.Type.BAN, u.getPlayer().getLocation())) {
                u.err("Vous n'avez pas la permission de faire cela !");
                return false;
            }
        }


        if (args.length == 0 || args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();

        else
            Bukkit.getBanList(BanList.Type.NAME).getBanEntries().forEach(ban -> {
                if (ban.getTarget().equalsIgnoreCase(args[0])) {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(ban.getTarget());
                    u.succ("Le joueur §6" + args[0] + "§r a bien été §2§ldébanni§r du serveur !");
                }
            });

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        Bukkit.getBanList(BanList.Type.NAME).getBanEntries().stream().map(BanEntry::getTarget).forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}