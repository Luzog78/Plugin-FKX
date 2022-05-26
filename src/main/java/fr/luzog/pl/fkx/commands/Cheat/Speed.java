package fr.luzog.pl.fkx.commands.Cheat;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Speed implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/speed [<players>] (w || walk | f || fly) <speed>";
    public static final String err_too_high = "La vitesse est comprise entre 0 et 1.";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if(args.length == 1 && (args[0].equals("?") || args[0].equalsIgnoreCase("help")))
            u.synt();

        if (args.length == 2)
            if (Arrays.asList("w", "walk", "f", "fly").contains(args[0].toLowerCase()))
                if (sender instanceof Player)
                    try {
                        float speed = Float.parseFloat(args[1]);
                        if (args[0].equalsIgnoreCase("w") || args[0].contains("walk"))
                            ((CraftPlayer) u.getPlayer()).getHandle().abilities.walkSpeed = speed / 2;
                        else
                            ((CraftPlayer) u.getPlayer()).getHandle().abilities.flySpeed = speed / 2;
                        u.succ("Vous avez défini votre vitesse de",
                                (args[0].equalsIgnoreCase("w") || args[0].contains("walk") ? "§9Marche" : "§bVol"),
                                "§rà§e", speed, "§r!");
                    } catch (NumberFormatException ignored) {
                        u.err(CmdUtils.err_number_format);
                    } catch (IllegalArgumentException ignored) {
                        u.err(err_too_high);
                    }
                else
                    u.err(CmdUtils.err_not_player);
            else
                u.synt();

        else if (args.length == 3)
            if (Bukkit.getPlayerExact(args[0]) != null || args[0].equalsIgnoreCase("@a"))
                if (Arrays.asList("w", "walk", "f", "fly").contains(args[1].toLowerCase()))
                    try {
                        float speed = Float.parseFloat(args[2]);
                        new ArrayList<Player>() {{
                            if (args[0].equalsIgnoreCase("@a"))
                                addAll(Bukkit.getOnlinePlayers());
                            else
                                add(Bukkit.getPlayerExact(args[0]));
                        }}.forEach(player -> {
                            if (args[1].equalsIgnoreCase("w") || args[1].contains("walk"))
                                ((CraftPlayer) player).getHandle().abilities.walkSpeed = speed / 2;
                            else
                                ((CraftPlayer) player).getHandle().abilities.flySpeed = speed / 2;
                        });
                        u.succ("Vous avez défini la vitesse de",
                                (args[1].equalsIgnoreCase("w") || args[1].contains("walk") ? "§9Marche" : "§bVol"),
                                "§rde§6",
                                (args[0].equalsIgnoreCase("@a") ? "tout le monde" : Bukkit.getPlayerExact(args[0]).getDisplayName()),
                                "§rà§e", speed, "§r!");
                    } catch (NumberFormatException ignored) {
                        u.err(CmdUtils.err_number_format);
                    } catch (IllegalArgumentException ignored) {
                        u.err(err_too_high);
                    }
                else
                    u.synt();
            else
                u.err(CmdUtils.err_player_not_found);

        else
            u.synt();

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        if (args.length > 3)
            return new ArrayList<>();

        ArrayList<String> list = new ArrayList<>();

        List<String> t = Arrays.asList("w", "walk", "f", "fly");
        List<String> s = Arrays.asList("0.1", "0.2", "0.3", "0.5", "0.75", "1.0");

        new ArrayList<String>() {{
            if (args.length == 1) {
                addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
                addAll(t);
            }
            else if(args.length == 2){
                if(!Arrays.asList("w", "walk", "f", "fly").contains(args[0].toLowerCase()))
                    addAll(t);
                else
                    addAll(s);
            }
            else if(args.length == 3){
                if(!Arrays.asList("w", "walk", "f", "fly").contains(args[0].toLowerCase()))
                    addAll(s);
            }
            addAll(Arrays.asList("2.0", "6.0", "10.0", "20.0", "40.0", "60.0", "100.0"));
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
