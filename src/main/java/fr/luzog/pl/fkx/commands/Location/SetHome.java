package fr.luzog.pl.fkx.commands.Location;//package fr.luzog.pl.uhc.commands.Location;
//
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import fr.luzog78.adventure.Main;
//import fr.luzog78.adventure.ressources.Function;
//
//public class SetHome implements CommandExecutor {
//	Main main;
//
//	public SetHome(Main main) {
//		this.main = main;
//	}
//
//	@SuppressWarnings("null")
//	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
//
//		if (sender instanceof Player) {
//			Player player = (Player) sender;
//
//			Location loc = null;
//
//			if(args.length >= 1) {
//				if (args[0].contains("'") || args[0].contains("\"") || args[0].contains(" ") || args[0].contains("�")
//						|| args[0].contains("\"")) {
//					sender.sendMessage("�cLe nom de doit pas contenir d'espace (\" \"), d'apostrophe (\"'\"), "
//							+ "de guillemets (\"\"\") ou de signe paragraphe (\"�\").");
//					return false;
//				}else if(main.getConfig().getConfigurationSection("locations.homes").getKeys(false).contains(args[0])) {
//					sender.sendMessage("�cLe home est deja utilise.");
//					return false;
//				}
//			}
//
//			if (args.length == 1 || args.length == 3) {
//				loc = player.getLocation();
//				if (args.length == 3) {
//					try {
//						loc.setYaw(Float.parseFloat(args[1]));
//						loc.setPitch(Float.parseFloat(args[2]));
//					} catch (NumberFormatException e) {
//						sender.sendMessage("�cLa syntaxe est : /sethome <name> [<x> <y> <z>] [<yaw> <pitch>]");
//					}
//				}
//			} else if (args.length == 4 || args.length == 6) {
//				try {
//					loc.setX(Double.parseDouble(args[1]));
//					loc.setY(Double.parseDouble(args[2]));
//					loc.setZ(Double.parseDouble(args[3]));
//				} catch (NumberFormatException e) {
//					sender.sendMessage("�cLa syntaxe est : /sethome <name> [<x> <y> <z>] [<yaw> <pitch>]");
//				}
//				if (args.length == 6) {
//					try {
//						loc.setYaw(Float.parseFloat(args[4]));
//						loc.setPitch(Float.parseFloat(args[5]));
//					} catch (NumberFormatException e) {
//						sender.sendMessage("�cLa syntaxe est : /sethome <name> [<x> <y> <z>] [<yaw> <pitch>]");
//					}
//				}
//			} else {
//				sender.sendMessage(ChatColor.RED + "La syntaxe est : /sethome <name> [<x> <y> <z>] [<yaw> <pitch>]");
//			}
//
//			Function.setLocInConfig("Locations", "locations.homes", args[0], loc);
//
//			if (args.length == 0 || !args[args.length - 1].equalsIgnoreCase("c")) {
//				Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "�a a pos� un home " + args[0] + " en ... !");
//			}
//
//		} else {
//			sender.sendMessage(ChatColor.RED + "Desole mais tu n'es pas un joueur...");
//		}
//
//		return false;
//	}
//}
