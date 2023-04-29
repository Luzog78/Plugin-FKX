package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GCSeason {
    public static final String syntaxe = "/" + Main.CMD + " season [<newSeason...>]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        if (args.length == 1)
            u.send("§f", "§aSaison actuelle : §r" + Main.SEASON);

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else {
            String newSeason = Strings.join(Arrays.copyOfRange(args, 1, args.length), " ").replace("&", "§");
            if (newSeason.length() > 20) {
                u.err("Le nom de la saison ne doit pas dépasser 20 caractères.");
                return true;
            }
            Main.SEASON = newSeason;
            Main.globalConfig.load().setSeason(newSeason, true).save();
            u.succ("La saison a été modifiée avec succès.");
            u.send("§f", "§aSaison actuelle : §r" + newSeason);
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return args.length == 2 ? Arrays.asList("Fallen", "FALLEN", "FK", "FKX", "Fallen-Kingdom", "FALLEN-KINGDOM", "FallenKingdom",
                "FALLENKINGDOM", "Fallen-Kingdom-X", "FALLEN-KINGDOM-X", "FallenKingdomX", "FALLENKINGDOMX")
                : args.length == 3 ? Arrays.asList("Kingdom", "KINGDOM", "Noël", "NOËL", "Halloween", "HALLOWEEN", "Pâques", "PÂQUES")
                : args.length == 4 ? Arrays.asList("X", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
                "XI", "XII", "XII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX",
                "XXI", "XXII", "XXIII", "XXIII", "XXIV", "XXV", "XXVI", "XXVII", "XXVIII", "XXIX", "XXX",
                "XXXI", "XXXII", "XXXII", "XXXIV", "XXXV", "XXXVI", "XXXVII", "XXXVIII", "XXXIX", "XL",
                "XLI", "XLII", "XLII", "XLIV", "XLV", "XLVI", "XLVII", "XLVIII", "XLIX", "L")
                : new ArrayList<>();
    }
}
