package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKPermissions;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.fk.FKTeam;
import org.bukkit.GameMode;
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

            FKPlayer fp = FKManager.getCurrentGame() == null ? null : FKManager.getCurrentGame().getPlayer(p.getName(), false);
            /*
             *  FKTeam t;
             *  if (FKManager.getCurrentGame() != null && event.getEntity().hasMetadata(FKTeam.GUARDIAN_TAG)
             *          && fp != null && fp.getTeam() != null && (t = FKManager.getCurrentGame().getTeam(event.getEntity()
             *          .getMetadata(FKTeam.GUARDIAN_TAG).get(0).asString())) != null
             *          && (fp.getTeam().getId().equals(t.getId()) || fp.getTeam().getId().equals(FKTeam.GODS_ID))
             *          && p.isSneaking()) {
             *      t.setChestsRoom(t.getSpawn(), true, true);
             *      p.sendMessage("§aVous avez réinitialisé la position de votre gardien.");
             *      return;
             *  }
             */

            if (event.getEntity() instanceof Player) {
                Player e = (Player) event.getEntity();
                FKPlayer fe = FKManager.getCurrentGame() == null ? null : FKManager.getCurrentGame().getPlayer(e.getName(), false);

                if (fe == null || fp == null) {
                    if (!p.isOp() || p.getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                        return;
                    }
                } else if (!fp.hasPermission(fp.getTeam().getPlayers().contains(fe) ?
                        FKPermissions.Type.FRIENDLY_FIRE : FKPermissions.Type.PVP, e.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            } else {
                if ((event.getEntity().hasMetadata(FKTeam.GUARDIAN_TAG) && ((fp == null && !p.isOp())
                        || (fp != null && (fp.getTeam() == null || !fp.getTeam().getId().equals(FKTeam.GODS_ID)))))
                        || (!event.getEntity().hasMetadata(FKTeam.GUARDIAN_TAG) && ((fp == null && !p.isOp())
                        || (fp != null && !fp.hasPermission(FKPermissions.Type.MOBS,
                        event.getEntity().getLocation()))))) {
                    event.setCancelled(true);
                    return;
                }

                event.getEntity().setMetadata(Events.lastDamageLootingLevelTag,
                        new FixedMetadataValue(Main.instance, p.getItemInHand()
                                .getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)));
                event.getEntity().setMetadata(Events.lastDamageSilkTouchTag,
                        new FixedMetadataValue(Main.instance, p.getItemInHand()
                                .getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0));
            }

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
