package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.GUIs.GuiFK;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FKCGame {
    public static final String syntaxe = "/fk game [help | list | current | (new | switch | delete) <id> | state | start | end | reboot | (pause | resume) [<cooldown>]]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        if (args.length == 1)
            if (sender instanceof Player)
                u.getPlayer().performCommand("fk game gui " + FKManager.currentGameId);
            else
                u.synt();

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list"))
            u.succ("Parties (" + FKManager.registered.size() + ") : §f"
                    + Strings.join(FKManager.registered.stream().map(FKManager::getId).collect(Collectors.toList()), "§r, §f"));

        else if (args[1].equalsIgnoreCase("current"))
            u.succ("Partie actuelle : §f" + FKManager.getCurrentGame().getId());

        else if (args[1].equalsIgnoreCase("new"))
            if (args.length >= 3)
                if (!args[2].equalsIgnoreCase("null")) {
                    if (FKManager.getCurrentGame() != null)
                        FKManager.getCurrentGame().getListener().cancelTask();
                    String old = FKManager.currentGameId;
                    FKManager m = new FKManager(args[2]);
                    m.register(true);
                    m.getListener().scheduleTask();
                    FKManager.setCurrentGame(m.getId(), true);
                    u.succ("Partie actuelle : §f" + args[2] + " §7§o(ancienne : " + (old == null ? "§cnull" : old) + "§r)");
                } else
                    u.err("ID invalide");
            else
                u.synt();

        else if (args[1].equalsIgnoreCase("switch"))
            if (args.length >= 3)
                if (args[2].equalsIgnoreCase("null") || FKManager.getGame(args[2]) != null) {
                    if (FKManager.getCurrentGame() != null)
                        FKManager.getCurrentGame().getListener().cancelTask();
                    String old = FKManager.currentGameId;
                    FKManager m = FKManager.setCurrentGame(args[2].equalsIgnoreCase("null") ? null : args[2], true);
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
                if (FKManager.getGame(args[2]) != null) {
                    Objects.requireNonNull(FKManager.getGame(args[2])).unregister(true);
                    u.succ("Partie : §f" + args[2] + "§r supprimée.\n§r"
                            + "Partie actuelle : §f" + (FKManager.currentGameId == null ? "§caucune" : FKManager.currentGameId));
                } else
                    u.err("Aucune partie trouvée.");
            else
                u.synt();

        else if (args[1].equalsIgnoreCase("state"))
            if (sender instanceof Player)
                u.getPlayer().openInventory(GuiFK.getStateInventory(u.getPlayer(), "fk"));
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("start"))
            FKManager.getCurrentGame().start();

        else if (args[1].equalsIgnoreCase("end"))
            FKManager.getCurrentGame().end();

        else if (args[1].equalsIgnoreCase("reboot"))
            FKManager.getCurrentGame().reboot();

        else if (args[1].equalsIgnoreCase("pause"))
            if (args.length >= 3)
                try {
                    FKManager.getCurrentGame().pause(Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format);
                }
            else
                FKManager.getCurrentGame().pause(0);

        else if (args[1].equalsIgnoreCase("resume"))
            if (args.length >= 3)
                try {
                    FKManager.getCurrentGame().resume(Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format);
                }
            else
                FKManager.getCurrentGame().resume(0);

        else
            u.synt();

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return args.length == 2 ? Arrays.asList("help", "current", "list", "new", "switch", "start", "end", "pause", "resume") : new ArrayList<>();
    }
}
