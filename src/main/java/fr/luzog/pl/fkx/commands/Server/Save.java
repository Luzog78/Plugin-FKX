package fr.luzog.pl.fkx.commands.Server;//package fr.luzog.pl.uhc.commands.Server;
//
//import java.util.List;
//
//import org.bukkit.Bukkit;
//import org.bukkit.World;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabCompleter;
//
//import fr.luzog78.adventure.Main;
//import fr.luzog78.adventure.ressources.Backpacks;
//import fr.luzog78.adventure.ressources.Function;
//
//public class Save implements CommandExecutor, TabCompleter {
//	Main main;
//
//	public Save(Main main) {
//		this.main = main;
//	}
//
//	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
//		/*World world = Bukkit.getWorld("world");
//		sender.sendMessage("" + world.getAmbientSpawnLimit());
//		sender.sendMessage("" + world.getAnimalSpawnLimit());
//		sender.sendMessage("" + world.getKeepSpawnInMemory());
//		sender.sendMessage("" + world.getFullTime());
//		sender.sendMessage("" + world.getGameRules());
//		sender.sendMessage("" + world.getWorldType());
//		sender.sendMessage("" + world.getWorldBorder());
//		sender.sendMessage("" + world.getWeatherDuration());
//		sender.sendMessage("" + world.getSpawnLocation());
//		sender.sendMessage("" + world.getRaids());
//		sender.sendMessage("" + world.getAllowMonsters());
//		sender.sendMessage("" + world.getAllowAnimals());
//		sender.sendMessage("" + world.getWaterAnimalSpawnLimit());
//		sender.sendMessage("" + world.getTime());
//		sender.sendMessage("" + world.getTicksPerMonsterSpawns());
//		sender.sendMessage("" + world.getTicksPerAnimalSpawns());
//		sender.sendMessage("" + world.getThunderDuration());
//		sender.sendMessage("" + world.getTemperature(50, 25));
//		sender.sendMessage("" + world.getSeaLevel());
//		sender.sendMessage("" + world.getSeed());*/
//		sender.sendMessage("Saving...");
//		Backpacks.backpackAndEnderChestSaving();
//		for(World w : main.getServer().getWorlds()) {
//			sender.sendMessage("Saving \"" + w.getName() + "\" ...");
//			w.save();
//		}
//		sender.sendMessage("Save Done !");
//
//		Bukkit.getServer().reload();
//
//		return false;
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
//		return Function.stringList("");
//	}
//}
