package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.commands.Admin.Kick;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fr.luzog.pl.fkx.utils.SpecialChars.*;

public class PlayerJoinQuitHandler implements Listener {
    public static String join = "§8§l[§a§l+§8§l] §7";
    public static String quit = "§8§l[§c§l-§8§l] §7";
    public static String warn = "§8§l[§6§l" + WARNING + "§8§l] §7";
    public static String ban = "§8§l[§4§l" + FF_EXCALMATION + FF_EXCALMATION + "§8§l] §7";
    public static String connexionInventory = "connexion";
    public static int maxConnexionInventories = 10;

    public static List<String> silent = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);

        ArrayList<GPlayer> gPlayers = GManager.getGlobalPlayer(e.getPlayer().getName());

        for (GPlayer gPlayer : gPlayers) {
            gPlayer.getStats().increaseConnections();
            if (gPlayer.getLastUuid() == null || !gPlayer.getLastUuid().equals(e.getPlayer().getUniqueId()))
                gPlayer.setLastUuid(e.getPlayer().getUniqueId(), true);
            if (!gPlayer.getName().equals(e.getPlayer().getName()))
                gPlayer.setName(e.getPlayer().getName(), true);
            if (gPlayer.getTeam() != null)
                gPlayer.getTeam().updatePlayers();
        }

        if (GManager.getCurrentGame() != null) {
            GPlayer gPlayer = GManager.getCurrentGame().getPlayer(e.getPlayer().getName(), false);
            if (gPlayer != null) {
                Utils.SavedInventory savedInventory = gPlayer.getLastInventory(connexionInventory, false);
                if (savedInventory != null) {
                    savedInventory.equip(gPlayer.getPlayer(), true);
                } else {
                    gPlayer.getPlayer().getInventory().clear();
                    gPlayer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
                }
                gPlayer.getPlayer().updateInventory();
            }
        }

        if (!e.getPlayer().hasPlayedBefore() && GManager.getCurrentGame() != null)
            if (GManager.getCurrentGame().getState() == GManager.State.RUNNING
                    || GManager.getCurrentGame().getState() == GManager.State.PAUSED) {
                if (GManager.getCurrentGame().getPlayer(e.getPlayer().getName(), false) != null
                        && GManager.getCurrentGame().getParticipantsTeams().contains(
                        GManager.getCurrentGame().getPlayer(e.getPlayer().getName(), false).getTeam())
                        && GManager.getCurrentGame().getPlayer(e.getPlayer().getName(), false).getTeam().getSpawn() != null)
                    e.getPlayer().teleport(GManager.getCurrentGame()
                            .getPlayer(e.getPlayer().getName(), false).getTeam().getSpawn());
                else if (GManager.getCurrentGame().getSpawn().getSpawn() != null)
                    e.getPlayer().teleport(GManager.getCurrentGame().getSpawn().getSpawn());
                else if (GManager.getCurrentGame().getLobby().getSpawn() != null)
                    e.getPlayer().teleport(GManager.getCurrentGame().getLobby().getSpawn());
            } else if (GManager.getCurrentGame().getLobby().getSpawn() != null)
                e.getPlayer().teleport(GManager.getCurrentGame().getLobby().getSpawn());

        String displayName = gPlayers.isEmpty() ? e.getPlayer().getName()
                : gPlayers.size() > 1 ? gPlayers.stream().map(GPlayer::getDisplayName).collect(Collectors.joining("§r"))
                : gPlayers.get(0).getDisplayName();
        Bukkit.broadcastMessage(join + displayName);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.setQuitMessage(null);

        ArrayList<GPlayer> gPlayers = GManager.getGlobalPlayer(e.getPlayer().getName());

        if (GManager.getCurrentGame() != null) {
            GPlayer gPlayer = GManager.getCurrentGame().getPlayer(e.getPlayer().getName(), false);
            if (gPlayer != null) {
                gPlayer.saveInventory(connexionInventory, null, Main.SYS_PREFIX, gPlayer.getPlayer().getInventory());
                List<Utils.SavedInventory> invs = gPlayer.getInventories().stream().filter(inventory ->
                                inventory.getId().equals(connexionInventory)).collect(Collectors.toList());
                if (invs.size() > 1) {
                    int nb = (int) invs.stream().filter(inv -> inv.getCreator().equalsIgnoreCase(Main.SYS_PREFIX)).count();
                    if (nb > maxConnexionInventories) {
                        int counter = 0;
                        for (int i = invs.size() - 1; i >= 0; i--) {
                            if (invs.get(i).getCreator().equalsIgnoreCase(Main.SYS_PREFIX)) {
                                counter++;
                                if (counter > maxConnexionInventories) {
                                    gPlayer.deleteInventory(connexionInventory, i);
                                    counter--;
                                }
                            }
                        }
                    }
                }
            }
        }

        String displayName = gPlayers.isEmpty() ? e.getPlayer().getName()
                : gPlayers.size() > 1 ? gPlayers.stream().map(GPlayer::getDisplayName).collect(Collectors.joining("§r"))
                : gPlayers.get(0).getDisplayName();
        if (silent.contains(e.getPlayer().getName())) {
            silent.remove(e.getPlayer().getName());
        } else {
            Bukkit.broadcastMessage(quit + displayName);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (GPlayer gPlayer : gPlayers) {
                    if (!gPlayer.getName().equals(e.getPlayer().getName()))
                        gPlayer.setName(e.getPlayer().getName(), true);
                    if (gPlayer.getTeam() != null)
                        gPlayer.getTeam().updatePlayers();
                }
            }
        }.runTask(Main.instance);
    }

}
