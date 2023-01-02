package fr.luzog.pl.fkx.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

public enum Heads {

    NOTCH("§dTête de Notch", "Notch", false),
    SATAN("§4Têtee de Satan", "Satan", false),
    HEROBRINE("§bTêtee de Herobrine", "Mojang", false),
    LUZOG78("Têtee de Luzog78", "Luzog78", false),
    ZARIUS666("Têtee de Zarius666", "Zarius666", false),
    SYNAPS0("Têtee de Synaps0", "Synaps0", false),
    HEAD_TROLL_FACE("Troll Face", "Troll", false),
    HEAD_GOLDEN_STEVE("Golden Steve", "zsoccer23", false),
    HEAD_DIAMOND_STEVE("Diamond Steve", "Foxcraft", false),
    HEAD_CAMERA1("Camera", "MHF_Cam", false),
    HEAD_CAMERA2("Camera", "Camera", false),
    HEAD_CAMERA3("Camera", "MHF_Cam", false),
    HEAD_MULTICOLOR("Multicolor", "YTB", false),
    HEAD_YOUTUBE("§fYou§cTube", "MHF_YouTube", false),
    HEAD_SKULL1("Skull", "D4rkFrame", false),
    HEAD_SKULL2("Skull", "Skull", false),
    HEAD_DEATH("§4Death", "Death", false),
    MOB_CREEPER("§2Creeper", "Creeper", false),
    MOB_ZOMBIE("Zombie", "Zombie", false),
    MOB_SKELETON("Skeleton", "Skeleton", false),
    MOB_GHAST("Ghast", "Ghast", false),
    CHAR_P("Black [P]", "phumicek", false),
    CHAR_QUESTION_MARK_BLUE("[?]", "vvvvvvvvvvvv", false),
    CHAR_QUESTION_MARK_WHITE("[?]", "crashdummie99", false),
    CHAR_QUESTION_MARK_GREEN("[?]", "FlyntCoal", false),
    CHAR_ARROW_UP("Arrow Up", "natatos", false),
    CHAR_ARROW_DOWN("Arrow Down", "saidus2", false),
    BLOCK_BEDROCK("Bedrock", "001", false),
    BLOCK_CHEST("Chest", "ElMarcosFTW", false),
    BLOCK_CHEST_GOLD("Golden Chest", "Tom25W", false),
    BLOCK_BOOKSHELF("BookShelf", "BookShelf", false),
    BLOCK_TNT("§cTNT", "Eternal", false),
    BLOCK_GRASS("§aGrass", "cake", false),
    BLOCK_COMMAND_BLOCK("§dCommand Block", "CommandBlock", false),
    BLOCK_MISSING_TEXTURE("§5404", "404", false),
    BLOCK_WOOL_LIGHT_BLUE("Light Blue Wool", "Super_Sniper", false),
    BLOCK_WOOL_WHITE("White Wool", "White_Wool", false),
    BLOCK_WOOL_RED("Red Wool", "Red_Wool", false),
    BLOCK_WOOL_YELLOW("Yellow Wool", "Yellow_Wool", false),
    BLOCK_WOOL_PURPLE("Purple Wool", "Purple_Wool", false),
    BLOCK_WOOL_DRAK_GREEN("Dark Green Wool", "Talia", false),
    COLOR_RED("Red", "Color_Red", false),
    COLOR_LIME("Lime", "Lime", false),
    COLOR_YELLOW("Yellow", "403", false),
    MISC_LUCKY_BLOCK("§6LuckyBlock", "ABigDwarf", false),
    MISC_BLUE_ORB("§9Orb", "Reziel", false),
    MISC_PURPLE_ORB("§5Orb", "XxAltha4xX", false),
    MISC_TELEVISION("Television", "Television", false),
    MISC_JUKEBOX("JukeBox", "C418", false),
    MISC_EARTH1("Earth", "BlockminersTV", false),
    MISC_EARTH2("Earth", "Earth", false),
    MISC_MONITOR("Monitor", "Zen9400", false),
    MISC_GIFT_RED("Red Gift", "CruXXx", false),
    MISC_GIFT_GREEN("Green Gift", "SeerPotion", false),
    MISC_BITCOIN("BitCoin", "Health", false),
    MISC_HEALTH("§cHealth", "Omanoctoa", false),
    MISC_BURGER("Burger", "Combat60", false),
    MISC_CAKE("§dCake", "ybab159", false),
    MISC_POTION("Potion", "GTR04", false),


