package net.sincere.jutsucraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public class ModKeyBindings {
    public static final KeyMapping CHANGE_JUTSU = new KeyMapping("key.jutsucraft.change_jutsu",
            InputConstants.KEY_UP, "key.categories.jutsucraft");

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(CHANGE_JUTSU);
    }
}