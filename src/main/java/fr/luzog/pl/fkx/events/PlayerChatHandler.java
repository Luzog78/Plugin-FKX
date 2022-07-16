package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.List;

public class PlayerChatHandler implements Listener {

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) {
        e.setCancelled(true);
        List<FKPlayer> fkps = FKManager.getGlobalPlayer(e.getPlayer().getName());
        if (fkps.isEmpty()) {
            e.getPlayer().sendMessage("§cVous n'êtes pas dans le jeu.");
            return;
        }

        boolean teamChat = !e.getMessage().startsWith("!");
        int sub = e.getMessage().startsWith("!") ? 1 : 0;

        for (FKPlayer fkp : fkps) {
            String format = (teamChat ? "§7[§aT§7]  §f" : "")
                    + fkp.getDisplayName() + "§8  >>  §7"
                    + e.getMessage().substring(sub).replace("&", "§").replace("§§", "&");

            fkp.getStats().increaseChats();

            if (!teamChat)
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(format));
            else
                Bukkit.getOnlinePlayers().forEach(p -> {
                    List<FKPlayer> fps = FKManager.getGlobalPlayer(p.getName());
                    for (FKPlayer fp : fps)
                        if (fp != null && fp.getTeam() != null && (fp.getTeam().getId().equals(fkp.getTeam().getId())
                                || fp.getTeam().getId().equals(fkp.getManager().getGods().getId())))
                            p.sendMessage(format);
                });

            Bukkit.getLogger().warning("Chat: " + (teamChat ? "[T] " : "    ")
                    + fkp.getDisplayName() + " >> " + e.getMessage().substring(sub));
        }
    }

}
