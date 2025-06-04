package net.sincere.jutsucraft.chakra;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChakraEvents {
    private static final String CHAKRA_ID = "chakra";

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new net.minecraft.resources.ResourceLocation("jutsucraft", CHAKRA_ID), new ChakraProvider());
        }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(oldStore -> {
            event.getEntity().getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(newStore -> {
                newStore.setMaxChakra(oldStore.getMaxChakra());
                newStore.setChakra(oldStore.getChakra());
            });
        });
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.END) {
            event.player.getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(chakra -> {
                chakra.addChakra(1); // simple regen
            });
        }
    }
}
