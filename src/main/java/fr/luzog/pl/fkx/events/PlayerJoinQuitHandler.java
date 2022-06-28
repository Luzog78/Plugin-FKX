package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
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

        ArrayList<FKPlayer> fkps = FKManager.getGlobalPlayer(e.getPlayer().getName());

        for(FKPlayer fkp : fkps) {
            fkp.getStats().increaseConnections();
            if(!fkp.getLastUuid().equals(e.getPlayer().getUniqueId()))
                fkp.setLastUuid(e.getPlayer().getUniqueId(), true);
            if(!fkp.getName().equals(e.getPlayer().getName()))
                fkp.setName(e.getPlayer().getName(), true);
            if(fkp.getTeam() != null)
                fkp.getTeam().updatePlayers();
        }

        String displayName = fkps.isEmpty() ? e.getPlayer().getName()
                : fkps.size() > 1 ? fkps.stream().map(FKPlayer::getDisplayName).collect(Collectors.joining("§r"))
                : fkps.get(0).getDisplayName();
        Bukkit.broadcastMessage(join + displayName);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.setQuitMessage(null);

        ArrayList<FKPlayer> fkps = FKManager.getGlobalPlayer(e.getPlayer().getName());

        String displayName = fkps.isEmpty() ? e.getPlayer().getName()
                : fkps.size() > 1 ? fkps.stream().map(FKPlayer::getDisplayName).collect(Collectors.joining("§r"))
                : fkps.get(0).getDisplayName();
        Bukkit.broadcastMessage((e.getPlayer().isBanned() ? ban : quit) + displayName);

        new BukkitRunnable() {
            @Override
            public void run() {
                for(FKPlayer fkp : fkps) {
                    if(!fkp.getName().equals(e.getPlayer().getName()))
                        fkp.setName(e.getPlayer().getName(), true);
                    if(fkp.getTeam() != null)
                        fkp.getTeam().updatePlayers();
                }
            }
        }.runTask(Main.instance);
    }

}
