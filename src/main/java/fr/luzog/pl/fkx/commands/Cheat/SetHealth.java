package fr.luzog.pl.fkx.commands.Cheat;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetHealth implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/sethealth [<player> | @a] <health>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 1)
            if (args[0].equals("?") || args[0].equalsIgnoreCase("help"))
                u.synt();
            else if (sender instanceof Player)
                try {
                    u.getPlayer().setMaxHealth(Double.parseDouble(args[0]));
                    u.succ("Vous avez redéfini vos points de vie maximums à§e", Double.parseDouble(args[0]), "§r!");
                } catch (NumberFormatException ignored) {
                    u.err(CmdUtils.err_number_format);
                }
            else
                u.err(CmdUtils.err_not_player);

        else if (args.length == 2)
            if (Bukkit.getPlayerExact(args[0]) != null || args[0].equalsIgnoreCase("@a"))
                try {
                    new ArrayList<Player>() {{
                        if (args[0].equalsIgnoreCase("@a"))
                            addAll(Bukkit.getOnlinePlayers());
                        else
                            add(Bukkit.getPlayerExact(args[0]));
                    }}.forEach(player -> player.setMaxHealth(Double.parseDouble(args[1])));
                    u.succ("Vous avez redéfini les points de vie maximums de§6",
                            (args[0].equalsIgnoreCase("@a") ? "tout le monde" : Bukkit.getPlayerExact(args[0]).getDisplayName()),
                            "§rà§e", Double.parseDouble(args[1]), "§r!");
                } catch (NumberFormatException ignored) {
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
        if (args.length > 2)
            return new ArrayList<>();

        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if (args.length == 1)
                addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
            addAll(Arrays.asList("2.0", "6.0", "10.0", "20.0", "40.0", "60.0", "100.0"));
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
