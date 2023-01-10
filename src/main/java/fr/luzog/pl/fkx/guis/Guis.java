package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.utils.*;
import fr.luzog.pl.fkx.utils.Utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                                              String navigationBaseCommand, int page, List<ItemStack> content) {
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
                    : navigationArrow(page, max, false, navigationBaseCommand));

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

    public static Inventory getInvInventory(String name, String back, @Nullable ItemStack main, @Nullable ItemStack second,
                                            @Nullable ItemStack[] armor, ItemStack... content) {
        Inventory inv = Bukkit.createInventory(null, 54, name);
        Utils.fill(inv, 9, 35, false, Items.gray());
        Utils.fill(inv, 36, 44, false, Items.yellow());
        Utils.fill(inv, 48, 51, false, Items.pink());
        for (int i : Arrays.asList(0, 1, 2, 6, 7, 45, 46, 47, 52, 53))
            inv.setItem(i, Items.blue());
        inv.setItem(3, Items.orange());
        inv.setItem(5, Items.orange());
        if (second != null)
            inv.setItem(47, second);
        inv.setItem(4, main == null ? Items.l_gray() : main);
        inv.setItem(8, close());
        if (back != null)
            inv.setItem(0, back(back));

        if (armor != null) {
            ItemStack[] a = Stream.of(armor).map(is -> is == null || is.getType() == Material.AIR ?
                            null : Items.builder(is.clone()).setCantClickOn(true).build())
                    .limit(4).toArray(ItemStack[]::new);
            for (int i = 0; i < a.length; i++)
                if (a[a.length - 1 - i] != null)
                    inv.setItem(48 + i, a[a.length - 1 - i]);
        }

        ItemStack[] h = Stream.of(content).map(is -> is == null || is.getType() == Material.AIR ?
                        null : Items.builder(is.clone()).setCantClickOn(true).build())
                .limit(9).toArray(ItemStack[]::new);
        for (int i = 0; i < h.length; i++)
            if (h[i] != null)
                inv.setItem(36 + i, h[i]);

        ItemStack[] c = Stream.of(content).map(is -> is == null || is.getType() == Material.AIR ?
                        null : Items.builder(is.clone()).setCantClickOn(true).build())
                .skip(9).limit(27).toArray(ItemStack[]::new);
        for (int i = 0; i < c.length; i++)
            if (c[i] != null)
                inv.setItem(9 + i, c[i]);

        return inv;
    }

    private static String generateOptions(int horizontalPosition, List<Utils.Pair<Integer, List<ItemStack>>> content) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.size(); i++) {
            Utils.Pair<Integer, List<ItemStack>> pair = content.get(i);
            if (pair.getKey() != 0)
                sb.append(i).append(":").append(pair.getKey()).append(",");
        }
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return horizontalPosition + (sb.length() > 0 ? ";" + sb : "");
    }

    private static String generateOptions(int horizontalPosition, List<Utils.Pair<Integer, List<ItemStack>>> content, int idx, int diff) {
        List<Utils.Pair<Integer, List<ItemStack>>> temp = new ArrayList<>();
        content.forEach(pair -> temp.add(new Utils.Pair<>(pair.getKey(), new ArrayList<>(pair.getValue()))));
        try {
            temp.get(idx).setKey(temp.get(idx).getKey() + diff);
        } catch (IndexOutOfBoundsException ignored) {
        }
        return generateOptions(horizontalPosition, temp);
    }

    private static ItemStack getComplexHorizontalNavigationItem(int horizontalIndex, int page, int size,
                                                                int itemPerPage, boolean left,
                                                                String navigationBaseCommand, int horizontalPosition,
                                                                List<Utils.Pair<Integer, List<ItemStack>>> content) {
        String cmd = navigationBaseCommand + " " + generateOptions(horizontalPosition, content,
                horizontalIndex, left ? -1 : 1);
        return (left ? page <= 0 : page + itemPerPage >= size) ? Items.blue()
                : Items.builder((left ? Heads.ARROW_LEFT_OAK : Heads.ARROW_RIGHT_OAK).getSkull())
                .setName("§fAller vers la " + (left ? "gauche" : "droite"))
                .setLore(
                        "§8" + Utils.loreSeparator,
                        "§7Cliquez pour aller vers la " + (left ? "gauche" : "droite"),
                        " ",
                        "§8Page actuelle : §f" + (page + 1) + "§7/" + Math.max(1, size - itemPerPage + 1)
                )
                .setGlobalCommandOnClick(cmd)
                .setCantClickOn(true)
                .build();
    }

    public static Inventory getComplexScrollingInventory(String name, int size, String back,
                                                         @Nullable ItemStack main, @Nullable ItemStack second,
                                                         String navigationBaseCommand, int horizontalPosition,
                                                         List<Utils.Pair<Integer, List<ItemStack>>> content) {
        Inventory inv = Bukkit.createInventory(null, size, name);

        ItemStack outOfBounds = Items.builder(Items.l_gray())
                .setName("§7Pas d'item ici")
                .setLore("§8" + Utils.loreSeparator)
                .setCantClickOn(true)
                .build();

        int lines = (size <= 18 ? 1 : (size / 9) - 2);

        String cmdUp = navigationBaseCommand + " " + generateOptions(horizontalPosition - 1, content),
                cmdDown = navigationBaseCommand + " " + generateOptions(horizontalPosition + 1, content);

        ItemStack up = horizontalPosition <= 0 ? Items.blue()
                : Items.builder(Heads.ARROW_UP_OAK.getSkull())
                .setName("§fAller vers le haut")
                .setLore(
                        "§8" + Utils.loreSeparator,
                        "§7Cliquez pour aller vers le haut",
                        " ",
                        "§8Page actuelle : §f" + (horizontalPosition + 1) + "§7/" + Math.max(1, content.size() - lines + 1),
                        " ",
                        "§7Commande :",
                        "§7/" + cmdUp
                )
                .setGlobalCommandOnClick(cmdUp)
                .setCantClickOn(true)
                .build(),
                down = horizontalPosition + lines >= content.size() ? Items.blue()
                        : Items.builder(Heads.ARROW_DOWN_OAK.getSkull())
                        .setName("§fAller vers le bas")
                        .setLore(
                                "§8" + Utils.loreSeparator,
                                "§7Cliquez pour aller vers le bas",
                                " ",
                                "§8Page actuelle : §f" + (horizontalPosition + 1) + "§7/" + Math.max(1, content.size() - lines + 1),
                                " ",
                                "§7Commande :",
                                "§7/" + cmdDown
                        )
                        .setGlobalCommandOnClick(cmdDown)
                        .setCantClickOn(true)
                        .build();

        if (size == 9) {
            inv.setItem(0, back == null ? Items.blue() : back(back));
            inv.setItem(1, main == null ? Items.l_gray() : main);
            inv.setItem(2, second == null ? Items.orange() : second);
            inv.setItem(8, close());

            inv.setItem(3, up);
            inv.setItem(7, down);

            try {
                Utils.Pair<Integer, List<ItemStack>> line = content.get(horizontalPosition);

                inv.setItem(4, getComplexHorizontalNavigationItem(horizontalPosition,
                        line.getKey(), line.getValue().size(), 1, true,
                        navigationBaseCommand, horizontalPosition, content));
                inv.setItem(6, getComplexHorizontalNavigationItem(horizontalPosition,
                        line.getKey(), line.getValue().size(), 1, false,
                        navigationBaseCommand, horizontalPosition, content));

                ItemStack item = line.getValue().get(line.getKey());
                inv.setItem(5, item == null || item.getType() == Material.AIR ? Items.l_gray() : item);
            } catch (IndexOutOfBoundsException e) {
                inv.setItem(4, Items.blue());
                inv.setItem(6, Items.blue());
                inv.setItem(5, outOfBounds);
            }

            return inv;
        } else if (size == 18) {
            inv.setItem(0, back == null ? Items.blue() : back(back));
            inv.setItem(2, main == null ? Items.l_gray() : main);
            inv.setItem(3, second == null ? Items.orange() : second);
            inv.setItem(8, close());

            inv.setItem(1, Items.blue());
            inv.setItem(4, Items.blue());
            inv.setItem(7, Items.blue());

            inv.setItem(5, up);
            inv.setItem(6, down);

            try {
                Utils.Pair<Integer, List<ItemStack>> line = content.get(horizontalPosition);

                inv.setItem(9, getComplexHorizontalNavigationItem(horizontalPosition,
                        line.getKey(), line.getValue().size(), 7, true,
                        navigationBaseCommand, horizontalPosition, content));
                inv.setItem(17, getComplexHorizontalNavigationItem(horizontalPosition,
                        line.getKey(), line.getValue().size(), 7, false,
                        navigationBaseCommand, horizontalPosition, content));

                for (int i = 0; i < 7; i++) {
                    try {
                        ItemStack item = line.getValue().get(line.getKey() + i);
                        inv.setItem(i + 10, item == null || item.getType() == Material.AIR ? Items.l_gray() : item);
                    } catch (IndexOutOfBoundsException e) {
                        inv.setItem(i + 10, Items.l_gray());
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                inv.setItem(9, Items.blue());
                inv.setItem(17, Items.blue());
                for (int i = 0; i < 7; i++) {
                    inv.setItem(10 + i, outOfBounds);
                }
            }

            return inv;
        } else {
            inv.setItem(0, back == null ? Items.blue() : back(back));
            inv.setItem(4, main == null ? Items.l_gray() : main);
            inv.setItem(size - 5, second == null ? Items.blue() : second);
            inv.setItem(8, close());

            inv.setItem(1, Items.blue());
            inv.setItem(2, Items.blue());
            inv.setItem(6, Items.blue());
            inv.setItem(7, Items.blue());
            inv.setItem(size - 1, Items.blue());
            inv.setItem(size - 2, Items.blue());
            inv.setItem(size - 4, Items.blue());
            inv.setItem(size - 6, Items.blue());
            inv.setItem(size - 8, Items.blue());
            inv.setItem(size - 9, Items.blue());

            inv.setItem(3, Items.orange());
            inv.setItem(5, Items.orange());

            inv.setItem(size - 7, up);
            inv.setItem(size - 3, down);

            for (int lineIdx = 0; lineIdx < (size / 9) - 2; lineIdx++) {
                try {
                    Utils.Pair<Integer, List<ItemStack>> line = content.get(horizontalPosition + lineIdx);

                    inv.setItem((lineIdx * 9) + 9, getComplexHorizontalNavigationItem(horizontalPosition + lineIdx,
                            line.getKey(), line.getValue().size(), 7, true,
                            navigationBaseCommand, horizontalPosition, content));
                    inv.setItem((lineIdx * 9) + 17, getComplexHorizontalNavigationItem(horizontalPosition + lineIdx,
                            line.getKey(), line.getValue().size(), 7, false,
                            navigationBaseCommand, horizontalPosition, content));

                    for (int i = 0; i < 7; i++) {
                        try {
                            ItemStack item = line.getValue().get(line.getKey() + i);
                            inv.setItem((lineIdx * 9) + i + 10,
                                    item == null || item.getType() == Material.AIR ? Items.l_gray() : item);
                        } catch (IndexOutOfBoundsException e) {
                            inv.setItem((lineIdx * 9) + i + 10, Items.l_gray());
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    inv.setItem((lineIdx * 9) + 9, Items.blue());
                    inv.setItem((lineIdx * 9) + 17, Items.blue());
                    for (int i = 0; i < 7; i++) {
                        inv.setItem((lineIdx * 9) + i + 10, outOfBounds);
                    }
                }
            }

            return inv;
        }
    }

    public static String generateSelectorOptions(int page, List<Utils.Pair<String, Boolean>> content) {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, Boolean> pair : content) {
            if (pair.getValue())
                sb.append(pair.getKey()).append(",");
        }
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return page + (sb.length() > 0 ? ";" + sb : "");
    }

    public static ItemStack selectorNavigationArrow(int page, int maxPage, boolean previous,
                                                    ArrayList<Utils.Pair<String, Boolean>> content,
                                                    String baseCommand) {
        String cmd1 = baseCommand + " " + generateSelectorOptions(previous ? page - 1 : page + 1, content),
                cmd2 = baseCommand + " " + generateSelectorOptions(previous ? 0 : maxPage - 1, content);
        return Items.builder(Material.ARROW)
                .setName(previous ? "§fPage Précédente" : "§fPage Suivante")
                .setLore(
                        "§8" + loreSeparator,
                        "§7Clic pour aller à  §f" + (page + (previous ? -1 : 1) + 1) + "§8/" + maxPage,
                        "§7  (Shift pour aller à  §f" + (previous ? 1 : maxPage) + "§8/" + maxPage + "§7)",
                        " ",
                        "§7Commandes :",
                        "§7/" + cmd1,
                        "§7/" + cmd2
                )
                .setCantClickOn(true)
                .setShiftCommandOnClick(cmd1, cmd2)
                .build();
    }

    public static Inventory getSelectorInventory(String name, boolean singleOr6Lines, String back,
                                                 @Nullable ItemStack main, @Nullable ItemStack second,
                                                 String navigationBaseCommand, int page,
                                                 LinkedHashMap<String, Boolean> options,
                                                 String commandToExecuteWithOUTVar, boolean noSpace) {
        Inventory inv = Bukkit.createInventory(null, singleOr6Lines ? 9 : 54, name);

        ArrayList<Utils.Pair<String, Boolean>> content = options.entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Utils.Pair<ItemStack, ItemStack>> items = content.stream()
                .map(entry -> {
                    ArrayList<Pair<String, Boolean>> opt1 = content.stream().map(entry1 ->
                                    new Pair<>(entry1.getKey(), entry1.getValue()))
                            .collect(Collectors.toCollection(ArrayList::new));
                    opt1.get(content.indexOf(entry)).setValue(!entry.getValue());
                    String cmd = navigationBaseCommand + " " + generateSelectorOptions(page, opt1);
                    return new Pair<>(
                            Items.builder(Heads.getSkullOf(entry.getKey()))
                                    .setName("§e" + entry.getKey() + "§7 : " + (entry.getValue() ? "§aEnabled" : "§cDisabled"))
                                    .setLore("§8" + Utils.loreSeparator, "§7Click to toggle")
                                    .setCantClickOn(true)
                                    .setGlobalCommandOnClick(cmd)
                                    .build(),
                            Items.builder(Material.INK_SACK)
                                    .setDurability((short) (entry.getValue() ? 10 : 8))
                                    .setName("§e" + entry.getKey() + "§7 : " + (entry.getValue() ? "§aEnabled" : "§cDisabled"))
                                    .setLore("§8" + Utils.loreSeparator, "§7Click to toggle")
                                    .setCantClickOn(true)
                                    .setGlobalCommandOnClick(cmd)
                                    .build());
                })
                .collect(Collectors.toCollection(ArrayList::new));

        StringBuilder selection = new StringBuilder();
        for (Pair<String, Boolean> pair : content) {
            if (pair.getValue())
                selection.append(pair.getKey()).append(",").append(noSpace ? "" : " ");
        }
        if (selection.length() > 0)
            selection.deleteCharAt(selection.length() - 2);
        String cmd = commandToExecuteWithOUTVar.replace("%OUT%", selection.toString());
        ItemStack exe = Items.builder(Material.EMERALD)
                .setName("§4Execute")
                .setLore("§8" + Utils.loreSeparator,
                        "§aClick to execute",
                        " ",
                        "§7Commandes :"
                                + (cmd.length() == 0 ? "" : "\n§7/" + cmd.replace("\n", "\n§7/")))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(cmd)
                .build();

        if (singleOr6Lines) {
            inv.setItem(0, back == null ? Items.orange() : back(back));
            inv.setItem(1, main == null ? Items.l_gray() : main);
            inv.setItem(2, second == null ? Items.orange() : second);
            inv.setItem(8, exe);

            inv.setItem(3, navigationFeather(page, content.size(), 1));

            inv.setItem(4, page == 0 ? Items.blue()
                    : selectorNavigationArrow(page, content.size(), true, content, navigationBaseCommand));
            inv.setItem(7, page + 1 == content.size() || content.size() == 0 ? Items.blue()
                    : selectorNavigationArrow(page, content.size(), false, content, navigationBaseCommand));

            try {
                inv.setItem(5, items.get(page).getKey());
                inv.setItem(6, items.get(page).getValue());
            } catch (IndexOutOfBoundsException e) {
                inv.setItem(4, Items.gray());
            }

        } else {
            Utils.fill(inv, 0, 54 - 1, true, Items.blue());
            for (int i : Arrays.asList(3, 5))
                inv.setItem(i, Items.orange());
            inv.setItem(4, main == null ? Items.l_gray() : main);
            inv.setItem(54 - 4, second == null ? Items.orange() : second);
            inv.setItem(0, back == null ? Items.blue() : back(back));
            inv.setItem(8, close());
            inv.setItem(54 - 6, exe);

            int isPerPage = 14;
            int max = ((int) (content.size() / (isPerPage + 0.0))) + (content.size() % isPerPage == 0 ? 0 : 1);

            inv.setItem(54 - 9, page == 0 ? Items.blue()
                    : selectorNavigationArrow(page, max, true, content, navigationBaseCommand));
            inv.setItem(54 - 5, navigationFeather(page, max, isPerPage));
            inv.setItem(54 - 1, page + 1 == max || max == 0 ? Items.blue()
                    : selectorNavigationArrow(page, max, false, content, navigationBaseCommand));

            int i = 0;
            while (i < isPerPage) {
                try {
                    inv.setItem(Utils.posOf((i % 7) + 1, (i / 7) + 1),
                            items.get(page * isPerPage + i).getKey());
                    inv.setItem(Utils.posOf((i % 7) + 1, (i / 7) + 2),
                            items.get(page * isPerPage + i).getValue());
                } catch (IndexOutOfBoundsException ignore) {
                }
                i++;
            }
            for (int ii = 0; ii < 54; ii++)
                if (inv.getItem(ii) == null || inv.getItem(ii).getType() == Material.AIR)
                    inv.setItem(ii, Items.gray());
        }

        return inv;
    }

}
