package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

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
                .setLore("§8" + loreSeparator, "§7Clic pour fermer l'inv")
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
                .setLore(
                        "§8" + loreSeparator,
                        "§7Clic pour retourner",
                        "§7 à l'inv précédent.",
                        " ",
                        "§7Commande :",
                        "§7/" + command)
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    /**
     * It creates an itemstack with the material of an ender pearl or an eye of ender, depending on the boolean parameter,
     * with a name and a lore, and when clicked, it executes a command
     *
     * @param here    Whether the item is a "teleport here" item or a "teleport to" item
     * @param command The command to execute when the item is clicked.
     *
     * @return An {@link ItemStack}
     */
    public static ItemStack tp(boolean here, String command) {
        return Items.builder(here ? Material.ENDER_PEARL : Material.EYE_OF_ENDER)
                .setName(here ? "§5Téléporter ici" : "§dSe téléporter")
                .setLore(
                        "§8" + loreSeparator,
                        "§7Clic pour " + (here ? "téléporter ici" : "se téléporter"),
                        " ",
                        "§7Commande :",
                        "§7/" + command
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    /**
     * It creates a feather item with the name "Navigation" and the lore "Avec x item/page" and "Page y/z"
     * to help the user to navigate through the inventory.
     *
     * @param page     The current page
     * @param maxPage  The maximum page number
     * @param pageSize The number of items per page
     *
     * @return An {@link ItemStack}
     */
    public static ItemStack navigationFeather(int page, int maxPage, int pageSize) {
        return Items.builder(Material.FEATHER)
                .setName("§fNavigation")
                .setLore(
                        "§8" + loreSeparator,
                        "§8Avec " + pageSize + " item/page",
                        " ",
                        "§8Page :  §f" + (page + (maxPage == 0 ? 0 : 1)) + "§7/" + maxPage
                )
                .setCantClickOn(true)
                .build();
    }

    /**
     * It creates an arrow item that will execute a command when clicked, to navigate to the previous or next pages.
     *
     * @param page        The current page
     * @param maxPage     The maximum page number
     * @param previous    If the arrow redirect to the previous page or the next page
     * @param baseCommand The command to execute when the player clicks on the item.
     *
     * @return An {@link ItemStack}
     */
    public static ItemStack navigationArrow(int page, int maxPage, boolean previous, String baseCommand) {
        return Items.builder(Material.ARROW)
                .setName(previous ? "§fPage Précédente" : "§fPage Suivante")
                .setLore(
                        "§8" + loreSeparator,
                        "§7Clic pour aller à  §f" + (page + (previous ? -1 : 1) + 1) + "§8/" + maxPage,
                        "§7  (Shift pour aller à  §f" + (previous ? 1 : maxPage) + "§8/" + maxPage + "§7)",
                        " ",
                        "§7Commandes :",
                        "§7/" + baseCommand + " " + (page + (previous ? -1 : 1)),
                        "§7/" + baseCommand + " " + (previous ? 0 : maxPage - 1)
                )
                .setCantClickOn(true)
                .setShiftCommandOnClick(
                        baseCommand + " " + (page + (previous ? -1 : 1)),
                        baseCommand + " " + (previous ? 0 : maxPage - 1)
                )
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
     * @param back  The command to execute when the player clicks on the error message.
     *
     * @return An {@link Inventory}
     */
    public static Inventory getErrorInventory(String error, String back) {
        Inventory inv = Bukkit.createInventory(null, 27, "§cErreur");
        Utils.fill(inv, 0, 26, Items.builder(Material.DEAD_BUSH)
                .setName("§4§l" + SpecialChars.NO + " Erreur " + SpecialChars.NO)
                .setLore(
                        "§8" + loreSeparator,
                        " ",
                        "§c" + (error == null ? "Erreur non reconnue.\n§cRessayez plus tard." : error),
                        " ",
                        "§8" + loreSeparator,
                        "§7Clic pour " + (back == null ? "fermer" : "retourner à\n§7l'inventaire précédent") + ".")
                .setCantClickOn(true)
                .setGlobalCommandOnClick(back)
                .build());
        inv.setItem(8, close());
        return inv;
    }

    /**
     * It creates an inventory with a navigation system
     *
     * @param name                  The name of the inventory
     * @param size                  The size of the inventory.
     * @param back                  The command to be executed when the back button is clicked.
     * @param main                  The item that will be in the middle of the top row.
     * @param second                The item that will be in the second slot.
     * @param navigationBaseCommand The command that will be executed when the player clicks on the navigation arrows.
     * @param page                  The current page
     * @param content               The content of the inventory.
     *
     * @return An {@link Inventory}
     */
    public static Inventory getPagedInventory(String name, int size, String back, @Nullable ItemStack main, @Nullable ItemStack second,
                                              String navigationBaseCommand, int page, Collection<ItemStack> content) {
        Inventory inv = Bukkit.createInventory(null, size, name);

        if (size == 9) {
            inv.setItem(0, back == null ? Items.orange() : back(back));
            inv.setItem(1, main == null ? Items.l_gray() : main);
            inv.setItem(2, second == null ? Items.orange() : second);

            inv.setItem(3, Items.blue());
            inv.setItem(5, Items.blue());

            inv.setItem(6, page == 0 ? Items.blue()
                    : navigationArrow(page, content.size(), true, navigationBaseCommand));
            inv.setItem(7, navigationFeather(page, content.size(), 1));
            inv.setItem(8, page + 1 == content.size() || content.size() == 0 ? Items.blue()
                    : navigationArrow(page, content.size(), false, navigationBaseCommand));

            try {
                inv.setItem(4, new ArrayList<>(content).get(page));
            } catch (IndexOutOfBoundsException e) {
                inv.setItem(4, Items.gray());
            }

            return inv;
        } else if (size == 18) {
            inv.setItem(4, main == null ? Items.l_gray() : main);
            inv.setItem(5, Items.orange());

            if (second != null) {
                inv.setItem(3, second);
                inv.setItem(2, Items.orange());
            } else {
                inv.setItem(3, Items.orange());
                inv.setItem(2, Items.blue());
            }

            inv.setItem(0, back == null ? Items.blue() : back(back));

            for (int i : Arrays.asList(1, 9, 10, 16, 17))
                inv.setItem(i, Items.blue());

            int isPerPage = 5;
            int max = ((int) (content.size() / (isPerPage + 0.0))) + (content.size() % isPerPage == 0 ? 0 : 1);

            inv.setItem(6, page == 0 ? Items.blue()
                    : navigationArrow(page, max, true, navigationBaseCommand));
            inv.setItem(7, navigationFeather(page, max, isPerPage));
            inv.setItem(8, page + 1 == max || max == 0 ? Items.blue()
                    : navigationArrow(page, max,false, navigationBaseCommand));

            int i = 0, start = 11;
            while (inv.firstEmpty() != -1) {
                try {
                    inv.setItem(start + i, new ArrayList<>(content).get(page * isPerPage + i));
                } catch (IndexOutOfBoundsException e) {
                    inv.setItem(start + i, Items.gray());
                }
                i++;
            }
            return inv;
        } else {
            Utils.fill(inv, 0, size - 1, true, Items.blue());
            for (int i : Arrays.asList(3, 5))
                inv.setItem(i, Items.orange());
            inv.setItem(4, main == null ? Items.l_gray() : main);
            inv.setItem(size - 6, second == null ? Items.orange() : second);
            inv.setItem(0, back == null ? Items.blue() : back(back));
            inv.setItem(8, close());

            int isPerPage = size - 2 * ((size - 18) / 9) - 18;
            int max = ((int) (content.size() / (isPerPage + 0.0))) + (content.size() % isPerPage == 0 ? 0 : 1);

            inv.setItem(size - 9, page == 0 ? Items.blue()
                    : navigationArrow(page, max, true, navigationBaseCommand));
            inv.setItem(size - 5, navigationFeather(page, max, isPerPage));
            inv.setItem(size - 1, page + 1 == max || max == 0 ? Items.blue()
                    : navigationArrow(page, max, false, navigationBaseCommand));

            int i = 0;
            while (i < isPerPage) {
                try {
                    inv.setItem(Utils.posOf((i % 7) + 1, (i / 7) + 1),
                            new ArrayList<>(content).get(page * isPerPage + i));
                } catch (IndexOutOfBoundsException ignore) {
                }
                i++;
            }
            for (int ii = 0; ii < size; ii++)
                if (inv.getItem(ii) == null || inv.getItem(ii).getType() == Material.AIR)
                    inv.setItem(ii, Items.gray());
            return inv;
        }
    }

}
