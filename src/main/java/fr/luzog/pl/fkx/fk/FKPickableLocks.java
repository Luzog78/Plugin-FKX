package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.GUIs.Guis;
import fr.luzog.pl.fkx.utils.*;
import javafx.util.Pair;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ContainerBlock;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FKPickableLocks {

    public static final String LOCK_KEY = "$FKX", LOCK_ID_TAG = "fkxLockIdTag", RARITY_TAG = "fkxRarityTag";
    public static final int maxDistance = 4;

    public static enum KeyType {NONE, RARITY, ID}

    public static enum Rarity {
        NONE(0, "§f"),
        COMMON(1, "§7"),
        RARE(2, "§a"),
        EPIC(3, "§d"),
        LEGENDARY(4, "§6"),
        SPECIAL(5, "§4");

        private int value;
        private String color;

        Rarity(int value, String color) {
            this.value = value;
            this.color = color;
        }

        public String getFormattedName(boolean key) {
            return color + "§l-=[ §k0§r" + color + " " + name() + (key ? " KEY" : "") + " §l§k0§r" + color + " §l]=-";
        }

        public String getFormattedLore() {
            return "§r   " + color + "[§l" + name() + "§r" + color + "]";
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class Lock {
        private String id;
        private Rarity rarity;
        private boolean pickable, picked;
        private long originalCoolDown, coolDown;
        private Location location;
        private String picker;
        private KeyType keyType;
        private UUID armorStand1, armorStand2;

        public Lock(String id, Rarity rarity, boolean pickable, long originalCoolDown, Location location) {
            this.id = id;
            this.rarity = rarity;
            this.pickable = pickable;
            this.picked = false;
            this.originalCoolDown = originalCoolDown;
            this.coolDown = originalCoolDown;
            this.location = location;
            this.picker = null;
            this.keyType = KeyType.NONE;
            this.armorStand1 = null;
            this.armorStand2 = null;
        }

        public Lock(String id, Rarity rarity, boolean pickable, boolean picked, long originalCoolDown, long coolDown,
                    Location location, String picker, KeyType keyType, UUID armorStand1, UUID armorStand2) {
            this.id = id;
            this.rarity = rarity;
            this.pickable = pickable;
            this.picked = picked;
            this.originalCoolDown = originalCoolDown;
            this.coolDown = coolDown;
            this.location = location;
            this.picker = picker;
            this.keyType = keyType;
            this.armorStand1 = armorStand1;
            this.armorStand2 = armorStand2;
        }

        public void resetCoolDown() {
            coolDown = originalCoolDown;
        }

        public void lock() {
            picked = false;
            resetCoolDown();
            hideArmorStand();
            TileEntity te = ((CraftWorld) location.getWorld()).getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            if (te == null)
                return;
            NBTTagCompound nbt = new NBTTagCompound();
            te.b(nbt);
            nbt.setString("Lock", LOCK_KEY);
            te.a(nbt);
            te.update();
            location.getBlock().setMetadata(LOCK_ID_TAG, new FixedMetadataValue(Main.instance, id));
        }

        public void unlock() {
            picked = true;
            coolDown = 0;
            updateArmorStand();
            TileEntity te = ((CraftWorld) location.getWorld()).getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            if (te == null)
                return;
            NBTTagCompound nbt = new NBTTagCompound();
            te.b(nbt);
            nbt.setString("Lock", "");
            te.a(nbt);
            te.update();
        }

        public static ItemStack getKey(KeyType keyType, Rarity rarity, @Nullable String id) {
            if (keyType == KeyType.RARITY)
                return FKPickableLocks.getLockItem(rarity);
            return FKPickableLocks.getLockItem(rarity, id);
        }

        public ItemStack getKey() {
            return getKey(keyType, rarity, id);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Rarity getRarity() {
            return rarity;
        }

        public void setRarity(Rarity rarity) {
            this.rarity = rarity;
        }

        public boolean isPickable() {
            return pickable;
        }

        public void setPickable(boolean pickable) {
            this.pickable = pickable;
        }

        public boolean isPicked() {
            return picked;
        }

        public void setPicked(boolean picked) {
            this.picked = picked;
        }

        public long getOriginalCoolDown() {
            return originalCoolDown;
        }

        public void setOriginalCoolDown(long originalCoolDown) {
            this.originalCoolDown = originalCoolDown;
        }

        public long getCoolDown() {
            return coolDown;
        }

        public void setCoolDown(long coolDown) {
            this.coolDown = coolDown;
        }

        public void decreaseCoolDown() {
            coolDown--;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getPicker() {
            return picker;
        }

        public void setPicker(String picker) {
            this.picker = picker;
        }

        public KeyType getKeyType() {
            return keyType;
        }

        public void setKeyType(KeyType keyType) {
            this.keyType = keyType;
        }

        public Pair<ArmorStand, ArmorStand> showArmorStand() {
            ArmorStand a1 = null, a2 = null;
            for (Entity e : location.getWorld().getEntities())
                if (e instanceof ArmorStand)
                    if (e.getUniqueId().equals(armorStand1))
                        a1 = (ArmorStand) e;
                    else if (e.getUniqueId().equals(armorStand2))
                        a2 = (ArmorStand) e;

            boolean save = a1 == null || a2 == null;

            if (a1 == null) {
                a1 = (ArmorStand) location.getWorld().spawnEntity(Utils.normalize(location, true).subtract(0, 1.45, 0), EntityType.ARMOR_STAND);
                a1.setCustomName("---");
                a1.setCustomNameVisible(true);
                a1.setVisible(false);
                a1.setGravity(false);
                a1.setCanPickupItems(false);
                a1.setSmall(false);
                a1.setBasePlate(false);
                a1.setRemoveWhenFarAway(false);
                a1.setNoDamageTicks(Integer.MAX_VALUE); // ~3.4 years of god mod
                setArmorStand1(a1.getUniqueId());
            }
            if (a2 == null) {
                a2 = (ArmorStand) location.getWorld().spawnEntity(Utils.normalize(location, true).subtract(0, 1.7, 0), EntityType.ARMOR_STAND);
                a2.setCustomName("---");
                a2.setCustomNameVisible(true);
                a2.setVisible(false);
                a2.setGravity(false);
                a2.setCanPickupItems(false);
                a2.setSmall(false);
                a2.setBasePlate(false);
                a2.setRemoveWhenFarAway(false);
                a2.setNoDamageTicks(Integer.MAX_VALUE); // ~3.4 years of god mod
                setArmorStand2(a2.getUniqueId());
            }

//            a.setTicksLived(Integer.MAX_VALUE); // ~3.4 years of lifetime

            if (save)
                FKManager.registered.forEach(FKManager::savePickableLocks);

            return new Pair<>(a1, a2);
        }

        public void hideArmorStand() {
            for (Entity e : location.getWorld().getEntities())
                if (e instanceof ArmorStand && (e.getUniqueId().equals(armorStand1) || e.getUniqueId().equals(armorStand2)))
                    e.remove();
        }

        public void updateArmorStand() {
            Pair<ArmorStand, ArmorStand> as = showArmorStand();
            if (!as.getKey().getCustomName().equals(rarity.getFormattedName(false)))
                as.getKey().setCustomName(rarity.getFormattedName(false));
            as.getValue().setCustomName(getProgressBar(originalCoolDown, coolDown));
        }

        public UUID getArmorStand1() {
            return armorStand1;
        }

        public void setArmorStand1(UUID armorStand1) {
            this.armorStand1 = armorStand1;
        }

        public UUID getArmorStand2() {
            return armorStand2;
        }

        public void setArmorStand2(UUID armorStand2) {
            this.armorStand2 = armorStand2;
        }

        public void destroy(FKManager manager) {
            unlock();
            manager.getPickableLocks().getPickableLocks().removeIf(lock -> lock.getId().equals(id));
            manager.savePickableLocks();
            new BukkitRunnable() {
                @Override
                public void run() {
                    hideArmorStand();
                }
            }.runTaskLater(Main.instance, 1);
        }
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler(priority = EventPriority.LOW)
        public static void onBreakBlock(BlockBreakEvent e) {
            if (isPickableLock(e.getBlock().getLocation()))
                e.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOW)
        public static void onExplode(BlockExplodeEvent e) {
            e.blockList().removeIf(block -> isPickableLock(block.getLocation()));
        }

        @EventHandler(priority = EventPriority.LOW)
        public static void onInteract(PlayerInteractEvent e) {
            Action a = e.getAction();
            Player p = e.getPlayer();
            boolean sneak = e.getPlayer().isSneaking();
            Block b = e.getClickedBlock();
            ItemStack is = e.getItem();
            List<FKPlayer> fkps = FKManager.getGlobalPlayer(p.getName());

            if (!(a == Action.RIGHT_CLICK_BLOCK || a == Action.LEFT_CLICK_BLOCK)
                    || b == null || !isPickableLock(b.getLocation()))
                return;

            for (FKPlayer fkp : fkps) {
                if ((fkp.getManager().getState() == FKManager.State.PAUSED && !fkp.getTeam().getId().equals(fkp.getManager().getGods().getId()))
                        || fkp.getTeam() == null || fkp.getTeam().getId().equalsIgnoreCase(FKTeam.SPECS_ID) /*TODO -> || fkp.getTeam().getId().equalsIgnoreCase(FKTeam.GODS_ID)*/)
                    return;

                FKPickableLocks pickableLocks = fkp.getManager().getPickableLocks();
                Lock l = pickableLocks.getLock(b.getMetadata(LOCK_ID_TAG).get(0).asString());
                if (l == null)
                    return;

                if (is != null && new CustomNBT(is).hasKey(RARITY_TAG) && new CustomNBT(is).getString(RARITY_TAG).equals("admin")) {
                    if (a == Action.RIGHT_CLICK_BLOCK)
                        if (sneak) {
                            l.destroy(fkp.getManager());
                            p.sendMessage("§aVous avez détruit ce coffre.");
                        } else {
                            p.sendMessage("§aCoffre crochetable :");
                            p.sendMessage("§a - Id : §9" + l.getId());
                            p.sendMessage("§a - Rareté : §f" + l.getRarity().getFormattedName(false));
                            p.sendMessage("§a - Accessible :  §f" + (l.isPickable() ? "§2§l" + SpecialChars.YES + "  Oui" : "§4§l" + SpecialChars.NO + "  Non"));
                            p.sendMessage("§a - Crocheté :  §f" + (l.isPicked() ? "§2" + SpecialChars.YES + "  Oui" : "§4" + SpecialChars.NO + "  Non"));
                            p.sendMessage("§a - Temps pour crocheter : §f" + l.getOriginalCoolDown() + " ticks");
                            p.sendMessage("§a - Temps restant à crocheter : §f" + l.getCoolDown() + " ticks");
                            p.sendMessage("§a - Nom du pilleur : §6" + (l.getPicker() == null ? "§cnull" : l.getPicker()));
                            p.sendMessage("§a - Location : §f" + Utils.locToString(l.getLocation(), false, false, true));
                            p.sendMessage("§a - ArmorStand 1 : §8" + l.getArmorStand1());
                            p.sendMessage("§a - ArmorStand 2 : §8" + l.getArmorStand2());
                        }
                    else if (sneak) {
                        if (l.isPickable()) {
                            l.setPickable(false);
                            p.sendMessage("§aCe coffre est désormais inaccessible.");
                        } else {
                            l.setPickable(true);
                            p.sendMessage("§aCe coffre est désormais crochetable.");
                        }
                        fkp.getManager().savePickableLocks();
                    } else {
                        if (l.isPicked()) {
                            l.lock();
                            p.sendMessage("§aVous avez verrouillé ce coffre.");
                        } else {
                            l.unlock();
                            p.sendMessage("§aVous avez déverrouillé ce coffre.");
                        }
                        fkp.getManager().savePickableLocks();
                    }
                    e.setCancelled(true);
                    return;
                }

                if (!l.isPickable() && (fkp.getTeam() == null || !fkp.getTeam().getId().equals(FKTeam.GODS_ID))) {
                    p.sendMessage("§cLe coffre n'est pas crochetable à l'heure actuelle.");
                    e.setCancelled(true);
                    return;
                }

                if (!isLocked(b.getLocation()))
                    return;

                if (l.isPicked()) {
                    p.sendMessage("§cLe coffre déjà crocheté.");
                    return;
                }

                if (!verifyDistance(l.getLocation(), p.getLocation())) {
                    p.sendMessage("§cVous êtes trop loin du coffre.");
                    return;
                }

                if (pickableLocks.getInPicking().containsValue(l.getId())) {
                    p.sendMessage("§cUn joueur crochète déjà ce coffre " + l.getRarity().getFormattedName(false) + "§a.");
                    return;
                }

                if (is == null || !new CustomNBT(is).hasKey(RARITY_TAG)) {
                    p.sendMessage("§cVous ne possédez pas de clé pour déverrouiller ce coffre " + l.getRarity().getFormattedName(false) + "§a.");
                    return;
                }

                CustomNBT isNbt = new CustomNBT(is);
                Rarity r = Rarity.valueOf(isNbt.getString(RARITY_TAG).split(":", 1)[0]);
                String id = isNbt.getString(RARITY_TAG).contains(":") ? isNbt.getString(RARITY_TAG).split(":", 1)[1] : "";
                if (id.equals(""))
                    id = null;

                if (!verifyKey(l.getId(), l.getRarity(), id, r)) {
                    p.sendMessage("§cVous ne possédez pas la bonne clé pour déverrouiller ce coffre " + l.getRarity().getFormattedName(false) + "§a.");
                    return;
                }

                p.sendMessage("§aVous essayez de crocheter le coffre " + l.getRarity().getFormattedName(false) + "§a !");
                if (is.getAmount() > 1)
                    is.setAmount(is.getAmount() - 1);
                else
                    p.setItemInHand(null);
                l.setPicker(p.getName());
                l.setKeyType(id == null ? KeyType.RARITY : KeyType.ID);
                pickableLocks.pickALock(p.getName(), l.getId());
            }
        }

    }

    public void saveToConfig(String gameId, boolean soft) {
        getConfig(gameId).load()

                .setLocks(pickableLocks, !soft)
                .setPicking(inPicking, !soft)

                .save();
    }

    public Config.PickableLocks getConfig(String gameId) {
        return new Config.PickableLocks("game-" + gameId + "/PickableLocks.yml");
    }

    public static boolean isPickableLock(Location location) {
        TileEntity te = ((CraftWorld) location.getWorld()).getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (te == null)
            return false;
        return location.getBlock().hasMetadata(LOCK_ID_TAG);
    }

    public static boolean isLocked(Location location) {
        TileEntity te = ((CraftWorld) location.getWorld()).getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (te == null)
            return false;
        NBTTagCompound nbt = new NBTTagCompound();
        te.b(nbt);
        return nbt.hasKey("Lock") && !nbt.getString("Lock").equals("") && location.getBlock().hasMetadata(LOCK_ID_TAG);
    }

    public static ItemStack getLockItem(Rarity rarity) {
        return Items.builder(Material.TRIPWIRE_HOOK)
                .setName(rarity.getFormattedName(true))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "§7 Cette clé vous permet de",
                        "§7  déverrouiller nombre de",
                        "§7  trésors plus fous les uns",
                        "§7  que les autres...",
                        "§7  Prenez-en grand soin !",
                        " ",
                        " " + rarity.getFormattedLore(),
                        " ",
                        "§8" + Guis.loreSeparator
                )
                .getNBT()
                .setString(RARITY_TAG, rarity.name())
                .build();
    }

    public static ItemStack getLockItem(Rarity rarity, String id) {
        return Items.builder(getLockItem(rarity))
                .addLore("§8Dévérouille : §7" + id)
                .getNBT()
                .setString(RARITY_TAG, rarity.name() + ":" + id)
                .build();
    }

    public static ItemStack getMasterKey() {
        return Items.builder(Material.TRIPWIRE_HOOK)
                .setName("§d§l-=[ §k0§d §nPasse Partout§d §l§k0§d§l ]=-")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "§7 Cette clé vous permet de",
                        "§7  déverrouiller §dn'importe",
                        "§d  quel trésor §7sur la carte.",
                        "§7  Il est un des item des",
                        "§7  plus rare car obtenable",
                        "§7  que par la grâce d'un",
                        "§d  Administrateur§7...",
                        "§7  Sentez-vous honoré !",
                        " ",
                        "    §d[§lMYTHICAL§d]",
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Droit pour avoir des infos",
                        "§7  (Sneak pour détruire)",
                        "§7Clic Gauche pour Lock / Unlock",
                        "§7  (Sneak pour rendre",
                        "§7    Crochetable / Inaccessible)"
                )
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .getNBT()
                .setString(RARITY_TAG, "admin")
                .build();
    }

    public static boolean verifyDistance(Location lock, Location picking) {
        return Utils.normalize(lock, true).distance(picking) <= maxDistance;
    }

    public static boolean verifyKey(String lockId, Rarity lockRarity, String keyId, Rarity keyRarity) {
        return (keyId == null && keyRarity.getValue() == lockRarity.getValue())
                || (keyId != null && keyId.equals(lockId) && keyRarity.getValue() == lockRarity.getValue());
    }

    public static String getProgressBar(long originalCountDown, long currentCoolDown) {
        if (currentCoolDown == 0)
            return Utils.progressBar("§f[", "§f]", "", "", "§2|", "", 16, 1.0f, "§a{p}% {b}");
        else
            return Utils.progressBar("§f[", "§f]", new String[]{
                            "§e|", "§e|", "§e|", "§e|", "§a|", "§a|", "§a|", "§a|", "§a|", "§a|",
                            "§a|", "§a|", "§a|", "§a|", "§2|", "§2|", "§2|", "§6|", "§c|", "§4|"
                    }, "§7|", "§2|", "§c|", 32,
                    (float) (1 - (currentCoolDown * 1.0) / originalCountDown), "§a{p.}% {b}");
    }

    private ArrayList<Lock> pickableLocks;
    private HashMap<String, String> inPicking;

    public FKPickableLocks() {
        pickableLocks = new ArrayList<>();
        inPicking = new HashMap<>();
    }

    public void pickALock(String player, String lockId) {
        if (!inPicking.containsKey(player))
            inPicking.put(player, lockId);
        else
            inPicking.replace(player, lockId);

        if (getLock(lockId) != null) {
            getLock(lockId).resetCoolDown();
            getLock(lockId).updateArmorStand();
            FKManager.getGlobalPlayer(player).forEach(p -> p.getManager().savePickableLocks());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Lock l;

                if (Bukkit.getOfflinePlayer(player).isOnline()
                        && inPicking.containsKey(player)
                        && inPicking.get(player).equals(lockId)
                        && (l = getLock(lockId)) != null
                        && l.isPickable()
                        && !l.isPicked()
                        && verifyDistance(l.getLocation(), Bukkit.getPlayerExact(player).getLocation()))
                    if (l.getCoolDown() > 0) {
                        l.decreaseCoolDown();
                        l.updateArmorStand();
                    } else {
                        l.unlock();
                        inPicking.remove(player);
                        Bukkit.getPlayerExact(player).sendMessage("§aVous avez déverrouillé le coffre.");
                        Bukkit.getPlayerExact(player).playSound(Bukkit.getPlayerExact(player).getLocation(), Sound.LEVEL_UP, 1, 1);
                        FKManager.getGlobalPlayer(player).forEach(p -> p.getManager().savePickableLocks());
                        cancel();
                    }
                else {
                    Lock lock = getLock(lockId);
                    if (Bukkit.getOfflinePlayer(player).isOnline()) {
                        Bukkit.getPlayerExact(player).sendMessage("§cCrochetage annulé.");
                        Bukkit.getPlayerExact(player).playSound(Bukkit.getPlayerExact(player).getLocation(), Sound.ANVIL_LAND, 1, 1);
                        if (lock != null)
                            if (Bukkit.getPlayerExact(player).getInventory().firstEmpty() == -1)
                                Bukkit.getPlayerExact(player).getWorld().dropItemNaturally(Bukkit.getPlayerExact(player).getLocation(), lock.getKey());
                            else
                                Bukkit.getPlayerExact(player).getInventory().addItem(lock.getKey());
                    }
                    if (lock != null) {
                        lock.resetCoolDown();
                        lock.setPicker(null);
                        lock.setKeyType(KeyType.NONE);
                        lock.hideArmorStand();
                    }
                    inPicking.remove(player);
                    FKManager.getGlobalPlayer(player).forEach(p -> p.getManager().savePickableLocks());
                    cancel();
                }

            }
        }.runTaskTimer(Main.instance, 0, 1);
    }

    public void updateAll() {
        for (Lock l : pickableLocks) {
            long tempCoolDown = l.getCoolDown();
            if (l.isPicked())
                l.unlock();
            else
                l.lock();
            l.setCoolDown(tempCoolDown);
        }
    }

    public Lock getLock(String id) {
        for (Lock lock : pickableLocks)
            if (lock.getId().equals(id))
                return lock;
        return null;
    }

    public void addLock(Lock lock) {
        if (getLock(lock.getId()) != null)
            throw new IllegalStateException("Lock with id: '" + lock.getId() + "' already exists");
        pickableLocks.add(lock);
    }

    public ArrayList<Lock> getPickableLocks() {
        return pickableLocks;
    }

    public void setPickableLocks(ArrayList<Lock> pickableLocks) {
        this.pickableLocks = pickableLocks;
    }

    public HashMap<String, String> getInPicking() {
        return inPicking;
    }

    public void setInPicking(HashMap<String, String> inPicking) {
        this.inPicking = inPicking;
    }

}
