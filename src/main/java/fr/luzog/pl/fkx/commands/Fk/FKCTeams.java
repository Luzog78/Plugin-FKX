package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.*;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FKCTeams {
    public static final String syntaxe = "/fk teams [help | list | create <id> [<options>] | (eliminate | reintroduce | delete) <id> | <id> ...]",
            syntaxe_create = "/fk teams create <id> [<options>]",
            syntaxe_team = "/fk teams <id> [help | info | list | (add | remove) <player> | armorStand (hide | show) | options ...]",
            syntaxe_team_options = "/fk teams <id>  options [help | list | <options>]",
            syntaxe_opts = "Options:\n  > --d <displayName>\n  > --p <prefix>\n  > --c <color>\n  > --r <radius>\n  > --s <x> <y> <z> [<yw> <pi>] [<world>]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            u.succ("TODO -> Teams GUIs");

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Teams :");
            FKManager.getCurrentGame().getTeams().forEach(t -> u.succ(" - §6" + t.getId() + "§r : §f" + t.getName()));
        } else if (args[1].equalsIgnoreCase("create")) {
            u.setSyntaxe(syntaxe_create + "\n" + syntaxe_opts);
            if (args.length < 3)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "id"));
            else if (FKManager.getCurrentGame().getTeam(args[2]) != null)
                u.err("Team déjà existante.");
            else if (args[2].length() > 8)
                u.err("L'identifiant ne doit pas dépasser 8 caractères.");
            else {
                FKTeam team = new FKTeam(args[2]);
                FKManager.getCurrentGame().addTeam(team);
                u.succ("Team §f" + team.getColor() + team.getName() + "§r créée dans §6" + FKManager.getCurrentGame().getId() + "§r.");
                if (args.length > 3)
                    handleOptions(u, team, args, 3);
            }
        } else if (args[1].equalsIgnoreCase("eliminate")) {
            if (args.length == 2)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "id"));
            else if (FKManager.getCurrentGame().getTeam(args[2]) == null)
                u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
            else if (args[2].equalsIgnoreCase(FKTeam.GODS_ID) || args[2].equalsIgnoreCase(FKTeam.SPECS_ID))
                u.err("Cette équipe ne peut pas être disqualifiée.");
            else if (FKManager.getCurrentGame().getTeam(args[2]).isEliminated())
                u.err("Cette équipe est déjà éliminée.");
            else {
                FKManager.getCurrentGame().getTeam(args[2]).eliminate(true,
                        FKManager.getCurrentGame().getState() == FKManager.State.RUNNING
                                || FKManager.getCurrentGame().getState() == FKManager.State.PAUSED, true);
            }
        } else if (args[1].equalsIgnoreCase("reintroduce")) {
            if (args.length == 2)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "id"));
            else if (FKManager.getCurrentGame().getTeam(args[2]) == null)
                u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
            else if (args[2].equalsIgnoreCase(FKTeam.GODS_ID) || args[2].equalsIgnoreCase(FKTeam.SPECS_ID))
                u.err("Cette équipe ne peut pas être requalifiée.");
            else if (!FKManager.getCurrentGame().getTeam(args[2]).isEliminated())
                u.err("Cette équipe est déjà participante.");
            else {
                FKManager.getCurrentGame().getTeam(args[2]).reintroduce(true, true);
            }
        } else if (args[1].equalsIgnoreCase("delete")) {
            if (args.length == 2)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "id"));
            else if (FKManager.getCurrentGame().getTeam(args[2]) == null)
                u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
            else if (args[2].equalsIgnoreCase(FKTeam.GODS_ID) || args[2].equalsIgnoreCase(FKTeam.SPECS_ID))
                u.err("Cette équipe ne peut pas être supprimée.");
            else {
                FKManager.getCurrentGame().removeTeam(args[2]);
                u.succ("Equipe §6" + args[2] + "§r supprimée.");
            }
        } else if (FKManager.getCurrentGame().getTeam(args[1]) != null) {
            u.setSyntaxe(syntaxe_team);
            FKTeam t = FKManager.getCurrentGame().getTeam(args[1]);

            if (args.length == 2)
                u.succ("TODO -> Team GUIs");
            else if (args[2].equalsIgnoreCase("info")) {
                u.succ("Team :");
                u.succ(" - Id : §6" + t.getId());
                u.succ(" - Name : §f" + t.getName());
                u.succ(" - Color : §f" + t.getColor() + t.getColor().name());
                u.succ(" - Préfixe : §7'§f" + t.getPrefix() + "§7'");
                u.succ(" - Joueurs : §f" + new DecimalFormat("00").format(t.getPlayers().size()));
                u.succ(" - Location :");
                u.succ("    > Rayon : §f" + t.getRadius());
                u.succ("    > Spawn : §f" + Utils.locToString(t.getSpawn(), true, false, true));
                u.succ(" - Autorisations :");
                t.getPermissions().getPermissions().keySet().stream().sorted(Comparator.comparingInt(o -> o.name().length())).forEach(k ->
                        u.succ("    > §6" + k.name() + "§r : §f" + t.getPermissions().getPermission(k).toFormattedString()));
            } else if (args[2].equalsIgnoreCase("list")) {
                u.succ("Joueurs de §f" + t.getColor() + t.getName() + "§r :");
                if (t.getPlayers().isEmpty())
                    u.err(" - Aucun joueur");
                else
                    t.getPlayers().forEach(p -> u.succ(" - §6" + p.getName() + "§r §7" + (p.getLastUuid() + "").replace("-", ":") + "§r :  " + (p.getPlayer() != null ? "§2" + SpecialChars.STAR_4_FILLED + " here" : "§4" + SpecialChars.STAR_4_EMPTY + " off")));
            } else if (args[2].equalsIgnoreCase("list")) {
                u.succ("Joueurs de §f" + t.getColor() + t.getName() + "§r :");
                if (t.getPlayers().isEmpty())
                    u.err(" - Aucun joueur");
                else
                    t.getPlayers().forEach(p -> u.succ(" - §6" + p.getName() + "§r §7" + (p.getLastUuid() + "").replace("-", ":") + "§r :  " + (p.getPlayer() != null ? "§2" + SpecialChars.STAR_4_FILLED + " here" : "§4" + SpecialChars.STAR_4_EMPTY + " off")));
            } else if (args[2].equalsIgnoreCase("add")) {
                if (args.length == 3)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "player"));
                else
                    try {
                        FKPlayer p;
                        p = FKManager.getCurrentGame().getPlayer(args[3], true);

                        try {
                            t.addPlayer(p);
                            u.succ("Joueur §6" + p.getDisplayName() + "§r ajouté à l'équipe.");
                        } catch (FKException.PlayerAlreadyInTeamException e) {
                            u.err(CmdUtils.err_player_already_in_team + " (§f" + p.getDisplayName() + "§r)");
                        }
                    } catch (FKException.PlayerDoesNotExistException e) {
                        u.err(CmdUtils.err_player_does_not_exist + " (" + args[3] + ")");
                    }
            } else if (args[2].equalsIgnoreCase("remove")) {
                if (args.length == 3)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "player"));
                else try {
                    FKPlayer p;
                    p = FKManager.getCurrentGame().getPlayer(args[3], true);

                    try {
                        t.removePlayer(p);
                        u.succ("Joueur §6" + p.getDisplayName() + "§r supprimé de l'équipe.");
                    } catch (FKException.PlayerNotInTeamException e) {
                        u.err(CmdUtils.err_player_not_in_the_team + " (§f" + p.getDisplayName() + "§r)");
                    }
                } catch (FKException.PlayerDoesNotExistException e) {
                    u.err(CmdUtils.err_player_does_not_exist + " (" + args[3] + ")");
                }
            } else if (args[2].equalsIgnoreCase("armorStand")) {
                if (args.length == 3)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "show | hide"));
                else if (args[3].equalsIgnoreCase("show")) {
                    Entity e = t.getArmorStand();
                    e.setCustomName(t.getProgressBar());
                    e.setCustomNameVisible(true);
                    u.succ("ArmorStand de l'équipe §f" + t.getColor() + t.getName() + "§r visible.");
                } else if (args[3].equalsIgnoreCase("hide")) {
                    Entity e = t.getArmorStand();
                    e.setCustomName(t.getProgressBar());
                    e.setCustomNameVisible(false);
                    u.succ("ArmorStand de l'équipe §f" + t.getColor() + t.getName() + "§r caché.");
                } else
                    u.err("Argument '" + args[3] + "' invalide.");
            } else if (args[2].equalsIgnoreCase("options")) {
                u.setSyntaxe(syntaxe_team_options + "\n" + syntaxe_opts);
                if (args.length == 3)
                    u.succ("TODO -> Team Options GUIs");
                else if (args[3].equalsIgnoreCase("help") || args[3].equals("?") || args[3].equalsIgnoreCase("-h") || args[3].equalsIgnoreCase("-help"))
                    u.synt();
                else
                    handleOptions(u, t, args, 3);
            } else
                u.synt();

        } else
            u.err(CmdUtils.err_team_not_found + " ('" + args[1] + "')");

        return false;
    }

    public static String handleString(String base, int substring) {
        String s = base.substring(substring);
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
            s = s.substring(1, s.length() - 1);
        return s.replace("\\\"", "\"").replace("\\'", "'").replace("\\ ", " ").replace("\\\\", "\\");
    }

    public static void handleOptions(CmdUtils u, FKTeam t, String[] arguments, int substring) {
        String[] args = (" " + String.join(" ", Arrays.copyOfRange(arguments, substring, arguments.length))).split(" --");
        for (int i = 0; i < args.length; i++)
            args[i] = args[i].replace("\\--", "--").replace("\\\\", "\\");
        u.succ("Options de l'équipe §f" + t.getColor() + t.getName() + "§r :");
        boolean hasAnyOption = false;
        for (String arg : args) {
            if (arg.replace(" ", "").length() == 0)
                continue;
            hasAnyOption = true;
            boolean isEmpty = arg.length() < 3 || arg.charAt(1) != ' ' || arg.substring(2).replace(" ", "").length() == 0;
            if (arg.toLowerCase().equals("d") || arg.toLowerCase().startsWith("d ")) {
                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "displayName"));
                else if (handleString(arg, 2).length() > 32)
                    u.err(" - Le Nom ne doit pas dépasser 32 caractères.");
                else {
                    t.setName(handleString(arg, 2), true);
                    u.succ(" - Nom : §f" + t.getColor() + t.getName());
                }
            } else if (arg.toLowerCase().equals("p") || arg.toLowerCase().startsWith("p ")) {
                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "prefix"));
                else if (handleString(arg, 2).length() > 32)
                    u.err(" - Le Préfixe ne doit pas dépasser 32 caractères.");
                else {
                    t.setPrefix(handleString(arg, 2), true);
                    u.succ(" - Préfixe : §7'§f" + t.getColor() + t.getPrefix() + "§7'");
                }
            } else if (arg.toLowerCase().equals("c") || arg.toLowerCase().startsWith("c ")) {
                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "color"));
                else {
                    try {
                        t.setColor(ChatColor.valueOf(handleString(arg, 2).toUpperCase()), true);
                        u.succ(" - Couleur : §f" + handleString(arg, 2).toUpperCase());
                    } catch (IllegalArgumentException e) {
                        u.err(" - Couleur '" + handleString(arg, 2) + "' inconnue.");
                    }
                }
            } else if (arg.toLowerCase().equals("r") || arg.toLowerCase().startsWith("r ")) {
                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "radius"));
                else {
                    try {
                        t.setRadius(Double.parseDouble(handleString(arg, 2)), true);
                        u.succ(" - Rayon : §f" + t.getRadius());
                    } catch (NumberFormatException e) {
                        u.err(" - Rayon '" + handleString(arg, 2) + "' invalide.");
                    }
                }
            } else if (arg.toLowerCase().equals("s") || arg.toLowerCase().startsWith("s ")) {
                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<x> <y> <z>"));
                else if (handleString(arg, 2).split(" ").length <= 1)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<y> <z>"));
                else if (handleString(arg, 2).split(" ").length <= 2)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<z>"));
                else {
                    Double x = null, y = null, z = null;
                    Float yw = null, pi = null;
                    World w = Main.world;
                    boolean orientation = handleString(arg, 2).split(" ").length >= 5;

                    try {
                        x = Double.parseDouble(handleString(arg, 2).split(" ")[0]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        y = Double.parseDouble(handleString(arg, 2).split(" ")[1]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        z = Double.parseDouble(handleString(arg, 2).split(" ")[2]);
                    } catch (NumberFormatException ignored) {
                    }
                    if (orientation) {
                        try {
                            yw = Float.parseFloat(handleString(arg, 2).split(" ")[3]);
                        } catch (NumberFormatException ignored) {
                        }
                        try {
                            pi = Float.parseFloat(handleString(arg, 2).split(" ")[4]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (handleString(arg, 2).split(" ").length >= (orientation ? 6 : 4)) {
                        w = Bukkit.getWorld(handleString(arg, 2).split(" ")[orientation ? 5 : 3]);
                    }

                    List<String> err = new ArrayList<>();
                    if (x == null)
                        err.add("X");
                    if (y == null)
                        err.add("Y");
                    if (z == null)
                        err.add("Z");
                    if (orientation && yw == null)
                        err.add("Yaw");
                    if (orientation && pi == null)
                        err.add("Pitch");
                    if (w == null)
                        err.add("World");

                    if (err.size() > 0) {
                        u.err(" - Erreur avec le(s) paramètre(s) : " + String.join(", ", err) + ".");
                    } else {
                        Location loc = orientation ? new Location(w, x, y, z, yw, pi) : new Location(w, x, y, z);
                        t.setSpawn(loc, true);
                        u.succ(" - Spawn : §f" + Utils.locToString(loc, true, orientation, true));
                    }
                }
            } else
                u.err(" - " + CmdUtils.err_arg.replace("%ARG%", arg));
        }
        if (!hasAnyOption)
            u.err(" - " + CmdUtils.err_unknown);
    }

    public static ArrayList<String> completeOptions(CommandSender sender, String[] args) {
        try {
            if (args[args.length - 2].equalsIgnoreCase("--c"))
                return Arrays.stream(ChatColor.values()).map(ChatColor::name).collect(Collectors.toCollection(ArrayList::new));
            else if (args[args.length - 2].equalsIgnoreCase("--r"))
                return new ArrayList<>(Arrays.asList("5.0", "8.0", "10.0", "12.0", "15.0", "20.0", "25.0"));
            else if (args[args.length - 2].equalsIgnoreCase("--s") && sender instanceof Player) {
                Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                return new ArrayList<>(Collections.singletonList(loc.getBlockX() + ""));
            } else if (args[args.length - 3].equalsIgnoreCase("--s") && sender instanceof Player) {
                Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                return new ArrayList<>(Collections.singletonList(loc.getBlockY() + ""));
            } else if (args[args.length - 4].equalsIgnoreCase("--s") && sender instanceof Player) {
                Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                return new ArrayList<>(Collections.singletonList(loc.getBlockZ() + ""));
            } else if (args[args.length - 5].equalsIgnoreCase("--s")) {
                ArrayList<String> list = new ArrayList<>();
                if (sender instanceof Player) {
                    Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                    Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                    list.add(loc.getYaw() + "");
                }
                list.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                return list;
            } else if (args[args.length - 6].equalsIgnoreCase("--s") && sender instanceof Player) {
                Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                return new ArrayList<>(Collections.singletonList(loc.getPitch() + ""));
            } else if (args[args.length - 7].equalsIgnoreCase("--s")) {
                return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toCollection(ArrayList::new));
            } else if (args[args.length - 1].startsWith("--") || !args[args.length - 2].startsWith("--"))
                return new ArrayList<>(Arrays.asList("--d", "--p", "--c", "--r", "--s"));
            else
                return new ArrayList<>();
        } catch (IndexOutOfBoundsException e) {
            if (args[args.length - 1].startsWith("--") || !args[args.length - 2].startsWith("--"))
                return new ArrayList<>(Arrays.asList("--d", "--p", "--c", "--r", "--s"));
            else
                return new ArrayList<>();
        }
    }

//    public static final String syntaxe = "/fk teams [help | list | create <id> [<options>] | delete <id> | <id> ...]",
//            syntaxe_create = "/fk teams create <id> [<options>]",
//            syntaxe_team = "/fk teams <id> [help | info | list | (add | remove) <player> | options ...]",
//            syntaxe_team_options = "/fk teams <id>  options [help | list | <options>]",
//            syntaxe_opts = "Options:\n  > -d <displayName>\n  > -p <prefix>\n  > -c <color>\n  > -r <radius>\n  > -s <x> <y> <z> [<yw> <pi>] [<world>]";

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            if (args.length == 2) {
                add("help");
                add("list");
                add("create");
                add("eliminate");
                add("reintroduce");
                add("delete");
                addAll(FKManager.getCurrentGame().getTeams().stream().map(FKTeam::getId).collect(Collectors.toList()));
            } else {
                if (args[1].equalsIgnoreCase("create")) {
                    if (args.length >= 4)
                        addAll(completeOptions(sender, args));
                } else if (args[1].equalsIgnoreCase("eliminate") || args[1].equalsIgnoreCase("reintroduce")
                        || args[1].equalsIgnoreCase("delete")) {
                    if (args.length == 3)
                        addAll(FKManager.getCurrentGame().getTeams().stream().map(FKTeam::getId).collect(Collectors.toList()));
                } else if (FKManager.getCurrentGame().getTeam(args[1]) != null) {
                    if (args.length == 3) {
                        add("help");
                        add("info");
                        add("list");
                        add("add");
                        add("remove");
                        add("armorStand");
                        add("options");
                    } else if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove")) {
                        if (args.length == 4)
                            addAll(Utils.getAllPlayers());
                    } else if (args[2].equalsIgnoreCase("armorStand")) {
                        if (args.length == 4) {
                            add("hide");
                            add("show");
                        }
                    } else if (args[2].equalsIgnoreCase("options")) {
                        if (args.length == 4) {
                            add("help");
                            add("list");
                        }
                        addAll(completeOptions(sender, args));
                    }
                }
            }
        }};
    }
}
