package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.guis.GuiGlobal;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GCGame {
    public static final String syntaxe = "/" + Main.CMD + " game [help | list | current | (new | switch | delete) <id> | state"
            + " | start | end | reboot | (pause | resume) [<cooldown>]]";
    public static final String err_no_game_running = "Aucune partie n'est en cours.";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        if (args.length == 1)
            if (sender instanceof Player)
                u.getPlayer().performCommand(Main.CMD + " game gui " + GManager.currentGameId);
            else
                u.synt();

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list"))
            u.succ("Parties (" + GManager.registered.size() + ") : §f"
                    + Strings.join(GManager.registered.stream().map(GManager::getId).collect(Collectors.toList()), "§r, §f"));

        else if (args[1].equalsIgnoreCase("current"))
            u.succ("Partie actuelle : §f" + (GManager.getCurrentGame() == null ? "§cnull" : GManager.getCurrentGame().getId()));

        else if (args[1].equalsIgnoreCase("new"))
            if (args.length >= 3)
                if (!args[2].equalsIgnoreCase("null")) {
                    if (GManager.getCurrentGame() != null)
                        GManager.getCurrentGame().getListener().cancelTask();
                    String old = GManager.currentGameId;
                    GManager m = new GManager(args[2]);
                    m.register(true);
                    m.getListener().scheduleTask();
                    GManager.setCurrentGame(m.getId(), true);
                    u.succ("Partie actuelle : §f" + args[2] + " §7§o(ancienne : " + (old == null ? "§cnull" : old) + "§r)");
                } else
                    u.err("ID invalide");
            else
                u.synt();

        else if (args[1].equalsIgnoreCase("switch"))
            if (args.length >= 3)
                if (args[2].equalsIgnoreCase("null") || GManager.getGame(args[2]) != null) {
                    if (GManager.getCurrentGame() != null)
                        GManager.getCurrentGame().getListener().cancelTask();
                    String old = GManager.currentGameId;
                    GManager m = GManager.setCurrentGame(args[2].equalsIgnoreCase("null") ? null : args[2], true);
                    if (m != null)
                        m.getListener().scheduleTask();
                    u.succ("Partie actuelle : §f" + (args[2].equalsIgnoreCase("null") ? "§cnull" : args[2])
                            + "§r §7§o(ancienne : " + (old == null ? "§cnull" : old) + "§r)");
                } else
                    u.err("Aucune partie trouvée.");
            else
                u.synt();

        else if (args[1].equalsIgnoreCase("delete"))
            if (args.length >= 3)
                if (GManager.getGame(args[2]) != null) {
                    Objects.requireNonNull(GManager.getGame(args[2])).unregister(true);
                    u.succ("Partie : §f" + args[2] + "§r supprimée.\n§r"
                            + "Partie actuelle : §f" + (GManager.currentGameId == null ? "§caucune" : GManager.currentGameId));
                } else
                    u.err("Aucune partie trouvée.");
            else
                u.synt();

        else if (args[1].equalsIgnoreCase("state"))
            if (sender instanceof Player)
                u.getPlayer().openInventory(GuiGlobal.getStateInventory(Main.CMD));
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("start"))
            if (GManager.getCurrentGame() != null)
                GManager.getCurrentGame().start();
            else
                u.err(err_no_game_running);

        else if (args[1].equalsIgnoreCase("end"))
            if (GManager.getCurrentGame() != null)
                GManager.getCurrentGame().end();
            else
                u.err(err_no_game_running);

        else if (args[1].equalsIgnoreCase("reboot"))
            if (GManager.getCurrentGame() != null)
                GManager.getCurrentGame().reboot();
            else
                u.err(err_no_game_running);

        else if (args[1].equalsIgnoreCase("pause"))
            if (GManager.getCurrentGame() != null)
                if (args.length >= 3)
                    try {
                        GManager.getCurrentGame().pause(Integer.parseInt(args[2]));
                    } catch (NumberFormatException e) {
                        u.err(CmdUtils.err_number_format);
                    }
                else
                    GManager.getCurrentGame().pause(0);
            else
                u.err(err_no_game_running);

        else if (args[1].equalsIgnoreCase("resume"))
            if (GManager.getCurrentGame() != null)
                if (args.length >= 3)
                    try {
                        GManager.getCurrentGame().resume(Integer.parseInt(args[2]));
                    } catch (NumberFormatException e) {
                        u.err(CmdUtils.err_number_format);
                    }
                else
                    GManager.getCurrentGame().resume(0);
            else
                u.err(err_no_game_running);

        else
            u.synt();

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return args.length == 2 ? Arrays.asList("help", "current", "list", "new", "switch", "delete",
                "state", "start", "end", "pause", "resume") : new ArrayList<>();
    }
}
