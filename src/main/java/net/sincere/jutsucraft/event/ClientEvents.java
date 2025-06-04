package net.sincere.jutsucraft.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sincere.jutsucraft.Jutsucraft;
import net.sincere.jutsucraft.client.ModKeyBindings;
import net.sincere.jutsucraft.chakra.ChakraHUD;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Jutsucraft.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void renderChakra(RenderGuiOverlayEvent.Post event) {
            if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
                return;
            }
            ChakraHUD.renderChakraBar(event);
        }
    }
    @Mod.EventBusSubscriber(modid = Jutsucraft.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents{
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event){
            event.register(ModKeyBindings.CHANGE_JUTSU);
        }
    }
}