package fr.luzog.pl.fkx.commands.Admin;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Weather implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/(sun | rain | thunder) [<duration> | default]";

    public static enum WeatherType {SUN, RAIN, THUNDER}

    public WeatherType type;

    public Weather(WeatherType type) {
        this.type = type;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);
        World w = sender instanceof Player ? u.getPlayer().getWorld() : Bukkit.getWorld("world");

        // 12,000 ~ 179,999 ticks  :  if weather is CLEAR
        // 12,000 ~  23,999 ticks  :  if weather is RAIN / STORM
        int weatherDuration = new Random().nextInt(type == WeatherType.SUN ? 168000 : 12000) + 12000;

        // 12,000 ~ 179,999 ticks  :  for STORM OFF duration
        //  3,600 ~  15,999 ticks  :  for STORM ON duration
        int thunderDuration = type == WeatherType.SUN ? (new Random().nextInt(168000) + 12000) : (new Random().nextInt(12400) + 3600);

        if (args.length != 0 && !args[0].equalsIgnoreCase("default"))
            try {
                weatherDuration = Integer.parseInt(args[0]);
                thunderDuration = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                u.synt();
            }

        w.setStorm(type != WeatherType.SUN);
        w.setThundering(type == WeatherType.THUNDER);

        w.setWeatherDuration(weatherDuration);
        w.setThunderDuration(thunderDuration);

        u.succ("Vous avez mis", type == WeatherType.SUN ? "§6Le Soleil" : type == WeatherType.RAIN ? "§9La Pluie" : "§7L'Orage",
                "§rdans le monde§e", w.getName(), "§r!");

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        if (args.length != 1)
            return new ArrayList<>();

        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            add("default");
            Random r = new Random();
            for (int i = 0; i < 5; i++)
                add("" + r.nextInt(1000000));
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
