package fr.luzog.pl.fkx.commands;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.commands.Admin.*;
import fr.luzog.pl.fkx.commands.Cheat.*;
import fr.luzog.pl.fkx.commands.Fk.FKCommand;
import fr.luzog.pl.fkx.commands.Fun.*;
import fr.luzog.pl.fkx.commands.Other.Ad;
import fr.luzog.pl.fkx.commands.Other.Test;
import fr.luzog.pl.fkx.commands.Server.R;
import fr.luzog.pl.fkx.commands.Utils.Craft;
import fr.luzog.pl.fkx.commands.Utils.EnderChest;
import fr.luzog.pl.fkx.commands.Utils.Trash;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CommandManager {

    ADMIN(
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
            new CommandItem(new Weather(Weather.WeatherType.THUNDER), "thunder")
    ),

    CHAT(

    ),

    CHEAT(
            new CommandItem(new Enchant(), "ench"),
            new CommandItem(new Feed(), "feed"),
            new CommandItem(new Freeze(), "freeze"),
            new CommandItem(new God(), "god"),
            new CommandItem(new Heal(), "heal"),
            new CommandItem(new SetHealth(), "sethealth"),
            new CommandItem(new Speed(), "speed")
    ),

    FUN(
            new CommandItem(new Bounce(), "bounce"),
            new CommandItem(new Burn(), "burn"),
            new CommandItem(new Damage(), "damage"),
            new CommandItem(new Hat(), "hat"),
            new CommandItem(new Head(), "head"),
            new CommandItem(new Lightning(), "lightning"),
            new CommandItem(new Rename(), "rename"),
            new CommandItem(new Shuffle(), "shuffle")
    ),

    LOCATION(

    ),

    OTHER(
            new CommandItem(new Ad(), "ad"),
            new CommandItem(new Test(), "test"),
            new CommandItem(new FKCommand(), "fk")
    ),

    SERVER(
            new CommandItem(new R(), "r")
    ),

    UTILS(
            new CommandItem(new Craft(), "craft"),
            new CommandItem(new EnderChest(), "ec"),
            new CommandItem(new Trash(), "trash")
    );


    private List<CommandItem> commands;

    CommandManager(CommandItem... commands) {
        this.commands = Arrays.stream(commands).collect(Collectors.toList());
    }

    public static void setupCommands() {

//        cmd(new Broadcast(), "broadcast");
//        cmd(new ClearChat(), "clearchat");

//        cmd(new Home(), "home");
//        cmd(new SetHome(), "sethome");
//        cmd(new Spawn(), "spawn");

//        cmd(new Save(), "save");

        /*
         *
         * ///// Other //////
         * getCommand("basickitcmd").setExecutor(new BasicKitCmd());
         * getCommand("randomchest").setExecutor(new RandomChest(main));
         *
         * ///// Server /////
         * getCommand("ping").setExecutor(new Tps(main));
         * getCommand("version").setExecutor(new Version(main));
         *
         * ///// Utils /////
         * getCommand("clearlag").setExecutor(new ClearLag(main));
         * getCommand("reset").setExecutor(new Reset(main)); //ordre
         */

    }

    public static void init() {
        for (CommandManager manager : CommandManager.values())
            manager.getCommands().forEach(cmd ->
                    Arrays.stream(cmd.getCommands()).collect(Collectors.toList()).forEach(c ->
                            Main.instance.getCommand(c).setExecutor(cmd.getExecutor())));
    }

    public List<CommandItem> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandItem> commands) {
        this.commands = commands;
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
