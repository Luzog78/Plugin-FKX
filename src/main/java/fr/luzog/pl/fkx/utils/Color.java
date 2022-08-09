package fr.luzog.pl.fkx.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Color {

    /*
     * §f WHITE
     * §0 BLACK
     *
     * --------------
     * §4 DARK RED
     * §c RED
     * --------------
     * §6 GOLD
     * §e YELLOW
     * --------------
     * §2 DARK GREEN
     * §a GREEN
     * --------------
     * §3 DARK AQUA
     * §b AQUA
     * --------------
     * §1 DARK BLUE
     * §9 BLUE
     * --------------
     * §5 DARK PURPLE
     * §d PURPLE
     * --------------
     * §8 DARK GRAY
     * §7 GRAY
     * --------------
     *
     *
     * §l BOLD
     * §o ITALIC
     * §n UNDERLINE
     * §m BARRE
     *
     * §k MAGIC
     *
     * §r RESET
     *
     *
     * --------------
     * https://www.planetminecraft.com/blog/bukkit-color-codes/
     * --------------
     */

    //Double letter = same

    public static final String SANE = "\u001B[0m"; //a
    public static final String RESET = "\u001B[0m"; //a

    public static final String HIGH_INTENSITY = "\u001B[1m"; //b
    public static final String BOLD = "\u001B[1m"; //b
    public static final String LOW_INTENSITY = "\u001B[2m"; //c
    public static final String NO_BOLD = "\u001B[2m"; //c

    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String CROSSED_OUT = "\u001B[9m";

    public static final String BLINK = "\u001B[5m";
    public static final String RAPID_BLINK = "\u001B[6m";

    public static final String REVERSE_VIDEO = "\u001B[7m"; //Inverse colors
    public static final String INVISIBLE_TEXT = "\u001B[8m";

    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BACKGROUND_BLACK = "\u001B[40m";
    public static final String BACKGROUND_RED = "\u001B[41m";
    public static final String BACKGROUND_GREEN = "\u001B[42m";
    public static final String BACKGROUND_YELLOW = "\u001B[43m";
    public static final String BACKGROUND_BLUE = "\u001B[44m";
    public static final String BACKGROUND_MAGENTA = "\u001B[45m";
    public static final String BACKGROUND_CYAN = "\u001B[46m";
    public static final String BACKGROUND_WHITE = "\u001B[47m";

    public static final Map<String, String> CONVERTER = new HashMap<String, String>() {{
        put("§0", RESET + BLACK);
        put("§f", RESET + BOLD + WHITE);
        put("§4", RESET + RED);
        put("§c", RESET + BOLD + RED);
        put("§6", RESET + YELLOW);
        put("§e", RESET + BOLD + YELLOW);
        put("§2", RESET + GREEN);
        put("§a", RESET + BOLD + GREEN);
        put("§3", RESET + CYAN);
        put("§b", RESET + BOLD + CYAN);
        put("§1", RESET + BLUE);
        put("§9", RESET + BOLD + BLUE);
        put("§5", RESET + MAGENTA);
        put("§d", RESET + BOLD + MAGENTA);
        put("§8", RESET + BOLD + BLACK);
        put("§7", RESET + WHITE);
        put("§l", BOLD);
        put("§o", ITALIC);
        put("§n", UNDERLINE);
        put("§m", CROSSED_OUT);
        put("§k", BLINK);
        put("§r", RESET);
    }};

    /**
     * Convert a string with Minecraft color codes to a string with ANSI color codes.
     *
     * @param str The string to convert
     *
     * @return A string with the color codes replaced with the actual console color.
     */
    public static String convert(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '§') {
                String code = str.substring(i, i + 2);
                if (CONVERTER.containsKey(code)) {
                    sb.append(CONVERTER.get(code));
                    i++;
                } else {
                    sb.append(str.charAt(i));
                }
            } else {
                sb.append(str.charAt(i));
            }
        }
        return sb.append(RESET).toString();
    }

    public static void sout(Object... o) {
        System.out.print(convert(String.join(" ", Arrays.stream(o).map(Object::toString).toArray(String[]::new))));
    }

}
