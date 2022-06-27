package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class FKCHelp {
    public static final String syntaxe = "/fk (help || ?)";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        u.send("", Main.HEADER.replace("--", "-"));
        u.send("", "§a > §6/fk help §7- §eAffiche l'aide");
        u.send("", "§a > §6/fk activations §7- §eModifie les options du FK");
        u.send("", "§a > §6/fk authorisations §7- §eChange les autorisations");
        u.send("", "§a > §6/fk (bc || broadcast) §7- §eEnvoie un message à tous les joueurs");
        u.send("", "§a > §6/fk date §7- §eGère la date, le temps, et la météo");
        u.send("", "§a > §6/fk event §7- §e ######");
        u.send("", "§a > §6/fk game §7- §eManage les différentes parties");
        u.send("", "§a > §6/fk players §7- §eAffiche les informations des joueurs");
        u.send("", "§a > §6/fk stats §7- §eModifie les stats des joueurs");
        u.send("", "§a > §6/fk teams §7- §eGère les équipes (complet)");
        u.send("", "§a > §6/fk title §7- §e ###### Envoie un titre aux joueurs");
        u.send("", "§a > §6/fk warp §7- §eTéléporte à des endroits clés");
        u.send("", Main.FOOTER.replace("--", "-"));

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<>();
    }
}
