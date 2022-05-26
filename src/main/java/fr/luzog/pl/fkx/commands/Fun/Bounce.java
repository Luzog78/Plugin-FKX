package fr.luzog.pl.fkx.commands.Fun;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Bounce implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/bounce [-v <dx> <dy> <dz>] <entities...>";
    public static final Vector defaultVector = new Vector(0, 50, 0);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        Vector v = defaultVector;

        if (args.length >= 1 && (args[0].equals("?") || args[0].equalsIgnoreCase("help")))
            u.synt();

        else if (args.length >= 1) {
            int sub = 0;
            boolean fails = false;
            if (args[0].equalsIgnoreCase("-v"))
                if (args.length >= 5)
                    try {
                        v = new Vector(Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                        sub = 4;
                    } catch (NumberFormatException ignored) {
                        fails = true;
                    }
                else
                    fails = true;
            Vector finalV = v;
            if (fails)
                u.synt();
            else
                CmdUtils.getEntitiesFromArray(args, sub).forEach(e -> {
                    e.setVelocity(e.getVelocity().add(finalV));
                    u.succ("Vélocité de§f", finalV, "§rappliquée sur", e instanceof Player ? "§6" + ((Player) e).getDisplayName()
                            : "§b" + e.getType().toString().toUpperCase().charAt(0) + e.getType().toString().toLowerCase().substring(1), "§r!");
                });
        }

        else
            u.synt();

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        LinkedHashSet<Entity> content = CmdUtils.getEntitiesFromArray(args, 0);
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if(args.length == 1)
                add("-v");
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (content.contains(p))
                    add("!" + p.getName());
                else
                    add(p.getName());
            });
            if (!content.isEmpty()) {
                add("!@e");
                add("!@a");
            }
            if (content.size() != Bukkit.getOnlinePlayers().size()) {
                add("@e");
                add("@a");
            }
            Main.world.getEntities().forEach(e -> {
                if (content.contains(e))
                    add("!" + e.getUniqueId().toString());
                else
                    add(e.getUniqueId().toString());
            });
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }

}
