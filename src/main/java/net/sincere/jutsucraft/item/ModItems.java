package net.sincere.jutsucraft.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sincere.jutsucraft.Jutsucraft;
import net.sincere.jutsucraft.item.KatonItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Jutsucraft.MODID);

    public static final RegistryObject<Item> KATON = ITEMS.register("katon", KatonItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}