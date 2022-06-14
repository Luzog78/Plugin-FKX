package fr.luzog.pl.fkx.commands.Other;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Test implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, "/test");

        FKManager.getCurrentGame().start();

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        return new ArrayList<>();
    }

}
