package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.utils.Broadcast;

import java.util.Arrays;
import java.util.List;

public class FKOptions {

    private FKManager manager;
    private FKOption pvp, nether, assaults, end;

    public FKOptions(FKOption pvp, FKOption nether, FKOption assaults, FKOption end) {
        this.pvp = pvp.getOptionListener() != null ? pvp : pvp.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Le !PVP est activé.");
                manager.getGlobal().setPermission(FKPermissions.Type.PVP, FKPermissions.Definition.ON);
                manager.getConfig().load().setGlobalPermissions(manager.getGlobal(), true).save();
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Le !PVP est désactivé.");
                manager.getGlobal().setPermission(FKPermissions.Type.PVP, FKPermissions.Definition.OFF);
                manager.getConfig().load().setGlobalPermissions(manager.getGlobal(), true).save();
            }
        });
        this.nether = nether.getOptionListener() != null ? nether : nether.setOptionListener(new FKOptionListener() {
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
        this.assaults = assaults.getOptionListener() != null ? assaults : assaults.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Les !Assauts sont activés.");
                manager.getGlobal().setPermission(FKPermissions.Type.PLACESPE, FKPermissions.Definition.ON);
                manager.getGlobal().setPermission(FKPermissions.Type.BREAKSPE, FKPermissions.Definition.ON);
                manager.getConfig().load().setGlobalPermissions(manager.getGlobal(), true).save();
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Les !Assauts sont désactivés.");
                manager.getGlobal().setPermission(FKPermissions.Type.PLACESPE, FKPermissions.Definition.OFF);
                manager.getGlobal().setPermission(FKPermissions.Type.BREAKSPE, FKPermissions.Definition.OFF);
                manager.getConfig().load().setGlobalPermissions(manager.getGlobal(), true).save();
            }
        });
        this.end = end.getOptionListener() != null ? end : end.setOptionListener(new FKOptionListener() {
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

    public static FKOptions getDefaultOptions() {
        return new FKOptions(FKOption.getDefaultOptionPvP(), FKOption.getDefaultOptionNether(), FKOption.getDefaultOptionAssaults(), FKOption.getDefaultOptionEnd());
    }

    public static interface FKOptionListener {
        void onActivate(boolean broadcast);

        void onDeactivate(boolean broadcast);
    }

    public static class FKOption {
        private String name;
        private int activationDay;
        private boolean activated;
        private FKOptionListener optionListener = null;

        public FKOption(String name, int activationDay, boolean activated) {
            this.name = name;
            this.activationDay = activationDay;
            this.activated = activated;
        }

        public FKOption(String name, int activationDay, boolean activated, FKOptionListener optionListener) {
            this.name = name;
            this.activationDay = activationDay;
            this.activated = activated;
            this.optionListener = optionListener;
        }

        public static FKOption getDefaultOptionPvP() {
            return new FKOptions.FKOption("PvP", 2, false);
        }

        public static FKOption getDefaultOptionNether() {
            return new FKOptions.FKOption("Nether", 4, false);
        }

        public static FKOption getDefaultOptionAssaults() {
            return new FKOptions.FKOption("Assauts", 6, false);
        }

        public static FKOption getDefaultOptionEnd() {
            return new FKOptions.FKOption("End", 6, false);
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

        public void activate() {
            activate(true);
        }

        public void activate(boolean broadcast) {
            this.activated = true;
            if (optionListener != null)
                optionListener.onActivate(broadcast);
        }

        public void deactivate() {
            deactivate(true);
        }

        public void deactivate(boolean broadcast) {
            this.activated = false;
            if (optionListener != null)
                optionListener.onDeactivate(broadcast);
        }

        public FKOptionListener getOptionListener() {
            return optionListener;
        }

        public FKOption setOptionListener(FKOptionListener optionListener) {
            this.optionListener = optionListener;
            return this;
        }
    }

    public List<FKOption> getOptions() {
        return Arrays.asList(pvp, nether, assaults, end);
    }

    public FKManager getManager() {
        return manager;
    }

    public void setManager(FKManager manager) {
        this.manager = manager;
    }

    public FKOption getPvp() {
        return pvp;
    }

    public void setPvp(FKOption pvp) {
        this.pvp = pvp;
    }

    public FKOption getNether() {
        return nether;
    }

    public void setNether(FKOption nether) {
        this.nether = nether;
    }

    public FKOption getAssaults() {
        return assaults;
    }

    public void setAssaults(FKOption assaults) {
        this.assaults = assaults;
    }

    public FKOption getEnd() {
        return end;
    }

    public void setEnd(FKOption end) {
        this.end = end;
    }
}
