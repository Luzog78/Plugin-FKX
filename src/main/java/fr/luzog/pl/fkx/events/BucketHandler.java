package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.fk.FKAuth;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketHandler implements Listener {

    @EventHandler
    public static void onEmpty(PlayerBucketEmptyEvent e) {
        FKPlayer fp = FKManager.getCurrentGame().getPlayer(e.getPlayer().getUniqueId());
        if (fp == null || !fp.hasAuthorization(Events.specialMat.contains(e.getBucket()) ? FKAuth.Type.PLACESPE :
                FKAuth.Type.PLACE, Utils.normalize(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation()))) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public static void onFill(PlayerBucketFillEvent e) {
        FKPlayer fp = FKManager.getCurrentGame().getPlayer(e.getPlayer().getUniqueId());
        if (fp == null || !fp.hasAuthorization(Events.specialMat.contains(e.getBucket()) ? FKAuth.Type.BREAKSPE :
                FKAuth.Type.BREAK, Utils.normalize(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation()))) {
            e.setCancelled(true);
            return;
        }
    }

}
