package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.utils.CustomNBT;
import fr.luzog.pl.fkx.utils.Limits;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InventoryClickHandler implements Listener {

    @EventHandler
    public static void onClick(InventoryClickEvent e) {
// DEPRECATED: Potion Limitations (Not needed anymore)
//        if (e.getInventory().getType() == InventoryType.BREWING
//                && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.POTION
//                && (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT)) {
//            e.setCancelled(true);
//            return;
//        }
//        if (e.getInventory().getType() == InventoryType.BREWING
//                && (e.getCurrentItem().getType() == Material.GLOWSTONE_DUST || (e.getCurrentItem().getType() == Material.AIR
//                && e.getCursor() != null && e.getCursor().getType() == Material.POTION
//                && (e.getSlot() == 0 || e.getSlot() == 1 || e.getSlot() == 2)))
//                && GManager.getCurrentGame() != null && GManager.getCurrentGame().getLimits() != null
//                && GManager.getCurrentGame().getPlayer(e.getWhoClicked().getName(), false) != null) {
//            Limits lim = GManager.getCurrentGame().getLimits();
//            HashMap<PotionEffectType, Integer> p = new HashMap<>();
//            for (ItemStack is : Arrays.asList(e.getCursor(), e.getInventory().getItem(0),
//                    e.getInventory().getItem(1), e.getInventory().getItem(2)))
//                if (is != null && is.getType() == Material.POTION) {
//                    Potion.fromItemStack(is).getEffects().forEach(pe -> {
//                        if (p.containsKey(pe.getType()))
//                            p.replace(pe.getType(), Math.max(pe.getAmplifier(), p.get(pe.getType())));
//                        else
//                            p.put(pe.getType(), pe.getAmplifier());
//                    });
//                }
//            for (PotionEffectType pet : p.keySet())
//                if (lim.getPotion().containsKey(pet) && p.get(pet) + 1 > lim.getPotion().get(pet)) {
//                    e.setCancelled(true);
//                    return;
//                }
//        }

        if (GManager.getCurrentGame() != null && e.getInventory().getType() == InventoryType.ANVIL
                && e.getRawSlot() == 2 && e.getCurrentItem() != null) {
            for (Enchantment ench : GManager.getCurrentGame().getLimits().getEnchantGlobal().keySet())
                if (e.getCurrentItem().containsEnchantment(ench) && e.getCurrentItem().getEnchantmentLevel(ench)
                        > GManager.getCurrentGame().getLimits().getEnchantGlobal().get(ench)) {
                    e.setCancelled(true);
                    return;
                }
            for (Enchantment ench : GManager.getCurrentGame().getLimits().getEnchantSpe().keySet()) {
                System.out.println(ench.getName());
                for (Material mat : GManager.getCurrentGame().getLimits().getEnchantSpe().get(ench).keySet())
                    if (e.getCurrentItem().getType() == mat && e.getCurrentItem().containsEnchantment(ench)
                            && e.getCurrentItem().getEnchantmentLevel(ench)
                            > GManager.getCurrentGame().getLimits().getEnchantSpe().get(ench).get(mat)) {
                        e.setCancelled(true);
                        return;
                    }
            }
        }

        GPlayer gp = null;
        if (GManager.getCurrentGame() != null
                && (gp = GManager.getCurrentGame().getPlayer(e.getWhoClicked().getName(), false)) != null) {
            gp.getStats().increaseClicksOnInventory();
        }

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR)
            if (e.getWhoClicked() instanceof Player) {
                CustomNBT nbt = new CustomNBT(e.getCurrentItem());
                if (nbt.hasKey(Events.closeTag) && nbt.getBoolean(Events.closeTag))
                    e.getWhoClicked().closeInventory();
                if (nbt.hasKey(Events.cantClickOnTag) && nbt.getBoolean(Events.cantClickOnTag))
                    e.setCancelled(true);
                int i = 0;
                if (e.getClick() == ClickType.LEFT && nbt.hasKey(Events.exeLeftTag) && nbt.getString(Events.exeLeftTag) != null)
                    for (String s : nbt.getString(Events.exeLeftTag).split("\n")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (s.equalsIgnoreCase("exit"))
                                    e.getWhoClicked().closeInventory();
                                else if (!s.equalsIgnoreCase("null"))
                                    ((Player) e.getWhoClicked()).performCommand(s);
                            }
                        }.runTaskLater(Main.instance, i);
                        i++;
                    }
                if (e.getClick() == ClickType.SHIFT_LEFT && nbt.hasKey(Events.exeShiftLeftTag) && nbt.getString(Events.exeShiftLeftTag) != null)
                    for (String s : nbt.getString(Events.exeShiftLeftTag).split("\n")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (s.equalsIgnoreCase("exit"))
                                    e.getWhoClicked().closeInventory();
                                else if (!s.equalsIgnoreCase("null"))
                                    ((Player) e.getWhoClicked()).performCommand(s);
                            }
                        }.runTaskLater(Main.instance, i);
                        i++;
                    }
                if (e.getClick() == ClickType.RIGHT && nbt.hasKey(Events.exeRightTag) && nbt.getString(Events.exeRightTag) != null)
                    for (String s : nbt.getString(Events.exeRightTag).split("\n")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (s.equalsIgnoreCase("exit"))
                                    e.getWhoClicked().closeInventory();
                                else if (!s.equalsIgnoreCase("null"))
                                    ((Player) e.getWhoClicked()).performCommand(s);
                            }
                        }.runTaskLater(Main.instance, i);
                        i++;
                    }
                if (e.getClick() == ClickType.SHIFT_RIGHT && nbt.hasKey(Events.exeShiftRightTag) && nbt.getString(Events.exeShiftRightTag) != null)
                    for (String s : nbt.getString(Events.exeShiftRightTag).split("\n")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (s.equalsIgnoreCase("exit"))
                                    e.getWhoClicked().closeInventory();
                                else if (!s.equalsIgnoreCase("null"))
                                    ((Player) e.getWhoClicked()).performCommand(s);
                            }
                        }.runTaskLater(Main.instance, i);
                        i++;
                    }
                if (e.getClick() == ClickType.MIDDLE && nbt.hasKey(Events.exeMiddleTag) && nbt.getString(Events.exeMiddleTag) != null)
                    for (String s : nbt.getString(Events.exeMiddleTag).split("\n")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (s.equalsIgnoreCase("exit"))
                                    e.getWhoClicked().closeInventory();
                                else if (!s.equalsIgnoreCase("null"))
                                    ((Player) e.getWhoClicked()).performCommand(s);
                            }
                        }.runTaskLater(Main.instance, i);
                        i++;
                    }
            }

