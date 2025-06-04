package net.sincere.jutsucraft.chakra;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sincere.jutsucraft.Jutsucraft;

/**
 * Simple chakra bar HUD rendered above the hotbar.
 */
@Mod.EventBusSubscriber(modid = Jutsucraft.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChakraHUD {

    @SubscribeEvent
    public static void renderChakraBar(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.player.getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(chakra -> {
            GuiGraphics graphics = event.getGuiGraphics();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            int barWidth = 80;
            int barHeight = 5;
            int x = 10;
            int y = screenHeight - 49; // slightly above the hotbar

            graphics.fill(x, y, x + barWidth, y + barHeight, 0xAA000000);
            int filled = (int) (barWidth * chakra.getChakra() / (float) chakra.getMaxChakra());
            graphics.fill(x + 1, y + 1, x + 1 + filled, y + barHeight - 1, 0xFF0080FF);

            String text = chakra.getChakra() + " / " + chakra.getMaxChakra();
            graphics.drawString(mc.font, text, x, y - 9, 0xFFFFFF, true);
        });
    }
}