package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPermissions;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.game.GTeam;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.List;

public class BucketHandler implements Listener {

    @EventHandler
    public static void onEmpty(PlayerBucketEmptyEvent e) {
        List<GPlayer> fps = GManager.getGlobalPlayer(e.getPlayer().getName());
        if (fps.isEmpty()) {
            e.setCancelled(true);
            return;
        }

        for (GPlayer fp : fps) {
            if (fp != null && ((fp.getManager().getState() == GManager.State.PAUSED
                    && !fp.getTeam().getId().equals(fp.getManager().getGods().getId()))
                    || !fp.hasPermission(Events.specialMat.contains(e.getBucket()) ? GPermissions.Type.PLACESPE :
                    GPermissions.Type.PLACE, Utils.normalize(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation()))
                    || fp.getManager().getTeams().stream().anyMatch(t -> t.isInside(e.getBlockClicked().getLocation())
                    && (!t.getId().equals(fp.getTeam().getId()) || t.getId().equals(GTeam.GODS_ID)) ))) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public static void onFill(PlayerBucketFillEvent e) {
        List<GPlayer> fps = GManager.getGlobalPlayer(e.getPlayer().getName());
        if (fps.isEmpty()) {
            e.setCancelled(true);
            return;
        }

        for (GPlayer fp : fps) {
            if (fp != null && ((fp.getManager().getState() == GManager.State.PAUSED
                    && !fp.getTeam().getId().equals(fp.getManager().getGods().getId()))
                    || !fp.hasPermission(Events.specialMat.contains(e.getBucket()) ? GPermissions.Type.BREAKSPE :
                    GPermissions.Type.BREAK, Utils.normalize(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation()))
                    || fp.getManager().getTeams().stream().anyMatch(t ->
                    t.isInside(e.getBlockClicked().getLocation()) && (!t.getId().equals(fp.getTeam().getId()) || t.getId().equals(GTeam.GODS_ID))
            ))) {
                e.setCancelled(true);
                return;
            }
        }
    }

}
