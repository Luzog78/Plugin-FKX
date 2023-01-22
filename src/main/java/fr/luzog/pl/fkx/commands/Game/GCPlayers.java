package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.guis.GuiInv;
import fr.luzog.pl.fkx.guis.GuiPlayers;
import fr.luzog.pl.fkx.guis.GuiTeams;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GCPlayers {
    public static final String syntaxe = "/" + Main.CMD + " players [help | list | <player> ... | page <page>]",
            syntaxe_players = "/" + Main.CMD + " players <player> [info | init | team | teams [<page>] | inv ...]",
            syntaxe_players_inv = "/" + Main.CMD + " players <player> inv [list | <id>[:<idx>] [del | save <clear?> | load (<delete?> <players...> | gui [<delete?> (<players,...(ns)>|.) [<page>]])]]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        if (args.length == 1)
            Bukkit.dispatchCommand(sender, Main.CMD + " players page 0");

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Joueurs G (§f" + (GManager.getCurrentGame() == null ? "null" : GManager.getCurrentGame().getPlayers().size()) + "§r) :");
            if (GManager.getCurrentGame() == null)
                u.err(" - Aucun jeu en cours");
            else
                GManager.getCurrentGame().getPlayers().forEach(gPlayer ->
                        u.succ(" - §6" + gPlayer.getName() + "§r §7" + gPlayer.getLastUuid() + " : §f" + (gPlayer.getPlayer() != null ? "§2" + SpecialChars.STAR_4_FILLED + " here" : "§4" + SpecialChars.STAR_4_EMPTY + " off")));
        } else if (args[1].equalsIgnoreCase("page")) {
            if (args.length == 2)
                Bukkit.dispatchCommand(sender, Main.CMD + " players page 0");
            else
                try {
                    if (sender instanceof Player)
                        u.getPlayer().openInventory(GuiPlayers.getPlayersInventory(Main.CMD,
                                Main.CMD + " players page", Integer.parseInt(args[2])));
                    else
                        u.synt();
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format + " (" + args[2] + ")");
                }
        } else {
            u.setSyntaxe(syntaxe_players);
            GPlayer gPlayer = GManager.getCurrentGame().getPlayer(args[1], false);
            Player p = gPlayer == null || gPlayer.getPlayer() == null ? Bukkit.getPlayerExact(args[1]) : gPlayer.getPlayer();
            if (args.length >= 3)
                if (args[2].equalsIgnoreCase("info")) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    u.succ("Joueur :");
                    u.succ(" - Nom : §f" + (p == null ? gPlayer == null ? "§cnull" : gPlayer.getName() : p.getName()));
                    u.succ(" - UUID : §7" + (p == null ? gPlayer == null ? "§cnull" : gPlayer.getLastUuid() : p.getUniqueId()));
                    u.succ(" - Team : §f" + (gPlayer == null ? "§cHors Jeu" : gPlayer.getTeam() == null ? "§4§lAucune" : gPlayer.getTeam().getName()));
                    u.succ(" - Nom d'Affichage : §f" + (gPlayer == null ? p == null ? "§cnull" : p.getDisplayName() : gPlayer.getDisplayName()));
                    u.succ(" - Vie : §c" + (p == null ? "0.0§7 /0.0" : df.format(p.getHealth()) + "§7 /" + p.getMaxHealth()));
                    u.succ(" - Nourriture : §a" + (p == null ? "0.0§7 /0.0" : df.format(p.getFoodLevel()) + "§7 /20.0"));
                    u.succ(" - Saturation : §e" + (p == null ? "0.0§7 /0.0" : df.format(p.getSaturation()) + "§7 /20.0"));
                    u.succ(" - Localisation : §f" + (p == null ? "§cAucune" : Utils.locToString(p.getLocation(), true, true, false)));
                    u.succ(" - Monde : §f" + (p == null ? "§cAucun" : (p.getWorld().getName().equalsIgnoreCase("world") ? "§2OverWorld"
                            : p.getWorld().getName().equalsIgnoreCase("world_nether") ? "§dNether"
                            : p.getWorld().getName().equalsIgnoreCase("world_the_end") ? "§5End"
                            : p.getWorld().getName())));
                    u.succ(" - Zone : §f" + (gPlayer == null ? "§cHors Jeu" : gPlayer.getZone() == null ? "§cAucune" : gPlayer.getZone().getId() + "§7 (" + gPlayer.getZone().getType() + ")"));
                } else if (args[2].equalsIgnoreCase("init")) {
                    if (gPlayer == null) {
                        gPlayer = GManager.getCurrentGame().getPlayer(args[1], true);
                        u.succ("Joueur créé : §f" + gPlayer.getDisplayName());
                    } else {
                        u.err("Joueur déjà créé : §f" + gPlayer.getDisplayName());
                    }
                } else if (args[2].equalsIgnoreCase("team")) {
                    if (gPlayer == null || gPlayer.getTeam() == null)
                        u.err("Le joueur n'a pas de team");
                    else if (sender instanceof Player)
                        u.getPlayer().openInventory(GuiTeams.getTeamInventory(u.getPlayer(), gPlayer.getTeam(),
                                Main.CMD + " players " + u.getPlayer().getName()));
                    else
                        u.err(CmdUtils.err_not_player);
                } else if (args[2].equalsIgnoreCase("teams")) {
                    if (args.length == 3)
                        Bukkit.dispatchCommand(sender, Main.CMD + " players " + args[1] + " teams 0");
                    else if (sender instanceof Player)
                        try {
                            u.getPlayer().openInventory(GuiPlayers.getPlayerChangeTeamInventory(args[1],
                                    Main.CMD + " players " + args[1], Main.CMD + " players " + args[1] + " teams",
                                    Integer.parseInt(args[3])));
                        } catch (NumberFormatException e) {
                            Bukkit.dispatchCommand(sender, Main.CMD + " players " + args[1] + " teams 0");
                        }
                    else
                        u.err(CmdUtils.err_not_player);
                } else if (args[2].equalsIgnoreCase("inv")) {
                    u.setSyntaxe(syntaxe_players_inv);
                    if (gPlayer != null) {
                        if (args.length == 3) {
                            u.getPlayer().performCommand(Main.CMD + " " + String.join(" ", args) + " gui 0");
                        } else if (args[3].equalsIgnoreCase("gui")) {
                            if (args.length == 4) {
                                u.getPlayer().performCommand(Main.CMD + " " + String.join(" ", args) + " 0");
                            } else {
                                try {
                                    int page = Integer.parseInt(args[4].contains(";") ? args[4].split(";")[0] : args[4]);
                                    Map<Integer, Integer> options = new HashMap<Integer, Integer>() {{
                                        if (args[4].contains(";"))
                                            if (args[4].split(";")[1].contains(",")) {
                                                for (String name : args[4].split(";")[1].split(",")) {
                                                    if (name.contains(":")) {
                                                        String[] split = name.split(":");
                                                        put(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                                                    }
                                                }
                                            } else if (args[4].split(";")[1].contains(":")) {
                                                String[] split = args[4].split(";")[1].split(":");
                                                put(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                                            }
                                    }};
                                    u.getPlayer().openInventory(GuiInv.getMainInventory(
                                            u.getPlayer().getName(), gPlayer.getName(),
                                            Main.CMD + " players " + args[1],
                                            Main.CMD + " players " + args[1] + " inv gui",
                                            page, options, Main.CMD + " " + String.join(" ", args)));
                                } catch (NumberFormatException e) {
                                    u.err("Indiquez un nombre de page (avec options) valide. (" + args[4] + ")");
                                }
                            }
                        } else if (args[3].equalsIgnoreCase("list")) {
                            LinkedHashMap<String, ArrayList<Utils.Pair<Integer, Utils.SavedInventory>>> inventories = new LinkedHashMap<>();
                            for (int i = 0; i < gPlayer.getInventories().size(); i++) {
                                Utils.Pair<Integer, Utils.SavedInventory> inv = new Utils.Pair<>(i, gPlayer.getInventories().get(i));
                                if (inventories.containsKey(inv.getValue().getId())) {
                                    inventories.get(inv.getValue().getId()).add(inv);
                                } else {
                                    ArrayList<Utils.Pair<Integer, Utils.SavedInventory>> list = new ArrayList<>();
                                    list.add(inv);
                                    inventories.put(inv.getValue().getId(), list);
                                }
                            }
                            u.succ("Inventaires de §6" + gPlayer.getDisplayName() + "§r§7 (§f" + inventories.size() + "§r) :");
                            for (String id : inventories.keySet()) {
                                u.succ(" §8- §b" + id + "§7 :");
                                for (Utils.Pair<Integer, Utils.SavedInventory> inv : inventories.get(id))
                                    u.succ("   §8> §f" + inv.getKey() + "§8 : §7" + inv.getValue().getName()
                                            + "§8 (§6" + inv.getValue().count() + "§8 item"
                                            + (inv.getValue().count() > 1 ? "s" : "") + ")");
                            }
                        } else {
                            String id;
                            Integer idx = null;
                            if (args[3].contains(":")) {
                                int i = args[3].lastIndexOf(":");
                                try {
                                    id = args[3].substring(0, i);
                                    idx = Integer.parseInt(args[3].substring(i + 1));
                                } catch (NumberFormatException e) {
                                    u.err("L'index n'est pas correct. (" + args[3].substring(i + 1) + ")");
                                    return false;
                                }
                            } else {
                                id = args[3];
                            }

                            if (args.length == 4) {
                                Utils.SavedInventory inv = idx == null ?
                                        gPlayer.getLastInventory(id, false)
                                        : gPlayer.getInventory(id, idx, false);
                                if (inv == null) {
                                    u.err("Inventaire introuvable. §7(§b" + id + (idx == null ? "" : "§7:§f" + idx) + "§7)");
                                } else {
                                    boolean isLast = idx == null;
                                    int last = (int) gPlayer.getInventories().stream()
                                            .filter(i -> i.getId().equals(id)).count() - 1;
                                    if (idx == null)
                                        idx = last;
                                    else
                                        isLast = idx == last;
                                    u.getPlayer().openInventory(GuiInv.getInvInventory(
                                            GuiPlayers.getHead(gPlayer.getName(), null, "null"),
                                            gPlayer.getName(), inv,
                                            isLast, idx,
                                            Main.CMD + " players " + gPlayer.getName() + " inv"));
                                }
                            } else if (args[4].equalsIgnoreCase("del")) {
                                if (idx != null) {
                                    if (gPlayer.deleteInventory(id, idx))
                                        u.succ("Inventaire §b" + id + ":" + idx + "§r supprimé !");
                                    else
                                        u.err("Inventaire §b" + id + "§7:§f" + idx + "§r introuvable.");
                                } else if (args.length >= 6 && args[5].equalsIgnoreCase("all")) {
                                    int i = gPlayer.deleteAllInventories(id);
                                    u.succ("§6" + i + "§r Inventaire" + (i > 1 ? "s" : "") + " §b" + id + "§r supprimés !");
                                } else {
                                    if (gPlayer.deleteLastInventory(id))
                                        u.succ("Dernier inventaire §b" + id + "§r supprimé !");
                                    else
                                        u.err("Dernier inventaire §b" + id + "§r introuvable.");
                                }
                            } else if (args[4].equalsIgnoreCase("save")) {
                                if (idx != null) {
                                    u.err("Vous ne pouvez pas préciser d'index pour sauvegarder un inventaire.");
                                } else if (args.length == 5) {
                                    u.getPlayer().performCommand(Main.CMD + " " + String.join(" ", args) + " gui false 0");
                                } else if (args[5].equalsIgnoreCase("gui")) {
                                    if (args.length == 6) {
                                        u.getPlayer().performCommand(Main.CMD + " " + String.join(" ", args) + " false 0");
                                    } else if (args.length == 7) {
                                        u.getPlayer().performCommand(Main.CMD + " " + String.join(" ", args) + " 0");
                                    } else {
                                        try {
                                            int page = Integer.parseInt(args[7]);
                                            boolean clear = args[6].equalsIgnoreCase("true");
                                            u.getPlayer().openInventory(GuiInv.getSaveInventory(id, gPlayer.getName(), clear,
                                                    Main.CMD + " players " + gPlayer.getName() + " inv",
                                                    Main.CMD + " players " + gPlayer.getName() + " inv " + id + " save gui", page));
                                        } catch (NumberFormatException e) {
                                            u.err("Indiquez un nombre de page valide. (" + args[7] + ")");
                                        }
                                    }
                                } else if (args.length == 6 || !(args[6].equalsIgnoreCase("true") || args[6].equalsIgnoreCase("false"))) {
                                    u.err("Indiquez si vous souhaitez ensuite clear le joueur ou non en dernier argument (true/false).");
                                } else {
                                    Player origin = Bukkit.getPlayer(args[5]);
                                    if (origin == null) {
                                        u.err(CmdUtils.err_player_not_found + " (" + args[5] + ")");
                                    } else {
                                        String name = args.length >= 8 ?
                                                String.join(" ", Arrays.copyOfRange(args, 7, args.length)) : null;
                                        gPlayer.saveInventory(id, name, u.getPlayer().getName(), origin.getInventory());
                                        u.succ("Inventaire sauvegardé ! §7(§b" + id + "§7)");
                                        if (args[6].equalsIgnoreCase("true")) {
                                            origin.getInventory().clear();
                                            origin.getInventory().setArmorContents(new ItemStack[4]);
                                        }
                                    }
                                }
                            } else if (args[4].equalsIgnoreCase("load")) {
                                if (args.length == 5) {
                                    u.synt();
                                } else if (args[5].equalsIgnoreCase("true") || args[5].equalsIgnoreCase("false")) {
                                    boolean delete = args[5].equalsIgnoreCase("true");
                                    List<Player> players = args.length < 7 ? Collections.singletonList(u.getPlayer())
                                            : new ArrayList<Player>() {{
                                        for (String name : Arrays.copyOfRange(args, 6, args.length))
                                            if (name.equalsIgnoreCase("@a"))
                                                addAll(Bukkit.getOnlinePlayers());
                                            else if (name.equalsIgnoreCase("!@a"))
                                                removeAll(Bukkit.getOnlinePlayers());
                                            else if (name.startsWith("!")) {
                                                for (Player player : Bukkit.getOnlinePlayers())
                                                    if (player.getName().equalsIgnoreCase(name.substring(1)))
                                                        remove(player);
                                            } else
                                                for (Player player : Bukkit.getOnlinePlayers())
                                                    if (player.getName().equalsIgnoreCase(name))
                                                        remove(player);
                                    }};
                                    Utils.SavedInventory inv = idx == null ?
                                            gPlayer.getLastInventory(id, delete)
                                            : gPlayer.getInventory(id, idx, delete);
                                    if (inv == null) {
                                        u.err("Inventaire introuvable. §7(§b" + id + (idx == null ? "" : "§7:§f" + idx) + "§7)");
                                    } else {
                                        players.forEach(player -> inv.equip(player, true));
                                        u.succ("Inventaire chargé sur §6" + players.size() + "§r joueur"
                                                + (players.size() > 1 ? "s" : "") + " ! §7(§b" + id
                                                + (idx == null ? "" : "§7:§f" + idx) + "§7)");
                                    }
                                } else if (args[5].equalsIgnoreCase("on")) {
                                    if (args.length == 6) {
                                        u.err("Vous devez préciser un joueur à équiper.");
                                    } else if (args.length == 7 || !(args[7].equalsIgnoreCase("true") || args[7].equalsIgnoreCase("false"))) {
                                        u.err("Vous devez préciser si vous souhaitez supprimer ensuite la sauvegarde ou non. (true/false)");
                                    } else {
                                        Player player = Bukkit.getPlayer(args[6]);
                                        boolean delete = args[7].equalsIgnoreCase("true");
                                        Utils.SavedInventory inv = idx == null ?
                                                gPlayer.getLastInventory(id, delete)
                                                : gPlayer.getInventory(id, idx, delete);
                                        if (inv == null) {
                                            u.err("Inventaire introuvable. §7(§b" + id + (idx == null ? "" : "§7:§f" + idx) + "§7)");
                                        } else if (player == null) {
                                            u.err(CmdUtils.err_player_not_found + " §7(§b" + args[6] + "§7)");
                                        } else {
                                            inv.equip(player, true);
                                            u.succ("Inventaire chargé sur §6" + player.getDisplayName()
                                                    + "§r ! §7(§b" + id + (idx == null ? "" : "§7:§f" + idx) + "§7)");
                                        }
                                    }
                                } else if (args[5].equalsIgnoreCase("gui")) {
                                    if (args.length == 6) {
                                        u.getPlayer().performCommand(Main.CMD + " " + String.join(" ", args) + " false 0");
                                    } else if (args[6].equalsIgnoreCase("true") || args[6].equalsIgnoreCase("false")) {
                                        if (args.length == 7) {
                                            u.getPlayer().performCommand(Main.CMD + " " + String.join(" ", args) + " 0");
                                        } else {
                                            try {
                                                int page = Integer.parseInt(args[7].contains(";") ? args[7].split(";")[0] : args[7]);
                                                boolean delete = args[6].equalsIgnoreCase("true");
                                                List<String> online = Bukkit.getOnlinePlayers().stream()
                                                        .map(Player::getName).collect(Collectors.toList());
                                                List<String> players = !args[7].contains(";") ? new ArrayList<>()
                                                        : new ArrayList<String>() {{
                                                    for (String name : args[7].split(";")[1].split(","))
                                                        if (name.equalsIgnoreCase("@a"))
                                                            addAll(Bukkit.getOnlinePlayers().stream()
                                                                    .map(Player::getName)
                                                                    .collect(Collectors.toList()));
                                                        else if (name.equalsIgnoreCase("!@a"))
                                                            removeAll(Bukkit.getOnlinePlayers().stream()
                                                                    .map(Player::getName)
                                                                    .collect(Collectors.toList()));
                                                        else if (name.startsWith("!"))
                                                            if (online.stream().anyMatch(n ->
                                                                    n.equalsIgnoreCase(name.substring(1)))) {
                                                                for (String n : online)
                                                                    if (n.equalsIgnoreCase(name.substring(1)))
                                                                        remove(n);
                                                            } else
                                                                remove(name.substring(1));
                                                        else if (online.stream().anyMatch(n ->
                                                                n.equalsIgnoreCase(name))) {
                                                            for (String n : online)
                                                                if (n.equalsIgnoreCase(name))
                                                                    add(n);
                                                        } else
                                                            add(name);
                                                }};
                                                u.getPlayer().openInventory(GuiInv.getLoadInventory(gPlayer.getName(),
                                                        id + ":" + idx, delete, players,
                                                        Main.CMD + " players " + gPlayer.getName() + " inv " + id + ":" + idx,
                                                        Main.CMD + " players " + gPlayer.getName() + " inv "
                                                                + id + ":" + idx + " load gui", page));
                                            } catch (NumberFormatException e) {
                                                u.err("Indiquez un nombre de page valide. (" + args[7] + ")");
                                            }
                                        }
                                    } else {
                                        u.err("Indiquez si vous souhaitez supprimer l'inventaire ou non (true/false).");
                                        u.synt();
                                    }
                                } else {
                                    u.err("L'argument '" + args[5] + "' n'est pas reconnu.");
                                    u.synt();
                                }
                            } else {
                                u.err("L'argument '" + args[4] + "' n'est pas reconnu.");
                                u.synt();
                            }
                        }
                    } else
                        u.err("Joueur non trouvé");
                } else
                    u.synt();
            else if (sender instanceof Player)
                u.getPlayer().openInventory(GuiPlayers.getPlayerInventory(p == null ? Bukkit.getOfflinePlayer(args[1]).getName() : p.getName(), u.getPlayer(), Main.CMD + " players"));
            else
                u.succ(CmdUtils.err_not_player);

        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return args.length == 2 ? new ArrayList<String>() {{
            add("help");
            add("list");
            addAll(Utils.getAllPlayers());
            add("page");
        }} : args.length == 3 ? new ArrayList<String>() {{
            add("info");
            add("init");
            add("team");
            add("inv");
        }} : args[2].equalsIgnoreCase("inv") ? new ArrayList<String>() {{
            GPlayer gPlayer = GManager.getCurrentGame().getPlayer(args[1], false);
            if (args.length == 4) {
                add("list");
                add("save");
                add("load");
                add("del");
            } else if (args[3].equalsIgnoreCase("save")) {
                if (args.length == 5) {
                    add("true");
                    add("false");
                } else if (args.length == 6) {
                    add("null");
                    if (gPlayer != null)
                        addAll(gPlayer.getInventories().stream().map(Utils.SavedInventory::getId)
                                .distinct().collect(Collectors.toList()));
                }
            } else if (args[3].equalsIgnoreCase("load")) {
                if (args.length == 5) {
                    add("true");
                    add("false");
                } else if (args.length == 6) {
                    add("null");
                    if (gPlayer != null)
                        addAll(gPlayer.getInventories().stream().map(Utils.SavedInventory::getId)
                                .distinct().collect(Collectors.toList()));
                } else if (args.length == 7) {
                    if (gPlayer != null)
                        for (int i = 0; i < gPlayer.getInventories().stream().filter(inv -> inv.getId().equals(args[5])).count(); i++)
                            add(String.valueOf(i));
                }
            } else if (args[3].equalsIgnoreCase("del")) {
                if (args.length == 5) {
                    add("null");
                    if (gPlayer != null)
                        addAll(gPlayer.getInventories().stream().map(Utils.SavedInventory::getId)
                                .distinct().collect(Collectors.toList()));
                } else if (args.length == 6) {
                    add("all");
                    if (gPlayer != null)
                        for (int i = 0; i < gPlayer.getInventories().stream().filter(inv -> inv.getId().equals(args[4])).count(); i++)
                            add(String.valueOf(i));
                }
            }
        }} : new ArrayList<>();
    }
}
