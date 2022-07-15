package fr.luzog.pl.fkx.commands.Fun;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Burn implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/burn <player> <seconds>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length >= 2)
            if (Bukkit.getOfflinePlayer(args[0]).isOnline())
                try {
                    Player p = Bukkit.getPlayerExact(args[0]);
                    p.setFireTicks(args[1].equalsIgnoreCase("stop") ? 0 : Integer.parseInt(args[1]) * 20);
                    u.succ((args[1].equalsIgnoreCase("stop") ?
                            "Vous §barrêtez§r les brûlures de §6" + p.getDisplayName() + "§r."
                            : "Vous avez §cenflammé§r §6" + p.getDisplayName() + "§r pour §b"
                            + Integer.parseInt(args[1]) + " secondes§r..."));
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format);
                }
            else
                u.err(CmdUtils.err_player_not_found);
        else
            u.synt();

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if (args.length == 1)
                addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
