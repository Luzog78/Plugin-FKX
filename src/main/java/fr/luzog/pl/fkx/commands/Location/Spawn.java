package fr.luzog.pl.fkx.commands.Location;//package fr.luzog.pl.uhc.commands.Location;
//
//import java.util.List;
//
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabCompleter;
//import org.bukkit.entity.Player;
//
//import fr.luzog78.adventure.Main;
//import fr.luzog78.adventure.ressources.Function;
//
//public class Spawn implements CommandExecutor, TabCompleter {
//	Main main;
//
//	public Spawn(Main main) {
//		this.main = main;
//	}
//
//	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
//
//		if (sender instanceof Player) {
//			Player player = (Player) sender;
//
//
//			Function.setLocConfig(player);
//
//			player.teleport(Function.getSpawn());
//
//			if (args.length == 0 || !args[args.length - 1].equalsIgnoreCase("c")) {
//				Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "�a c tp au spawn !");
//			}
//
//		} else {
//			sender.sendMessage(ChatColor.RED + "Desole mais tu n'es pas un joueur...");
//		}
//
//		return false;
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
//		return Function.stringList("");
//	}
//}
