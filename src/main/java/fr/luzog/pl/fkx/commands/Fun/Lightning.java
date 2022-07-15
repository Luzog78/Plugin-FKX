package fr.luzog.pl.fkx.commands.Fun;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.CustomNBT;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Lightning implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/lightning (-rod | -pos <x> <y> <z> [<times>] | [<times>] <players...>)";
    public static final String NBTName = "Mjolnir";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length >= 1 && !(args[0].equals("?") || args[0].equalsIgnoreCase("help")))

            if (args[0].equalsIgnoreCase("-rod"))
                if (sender instanceof Player)
                    if (u.getPlayer().getInventory().firstEmpty() != -1) {
                        u.getPlayer().getInventory().addItem(getMjolnir());
                        u.succ("Vous avez invoqué§6", getMjolnir().getItemMeta().getDisplayName(), "§r!");
                    } else
                        u.err(CmdUtils.err_inventory_full);
                else
                    u.synt();

            else if (args[0].equalsIgnoreCase("-pos"))
                if (args.length >= 4)
                    try {
                        World w = sender instanceof Player ? ((Player) sender).getWorld() : Main.world;
                        int times = args.length >= 5 ? Integer.parseInt(args[4]) : 1;
                        new BukkitRunnable() {
                            int timer = times;

                            @Override
                            public void run() {
                                if (timer <= 0)
                                    cancel();
                                Location l = new Location(w, Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                                w.strikeLightning(l);
                                u.succ("Un éclair s'abat à la position §f" + Utils.locToString(l, true, false, true) + " §r!");
                                timer--;
                            }
                        }.runTaskTimer(Main.instance, 0, 20);
                    } catch (NumberFormatException ignored) {
                        u.err(CmdUtils.err_number_format);
                    }
                else
                    u.err(CmdUtils.err_not_enough_args);

            else {
                int times = 1;
                boolean modified = false;
                if (args.length >= 2)
                    try {
                        times = Integer.parseInt(args[0]);
                        modified = true;
                    } catch (NumberFormatException ignored) {
                    }
                LinkedHashSet<Player> players = CmdUtils.getPlayersFromArray(args, modified ? 1 : 0);
                int finalTimes = times;
                new BukkitRunnable() {
                    int timer = finalTimes;

                    @Override
                    public void run() {
                        players.forEach(player -> {
                            if (player.isOnline()) {
                                player.getWorld().strikeLightning(player.getLocation());
                                u.succ("§6" + player.getDisplayName() + "§r a été §efoudroyé§r !" + timer);
                            }
                        });
                        timer--;
                        if (timer <= 0)
                            cancel();
                    }
                }.runTaskTimer(Main.instance, 0, 20);
            }

        else
            u.synt();

        return false;
    }

    public static ItemStack getMjolnir() {
        ItemStack is = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = is.getItemMeta();
        String c = "§8", s = "§6";
        meta.setDisplayName(c + "[" + s + "§lLegendary" + c + "] - " + s + "Mjöllnir");
        meta.setLore(Arrays.asList("", c + "----------------", "",
                s + "Mjöllnir" + c + " est le marteau §d§oLégendaire",
                c + "appartenant au §fDieu du Tonnerre",
                c + "de la Mythologie Nordique : Thor.",
                c + "En tant que dieu guerrier le plus",
                c + "puissant, il évoque la §cforce" + c + ",",
                c + "le §acourage" + c + " et la §9vertu" + c + ".",
                c + "Cette arme utilisable par un §lDieu",
                c + "§luniquement" + c + " est l'arme Nordique",
                c + "la plus puissante.", "", "§c[§lClic Droit§c] - Tonnerre"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.spigot().setUnbreakable(true);
        is.setItemMeta(meta);

        return new CustomNBT(is).setBoolean(NBTName, true).build();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        LinkedHashSet<Player> content = CmdUtils.getPlayersFromArray(args, 0);
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if (args.length == 1) {
                add("-rod");
                add("-pos");
            } else if (args[0].equalsIgnoreCase("-pos") && sender instanceof Player) {
                Location loc;
                Block target = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 10);
                if (target != null && !target.getType().equals(Material.AIR))
                    loc = target.getLocation().add(0, 1, 0);
                else
                    loc = ((Player) sender).getLocation();
                if (args.length == 2)
                    add(loc.getX() + "");
                else if (args.length == 3)
                    add(loc.getY() + "");
                else if (args.length == 4)
                    add(loc.getZ() + "");
            }
            if (args.length == 1 || !args[0].equalsIgnoreCase("-pos")) {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (content.contains(p))
                        add("!" + p.getName());
                    else
                        add(p.getName());
                });
                if (!content.isEmpty())
                    add("!@a");
                if (content.size() != Bukkit.getOnlinePlayers().size())
                    add("@a");
            }
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }

}
