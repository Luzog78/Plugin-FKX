package fr.luzog.pl.fkx.commands.Admin;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class Vanish implements CommandExecutor, TabCompleter, Listener {
    public static final String syntaxe = "/vanish [(on | off)] [<players...>]";
    public static final String pre_suf_ix = "§7[VANISH§7] §r";
    public static final boolean isPrefix = true;

    public static List<UUID> vanished = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        // 0 -> Switch ; 1 -> On ; -1 -> Off
        int mode = args.length >= 1 ? (args[0].equalsIgnoreCase("on") ? 1 :
                args[0].equalsIgnoreCase("off") ? -1 : 0) : 0;

        if (args.length == 0 || (args.length == 1 && mode != 0))
            if (sender instanceof Player) {
                boolean vanish = mode == 0 ? !vanished.contains(u.getPlayer().getUniqueId()) : (mode == 1);
                vanish(u.getPlayer().getUniqueId(), vanish);
                u.succ("Vous êtes désormais§e", (vanish ? "Invisible" : "Réapparu"), "§r!");
            } else
                u.err(CmdUtils.err_not_player);

        else if (args[0].equalsIgnoreCase("?"))
            u.synt();

        else
            CmdUtils.getPlayersFromArray(args, mode == 0 ? 0 : 1).forEach(player -> {
                boolean vanish = mode == 1 || (mode == 0 && !vanished.contains(player.getUniqueId()));
                vanish(player.getUniqueId(), vanish);
                u.succ("§6" + player.getDisplayName() + "§r est désormais§e",
                        (vanish ? "Invisible" : "Réapparu"), "§r !");
            });

        return false;
    }

    @EventHandler
    public static void onJoin(PlayerJoinEvent e) {
        refreshVanished();
    }

    public static void vanish(UUID uuid, boolean vanish) {
        if (vanish && !vanished.contains(uuid))
            vanished.add(uuid);
        else if (!vanish)
            vanished.remove(uuid);
        refreshVanished();
    }

    public static void refreshVanished() {
        Bukkit.getOnlinePlayers().forEach(seer ->
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (vanished.contains(seer.getUniqueId()) || !vanished.contains(target.getUniqueId()))
                    seer.showPlayer(target);
                else
                    seer.hidePlayer(target);
            }));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        LinkedHashSet<Player> content = CmdUtils.getPlayersFromArray(args, 0);
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
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

            if (args.length == 1) {
                add("on");
                add("off");
            }
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
