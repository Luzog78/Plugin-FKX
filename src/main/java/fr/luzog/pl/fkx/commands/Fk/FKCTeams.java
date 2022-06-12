package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FKCTeams {
    public static final String syntaxe = "/fk";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return null;
    }
}
