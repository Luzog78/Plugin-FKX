package fr.luzog.pl.fkx.utils;

import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class CustomNBT {
    private ItemStack is;
    private net.minecraft.server.v1_8_R3.ItemStack cis;
    private NBTTagCompound nbt;

    public CustomNBT(ItemStack is) {
        this.is = is;
        this.cis = is == null ? null : CraftItemStack.asNMSCopy(is);
        this.nbt = this.cis == null || !this.cis.hasTag() ? new NBTTagCompound() : this.cis.getTag();
    }

    public NBTBase get(String key) {
        return this.nbt.get(key);
    }

    public boolean hasKey(String key) {
        return this.nbt.hasKey(key);
    }

    public boolean isEmpty() {
        return this.nbt.isEmpty();
    }

    public String getString(String key) {
        return this.nbt.getString(key);
    }

    public long getLong(String key) {
        return this.nbt.getLong(key);
    }

    public int getInt(String key) {
        return this.nbt.getInt(key);
    }

    public int[] getIntArray(String key) {
        return this.nbt.getIntArray(key);
    }

    public double getDouble(String key) {
        return this.nbt.getDouble(key);
    }

    public float getFloat(String key) {
        return this.nbt.getFloat(key);
    }

    public short getShort(String key) {
        return this.nbt.getShort(key);
    }

    public byte getByte(String key) {
        return this.nbt.getByte(key);
    }

    public byte[] getByteArray(String key) {
        return this.nbt.getByteArray(key);
    }

    public boolean getBoolean(String key) {
        return this.nbt.getBoolean(key);
    }

    public CustomNBT set(String key, Object value) {
        Class<?> aClass = value.getClass();
        if (String.class.equals(aClass)) {
            nbt.setString(key, value + "");
        } else if (Long.class.equals(aClass) || long.class.equals(aClass)) {
            nbt.setLong(key, (Long) value);
        } else if (Integer.class.equals(aClass) || int.class.equals(aClass)) {
            nbt.setInt(key, (Integer) value);
        } else if (Integer[].class.equals(aClass) || int[].class.equals(aClass)) {
            assert value instanceof int[];
            nbt.setIntArray(key, (int[]) value);
        } else if (Double.class.equals(aClass) || double.class.equals(aClass)) {
            nbt.setDouble(key, (Double) value);
        } else if (Float.class.equals(aClass) || float.class.equals(aClass)) {
            nbt.setFloat(key, (Float) value);
        } else if (Short.class.equals(aClass) || short.class.equals(aClass)) {
            nbt.setShort(key, (Short) value);
        } else if (Byte.class.equals(aClass) || byte.class.equals(aClass)) {
            nbt.setByte(key, (Byte) value);
        } else if (Byte[].class.equals(aClass) || byte[].class.equals(aClass)) {
            assert value instanceof byte[];
            nbt.setByteArray(key, (byte[]) value);
        } else if (Boolean.class.equals(aClass) || boolean.class.equals(aClass)) {
            nbt.setBoolean(key, (Boolean) value);
        } else {
            nbt.set(key, (NBTBase) value);
        }
        return this;
    }

    public CustomNBT setString(String key, String value) {
        this.nbt.setString(key, value);
        return this;
    }

    public CustomNBT setLong(String key, long value) {
        this.nbt.setLong(key, value);
        return this;
    }

    public CustomNBT setInt(String key, int value) {
        this.nbt.setInt(key, value);
        return this;
    }

    public CustomNBT setIntArray(String key, int[] value) {
        this.nbt.setIntArray(key, value);
        return this;
    }

    public CustomNBT setDouble(String key, double value) {
        this.nbt.setDouble(key, value);
        return this;
    }

    public CustomNBT setFloat(String key, float value) {
        this.nbt.setFloat(key, value);
        return this;
    }

    public CustomNBT setShort(String key, short value) {
        this.nbt.setShort(key, value);
        return this;
    }

    public CustomNBT setByte(String key, byte value) {
        this.nbt.setByte(key, value);
        return this;
    }

    public CustomNBT setByteArray(String key, byte[] value) {
        this.nbt.setByteArray(key, value);
        return this;
    }

    public CustomNBT setBoolean(String key, boolean value) {
        this.nbt.setBoolean(key, value);
        return this;
    }

    public CustomNBT remove(String key) {
        this.nbt.remove(key);
        return this;
    }

    public CustomNBT update() {
        this.cis.setTag(nbt);
        this.is = CraftItemStack.asBukkitCopy(this.cis);
        return this;
    }

    public ItemStack build() {
        update();
        return is;
    }

    public Items.Builder builder() {
        return new Items.Builder(build());
    }

    public ItemStack getNotUpdatedItemStack() {
        return is;
    }

    public net.minecraft.server.v1_8_R3.ItemStack getNotUpdatedCraftItemStack() {
        return cis;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    public void setNbt(NBTTagCompound nbt) {
        this.nbt = nbt;
    }
}
