package fr.luzog.pl.fkx.commands.Utils;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Color;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class InputText implements CommandExecutor, TabCompleter, Listener {
    public static final String syntaxe = "/input <varCount> <command...>"
            + "\n§cL'argument 'command' peut supporter les variables de formatage java."
            + "\n§cExemple : %b, %d, %f, %x, %s  ||  %15s ou %-15.2f";
    public static final String title = "§8Veuillez entre une valeur pour : §b%s";

    //                Player's UUID      Anvil Location          Full Cmd      Results        Var Count
    public static HashMap<UUID, Utils.Triple<Location, Utils.Pair<String, ArrayList<Object>>, Integer>> inputs = new HashMap<>();
    public static final HashSet<Material> transparent = new HashSet<>(Arrays.asList(Material.AIR, Material.WATER,
            Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.FIRE, Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM, Material.RED_ROSE, Material.YELLOW_FLOWER, Material.LONG_GRASS));

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length <= 1)
            u.synt();

        else if (sender instanceof Player) {
            if (inputs.containsKey(u.getPlayer().getUniqueId())) {
                u.err("Vous avez déjà une entrée en cours !");
                return false;
            } else if (inputs.values().stream().anyMatch(v ->
                    v.getA().equals(u.getPlayer().getLocation().getBlock().getLocation()))) {
                u.err("Une autre entrée est déjà en cours à cet endroit !");
                return false;
            } else if (!transparent.contains(u.getPlayer().getLocation().getBlock().getType())) {
                u.err("Un bloc est déjà cet endroit !");
                return false;
            } else
                try {
                    int length = Integer.parseInt(args[0]);
                    String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                            .replace("\\\\", "\uffff").replace("\\n", "\n").replace("%n", "\n").replace("\uffff", "\\");
                    u.getPlayer().closeInventory();
                    inputs.put(u.getPlayer().getUniqueId(), new Utils.Triple<>(u.getPlayer().getLocation().getBlock().getLocation(),
                            new Utils.Pair<>(command, new ArrayList<>()), length));
                    openAnvil(u.getPlayer(), u.getPlayer().getLocation(), command, new ArrayList<>(), length);
                    u.succ("Vous avez ouvert §fla Boîte de saisie§r !");
                } catch (NumberFormatException e) {
                    u.err("Le premier argument doit être un nombre !");
                    u.synt();
                }
        } else
            u.err(CmdUtils.err_not_player);

        return false;
    }

    public static void openAnvil(Player p, Location loc,
                                 String command, ArrayList<Object> results, int length) {
        if (!inputs.containsKey(p.getUniqueId()))
            return;
//                ((CraftPlayer) u.getPlayer()).getHandle().playerConnection
//                        .sendPacket(new PacketPlayOutOpenWindow(((CraftPlayer) u.getPlayer())
//                                .getHandle().nextContainerCounter(), "minecraft:anvil",
//                                new ChatMessage("Repairing"), 0));
        p.getLocation().getBlock().setType(Material.ANVIL, false);
        EntityPlayer pp = ((CraftPlayer) p).getHandle();
        AnvilContainer container = new AnvilContainer(pp, loc, command, results, length);
        int c = pp.nextContainerCounter();
        pp.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
        pp.activeContainer = container;
        pp.activeContainer.windowId = c;
        pp.activeContainer.addSlotListener(pp);
        p.getLocation().getBlock().setType(Material.AIR, false);
    }

    public static class AnvilContainer extends ContainerAnvil {

        public AnvilContainer(EntityHuman entity, Location loc,
                              String cmd, ArrayList<Object> results, int length) {
            super(entity.inventory, entity.world, new BlockPosition(
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), entity);
            super.setItem(0, CraftItemStack.asNMSCopy(Items.builder(Material.PAPER)
                    .setName(">")
                    .setLore(
                            "§8" + Utils.loreSeparator,
                            " ",
                            "  §7Veuillez entrer une valeur pour",
                            "  §7 l'argument §f" + (results.size() + 1) + "§7 sur §8" + length + "§7.",
                            " ",
                            "  §7Notez qu'il faut remplacer le '>'",
                            "  §7 sinon, il sera pris en compte.",
                            "  §7Par ailleurs, si vous ne souhaitez",
                            "  §7 préciser un argument, entrez '.'",
                            " ",
                            "  §7Commande(s) :",
                            "  §7" + cmd.replace("\n", "§8\\n\n  §7"),
                            (results.size() > 0 ? " "
                                    + "  §7Résultats :"
                                    + "\n  §7 - §f" + String.join("\n  §7 - §f", results.stream()
                                    .map(Object::toString)
                                    .collect(Collectors.toCollection(ArrayList::new))) : ""),
                            " ",
                            "§8" + Utils.loreSeparator
                    )
                    .build()));
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            return true;
        }
    }

    @EventHandler
    public void onForgeFalls(BlockPhysicsEvent e) {
        if ((e.getBlock().getType() == Material.ANVIL || e.getChangedType() == Material.ANVIL)
                && inputs.values().stream().map(Utils.Triple::getA).anyMatch(l ->
                l != null && l.equals(e.getBlock().getLocation())))
            e.setCancelled(true);
    }

    @EventHandler
    public void onExit(InventoryCloseEvent e) {
        if (inputs.containsKey(e.getPlayer().getUniqueId())) {
            e.getInventory().clear();
//            inputs.get(e.getPlayer().getUniqueId()).getA().getBlock().setType(Material.AIR, false);
            inputs.remove(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage("§cVous avez quitté la boîte de saisie !");
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.ANVIL
                && inputs.containsKey(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            Utils.Triple<Location, Utils.Pair<String, ArrayList<Object>>, Integer>
                    in = inputs.get(e.getWhoClicked().getUniqueId());
            if (e.getRawSlot() == 2 && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
//                in.getA().getBlock().setType(Material.AIR, false);
                String name = e.getCurrentItem().getItemMeta().getDisplayName();
                if (name.equals("."))
                    in.getB().getValue().add("");
                else if (name.startsWith("\"") && name.endsWith("\""))
                    in.getB().getValue().add(name.substring(1, name.length() - 1));
                else
                    try {
                        in.getB().getValue().add(Integer.parseInt(name));
                    } catch (NumberFormatException ex) {
                        try {
                            in.getB().getValue().add(Double.parseDouble(name));
                        } catch (NumberFormatException exx) {
                            in.getB().getValue().add(name.replace("\\\\", "\uffff")
                                    .replace("\\\"", "\"").replace("\uffff", "\\"));
                        }
                    }
                if (in.getB().getValue().size() >= in.getC()) {
                    try {
                        e.getWhoClicked().closeInventory();
                        int i = 0;
                        for (String c : String.format(in.getB().getKey(),
                                in.getB().getValue().toArray(new Object[0])).split("\n")) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!c.equalsIgnoreCase("null") && !c.equals(""))
                                        ((Player) e.getWhoClicked()).performCommand(c);
                                }
                            }.runTaskLater(Main.instance, i);
                            i++;
                        }
                    } catch (Exception ex) {
                        e.getWhoClicked().sendMessage("§cUne erreur est survenue !");
                        inputs.remove(e.getWhoClicked().getUniqueId());
                        System.out.println(Color.GREEN + Color.BOLD
                                + "[Niveau de gravité: 0] Un joueur execute une commande avec un formatage invalide.\n"
                                + "                                        Le message d'erreur n'est donc qu'à caractère informatif.");
                        ex.printStackTrace();
                        System.out.println("-----------------------------------" + Color.RESET);
                    }
                } else {
                    openAnvil((Player) e.getWhoClicked(), in.getA(), in.getB().getKey(),
                            in.getB().getValue(), in.getC());
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if (args.length == 1)
                for (int i = 0; i < 10; i++)
                    add(i + "");
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }

}
