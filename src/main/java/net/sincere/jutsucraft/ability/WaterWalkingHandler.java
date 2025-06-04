package net.sincere.jutsucraft.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.sincere.jutsucraft.chakra.ChakraProvider;
import net.minecraftforge.event.TickEvent;


public class WaterWalkingHandler {
    private static final float FALL_THRESHOLD = 3.0F;
    private static final String SINK_KEY = "WaterWalkSink";
    private static final int SINK_TICKS = 20;
    private static final double VELOCITY_CHECK_DIST = 0.3D;


    public record WaterChecks(boolean steadyCheck, boolean pushUpFast, boolean pushUpNormal) {
    }

    public static void handlePlayerTick(TickEvent.PlayerTickEvent event) {
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
    }
}