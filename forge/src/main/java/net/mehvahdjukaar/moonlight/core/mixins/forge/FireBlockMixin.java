package net.mehvahdjukaar.moonlight.core.mixins.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.mehvahdjukaar.moonlight.api.events.IFireConsumeBlockEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin extends BaseFireBlock {

    protected FireBlockMixin(Properties settings, float damage) {
        super(settings, damage);
    }

    @WrapOperation(method = "tryCatchFire",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/world/level/Level.removeBlock (Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean afterRemoveBlock(Level level, BlockPos pos, boolean isMoving, Operation<Boolean> original,
                                     @Local BlockState oldState, @Local(ordinal = 0, argsOnly = true) int chance,
                                     @Local(ordinal = 1, argsOnly = true) int age, @Local(argsOnly = true) Direction face) {
        var event = IFireConsumeBlockEvent.create(pos, level, oldState, chance, age, face);
        MoonlightEventsHelper.postEvent(event, IFireConsumeBlockEvent.class);
        BlockState newState = event.getFinalState();
        if (newState != null) return level.setBlockAndUpdate(pos, newState);
        else return original.call(level, pos, isMoving);
    }
}