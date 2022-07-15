package fr.luzog.pl.fkx.commands.Admin;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InvSee implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/invsee <seerPlayer> <targetPlayer> [<arg>]\nArgs :\n > -n / --normal\n > -a / --armor\n > -ec / --ender-chest";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 1)
            if (Bukkit.getPlayerExact(args[0]) != null)
                if (sender instanceof Player) {
                    Player p = Bukkit.getPlayerExact(args[0]);
                    u.getPlayer().openInventory(p.getInventory());
                    u.succ("Vous ouvrez l'§eInventaire§r de§6", p.getDisplayName(), "§r!");
                } else
                    u.err(CmdUtils.err_not_player);
            else
                u.err(CmdUtils.err_player_not_found);

        else if (args.length == 3)
            if (Bukkit.getPlayerExact(args[0]) != null && Bukkit.getPlayerExact(args[1]) != null) {
                Player seer = Bukkit.getPlayerExact(args[0]), target = Bukkit.getPlayerExact(args[1]);
                if (args[2].equalsIgnoreCase("--normal") || args[2].equalsIgnoreCase("-n")) {
                    seer.openInventory(target.getInventory());
                    u.succ("§6" + seer.getDisplayName() + "§r ouvre l'§eInventaire§r de§6", target.getDisplayName(), "§r!");
                } else if (args[2].equalsIgnoreCase("--armor") || args[2].equalsIgnoreCase("-a")) {
                    u.err("Non disponible pour l'instant.");
                    u.succ("§6" + seer.getDisplayName() + "§r ouvre l'§7Armure§r de§6", target.getDisplayName(), "§r!");
                } else if (args[2].equalsIgnoreCase("--ender-chest") || args[2].equalsIgnoreCase("-ec")) {
                    seer.openInventory(target.getEnderChest());
                    u.succ("§6" + seer.getDisplayName() + "§r ouvre l'§dEnderChest§r de§6", target.getDisplayName(), "§r!");
                } else
                    u.err(CmdUtils.err_arg.replace("%ARG%", args[2]));
            } else
                u.err(CmdUtils.err_player_not_found);

        else if (args.length == 2)
            if (Bukkit.getPlayerExact(args[0]) != null) {
                Player p = Bukkit.getPlayerExact(args[0]);
                if (Bukkit.getPlayerExact(args[1]) != null) {
                    Player pp = Bukkit.getPlayerExact(args[1]);
                    p.openInventory(pp.getInventory());
                    u.succ("§6" + p.getDisplayName() + "§r ouvre l'§eInventaire§r de§6", pp.getDisplayName(), "§r!");
                } else if (sender instanceof Player)
                    if (args[1].equalsIgnoreCase("--normal") || args[1].equalsIgnoreCase("-n")) {
                        u.getPlayer().openInventory(p.getInventory());
                        u.succ("Vous ouvrez l'§eInventaire§r de§6", p.getDisplayName(), "§r!");
                    } else if (args[1].equalsIgnoreCase("--armor") || args[1].equalsIgnoreCase("-a")) {
                        u.err("Non disponible pour l'instant.");
                        u.succ("Vous ouvrez l'§7Armure§r de§6", p.getDisplayName(), "§r!");
                    } else if (args[1].equalsIgnoreCase("--ender-chest") || args[1].equalsIgnoreCase("-ec")) {
                        u.getPlayer().openInventory(p.getEnderChest());
                        u.succ("Vous ouvrez l'§dEnderChest§r de§6", p.getDisplayName(), "§r!");
                    } else
                        u.err(CmdUtils.err_arg.replace("%ARG%", args[1]), "OU", CmdUtils.err_not_player);
                else
                    u.err(CmdUtils.err_arg.replace("%ARG%", args[1]), "OU", CmdUtils.err_not_player);
            } else
                u.err(CmdUtils.err_player_not_found);

        else
            u.synt();

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        List<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if (args.length == 1 || args.length == 2)
                addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));

            if (args.length == 2 || args.length == 3)
                addAll(Arrays.asList("--normal", "-n", "--armor", "-a", "--ender-chest", "-ec"));
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
