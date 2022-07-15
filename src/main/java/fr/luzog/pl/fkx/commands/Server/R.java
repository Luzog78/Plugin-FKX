package fr.luzog.pl.fkx.commands.Server;

import fr.luzog.pl.fkx.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class R implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        Bukkit.broadcastMessage(Main.PREFIX + "§4Le plugin se prepare...");
        Main.instance.getServer().reload();
        Bukkit.broadcastMessage(Main.PREFIX + "§2Le plugin est pret !");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        return new ArrayList<>();
    }
}
