package fr.luzog.pl.fkx.commands.Other;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class Test implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, "/test");

        u.succ("test");

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        return new ArrayList<>();
    }

}
