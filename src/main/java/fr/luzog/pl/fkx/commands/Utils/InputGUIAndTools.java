package fr.luzog.pl.fkx.commands.Utils;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.*;
import fr.luzog.pl.fkx.utils.Items;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

public class InputGUIAndTools implements CommandExecutor, TabCompleter, Listener {
    public static final String syntaxe = "/input <command...>"
            + "\n§cL'argument 'command' peut supporter les variables suivantes :"
            + "\n§c - %s (texte), %s{i-j} (longueur de i à j),"
            + "\n§c - %d (entier), %d{i-j} (entre i et j),"
            + "\n§c - %f (nombre décimal), %f{k} (arrondi à k),"
            + "\n§c - %b (booléen)"
            + "\n§c - Coordonnées (avec tool) : %l (décimales), %L (entières)";
    public static final String title = "§8Veuillez entre une valeur pour : §b%s";

    private static final String isWandTag = "isWand", isWandableTag = "isWandable", wandResultTag = "wandResult",
            wandXTag = "wandX", wandYTag = "wandY", wandZTag = "wandZ",
            wandYawTag = "wandYaw", wandPitchTag = "wandPitch", wandWorldTag = "wandWorld";

    //                Player's UUID     Anvil Location    Cmd
    public static HashMap<UUID, Utils.Pair<Location, InputCommand>> inputs = new HashMap<>();
    public static final HashSet<Material> transparent = new HashSet<>(Arrays.asList(Material.AIR, Material.WATER,
            Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.FIRE, Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM, Material.RED_ROSE, Material.YELLOW_FLOWER, Material.LONG_GRASS));

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 0)
            u.synt();

        else if (sender instanceof Player) {
            if (inputs.containsKey(u.getPlayer().getUniqueId())) {
                u.err("Vous avez déjà une entrée en cours !");
                return false;
            } else if (inputs.values().stream().anyMatch(v ->
                    v.getKey().equals(u.getPlayer().getLocation().getBlock().getLocation()))) {
                u.err("Une autre entrée est déjà en cours à cet endroit !");
                return false;
            } else if (!transparent.contains(u.getPlayer().getLocation().getBlock().getType())) {
                u.err("Un bloc est déjà cet endroit !");
                return false;
            } else
                try {
                    String command = String.join(" ", args)
                            .replace("\\\\", "\uffff").replace("\\n", "\n").replace("%n", "\n").replace("\uffff", "\\");
                    u.getPlayer().closeInventory();
                    InputCommand ic = new InputCommand(command);
                    inputs.put(u.getPlayer().getUniqueId(),
                            new Utils.Pair<>(u.getPlayer().getLocation().getBlock().getLocation(), ic));
                    openAnvil(u.getPlayer(), u.getPlayer().getLocation(), ic);
                    u.succ("Vous avez ouvert §fla Boîte de saisie§r !");
                } catch (NumberFormatException e) {
                    u.err("Le premier argument doit être un nombre !");
                    u.synt();
                }
        } else
            u.err(CmdUtils.err_not_player);

        return false;
    }

    public static enum InputRawType {
        STR("Texte", "%s", true),
        INT("Entier", "%d", true),
        DOUBLE("Décimal", "%f", true),
        BOOLEAN("Booléen", "%b", false),
        LOCATION("Coordonnées", "%l", true);

        private final String display, syntaxe;
        private final boolean admitParams;

        InputRawType(String display, String syntaxe, boolean admitParams) {
            this.display = display;
            this.syntaxe = syntaxe;
            this.admitParams = admitParams;
        }

        public String getDisplay() {
            return display;
        }

        public String getSyntaxe() {
            return syntaxe;
        }

        public boolean admitParams() {
            return admitParams;
        }

        public static InputRawType getBySyntaxe(String s) {
            for (InputRawType t : values())
                if (s.startsWith(t.getSyntaxe()))
                    return t;
            return null;
        }
    }

    public static class InputType {

        private final InputRawType rawType;
        private final String syntaxe;
        private final String[] params;

        InputType(String syntaxe) {
            this.rawType = InputRawType.getBySyntaxe(syntaxe);
            this.syntaxe = syntaxe;
            String[] params = new String[0];
            if (rawType != null) {
                if (rawType.admitParams()) {
                    String s = syntaxe.substring(rawType.getSyntaxe().length());
                    if (s.startsWith("{") && s.endsWith("}")) {
                        s = s.substring(1, s.length() - 1);
                        params = s.split(",", -1);
                    }
                }
            }
            this.params = params;
        }

        @Override
        public String toString() {
            return (rawType == null ? "None" : rawType.getDisplay())
                    + (params.length == 0 ? "" : " (" + String.join(", ", params) + ")");
        }

        public InputRawType getRawType() {
            return rawType;
        }

        public String getSyntaxe() {
            return syntaxe;
        }

        public String[] getParams() {
            return params;
        }

        public Object matchWith(String s) {
            return matchWith(this, s);
        }

        public static Object matchWith(InputType type, String s) {
            switch (type.getRawType()) {
                case STR:
                    return s;
                case INT:
                    try {
                        int i = Integer.parseInt(s);
                        Integer min = null, max = null;
                        if (type.getParams().length >= 2) {
                            try {
                                min = Integer.parseInt(type.getParams()[0]);
                            } catch (NumberFormatException ignored) {
                            }
                            try {
                                max = Integer.parseInt(type.getParams()[1]);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        if ((min != null && i < min) || (max != null && i > max))
                            return null;
                        return i;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                case DOUBLE:
                    try {
                        double d = Double.parseDouble(s);
                        Integer rounding = null;
                        if (type.getParams().length >= 1) {
                            try {
                                rounding = Integer.parseInt(type.getParams()[0]);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        StringBuilder format = new StringBuilder();
                        if (rounding != null)
                            for (int i = 0; i < Math.max(0, rounding); i++)
                                format.append("0");
                        DecimalFormat df = new DecimalFormat("0" + (format.length() > 0 ? "." + format : ""));
                        df.setRoundingMode(RoundingMode.HALF_UP);
                        return Double.parseDouble(df.format(d).replace(",", "."));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                case BOOLEAN:
                    if (Arrays.asList("true", "1", "yes", "oui", "y", "t").contains(s.toLowerCase()))
                        return true;
                    else if (Arrays.asList("false", "0", "no", "non", "n", "f").contains(s.toLowerCase()))
                        return false;
                    else
                        return null;
                case LOCATION:
                    String[] split = s.split(" ");
                    String[] params = type.getParams().length == 0 ?
                            new String[]{"x", "y", "z"} : type.getParams();
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < params.length; i++) {
                        String param = params[i];
                        if (split.length <= i)
                            continue;
                        String arg = split[i];
                        try {
                            Object converted = param.equals("x") || param.equals("y") || param.equals("z")
                                    ? Double.parseDouble(arg)
                                    : param.equals("X") || param.equals("Y") || param.equals("Z")
                                    ? Integer.parseInt(arg)
                                    : param.equalsIgnoreCase("yw") || param.equalsIgnoreCase("pi")
                                    ? Float.parseFloat(arg)
                                    : param.equalsIgnoreCase("w")
                                    ? arg : "";
                            result.append(converted).append(" ");
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                    return result.toString().trim();
                default:
                    return null;
            }
        }
    }

    public static class InputCommand {
        private final String command;
        private final InputType[] types;
        private final Object[] results;

        private final String noFormat;
        private final int[] indexes;

        public InputCommand(String command) {
            this.command = command;

            ArrayList<Utils.Pair<Integer, InputType>> list = new ArrayList<>();
            StringBuilder noFormat = new StringBuilder(), temp = new StringBuilder();
            for (int i = 0; i < command.length(); i++) {
                if (temp.length() > 0) {
                    if (command.charAt(i) == '%' || command.charAt(i) == ' ') {
                        // Handle format
                        InputType type = new InputType(temp.toString());
                        if (type.getRawType() != null) {
                            // Add to results
                            list.add(new Utils.Pair<>(noFormat.length(), type));
                        } else {
                            noFormat.append(temp);
                        }
                        temp = new StringBuilder();
                        temp.append(command.charAt(i));
                    } else if (command.charAt(i) == '}') {
                        if (temp.toString().contains("{")) {
                            temp.append(command.charAt(i));
                            // Handle format
                            InputType type = new InputType(temp.toString());
                            if (type.getRawType() != null) {
                                // Add to results
                                list.add(new Utils.Pair<>(noFormat.length(), type));
                            } else {
                                noFormat.append(temp);
                            }
                        } else {
                            noFormat.append(temp);
                        }
                        temp = new StringBuilder();
                    } else if (command.charAt(i) == '{') {
                        if (temp.toString().contains("{")) {
                            temp.append(command.charAt(i));
                            noFormat.append(temp);
                            temp = new StringBuilder();
                        } else {
                            temp.append(command.charAt(i));
                        }
                    } else {
                        temp.append(command.charAt(i));
                    }
                } else if (command.charAt(i) == '%') {
                    temp.append(command.charAt(i));
                } else {
                    noFormat.append(command.charAt(i));
                }
            }
            // Handle - last time
            InputType type = new InputType(temp.toString());
            if (type.getRawType() != null) {
                // Add to results
                list.add(new Utils.Pair<>(noFormat.length(), type));
            } else {
                noFormat.append(temp);
            }

            this.types = new InputType[list.size()];
            this.results = new Object[list.size()];

            this.noFormat = noFormat.toString();
            this.indexes = new int[list.size()];

            for (int i = 0; i < list.size(); i++) {
                Utils.Pair<Integer, InputType> pair = list.get(i);
                this.types[i] = pair.getValue();
                this.indexes[i] = pair.getKey();
            }
        }

        public String getCommand() {
            return command;
        }

        public InputType[] getTypes() {
            return types;
        }

        public Object[] getResults() {
            return results;
        }

        public String getNoFormat() {
            return noFormat;
        }

        public int[] getIndexes() {
            return indexes;
        }

        public int getLength() {
            return types.length;
        }

        public boolean isComplete() {
            for (Object o : results)
                if (o == null)
                    return false;
            return true;
        }

        public int getRemaining() {
            int i = 0;
            for (Object result : results)
                if (result == null)
                    i++;
            return i;
        }

        public int getActual() {
            for (int i = 0; i < results.length; i++)
                if (results[i] == null)
                    return i;
            return -1;
        }

        public Object addResult(String s) {
            Object o = types[getActual()].matchWith(s);
            results[getActual()] = o;
            return o;
        }

        public Object addResult(String s, boolean force) {
            if (force) {
                results[getActual()] = s;
                return s;
            }
            return addResult(s);
        }

        public String build() {
            StringBuilder sb = new StringBuilder(noFormat);

            for (int i = types.length - 1; i >= 0; i--) {
                String s = results[i] + "";
                if (s.equals("") && sb.charAt(indexes[i]) == ' ' && (indexes[i] == 0 || sb.charAt(indexes[i] - 1) == ' '))
                    sb.replace(indexes[i], indexes[i] + 1, "");
                else
                    sb.insert(indexes[i], s);
            }

            return sb.toString();
        }
    }

    public static void openAnvil(Player p, Location loc, InputCommand command) {
        if (!inputs.containsKey(p.getUniqueId()))
            return;
//                ((CraftPlayer) u.getPlayer()).getHandle().playerConnection
//                        .sendPacket(new PacketPlayOutOpenWindow(((CraftPlayer) u.getPlayer())
//                                .getHandle().nextContainerCounter(), "minecraft:anvil",
//                                new ChatMessage("Repairing"), 0));
        p.getLocation().getBlock().setType(Material.ANVIL, false);
        EntityPlayer pp = ((CraftPlayer) p).getHandle();
        AnvilContainer container = new AnvilContainer(pp, loc, command);
        int c = pp.nextContainerCounter();
        pp.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
        pp.activeContainer = container;
        pp.activeContainer.windowId = c;
        pp.activeContainer.addSlotListener(pp);
        p.getLocation().getBlock().setType(Material.AIR, false);
    }

    public static class AnvilContainer extends ContainerAnvil {

        public AnvilContainer(EntityHuman entity, Location loc, InputCommand command) {
            super(entity.inventory, entity.world, new BlockPosition(
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), entity);
            boolean isWandable = command.getTypes()[command.getActual()].getRawType() == InputRawType.LOCATION;
            super.setItem(0, CraftItemStack.asNMSCopy(Items.builder(isWandable ? Material.WOOD_AXE : Material.PAPER)
                    .setName(">")
                    .setLore(
                            "§8" + Utils.loreSeparator,
                            " ",
                            "  §7Veuillez entrer une valeur pour",
                            "  §7 l'argument §f" + (command.getActual() + 1) + "§7 sur §8" + command.getLength() + "§7,",
                            "  §7 de type : §b§o" + command.getTypes()[command.getActual()].toString() + "§7.",
                            " ",
                            "  §7Notez qu'il faut remplacer le '>'",
                            "  §7 sinon, il sera pris en compte.",
                            "  §7Par ailleurs, si vous ne souhaitez",
                            "  §7 préciser un argument, supprimez le tout.",
                            " " + (!isWandable ? "" : ""
                                    + "\n  §dVous pouvez utiliser le Tool"
                                    + "\n  §d pour sélectionner une position."
                                    + "\n  §d §l[Clic droit]§d pour récupérer"
                                    + "\n "),
                            "  §7Commande(s) :",
                            "  §7" + command.getCommand().replace("\n", "§8\\n\n  §7"),
                            (command.getActual() == 0 ? "" : " "
                                    + "  §7Résultats :"
                                    + "\n  §7 - §f" + Arrays.stream(command.getResults())
                                    .filter(Objects::nonNull)
                                    .map(Object::toString)
                                    .collect(Collectors.joining("\n  §7 - §f"))),
                            " ",
                            "§8" + Utils.loreSeparator
                    )
                    .getNBT()
                    .setBoolean(isWandableTag, isWandable)
                    .build()));
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            return true;
        }
    }

    public static Utils.Pair<ItemStack, String> getTool(InputCommand command, Double x, Double y, Double z,
                                                        Float yaw, Float pitch, String world) {
        DecimalFormat df = new DecimalFormat("0.0###", new DecimalFormatSymbols(Locale.US));
        df.setRoundingMode(RoundingMode.HALF_DOWN);
        DecimalFormat ddf = new DecimalFormat("0.0#", new DecimalFormatSymbols(Locale.US));
        ddf.setRoundingMode(RoundingMode.HALF_UP);

        Object xx = null, yy = null, zz = null, yw = null, pi = null, ww = null;

        String[] params = command.getTypes()[command.getActual()].getParams().length == 0 ?
                new String[]{"x", "y", "z"} : command.getTypes()[command.getActual()].getParams();
        StringBuilder result = new StringBuilder();
        for (String param : params) {
            Object converted = null;
            if (param.equals("x")) {
                if (x == null) {
                    xx = "§cnull";
                } else {
                    converted = df.format(x);
                    xx = converted;
                }
            } else if (param.equals("X")) {
                if (x == null) {
                    xx = "§cnull";
                } else {
                    converted = (int) Math.round(x);
                    if (xx == null)
                        xx = converted;
                }
            } else if (param.equals("y")) {
                if (y == null) {
                    yy = "§cnull";
                } else {
                    converted = df.format(y);
                    yy = converted;
                }
            } else if (param.equals("Y")) {
                if (y == null) {
                    yy = "§cnull";
                } else {
                    converted = (int) Math.round(y);
                    if (yy == null)
                        yy = converted;
                }
            } else if (param.equals("z")) {
                if (z == null) {
                    zz = "§cnull";
                } else {
                    converted = df.format(z);
                    zz = converted;
                }
            } else if (param.equals("Z")) {
                if (z == null) {
                    zz = "§cnull";
                } else {
                    converted = (int) Math.round(z);
                    if (zz == null)
                        zz = converted;
                }
            } else if (param.equalsIgnoreCase("yw")) {
                if (yaw == null) {
                    yw = "§cnull";
                } else {
                    converted = ddf.format(yaw);
                    yw = converted;
                }
            } else if (param.equalsIgnoreCase("pi")) {
                if (pitch == null) {
                    pi = "§cnull";
                } else {
                    converted = ddf.format(pitch);
                    pi = converted;
                }
            } else if (param.equals("w")) {
                if (world == null) {
                    ww = "§cnull";
                } else {
                    converted = world;
                    ww = world.equalsIgnoreCase("world") ? "§aOverworld"
                            : world.equalsIgnoreCase("world_nether") ? "§dNether"
                            : world.equalsIgnoreCase("world_the_end") ? "§5End"
                            : world;
                }
            }
            if (converted != null)
                result.append(converted).append(" ");
        }

        CustomNBT nbt = Items.builder(Material.WOOD_AXE)
                .setName("§bTool")
                .setLore(
                        "§8" + Utils.loreSeparator,
                        " ",
                        "  §7§nPosition enregistrée :",
                        " "
                                + (xx == null && yy == null && zz == null ? ""
                                : "\n    §7>> §8§l"
                                + (xx == null ? "" : "X")
                                + (xx != null && yy != null ? "/" : "")
                                + (yy == null ? "" : "Y")
                                + (yy != null && zz != null ? "/" : "")
                                + (zz == null ? "" : "Z")
                                + " : §d"
                                + (xx == null ? "" : xx)
                                + (xx != null && yy != null ? " §8/§d " : "")
                                + (yy == null ? "" : yy)
                                + (yy != null && zz != null ? " §8/§d " : "")
                                + (zz == null ? "" : zz))
                                + (yw == null && pi == null ? ""
                                : "\n    §7>> §8§l"
                                + (yw == null ? "" : "Yaw")
                                + (yw != null && pi != null ? "/" : "")
                                + (pi == null ? "" : "Pitch")
                                + " : §6"
                                + (yw == null ? "" : yw)
                                + (yw != null && pi != null ? " §8/§6 " : "")
                                + (pi == null ? "" : pi))
                                + (ww == null ? "" : "\n    §7>> §8§lMonde : §f" + ww),
                        "    §7>> §8§lFormat : §7" + String.join(" ", params),
                        " ",
                        " ",
                        "  §8§l[Clic gauche]§8 (Bloc) : §7§oSélect. la position",
                        "  §8§l[Clic droit]§8 (Bloc) : §7§oSélect. le spawn",
                        " ",
                        "  §8§l[Clic gauche]§8 (Air) : §7§oSélect. sa position",
                        "  §8§l[Clic droit]§8 (Air) : §7§oEnregistrer le Yaw/Pitch",
                        " ",
                        "  §8§l[Shift + Clic gauche]§8 : §7§oRéinitialiser",
                        "  §8§l[Shift + Clic droit]§8 : §7§oValider la position",
                        "  §8§l[Drop]§8 : §7§oAnnuler la sélection",
                        " ",
                        "  §7Commande(s) :",
                        "  §7" + command.getCommand().replace("\n", "§8\\n\n  §7"),
                        (command.getActual() == 0 ? "" : " "
                                + "  §7Résultats :"
                                + "\n  §7 - §f" + String.join("\n  §7 - §f",
                                Arrays.stream(command.getResults())
                                        .map(Object::toString)
                                        .collect(Collectors.toCollection(ArrayList::new)))),
                        "§8" + Utils.loreSeparator
                )
                .setUnbreakable(true)
                .getNBT()
                .setBoolean(isWandTag, true)
                .setString(wandResultTag, result.toString().trim());
        if (x != null)
            nbt.setDouble(wandXTag, x);
        if (y != null)
            nbt.setDouble(wandYTag, y);
        if (z != null)
            nbt.setDouble(wandZTag, z);
        if (yaw != null)
            nbt.setFloat(wandYawTag, yaw);
        if (pitch != null)
            nbt.setFloat(wandPitchTag, pitch);
        if (world != null)
            nbt.setString(wandWorldTag, world);

        String message = "§aPosition enregistrée :"
                + (xx == null && yy == null && zz == null ? ""
                : "\n  §7- §8§l"
                + (xx == null ? "" : "X")
                + (xx != null && yy != null ? "/" : "")
                + (yy == null ? "" : "Y")
                + (yy != null && zz != null ? "/" : "")
                + (zz == null ? "" : "Z")
                + " : §d"
                + (xx == null ? "" : xx)
                + (xx != null && yy != null ? " §8/§d " : "")
                + (yy == null ? "" : yy)
                + (yy != null && zz != null ? " §8/§d " : "")
                + (zz == null ? "" : zz))
                + (yw == null && pi == null ? ""
                : "\n  §7- §8§l"
                + (yw == null ? "" : "Yaw")
                + (yw != null && pi != null ? "/" : "")
                + (pi == null ? "" : "Pitch")
                + " : §6"
                + (yw == null ? "" : yw)
                + (yw != null && pi != null ? " §8/§6 " : "")
                + (pi == null ? "" : pi))
                + (ww == null ? "" : "\n  §7- §8§lMonde : §f" + ww);

        return new Utils.Pair<>(nbt.build(), message);
    }

    public static void complete(Player p) {
        try {
            p.closeInventory();
            int i = 0;
            for (String c : inputs.get(p.getUniqueId()).getValue().build().split("\n")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (c.equalsIgnoreCase("exit"))
                            p.closeInventory();
                        else if (!c.equalsIgnoreCase("null") && !c.equals(""))
                            p.performCommand(c);
                    }
                }.runTaskLater(Main.instance, i);
                i++;
            }
            inputs.remove(p.getUniqueId());
        } catch (Exception ex) {
            p.sendMessage("§cUne erreur est survenue !");
            inputs.remove(p.getUniqueId());
            System.out.println(Color.GREEN + Color.BOLD
                    + "[Niveau de gravité: 0] Un joueur execute une commande avec un formatage invalide.\n"
                    + "                                        Le message d'erreur n'est donc qu'à caractère informatif.");
            ex.printStackTrace();
            System.out.println("-----------------------------------" + Color.RESET);
        }
    }

    @EventHandler
    public void onForgeFalls(BlockPhysicsEvent e) {
        if ((e.getBlock().getType() == Material.ANVIL || e.getChangedType() == Material.ANVIL)
                && inputs.values().stream().map(Utils.Pair::getKey).anyMatch(l ->
                l != null && l.equals(e.getBlock().getLocation())))
            e.setCancelled(true);
    }

    @EventHandler
    public void onExit(InventoryCloseEvent e) {
        if (inputs.containsKey(e.getPlayer().getUniqueId())) {
            if (e.getInventory().getType() == InventoryType.ANVIL)
                e.getInventory().clear();
//            inputs.get(e.getPlayer().getUniqueId()).getA().getBlock().setType(Material.AIR, false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (e.getPlayer().getItemInHand() == null
                            || e.getPlayer().getItemInHand().getType() == Material.AIR
                            || !new CustomNBT(e.getPlayer().getItemInHand()).hasKey(isWandTag)
                            || !new CustomNBT(e.getPlayer().getItemInHand()).getBoolean(isWandTag)) {
                        inputs.remove(e.getPlayer().getUniqueId());
                        e.getPlayer().sendMessage("§cVous avez quitté la boîte de saisie !");
                    }
                }
            }.runTask(Main.instance);
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.ANVIL && e.getCurrentItem() != null) {
            CustomNBT tag = new CustomNBT(e.getCurrentItem());
            if (tag.hasKey(isWandTag) && tag.getBoolean(isWandTag)) {
                e.getWhoClicked().sendMessage("§cVous ne pouvez pas déplacer cet item ! (DROP pour annuler)");
                e.setCancelled(true);
                return;
            }
        }
        if (e.getInventory().getType() == InventoryType.ANVIL
                && inputs.containsKey(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            Utils.Pair<Location, InputCommand> in = inputs.get(e.getWhoClicked().getUniqueId());
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
//                in.getA().getBlock().setType(Material.AIR, false);

                if (new CustomNBT(e.getCurrentItem()).hasKey(isWandableTag)
                        && new CustomNBT(e.getCurrentItem()).getBoolean(isWandableTag)
                        && (e.getClick() == ClickType.RIGHT || e.getRawSlot() == 0)) {
                    e.getWhoClicked().closeInventory();
                    if (e.getWhoClicked().getItemInHand() != null
                            && e.getWhoClicked().getItemInHand().getType() != Material.AIR) {
                        e.getWhoClicked().sendMessage("§cVotre main est pleine !");
                    } else {
                        Utils.Pair<ItemStack, String> tool =
                                getTool(in.getValue(), null, null, null, null, null, null);
                        e.getWhoClicked().setItemInHand(tool.getKey());
                        e.getWhoClicked().sendMessage("§aVous avez récupéré le §dTool§a !");
                        e.getWhoClicked().sendMessage(tool.getValue());
                    }
                    return;
                }

                if (e.getRawSlot() == 2) {
                    String name = e.getCurrentItem().getItemMeta().getDisplayName();
                    if (!e.getCurrentItem().getItemMeta().hasDisplayName()) {
                        if (in.getValue().addResult("", true) == null) {
                            e.getWhoClicked().sendMessage("§cErreur : la valeur entrée n'est pas valide !");
                            return;
                        }
                    } else if (name.startsWith("\"") && name.endsWith("\"")) {
                        if (in.getValue().addResult(name.substring(1, name.length() - 1)) == null) {
                            e.getWhoClicked().sendMessage("§cErreur : la valeur entrée n'est pas valide !");
                            return;
                        }
                    } else {
                        if (in.getValue().addResult(name.replace("\\\\", "\uffff").replace("\\\"", "\"").replace("\uffff", "\\")) == null) {
                            e.getWhoClicked().sendMessage("§cErreur : la valeur entrée n'est pas valide !");
                            return;
                        }
                    }
                    if (in.getValue().isComplete()) {
                        complete((Player) e.getWhoClicked());
                    } else {
                        openAnvil((Player) e.getWhoClicked(), in.getKey(), in.getValue());
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onInteract(PlayerInteractEvent e) {
        CustomNBT tag;
        if (e.getItem() == null || !(tag = new CustomNBT(e.getItem())).hasKey(isWandTag) || !tag.getBoolean(isWandTag))
            return;
        e.setCancelled(true);

        if (!inputs.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage("§cVous n'êtes pas dans un protocol de saisie actuellement...");
            return;
        }

        if (e.getPlayer().isSneaking()) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                Utils.Pair<ItemStack, String> tool = getTool(inputs.get(
                        e.getPlayer().getUniqueId()).getValue(), null, null, null, null, null, null);
                e.getPlayer().setItemInHand(tool.getKey());
                e.getPlayer().sendMessage(tool.getValue());
            } else {
                String result = tag.hasKey(wandResultTag) ? tag.getString(wandResultTag) : null;
                if (result == null || result.isEmpty()) {
                    e.getPlayer().sendMessage("§cErreur : Veuillez sélectionner une position !");
                } else if (inputs.get(e.getPlayer().getUniqueId()).getValue().addResult(result) == null) {
                    e.getPlayer().sendMessage("§cErreur : Position incorrecte !");
                } else if (inputs.get(e.getPlayer().getUniqueId()).getValue().isComplete()) {
                    e.getPlayer().setItemInHand(null);
                    complete(e.getPlayer());
                } else {
                    e.getPlayer().setItemInHand(null);
                    openAnvil(e.getPlayer(), inputs.get(e.getPlayer().getUniqueId()).getKey(),
                            inputs.get(e.getPlayer().getUniqueId()).getValue());
                }
            }
        } else {
            Double x = null, y = null, z = null;
            Float yaw = null, pitch = null;
            String world = null;
            switch (e.getAction()) {
                case LEFT_CLICK_BLOCK:
                    x = (double) e.getClickedBlock().getX();
                    y = (double) e.getClickedBlock().getY();
                    z = (double) e.getClickedBlock().getZ();
                    world = e.getClickedBlock().getWorld().getName();
                    if (tag.hasKey(wandYawTag))
                        yaw = tag.getFloat(wandYawTag);
                    if (tag.hasKey(wandPitchTag))
                        pitch = tag.getFloat(wandPitchTag);
                    break;
                case RIGHT_CLICK_BLOCK:
                    Location loc = Utils.normalize(e.getClickedBlock().getLocation(), false);
                    x = loc.getX();
                    y = loc.getY() + 1;
                    z = loc.getZ();
                    world = loc.getWorld().getName();
                    if (tag.hasKey(wandYawTag))
                        yaw = tag.getFloat(wandYawTag);
                    if (tag.hasKey(wandPitchTag))
                        pitch = tag.getFloat(wandPitchTag);
                    break;
                case LEFT_CLICK_AIR:
                    x = e.getPlayer().getLocation().getX();
                    y = e.getPlayer().getLocation().getY();
                    z = e.getPlayer().getLocation().getZ();
                    world = e.getPlayer().getLocation().getWorld().getName();
                    if (tag.hasKey(wandYawTag))
                        yaw = tag.getFloat(wandYawTag);
                    if (tag.hasKey(wandPitchTag))
                        pitch = tag.getFloat(wandPitchTag);
                    break;
                case RIGHT_CLICK_AIR:
                    yaw = e.getPlayer().getLocation().getYaw();
                    pitch = e.getPlayer().getLocation().getPitch();
                    if (tag.hasKey(wandXTag))
                        x = tag.getDouble(wandXTag);
                    if (tag.hasKey(wandYTag))
                        y = tag.getDouble(wandYTag);
                    if (tag.hasKey(wandZTag))
                        z = tag.getDouble(wandZTag);
                    if (tag.hasKey(wandWorldTag))
                        world = tag.getString(wandWorldTag);
                    break;
            }
            Utils.Pair<ItemStack, String> tool = getTool(inputs.get(
                    e.getPlayer().getUniqueId()).getValue(), x, y, z, yaw, pitch, world);
            e.getPlayer().setItemInHand(tool.getKey());
            e.getPlayer().sendMessage(tool.getValue());
        }
    }

    @EventHandler
    public static void onDrop(PlayerDropItemEvent e) {
        CustomNBT tag;
        if (e.getItemDrop().getItemStack() == null
                || !(tag = new CustomNBT(e.getItemDrop().getItemStack())).hasKey(isWandTag)
                || !tag.getBoolean(isWandTag))
            return;
        e.setCancelled(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().setItemInHand(null);
            }
        }.runTask(Main.instance);
        if (inputs.containsKey(e.getPlayer().getUniqueId())) {
            openAnvil(e.getPlayer(), inputs.get(e.getPlayer().getUniqueId()).getKey(),
                    inputs.get(e.getPlayer().getUniqueId()).getValue());
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
