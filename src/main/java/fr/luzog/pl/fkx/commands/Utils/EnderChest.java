package fr.luzog.pl.fkx.commands.Utils;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnderChest implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/ec [[<seer>] <target>]";

    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 0)
            if (sender instanceof Player) {
                u.getPlayer().openInventory(u.getPlayer().getEnderChest());
                u.succ("Vous ouvrez votre §dEnderChest§r !");
            } else
                u.err(CmdUtils.err_not_player);

        else if (args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();

        else if (args.length == 1)
            if (sender instanceof Player)
                if (Bukkit.getPlayerExact(args[0]) != null) {
                    u.getPlayer().openInventory(Bukkit.getPlayerExact(args[0]).getEnderChest());
                    u.succ("Vous ouvrez l'§dEnderChest§r de§6", Bukkit.getPlayerExact(args[0]).getDisplayName(), "§r!");
                } else
                    u.err(CmdUtils.err_player_not_found);
            else
                u.err(CmdUtils.err_not_player);

        else if (Bukkit.getPlayerExact(args[0]) != null)
            if (Bukkit.getPlayerExact(args[1]) != null) {
                Bukkit.getPlayerExact(args[0]).openInventory(Bukkit.getPlayerExact(args[1]).getEnderChest());
                u.succ("§6" + Bukkit.getPlayerExact(args[1]).getDisplayName(), "§rouvre l'§dEnderChest§r de§6",
                        Bukkit.getPlayerExact(args[1]).getDisplayName(), "§r!");
            } else
                u.err(CmdUtils.err_player_not_found, "('" + args[1] + "')");
        else
            u.err(CmdUtils.err_player_not_found, "('" + args[0] + "')");

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        if (args.length > 2)
            return new ArrayList<>();
        return new ArrayList<String>() {{
            Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()).forEach(p -> {
                if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    add(p);
            });
        }};
    }
}
