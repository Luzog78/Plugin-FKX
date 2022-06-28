package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FKCPlayers {
    public static final String syntaxe = "/fk players [help | list | <player> [info]]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        if (args.length == 1)
            if (sender instanceof Player)
                u.succ("TODO -> Players GUIs");
            else
                u.synt();

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Joueurs FK (§f" + (FKManager.getCurrentGame() == null ? "null" : FKManager.getCurrentGame().getPlayers().size()) + "§r) :");
            if (FKManager.getCurrentGame() == null)
                u.err(" - Aucun jeu en cours");
            else
                FKManager.getCurrentGame().getPlayers().forEach(fkp ->
                        u.succ(" - §6" + fkp.getName() + "§r §7" + fkp.getLastUuid() + " : §f" + (fkp.getPlayer() != null ? "§2" + SpecialChars.STAR_4_FILLED + " here" : "§4" + SpecialChars.STAR_4_EMPTY + " off")));
        } else {
            FKPlayer fkp = FKManager.getCurrentGame().getPlayer(args[1], false);
            Player p = fkp == null || fkp.getPlayer() == null ? Bukkit.getPlayerExact(args[1]) : fkp.getPlayer();
            if (args.length >= 3)
                if (args[2].equalsIgnoreCase("info")) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    u.succ("Joueur :");
                    u.succ(" - Nom : §f" + (p == null ? fkp == null ? "§cnull" : fkp.getName() : p.getName()));
                    u.succ(" - UUID : §7" + (p == null ? fkp == null ? "§cnull" : fkp.getLastUuid() : p.getUniqueId()));
                    u.succ(" - Team : §f" + (fkp == null ? "§cHors Jeu" : fkp.getTeam() == null ? "§4§lAucune" : fkp.getTeam().getName()));
                    u.succ(" - Nom d'Affichage : §f" + (fkp == null ? p == null ? "§cnull" : p.getDisplayName() : fkp.getDisplayName()));
                    u.succ(" - Vie : §c" + (p == null ? "0.0§7 /0.0" : df.format(p.getHealth()) + "§7 /" + p.getMaxHealth()));
                    u.succ(" - Nourriture : §a" + (p == null ? "0.0§7 /0.0" : df.format(p.getFoodLevel()) + "§7 /20.0"));
                    u.succ(" - Saturation : §e" + (p == null ? "0.0§7 /0.0" : df.format(p.getSaturation()) + "§7 /20.0"));
                    u.succ(" - Localisation : §f" + (p == null ? "§cAucune" : Utils.locToString(p.getLocation(), true, true, false)));
                    u.succ(" - Monde : §f" + (p == null ? "§cAucun" : (p.getWorld().getName().equalsIgnoreCase("world") ? "§2OverWorld"
                            : p.getWorld().getName().equalsIgnoreCase("world_nether") ? "§dNether"
                            : p.getWorld().getName().equalsIgnoreCase("world_the_end") ? "§5End"
                            : p.getWorld().getName())));
                    u.succ(" - Zone : §f" + (fkp == null ? "§cHors Jeu" : fkp.getZone() == null ? "§cAucune" : fkp.getZone().getId() + "§7 (" + fkp.getZone().getType() + ")"));
                } else
                    u.synt();
            else
                u.succ("TODO -> Player GUIs");

        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return args.length == 2 ? new ArrayList<String>() {{
            add("help");
            add("list");
            addAll(Utils.getAllPlayers());
        }} : args.length == 3 ? new ArrayList<String>() {{
            if (Bukkit.getOfflinePlayer(args[1]).isOnline())
                add("info");
        }} : new ArrayList<>();
    }
}
