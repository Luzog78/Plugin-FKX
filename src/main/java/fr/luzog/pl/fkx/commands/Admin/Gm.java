package fr.luzog.pl.fkx.commands.Admin;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Gm implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/gm[0|1|2|3] [<gmode>] [<players...>]";

    public GameMode type;

    public Gm() {
        this.type = null;
    }

    public Gm(GameMode type) {
        this.type = type;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (type != null)

            if (args.length == 0)
                if (sender instanceof Player)
                    change(u, u.getPlayer(), type);
                else
                    u.err(CmdUtils.err_not_player);
            else
                CmdUtils.getPlayersFromArray(args, 0).forEach(p -> change(u, p, type));

        else if (args.length == 0)

            if (sender instanceof Player)
                if (u.getPlayer().getGameMode() == GameMode.CREATIVE || u.getPlayer().getGameMode() == GameMode.SPECTATOR)
                    change(u, u.getPlayer(), GameMode.SURVIVAL);
                else
                    change(u, u.getPlayer(), GameMode.CREATIVE);
            else
                u.err(CmdUtils.err_not_player);

        else if (args[0].equals("?") || args[0].equalsIgnoreCase("help"))

            u.synt();

        else {

            int sub = get(args[0]) == null ? 0 : 1;
            GameMode gm = sub == 1 ? get(args[0]) : null;
            CmdUtils.getPlayersFromArray(args, sub).forEach(p -> change(u, p, gm != null ? gm : p.getGameMode() == GameMode.CREATIVE ||
                    p.getGameMode() == GameMode.SPECTATOR ? GameMode.SURVIVAL : GameMode.CREATIVE));

        }

        return false;
    }

    public static GameMode get(String s) {
        return s.equals("0") || s.equalsIgnoreCase("survival") ? GameMode.SURVIVAL :
                s.equals("1") || s.equalsIgnoreCase("creative") ? GameMode.CREATIVE :
                        s.equals("2") || s.equalsIgnoreCase("adventure") ? GameMode.ADVENTURE :
                                s.equals("3") || s.equalsIgnoreCase("spectator") ? GameMode.SPECTATOR :
                                        null;
    }

    public static void change(CmdUtils u, Player p, GameMode gm) {
        p.setGameMode(gm);
        u.succ((u.getSender() instanceof Player && p.getName().equals(u.getPlayer().getName()) ? "Vous êtes" :
                p.getDisplayName() + " est") + " passé en mode §6" + (gm == GameMode.CREATIVE ? "Créatif" :
                gm == GameMode.SPECTATOR ? "Spéctateur" :
                        gm == GameMode.ADVENTURE ? "Aventure" : "Survie") + "§r !");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        LinkedHashSet<Player> content = CmdUtils.getPlayersFromArray(args, get(args[0]) == null ? 0 : 1);
        List<String> list = new ArrayList<>();

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
                add("survival");
                add("creative");
                add("adventure");
                add("spectator");
                add("0");
                add("1");
                add("2");
                add("3");
            }
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }

}