    ARROW_LEFT_OAK("Oak Left Arrow", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==", true),
    ARROW_RIGHT_OAK("Oak Right Arrow", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19", true),
    ARROW_UP_OAK("Oak Up Arrow", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA0MGZlODM2YTZjMmZiZDJjN2E5YzhlYzZiZTUxNzRmZGRmMWFjMjBmNTVlMzY2MTU2ZmE1ZjcxMmUxMCJ9fX0=", true),
    ARROW_DOWN_OAK("Oak Down Arrow", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQzNzM0NmQ4YmRhNzhkNTI1ZDE5ZjU0MGE5NWU0ZTc5ZGFlZGE3OTVjYmM1YTEzMjU2MjM2MzEyY2YifX19", true),
    GREEN_CHEST("Green Chest", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGZlOGRhZmZlMzVjYTE4MzJmZjk5ZWVhZDQ2MTdhMzY5ZWEwMzNjMjRiODI1M2Y1MTFmZDhiOTE3MzdhN2UifX19", true),
    MISC_EARTH("Earth", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTFiMzE4OGZkNDQ5MDJmNzI2MDJiZDdjMjE0MWY1YTcwNjczYTQxMWFkYjNkODE4NjJjNjllNTM2MTY2YiJ9fX0=", true),
    MISC_HOURGLASS("Hourglass", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYThhOGMyZmUzMGVmOTQwOGZjOTYyYWUwYzVlZGU5YjQ3OGI5MjU1ZDIyZWNjMTMxYTliZTZlYmE1YzFlZDRiNCJ9fX0=", true),
    MISC_WARNING("Warning", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQ5YjYzYzNiNzQ1ZjVkZjg1MTE3NTk3MmVlZmQ3N2VjYTUyNjlkNDg2N2M0ZWRhMTU5NGZmM2U2NjM0NjU4In19fQ==", true),
    MISC_PLAY_BUTTON_EMERALD("Emerald Play Button", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4ZjZiMTMxZWY4NDdkOTE2MGU1MTZhNmY0NGJmYTkzMjU1NGQ0MGMxOGE4MTc5NmQ3NjZhNTQ4N2I5ZjcxMCJ9fX0=", true),
    MISC_PLAY_BUTTON_SILVER("Silver Play Button", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmMwNzFlMTE5N2JjZWI0ODE5ZDU2ZGRjYmYwYTY4NDE1ODE3MDhlODAzOWZiYWZkZTAzMGNiMzY1NjdjNmVlOSJ9fX0=", true),
    MISC_PLAY_BUTTON_RUBY("Ruby Play Button", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VjZDA0MWY2MjhjMDA1YTY5MGZjNmI4MjM3ZTczMTFiYjdjM2IzYWFjMTA1MzlmZWZlMzk2YTRjN2M3ODNlNyJ9fX0=", true),
    MISC_ENDER_PEARL_PURPLE("Purple Ender Pearl", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc3ZDRhMjA2ZDc3NTdmNDc5ZjMzMmVjMWEyYmJiZWU1N2NlZjk3NTY4ZGQ4OGRmODFmNDg2NGFlZTdkM2Q5OCJ9fX0=", true),
    MISC_ENDER_PEARL_GREEN("Green Ender Pearl", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4MjYxODFjZTkwMTJiNjY1ODY1ZjNhYzAwNjYzMDdiNGQwMmRhMjgxNTQwMTA0ZTA0NjFmZmVmYTc0NTlmZCJ9fX0=", true),
    MISC_ENDER_PEARL_BLUE("Blue Ender Pearl", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhiZThhYmQ2NmQwOWE1OGNlMTJkMzc3NTQ0ZDcyNmQyNWNhZDdlOTc5ZThjMjQ4MTg2NmJlOTRkM2IzMmYifX19", true),

    ;

    private String name;
    private String data;
    private boolean isCustom;

    Heads(String name, String player, boolean isCustom) {
        setName(name);
        setData(player);
        setCustom(isCustom);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public ItemStack getSkull() {
        if (isCustom()) {
            return getCustomSkull(data);
        } else {
            return getSkullOf(data);
        }
    }

    public static ItemStack getSkullOf(String owner) {
        return getSkullOf(owner, owner);
    }

    public static ItemStack getSkullOf(String owner, String name) {
        ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
        SkullMeta isM = (SkullMeta) is.getItemMeta();
        isM.setOwner(owner);
        isM.setDisplayName("§r§e" + name);
        isM.setLore(Arrays.asList("§8Owner :", "§8 > §7§o" + owner));
        is.setItemMeta(isM);
        return is;
    }

    public static ItemStack getCustomSkull(String base64) {

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (base64.isEmpty()) return head;

        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        propertyMap.put("textures", new Property("textures", base64));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        head.setItemMeta(meta);

        return head;
    }
}
