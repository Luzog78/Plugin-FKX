package fr.luzog.pl.fkx.commands.Fun;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class Shuffle implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/shuffle (hotbar | inv) <players...>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length <= 1)
            u.synt();
        else if (args[0].equalsIgnoreCase("hotbar"))
            CmdUtils.getPlayersFromArray(args, 1).forEach(player -> {
                ArrayList<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < 9; i++)
                    items.add(player.getInventory().getItem(i));
                Collections.shuffle(items);
                for (int i = 0; i < items.size(); i++)
                    player.getInventory().setItem(i, items.get(i));
                player.updateInventory();
                u.succ("La hotbar de§6", player.getDisplayName(), "§ra été §emélangé§r !");
            });
        else if (args[0].equalsIgnoreCase("inv"))
            CmdUtils.getPlayersFromArray(args, 1).forEach(player -> {
                ArrayList<ItemStack> items = new ArrayList<>();
                player.getInventory().forEach(items::add);
                Collections.shuffle(items);
                for (int i = 0; i < items.size(); i++)
                    player.getInventory().setItem(i, items.get(i));
                player.updateInventory();
                u.succ("L'inventaire de§6", player.getDisplayName(), "§ra été §emélangé§r !");
            });
        else
            u.synt();

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if (args.length == 1) {
                add("hotbar");
                add("inv");
            } else {
                LinkedHashSet<Player> content = CmdUtils.getPlayersFromArray(args, 1);
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (content.contains(p))
                        add("!" + p.getName());
                    else
                        add(p.getName());
                });
                if (!content.isEmpty())
                    add("!@a");
                if (content.size() != Bukkit.getOnlinePlayers().size())
                    add("@a");
            }
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }

}
