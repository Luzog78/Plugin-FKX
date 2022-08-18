package fr.luzog.pl.fkx.commands.Other;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.GUIs.GuiAd;
import fr.luzog.pl.fkx.utils.Broadcast;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Ad implements CommandExecutor, TabCompleter, Listener {
    public static final String AD_PREFIX = "§8§l[§2Ad§8§l] >>  ",
            SYS_NAME = "§4§lSYSTEM";
    public static ArrayList<Item> ads = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("000");

    public static Config getConfig() {
        return new Config("Ads.yml", true);
    }

    public static void initFromConfig() {
        getConfig().load().save().getMapList("ads").forEach(map -> {
            Item i = new Item(null, null, null);
            if (map.containsKey("id"))
                try {
                    i.setId(Integer.parseInt(map.get("id") + ""));
                } catch (NumberFormatException e) {
                    i.setId(null);
                }
            if (map.containsKey("sender"))
                i.setSender(map.get("sender") == null ? null : map.get("sender") + "");
            if (map.containsKey("admin"))
                i.setAdmin(map.get("admin") == null ? null : map.get("admin") + "");
            if (map.containsKey("message"))
                i.setMessage(map.get("message") == null ? null : map.get("message") + "");
            if (map.containsKey("insistence"))
                try {
                    i.setInsistence(Integer.parseInt(map.get("insistence") + ""));
                } catch (NumberFormatException ignore) {
                }
            if (map.containsKey("state") && Arrays.stream(State.values()).map(Enum::name)
                    .collect(Collectors.toList()).contains((map.get("state") + "").toUpperCase()))
                i.setState(State.valueOf((map.get("state") + "").toUpperCase()));
            if (map.containsKey("date"))
                try {
                    i.setDate(new Date(Long.parseLong(map.get("date") + "")));
                } catch (NumberFormatException ignore) {
                    i.setDate(new Date(0));
                }
            else
                i.setDate(new Date(0));
            if (i.getSender() != null && i.getDate() != null && i.getState() != null)
                ads.add(i);
        });
        sortAds();
    }

    public static void saveToConfig() {
        ArrayList<HashMap<String, Object>> a = new ArrayList<>();

        ads.forEach(ad -> a.add(new HashMap<String, Object>() {{
            put("id", ad.getId());
            put("sender", ad.getSender());
            put("admin", ad.getAdmin());
            put("message", ad.getMessage());
            put("insistence", ad.getInsistence());
            put("state", ad.getState().name());
            put("date", ad.getDate().getTime());
        }}));

        getConfig().load().set("ads", a, true).save();
    }

    public static enum State {WAITING, ACCEPTED, CLOSED, IGNORED}

    public static class Item {
        private Integer id;
        private String sender, admin, message;
        private int insistence;
        private State state;
        private Date date;

        public Item(int id, String sender, String admin, String message, int insistence, State state, Date date) {
            this.id = id;

            this.sender = sender;
            this.admin = admin;
            this.message = message;

            this.insistence = insistence;
            this.state = state;
            this.date = date;
        }

        public Item(String sender, String admin, String message) {
            this.id = null;

            this.sender = sender;
            this.admin = admin;
            this.message = message;

            this.insistence = 1;
            this.state = State.WAITING;
            this.date = new Date();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Item))
                return false;
            Item o = (Item) other;
            return Objects.equals(sender, o.getSender())
                    && (id == null || o.getId() == null || Objects.equals(id, o.getId()))
                    && (message == null || o.getMessage() == null || Objects.equals(message, o.getMessage()))
                    && Objects.equals(state, o.getState());
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getInsistence() {
            return insistence;
        }

        public void setInsistence(int insistence) {
            this.insistence = insistence;
        }

        public void increaseInsistence(Item ad) {
            insistence++;
            if (message == null && ad != null)
                message = ad.getMessage();
            if (ad != null && ad.getAdmin() != null)
                admin = ad.getAdmin();
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public static void sortAds() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (Item ad : ads)
            if (ad.getId() != null)
                if (!ids.contains(ad.getId()))
                    ids.add(ad.getId());
                else
                    ad.setId(null);
        for (int i = 0; i < ads.size(); i++)
            if (ads.get(i).getId() == null)
                ads.get(i).setId(ads.stream().map(Item::getId)
                        .filter(Objects::nonNull)
                        .max(Integer::compare)
                        .orElse(-1) + 1);
        ads.sort(Comparator.comparingInt(Item::getId));
    }

    public static Item get(int id) {
        return ads.stream().filter(ad -> ad.getId() == id).findFirst().orElse(null);
    }

    public static Item get(Item ad) {
        ArrayList<Item> tempAds = new ArrayList<>(ads);
        Collections.reverse(tempAds);
        for (Item a : tempAds)
            if (Objects.equals(a, ad))
                return a;
        return null;
    }

    public static boolean has(Item ad) {
        for (Item a : ads)
            if (a.equals(ad))
                return true;
        return false;
    }

    public static void post(CmdUtils u, Item ad) {
        if (has(ad)) {
            Objects.requireNonNull(get(ad)).increaseInsistence(ad); // Don't worry, it's 100% Safe !
            u.succ("Nous avons rapellé le staff de votre demande, encore un peu de patience...");
        } else {
            ads.add(ad);
            u.succ("Votre demande a bien été envoyée"
                    + (ad.getAdmin() == null ? "" : " à §4" + ad.getAdmin()) + " !");
            u.succ("Nous prenons en charge votre demande," +
                    " patientez quelques insant en attendant une réponse.");
        }
        if (FKManager.getCurrentGame() != null)
            FKManager.getCurrentGame().getGods().getPlayers().forEach(p -> {
                if (p.getPlayer() != null) {
                    p.getPlayer().sendMessage(AD_PREFIX + "§aRequêtes disponibles pour les grands maîtres de la part de §6"
                            + ad.getSender() + "...");
                    p.getPlayer().sendMessage(AD_PREFIX + "§7Un petit §8/ad help§7 pourrais vous être util. ^^");
                    List<Sound> sounds = Arrays.asList(Sound.GHAST_CHARGE, /* Sound.GHAST_DEATH, Sound.GHAST_FIREBALL, */
                            Sound.GHAST_MOAN, Sound.GHAST_SCREAM, Sound.GHAST_SCREAM2);
                    p.getPlayer().playSound(p.getPlayer().getLocation(), sounds.get(new Random().nextInt(sounds.size())), 1, 1);
                }
            });
        sortAds();
        saveToConfig();
    }

    public static void rem(Item ad) {
        if (has(ad)) {
            ads.remove(get(ad));
            saveToConfig();
        }
    }

    public static final String syntaxe_player = "/ad [help | [<admin>] [<message...>]]";
    public static final String syntaxe_admin = "/ad [help | list | all | page [[+/-]<filter>] <page> | <status> <id> | ad [<admin>] [<message...>]]" +
            "\n§r  >  Filters : " + String.join(" / ", Arrays.stream(GuiAd.SortType.values()).map(Enum::name).toArray(String[]::new))
            + "\n§r  >  Status : " + String.join(" / ", Arrays.stream(State.values()).map(Enum::name).toArray(String[]::new));

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (FKManager.getCurrentGame() == null) {
            sender.sendMessage("§cAucune partie en cours...");
            sender.sendMessage("§cRéessayez plus tard !");
            return false;
        }

        boolean isPlayer = sender instanceof Player && FKManager.getCurrentGame().getGods().getPlayer(sender.getName()) == null;
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, isPlayer ? syntaxe_player : syntaxe_admin);

        if (args.length >= 1 && (args[0].equalsIgnoreCase("help") || args[0].equals("?"))) {
            u.synt();
            return false;
        }

        if (isPlayer)
            if (args.length == 0)
                post(u, new Item(u.getPlayer().getName(), null, null));
            else if (FKManager.getCurrentGame().getGods().getPlayer(args[0]) != null)
                if (args.length == 1)
                    post(u, new Item(u.getPlayer().getName(), args[0], null));
                else
                    post(u, new Item(u.getPlayer().getName(), args[0],
                            String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
            else
                post(u, new Item(u.getPlayer().getName(), null, String.join(" ", args)));

        else if (args.length == 0)
            Bukkit.dispatchCommand(sender, "ad page 0");
        else if (args[0].equalsIgnoreCase("all")) {
            u.succ("Liste des requêtes (§f" + ads.size() + "§r) :");
            for (int i = 0; i < ads.size(); i++)
                u.succ(" - §3#" + df.format(i) + "§7-" +
                        (ads.get(i).getInsistence() == 1 ? "§f1"
                                : ads.get(i).getInsistence() == 2 ? "§62"
                                : ads.get(i).getInsistence() == 3 ? "§c3"
                                : "§4" + ads.get(i).getInsistence())
                        + " §rde §6" + ads.get(i).getSender()
                        + "§r : §f" + (ads.get(i).getMessage() == null ? "§cnull"
                        : ads.get(i).getMessage().substring(0, Math.min(60, ads.get(i).getMessage().length()))));
        } else if (args[0].equalsIgnoreCase("list")) {
            ArrayList<String> waiting = new ArrayList<>();
            for (int i = 0; i < ads.size(); i++)
                if (ads.get(i).getState() == State.WAITING)
                    waiting.add(" - §3#" + df.format(i) + "§7-" +
                            (ads.get(i).getInsistence() == 1 ? "§f1"
                                    : ads.get(i).getInsistence() == 2 ? "§62"
                                    : ads.get(i).getInsistence() == 3 ? "§c3"
                                    : "§4" + ads.get(i).getInsistence())
                            + " §rde §6" + ads.get(i).getSender()
                            + "§r : §f" + (ads.get(i).getMessage() == null ? "§cnull"
                            : ads.get(i).getMessage().substring(0, Math.min(60, ads.get(i).getMessage().length()))));
            u.succ("Liste des requêtes en attente (§f" + waiting.size() + "§r) :");
            waiting.forEach(u::succ);
        } else if (args[0].equalsIgnoreCase("page")) {
            if (args.length == 1)
                Bukkit.dispatchCommand(sender, "ad page 0");
            else if (args.length == 2)
                Bukkit.dispatchCommand(sender, "ad page " + GuiAd.SortType.STATUS.name() + " " + args[1]);
            else
                try {
                    String arg = args[1].startsWith("-") || args[1].startsWith("+") ? args[1].substring(1) : args[1];
                    if (sender instanceof Player)
                        u.getPlayer().openInventory(GuiAd.getAdsInventory(GuiAd.SortType.valueOf(arg.toUpperCase()),
                                args[1].startsWith("-"), FKManager.getCurrentGame() == null ? null : "fk",
                                "ad page", Integer.parseInt(args[2])));
                    else
                        u.err(CmdUtils.err_not_player);
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format + " (" + args[2] + ")");
                } catch (IllegalArgumentException e) {
                    Bukkit.dispatchCommand(sender, "ad page +" + GuiAd.SortType.STATUS.name() + " " + args[2]);
                }
        } else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("accept")
                || args[0].equalsIgnoreCase("ignore") || args[0].equalsIgnoreCase("close")
                || args[0].equalsIgnoreCase("waiting"))
            try {
                int i = Integer.parseInt(args[1]);
                Item ad = ads.get(i);
                if (args[0].equalsIgnoreCase("info"))
                    u.succ("Informations de la requête §3#" + df.format(i) + "§r :"
                            + "\n§r - Date : §f" + new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(ads.get(i).getDate())
                            + "\n§r - Status : §f" + (ad.getState() == State.WAITING ? "§7EN ATTENTE"
                            : ad.getState() == State.ACCEPTED ? "§2PRIS EN CHARGE"
                            : ad.getState() == State.IGNORED ? "§8IGNORÉ"
                            : ad.getState() == State.CLOSED ? "§4FERMÉE" : ad.getState())

                            + "\n§r - Insistence : " + (ads.get(i).getInsistence() == 1 ? "§f1"
                            : ads.get(i).getInsistence() == 2 ? "§62"
                            : ads.get(i).getInsistence() == 3 ? "§c3"
                            : "§4" + ads.get(i).getInsistence())

                            + "\n§r - Joueur : §6" + ad.getSender()
                            + "\n§r - Admin : §f" + (ad.getAdmin() == null ? "§cnull" : ad.getAdmin())
                            + "\n§r - Description : §f" + (ad.getMessage() == null ? "§cnull" : ad.getMessage()));
                else if (args[0].equalsIgnoreCase("accept")) {
                    ad.setState(State.ACCEPTED);
                    u.succ("Requête §3#" + df.format(i) + "§r : §2Acceptée");
                    saveToConfig();
                } else if (args[0].equalsIgnoreCase("ignore")) {
                    ad.setState(State.IGNORED);
                    u.succ("Requête §3#" + df.format(i) + "§r : §8Ignorée");
                    saveToConfig();
                } else if (args[0].equalsIgnoreCase("close")) {
                    ad.setState(State.CLOSED);
                    u.succ("Requête §3#" + df.format(i) + "§r : §4Fermée");
                    saveToConfig();
                } else if (args[0].equalsIgnoreCase("waiting")) {
                    ad.setState(State.WAITING);
                    u.succ("Requête §3#" + df.format(i) + "§r : §7En attente");
                    saveToConfig();
                }
            } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException e) {
                u.synt();
                return false;
            }
        else if (args[0].equalsIgnoreCase("ad"))
            if (args.length == 1)
                post(u, new Item(sender instanceof Player ? u.getPlayer().getName() : SYS_NAME, null, null));
            else if (FKManager.getCurrentGame().getGods().getPlayer(args[1]) != null)
                if (args.length == 2)
                    post(u, new Item(sender instanceof Player ? u.getPlayer().getName() : SYS_NAME, args[1], null));
                else
                    post(u, new Item(sender instanceof Player ? u.getPlayer().getName() : SYS_NAME, args[1],
                            String.join(" ", Arrays.copyOfRange(args, 2, args.length))));
            else
                post(u, new Item(sender instanceof Player ? u.getPlayer().getName() : SYS_NAME, null,
                        String.join(" ", Arrays.copyOfRange(args, 1, args.length))));

        else
            u.synt();

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        boolean isPlayer = sender instanceof Player && FKManager.getCurrentGame().getGods().getPlayer(sender.getName()) == null;

        new ArrayList<String>() {{
            if (args.length == 1)
                add("help");

            if (isPlayer)
                addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));

            else if (args.length == 1)
                addAll(Arrays.asList("list", "all", "info", "accept", "ignore", "close", "waiting", "ad"));
            else if (args[0].equalsIgnoreCase("info"))
                for (int i = 0; i < ads.size(); i++)
                    add(df.format(i));
            else if (args[0].equalsIgnoreCase("accept")) {
                for (int i = 0; i < ads.size(); i++)
                    if (ads.get(i).getState() == State.WAITING)
                        add(df.format(i));
            } else if (args[0].equalsIgnoreCase("ignore")
                    || args[0].equalsIgnoreCase("close")) {
                for (int i = 0; i < ads.size(); i++)
                    if (ads.get(i).getState() == State.WAITING
                            || ads.get(i).getState() == State.ACCEPTED)
                        add(df.format(i));
            } else if (args[0].equalsIgnoreCase("waiting")) {
                for (int i = 0; i < ads.size(); i++)
                    if (ads.get(i).getState() != State.WAITING)
                        add(df.format(i));
            }
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
