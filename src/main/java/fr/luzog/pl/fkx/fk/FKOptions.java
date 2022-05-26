package fr.luzog.pl.fkx.fk;

public class FKOptions {

    private FKManager manager;
    private FKOption pvp, nether, assauts, end;

    public FKOptions(FKOption pvp, FKOption nether, FKOption assauts, FKOption end){
        this.pvp = pvp.getOptionListener() != null ? pvp : pvp.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate() {

            }

            @Override
            public void onInactivate() {

            }
        });
        this.nether = nether.getOptionListener() != null ? nether : nether.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate() {

            }

            @Override
            public void onInactivate() {

            }
        });
        this.assauts = assauts.getOptionListener() != null ? assauts : assauts.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate() {

            }

            @Override
            public void onInactivate() {

            }
        });
        this.end = end.getOptionListener() != null ? end : end.setOptionListener(new FKOptionListener() {
            @Override
            public void onActivate() {

            }

            @Override
            public void onInactivate() {

            }
        });
    }

    public static interface FKOptionListener {
        void onActivate();

        void onInactivate();
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

        public void setActivated(boolean activated) {
            this.activated = activated;
        }

        public FKOptionListener getOptionListener() {
            return optionListener;
        }

        public FKOption setOptionListener(FKOptionListener optionListener) {
            this.optionListener = optionListener;
            return this;
        }
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
