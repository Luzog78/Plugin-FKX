package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.fk.FKPermissions;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.List;

public class BucketHandler implements Listener {

    @EventHandler
    public static void onEmpty(PlayerBucketEmptyEvent e) {
        List<FKPlayer> fps = FKManager.getGlobalPlayer(e.getPlayer().getName());
        if (fps.isEmpty()) {
            e.setCancelled(true);
            return;
        }

        for (FKPlayer fp : fps) {
            if (fp != null && ((fp.getManager().getState() == FKManager.State.PAUSED && !fp.getTeam().getId().equals(fp.getManager().getGods().getId()))
                    || !fp.hasPermission(Events.specialMat.contains(e.getBucket()) ? FKPermissions.Type.PLACESPE :
                    FKPermissions.Type.PLACE, Utils.normalize(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation())))) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public static void onFill(PlayerBucketFillEvent e) {
        List<FKPlayer> fps = FKManager.getGlobalPlayer(e.getPlayer().getName());
        if (fps.isEmpty()) {
            e.setCancelled(true);
            return;
        }

        for (FKPlayer fp : fps) {
            if (fp != null && ((fp.getManager().getState() == FKManager.State.PAUSED && !fp.getTeam().getId().equals(fp.getManager().getGods().getId()))
                    || !fp.hasPermission(Events.specialMat.contains(e.getBucket()) ? FKPermissions.Type.BREAKSPE :
                    FKPermissions.Type.BREAK, Utils.normalize(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation())))) {
                e.setCancelled(true);
                return;
            }
        }
    }

}
