package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.events.Events;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Limits {

    private ArrayList<Material> craft;
    private HashMap<PotionEffectType, Integer> potion;
    private HashMap<Enchantment, Integer> enchant;
    private int wearingMaxDiamondPieces, wearingMaxGoldPieces,
            wearingMaxIronPieces, wearingMaxLeatherPieces;


    public void saveToConfig(String gameId, boolean soft) {
        if(soft)
            return;

        getConfig(gameId).load()

                .setCraft(craft, true)
                .setPotion(potion, true)
                .setEnchant(enchant, true)
                .setWearingMaxDiamondPieces(wearingMaxDiamondPieces, true)
                .setWearingMaxGoldPieces(wearingMaxGoldPieces, true)
                .setWearingMaxIronPieces(wearingMaxIronPieces, true)
                .setWearingMaxLeatherPieces(wearingMaxLeatherPieces, true)

                .save();
    }

    public Config.Limits getConfig(String gameId) {
        return new Config.Limits("game-" + gameId + "/Limits.yml");
    }

    public Limits() {
        this.craft = new ArrayList<>();
        this.potion = new HashMap<>();
        this.enchant = new HashMap<>();
        this.wearingMaxDiamondPieces = 5;
        this.wearingMaxGoldPieces = 5;
        this.wearingMaxIronPieces = 5;
        this.wearingMaxLeatherPieces = 5;
    }

    public Limits(ArrayList<Material> craft, HashMap<PotionEffectType, Integer> potion,
                  HashMap<Enchantment, Integer> enchant, HashMap<Material, Integer> holding,
                  int wearingMaxDiamondPieces, int wearingMaxGoldPieces,
                  int wearingMaxIronPieces, int wearingMaxLeatherPieces) {
        this.craft = craft;
        this.potion = potion;
        this.enchant = enchant;
        this.wearingMaxDiamondPieces = wearingMaxDiamondPieces;
        this.wearingMaxGoldPieces = wearingMaxGoldPieces;
        this.wearingMaxIronPieces = wearingMaxIronPieces;
        this.wearingMaxLeatherPieces = wearingMaxLeatherPieces;
    }

    public static int wearingScore(List<Material> mat, HumanEntity p) {
        int i = p.getItemInHand() != null && mat.contains(p.getItemInHand().getType()) ? 1 : 0;
        for(ItemStack is : p.getInventory().getArmorContents())
            if(is != null && mat.contains(is.getType()))
                i++;
        return i;
    }

    public static int diamondScore(HumanEntity p) {
        return wearingScore(Events.diamond, p);
    }

    public static int goldScore(HumanEntity p) {
        return wearingScore(Events.gold, p);
    }

    public static int ironScore(HumanEntity p) {
        return wearingScore(Events.iron, p);
    }

    public static int leatherScore(HumanEntity p) {
        return wearingScore(Events.leather, p);
    }

    public ArrayList<Material> getCraft() {
        return craft;
    }

    public void setCraft(ArrayList<Material> craft) {
        this.craft = craft;
    }

    public HashMap<PotionEffectType, Integer> getPotion() {
        return potion;
    }

    public void setPotion(HashMap<PotionEffectType, Integer> potion) {
        this.potion = potion;
    }

    public HashMap<Enchantment, Integer> getEnchant() {
        return enchant;
    }

    public void setEnchant(HashMap<Enchantment, Integer> enchant) {
        this.enchant = enchant;
    }

    public int getWearingMaxDiamondPieces() {
        return wearingMaxDiamondPieces;
    }

    public void setWearingMaxDiamondPieces(int wearingMaxDiamondPieces) {
        this.wearingMaxDiamondPieces = wearingMaxDiamondPieces;
    }

    public int getWearingMaxGoldPieces() {
        return wearingMaxGoldPieces;
    }

    public void setWearingMaxGoldPieces(int wearingMaxGoldPieces) {
        this.wearingMaxGoldPieces = wearingMaxGoldPieces;
    }

    public int getWearingMaxIronPieces() {
        return wearingMaxIronPieces;
    }

    public void setWearingMaxIronPieces(int wearingMaxIronPieces) {
        this.wearingMaxIronPieces = wearingMaxIronPieces;
    }

    public int getWearingMaxLeatherPieces() {
        return wearingMaxLeatherPieces;
    }

    public void setWearingMaxLeatherPieces(int wearingMaxLeatherPieces) {
        this.wearingMaxLeatherPieces = wearingMaxLeatherPieces;
    }

}
