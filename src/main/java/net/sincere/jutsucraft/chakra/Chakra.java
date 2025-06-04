package net.sincere.jutsucraft.chakra;

import net.minecraft.nbt.CompoundTag;

public class Chakra implements IChakra {
    private int chakra = 0;
    private int maxChakra = 100;
    private int regenInterval = 20; // ticks between regeneration
    private int regenAmount = 1;
    private int tickCounter = 0;

    @Override
    public int getChakra() {
        return chakra;
    }

    @Override
    public int getMaxChakra() {
        return maxChakra;
    }

    @Override
    public void setChakra(int value) {
        this.chakra = Math.max(0, Math.min(value, maxChakra));
    }

    @Override
    public void setMaxChakra(int value) {
        this.maxChakra = Math.max(0, value);
        if (chakra > maxChakra) {
            chakra = maxChakra;
        }
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("chakra", chakra);
        nbt.putInt("maxChakra", maxChakra);
    }

    public void loadNBTData(CompoundTag nbt) {
        this.chakra = nbt.getInt("chakra");
        this.maxChakra = nbt.getInt("maxChakra");
    }

    @Override
    public void tick() {
        if (++tickCounter >= regenInterval) {
            tickCounter = 0;
            addChakra(regenAmount);
        }
    }
}