package fr.luzog.pl.fkx.game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.*;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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

import java.util.*;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class GPickableLocks {

    public static final String LOCK_KEY = "§d§l-=[ §k0§d §nPasse Partout§d §l§k0§d§l ]=-",
            LOCK_ID_TAG = "gameLockIdTag", RARITY_TAG = "gameRarityTag";
    public static final int maxDistance = 5;
    public static double RADIUS = 15;

    public static class Lock {
        private String id;
        private int level;
        private boolean pickable, picked, autoBC;
        private long originalCoolDown, coolDown;
        private Location location;
        private String picker;
        private UUID armorStand1, armorStand2;

        public Lock(String id, int level, boolean pickable, long originalCoolDown, Location location) {
            this.id = id;
            this.level = level;
            this.pickable = pickable;
            this.picked = false;
            this.autoBC = false;
            this.originalCoolDown = originalCoolDown;
            this.coolDown = originalCoolDown;
            this.location = location;
            this.picker = null;
            this.armorStand1 = null;
            this.armorStand2 = null;
        }

        public Lock(String id, int level, boolean pickable, boolean picked, boolean autoBC, long originalCoolDown,
                    long coolDown, Location location, String picker, UUID armorStand1, UUID armorStand2) {
            this.id = id;
            this.level = level;
            this.pickable = pickable;
            this.picked = picked;
            this.autoBC = autoBC;
            this.originalCoolDown = originalCoolDown;
            this.coolDown = coolDown;
            this.location = location;
            this.picker = picker;
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
            if (location.getBlock() != null)
                (location.getBlock().getType() == Material.CHEST
                        || location.getBlock().getType() == Material.TRAPPED_CHEST ? Arrays.asList(location,
                        location.clone().add(-1, 0, -1), location.clone().add(0, 0, -1),
                        location.clone().add(1, 0, -1), location.clone().add(-1, 0, 0),
                        location.clone().add(1, 0, 0), location.clone().add(-1, 0, 1),
                        location.clone().add(0, 0, 1), location.clone().add(1, 0, 1))
                        : Collections.singletonList(location)).stream().filter(l -> l.getBlock() != null
                        && l.getBlock().getType() == location.getBlock().getType()).forEach(loc -> {
                    TileEntity te = ((CraftWorld) loc.getWorld()).getTileEntityAt(loc.getBlockX(),
                            loc.getBlockY(), loc.getBlockZ());
                    if (te != null) {
                        NBTTagCompound nbt = new NBTTagCompound();
                        te.b(nbt);
                        nbt.setString("Lock", "");
                        te.a(nbt);
                        te.update();
                    }
                });
        }

        public void broadcast() {
            Random rand = new Random();
            rand.setSeed(Utils.hashStringToSeed(id));
            double x = rand.nextDouble() * GPickableLocks.RADIUS * (rand.nextBoolean() ? 1 : -1),
                    z = rand.nextDouble() * GPickableLocks.RADIUS * (rand.nextBoolean() ? 1 : -1);
            Location loc = location.clone().add(x, 0, z);
            Broadcast.event(String.format("Chers joueurs et joueuses de §6%s§r,"
                            + " nous vous informons que le coffre crochetable !%s, de niveau §f%d§r est présent"
                            + " non loins des coordonnées  X: !%.2f  Z: !%.2f  !",
                    Main.SEASON, id, level, loc.getX(), loc.getZ()));
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
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

        public boolean isAutoBC() {
            return autoBC;
        }

        public void setAutoBC(boolean autoBC) {
            this.autoBC = autoBC;
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

        public Utils.Pair<ArmorStand, ArmorStand> showArmorStand() {
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
                GManager.registered.forEach(GManager::savePickableLocks);

            return new Utils.Pair<>(a1, a2);
        }

        public void hideArmorStand() {
            for (Entity e : location.getWorld().getEntities())
                if (e instanceof ArmorStand && (e.getUniqueId().equals(armorStand1) || e.getUniqueId().equals(armorStand2)))
                    e.remove();
        }

        public void updateArmorStand() {
            Utils.Pair<ArmorStand, ArmorStand> as = showArmorStand();
            if (!as.getKey().getCustomName().equals("Niveau " + level))
                as.getKey().setCustomName("Niveau " + level);
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

        public void destroy(GManager manager) {
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
            if (GManager.getCurrentGame() != null &&
                    GManager.getCurrentGame().getPickableLocks().isPickableLock(e.getBlock().getLocation()))
                e.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOW)
        public static void onExplode(BlockExplodeEvent e) {
            e.blockList().removeIf(block -> GManager.getCurrentGame() != null &&
                    GManager.getCurrentGame().getPickableLocks().isPickableLock(block.getLocation()));
        }

        @EventHandler(priority = EventPriority.LOW)
        public static void onInteract(PlayerInteractEvent e) {
            Action a = e.getAction();
            Player p = e.getPlayer();
            boolean sneak = e.getPlayer().isSneaking();
            Block b = e.getClickedBlock();
            ItemStack is = e.getItem();
            GPlayer gPlayer;

            if (!(a == RIGHT_CLICK_BLOCK || a == Action.LEFT_CLICK_BLOCK) || b == null || GManager.getCurrentGame() == null
                    || (gPlayer = GManager.getCurrentGame().getPlayer(p.getName(), false)) == null || e.isCancelled())
                return;

            GPickableLocks pickableLocks = gPlayer.getManager().getPickableLocks();
            if (!pickableLocks.isPickableLock(b.getLocation()) || pickableLocks.getLock(b.getMetadata(LOCK_ID_TAG).get(0).asString()) == null) {
                if (is != null && new CustomNBT(is).hasKey(RARITY_TAG) && new CustomNBT(is).getString(RARITY_TAG).equals("admin"))
                    if (a == RIGHT_CLICK_BLOCK) {
                        p.sendMessage("§cCe bloc n'est pas un coffre crochetable." +
                                "\n§cVous pouvez faire §7Clic Gauche§c sur un bloc pour le rendre crochetable.");
                        e.setCancelled(true);
                    } else {
                        String id;
                        do
                            id = UUID.randomUUID().toString().substring(0, 6);
                        while (GManager.getCurrentGame().getPickableLocks().getLock(id) != null);
                        Lock l = new Lock(id, 0, true, 1L, b.getLocation());
                        pickableLocks.addLock(l);
                        p.sendMessage("§aVous avez créé un coffre crochetable !" +
                                "\n§a  >  §aId : §b" + l.getId() + "§a   |   Niveau : §f" + l.getLevel());
                        gPlayer.getManager().savePickableLocks();
                        pickableLocks.updateAll();
                    }
            } else {
                Lock l = pickableLocks.getLock(b.getMetadata(LOCK_ID_TAG).get(0).asString());

                if (is != null && new CustomNBT(is).hasKey(RARITY_TAG) && new CustomNBT(is).getString(RARITY_TAG).equals("admin")) {
                    if (a == RIGHT_CLICK_BLOCK)
                        if (sneak) {
                            l.destroy(gPlayer.getManager());
                            p.sendMessage("§aVous avez détruit ce coffre.");
                            e.setCancelled(true);
                        } else {
                            p.sendMessage("§aCoffre crochetable :");
                            p.sendMessage("§a - Id : §b" + l.getId());
                            p.sendMessage("§a - Niveau : §f" + l.getLevel());
                            p.sendMessage("§a - Accessible :  §f" + (l.isPickable() ? "§2§l" + SpecialChars.YES + "  Oui" : "§4§l" + SpecialChars.NO + "  Non"));
                            p.sendMessage("§a - Crocheté :  §f" + (l.isPicked() ? "§2" + SpecialChars.YES + "  Oui" : "§4" + SpecialChars.NO + "  Non"));
                            p.sendMessage("§a - Temps pour crocheter : §f" + l.getOriginalCoolDown() + " ticks");
                            p.sendMessage("§a - Temps restant à crocheter : §f" + l.getCoolDown() + " ticks");
                            p.sendMessage("§a - Nom du pilleur : §6" + (l.getPicker() == null ? "§cnull" : l.getPicker()));
                            p.sendMessage("§a - Location : §f" + Utils.locToString(l.getLocation(), false, false, true));
//                            p.sendMessage("§a - ArmorStand 1 : §8" + l.getArmorStand1());
//                            p.sendMessage("§a - ArmorStand 2 : §8" + l.getArmorStand2());
                        }
                    else if (sneak) {
                        if (l.isPickable()) {
                            l.setPickable(false);
                            p.sendMessage("§aCe coffre est désormais inaccessible.");
                        } else {
                            l.setPickable(true);
                            p.sendMessage("§aCe coffre est désormais crochetable.");
                        }
                        e.setCancelled(true);
                        gPlayer.getManager().savePickableLocks();
                    } else {
                        if (l.isPicked()) {
                            l.lock();
                            p.sendMessage("§aVous avez verrouillé ce coffre.");
                        } else {
                            l.unlock();
                            p.sendMessage("§aVous avez déverrouillé ce coffre.");
                        }
                        e.setCancelled(true);
                        gPlayer.getManager().savePickableLocks();
                    }
                    return;
                }

                if ((gPlayer.getManager().getState() == GManager.State.PAUSED
                        && !gPlayer.getTeam().getId().equals(gPlayer.getManager().getGods().getId()))
                        || gPlayer.getTeam() == null || gPlayer.getTeam().getId().equalsIgnoreCase(GTeam.SPECS_ID)
                    /*TODO -> || gPlayer.getTeam().getId().equalsIgnoreCase(FKTeam.GODS_ID)*/)
                    return;

                if (!l.isPickable()) {
                    p.sendMessage("§cLe coffre n'est pas crochetable à l'heure actuelle.");
                    e.setCancelled(true);
                    return;
                }

                if (!pickableLocks.isLocked(b.getLocation()))
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
                    p.sendMessage("§cUn joueur crochète déjà ce coffre.\n  >  §cId : §b" + l.getId() + "§c   |   Niveau : §f" + l.getLevel());
                    return;
                }

                if (l.getLevel() > getPickingLevel(GManager.getCurrentGame())) {
                    p.sendMessage("§cVous n'avez pas le niveau requis pour déverrouiller ce coffre.\n§c  >  Id : §b" + l.getId() + "§c   |   Niveau : §f" + l.getLevel());
                    return;
                }

                p.sendMessage("§aVous essayez de crocheter un coffre !\n§a  >  Id : §b" + l.getId() + "§a   |   Niveau : §f" + l.getLevel());
                l.setPicker(p.getName());
                pickableLocks.pickALock(p.getName(), l.getId());
            }
        }

    }

    public void saveToConfig(String gameId, boolean soft) {
        if (soft)
            return;

        getConfig(gameId).load()

                .setLocks(pickableLocks, true)
                .setPicking(inPicking, true)

                .save();
    }

    public Config.PickableLocks getConfig(String gameId) {
        return new Config.PickableLocks("game-" + gameId + "/PickableLocks.yml");
    }

    public static int getPickingLevel(GManager manager) {
        return manager.getDay();
    }

    public static ItemStack getMasterKey() {
        return Items.builder(Material.TRIPWIRE_HOOK)
                .setName(LOCK_KEY)
                .setLore(
                        "§8" + Utils.loreSeparator,
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
                        "§8" + Utils.loreSeparator,
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
        Double d = Utils.safeDistance(Utils.normalize(lock, true), picking, false);
        return d != null && d <= maxDistance;
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

    public GPickableLocks() {
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
            GManager.getGlobalPlayer(player).forEach(p -> p.getManager().savePickableLocks());
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
                        GManager.getGlobalPlayer(player).forEach(p -> p.getManager().savePickableLocks());
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                GManager.registered.forEach(m -> m.getPickableLocks().updateAll());
                            }
                        }.runTaskLater(Main.instance, 1);
                        cancel();
                    }
                else {
                    Lock lock = getLock(lockId);
                    if (Bukkit.getOfflinePlayer(player).isOnline()) {
                        Bukkit.getPlayerExact(player).sendMessage("§cCrochetage annulé.");
                        Bukkit.getPlayerExact(player).playSound(Bukkit.getPlayerExact(player).getLocation(), Sound.ANVIL_LAND, 1, 1);
                    }
                    if (lock != null) {
                        lock.resetCoolDown();
                        lock.setPicker(null);
                        lock.hideArmorStand();
                    }
                    inPicking.remove(player);
                    GManager.getGlobalPlayer(player).forEach(p -> p.getManager().savePickableLocks());
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

    public boolean isPickableLock(Location location) {
        TileEntity te = ((CraftWorld) location.getWorld()).getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (te == null)
            return false;
//        return location.getBlock().hasMetadata(LOCK_ID_TAG) && getLock(location.getBlock().getMetadata(LOCK_ID_TAG).get(0).asString()) != null;
        for (Lock l : pickableLocks)
            if (l.getLocation().getBlock().getLocation().equals(location.getBlock().getLocation()))
                return true;
        return false;
    }

    public boolean isLocked(Location location) {
        TileEntity te = ((CraftWorld) location.getWorld()).getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (te == null)
            return false;
        NBTTagCompound nbt = new NBTTagCompound();
        te.b(nbt);
        return nbt.hasKey("Lock") && !nbt.getString("Lock").equals("") && location.getBlock().hasMetadata(LOCK_ID_TAG)
                && getLock(location.getBlock().getMetadata(LOCK_ID_TAG).get(0).asString()) != null;
    }

    public Lock getLock(String id) {
        for (Lock lock : pickableLocks)
            if (lock.getId().equals(id))
                return lock;
        return null;
    }

    public Lock getLock(Location loc) {
        for (Lock lock : pickableLocks)
            if (lock.getLocation().getBlock().getLocation().equals(loc.getBlock().getLocation()))
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
