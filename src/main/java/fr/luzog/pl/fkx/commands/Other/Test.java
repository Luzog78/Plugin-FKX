package fr.luzog.pl.fkx.commands.Other;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Crafting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Test implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, "/test");

		u.succ(Bukkit.getOfflinePlayer(UUID.fromString("6ecad76a-6c70-4e35-b8de-76ceaf7b4e40")).getName());

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		return new ArrayList<>();
	}

}
