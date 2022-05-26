package fr.luzog.pl.fkx.commands.Other;//package fr.luzog.pl.uhc.commands.Other;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabCompleter;
//
//import fr.luzog78.adventure.Main;
//import fr.luzog78.adventure.ressources.Function;
//
//public class Test2 implements CommandExecutor, TabCompleter {
//	Main main;
//
//	public Test2(Main main) {
//		this.main = main;
//	}
//
//	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
//
//		return false;
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
//		if (args.length > 1)
//			return Function.stringList("");
//		List<String> l = new ArrayList<>();
//		List<String> list = new ArrayList<>();
//		l.add("test");
//		for (String li : l) {
//			if (li.startsWith(args[0])) {
//				list.add(li);
//			}
//		}
//		return list;
//	}
//}
