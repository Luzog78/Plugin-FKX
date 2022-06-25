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
                FKManager.getCurrentGame().getGlobal().setPermission(FKPermissions.Type.PVP, FKPermissions.Definition.ON);
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Le !PVP est désactivé.");
                FKManager.getCurrentGame().getGlobal().setPermission(FKPermissions.Type.PVP, FKPermissions.Definition.OFF);
            }
        });
        this.nether = nether.getOptionListener() != null ? nether : nether.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Le !" + FKManager.getCurrentGame().getNether().getName() + " est activé.");
                FKManager.getCurrentGame().getNether().open();
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Le !" + FKManager.getCurrentGame().getNether().getName() + " est désactivé.");
                FKManager.getCurrentGame().getNether().close();
            }
        });
        this.assaults = assaults.getOptionListener() != null ? assaults : assaults.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Les !Assauts sont activés.");
                FKManager.getCurrentGame().getGlobal().setPermission(FKPermissions.Type.PLACESPE, FKPermissions.Definition.ON);
                FKManager.getCurrentGame().getGlobal().setPermission(FKPermissions.Type.BREAKSPE, FKPermissions.Definition.ON);
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Les !Assauts sont désactivés.");
                FKManager.getCurrentGame().getGlobal().setPermission(FKPermissions.Type.PLACESPE, FKPermissions.Definition.OFF);
                FKManager.getCurrentGame().getGlobal().setPermission(FKPermissions.Type.BREAKSPE, FKPermissions.Definition.OFF);
            }
        });
        this.end = end.getOptionListener() != null ? end : end.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.announcement("Le !" + FKManager.getCurrentGame().getEnd().getName() + " est activé.");
                FKManager.getCurrentGame().getEnd().open();
            }

            @Override
            public void onDeactivate(boolean broadcast) {
                if (broadcast)
                    Broadcast.warn("Le !" + FKManager.getCurrentGame().getEnd().getName() + " est désactivé.");
                FKManager.getCurrentGame().getEnd().close();
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
