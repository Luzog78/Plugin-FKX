package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.*;
import fr.luzog.pl.fkx.guis.GuiTeams;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GCTeams {
    public static final String syntaxe = "/" + Main.CMD + " teams [help | list | create <id> [<options>] | (eliminate | reintroduce | delete) <id> | clearEntities | page <page> | <id> ...]",
            syntaxe_create = "/" + Main.CMD + " teams create <id> [<options>]",
            syntaxe_team = "/" + Main.CMD + " teams <id> [help | info | list | armorStand (hide | show)]"
                    + "\n§r/" + Main.CMD + " teams <id> [colorGui | playersGui [<page>] | (add | remove) <player>]"
                    + "\n§r/" + Main.CMD + " teams <id> [altar | wall [<height>] <material> | options [<args...>]]",
            syntaxe_team_options = "/" + Main.CMD + " teams <id>  options [help | list | <options>]",
            syntaxe_opts = "Options:"
                    + "\n§r  > --d <displayName>"
                    + "\n§r  > --p <prefix>"
                    + "\n§r  > --c <color>"
                    + "\n§r  > --r <radius>"
                    + "\n§r  > --e <eliminationDelay>"
                    + "\n§r  > --s <x> <y> <z> [<yw> <pi>] [<world>]  §7||>> Spawn§r"
                    /*+ "\n  > --g <x> <y> <z> [<yw> <pi>] [<world>]  §7||>> Guardian§r"*/;

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            Bukkit.dispatchCommand(sender, Main.CMD + " teams page 0");

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Teams :");
            GManager.getCurrentGame().getTeams().forEach(t -> u.succ(" - §6" + t.getId() + "§r : §f" + t.getName()));
        } else if (args[1].equalsIgnoreCase("page")) {
            if (args.length == 2)
                Bukkit.dispatchCommand(sender, Main.CMD + " teams page 0");
            else if (sender instanceof Player)
                try {
                    u.getPlayer().openInventory(GuiTeams.getTeamsInventory(Main.CMD, Main.CMD + " teams page", Integer.parseInt(args[2])));
                } catch (NumberFormatException e) {
                    Bukkit.dispatchCommand(sender, Main.CMD + " teams page 0");
                }
            else
                u.err(CmdUtils.err_not_player);
        } else if (args[1].equalsIgnoreCase("clearEntities")) {
            u.succ("Vous venez de supprimer §c" + GTeam.killAllArmorStands() + "§r entités !");
        } else if (args[1].equalsIgnoreCase("create")) {
            u.setSyntaxe(syntaxe_create + "\n" + syntaxe_opts);
            if (args.length < 3)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "id"));
            else if (GManager.getCurrentGame().getTeam(args[2]) != null)
                u.err("Team déjà existante.");
            else if (args[2].length() > 8)
                u.err("L'identifiant ne doit pas dépasser 8 caractères.");
            else {
                GTeam team = new GTeam(args[2]);
                GManager.getCurrentGame().addTeam(team);
                u.succ("Team §f" + team.getColor() + team.getName() + "§r créée dans §6" + GManager.getCurrentGame().getId() + "§r.");
                if (args.length > 3)
                    handleOptions(u, team, args, 3);
            }
        } else if (args[1].equalsIgnoreCase("eliminate")) {
            if (args.length == 2)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "id"));
            else if (GManager.getCurrentGame().getTeam(args[2]) == null)
                u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
            else if (args[2].equalsIgnoreCase(GTeam.GODS_ID) || args[2].equalsIgnoreCase(GTeam.SPECS_ID))
                u.err("Cette équipe ne peut pas être disqualifiée.");
            else if (GManager.getCurrentGame().getTeam(args[2]).isEliminated())
                u.err("Cette équipe est déjà éliminée.");
            else {
                GManager.getCurrentGame().getTeam(args[2]).eliminate(true,
                        GManager.getCurrentGame().getState() == GManager.State.RUNNING
                                || GManager.getCurrentGame().getState() == GManager.State.PAUSED, true);
            }
        } else if (args[1].equalsIgnoreCase("reintroduce")) {
            if (args.length == 2)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "id"));
            else if (GManager.getCurrentGame().getTeam(args[2]) == null)
                u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
            else if (args[2].equalsIgnoreCase(GTeam.GODS_ID) || args[2].equalsIgnoreCase(GTeam.SPECS_ID))
                u.err("Cette équipe ne peut pas être requalifiée.");
            else if (!GManager.getCurrentGame().getTeam(args[2]).isEliminated())
                u.err("Cette équipe est déjà participante.");
            else {
                GManager.getCurrentGame().getTeam(args[2]).reintroduce(true, true);
            }
        } else if (args[1].equalsIgnoreCase("delete")) {
            if (args.length == 2)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "id"));
            else if (GManager.getCurrentGame().getTeam(args[2]) == null)
                u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
            else if (args[2].equalsIgnoreCase(GTeam.GODS_ID) || args[2].equalsIgnoreCase(GTeam.SPECS_ID))
                u.err("Cette équipe ne peut pas être supprimée.");
            else {
                GManager.getCurrentGame().removeTeam(args[2]);
                u.succ("Equipe §6" + args[2] + "§r supprimée.");
            }
        } else if (GManager.getCurrentGame().getTeam(args[1]) != null) {
            u.setSyntaxe(syntaxe_team);
            GTeam t = GManager.getCurrentGame().getTeam(args[1]);

            if (args.length == 2)
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiTeams.getTeamInventory(u.getPlayer(), t, Main.CMD + " teams"));
                else
                    u.err(CmdUtils.err_not_player);
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
            } else if (args[2].equalsIgnoreCase("colorGui")) {
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiTeams.getColorInventory(t, Main.CMD + " teams " + t.getId()));
                else
                    u.err(CmdUtils.err_not_player);
            } else if (args[2].equalsIgnoreCase("playersGui")) {
                if (args.length == 3)
                    Bukkit.dispatchCommand(sender, Main.CMD + " teams " + t.getId() + " playersGui 0");
                else if (sender instanceof Player)
                    try {
                        u.getPlayer().openInventory(GuiTeams.getTeamPlayers(t, Main.CMD + " teams " + t.getId(),
                                Main.CMD + " teams " + t.getId() + " playersGui", Integer.parseInt(args[3])));
                    } catch (NumberFormatException e) {
                        Bukkit.dispatchCommand(sender, Main.CMD + " teams " + t.getId() + " playersGui 0");
                    }
                else
                    u.err(CmdUtils.err_not_player);
            } else if (args[2].equalsIgnoreCase("add")) {
                if (args.length == 3)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "player"));
                else
                    try {
                        GPlayer p;
                        p = GManager.getCurrentGame().getPlayer(args[3], true);

                        try {
                            t.addPlayer(p);
                            u.succ("Joueur §6" + p.getDisplayName() + "§r ajouté à l'équipe.");
                        } catch (GException.PlayerAlreadyInTeamException e) {
                            u.err(CmdUtils.err_player_already_in_team + " (§f" + p.getDisplayName() + "§r)");
                        }
                    } catch (GException.PlayerDoesNotExistException e) {
                        u.err(CmdUtils.err_player_does_not_exist + " (" + args[3] + ")");
                    }
            } else if (args[2].equalsIgnoreCase("remove")) {
                if (args.length == 3)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "player"));
                else try {
                    GPlayer p;
                    p = GManager.getCurrentGame().getPlayer(args[3], true);

                    try {
                        t.removePlayer(p);
                        u.succ("Joueur §6" + p.getDisplayName() + "§r supprimé de l'équipe.");
                    } catch (GException.PlayerNotInTeamException e) {
                        u.err(CmdUtils.err_player_not_in_the_team + " (§f" + p.getDisplayName() + "§r)");
                    }
                } catch (GException.PlayerDoesNotExistException e) {
                    u.err(CmdUtils.err_player_does_not_exist + " (" + args[3] + ")");
                }
            } else if (args[2].equalsIgnoreCase("altar")) {
                t.altar();
                u.succ("Vous avez invoqué l'§6Autel§r de la team §f" + t.getColor() + t.getName());
            } else if (args[2].equalsIgnoreCase("wall")) {
                Material mat;
                if (args.length == 3)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "material"));
                else if (args.length == 4)
                    if ((mat = Material.matchMaterial(args[3])) == null)
                        u.err("Materiau '" + args[3] + "' inconnu.");
                    else {
                        t.wall(1, mat);
                        u.succ("Vous avez construit une muraille en §9" + mat.name().toLowerCase()
                                + "§r de §b1 bloc§r de haut pour la team " + t.getColor() + t.getName());
                    }
                else
                    try {
                        int height = Integer.parseInt(args[3]);
                        if ((mat = Material.matchMaterial(args[4])) == null)
                            u.err("Materiau '" + args[4] + "' inconnu.");
                        else {
                            t.wall(height, mat);
                            u.succ("Vous avez construit une muraille en §9" + mat.name().toLowerCase()
                                    + "§r de §b" + height + " bloc" + (height > 1 ? "s" : "")
                                    + "§r de haut pour la team " + t.getColor() + t.getName());
                        }
                    } catch (NumberFormatException e) {
                        u.err(CmdUtils.err_number_format + " (" + args[3] + ")");
                    }

            }/* else if (args[2].equalsIgnoreCase("chestRoom")) {
                if (t.getId().equals(GTeam.GODS_ID) || t.getId().equals(GTeam.SPECS_ID))
                    u.err("Cette équipe n'a pas de salle des coffres.");
                else if (args.length == 3)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "<x> <y> <z>"));
                else if (args.length == 4)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "<y> <z>"));
                else if (args.length == 5)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "<z>"));
                else {
                    Double x = null, y = null, z = null;
                    Float yw = null, pi = null;
                    World w = Main.world;
                    boolean orientation = args.length >= 8;

                    try {
                        x = Double.parseDouble(args[3]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        y = Double.parseDouble(args[4]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        z = Double.parseDouble(args[5]);
                    } catch (NumberFormatException ignored) {
                    }
                    if (orientation) {
                        try {
                            yw = Float.parseFloat(args[6]);
                        } catch (NumberFormatException ignored) {
                        }
                        try {
                            pi = Float.parseFloat(args[7]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (args.length >= (orientation ? 9 : 7)) {
                        w = Bukkit.getWorld(args[orientation ? 8 : 6]);
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
                        u.err("Erreur avec le(s) paramètre(s) : " + String.join(", ", err) + ".");
                    } else {
                        Location loc = orientation ? new Location(w, x, y, z, yw, pi) : new Location(w, x, y, z);
                        t.setChestsRoom(loc, true, true);
                        u.succ("Position de la salle des coffres mise à jour en §f"
                                + Utils.locToString(loc, true, orientation, true));
                    }
                }
            }*/ else if (args[2].equalsIgnoreCase("armorStand")) {
                if (args.length == 3)
                    u.err(CmdUtils.err_missing_arg.replace("%ARG%", "show | hide"));
                else if (args[3].equalsIgnoreCase("show")) {
                    t.updateArmorStand();
                    u.succ("ArmorStand de l'équipe §f" + t.getColor() + t.getName() + "§r visible.");
                } else if (args[3].equalsIgnoreCase("hide")) {
                    t.killArmorStand();
                    u.succ("ArmorStand de l'équipe §f" + t.getColor() + t.getName() + "§r caché.");
                } else
                    u.err("Argument '" + args[3] + "' invalide.");
            } else if (args[2].equalsIgnoreCase("options")) {
                u.setSyntaxe(syntaxe_team_options + "\n" + syntaxe_opts);
                if (args.length == 3) {
                    u.err("Arguments manquants.\n");
                    u.synt();
                } else if (args[3].equalsIgnoreCase("help") || args[3].equals("?")
                        || args[3].equalsIgnoreCase("-h") || args[3].equalsIgnoreCase("-help")
                        || args[3].equalsIgnoreCase("--h") || args[3].equalsIgnoreCase("--help"))
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
        return s.replace("\\\"", "\"").replace("\\'", "'").replace("\\ ", " ").replace("\\\\", "\\").replace("&", "§");
    }

    public static void handleOptions(CmdUtils u, GTeam t, String[] arguments, int substring) {
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
            } else if (arg.toLowerCase().equals("e") || arg.toLowerCase().startsWith("e ")) {
                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "eliminationDelay"));
                else {
                    try {
                        t.setDefaultEliminationCooldown((long) (Double.parseDouble(
                                handleString(arg, 2)) * 20), true);
                        u.succ(" - Délai d'élimination : §7" + (t.getDefaultEliminationCooldown() / 20.0) + "s");
                    } catch (NumberFormatException e) {
                        u.err(" - Délai d'élimination '" + handleString(arg, 2) + "' invalide.");
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
                Location loc = block == null || block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
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

//    public static final String syntaxe = "/" + Main.CMD + " teams [help | list | create <id> [<options>] | delete <id> | <id> ...]",
//            syntaxe_create = "/" + Main.CMD + " teams create <id> [<options>]",
//            syntaxe_team = "/" + Main.CMD + " teams <id> [help | info | list | (add | remove) <player> | options ...]",
//            syntaxe_team_options = "/" + Main.CMD + " teams <id>  options [help | list | <options>]",
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
                add("page");
                add("clearEntities");
                addAll(GManager.getCurrentGame().getTeams().stream().map(GTeam::getId).collect(Collectors.toList()));
            } else {
                if (args[1].equalsIgnoreCase("create")) {
                    if (args.length >= 4)
                        addAll(completeOptions(sender, args));
                } else if (args[1].equalsIgnoreCase("eliminate") || args[1].equalsIgnoreCase("reintroduce")
                        || args[1].equalsIgnoreCase("delete")) {
                    if (args.length == 3)
                        addAll(GManager.getCurrentGame().getTeams().stream().map(GTeam::getId).collect(Collectors.toList()));
                } else if (GManager.getCurrentGame().getTeam(args[1]) != null) {
                    if (args.length == 3) {
                        add("help");
                        add("info");
                        add("list");
                        add("add");
                        add("remove");
                        add("options");
                        add("altar");
                        add("wall");
                        add("armorStand");
                        add("playersGui");
                        add("colorGui");
                    } else if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove")) {
                        if (args.length == 4)
                            addAll(Utils.getAllPlayers());
                    } else if (args[2].equalsIgnoreCase("wall")) {
                        if (args.length == 4)
                            addAll(Arrays.asList("1", "2", "3", "5", "7", "8", "10", "15", "20", "30"));
                        else if (args.length == 5)
                            addAll(Arrays.stream(Material.values()).map(m -> m.name().toLowerCase())
                                    .collect(Collectors.toList()));
                    }/* else if (args[2].equalsIgnoreCase("chestRoom")) {
                        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                        Block b = sender instanceof Player ? ((Player) sender).getTargetBlock(new HashSet<>(
                                Collections.singletonList(Material.AIR)), 7) : null;
                        Location loc1 = sender instanceof Player ? ((Player) sender).getLocation() : null,
                                loc2 = b != null ? Utils.normalize(b.getLocation(), false) : null;
                        if (args.length == 4) {
                            if (loc1 != null)
                                add("" + df.format(loc1.getX()));
                            if (loc2 != null)
                                add("" + df.format(loc2.getX()));
                            add("0.00");
                        } else if (args.length == 5) {
                            if (loc1 != null)
                                add("" + df.format(loc1.getY()));
                            if (loc2 != null)
                                add("" + df.format(loc2.getY()));
                            add("0.00");
                        } else if (args.length == 6) {
                            if (loc1 != null)
                                add("" + df.format(loc1.getZ()));
                            if (loc2 != null)
                                add("" + df.format(loc2.getZ()));
                            add("0.00");
                        } else if (args.length == 7) {
                            if (loc1 != null)
                                add("" + df.format(loc1.getYaw()));
                            if (loc2 != null)
                                add("" + df.format(loc2.getYaw()));
                            add("0.0");
                        } else if (args.length == 8) {
                            if (loc1 != null)
                                add("" + df.format(loc1.getPitch()));
                            if (loc2 != null)
                                add("" + df.format(loc2.getPitch()));
                            add("0.0");
                        } else if (args.length == 9) {
                            Bukkit.getWorlds().stream().map(World::getName).forEach(this::add);
                        }
                    }*/ else if (args[2].equalsIgnoreCase("armorStand")) {
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
