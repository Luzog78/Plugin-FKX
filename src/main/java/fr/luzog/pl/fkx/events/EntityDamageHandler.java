package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Broadcast;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EntityDamageHandler implements Listener {

    @EventHandler
    public static void onDamage(EntityDamageEvent e) {
        System.out.println("onDamage " + e.getEntityType() + " " + e.getCause() + " " + e.getEntity().getLocation());

        if (!(e.getEntity() instanceof LivingEntity))
            return;

        LivingEntity entity = (LivingEntity) e.getEntity();

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            List<FKPlayer> fps = FKManager.getGlobalPlayer(p.getName());
            if (fps.isEmpty()) {
                e.setCancelled(true);
                return;
            }

            for (FKPlayer fp : fps) {

                if (fp != null) {
                    if (fp.getManager().getState() == FKManager.State.PAUSED && !fp.getTeam().getId().equals(fp.getManager().getGods().getId())) {
                        e.setCancelled(true);
                        return;
                    }
                    fp.getStats().increaseDamageTaken(e.getDamage());
                }

                if (entity.getHealth() - e.getFinalDamage() <= 0) {
                    if (fp != null)
                        fp.getStats().increaseDeaths();
                    Broadcast.mess((fp != null ? fp.getDisplayName() : p.getDisplayName()) + "§c est mort.");
                    e.setCancelled(true);
                    p.setHealth(p.getMaxHealth());
                    p.setFoodLevel(20);
                    p.setSaturation(20f);
                    p.teleport(p.getBedSpawnLocation() == null ? fp == null ? FKManager.getCurrentGame().getLobby().getSpawn() == null ?
                            Main.world.getSpawnLocation() : FKManager.getCurrentGame().getLobby().getSpawn() : fp.getTeam().getSpawn() : p.getBedSpawnLocation());
                }
            }
        } else if (entity.getHealth() - e.getFinalDamage() <= 0)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!e.isCancelled())
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
