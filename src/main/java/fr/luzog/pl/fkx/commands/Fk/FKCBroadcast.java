package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Broadcast;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FKCBroadcast {
    public static final String syntaxe = "/fk bc [? || help] <type> <message>";
    public static final String dsyntaxe = "(normal | succ | err | log | info | announcement" +
            "| event | warn | sys_succ | sys_err)";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?")) {
            u.synt();
            u.err("Types :");
            u.err(" - §6normal§r : §fJust a normal message that everyone can see.");
            u.err("");
            u.err(" - §6succ§r :  §aBase   §cStrong   §fPrefix: " + Main.PREFIX);
            u.err(" - §6err§r :  §cBase   §4Strong   §fPrefix: " + Main.PREFIX);
            u.err(" - §6log§r :  §6Base   §cStrong   §fPrefix: " + Main.PREFIX);
            u.err("");
            u.err(" - §6announcement§r :  §aBase   §cStrong   §fLine: §2-----");
            u.err(" - §6info§r :  §5Base   §dStrong   §fLine: §c-----");
            u.err(" - §6event§r :  §9Base   §1Strong   §fLine: §5-----");
            u.err(" - §6warn§r :  §cBase   §4Strong   §fLine: §6-----");
            u.err("");
            u.err(" - §6sys_succ§r :  §aBase   §cStrong   §fPrefix: " + Main.SYS_PREFIX);
            u.err(" - §6sys_err§r :  §cBase   §4Strong   §fPrefix: " + Main.SYS_PREFIX);
        } else if (args.length < 3)
            u.synt();
        else {
            String content = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).replace("\\ ", " ");
            switch (args[1].toLowerCase()) {
                case "normal":
                    Broadcast.mess(content);
                    break;
                case "succ":
                    Broadcast.succ(content);
                    break;
                case "err":
                    Broadcast.err(content);
                    break;
                case "log":
                    Broadcast.log(content);
                    break;
                case "announcement":
                    Broadcast.announcement(content);
                    break;
                case "info":
                    Broadcast.info(content);
                    break;
                case "event":
                    Broadcast.event(content);
                    break;
                case "warn":
                    Broadcast.warn(content);
                    break;
                case "sys_succ":
                    Broadcast.sys_succ(content);
                    break;
                case "sys_err":
                    Broadcast.sys_err(content);
                    break;
                default:
                    u.err("Type inconnu. (" + args[1] + ")");
                    u.synt();
                    break;
            }
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            if(args.length == 2){
                add("help");
                add("normal");
                add("succ");
                add("err");
                add("log");
                add("announcement");
                add("info");
                add("event");
                add("warn");
                add("sys_succ");
                add("sys_err");
            } else {
                add("!");
                add("!!");
                add("&");
                add("&&");
                addAll(Utils.getAllPlayers());
            }
        }};
    }
}
