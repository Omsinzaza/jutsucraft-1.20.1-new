package net.sincere.jutsucraft.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.sincere.jutsucraft.chakra.ChakraProvider;

public class ChakraSyncS2CPacket {
    private final int chakra;
    private final int maxChakra;

    public ChakraSyncS2CPacket(int chakra, int maxChakra) {
        this.chakra = chakra;
        this.maxChakra = maxChakra;
    }

    public ChakraSyncS2CPacket(FriendlyByteBuf buf) {
        this.chakra = buf.readInt();
        this.maxChakra = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(chakra);
        buf.writeInt(maxChakra);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(chakra -> {
                    chakra.setMaxChakra(maxChakra);
                    chakra.setChakra(this.chakra);
                });
            }
        });
        context.setPacketHandled(true);
    }
}