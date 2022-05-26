package fr.luzog.pl.fkx.commands.Chat;//package fr.luzog.pl.uhc.commands.Chat;
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
//import fr.luzog78.adventure.ressources.Color;
//import fr.luzog78.adventure.ressources.Function;
//
//public class Broadcast implements CommandExecutor, TabCompleter {
//
//	Main main;
//	public Broadcast(Main main) {
//		this.main = main;
//	}
//
//	@Override
//	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
//
//		StringBuilder bc = new StringBuilder();
//		if(args.length == 0) {
//			if(sender instanceof Player) {
//				sender.sendMessage(ChatColor.RED + "La syntaxe est : /broadcast <message> ou /bc <message>" + ChatColor.RESET);
//			}else {
//				sender.sendMessage(Color.RED + "La syntaxe est : /broadcast <message> ou /bc <message>" + Color.RESET);
//			}
//		}else {
//			for(String part : args) {
//				bc.append(part + " ");
//			}
//
//			if(sender instanceof Player) {
//				Player player = (Player)sender;
//				Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Psssst... ! "
//						+ ChatColor.RESET + player.getDisplayName()
//						+ ChatColor.DARK_PURPLE + " a dit : "
//						+ ChatColor.BOLD + ChatColor.DARK_AQUA + bc.toString());
//			}else {
//				Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Excusez-moi ! "
//						+ ChatColor.DARK_GRAY + "["
//						+ ChatColor.BOLD + ChatColor.DARK_RED + "CONSOLE"
//						+ ChatColor.RESET + ChatColor.DARK_GRAY + "]"
//						+ ChatColor.DARK_PURPLE + " a dit : "
//						+ ChatColor.BOLD + ChatColor.DARK_AQUA + bc.toString());
//			}
//		}
//
//		return true;
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
//		return Function.stringList("");
//	}
//
//}
