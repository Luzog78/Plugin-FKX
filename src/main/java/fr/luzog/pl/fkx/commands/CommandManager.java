package fr.luzog.pl.fkx.commands;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.commands.Admin.*;
import fr.luzog.pl.fkx.commands.Chat.Msg;
import fr.luzog.pl.fkx.commands.Chat.Reply;
import fr.luzog.pl.fkx.commands.Cheat.*;
import fr.luzog.pl.fkx.commands.Fun.*;
import fr.luzog.pl.fkx.commands.Game.GCMainCommand;
import fr.luzog.pl.fkx.commands.Location.Tp;
import fr.luzog.pl.fkx.commands.Other.Ad;
import fr.luzog.pl.fkx.commands.Other.Test;
import fr.luzog.pl.fkx.commands.Utils.Craft;
import fr.luzog.pl.fkx.commands.Utils.EnderChest;
import fr.luzog.pl.fkx.commands.Utils.InputGUIAndTools;
import fr.luzog.pl.fkx.commands.Utils.Trash;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private static final ArrayList<CommandItem> commands = new ArrayList<>(Arrays.asList(
            // Admin
            new CommandItem(new Execute(), "execute"),
            new CommandItem(new Gm(), "gm"),
            new CommandItem(new Gm(GameMode.SURVIVAL), "gm0"),
            new CommandItem(new Gm(GameMode.CREATIVE), "gm1"),
            new CommandItem(new Gm(GameMode.ADVENTURE), "gm2"),
            new CommandItem(new Gm(GameMode.SPECTATOR), "gm3"),
            new CommandItem(new InvSee(), "invsee"),
            new CommandItem(new Vanish(), "vanish"),
            new CommandItem(new Weather(Weather.WeatherType.SUN), "sun"),
            new CommandItem(new Weather(Weather.WeatherType.RAIN), "rain"),
            new CommandItem(new Weather(Weather.WeatherType.THUNDER), "thunder"),

            // Chat
            new CommandItem(new Msg(), "msg"),
            new CommandItem(new Reply(), "reply"),

            // Cheat
            new CommandItem(new Enchant(), "ench"),
            new CommandItem(new Feed(), "feed"),
            new CommandItem(new Freeze(), "freeze"),
            new CommandItem(new God(), "god"),
            new CommandItem(new Heal(), "heal"),
            new CommandItem(new SetHealth(), "sethealth"),
            new CommandItem(new Speed(), "speed"),

            // Fun
            new CommandItem(new Bounce(), "bounce"),
            new CommandItem(new Burn(), "burn"),
            new CommandItem(new Damage(), "damage"),
            new CommandItem(new Hat(), "hat"),
            new CommandItem(new Head(), "head"),
            new CommandItem(new Lightning(), "lightning"),
            new CommandItem(new Rename(), "rename"),
            new CommandItem(new Shuffle(), "shuffle"),

            // Game
            new CommandItem(new GCMainCommand(), Main.CMD),

            // Location
            new CommandItem(new Tp(), "tp"),

            // Other
            new CommandItem(new Ad(), "ad"),
            new CommandItem(new Test(), "test"),

            // Server

            // Utils
            new CommandItem(new Craft(), "craft"),
            new CommandItem(new EnderChest(), "ec"),
            new CommandItem(new InputGUIAndTools(), "input"),
            new CommandItem(new Trash(), "trash")
    ));

    public static List<CommandItem> getCommands() {
        return Arrays.asList(commands.toArray(new CommandItem[0]));
    }

    public static void registerCommand(CommandItem command) {
        commands.add(command);
        Arrays.stream(command.getCommands()).forEach(c ->
                Main.instance.getCommand(c).setExecutor(command.getExecutor()));
    }

    public static void init() {
        commands.forEach(command -> Arrays.stream(command.getCommands()).forEach(c ->
                Main.instance.getCommand(c).setExecutor(command.getExecutor())));
    }

    public static class CommandItem {
        private CommandExecutor executor;
        private String[] commands;

        public CommandItem(CommandExecutor executor, String... commands) {
            this.executor = executor;
            this.commands = commands;
        }

        public CommandExecutor getExecutor() {
            return executor;
        }

        public void setExecutor(CommandExecutor executor) {
            this.executor = executor;
        }

        public String[] getCommands() {
            return commands;
        }

        public void setCommands(String[] commands) {
            this.commands = commands;
        }
    }
}
