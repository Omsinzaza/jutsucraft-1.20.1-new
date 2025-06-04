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
    private static final double VELOCITY_CHECK_DIST = 0.3D;


    public record WaterChecks(boolean steadyCheck, boolean pushUpFast, boolean pushUpNormal) {
    }

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
        WaterChecks checks = checkSteadyNormalFastPush(player);

        updatePlayerMovement(player);
    }

    private static boolean triggerWaterWalk(Level level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        BlockState block = level.getBlockState(pos);
        return (fluid.is(Fluids.WATER) || fluid.is(Fluids.FLOWING_WATER)) && !block.blocksMotion();
    }

    private static WaterChecks checkSteadyNormalFastPush(Player player) {
        int blockX = (int) Math.floor(player.getX());
        int blockZ = (int) Math.floor(player.getZ());

        int block1 = (int) Math.round(player.getY() - 0.56f);
        boolean steady = triggerWaterWalk(player.level(), new BlockPos(blockX, block1, blockZ));

        int block2 = (int) Math.round(player.getY());
        int beforeBlock2 = (int) Math.round(player.yo);
        boolean fast = triggerWaterWalk(player.level(), new BlockPos(blockX, block2, blockZ));
        if (player.level().isClientSide() && player.yo > player.getY()) {
            boolean beforeY = triggerWaterWalk(player.level(), new BlockPos(blockX, beforeBlock2, blockZ));
            if (!beforeY && steady && player.yo - player.getY() < 0.9f) {
                Vec3 vec = player.getDeltaMovement();
                player.lerpMotion(vec.x(), 0, vec.z());
                player.setPos(player.getX(), block2 + 0.05f, player.getZ());
            } else {
                steady = false;
            }
        }

        int block3 = (int) Math.round(player.getY() - 0.47f);
        boolean normal = triggerWaterWalk(player.level(), new BlockPos(blockX, block3, blockZ));

        return new WaterChecks(steady, fast, normal);
    }
    private static boolean isNearWaterSurface(Player player) {
        Level level = player.level();
        BlockPos below = BlockPos.containing(player.getX(), player.getY() - VELOCITY_CHECK_DIST, player.getZ());
        BlockPos at = BlockPos.containing(player.getX(), Math.ceil(player.getY()), player.getZ());
        return level.getFluidState(below).is(FluidTags.WATER) && !level.getFluidState(at).is(FluidTags.WATER);
    }


    private static void updatePlayerMovement(Player player) {
        WaterChecks checks = checkSteadyNormalFastPush(player);
        Vec3 vec = player.getDeltaMovement();
        double y = vec.y();

        if (checks.pushUpFast()) {
            y = Math.min(y + 0.2D, 0.6D);
        } else if (checks.pushUpNormal()) {
            y = Math.min(y + 0.1D, 0.2D);
        } else if (checks.steadyCheck() && y < 0.0D) {
            y = 0.0D;
            player.resetFallDistance();
            player.setOnGround(true);
            if (player.isFallFlying()) {
                player.stopFallFlying();
            }
        }
        if (isNearWaterSurface(player) && y < 0.0D) {
            y = 0.0D;
        }
        player.lerpMotion(vec.x(), y, vec.z());
    }
}