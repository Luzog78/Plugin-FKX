package fr.luzog.pl.fkx.commands.Fun;

import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Head implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/head [<owner>]";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length >= 1 && (args[0].equalsIgnoreCase("help") || args[0].equals("?")))
            u.synt();

        else if (sender instanceof Player)
            if (u.getPlayer().getInventory().firstEmpty() != -1) {
                ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
                SkullMeta meta = (SkullMeta) is.getItemMeta();
                meta.setOwner(args.length == 0 ? u.getPlayer().getName() : String.join(" ", args));
                is.setItemMeta(meta);
                u.getPlayer().getInventory().addItem(is);
                u.succ("Vous avez récupéré la tête de§6", meta.getOwner(), "§r!");
            } else
                u.err(CmdUtils.err_inventory_full);

        else
            u.err(CmdUtils.err_not_player);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> temp = new ArrayList<>(new HashSet<String>() {{
            addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
            addAll(Arrays.stream(Heads.values()).map(Heads::getData).collect(Collectors.toList()));
        }});
        temp.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return temp;
    }
}
