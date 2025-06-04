package net.sincere.jutsucraft.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.List;

public class KatonItem extends JutsuItem {
    public static final Jutsu GREAT_FIREBALL = new Jutsu("jutsu.katon.great_fireball", 50, 40,
            (player, level) -> player.displayClientMessage(Component.literal("Katon: Great Fireball"), true));

    public KatonItem() {
        super(new Item.Properties(), List.of(GREAT_FIREBALL));
    }
}