package fr.luzog.pl.fkx.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Loots {

    public static class LootItem {
        private double proba;
        private ItemStack item;
        private int chanceLvl;
        private Boolean hasSilkTouch;

        public LootItem(double proba, ItemStack item, int chanceLvl, @Nullable Boolean hasSilkTouch) {
            this.proba = proba;
            this.item = item;
            this.chanceLvl = chanceLvl;
            this.hasSilkTouch = hasSilkTouch;
        }

        public double getProba() {
            return proba;
        }

        public void setProba(double proba) {
            this.proba = proba;
        }

        public ItemStack getItem() {
            return item;
        }

        public void setItem(ItemStack item) {
            this.item = item;
        }

        public int getChanceLvl() {
            return chanceLvl;
        }

        public void setChanceLvl(int chanceLvl) {
            this.chanceLvl = chanceLvl;
        }

        public Boolean hasSilkTouch() {
            return hasSilkTouch;
        }

        public void setSilkTouch(Boolean hasSilkTouch) {
            this.hasSilkTouch = hasSilkTouch;
        }
    }

    private ArrayList<LootItem> loots;
    private Random random;
    private Integer seed;

    private double chanceLvlProbaCoefficient;
    private int chanceLvlAmountCoefficient;

    public Loots() {
        loots = new ArrayList<>();
        random = null;
        seed = null;
        chanceLvlProbaCoefficient = 0;
        chanceLvlAmountCoefficient = 0;
    }

    public Loots clear() {
        loots.clear();
        return this;
    }

    public boolean contains(ItemStack item) {
        for (LootItem loot : loots)
            if (loot.getItem().isSimilar(item))
                return true;
        return false;
    }

    public boolean contains(double proba, ItemStack item) {
        for (LootItem loot : loots)
            if (loot.getProba() == proba && loot.getItem().isSimilar(item))
                return true;
        return false;
    }

    public int size() {
        return loots.size();
    }

    public Loots add(ItemStack item) {
        loots.add(new LootItem(1, item, -1, null));
        return this;
    }

    public Loots add(double proba, ItemStack item) {
        loots.add(new LootItem(proba, item, -1, null));
        return this;
    }

    public Loots add(ItemStack item, int chanceLvl, @Nullable Boolean hasSilkTouch) {
        loots.add(new LootItem(1, item, chanceLvl, hasSilkTouch));
        return this;
    }

    public Loots add(double proba, ItemStack item, int chanceLvl, @Nullable Boolean hasSilkTouch) {
        loots.add(new LootItem(proba, item, chanceLvl, hasSilkTouch));
        return this;
    }

    public Loots insert(int index, ItemStack item) {
        loots.add(index, new LootItem(1, item, -1, null));
        return this;
    }

    public Loots insert(int index, double proba, ItemStack item) {
        loots.add(index, new LootItem(proba, item, -1, null));
        return this;
    }

    public Loots insert(int index, ItemStack item, int chanceLvl, @Nullable Boolean hasSilkTouch) {
        loots.add(index, new LootItem(1, item, chanceLvl, hasSilkTouch));
        return this;
    }

    public Loots insert(int index, double proba, ItemStack item, int chanceLvl, @Nullable Boolean hasSilkTouch) {
        loots.add(index, new LootItem(proba, item, chanceLvl, hasSilkTouch));
        return this;
    }

    public Loots removeIndex(int index) {
        loots.remove(index);
        return this;
    }

    public Loots remove(int chanceLvl) {
        int i = 0;
        while (i < loots.size())
            if (loots.get(i).getChanceLvl() == chanceLvl)
                loots.remove(i);
            else
                i++;
        return this;
    }

    public Loots remove(@Nullable Boolean silkTouch) {
        int i = 0;
        while (i < loots.size())
            if (loots.get(i).hasSilkTouch() == silkTouch)
                loots.remove(i);
            else
                i++;
        return this;
    }

    public Loots remove(int chanceLvl, @Nullable Boolean silkTouch) {
        int i = 0;
        while (i < loots.size())
            if (loots.get(i).getChanceLvl() == chanceLvl
                    && loots.get(i).hasSilkTouch() == silkTouch)
                loots.remove(i);
            else
                i++;
        return this;
    }

    public Loots remove(ItemStack item) {
        int i = 0;
        while (i < loots.size())
            if (loots.get(i).getItem().isSimilar(item))
                loots.remove(i);
            else
                i++;
        return this;
    }

    public Loots remove(double proba, ItemStack item) {
        int i = 0;
        while (i < loots.size())
            if (loots.get(i).getProba() == proba
                    && loots.get(i).getItem().isSimilar(item))
                loots.remove(i);
            else
                i++;
        return this;
    }

    public Loots remove(double proba, ItemStack item, int chanceLvl, @Nullable Boolean silkTouch) {
        int i = 0;
        while (i < loots.size())
            if (loots.get(i).getChanceLvl() == chanceLvl
                    && loots.get(i).hasSilkTouch() == silkTouch
                    && loots.get(i).getProba() == proba
                    && loots.get(i).getItem().isSimilar(item))
                loots.remove(i);
            else
                i++;
        return this;
    }

    public HashSet<Integer> getMaxChanceLvl() {
        HashSet<Integer> i = new HashSet<>();
        for (LootItem loot : loots)
            i.add(loot.getChanceLvl());
        return i;
    }

    public Loots forEach(Consumer<? super LootItem> toDo) {
        loots.forEach(toDo);
        return this;
    }

    public List<ItemStack> lootsInclusive() {
        return lootsInclusive(0, false);
    }

    public List<ItemStack> lootsInclusive(int chanceLvl, @Nullable Boolean hasSilkTouch) {
        if (seed == null)
            random = new Random();
        else
            random = new Random(seed);
        List<ItemStack> loots = new ArrayList<>();
        this.loots.forEach(l -> {
            double p = random.nextDouble();
            if (l.getChanceLvl() == -2 && !getMaxChanceLvl().contains(chanceLvl)) {
                if (p < l.getProba() * (1 + chanceLvlProbaCoefficient * chanceLvl)
                        && (l.hasSilkTouch() == hasSilkTouch || l.hasSilkTouch() == null || hasSilkTouch == null)) {
                    ItemStack is = l.getItem().clone();
                    is.setAmount(is.getAmount() * (1 + chanceLvlAmountCoefficient * chanceLvl));
                    loots.add(is);
                }
            } else if (p < l.getProba()
                    && (l.getChanceLvl() == chanceLvl || l.getChanceLvl() == -1 || chanceLvl == -1)
                    && (l.hasSilkTouch() == hasSilkTouch || l.hasSilkTouch() == null || hasSilkTouch == null))
                loots.add(l.getItem().clone());
        });
        return loots;
    }

    public ItemStack lootsExclusive() {
        return lootsExclusive(0, false);
    }

    public ItemStack lootsExclusive(int chanceLvl, @Nullable Boolean hasSilkTouch) {
        if (seed == null)
            random = new Random();
        else
            random = new Random(seed);
        double p = random.nextDouble();
        double pp = 0;
        for (LootItem loot : loots) {
            if (!(loot.getChanceLvl() == -2 && !getMaxChanceLvl().contains(chanceLvl)
                    || ((loot.getChanceLvl() == chanceLvl || loot.getChanceLvl() == -1 || chanceLvl == -1)
                    && (loot.hasSilkTouch() == hasSilkTouch || loot.hasSilkTouch() == null || hasSilkTouch == null))))
                continue;
            pp += loot.getProba() * (loot.getChanceLvl() == -2 && !getMaxChanceLvl().contains(chanceLvl) ? (1 + chanceLvlProbaCoefficient * chanceLvl) : 1);
            if (p < pp) {
                ItemStack is = loot.getItem().clone();
                if (loot.getChanceLvl() == -2 && !getMaxChanceLvl().contains(chanceLvl))
                    is.setAmount(is.getAmount() * (1 + chanceLvlAmountCoefficient * chanceLvl));
                return is;
            }
        }
        return new ItemStack(Material.AIR);
    }

    public ArrayList<LootItem> getLoots() {
        return loots;
    }

    public Loots setLoots(ArrayList<LootItem> loots) {
        this.loots = loots;
        return this;
    }

    public Random getRandom() {
        return random;
    }

    public Loots setRandom(Random random) {
        this.random = random;
        return this;
    }

    public Integer getSeed() {
        return seed;
    }

    public Loots setSeed(@Nullable Integer seed) {
        this.seed = seed;
        return this;
    }

    public double getChanceLvlProbaCoefficient() {
        return chanceLvlProbaCoefficient;
    }

    public Loots setChanceLvlProbaCoefficient(double chanceLvlProbaCoefficient) {
        this.chanceLvlProbaCoefficient = chanceLvlProbaCoefficient;
        return this;
    }

    public int getChanceLvlAmountCoefficient() {
        return chanceLvlAmountCoefficient;
    }

    public Loots setChanceLvlAmountCoefficient(int chanceLvlAmountCoefficient) {
        this.chanceLvlAmountCoefficient = chanceLvlAmountCoefficient;
        return this;
    }
}
