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

public class God implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/god [(on | off)] [<players...>]";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        // 0 -> Switch ; -1 -> On ; 20 -> Off
        int mode = args.length >= 1 ? (args[0].equalsIgnoreCase("on") ? -1 :
                args[0].equalsIgnoreCase("off") ? 20 : 0) : 0;

        if (args.length == 0 || (args.length == 1 && mode != 0))
            if (sender instanceof Player) {
                int finalTick = mode == 0 ? u.getPlayer().getMaximumNoDamageTicks() == -1 ? 20 : -1 : mode;
                u.getPlayer().setMaximumNoDamageTicks(finalTick);
                u.succ("Vous êtes désormais§e", (finalTick == -1 ? "Invulnérable" : "Vulnérable"), "§r!");
            } else
                u.err(CmdUtils.err_not_player);

        else if (args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();

        else
            CmdUtils.getPlayersFromArray(args, mode == 0 ? 0 : 1).forEach(player -> {
                int finalTick = mode == 0 ? player.getMaximumNoDamageTicks() == -1 ? 20 : -1 : mode;
                player.setMaximumNoDamageTicks(finalTick);
                u.succ("§6" + player.getDisplayName() + "§r est désormais§e",
                        (finalTick == -1 ? "Invulnérable" : "Vulnérable"), "§r !");
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

            if (args.length == 1) {
                add("on");
                add("off");
            }
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
