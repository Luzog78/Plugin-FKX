package fr.luzog.pl.fkx.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import javafx.util.Pair;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {

    /**
     * If the location is within the bounds of the two locations, return true
     * <br>
     * (If the locations are not in the same world or just null, return false)
     *
     * @param loc  The location you want to check if it's inside the cuboid.
     * @param loc1 The first location
     * @param loc2 The location of the second corner of the cuboid.
     *
     * @return A boolean value.
     */
    public static boolean isInside(Location loc, Location loc1, Location loc2) {
        if (loc == null || loc1 == null || loc2 == null || !loc.getWorld().getUID().equals(loc1.getWorld().getUID()) || !loc.getWorld().getUID().equals(loc2.getWorld().getUID()))
            return false;
        double maxX = Math.max(loc1.getX(), loc2.getX()), maxY = Math.max(loc1.getY(), loc2.getY()), maxZ = Math.max(loc1.getZ(), loc2.getZ()),
                minX = Math.min(loc1.getX(), loc2.getX()), minY = Math.min(loc1.getY(), loc2.getY()), minZ = Math.min(loc1.getZ(), loc2.getZ());
        return loc.getX() <= maxX && loc.getX() >= minX && loc.getY() <= maxY && loc.getY() >= minY && loc.getZ() <= maxZ && loc.getZ() >= minZ
                && (loc.getWorld().getUID().equals(loc1.getWorld().getUID()) || loc.getWorld().getUID().equals(loc2.getWorld().getUID()));
    }

    /**
     * It returns a list of all the blocks in the cuboid defined by the two locations
     * <br>
     * (If the locations are not in the same world or just null, the method will return an empty list)
     *
     * @param loc1 The first location
     * @param loc2 The second location
     *
     * @return A list of blocks
     */
    public static List<Block> getBlocksIn(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null || !loc1.getWorld().getUID().equals(loc2.getWorld().getUID()))
            return new ArrayList<>();
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX()), maxY = Math.max(loc1.getBlockY(), loc2.getBlockY()), maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ()),
                minX = Math.min(loc1.getBlockX(), loc2.getBlockX()), minY = Math.min(loc1.getBlockY(), loc2.getBlockY()), minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        List<Block> blocks = new ArrayList<>();
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    blocks.add(loc1.getWorld().getBlockAt(x, y, z));
        return blocks;
    }

    /**
     * Given a column and row, return the position of the cell in the inventory.
     *
     * @param col The column of the cell.
     * @param row The row of the cell you want to get the position of.
     *
     * @return The position of the cell in the array.
     */
    public static int posOf(int col, int row) {
        return row * 9 + col;
    }

    /**
     * Given a position, return the column and row of that position.
     *
     * @param position The position of the cell in the inventory.
     *
     * @return A pair of integers.
     */
    public static Pair<Integer, Integer> colAndRowOf(int position) {
        return new Pair<>(position % 9, position / 9);
    }

    public static List<Integer> zoneOf(int pos1, int pos2) {
        return zoneOf(pos1, pos2, false);
    }

    /**
     * It returns a list of all the positions in the zone between two positions, including the two positions themselves
     *
     * @param pos1 The first position.
     * @param pos2 The position of the second point.
     * @param wall If true, the zone will be a filled zone, if false, it will be an empty zone.
     *
     * @return A list of integers.
     */
    public static List<Integer> zoneOf(int pos1, int pos2, boolean wall) {
        int i1, j1, i2, j2;
        if (colAndRowOf(pos1).getKey() <= colAndRowOf(pos2).getKey()) {
            i1 = colAndRowOf(pos1).getKey();
            j1 = colAndRowOf(pos2).getKey();
        } else {
            i1 = colAndRowOf(pos2).getKey();
            j1 = colAndRowOf(pos1).getKey();
        }
        if (colAndRowOf(pos1).getValue() <= colAndRowOf(pos2).getValue()) {
            i2 = colAndRowOf(pos1).getValue();
            j2 = colAndRowOf(pos2).getValue();
        } else {
            i2 = colAndRowOf(pos2).getValue();
            j2 = colAndRowOf(pos1).getValue();
        }
        return new ArrayList<Integer>() {{
            for (int col = i1; col <= j1; col++)
                for (int row = i2; row <= j2; row++)
                    if (!wall || col == i1 || col == j1 || row == i2 || row == j2)
                        add(posOf(col, row));
        }};
    }

    /**
     * It fills an inventory with an itemstack from position 1 to position 2
     *
     * @param inv  The inventory you want to fill
     * @param pos1 The first position to start filling from.
     * @param pos2 The position to stop filling at.
     * @param is   The itemstack you want to fill the inventory with
     */
    public static void fill(Inventory inv, int pos1, int pos2, ItemStack is) {
        fill(inv, pos1, pos2, false, is);
    }

    /**
     * Fills the inventory with the given item stack from the given positions.
     *
     * @param inv  The inventory to fill
     * @param pos1 The first position of the zone.
     * @param pos2 The second position of the zone.
     * @param wall If true, the zone will be a filled zone, if false, it will be an empty zone.
     * @param is   The item to fill the inventory with
     */
    public static void fill(Inventory inv, int pos1, int pos2, boolean wall, ItemStack is) {
        zoneOf(pos1, pos2, wall).forEach(p -> inv.setItem(p, is));
    }

    /**
     * @param p    <strong style="color: #ff0000">null</strong> to Broadcast
     * @param sec  &nbsp; <code style="color: #00ff00"><0</code> &nbsp; to do instantly &nbsp;
     *             | &nbsp; <code style="color: #00ff00">0</code> &nbsp; to do instantly WITH last msg &nbsp;
     *             | &nbsp; <code style="color: #00ff00">>0</code> &nbsp; normal
     * @param text Use variable <strong style="color: #ffffff">%i%</strong> and use <strong style="color: #ffffff">§r</strong> to default color
     */
    public static void countDown(@Nullable Player p, int sec, boolean ascendant, boolean title, boolean chat, String text, @Nullable String end,
                                 String defaultColor, String warningColor, String criticalColor, String lastColor, String endColor, @Nullable Runnable r) {
        if (sec < 0) {
            if (r != null)
                r.run();
            return;
        }
        new BukkitRunnable() {
            int i = sec;

            @Override
            public void run() {
                String ii = (ascendant ? sec - i : i) + "";
                String s = ("§r" + (i == 0 ? end == null ? "" : end : text).replace("%i%", ii).replace("%I%", ii)).replace("\n", "\n§r")
                        .replace("§r", i == 5 || i == 4 ? warningColor : i == 3 || i == 2 ? criticalColor : i == 1 ? lastColor : i == 0 ? endColor : defaultColor);

                if (i != 0 || end != null)
                    if (p == null) {
                        if (chat)
                            Broadcast.log(s.contains("\n") ? s.split("\n", 2)[0] : s);
                        if (title)
                            Bukkit.getOnlinePlayers().forEach(pl ->
                                    pl.sendTitle(s.contains("\n") ? s.split("\n", 2)[0] : s, s.contains("\n") ? s.split("\n", 2)[1] : ""));
                    } else {
                        if (title)
                            p.sendTitle(s.contains("\n") ? s.split("\n", 2)[0] : s, s.contains("\n") ? s.split("\n", 2)[1] : "");
                        if (chat)
                            p.sendMessage(Main.PREFIX + (s.contains("\n") ? s.split("\n", 2)[0] : s));
                    }

                if (i == 0) {
                    this.cancel();
                    if (r != null)
                        r.run();
                    return;
                }

                i--;
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }

    /**
     * It takes a location, and returns a location that is the center of the block that the original location is in
     * (Y position included)
     *
     * @param loc The location to normalize
     *
     * @return A Location object
     */
    public static Location normalize(Location loc) {
        return normalize(loc, true);
    }

    /**
     * It takes a location, and returns a location that is the center of the block that the original location is in
     *
     * @param loc The location to normalize
     * @param y   If true, the y-coordinate will be normalized. If false, it will not.
     *
     * @return A Location object
     */
    public static Location normalize(Location loc, boolean y) {
        Location l = loc.clone();
        l.setX(((int) l.getX()) + 0.5);
        if (y)
            l.setY(((int) l.getY()) + 0.5);
        l.setZ(((int) l.getZ()) + 0.5);
        l.setYaw(0);
        l.setPitch(0);
        return l;
    }

    /**
     * It takes a list of strings and returns a packet that will set the header and footer of the tab list
     *
     * @param header The header of the tab list.
     * @param footer The footer of the tab list.
     *
     * @return A PacketPlayOutPlayerListHeaderFooter object.
     */
    public static PacketPlayOutPlayerListHeaderFooter getTabHeaderAndFooter(List<String> header, List<String> footer) {
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        Field a, b;
        try {
            a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return null;
        }

        String h = "";
        String f = "";
        if (header.isEmpty())
            try {
                a.set(packet, new ChatComponentText(""));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        else
            for (String hd : header)
                h += hd + "\n";
        if (footer.isEmpty())
            try {
                b.set(packet, new ChatComponentText(""));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        else
            for (String ft : footer)
                f += ft + "\n";
        try {
            a.set(packet, new ChatComponentText(h.substring(0, h.length() - 1)));
            b.set(packet, new ChatComponentText(f.substring(0, f.length() - 1)));
        } catch (StringIndexOutOfBoundsException ignore) {
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static String locToString(Location loc, boolean decimal, boolean ywPi, boolean world) {
        DecimalFormat df = new DecimalFormat(decimal ? "0.00" : "#");
        // Formatting the location of the player to a string.
        return (df.format(loc.getX()) + "  " + df.format(loc.getY()) + "  " + df.format(loc.getZ())
                + (ywPi ? "  (" + df.format(loc.getYaw()) + "  " + df.format(loc.getPitch()) + ")" : "")
                + (world ? "  " + (loc.getWorld().getName().equalsIgnoreCase("world") ? "§aOverWorld"
                : loc.getWorld().getName().equalsIgnoreCase("world_nether") ? "§dNether"
                : loc.getWorld().getName().equalsIgnoreCase("world_the_end") ? "§5End"
                : loc.getWorld().getName().equalsIgnoreCase("world")) : "") + "§r").replace(",", ".");
    }

    public static UUID parseUUID(String uuid) {
        try {
            if (uuid.length() == 32)
                return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }

    public static String sendGETRequest(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line).append("\n");
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRandomQuoteFromAPI() {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(sendGETRequest("https://api.quotable.io/random")); // Or https://zenquotes.io/api/random
            return obj.get("content") + "\n  - " + obj.get("author");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Pair<String, UUID> getNameAndUUIDFromMojangAPI(String username) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(sendGETRequest("https://api.mojang.com/users/profiles/minecraft/" + username));
            return new Pair<>(obj.get("name").toString(), parseUUID(obj.get("id").toString()));
        } catch (ParseException | RuntimeException e) {
            // Case of the username not existing ->> GET response is null                        ->> ParseException
            // Case of the username is invalid   ->> GET response code: 400 and contains "error" ->> RuntimeException
            return null;
        }
    }

    public static MojangProfile getProfileFromMojangAPI(UUID uuid) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(sendGETRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + (uuid + "").replace("-", "") + "?unsigned=false"));
            JSONObject prop = (JSONObject) ((JSONArray) obj.get("properties")).get(0);
            return new MojangProfile(parseUUID(obj.get("id").toString()), obj.get("name").toString(), prop.get("value").toString(), prop.get("signature").toString());
        } catch (ParseException | RuntimeException e) {
            // Case of the uuid not existing ->> GET response is null                        ->> ParseException
            // Case of the uuid is invalid   ->> GET response code: 400 and contains "error" ->> RuntimeException
            return null;
        }
    }

    public static class MojangProfile {
        private UUID uuid;
        private String name;
        private String rawTextures;
        private String rawSignature;
        private long timestamp;
        private String profileId;
        private String profileName;
        private String skinURL;
        private String capeURL;

        public MojangProfile(UUID uuid, String name, String rawTextures, String rawSignature) {
            this.uuid = uuid;
            this.name = name;
            this.rawTextures = rawTextures;
            this.rawSignature = rawSignature;

            timestamp = 0;
            profileId = null;
            profileName = null;
            skinURL = null;
            capeURL = null;

            try {
                JSONObject obj = (JSONObject) new JSONParser().parse(new String(Base64.decode(this.rawTextures)));
                if (obj.containsKey("timestamp"))
                    timestamp = Long.parseLong(obj.get("timestamp").toString());
                if (obj.containsKey("profileId"))
                    profileId = obj.get("profileId").toString();
                if (obj.containsKey("profileName"))
                    profileName = obj.get("profileName").toString();
                if (obj.containsKey("textures")) {
                    JSONObject textures = (JSONObject) obj.get("textures");
                    if (textures.containsKey("SKIN")) {
                        JSONObject skin = (JSONObject) textures.get("SKIN");
                        if (skin.containsKey("url"))
                            skinURL = skin.get("url").toString();
                    }
                    if (textures.containsKey("CAPE")) {
                        JSONObject cape = (JSONObject) textures.get("CAPE");
                        if (cape.containsKey("url"))
                            capeURL = cape.get("url").toString();
                    }
                }
            } catch (ParseException ignored) {
            }
        }

        @Override
        public String toString() {
            return "MojangProfile{" +
                    "uuid=" + uuid +
                    ", name='" + name + '\'' +
                    ", rawTextures='" + rawTextures + '\'' +
                    ", rawSignature='" + rawSignature + '\'' +
                    ", timestamp=" + timestamp +
                    ", profileId='" + profileId + '\'' +
                    ", profileName='" + profileName + '\'' +
                    ", skinURL='" + skinURL + '\'' +
                    ", capeURL='" + capeURL + '\'' +
                    '}';
        }

        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRawTextures() {
            return rawTextures;
        }

        public void setRawTextures(String rawTextures) {
            this.rawTextures = rawTextures;
        }

        public String getRawSignature() {
            return rawSignature;
        }

        public void setRawSignature(String rawSignature) {
            this.rawSignature = rawSignature;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getProfileId() {
            return profileId;
        }

        public void setProfileId(String profileId) {
            this.profileId = profileId;
        }

        public String getProfileName() {
            return profileName;
        }

        public void setProfileName(String profileName) {
            this.profileName = profileName;
        }

        public String getSkinURL() {
            return skinURL;
        }

        public void setSkinURL(String skinURL) {
            this.skinURL = skinURL;
        }

        public String getCapeURL() {
            return capeURL;
        }

        public void setCapeURL(String capeURL) {
            this.capeURL = capeURL;
        }
    }

    /**
     * Try to run the given runnable, and return true if it succeeded, or false if it failed.
     *
     * @param printStackTrace If true, print in console errors.
     * @param r               The runnable to run.
     *
     * @return A boolean value.
     */
    public static boolean tryTo(boolean printStackTrace, Runnable r) {
        try {
            r.run();
            return true;
        } catch (Exception e) {
            if (printStackTrace) {
                System.out.println(Color.RED);
                e.printStackTrace();
                System.out.println(Color.RESET);
            }
            return false;
        }
    }


    /**
     * This function returns an ArrayList of all the players in the current game and in the server.
     *
     * @return An {@link ArrayList} of {@link String}s
     */
    public static ArrayList<String> getAllPlayers() {
        return getAllPlayers(FKManager.getCurrentGame());
    }

    /**
     * It gets all the players in the game and in the server, and returns them as an ArrayList
     *
     * @param manager The {@link FKManager} object that you want to get the players from.
     *
     * @return A list of all players in the game.
     */
    public static ArrayList<String> getAllPlayers(FKManager manager) {
        HashSet<String> players = new HashSet<>();
        if (manager != null && manager.getPlayers() != null)
            players.addAll(manager.getPlayers().stream().map(FKPlayer::getName).collect(Collectors.toList()));
        players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        return new ArrayList<>(players);
    }

    public static ChatColor dyeToChatColor(DyeColor color) {
        return dataToChatColor(color.getData());
    }

    public static DyeColor chatToDyeColor(ChatColor color) {
        return dataToDyeColor(chatToDataColor(color));
    }

    public static ChatColor dataToChatColor(int color) {
        switch (color) {
            case 0:
                return ChatColor.WHITE;
            case 1:
                return ChatColor.GOLD;
            case 2:
                return ChatColor.LIGHT_PURPLE;
            case 3:
                return ChatColor.BLUE;
            case 4:
                return ChatColor.YELLOW;
            case 5:
                return ChatColor.GREEN;
            case 6:
                return ChatColor.AQUA;
            case 7:
                return ChatColor.GRAY;
            case 8:
                return ChatColor.DARK_GRAY;
            case 9:
                return ChatColor.DARK_AQUA;
            case 10:
                return ChatColor.DARK_PURPLE;
            case 11:
                return ChatColor.DARK_BLUE;
            case 12:
                return ChatColor.DARK_RED;
            case 13:
                return ChatColor.DARK_GREEN;
            case 14:
                return ChatColor.RED;
            case 15:
                return ChatColor.BLACK;
            default:
                return null;
        }
    }

    public static DyeColor dataToDyeColor(int color) {
        switch (color) {
            case 0:
                return DyeColor.WHITE;
            case 1:
                return DyeColor.ORANGE;
            case 2:
                return DyeColor.MAGENTA;
            case 3:
                return DyeColor.LIGHT_BLUE;
            case 4:
                return DyeColor.YELLOW;
            case 5:
                return DyeColor.LIME;
            case 6:
                return DyeColor.PINK;
            case 7:
                return DyeColor.GRAY;
            case 8:
                return DyeColor.SILVER;
            case 9:
                return DyeColor.CYAN;
            case 10:
                return DyeColor.PURPLE;
            case 11:
                return DyeColor.BLUE;
            case 12:
                return DyeColor.BROWN;
            case 13:
                return DyeColor.GREEN;
            case 14:
                return DyeColor.RED;
            case 15:
                return DyeColor.BLACK;
            default:
                return null;
        }
    }

    public static byte chatToDataColor(ChatColor color) {
        switch (color) {
            case WHITE:
                return 0;
            case GOLD:
                return 1;
            case LIGHT_PURPLE:
                return 2;
            case BLUE:
                return 3;
            case YELLOW:
                return 4;
            case GREEN:
                return 5;
            case AQUA:
                return 6;
            case GRAY:
                return 7;
            case DARK_GRAY:
                return 8;
            case DARK_AQUA:
                return 9;
            case DARK_PURPLE:
                return 10;
            case DARK_BLUE:
                return 11;
            case DARK_RED:
                return 12;
            case DARK_GREEN:
                return 13;
            case RED:
                return 14;
            case BLACK:
                return 15;
            default:
                return -1;
        }
    }
}
