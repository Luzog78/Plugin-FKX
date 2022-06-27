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
                        u.succ(" - §6" + fkp.getName() + "§r §7" + fkp.getUuid() + " : §f" + (fkp.getPlayer() != null ? "§2" + SpecialChars.STAR_4_FILLED + " here" : "§4" + SpecialChars.STAR_4_EMPTY + " off")));
        } else if (Bukkit.getOfflinePlayer(args[1]).isOnline())
            if (args.length >= 3)
                if (args[2].equalsIgnoreCase("info")) {
                    Player p = Bukkit.getPlayer(args[1]);
                    DecimalFormat df = new DecimalFormat("0.00");
                    FKPlayer fkp = FKManager.getCurrentGame().getPlayer(p.getUniqueId(), false);
                    u.succ("Joueur :");
                    u.succ(" - Nom : §f" + p.getName());
                    if (fkp != null && fkp.getTeam() != null)
                        u.succ(" - Team : §f" + fkp.getTeam().getName());
                    u.succ(" - Nom d'Affichage : §f" + (fkp == null ? p.getDisplayName() : fkp.getDisplayName()));
                    u.succ(" - Vie : §c" + df.format(p.getHealth()) + "§7 /" + p.getMaxHealth());
                    u.succ(" - Nourriture : §a" + df.format(p.getFoodLevel()) + "§7 /20.0");
                    u.succ(" - Saturation : §e" + df.format(p.getSaturation()) + "§7 /20.0");
                    u.succ(" - Monde : §f" + (p.getWorld().getName().equalsIgnoreCase("world") ? "§2OverWorld"
                            : p.getWorld().getName().equalsIgnoreCase("world_nether") ? "§dNether"
                            : p.getWorld().getName().equalsIgnoreCase("world_the_end") ? "§5End"
                            : p.getWorld().getName()));
                    u.succ(" - Localisation : §f" + df.format(p.getLocation().getBlockX()) + "  "
                            + df.format(p.getLocation().getBlockY()) + "  " + df.format(p.getLocation().getBlockZ()));
                    if (fkp != null)
                        u.succ(" - Zone : §f" + (fkp.getZone() == null ? "Aucune" : fkp.getZone().getId() + "§7 (" + fkp.getZone().getType() + ")"));
                    else
                        u.succ(" - Statut : §cHors jeu.");
                } else
                    u.synt();
            else
                u.succ("TODO -> Player GUIs");

        else
            u.synt();

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
