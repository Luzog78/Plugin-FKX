package fr.luzog.pl.fkx.commands.Other;

import java.util.ArrayList;
import java.util.List;

import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Crafting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Test implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, "/test");

		u.getPlayer().openInventory(Crafting.getInv());

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		return new ArrayList<>();
	}

}
