package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class Guis {

    public static String loreSeparator = "------------------";

    /**
     * It creates an item with the material `BARRIER` and the name `Fermer` (close in french) and the lore `Cliquez pour
     * fermer l'inventaire.` (Click to close the inventory in french)
     *
     * @return An {@link ItemStack}
     */
    public static ItemStack close() {
        return Items.builder(Material.BARRIER)
                .setName("§cFermer")
                .setLore("§8" + loreSeparator, "§7Cliquez pour fermer l'inventaire.")
                .setCantClickOn(true)
                .setCloseOnClick(true)
                .build();
    }

    /**
     * It creates an item with the material BARRIER, the name "Retour" and the lore "Cliquez pour retourner à l'inventaire
     * précédent.". It also sets the NBT tag "exe" to the command "back"
     *
     * @param command The command to execute when the item is clicked.
     *
     * @return An {@link ItemStack}
     */
    public static ItemStack back(String command) {
        return Items.builder(Material.ARROW)
                .setName("§7Retour")
                .setLore("§8" + loreSeparator, "§7Cliquez pour retourner à", "§7 l'inventaire précédent.", " ", "§7Commande:", "§7" + command)
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    /**
     * It creates an itemstack with the material of an ender pearl or an eye of ender, depending on the boolean parameter,
     * with a name and a lore, and when clicked, it executes a command
     *
     * @param here Whether the item is a "teleport here" item or a "teleport to" item
     * @param command The command to execute when the item is clicked.
     * @return An ItemStack
     */
    public static ItemStack tp(boolean here, String command) {
        return Items.builder(here ? Material.ENDER_PEARL : Material.EYE_OF_ENDER)
                .setName(here ? "§5Téléporter ici" : "§dSe téléporter")
                .setLore("§8" + loreSeparator, "§7Cliquez pour " + (here ? "téléporter ici" : "se téléporter"))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    /**
     * It creates an inventory with a name, size, back button, main item, and second item
     *
     * @param name   The name of the inventory
     * @param size   The size of the inventory.
     * @param back   The command to access the inventory you want to go back to.
     * @param main   The item that will be in the middle-top of the inventory.
     * @param second The item that will be in the middle-bottom corner of the inventory.
     *
     * @return An {@link Inventory}
     */
    public static Inventory getBaseInventory(String name, int size, String back, @Nullable ItemStack main, @Nullable ItemStack second) {
        Inventory inv = Bukkit.createInventory(null, size, name);
        Utils.fill(inv, 0, size - 1, false, Items.gray());
        Utils.fill(inv, 0, size - 1, true, Items.blue());
        inv.setItem(3, Items.orange());
        inv.setItem(5, Items.orange());
        if (second != null)
            inv.setItem(size - 5, second);
        inv.setItem(4, main == null ? Items.l_gray() : main);
        inv.setItem(8, close());
        if (back != null)
            inv.setItem(0, back(back));
        return inv;
    }

    /**
     * It creates an inventory with a red background, a red title, a red error message, and a red close button
     *
     * @param error The error message to display.
     * @param back The command to execute when the player clicks on the error message.
     * @return An inventory
     */
    public static Inventory getErrorInventory(String error, String back) {
        Inventory inv = Bukkit.createInventory(null, 27, "§cErreur");
        Utils.fill(inv, 0, 26, Items.builder(Material.DEAD_BUSH)
                .setName("§4§l" + SpecialChars.NO + " Erreur " + SpecialChars.NO)
                .setLore("§8" + loreSeparator, " ",
                        "§c" + (error == null ? "Erreur non reconnue.\n§cRessayez plus tard." : error),
                        " ", "§8" + loreSeparator, "§7Cliquez pour " + (back == null ? "fermer" : "retourner à\n§7l'inventaire précédent") + ".")
                .setCantClickOn(true)
                .setGlobalCommandOnClick(back)
                .build());
        inv.setItem(8, close());
        return inv;
    }

}