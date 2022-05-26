package fr.luzog.pl.fkx.commands.Admin;//package fr.luzog.pl.uhc.commands.Admin;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import fr.luzog.pl.uhc.Main;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabCompleter;
//import org.bukkit.entity.Player;
//
//public class Fly implements CommandExecutor, TabCompleter {
//	Main main;
//
//	public Fly(Main main) {
//		this.main = main;
//	}
//
//	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
//
//		Player player = null;
//		Player playerS = null;
//
//		String perm = "VIP";
//
//		if (args.length == 0) {
//			if (sender instanceof Player) {
//				player = (Player) sender;
//				playerS = (Player) sender;
//			} else {
//				sender.sendMessage(ChatColor.RED + "Desole mais tu n'es pas un joueur...");
//				return false;
//			}
//		} else {
//			if (args[0].equals("speed")) {
//				if (args.length == 2 || args.length == 3 || args.length == 4) {
//					float speed = 0.1f;
//					if (args[1].equals("default")) {
//						if (sender instanceof Player) {
//							player = (Player) sender;
//						} else {
//							sender.sendMessage(ChatColor.RED + "Desole mais tu n'es pas un joueur...");
//							return false;
//						}
//					} else {
//						try {
//							speed = Float.parseFloat(args[1]);
//							if (sender instanceof Player) {
//								player = (Player) sender;
//							} else {
//								sender.sendMessage(ChatColor.RED + "Desole mais tu n'es pas un joueur...");
//								return false;
//							}
//						} catch (NumberFormatException e) {
//							try {
//								if (sender.getServer().getPlayer(args[1]).isOnline()) {
//									player = sender.getServer().getPlayer(args[1]);
//								} else {
//									sender.sendMessage("�cLe joueur n'est pas en ligne");
//									return false;
//								}
//								if (args[2].equals("default")) {
//								} else {
//									try {
//										speed = Float.parseFloat(args[2]);
//									} catch (NumberFormatException e2) {
//										synt(sender);
//										return false;
//									}
//								}
//							} catch (NullPointerException e1) {
//								synt(sender);
//								return false;
//							}
//						}
//					}
//					if (sender instanceof Player) {
//						playerS = (Player) sender;
//						if (!Function.permission(playerS, perm)) {
//							sender.sendMessage("�cDesole mais tu nas pas la permission.");
//							return false;
//						}
//					}
//
//					player.setFlySpeed(speed);
//
//					if (args.length == 0 || !args[args.length - 1].equalsIgnoreCase("c")) {
//						Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName()
//								+ "�a a mis sa vitesse de fly a �6" + speed + " �a!");
//					}
//					return false;
//				} else {
//					synt(sender);
//					return false;
//				}
//			} else if (args.length == 1 || args.length == 2) {
//				if (args[0].equals("c")) {
//					if (sender instanceof Player) {
//						playerS = (Player) sender;
//					} else {
//						sender.sendMessage(ChatColor.RED + "Desole mais tu n'es pas un joueur...");
//						return false;
//					}
//				} else {
//					try {
//						if (sender.getServer().getPlayer(args[0]).isOnline()) {
//							player = sender.getServer().getPlayer(args[0]);
//						} else {
//							sender.sendMessage("�cLe joueur n'est pas en ligne");
//							return false;
//						}
//					} catch (NullPointerException e) {
//						sender.sendMessage("�cAucun Joueur n'a ete trouve");
//						return false;
//					}
//					if (sender instanceof Player) {
//						playerS = (Player) sender;
//						if (!Function.permission(playerS, perm)) {
//							sender.sendMessage("�cDesole mais tu nas pas la permission.");
//							return false;
//						}
//					}
//				}
//			} else {
//				synt(sender);
//				return false;
//			}
//		}
//
//		if (player.getAllowFlight() == true) {
//			player.setAllowFlight(false);
//			CustomConfigFiles.force("Users", "users." + player.getUniqueId() + ".mode.fly", false);
//			main.saveConfig();
//			main.reloadConfig();
//			if (args.length == 0 || !args[args.length - 1].equalsIgnoreCase("c")) {
//				Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "�a nest plus en mode �6Fly�a !");
//			}
//		} else {
//			player.setAllowFlight(true);
//			player.setFlying(true);
//			CustomConfigFiles.force("Users", "users." + player.getUniqueId() + ".mode.fly", true);
//			main.saveConfig();
//			main.reloadConfig();
//			if (args.length == 0 || !args[args.length - 1].equalsIgnoreCase("c")) {
//				Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "�a est en mode �6Fly�a !");
//			}
//		}
//
//		return false;
//	}
//
//	public void synt(CommandSender sender) {
//		sender.sendMessage("�cLa syntaxe est : /fly [player] ou /fly speed [player] <speed>");
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
//		if (args.length > 3)
//			return Function.stringList("");
//		List<String> l = new ArrayList<>();
//		List<String> list = new ArrayList<>();
//
//		Random r = new Random();
//
//		if (args.length == 1) {
//			l.add("speed");
//			for (String pName : Function.playersNameList()) {
//				l.add(pName);
//			}
//		} else if ((args.length == 2) && args[0].equals("speed")) {
//			l.add("0.1");
//			l.add("default");
//			l.add(r.nextDouble() + "");
//			l.add(r.nextDouble() + "");
//			for (String pName : Function.playersNameList()) {
//				l.add(pName);
//			}
//		} else if ((args.length == 3) && args[0].equals("speed") && Function.playersNameList().contains(args[1])) {
//			l.add("0.1");
//			l.add("default");
//			l.add(r.nextDouble() + "");
//			l.add(r.nextDouble() + "");
//			l.add(r.nextFloat() + "");
//		} else {
//			return Function.stringList("");
//		}
//
//		for (String li : l) {
//			if (li.startsWith(args[0])) {
//				list.add(li);
//			}
//		}
//		return list;
//	}
//}
