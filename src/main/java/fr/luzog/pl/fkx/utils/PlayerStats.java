package fr.luzog.pl.fkx.utils;

public class PlayerStats {

    private int kills, deaths, blocksBroken, blocksPlaced, oresBroken, arrowsShot, arrowsHit, pickedItems, droppedItems,
            enchantedItems, jumps, chats, inventoriesOpened, clickOnInventory;
    private double damageDealt, damageTaken, regainedHealth, regainedFood;

    public PlayerStats() {
        this.kills = 0;
        this.deaths = 0;
        this.blocksBroken = 0;
        this.blocksPlaced = 0;
        this.oresBroken = 0;
        this.arrowsShot = 0;
        this.arrowsHit = 0;
        this.pickedItems = 0;
        this.droppedItems = 0;
        this.enchantedItems = 0;
        this.jumps = 0;
        this.chats = 0;
        this.inventoriesOpened = 0;
        this.clickOnInventory = 0;
        this.damageDealt = 0;
        this.damageTaken = 0;
        this.regainedHealth = 0;
        this.regainedFood = 0;
    }

    public PlayerStats(int kills, int deaths, int blocksBroken, int blocksPlaced, int oresBroken,
                       int arrowsShot, int arrowsHit, int pickedItems, int droppedItems, int enchantedItems, int jumps,
                       int chats, int inventoriesOpened, int clickOnInventory, double damageDealt, double damageTaken,
                       double regainedHealth, double regainedFood) {
        this.kills = kills;
        this.deaths = deaths;
        this.blocksBroken = blocksBroken;
        this.blocksPlaced = blocksPlaced;
        this.oresBroken = oresBroken;
        this.arrowsShot = arrowsShot;
        this.arrowsHit = arrowsHit;
        this.pickedItems = pickedItems;
        this.droppedItems = droppedItems;
        this.enchantedItems = enchantedItems;
        this.jumps = jumps;
        this.chats = chats;
        this.inventoriesOpened = inventoriesOpened;
        this.clickOnInventory = clickOnInventory;
        this.damageDealt = damageDealt;
        this.damageTaken = damageTaken;
        this.regainedHealth = regainedHealth;
        this.regainedFood = regainedFood;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void increaseKills() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void increaseDeaths() {
        this.deaths++;
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public void increaseBlocksBroken() {
        this.blocksBroken++;
    }

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    public void setBlocksPlaced(int blocksPlaced) {
        this.blocksPlaced = blocksPlaced;
    }

    public void increaseBlocksPlaced() {
        this.blocksPlaced++;
    }

    public int getOresBroken() {
        return oresBroken;
    }

    public void setOresBroken(int oresBroken) {
        this.oresBroken = oresBroken;
    }

    public void increaseOresBroken() {
        this.oresBroken++;
    }

    public int getArrowsShot() {
        return arrowsShot;
    }

    public void setArrowsShot(int arrowsShot) {
        this.arrowsShot = arrowsShot;
    }

    public void increaseArrowsShot() {
        this.arrowsShot++;
    }

    public int getArrowsHit() {
        return arrowsHit;
    }

    public void setArrowsHit(int arrowsHit) {
        this.arrowsHit = arrowsHit;
    }

    public void increaseArrowsHit() {
        this.arrowsHit++;
    }

    public int getPickedItems() {
        return pickedItems;
    }

    public void setPickedItems(int pickedItems) {
        this.pickedItems = pickedItems;
    }

    public void increasePickedItems() {
        this.pickedItems++;
    }

    public int getDroppedItems() {
        return droppedItems;
    }

    public void setDroppedItems(int droppedItems) {
        this.droppedItems = droppedItems;
    }

    public void increaseDroppedItems() {
        this.droppedItems++;
    }

    public int getEnchantedItems() {
        return enchantedItems;
    }

    public void setEnchantedItems(int enchantedItems) {
        this.enchantedItems = enchantedItems;
    }

    public void increaseEnchantedItems() {
        this.enchantedItems++;
    }

    public int getJumps() {
        return jumps;
    }

    public void setJumps(int jumps) {
        this.jumps = jumps;
    }

    public void increaseJumps() {
        this.jumps++;
    }

    public int getChats() {
        return chats;
    }

    public void setChats(int chats) {
        this.chats = chats;
    }

    public void increaseChats() {
        this.chats++;
    }

    public int getInventoriesOpened() {
        return inventoriesOpened;
    }

    public void setInventoriesOpened(int inventoriesOpened) {
        this.inventoriesOpened = inventoriesOpened;
    }

    public void increaseInventoriesOpened() {
        this.inventoriesOpened++;
    }

    public int getClickOnInventory() {
        return clickOnInventory;
    }

    public void setClickOnInventory(int clickOnInventory) {
        this.clickOnInventory = clickOnInventory;
    }

    public void increaseClickOnInventory() {
        this.clickOnInventory++;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(double damageDealt) {
        this.damageDealt = damageDealt;
    }

    public void increaseDamageDealt(double damageDealt) {
        this.damageDealt += damageDealt;
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    public void setDamageTaken(double damageTaken) {
        this.damageTaken = damageTaken;
    }

    public void increaseDamageTaken(double damageTaken) {
        this.damageTaken += damageTaken;
    }

    public double getRegainedHealth() {
        return regainedHealth;
    }

    public void setRegainedHealth(double regainedHealth) {
        this.regainedHealth = regainedHealth;
    }

    public void increaseRegainedHealth(double regainedHealth) {
        this.regainedHealth += regainedHealth;
    }

    public double getRegainedFood() {
        return regainedFood;
    }

    public void setRegainedFood(double regainedFood) {
        this.regainedFood = regainedFood;
    }

    public void increaseRegainedFood(double regainedFood) {
        this.regainedFood += regainedFood;
    }

}
