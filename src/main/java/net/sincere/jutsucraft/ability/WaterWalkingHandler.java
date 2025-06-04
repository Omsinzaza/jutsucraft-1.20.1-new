package net.sincere.jutsucraft.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Simple water walking ability. When the player is standing on water and not
 * crouching, they can walk across the surface. Falling from a large height will
 * still cause fall damage and temporarily force the player to sink. Crouching
 * allows the player to sink normally.
 */
@Mod.EventBusSubscriber
public class WaterWalkingHandler {
    private static final float FALL_THRESHOLD = 3.0F;
    private static final String SINK_KEY = "WaterWalkSink";
    private static final int SINK_TICKS = 20;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.isSpectator() || player.isPassenger()) return;

        Level level = player.level();
        BlockPos feet = BlockPos.containing(player.getX(), player.getY() - 0.1D, player.getZ());
        if (!level.getFluidState(feet).is(FluidTags.WATER)) {
            player.getPersistentData().remove(SINK_KEY);
            return;
        }

        CompoundTag tag = player.getPersistentData();
        int sinkTicks = tag.getInt(SINK_KEY);

        if (player.isCrouching()) {
            tag.remove(SINK_KEY);
            return; // Sneak to sink
        }

        if (player.getDeltaMovement().y < 0 && player.fallDistance > FALL_THRESHOLD) {
            player.causeFallDamage(player.fallDistance, 1.0F, player.damageSources().fall());
            player.fallDistance = 0.0F;
            tag.putInt(SINK_KEY, SINK_TICKS);
            return;
        }

        if (sinkTicks > 0) {
            tag.putInt(SINK_KEY, sinkTicks - 1);
            return; // allow sinking after a fall
        } else {
            tag.remove(SINK_KEY);
        }

        Vec3 motion = player.getDeltaMovement();
        if (motion.y < 0) {
            player.setDeltaMovement(motion.x, 0, motion.z);
        }
        player.setOnGround(true);
    }
}
