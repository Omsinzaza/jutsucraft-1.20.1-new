package net.sincere.jutsucraft.chakra;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

public class ChakraHUD {

    public static void renderChakraBar(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.player.getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(chakra -> {
            GuiGraphics graphics = event.getGuiGraphics();
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            int barWidth = 80;
            int barHeight = 5;

            int hotbarLeft = (screenWidth - 182) / 2;
            int x = hotbarLeft / 2 - barWidth / 2; // center between left border and hotbar

            int hotbarTop = screenHeight - 23;
            int previousY = hotbarTop - 15;
            int y = previousY + (screenHeight - previousY - barHeight) / 2; // halfway to bottom

            graphics.fill(x, y, x + barWidth, y + barHeight, 0xAA000000);
            int innerWidth = barWidth - 2; // leave a 1px border on each side
            int filled = (int) (innerWidth * chakra.getChakra() / (float) chakra.getMaxChakra());
            graphics.fill(x + 1, y + 1, x + 1 + filled, y + barHeight - 1, 0xFF0080FF);


            String text = chakra.getChakra() + " / " + chakra.getMaxChakra();
            int textX = x + (barWidth - mc.font.width(text)) / 2;
            graphics.drawString(mc.font, text, textX, y - 9, 0xFFFFFF, true);
        });
    }
}