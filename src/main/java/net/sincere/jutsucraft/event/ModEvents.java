package net.sincere.jutsucraft.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.sincere.jutsucraft.Jutsucraft;
import net.sincere.jutsucraft.chakra.ChakraProvider;
import net.sincere.jutsucraft.network.ChakraSyncS2CPacket;
import net.sincere.jutsucraft.network.ModMessages;
import net.sincere.jutsucraft.ability.WaterWalkingHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Jutsucraft.MODID)
public class ModEvents {

    private static final String CHAKRA_ID = "chakra";
    private static final Map<UUID, Vec3> LAST_POS = new HashMap<>();
    private static final Map<UUID, Integer> STILL_TICKS = new HashMap<>();
    private static final Map<UUID, Integer> REGEN_TICKS = new HashMap<>();
    private static final int STAND_STILL_THRESHOLD = 40;

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        WaterWalkingHandler.handlePlayerTick(event);
        if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.END) {
            UUID id = player.getUUID();
            Vec3 current = player.position();
            Vec3 last = LAST_POS.get(id);

            if (last == null || current.distanceToSqr(last) > 0.0001D) {
                STILL_TICKS.put(id, 0);
                REGEN_TICKS.put(id, 0);
            } else {
                STILL_TICKS.put(id, STILL_TICKS.getOrDefault(id, 0) + 1);
            }
            LAST_POS.put(id, current);

            if (STILL_TICKS.getOrDefault(id, 0) >= STAND_STILL_THRESHOLD) {
                player.getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(chakra -> {
                    int regenInterval = Math.max(20, chakra.getMaxChakra() / 2);
                    int regenAmount = Math.max(1, chakra.getMaxChakra() / 20);
                    int counter = REGEN_TICKS.getOrDefault(id, 0) + 1;
                    if (counter >= regenInterval) {
                        counter = 0;
                        int before = chakra.getChakra();
                        chakra.addChakra(regenAmount);
                        if (before != chakra.getChakra() && player instanceof ServerPlayer serverPlayer) {
                            ModMessages.sendToClient(new ChakraSyncS2CPacket(chakra.getChakra(), chakra.getMaxChakra()), serverPlayer);
                        }
                    }
                    REGEN_TICKS.put(id, counter);
                });
            }
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(Jutsucraft.MODID, CHAKRA_ID), new ChakraProvider());
        }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(oldStore -> {
            event.getEntity().getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(newStore -> {
                newStore.setMaxChakra(oldStore.getMaxChakra());
                newStore.setChakra(oldStore.getChakra());
                if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
                    ModMessages.sendToClient(new ChakraSyncS2CPacket(newStore.getChakra(), newStore.getMaxChakra()), serverPlayer);
                }
            });
        });
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(chakra -> {
                ModMessages.sendToClient(new ChakraSyncS2CPacket(chakra.getChakra(), chakra.getMaxChakra()), serverPlayer);
            });
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        ChakraProvider.register(event);
    }
}