//      TODO : there is a problem of duplication :
//        When the player pickup an item, idk why but, if it's clicked, no event is called.
//        ... So for the next click (to complete the drag), the rollback notice an extra item.
//        Eventually, the item is duplicated. ->> Big problem, but I don't have the time to patch
//          it. I already spent about 1h30 so tht enough sorry, TO DO IN TIMES TO COME.
//
//        if (FKManager.getCurrentGame() == null || FKManager.getCurrentGame().getLimits() == null
//                || FKManager.getCurrentGame().getPlayer(e.getWhoClicked().getName(), false) == null)
//            return;
//
//        Limits lim = FKManager.getCurrentGame().getLimits();
//        int d = Limits.diamondScore(e.getWhoClicked()),
//                g = Limits.goldScore(e.getWhoClicked()),
//                i = Limits.ironScore(e.getWhoClicked()),
//                l = Limits.leatherScore(e.getWhoClicked());
//
//        ItemStack[] rollBackInv = e.getWhoClicked().getInventory().getContents(),
//                rollBackArmor = e.getWhoClicked().getInventory().getArmorContents();
//        ItemStack rollBackCursor = e.getCursor();
//
//        System.out.println("test");
//
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                int dd = Limits.diamondScore(e.getWhoClicked()),
//                        gg = Limits.goldScore(e.getWhoClicked()),
//                        ii = Limits.ironScore(e.getWhoClicked()),
//                        ll = Limits.leatherScore(e.getWhoClicked());
//                System.out.println(Arrays.stream(rollBackInv).filter(Objects::nonNull).map(ItemStack::getType).map(Enum::name).collect(Collectors.toList()));
//                System.out.println(Arrays.stream(rollBackArmor).filter(Objects::nonNull).map(ItemStack::getType).map(Enum::name).collect(Collectors.toList()));
//                System.out.println(rollBackCursor.getType().name());
//
//                System.out.println(d + " / " + dd);
//
//                if ((dd > d && dd > lim.getWearingMaxDiamondPieces())
//                        || (gg > g && gg > lim.getWearingMaxGoldPieces())
//                        || (ii > i && ii > lim.getWearingMaxIronPieces())
//                        || (ll > l && ll > lim.getWearingMaxLeatherPieces())) {
//                    System.out.println("Cancel !");
//                    e.getWhoClicked().getInventory().setContents(rollBackInv);
//                    e.getWhoClicked().getInventory().setArmorContents(rollBackArmor);
//                    if (e.getWhoClicked() instanceof Player)
//                        ((Player) e.getWhoClicked()).updateInventory();
//                    e.getWhoClicked().getWorld().dropItem(e.getWhoClicked().getLocation(), rollBackCursor);
//                }
//            }
//        }.runTaskLater(Main.instance, 1);
    }

}
