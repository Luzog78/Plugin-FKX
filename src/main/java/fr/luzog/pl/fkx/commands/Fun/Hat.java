package fr.luzog.pl.fkx.commands.Fun;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Hat implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/hat [<material>] [<player>]";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length >= 1 && (args[0].equals("?") || args[0].equalsIgnoreCase("help")))
            u.synt();

        else if (args.length == 0)
            if (sender instanceof Player)
                hat(u.getPlayer(), u);
            else
                u.err(CmdUtils.err_not_player);

        else if(Material.matchMaterial(args[0]) != null)
            if (args.length == 1)
                if (sender instanceof Player)
                    hat(u.getPlayer(), new ItemStack(Material.matchMaterial(args[0])), u);
                else
                    u.err(CmdUtils.err_not_player);
            else
                CmdUtils.getPlayersFromArray(args, 1).forEach(player ->
                        hat(player, new ItemStack(Material.matchMaterial(args[0])), u));

        else
            CmdUtils.getPlayersFromArray(args, 0).forEach(player -> hat(player, u));

        return false;
    }

    public static void hat(Player p, ItemStack is, CmdUtils u){
        if ((p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR)
                && (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR))
            u.err(p.equals(u.getPlayer()) ? CmdUtils.err_too_items_hold :
                    CmdUtils.err_too_items_hold_on.replace("%PLAYER%", p.getDisplayName()));
        else {
            if (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR)
                p.setItemInHand(p.getInventory().getHelmet());
            p.getInventory().setHelmet(is);
            u.succ("Vous avez remplacé", (p.equals(u.getPlayer()) ? "votre tête" :
                    "la tête de §6" + p.getDisplayName()), "§rpar§b", is.getType().name(), "§r!");
        }
    }

    public static void hat(Player p, CmdUtils u){
        if ((p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR)
                || (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR)) {
            ItemStack hand = p.getItemInHand();
            p.setItemInHand(p.getInventory().getHelmet());
            p.getInventory().setHelmet(hand);
            u.succ("Vous avez remplacé", (p.equals(u.getPlayer()) ? "votre tête" :
                    "la tête de §6" + p.getDisplayName()), "§rpar§b", hand.getType().name(), "§r!");
        } else
        u.err(p.equals(u.getPlayer()) ? CmdUtils.err_no_item_hold :
                CmdUtils.err_no_item_hold_on.replace("%PLAYER%", p.getDisplayName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        LinkedHashSet<Player> content = CmdUtils.getPlayersFromArray(args, 0);
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if(args.length == 1)
                addAll(Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList()));
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
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
