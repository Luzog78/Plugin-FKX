package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Broadcast;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityDamageHandler implements Listener {

    @EventHandler
    public static void onDamage(EntityDamageEvent e) {
        if (e.getEntity().getType() == EntityType.ARMOR_STAND)
            return;
        System.out.println("onDamage " + e.getEntityType() + " " + e.getCause() + " " + e.getEntity().getLocation());

        if (!(e.getEntity() instanceof LivingEntity))
            return;

        LivingEntity entity = (LivingEntity) e.getEntity();
        Location tempLoc = entity.getLocation().add(0, 0.5, 0);

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            List<FKPlayer> fps = FKManager.getGlobalPlayer(p.getName());
            if (fps.isEmpty()) {
                e.setCancelled(true);
                return;
            }

            for (FKPlayer fp : fps) {

                if (fp.getManager().getState() == FKManager.State.PAUSED
                        && !fp.getTeam().getId().equals(fp.getManager().getGods().getId())) {
                    e.setCancelled(true);
                    return;
                }

                if (e.getDamage() < 1_000_000_000_000_000_000_000.0) // Avoid /kill command
                    fp.getStats().increaseDamageTaken(e.getDamage());

                if (entity.getHealth() - e.getFinalDamage() <= 0) {
                    fp.getStats().increaseDeaths();
                    Broadcast.mess(fp.getDisplayName() + "§c est mort.");
                    e.setCancelled(true);
                    p.setHealth(p.getMaxHealth());
                    p.setFoodLevel(20);
                    p.setSaturation(20f);
                    p.teleport(p.getBedSpawnLocation() == null ?
                            fp.getTeam() == null || fp.getTeam().getSpawn() == null ?
                                    fp.getManager().getSpawn() == null || fp.getManager().getSpawn().getSpawn() == null ?
                                            fp.getManager().getLobby() == null || fp.getManager().getLobby().getSpawn() == null ?
                                                    Main.world == null ?
                                                            p.getLocation()
                                                            : Main.world.getSpawnLocation()
                                                    : fp.getManager().getLobby().getSpawn() : fp.getManager().getSpawn().getSpawn()
                                    : fp.getTeam().getSpawn()
                            : p.getBedSpawnLocation());
                }

            }

            if (entity.getHealth() - e.getFinalDamage() <= 0) {
                new ArrayList<ItemStack>() {{
                    addAll(Arrays.asList(p.getInventory().getContents()));
                    addAll(Arrays.asList(p.getInventory().getArmorContents()));
                    removeIf(is -> is == null || is.getType() == Material.AIR);
                    forEach(is -> p.getWorld().dropItemNaturally(tempLoc, is));
                }};
                p.getInventory().setArmorContents(new ItemStack[4]);
                p.getInventory().clear();
            }
        } else if (entity.getHealth() - e.getFinalDamage() <= 0)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!e.isCancelled() && Main.customLootingSystem)
                        Events.killMobLoots.forEach(loot -> {
                            if (entity.getType() == loot.getType() && (loot.getData() == Events.EntityData.WHATEVER || (loot.getData() != Events.EntityData.WHATEVER
                                    && (entity instanceof Creeper && ((Creeper) entity).isPowered() == (loot.getData() == Events.EntityData.CREEPER_SUPERCHARGED))
                                    || (entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == (loot.getData() == Events.EntityData.SKELETON_WITHER ?
                                    Skeleton.SkeletonType.WITHER : Skeleton.SkeletonType.NORMAL))))) {
                                Location loc = entity.getLocation().clone().add(0, 0.3, 0);
                                int chanceLvl = entity.hasMetadata(Events.lastDamageLootingLevelTag) ?
                                        entity.getMetadata(Events.lastDamageLootingLevelTag).get(0).asInt() : 0;
                                boolean silkTouch = entity.hasMetadata(Events.lastDamageSilkTouchTag) && entity.getMetadata(Events.lastDamageSilkTouchTag).get(0).asBoolean();
                                if (loot.isExclusive()) {
                                    ItemStack is = loot.getLoots().lootsExclusive(chanceLvl, silkTouch);
                                    if (is.getType() != Material.AIR)
                                        loc.getWorld().dropItemNaturally(loc, is);
                                } else
                                    loot.getLoots().lootsInclusive(chanceLvl, silkTouch).forEach(is -> loc.getWorld().dropItemNaturally(loc, is));
                            }
                        });
                }
            }.runTask(Main.instance);
    }

}
