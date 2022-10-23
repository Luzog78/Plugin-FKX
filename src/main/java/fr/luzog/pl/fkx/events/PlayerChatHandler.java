package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class PlayerChatHandler implements Listener {

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) {
        e.setCancelled(true);
        GPlayer fp;
        if (GManager.getCurrentGame() == null
                || (fp = GManager.getCurrentGame().getPlayer(e.getPlayer().getName(), false)) == null) {
            e.getPlayer().sendMessage("§cVous n'êtes pas dans le jeu.");
            return;
        }

        boolean teamChat = !e.getMessage().startsWith("!");
        int sub = e.getMessage().startsWith("!") ? 1 : 0;

        String format = (teamChat ? "§7[§aT§7]  §f" : "")
                + fp.getDisplayName() + "§8  >>  §7"
                + e.getMessage().substring(sub).replace("&", "§").replace("§§", "&");

        fp.getStats().increaseChats();

        if (!teamChat)
            GManager.getCurrentGame().getPlayers().stream().filter(p -> p.getPlayer() != null)
                    .forEach(p -> p.getPlayer().sendMessage(format));
        else
            GManager.getCurrentGame().getPlayers().forEach(p -> {
                if (p != null && p.getPlayer() != null && p.getTeam() != null && (p.getTeam().getId().equalsIgnoreCase(fp.getTeamId())
                        || p.getTeam().getId().equals(fp.getManager().getGods().getId())))
                    p.getPlayer().sendMessage(format);
            });

        Bukkit.getLogger().warning("Chat: " + (teamChat ? "[T] " : "    ")
                + fp.getDisplayName() + " >> " + e.getMessage().substring(sub));
    }

}
