package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPermissions;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.game.GTeam;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Objects;

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

        if (!fp.hasPermission(teamChat ? GPermissions.Type.CHAT_TEAM : GPermissions.Type.CHAT_GLOBAL,
                e.getPlayer().getLocation())) {
            e.getPlayer().sendMessage("§cVous n'avez pas la permission de parler en §6" + (teamChat ? "équipe" : "global") + "§c.");
            return;
        }

        String format = (teamChat ? "§7[§aT§7]  §f" : "")
                + fp.getDisplayName() + "§8  >>  §7"
                + e.getMessage().substring(sub).replace("&", "§").replace("§§", "&");

        fp.getStats().increaseChats();

        if (!teamChat)
            GManager.getCurrentGame().getPlayers().stream().filter(p -> p.getPlayer() != null)
                    .forEach(p -> p.getPlayer().sendMessage(format));
        else
            GManager.getCurrentGame().getPlayers().forEach(p -> {
                if (p != null && p.getPlayer() != null && (Objects.equals(p.getTeamId(), fp.getTeamId())
                        || Objects.equals(p.getTeamId(), GTeam.GODS_ID)))
                    p.getPlayer().sendMessage(format);
            });

        Bukkit.getLogger().warning("Chat: " + (teamChat ? "[T] " : "    ")
                + fp.getDisplayName() + " >> " + e.getMessage().substring(sub));
    }

}
