package fr.luzog.pl.fkx.commands.Admin;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Execute implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/execute <player> <command>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 1 && (args[0].equals("?") || args[0].equalsIgnoreCase("help")))
            u.synt();
        else if (args.length >= 2)
            if (Bukkit.getPlayerExact(args[0]) != null || args[0].equalsIgnoreCase("@a")) {
                String c = "";
                boolean sub = true;
                for (String a : args) {
                    if (sub) {
                        if (!a.equals(""))
                            sub = false;
                    } else
                        c += " " + a;
                }

                if (c.startsWith(" "))
                    c = c.substring(1);
                if (c.startsWith("/"))
                    c = c.substring(1);

                String finalC = c;
                new ArrayList<Player>() {{
                    if (args[0].equalsIgnoreCase("@a"))
                        addAll(Bukkit.getOnlinePlayers());
                    else
                        add(Bukkit.getPlayerExact(args[0]));
                }}.forEach(player -> player.performCommand(finalC));
            } else
                u.err(CmdUtils.err_player_not_found);
        else
            u.synt();

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        if (args.length != 1)
            return new ArrayList<>();
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
    }
}
