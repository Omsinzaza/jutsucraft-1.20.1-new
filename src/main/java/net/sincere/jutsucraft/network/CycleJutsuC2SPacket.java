package net.sincere.jutsucraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.sincere.jutsucraft.item.JutsuItem;

import java.util.function.Supplier;

public class CycleJutsuC2SPacket {
    public CycleJutsuC2SPacket() {}

    public CycleJutsuC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            context.enqueueWork(() -> {
                ItemStack stack = player.getMainHandItem();
                if (!(stack.getItem() instanceof JutsuItem)) {
                    stack = player.getOffhandItem();
                }
                if (stack.getItem() instanceof JutsuItem item) {
                    item.nextJutsu(stack);
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal(item.getCurrentJutsu(stack).getTranslationKey()), true);
                }
            });
        }
        context.setPacketHandled(true);
    }
}