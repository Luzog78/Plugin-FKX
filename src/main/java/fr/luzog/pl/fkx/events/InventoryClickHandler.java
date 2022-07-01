package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.CustomNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class InventoryClickHandler implements Listener {

    @EventHandler
    public static void onClick(InventoryClickEvent e) {
        List<FKPlayer> fps = FKManager.getGlobalPlayer(e.getWhoClicked().getName());

        for (FKPlayer fp : fps)
            if (fp != null)
                fp.getStats().increaseClicksOnInventory();

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR)
            if (e.getWhoClicked() instanceof Player) {
                CustomNBT nbt = new CustomNBT(e.getCurrentItem());
                if (nbt.hasKey(Events.closeTag) && nbt.getBoolean(Events.closeTag))
                    e.getWhoClicked().closeInventory();
                if (nbt.hasKey(Events.cantClickOnTag) && nbt.getBoolean(Events.cantClickOnTag))
                    e.setCancelled(true);
                if (e.getClick() == ClickType.LEFT && nbt.hasKey(Events.exeLeftTag) && nbt.getString(Events.exeLeftTag) != null)
                    for (String s : nbt.getString(Events.exeLeftTag).split("\n"))
                        if (!s.equalsIgnoreCase("null"))
                            ((Player) e.getWhoClicked()).performCommand(s);
                if (e.getClick() == ClickType.SHIFT_LEFT && nbt.hasKey(Events.exeShiftLeftTag) && nbt.getString(Events.exeShiftLeftTag) != null)
                    for (String s : nbt.getString(Events.exeShiftLeftTag).split("\n"))
                        if (!s.equalsIgnoreCase("null"))
                            ((Player) e.getWhoClicked()).performCommand(s);
                if (e.getClick() == ClickType.RIGHT && nbt.hasKey(Events.exeRightTag) && nbt.getString(Events.exeRightTag) != null)
                    for (String s : nbt.getString(Events.exeRightTag).split("\n"))
                        if (!s.equalsIgnoreCase("null"))
                            ((Player) e.getWhoClicked()).performCommand(s);
                if (e.getClick() == ClickType.SHIFT_RIGHT && nbt.hasKey(Events.exeShiftRightTag) && nbt.getString(Events.exeShiftRightTag) != null)
                    for (String s : nbt.getString(Events.exeShiftRightTag).split("\n"))
                        if (!s.equalsIgnoreCase("null"))
                            ((Player) e.getWhoClicked()).performCommand(s);
                if (e.getClick() == ClickType.MIDDLE && nbt.hasKey(Events.exeMiddleTag) && nbt.getString(Events.exeMiddleTag) != null)
                    for (String s : nbt.getString(Events.exeMiddleTag).split("\n"))
                        if (!s.equalsIgnoreCase("null"))
                            ((Player) e.getWhoClicked()).performCommand(s);
            }
    }

}
