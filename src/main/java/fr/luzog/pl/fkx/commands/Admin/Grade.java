package fr.luzog.pl.fkx.commands.Admin;//package fr.luzog.pl.uhc.commands.Admin;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
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
//import fr.luzog78.adventure.config.CustomConfigFiles;
//import fr.luzog78.adventure.ressources.Function;
//
//public class Grade implements CommandExecutor, TabCompleter {
//	Main main;
//
//	public Grade(Main main) {
//		this.main = main;
//	}
//
//	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
//
//		if (args.length == 1 || args.length == 2) {
//			if (sender instanceof Player) {
//				Player player = (Player) sender;
//
//				Map<String, Integer> permByName = new HashMap<>(); // permission list
//				Map<Integer, String> permByLvl = new HashMap<>(); // permission list
//				int pl = 1; // permission lvl
//				for (String grade : CustomConfigFiles.getCCF("Grades").getConfigurationSection("grades").getKeys(false)) {
//					permByName.put(grade, pl);
//					permByLvl.put(pl, grade);
//					pl++;
//				}
//				String playerGrade = Function.grade(player);
//				int playerGradeLvl = permByName.get(playerGrade);
//
//				String rank = "�cERROR";
//
//				if (args[0].equals("rankup")) {
//					if (Function.permission(player, "Moderator")) {
//						player.sendMessage("�cVous etes du staff, vous de pouvez pas rankup");
//						return false;
//					} else if (Function.permission(player, permByLvl.get(permByName.get("Moderator") - 1))) {
//						player.sendMessage("�cVous n'etes pas du staff, vous de pouvez pas rankup");
//						return false;
//					}
//					CustomConfigFiles.force("Users", "users." + player.getUniqueId() + ".grade",
//							permByLvl.get(playerGradeLvl + 1));
//					CustomConfigFiles.refresh("Users");
//					rank = "rankup";
//				} else if (args[0].equals("rankdown")) {
//					if (Function.permission(player, "Moderator")) {
//						player.sendMessage("�cVous etes du staff, vous de pouvez pas rankdown");
//						return false;
//					}
//					CustomConfigFiles.force("Users", "users." + player.getUniqueId() + ".grade",
//							permByLvl.get(playerGradeLvl - 1));
//					CustomConfigFiles.refresh("Users");
//					rank = "rankdown";
//				} else {
//					synt(sender);
//					return false;
//				}
//
//				if (args.length == 2) {
//					if (!args[1].equalsIgnoreCase("c")) {
//						synt(sender);
//						return false;
//					}
//				}
//
//				if (args.length == 0 || !args[args.length - 1].equalsIgnoreCase("c")) {
//					Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "�a a " + rank + " !");
//				}
//			} else {
//				sender.sendMessage(ChatColor.RED + "Desole mais tu n'es pas un joueur...");
//				return false;
//			}
//		} else if (args.length == 3 || args.length == 4) {
//			Player player = null;
//			try {
//				if (sender.getServer().getPlayer(args[1]).isOnline()) {
//					player = sender.getServer().getPlayer(args[1]);
//				} else {
//					sender.sendMessage("�cLe joueur n'est pas en ligne");
//					return false;
//				}
//			} catch (NullPointerException e) {
//				sender.sendMessage("�cAucun Joueur n'a ete trouve");
//				return false;
//			}
//			if (CustomConfigFiles.getCCF("Grades").getConfigurationSection("grades").getKeys(false).contains(args[2])) {
//				if (sender instanceof Player) {
//					Player player2 = (Player) sender;
//					if (args[2].equals("Admin")) {
//						if (!Function.permission(player2, "Admin")) {
//							player2.sendMessage("�cDesole mais tu n'as pas la permission");
//							return false;
//						}
//					} else if (args[2].equals("Operator")) {
//						if (!Function.permission(player2, "Operator")) {
//							player2.sendMessage("�cDesole mais tu n'as pas la permission");
//							return false;
//						}
//					} else {
//						if (!Function.permission(player2, "Moderator")) {
//							player2.sendMessage("�cDesole mais tu n'as pas la permission");
//							return false;
//						}
//					}
//				} else {
//				}
//				CustomConfigFiles.force("Users", "users." + player.getUniqueId() + ".grade", args[2]);
//				CustomConfigFiles.refresh("Users");
//				if (args.length == 0 || !args[args.length - 1].equalsIgnoreCase("c")) {
//					Bukkit.broadcastMessage(
//							ChatColor.YELLOW + player.getDisplayName() + "�a est maintenant " + args[2] + " �a!");
//				}
//			} else {
//				sender.sendMessage("�cGrade non existant");
//				return false;
//			}
//		} else {
//			synt(sender);
//		}
//
//		return false;
//	}
//
//	public void synt(CommandSender sender) {
//		sender.sendMessage("�cLa syntaxe est : /grade <rankup|rankdown> ou /grade set <player> <grade>");
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
//		if (args.length > 3)
//			return Function.stringList("");
//		List<String> l = new ArrayList<>();
//		List<String> list = new ArrayList<>();
//
//		if (args.length == 1) {
//			l.add("set");
//			l.add("rankup");
//			l.add("rankdown");
//			for (String li : l) {
//				if (li.startsWith(args[0])) {
//					list.add(li);
//				}
//			}
//		} else if (args.length == 2) {
//			if (args[0].equals("set")) {
//				for (Player p : main.getServer().getOnlinePlayers()) {
//					l.add(p.getName());
//				}
//				for (String li : l) {
//					if (li.startsWith(args[1])) {
//						list.add(li);
//					}
//				}
//			} else {
//				return Function.stringList("");
//			}
//		} else if (args.length == 3 && args[0].equals("set")) {
//			for (String grade : CustomConfigFiles.getCCF("Grades").getConfigurationSection("grades").getKeys(false)) {
//				l.add(grade);
//			}
//			for (String li : l) {
//				if (li.startsWith(args[2])) {
//					list.add(li);
//				}
//			}
//		} else {
//			return Function.stringList("");
//		}
//
//		return list;
//	}
//}
