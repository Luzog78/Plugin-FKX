package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKAuth;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EntityDamageByEntityHandler implements Listener {

    @EventHandler
    public static void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            FKPlayer fp = FKManager.getCurrentGame().getPlayer(p.getUniqueId());

            if (fp == null) {
                event.setCancelled(true);
                return;
            }


            if (event.getEntity() instanceof Player) {
                Player e = (Player) event.getEntity();
                FKPlayer fe = FKManager.getCurrentGame().getPlayer(e.getUniqueId());

                if (!fp.hasAuthorization(fp.getTeam().getPlayers().contains(fe) ? FKAuth.Type.FRIENDLY_FIRE : FKAuth.Type.PVP, e.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            } else {
                if (!fp.hasAuthorization(FKAuth.Type.MOBS, event.getEntity().getLocation())) {
                    event.setCancelled(true);
                    return;
                }

                event.getEntity().setMetadata(Events.lastDamageLootingLevelTag,
                        new FixedMetadataValue(Main.instance, p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)));
                event.getEntity().setMetadata(Events.lastDamageSilkTouchTag,
                        new FixedMetadataValue(Main.instance, p.getItemInHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0));
            }
        }

        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            FKPlayer fp = FKManager.getCurrentGame().getPlayer(p.getUniqueId());

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
