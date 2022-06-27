package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;

public class CmdUtils {

    public static final String err_unknown = "Un problème est survenu.",
            err_syntaxe = "Syntaxe :",
            err_not_player = "Vous n'êtes pas un joueur.",
            err_player_not_found = "Joueur introuvable.",
            err_number_format = "Nombre attendu incorrect.",
            err_no_item_hold = "Aucun item équipé.",
            err_no_item_hold_on = "Aucun item équipé sur %PLAYER%§r.",
            err_too_items_hold = "Trop d'items (en main ET sur la tête).\nEssayez de vider un slot.",
            err_too_items_hold_on = "Trop d'items équipés sur %PLAYER%§r.",
            err_arg = "Argument '%ARG%' inconnu.",
            err_missing_arg = "Argument '%ARG%' introuvable.",
            err_unknown_enchant = "Enchantement inconnu.",
            err_inventory_full = "Inventaire plein.",
            err_not_enough_args = "Pas assez d'arguments, vérifiez la syntaxe",
            err_no_gui_for_this_instance = "Pas de GUI pour cette instance.\nTapez /%CMD% help pour plus d'informations.",
            err_team_not_found = "Aucune équipe trouvée.",
            err_player_has_no_team = "Le joueur n'est dans aucune équipe.",
            err_player_not_in_the_team = "Le joueur n'est pas dans l'équipe.",
            err_player_already_in_team = "Le joueur est déjà dans une équipe.",
            err_player_does_not_exist = "Le joueur n'existe pas.";

    private CommandSender sender;
    private Command cmd;
    private String msg;
    private String[] args;
    private String syntaxe;

    public CmdUtils(CommandSender sender, Command cmd, String msg, String[] args, String syntaxe) {
        this.sender = sender;
        this.cmd = cmd;
        this.msg = msg;
        this.args = args;
        this.syntaxe = syntaxe;
    }

    public static LinkedHashSet<Player> getPlayersFromArray(String[] args, int sub) {
        LinkedHashSet<Player> pls = new LinkedHashSet<>();
        for (int i = sub; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("@a"))
                pls.addAll(Bukkit.getOnlinePlayers());
            else if (args[i].equalsIgnoreCase("!@a"))
                pls.clear();
            else {
                boolean rem = args[i].startsWith("!");
                String name = args[i].replaceFirst(rem ? "!" : "/", "");
                for (Player player : Bukkit.getOnlinePlayers())
                    if (player.getName().equalsIgnoreCase(name)) {
                        if (rem)
                            try {
                                pls.remove(player);
                            } catch (Exception ignored) {
                            }
                        else
                            pls.add(player);
                        break;
                    }
            }
        }
        return pls;
    }

    public static LinkedHashSet<Entity> getEntitiesFromArray(String[] args, int sub) {
        LinkedHashSet<Entity> entities = new LinkedHashSet<>();
        for (int i = sub; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("@e"))
                entities.addAll(Main.world.getEntities());
            else if (args[i].equalsIgnoreCase("!@e"))
                entities.clear();
            else if (args[i].equalsIgnoreCase("@a"))
                entities.addAll(Bukkit.getOnlinePlayers());
            else if (args[i].equalsIgnoreCase("!@a"))
                Bukkit.getOnlinePlayers().forEach(entities::remove);
            else {
                boolean rem = args[i].startsWith("!");
                String name = args[i].replaceFirst(rem ? "!" : "/", "");
                boolean pursue = true;
                for (Player player : Bukkit.getOnlinePlayers())
                    if (player.getName().equalsIgnoreCase(name)) {
                        if (rem)
                            try {
                                entities.remove(player);
                            } catch (Exception ignored) {
                            }
                        else
                            entities.add(player);
                        pursue = false;
                        break;
                    }
                if (!pursue)
                    break;
                for (Entity e : Main.world.getEntities())
                    if (e.getUniqueId().toString().equalsIgnoreCase(name)) {
                        if (rem)
                            try {
                                entities.remove(e);
                            } catch (Exception ignored) {
                            }
                        else
                            entities.add(e);
                        break;
                    }
            }
        }
        return entities;
    }

    public void synt() {
        err(err_syntaxe, syntaxe);
    }

    public void send(String color, Object... o) {
        if (o.length == 0)
            return;
        String s = color;
        for (Object ob : o)
            s += ob + " ";
        sender.sendMessage(s.substring(0, s.length() - 1).replace("§r", "§r" + (color == null ? "" : color)));
    }

    public void err(Object... o) {
        send("§c", o);
    }

    public void succ(Object... o) {
        send("§a", o);
    }

    public void warn(Object... o) {
        send("§6", o);
    }

    public Player getPlayer() {
        return sender instanceof Player ? (Player) sender : null;
    }

    public CommandSender getSender() {
        return sender;
    }

    public void setSender(CommandSender sender) {
        this.sender = sender;
    }

    public Command getCmd() {
        return cmd;
    }

    public void setCmd(Command cmd) {
        this.cmd = cmd;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getSyntaxe() {
        return syntaxe;
    }

    public void setSyntaxe(String syntaxe) {
        this.syntaxe = syntaxe;
    }
}
