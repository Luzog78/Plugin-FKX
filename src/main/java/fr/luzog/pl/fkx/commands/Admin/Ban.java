package fr.luzog.pl.fkx.commands.Admin;

import fr.luzog.pl.fkx.events.PlayerJoinQuitHandler;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPermissions;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ban implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/ban <player> [<reason>]";

    public static String getMessage(String sender, String reason) {
        return " "
                + "§r\n§6       ----------------  §9--==|[  §c§l§nFallen Kingdom§9  ]|==--  §6----------------"
                + "§r\n "
                + "§r\n "
                + "§r\n§4§lVous avez été  §6§lbanni§4§l  du serveur !"
                + "§r\n "
                + "§r\n§c§lpar §b" + sender + "§c§l."
                + "§r\n "
                + "§r\n§8---"
                + "§r\n "
                + "§r\n§c§l§nRaison :"
                + "§r\n "
                + "§r\n§7" + reason.replace("§r", "§7")
                + "§r\n "
                + "§r\n "
                + "§r\n§6-------------------------------------------------------------";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (sender instanceof Player && !sender.isOp()) {
            if (GManager.getCurrentGame() == null
                    || GManager.getCurrentGame().getPlayer(sender.getName(), false) == null
                    || !GManager.getCurrentGame().getPlayer(sender.getName(), false)
                    .hasPermission(GPermissions.Type.BAN, u.getPlayer().getLocation())) {
                u.err("Vous n'avez pas la permission de faire cela !");
                return false;
            }
        }

        if (args.length == 0 || args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();

        else {
            String reason = "Aucune raison spécifiée.";
            if (args.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    String arg = args[i];
                    if (GManager.getCurrentGame() != null && GManager.getCurrentGame().getPlayer(arg, false) != null)
                        arg = "§e§l" + GManager.getCurrentGame().getPlayer(arg, false).getDisplayName() + "§r";
                    else if (Bukkit.getPlayer(arg) != null)
                        arg = "§e§l" + Bukkit.getPlayer(arg).getName() + "§r";
                    else if (arg.startsWith("@"))
                        arg = "§e§l" + arg + "§r";
                    sb.append(arg).append(i % 9 == 0 ? "\n" : " ");
                }
                reason = sb.toString().trim().replace("&", "§");
            }


            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                Utils.Pair<String, UUID> profile = Utils.getNameAndUUIDFromMojangAPI(args[0]);
                String target;
                if (profile == null) {
                    sender.sendMessage("§6" + SpecialChars.WARNING + " Ce joueur n'existe pas (via MojangAPI) !");
                    target = args[0];
                } else
                    target = profile.getKey();
                Bukkit.getBanList(BanList.Type.NAME).addBan(target, reason, null, sender.getName());
                u.succ("Le joueur §6" + target + "§r a bien été §4§lbanni§r du serveur !");
            } else {
                PlayerJoinQuitHandler.silent.add(p.getName());
                p.kickPlayer(getMessage(GManager.getCurrentGame() != null
                        && GManager.getCurrentGame().getPlayer(sender.getName(), false) != null ?
                        GManager.getCurrentGame().getPlayer(sender.getName(), false).getDisplayName() : sender.getName(), reason));
                Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), reason, null, sender.getName());
                Bukkit.broadcastMessage(PlayerJoinQuitHandler.ban + "§c" + p.getName());
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(this::add);
            Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(p -> this.add("@" + p));
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}