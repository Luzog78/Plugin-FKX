package fr.luzog.pl.fkx.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public enum Heads {
	
	NOTCH("§dTête de Notch", "Notch"),
	SATAN("§4Têtee de Satan", "Satan"),
	HEROBRINE("§bTêtee de Herobrine", "Mojang"),
	LUZOG78("Têtee de Luzog78", "Luzog78"),
	ZARIUS666("Têtee de Zarius666", "Zarius666"),
	SYNAPS0("Têtee de Synaps0", "Synaps0"),
	HEAD_TROLL_FACE("Troll Face", "Troll"),
	HEAD_GOLDEN_STEVE("Golden Steve", "zsoccer23"),
	HEAD_DIAMOND_STEVE("Diamond Steve", "Foxcraft"),
	HEAD_CAMERA1("Camera", "MHF_Cam"),
	HEAD_CAMERA2("Camera", "Camera"),
	HEAD_CAMERA3("Camera", "MHF_Cam"),
	HEAD_MULTICOLOR("Multicolor", "YTB"),
	HEAD_YOUTUBE("§fYou§cTube", "MHF_YouTube"),
	HEAD_SKULL1("Skull", "D4rkFrame"),
	HEAD_SKULL2("Skull", "Skull"),
	HEAD_DEATH("§4Death", "Death"),
	MOB_CREEPER("§2Creeper", "Creeper"),
	MOB_ZOMBIE("Zombie", "Zombie"),
	MOB_SKELETON("Skeleton", "Skeleton"),
	MOB_GHAST("Ghast", "Ghast"),
	CHAR_P("Black [P]", "phumicek"),
	CHAR_QUESTION_MARK_BLUE("[?]", "vvvvvvvvvvvv"),
	CHAR_QUESTION_MARK_WHITE("[?]", "crashdummie99"),
	CHAR_QUESTION_MARK_GREEN("[?]", "FlyntCoal"),
	CHAR_ARROW_UP("Arrow Up", "natatos"),
	CHAR_ARROW_DOWN("Arrow Down", "saidus2"),
	BLOCK_BEDROCK("Bedrock", "001"),
	BLOCK_CHEST("Chest", "ElMarcosFTW"),
	BLOCK_CHEST_GOLD("Golden Chest", "Tom25W"),
	BLOCK_BOOKSHELF("BookShelf", "BookShelf"),
	BLOCK_TNT("§cTNT", "Eternal"),
	BLOCK_GRASS("§aGrass", "cake"),
	BLOCK_COMMAND_BLOCK("§dCommand Block", "CommandBlock"),
	BLOCK_MISSING_TEXTURE("§5404", "404"),
	BLOCK_WOOL_LIGHT_BLUE("Light Blue Wool", "Super_Sniper"),
	BLOCK_WOOL_WHITE("White Wool", "White_Wool"),
	BLOCK_WOOL_RED("Red Wool", "Red_Wool"),
	BLOCK_WOOL_YELLOW("Yellow Wool", "Yellow_Wool"),
	BLOCK_WOOL_PURPLE("Purple Wool", "Purple_Wool"),
	BLOCK_WOOL_DRAK_GREEN("Dark Green Wool", "Talia"),
	COLOR_WHITE("White", "Armen"), // Doesnt work
	COLOR_RED("Red", "Color_Red"),
	COLOR_LIME("Lime", "Lime"),
	COLOR_YELLOW("Yellow", "403"),
	MISC_LUCKY_BLOCK("§6LuckyBlock", "ABigDwarf"),
	MISC_BLUE_ORB("§9Orb", "Reziel"),
	MISC_PURPLE_ORB("§5Orb", "XxAltha4xX"),
	MISC_TELEVISION("Television", "Television"),
	MISC_JUKEBOX("JukeBox", "C418"),
	MISC_EARTH1("Earth", "BlockminersTV"),
	MISC_EARTH2("Earth", "Earth"),
	MISC_MONITOR("Monitor", "Zen9400"),
	MISC_GIFT_RED("Red Gift", "CruXXx"),
	MISC_GIFT_GREEN("Green Gift", "SeerPotion"),
	MISC_BITCOIN("BitCoin", "Health"),
	MISC_HEALTH("§cHealth", "Omanoctoa"),
	MISC_BURGER("Burger", "Combat60"),
	MISC_CAKE("§dCake", "ybab159"),
	MISC_POTION("Potion", "GTR04");
	
	private String name;
	private String player;
	
	private Heads(String name, String player) {
		setName(name);
		setPlayer(player);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public ItemStack getSkull() {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
		SkullMeta isM = (SkullMeta) is.getItemMeta();
		isM.setOwner(player);
		isM.setDisplayName("§r§e" + name);
		isM.setLore(Arrays.asList("§8Owner :", "§8 > §7§o" + player));
		is.setItemMeta(isM);
		return is;
	}
}
