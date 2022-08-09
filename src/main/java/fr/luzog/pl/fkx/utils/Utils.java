package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
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
import java.util.*;
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
     * It returns the distance between two locations. (null if one of the locations is null)
     *
     * @param a             The first location
     * @param b             The second location
     * @param considerWorld If true, the method will return null if the two locations are in different worlds.
     *
     * @return The exact distance between the two locations.
     */
    public static Double safeDistance(Location a, Location b, boolean considerWorld) {
        if (a == null && b == null)
            return 0d;
        if (a == null || b == null)
            return null;
        double d = Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
        return !considerWorld || (a.getWorld() != null && b.getWorld() != null
                && Objects.equals(a.getWorld().getName(), b.getWorld().getName())) ? d : null;
    }

    /**
     * It returns the distance between two locations, or "x" if the locations are in different worlds
     *
     * @param a             The first location
     * @param b             The location to compare to.
     * @param considerWorld If true, the distance will be calculated between the two locations, even if they are in
     *                      different worlds.
     * @param precision     The number of decimal places to round to (0 to get an int).
     * @param radius        If distance <= radius, the distance will be xx.x
     *
     * @return The formatted distance between two locations.
     */
    public static String safeDistance(Location a, Location b, boolean considerWorld, int precision, double radius) {
        Double d = safeDistance(a, b, considerWorld);
        String s = precision <= 0 ? d == null || d <= radius ? "00" : Math.round(d) + ""
                : String.format("%." + precision + "f", d == null || d <= radius ? 0.0 : d);
        return (d == null || d <= radius ? s.replace("0", "x") : s).replace(",", ".");
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

    public static String getFormattedWorld(String world) {
        return world.equalsIgnoreCase("world") ? "§aOverWorld"
                : world.equalsIgnoreCase("world_nether") ? "§dNether"
                : world.equalsIgnoreCase("world_the_end") ? "§5End"
                : world;
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
                JSONObject obj = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(this.rawTextures)));
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

    /**
     * Let's make a <strong>ProgressBar</strong> !
     *
     * @param leftSide   The string to be placed to the left of the progress bar.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code style="color: #ff0000; font-weight: 900;">[</code><code>####------------]</code><br>
     * @param rightSide  The string to be appended to the right side of the progress bar.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code>[####------------</code><code style="color: #ff0000; font-weight: 900;">]</code><br>
     * @param fill       The character to use to fill the progress bar.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code>[<code style="color: #ff0000; font-weight: 900;">####</code>------------]</code><br>
     * @param empty      The character to use for the empty part of the bar.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code>[####<code style="color: #ff0000; font-weight: 900;">------------</code>]</code><br>
     * @param endFill    If progress is 100%, the bar will be filled with the fill character.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">100% - </code><code>[<code style="color: #00ff00; font-weight: 900;">****************</code>]</code><br>
     * @param startEmpty In contrary, at 0%, the bar will be filled with the empty character.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp; 0% - </code><code>[<code style="color: #0000ff; font-weight: 900;">~~~~~~~~~~~~~~~~</code>]</code><br>
     * @param length     The length of the progress bar (number of characters).<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code style="color: #aaaaaa;">[####------------]</code>
     *                   &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code style="color: #aaaaaa;">[##------]</code><br>
     * @param progress   The progress of the bar, from 0 to 1.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code style="color: #aaaaaa;">[##------]</code>
     *                   <code style="color: #00aa00">&nbsp;50% - </code><code style="color: #aaaaaa;">[####----]</code>
     *                   <code style="color: #00aa00">&nbsp;75% - </code><code style="color: #aaaaaa;">[######--]</code><br>
     * @param format     The format of the final bar with 5 vars: <b>{b}</b> | the formatted bar ; <b>{p}</b> | the percentage (int) ;
     *                   <b>{p.}</b> | the percentage (double #0.0) ; <b>{p..}</b> | the percentage (double #0.00) ;
     *                   <b>{p#}</b> | the progression (0 to 1 float) ; <b>{l}</b> | the length of the progress bar<br>
     *                   Example:
     *                   <ul>
     *                          <li><code style="color: #aa00aa">{p}% - {b} </code>&nbsp; &nbsp; <code style="color: #00aaaa">25% - [####------------]</code></li>
     *                          <li><code style="color: #aa00aa">{p.}% $ {b}</code>&nbsp; &nbsp; <code style="color: #00aaaa">25.00 $ [####------------]</code></li>
     *                          <li><code style="color: #aa00aa">{b} >> {p#}</code>&nbsp; &nbsp; <code style="color: #00aaaa">[####------------] >> 0.25</code></li>
     *                   </ul>
     *
     * @return A string that is a progress bar.
     */
    public static String progressBar(String leftSide, String rightSide, String fill, String empty, String endFill, String startEmpty,
                                     int length, float progress, String format) {
        StringBuilder sb = new StringBuilder();
        sb.append(leftSide);
        for (int i = 0; i <= Math.round(length * progress); i++) {
            sb.append(progress == 1 ? endFill : fill);
        }
        for (int i = 0; i < Math.round(length * (1 - progress)); i++) {
            sb.append(progress == 0 ? startEmpty : empty);
        }
        sb.append(rightSide);
        return format.replace("{b}", sb.toString()).replace("{p}", String.valueOf(Math.round(progress * 100)))
                .replace("{p.}", String.format("%.1f", progress * 100)).replace("{p..}", String.format("%.2f", progress * 100))
                .replace("{p#}", String.valueOf(progress)).replace("{l}", String.valueOf(length));
    }

    /**
     * Let's make a <strong>ProgressBar</strong> !
     *
     * @param leftSide   The string to be placed to the left of the progress bar.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code style="color: #ff0000; font-weight: 900;">[</code><code>####------------]</code><br>
     * @param rightSide  The string to be appended to the right side of the progress bar.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code>[####------------</code><code style="color: #ff0000; font-weight: 900;">]</code><br>
     * @param fillChars  The characters to use to fill the progress bar.<br>
     *                   Example: If we use <code style="color: #555555">[".", "*", "/", "#"]</code>, the fill char will be :<br>
     *                   &nbsp; >> &nbsp; <code style="color: #555555">"."</code> if progress is <code style="color: #00aa00">0%</code> to <code style="color: #00aa00">25%</code>,<br>
     *                   &nbsp; >> &nbsp; <code style="color: #555555">"*"</code> if progress is <code style="color: #00aa00">25%</code> to <code style="color: #00aa00">50%</code>,<br>
     *                   &nbsp; >> &nbsp; <code style="color: #555555">"/"</code> if progress is <code style="color: #00aa00">50%</code> to <code style="color: #00aa00">75%</code>,<br>
     *                   &nbsp; >> &nbsp; <code style="color: #555555">"#"</code> if progress is <code style="color: #00aa00">75%</code> to <code style="color: #00aa00">100%</code>.<br>
     * @param empty      The character to use for the empty part of the bar.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code>[####<code style="color: #ff0000; font-weight: 900;">------------</code>]</code><br>
     * @param endFill    If progress is 100%, the bar will be filled with the fill character.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">100% - </code><code>[<code style="color: #00ff00; font-weight: 900;">****************</code>]</code><br>
     * @param startEmpty In contrary, at 0%, the bar will be filled with the empty character.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp; 0% - </code><code>[<code style="color: #0000ff; font-weight: 900;">~~~~~~~~~~~~~~~~</code>]</code><br>
     * @param length     The length of the progress bar (number of characters).<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code style="color: #aaaaaa;">[####------------]</code>
     *                   &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code style="color: #aaaaaa;">[##------]</code><br>
     * @param progress   The progress of the bar, from 0 to 1.<br>
     *                   Example: &nbsp; <code style="color: #00aa00">&nbsp;25% - </code><code style="color: #aaaaaa;">[##------]</code>
     *                   <code style="color: #00aa00">&nbsp;50% - </code><code style="color: #aaaaaa;">[####----]</code>
     *                   <code style="color: #00aa00">&nbsp;75% - </code><code style="color: #aaaaaa;">[######--]</code><br>
     * @param format     The format of the final bar with 5 vars: <b>{b}</b> | the formatted bar ; <b>{p}</b> | the percentage (int) ;
     *                   <b>{p.}</b> | the percentage (double #0.0) ; <b>{p..}</b> | the percentage (double #0.00) ;
     *                   <b>{p#}</b> | the progression (0 to 1 float) ; <b>{l}</b> | the length of the progress bar<br>
     *                   Example:
     *                   <ul>
     *                          <li><code style="color: #aa00aa">{p}% - {b} </code>&nbsp; &nbsp; <code style="color: #00aaaa">25% - [####------------]</code></li>
     *                          <li><code style="color: #aa00aa">{p.}% $ {b}</code>&nbsp; &nbsp; <code style="color: #00aaaa">25.00 $ [####------------]</code></li>
     *                          <li><code style="color: #aa00aa">{b} >> {p#}</code>&nbsp; &nbsp; <code style="color: #00aaaa">[####------------] >> 0.25</code></li>
     *                   </ul>
     *
     * @return A string that is a progress bar.
     */
    public static String progressBar(String leftSide, String rightSide, String[] fillChars, String empty, String endFill, String startEmpty,
                                     int length, float progress, String format) {
        StringBuilder sb = new StringBuilder();
        sb.append(leftSide);
        for (int i = 0; i < Math.round(length * progress); i++) {
            sb.append(progress == 1.0 ? endFill : fillChars[(int) (progress / (1.0 / fillChars.length))]);
        }
        for (int i = 0; i <= Math.round(length * (1 - progress)); i++) {
            sb.append(progress == 0.0 ? startEmpty : empty);
        }
        sb.append(rightSide);
        return format.replace("{b}", sb.toString()).replace("{p}", String.valueOf(Math.round(progress * 100)))
                .replace("{p.}", String.format("%.1f", progress * 100)).replace("{p..}", String.format("%.2f", progress * 100))
                .replace("{p#}", String.valueOf(progress)).replace("{l}", String.valueOf(length));
    }

    /**
     * It returns the difference between two dates in the format of Xd Xh Xmin Xsec Xms.
     *
     * @param from   The date to start the comparison from.
     * @param to     The date to compare to.
     * @param millis If true, the milliseconds will be included in the output.
     *
     * @return A string that represents the difference between two dates.
     */
    public static String compareDate(Date from, Date to, boolean millis) {
        long diff = to.getTime() - from.getTime();
        int days = (int) (diff / (1000 * 60 * 60 * 24));
        int hours = (int) ((diff - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int minutes = (int) ((diff - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60));
        int seconds = (int) ((diff - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * minutes)) / 1000);
        int milliseconds = (int) ((diff - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * minutes) - (1000 * seconds)));
        String out = (days == 0 ? "" : days + "d ") + (hours == 0 ? "" : hours + "h ") + (minutes == 0 ? "" : minutes + "m ")
                + (seconds == 0 ? "" : seconds + "s ") + (millis && milliseconds != 0 ? milliseconds + "ms " : "");
        return out.equals("") ? millis ? "0ms" : "0s" : out.substring(0, out.length() - 1);
    }

    /**
     * Pair is a generic class that holds two objects of any type.
     */
    public static class Pair<A, B> {
        private A a;
        private B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public A getKey() {
            return a;
        }

        public B getValue() {
            return b;
        }

        public void setKey(A a) {
            this.a = a;
        }

        public void setValue(B b) {
            this.b = b;
        }
    }

    /**
     * Triple is a class that holds three objects of any type.
     */
    public static class Triple<A, B, C> {
        private A a;
        private B b;
        private C c;

        public Triple(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }

        public C getC() {
            return c;
        }

        public void setA(A a) {
            this.a = a;
        }

        public void setB(B b) {
            this.b = b;
        }

        public void setC(C c) {
            this.c = c;
        }
    }

    /**
     * It takes a string, hash it, and returns a long
     *
     * @param s The string to hash.
     *
     * @return A hash of the string.
     */
    public static long hashStringToSeed(String s) {
        if (s == null)
            return 0;
        long hash = 0;
        for (char c : s.toCharArray())
            hash = 31L * hash + c;
        return hash;
    }

    /**
     * Calculates the total EXP needed to reach the given level.
     *
     * @param lvl The level you want to calculate the experience for.
     * @return The amount of experience needed to reach a certain level.
     */
    public static long lvlToExp(double lvl) {
        return (long) (((int) lvl <= 16 ? Math.pow((int) lvl, 2) + 6L * (int) lvl
                : (int) lvl <= 31 ? 2.5 * Math.pow((int) lvl, 2) - 40.5 * (int) lvl + 360
                : 4.5 * Math.pow((int) lvl, 2) - 162.5 * (int) lvl + 2220)
                + expToLvlUpAt((int) lvl) * (lvl - (int) lvl));
    }

    /**
     * Calculs the amount of exp needed to level up at a certain level.
     *
     * @param lvl The level of the player.
     * @return The amount of experience needed to level up at the given level.
     */
    public static long expToLvlUpAt(int lvl) {
        return lvl <= 15 ? 2L * lvl + 7
                : lvl <= 30 ? 5L * lvl - 38
                : 9L * lvl - 158;
    }

    /**
     * Converts experience to level.
     *
     * @param exp The amount of experience you want to convert.
     * @return The level of a player based on the amount of experience.
     */
    public static double expToLvl(long exp) {
        return exp <= 352 ? Math.sqrt(exp + 9) - 3
                : exp <= 1507 ? 81.0 / 10.0 + Math.sqrt(2.0 / 5.0 * (exp - 7839.0 / 40.0))
                : 325.0 / 18.0 + Math.sqrt(2.0 / 9.0 * (exp - 54215.0 / 72.0));
    }
}
