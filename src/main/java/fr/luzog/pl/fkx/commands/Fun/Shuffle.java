package fr.luzog.pl.fkx.commands.Fun;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Shuffle implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/shuffle <players...>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 0 || args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();
        else
            CmdUtils.getPlayersFromArray(args, 0).forEach(player -> {
                ArrayList<ItemStack> items = new ArrayList<>();
                player.getInventory().forEach(items::add);
                Collections.shuffle(items);
                for (int i = 0; i < items.size(); i++)
                    player.getInventory().setItem(i, items.get(i));
                player.updateInventory();
                u.succ("L'inventaire de§6", player.getDisplayName(), "§ra été §emélangé§r !");
            });

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        return new ArrayList<String>() {{
            Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()).forEach(p -> {
                if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    add(p);
            });
        }};
    }

}
