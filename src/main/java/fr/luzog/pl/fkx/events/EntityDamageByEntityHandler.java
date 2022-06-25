package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKPermissions;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class EntityDamageByEntityHandler implements Listener {

    @EventHandler
    public static void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            List<FKPlayer> fps = FKManager.getGlobalPlayer(p.getUniqueId(), p.getName());
            if (fps.isEmpty()) {
                event.setCancelled(true);
                return;
            }

            for (FKPlayer fp : fps) {
                if (fp == null) {
                    event.setCancelled(true);
                    return;
                }


                if (event.getEntity() instanceof Player) {
                    Player e = (Player) event.getEntity();
                    List<FKPlayer> fes = FKManager.getGlobalPlayer(e.getUniqueId(), e.getName());
                    if (fes.isEmpty()) {
                        event.setCancelled(true);
                        return;
                    }

                    for (FKPlayer fe : fes)
                        if (fe == null || !fp.hasPermission(fp.getTeam().getPlayers().contains(fe) ? FKPermissions.Type.FRIENDLY_FIRE : FKPermissions.Type.PVP, e.getLocation())) {
                            event.setCancelled(true);
                            return;
                        }
                } else {
                    if (!fp.hasPermission(FKPermissions.Type.MOBS, event.getEntity().getLocation())) {
                        event.setCancelled(true);
                        return;
                    }

                    event.getEntity().setMetadata(Events.lastDamageLootingLevelTag,
                            new FixedMetadataValue(Main.instance, p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)));
                    event.getEntity().setMetadata(Events.lastDamageSilkTouchTag,
                            new FixedMetadataValue(Main.instance, p.getItemInHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0));
                }
            }
        }

        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            List<FKPlayer> fps = FKManager.getGlobalPlayer(p.getUniqueId(), p.getName());
            if (fps.isEmpty()) {
                event.setCancelled(true);
                return;
            }

            for (FKPlayer fp : fps)
                if (fp != null) {
                    fp.getStats().increaseDamageDealt(event.getFinalDamage());
                    if (event.getEntity() instanceof Player && event.getCause() == EntityDamageByEntityEvent.DamageCause.PROJECTILE)
                        fp.getStats().increaseArrowsHit();
                    if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getHealth() - event.getFinalDamage() <= 0)
                        fp.getStats().increaseKills();
                }
        }
    }

}
