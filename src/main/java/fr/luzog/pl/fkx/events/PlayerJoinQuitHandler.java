package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static fr.luzog.pl.fkx.utils.SpecialChars.*;

public class PlayerJoinQuitHandler implements Listener {
    public static String join = "§8§l[§a§l+§8§l] §7";
    public static String quit = "§8§l[§c§l-§8§l] §7";
    public static String warn = "§8§l[§6§l" + WARNING + "§8§l] §7";
    public static String ban = "§8§l[§4§l" + FF_EXCALMATION + FF_EXCALMATION + "§8§l] §7";

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);

        ArrayList<GPlayer> gPlayers = GManager.getGlobalPlayer(e.getPlayer().getName());

        for(GPlayer gPlayer : gPlayers) {
            gPlayer.getStats().increaseConnections();
            if(gPlayer.getLastUuid() == null || !gPlayer.getLastUuid().equals(e.getPlayer().getUniqueId()))
                gPlayer.setLastUuid(e.getPlayer().getUniqueId(), true);
            if(!gPlayer.getName().equals(e.getPlayer().getName()))
                gPlayer.setName(e.getPlayer().getName(), true);
            if(gPlayer.getTeam() != null)
                gPlayer.getTeam().updatePlayers();
        }

        String displayName = gPlayers.isEmpty() ? e.getPlayer().getName()
                : gPlayers.size() > 1 ? gPlayers.stream().map(GPlayer::getDisplayName).collect(Collectors.joining("§r"))
                : gPlayers.get(0).getDisplayName();
        Bukkit.broadcastMessage(join + displayName);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.setQuitMessage(null);

        ArrayList<GPlayer> gPlayers = GManager.getGlobalPlayer(e.getPlayer().getName());

        String displayName = gPlayers.isEmpty() ? e.getPlayer().getName()
                : gPlayers.size() > 1 ? gPlayers.stream().map(GPlayer::getDisplayName).collect(Collectors.joining("§r"))
                : gPlayers.get(0).getDisplayName();
        Bukkit.broadcastMessage((e.getPlayer().isBanned() ? ban : quit) + displayName);

        new BukkitRunnable() {
            @Override
            public void run() {
                for(GPlayer gPlayer : gPlayers) {
                    if(!gPlayer.getName().equals(e.getPlayer().getName()))
                        gPlayer.setName(e.getPlayer().getName(), true);
                    if(gPlayer.getTeam() != null)
                        gPlayer.getTeam().updatePlayers();
                }
            }
        }.runTask(Main.instance);
    }

}
