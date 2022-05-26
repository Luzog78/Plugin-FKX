package fr.luzog.pl.fkx.commands.Cheat;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Heal implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/heal [<players...>]";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 0)
            if (sender instanceof Player) {
                u.getPlayer().setHealth(u.getPlayer().getMaxHealth());
                u.succ("Vous avez été §cguéri§r !");
            } else
                u.err(CmdUtils.err_not_player);

        else if(args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();

        else
            CmdUtils.getPlayersFromArray(args, 0).forEach(player -> {
                player.setHealth(player.getMaxHealth());
                u.succ("§6" + player.getDisplayName() + "§r a été §cguéri§r !");
            });

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        LinkedHashSet<Player> content = CmdUtils.getPlayersFromArray(args, 0);
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (content.contains(p))
                    add("!" + p.getName());
                else
                    add(p.getName());
            });
            if (!content.isEmpty())
                add("!@a");
            if (content.size() != Bukkit.getOnlinePlayers().size())
                add("@a");
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
