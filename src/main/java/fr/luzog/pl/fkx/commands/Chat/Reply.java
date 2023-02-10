package fr.luzog.pl.fkx.commands.Chat;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class Reply implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/reply <message...>";

    private static final Msg MSG_INSTANCE = new Msg();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 0 || args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();

        else if (Msg.replies.containsKey(sender.getName())) {
            Bukkit.dispatchCommand(sender, "msg " + Msg.replies.get(sender.getName()).getKey() + " " + String.join(" ", args));
        } else
            u.err("Vous n'avez pas de §6conversation§r en cours.");

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        String[] a = new String[args.length + 1];
        a[0] = Msg.CONSOLE;
        System.arraycopy(args, 0, a, 1, a.length - 1);
        return MSG_INSTANCE.onTabComplete(sender, cmd, msg, a);
    }
}