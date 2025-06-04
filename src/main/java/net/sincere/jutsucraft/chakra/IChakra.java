package net.sincere.jutsucraft.chakra;

public interface IChakra {
    int getChakra();
    int getMaxChakra();

    void setChakra(int value);
    void setMaxChakra(int value);

    default void addChakra(int amount) {
        setChakra(Math.min(getChakra() + amount, getMaxChakra()));
    }

    default boolean consumeChakra(int amount) {
        if (getChakra() >= amount) {
            setChakra(getChakra() - amount);
            return true;
        }
        return false;
    }

    /**
     * Called every tick on the server side to update chakra state.
     */
    default void tick() {
    }
}