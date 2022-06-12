package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class FKCHelp {
    public static final String syntaxe = "/fk (help || ?)";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        u.send("", Main.HEADER);
        u.send("", "§6/fk help §7- §eAffiche l'aide");
        u.send("", Main.FOOTER);

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<>();
    }
}
