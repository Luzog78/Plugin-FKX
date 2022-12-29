package fr.luzog.pl.fkx.game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Broadcast;
import fr.luzog.pl.fkx.utils.SpecialChars;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class GOptions {

    private GManager manager;
    private GOption pvp, nether, assaults, end;

    public GOptions(GOption pvp, GOption nether, GOption assaults, GOption end) {
        this.pvp = pvp.getOptionListener() != null ? pvp : pvp.setOptionListener(new GOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Le !PVP est activé.");
                manager.getGlobal().setPermission(GPermissions.Type.PVP, GPermissions.Definition.ON);
                manager.getConfig().load().setGlobalPermissions(manager.getGlobal(), true).save();
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Le !PVP est désactivé.");
                manager.getGlobal().setPermission(GPermissions.Type.PVP, GPermissions.Definition.OFF);
                manager.getConfig().load().setGlobalPermissions(manager.getGlobal(), true).save();
            }
        });
        this.nether = nether.getOptionListener() != null ? nether : nether.setOptionListener(new GOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Le !" + manager.getNether().getName() + " est activé.");
                manager.getNether().open();
                manager.saveNether();
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Le !" + manager.getNether().getName() + " est désactivé.");
                manager.getNether().close();
                manager.saveNether();
            }
        });
        this.assaults = assaults.getOptionListener() != null ? assaults : assaults.setOptionListener(new GOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Les !Assauts sont activés.");
                manager.getConfig().load().setGlobalPermissions(manager.getGlobal(), true).save();
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Les !Assauts sont désactivés.");
                manager.getConfig().load().setGlobalPermissions(manager.getGlobal(), true).save();
            }
        });
        this.end = end.getOptionListener() != null ? end : end.setOptionListener(new GOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Le !" + manager.getEnd().getName() + " est activé.");
                manager.getEnd().open();
                manager.saveEnd();
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Le !" + manager.getEnd().getName() + " est désactivé.");
                manager.getEnd().close();
                manager.saveEnd();
            }
        });

    }

    public static GOptions getDefaultOptions() {
        return new GOptions(GOption.getDefaultOptionPvP(), GOption.getDefaultOptionNether(), GOption.getDefaultOptionAssaults(), GOption.getDefaultOptionEnd());
    }

    public static interface GOptionListener {
        void onActivate(boolean broadcast);

        void onDeactivate(boolean broadcast);
    }

    public static class GOption {
        private String id, name;
        private int activationDay;
        private boolean activated;
        private GOptionListener optionListener;

        public GOption(String id, String name, int activationDay, boolean activated) {
            this.id = id;
            this.name = name;
            this.activationDay = activationDay;
            this.activated = activated;
            optionListener = null;
        }

        public GOption(String id, String name, int activationDay, boolean activated, GOptionListener optionListener) {
            this.id = id;
            this.name = name;
            this.activationDay = activationDay;
            this.activated = activated;
            this.optionListener = optionListener;
        }

        public static GOption getDefaultOptionPvP() {
            return new GOption("pvp", "PvP", 5, false);
        }

        public static GOption getDefaultOptionNether() {
            return new GOption("nether", "Nether", 5, false);
        }

        public static GOption getDefaultOptionAssaults() {
            return new GOption("assaults", "Assauts", 8, false);
        }

        public static GOption getDefaultOptionEnd() {
            return new GOption("end", "End", 8, false);
        }

        public String getFormattedActivation() {
            return activated ? "§2§l" + SpecialChars.YES : "§4§l" + SpecialChars.NO
                    + (activationDay == -1 ? "" : "§7§o (J " + activationDay + ")");
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getActivationDay() {
            return activationDay;
        }

        public void setActivationDay(int activationDay) {
            this.activationDay = activationDay;
        }

        public boolean isActivated() {
            return activated;
        }

        public void activate(boolean broadcast) {
            this.activated = true;
            if (optionListener != null)
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        optionListener.onActivate(broadcast);
                    }
                }.runTask(Main.instance);
        }

        public void deactivate(boolean broadcast) {
            this.activated = false;
            if (optionListener != null)
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        optionListener.onDeactivate(broadcast);
                    }
                }.runTask(Main.instance);
        }

        public GOptionListener getOptionListener() {
            return optionListener;
        }

        public GOption setOptionListener(GOptionListener optionListener) {
            this.optionListener = optionListener;
            return this;
        }
    }

    public List<GOption> getOptions() {
        return Arrays.asList(pvp, nether, assaults, end);
    }

    public GManager getManager() {
        return manager;
    }

    public void setManager(GManager manager) {
        this.manager = manager;
    }

    public GOption getPvp() {
        return pvp;
    }

    public void setPvp(GOption pvp) {
        this.pvp = pvp;
    }

    public GOption getNether() {
        return nether;
    }

    public void setNether(GOption nether) {
        this.nether = nether;
    }

    public GOption getAssaults() {
        return assaults;
    }

    public void setAssaults(GOption assaults) {
        this.assaults = assaults;
    }

    public GOption getEnd() {
        return end;
    }

    public void setEnd(GOption end) {
        this.end = end;
    }
}
