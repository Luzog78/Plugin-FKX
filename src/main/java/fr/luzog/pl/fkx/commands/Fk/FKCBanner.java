package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FKCBanner {
    public static final String syntaxe = "/fk banner [color]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            if (sender instanceof Player)
                u.getPlayer().getInventory().addItem(FKManager.getBanner());
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else
            try {
                ChatColor c = ChatColor.valueOf(args[1].toUpperCase());
                if (sender instanceof Player)
                    u.getPlayer().getInventory().addItem(FKManager.getBanner(c));
                else
                    u.err(CmdUtils.err_not_player);
            } catch (IllegalArgumentException e) {
                u.err("Couleur '" + args[1] + "' inconnue.");
            }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            if (args.length == 2)
                addAll(Arrays.stream(ChatColor.values()).map(Enum::name).collect(Collectors.toList()));
        }};
    }
}
