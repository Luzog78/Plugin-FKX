package fr.luzog.pl.fkx.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamageHandler implements Listener {

    @EventHandler
    public static void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof LivingEntity))
            return;

        LivingEntity entity = (LivingEntity) e.getEntity();

        if (entity.getHealth() - e.getFinalDamage() <= 0)
            Events.killMobLoots.forEach(loot -> {
                if (entity.getType() == loot.getType() && (loot.getData() == Events.EntityData.WHATEVER || (loot.getData() != Events.EntityData.WHATEVER
                        && (entity instanceof Creeper && ((Creeper) entity).isPowered() == (loot.getData() == Events.EntityData.CREEPER_SUPERCHARGED))
                        || (entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == (loot.getData() == Events.EntityData.SKELETON_WITHER ?
                        Skeleton.SkeletonType.WITHER : Skeleton.SkeletonType.NORMAL))))) {
                    Location loc = entity.getLocation().clone().add(0, 0.3, 0);
                    int chanceLvl = entity.hasMetadata(Events.lastDamageLootingLevelTag) ?
                            entity.getMetadata(Events.lastDamageLootingLevelTag).get(0).asInt() : 0;
                    boolean silkTouch = entity.hasMetadata(Events.lastDamageSilkTouchTag) && entity.getMetadata(Events.lastDamageSilkTouchTag).get(0).asBoolean();
                    if(loot.isExclusive()) {
                        ItemStack is = loot.getLoots().lootsExclusive(chanceLvl, silkTouch);
                        if(is.getType() != Material.AIR)
                            loc.getWorld().dropItemNaturally(loc, is);
                    } else
                        loot.getLoots().lootsInclusive(chanceLvl, silkTouch).forEach(is -> loc.getWorld().dropItemNaturally(loc, is));
                }
            });
    }

}
