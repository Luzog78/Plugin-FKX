package fr.luzog.pl.fkx.commands.Chat;

import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPermissions;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Msg implements CommandExecutor, TabCompleter {
    public static HashMap<String, Utils.Pair<String, Long>> replies = new HashMap<>();
    public static final String syntaxe = "/msg <player> <message...>";
    public static final String CONSOLE = "CONSOLE";
    public static final long cooldown = 400; // ticks

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length <= 1 || args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();

        else {
            GPlayer gp = null;
            boolean isConsole;
            if (!(sender instanceof Player)
                    || GManager.getCurrentGame() == null
                    || (gp = GManager.getCurrentGame().getPlayer(sender.getName(), false)) == null
                    || gp.hasPermission(GPermissions.Type.CHAT_PRIVATE, gp.getPlayer().getLocation())) {
                if (!(isConsole = args[0].equals(CONSOLE)) && Bukkit.getPlayer(args[0]) == null)
                    u.err("Le joueur §6" + args[0] + "§r n'est pas connecté !");
                else {
                    if (gp != null)
                        gp.getStats().increaseChats();

                    String target = isConsole ? CONSOLE : Bukkit.getPlayer(args[0]).getName();
                    if (replies.containsKey(sender.getName())) {
                        replies.replace(sender.getName(), new Utils.Pair<>(target, cooldown));
                    } else {
                        replies.put(sender.getName(), new Utils.Pair<>(target, cooldown));
                    }
                    if (replies.containsKey(target)) {
                        replies.replace(target, new Utils.Pair<>(sender.getName(), cooldown));
                    } else {
                        replies.put(target, new Utils.Pair<>(sender.getName(), cooldown));
                    }

                    StringBuilder message = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        String arg = args[i];
                        if (GManager.getCurrentGame() != null && GManager.getCurrentGame().getPlayer(arg, false) != null)
                            arg = "§e§l" + GManager.getCurrentGame().getPlayer(arg, false).getDisplayName() + "§r";
                        else if (Bukkit.getPlayer(arg) != null)
                            arg = "§e§l" + Bukkit.getPlayer(arg).getName() + "§r";
                        else if (arg.startsWith("@"))
                            arg = "§e§l" + arg + "§r";
                        message.append(arg).append(" ");
                    }
                    String finalMessage = message.toString().trim().replace("&", "§").replace("§r", "§7§o");

                    sender.sendMessage("§6§o[§bmoi §5⤇ §d" + target + "§6§o]  §7§o" + finalMessage);
                    (isConsole ? Bukkit.getConsoleSender() : Bukkit.getPlayer(args[0]))
                            .sendMessage("§6§o[§b" + sender.getName() + "§r §5⤇ §dmoi§6§o]  §7§o" + finalMessage);
                }
            } else
                u.err("Vous n'avez pas la permission de parler en §6privé§r.");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(this::add);
            add(CONSOLE);
            Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(p -> this.add("@" + p));
            add("@" + CONSOLE);
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}