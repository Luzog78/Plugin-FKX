package fr.luzog.pl.fkx.commands.Admin;

import fr.luzog.pl.fkx.Main;
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

import java.util.*;

public class Vanish implements CommandExecutor, TabCompleter, Listener {
    public static final String syntaxe = "/vanish [(on | off)] [<players...>]";
    public static String pre_suf_ix;
    public static boolean isPrefix;

    public static HashSet<String> vanished = new HashSet<>();

    public static void initFromConfig() {
        Main.globalConfig.setVanish("§7[VANISH§7] §r", true, new ArrayList<>(), false);
        pre_suf_ix = Main.globalConfig.getVanishPreSufIx();
        isPrefix = Main.globalConfig.getVanishIsPrefix();
        vanished = Main.globalConfig.getVanishPlayers();
    }

    public static void saveToConfig() {
        Main.globalConfig.setVanish(pre_suf_ix, isPrefix, vanished, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        // 0 -> Switch ; 1 -> On ; -1 -> Off
        int mode = args.length >= 1 ? (args[0].equalsIgnoreCase("on") ? 1 :
                args[0].equalsIgnoreCase("off") ? -1 : 0) : 0;

        if (args.length == 0 || (args.length == 1 && mode != 0))
            if (sender instanceof Player) {
                boolean vanish = mode == 0 ? !isVanished(u.getPlayer().getName()) : (mode == 1);
                vanish(u.getPlayer().getName(), vanish);
                u.succ("Vous êtes désormais§e", (vanish ? "Invisible" : "Réapparu"), "§r!");
            } else
                u.err(CmdUtils.err_not_player);

        else if (args[0].equalsIgnoreCase("?"))
            u.synt();

        else
            CmdUtils.getPlayersFromArray(args, mode == 0 ? 0 : 1).forEach(player -> {
                boolean vanish = mode == 1 || (mode == 0 && !isVanished(player.getName()));
                vanish(player.getName(), vanish);
                u.succ("§6" + player.getDisplayName() + "§r est désormais§e",
                        (vanish ? "Invisible" : "Réapparu"), "§r !");
            });

        return false;
    }

    @EventHandler
    public static void onJoin(PlayerJoinEvent e) {
        refreshVanished();
    }

    public static boolean isVanished(String name) {
        for (String s : vanished)
            if (s.equalsIgnoreCase(name))
                return true;
        return false;
    }

    public static void vanish(String name, boolean vanish) {
        if (vanish && !isVanished(name))
            vanished.add(name);
        else if (!vanish)
            vanished.removeIf(v -> v.equalsIgnoreCase(name));
        refreshVanished();
    }

    public static void refreshVanished() {
        Bukkit.getOnlinePlayers().forEach(seer ->
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (isVanished(seer.getName()) || !isVanished(target.getName()))
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
