package net.sincere.jutsucraft.chakra;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChakraProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<IChakra> CHAKRA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private final Chakra chakra = new Chakra();
    private final LazyOptional<IChakra> optional = LazyOptional.of(() -> chakra);

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IChakra.class);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CHAKRA_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        chakra.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        chakra.loadNBTData(nbt);
    }
}
