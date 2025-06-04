package net.sincere.jutsucraft.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.sincere.jutsucraft.chakra.ChakraProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ArrayList;

public class JutsuItem extends Item {
    private static final String INDEX_TAG = "JutsuIndex";

    public static class Jutsu {
        private final String translationKey;
        private final int chakraCost;
        private final int cooldown;
        private final IJutsuCallback callback;

        public Jutsu(String translationKey, int chakraCost, int cooldown, IJutsuCallback callback) {
            this.translationKey = translationKey;
            this.chakraCost = chakraCost;
            this.cooldown = cooldown;
            this.callback = callback;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public int getChakraCost() {
            return chakraCost;
        }

        public int getCooldown() {
            return cooldown;
        }

        public void execute(Player player, Level level) {
            callback.perform(player, level);
        }
    }

    @FunctionalInterface
    public interface IJutsuCallback {
        void perform(Player player, Level level);
    }

    private final List<Jutsu> jutsus = new ArrayList<>();

    /**
     * Categorization for jutsu. This mirrors the original mod's enumeration so
     * that each nature release item can store its jutsu independently.
     */
    public enum JutsuType {
        NINJUTSU,
        DOTON,
        FUTON,
        KATON,
        RAITON,
        SUITON,
        INTON,
        YOTON,
        JINTON,
        MOKUTON,
        JITON,
        IRYO,
        HYOTON,
        BAKUTON,
        SHAKUTON,
        BYAKUGAN,
        SHARINGAN,
        RINNEGAN,
        RANTON,
        FUTTON,
        YOOTON,
        SHIKOTSUMYAKU,
        KUCHIYOSE,
        TENSEIGAN,
        SENJUTSU,
        SIXPATHSENJUTSU,
        KEKKEIMORA,
        SHOTON,
        OTHER
    }

    public JutsuItem(Properties properties, List<Jutsu> jutsuList) {
        super(properties.stacksTo(1));
        this.jutsus.addAll(jutsuList);
    }

    private int getIndex(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(INDEX_TAG)) {
            tag.putInt(INDEX_TAG, 0);
        }
        int idx = tag.getInt(INDEX_TAG);
        if (idx < 0 || idx >= jutsus.size()) {
            idx = 0;
            tag.putInt(INDEX_TAG, 0);
        }
        return idx;
    }

    private void setIndex(ItemStack stack, int index) {
        stack.getOrCreateTag().putInt(INDEX_TAG, index);
    }

    public void nextJutsu(ItemStack stack) {
        int idx = getIndex(stack);
        idx = (idx + 1) % jutsus.size();
        setIndex(stack, idx);
    }

    public Jutsu getCurrentJutsu(ItemStack stack) {
        return jutsus.get(getIndex(stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            Jutsu jutsu = getCurrentJutsu(stack);
            long current = level.getGameTime();
            long cd = stack.getOrCreateTag().getLong("CD_" + getIndex(stack));
            if (cd > current) {
                player.displayClientMessage(Component.translatable("chat.jutsu.cooldown", (cd - current) / 20), true);
                return InteractionResultHolder.fail(stack);
            }
            player.getCapability(ChakraProvider.CHAKRA_CAPABILITY).ifPresent(chakra -> {
                if (chakra.getChakra() >= jutsu.getChakraCost()) {
                    chakra.setChakra(chakra.getChakra() - jutsu.getChakraCost());
                    jutsu.execute(player, level);
                    stack.getTag().putLong("CD_" + getIndex(stack), current + jutsu.getCooldown());
                    player.getCooldowns().addCooldown(this, jutsu.getCooldown());
                    player.displayClientMessage(Component.translatable(jutsu.getTranslationKey()), true);
                } else {
                    player.displayClientMessage(Component.literal("Not enough chakra"), true);
                }
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Jutsu jutsu = getCurrentJutsu(stack);
        tooltip.add(Component.translatable(jutsu.getTranslationKey()));
        tooltip.add(Component.literal("Cost: " + jutsu.getChakraCost()));
        tooltip.add(Component.literal("Press key to switch"));
    }
}