package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.utils.Broadcast;

import java.util.Arrays;
import java.util.List;

public class FKOptions {

    private FKManager manager;
    private FKOption pvp, nether, assauts, end;

    public FKOptions(FKOption pvp, FKOption nether, FKOption assauts, FKOption end) {
        this.pvp = pvp.getOptionListener() != null ? pvp : pvp.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate() {
                Broadcast.announcement("Le !PVP est activé.");
                FKManager.getCurrentGame().getGlobals().setAuthorization(FKAuth.Type.PVP, FKAuth.Definition.ON);
            }

            @Override
            public void onDeactivate() {
                Broadcast.warn("Le !PVP est désactivé.");
                FKManager.getCurrentGame().getGlobals().setAuthorization(FKAuth.Type.PVP, FKAuth.Definition.OFF);
            }
        });
        this.nether = nether.getOptionListener() != null ? nether : nether.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate() {

            }

            @Override
            public void onDeactivate() {

            }
        });
        this.assauts = assauts.getOptionListener() != null ? assauts : assauts.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate() {
                Broadcast.announcement("Les !Assauts sont activés.");
                FKManager.getCurrentGame().getGlobals().setAuthorization(FKAuth.Type.PLACESPE, FKAuth.Definition.ON);
                FKManager.getCurrentGame().getGlobals().setAuthorization(FKAuth.Type.BREAKSPE, FKAuth.Definition.ON);
            }

            @Override
            public void onDeactivate() {
                Broadcast.warn("Les !Assauts sont désactivés.");
                FKManager.getCurrentGame().getGlobals().setAuthorization(FKAuth.Type.PLACESPE, FKAuth.Definition.OFF);
                FKManager.getCurrentGame().getGlobals().setAuthorization(FKAuth.Type.BREAKSPE, FKAuth.Definition.OFF);
            }
        });
        this.end = end.getOptionListener() != null ? end : end.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate() {

            }

            @Override
            public void onDeactivate() {

            }
        });
    }

    public static FKOptions getDefaultOptions() {
        return new FKOptions(FKOption.getDefaultOptionPvP(), FKOption.getDefaultOptionNether(), FKOption.getDefaultOptionAssaults(), FKOption.getDefaultOptionEnd());
    }

    public static interface FKOptionListener {
        void onActivate();

        void onDeactivate();
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
            this.activated = true;
            if (optionListener != null)
                optionListener.onActivate();
        }

        public void deactivate() {
            this.activated = false;
            if (optionListener != null)
                optionListener.onDeactivate();
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
        return Arrays.asList(pvp, nether, assauts, end);
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

    public FKOption getAssauts() {
        return assauts;
    }

    public void setAssauts(FKOption assauts) {
        this.assauts = assauts;
    }

    public FKOption getEnd() {
        return end;
    }

    public void setEnd(FKOption end) {
        this.end = end;
    }
}
