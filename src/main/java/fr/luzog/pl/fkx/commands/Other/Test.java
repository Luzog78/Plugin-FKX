package fr.luzog.pl.fkx.commands.Other;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKDimension;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Test implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, "/test");

        FKDimension n = FKManager.getCurrentGame().getNether();

        if (args.length > 0)
            if (args[0].equals("open"))
                n.open();
            else if (args[0].equals("close"))
                n.close();
            else if (args[0].equals("tp")) {
                System.out.println(n.isOpened());
                System.out.println(n.nextSpawn(u.getPlayer().getLocation()));

                System.out.println(u.getPlayer().getLocation());
                for(Block b : Utils.getBlocksIn(n.getOverPortal1(), n.getOverPortal2()))
                    System.out.println("  -> " + Utils.normalize(b.getLocation(), false) + " : " + u.getPlayer().getLocation().distance(Utils.normalize(b.getLocation(), false)));

                System.out.println("---------------------------------");
                System.out.println(n.tryToTeleport(u.getPlayer()));
            } else if(args[0].equals("test")) {
                new BukkitRunnable() {
                    int i = 0;
                    @Override
                    public void run() {
                        if(i++ > 28)
                            cancel();

                        System.out.println("---------------[ " + i + " ]---------------");
                        System.out.println(n.isOpened());
                        System.out.println(n.nextSpawn(u.getPlayer().getLocation()));

                        System.out.println(u.getPlayer().getLocation());
                        for(Block b : Utils.getBlocksIn(n.getOverPortal1(), n.getOverPortal2()))
                            System.out.println("  -> " + Utils.normalize(b.getLocation(), false) + " : " + u.getPlayer().getLocation().distance(Utils.normalize(b.getLocation(), false)));

                        System.out.println("---------------------------------");
                        System.out.println(n.tryToTeleport(u.getPlayer()));
                    }
                }.runTaskTimer(Main.instance, 0, 2);
            }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        return new ArrayList<>();
    }

}
