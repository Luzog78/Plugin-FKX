package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GCHelp {
    public static final String syntaxe = "/" + Main.CMD + " (help || ?)";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        u.send("", Main.HEADER.replace("--", "-"));
        u.send("", "§a > §6/" + Main.CMD + " help §7- §eAffiche l'aide");
        u.send("", "§a > §6/" + Main.CMD + " activations §7- §eModifie les options du G");
        u.send("", "§a > §6/" + Main.CMD + " banner §7- §eDonne les bannières du Jeu");
        u.send("", "§a > §6/" + Main.CMD + " (bc || broadcast) §7- §eEnvoie un message à tous les joueurs");
        u.send("", "§a > §6/" + Main.CMD + " compass §7- §eModifie la direction suivie par le joueur");
        u.send("", "§a > §6/" + Main.CMD + " date §7- §eGère la date, le temps, et la météo");
        u.send("", "§a > §6/" + Main.CMD + " game §7- §eManage les différentes parties");
        u.send("", "§a > §6/" + Main.CMD + " locks §7- §eCréé des coffres crochetables");
        u.send("", "§a > §6/" + Main.CMD + " (perm || permissions) §7- §eChange les permissions");
        u.send("", "§a > §6/" + Main.CMD + " players §7- §eAffiche les informations des joueurs");
        u.send("", "§a > §6/" + Main.CMD + " portal §7- §eGère les portails");
        u.send("", "§a > §6/" + Main.CMD + " stats §7- §eModifie les stats des joueurs");
        u.send("", "§a > §6/" + Main.CMD + " teams §7- §eGère les équipes (complet)");
        u.send("", "§a > §6/" + Main.CMD + " title §7- §c ###### Envoie un titre aux joueurs");
        u.send("", "§a > §6/" + Main.CMD + " warp §7- §eTéléporte à des endroits clés");
        u.send("", Main.FOOTER.replace("--", "-"));

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<>();
    }
}
