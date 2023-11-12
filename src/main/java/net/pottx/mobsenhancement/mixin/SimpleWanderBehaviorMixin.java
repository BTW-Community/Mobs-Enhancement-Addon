package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.SkeletonEntity;
import btw.entity.mob.ZombieEntity;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityCreature;
import net.pottx.mobsenhancement.access.SkeletonEntityAccess;
import net.pottx.mobsenhancement.access.ZombieEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleWanderBehavior.class)
public abstract class SimpleWanderBehaviorMixin extends EntityAIBase {
    @Shadow private EntityCreature myEntity;

    @Inject(
            method = "shouldExecute()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void shouldNotExecuteIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.myEntity instanceof ZombieEntity && ((ZombieEntityAccess) this.myEntity).getIsBreakingBlock()) ||
                (this.myEntity instanceof SkeletonEntity && ((SkeletonEntityAccess) this.myEntity).getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "continueExecuting()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void notContinueIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.myEntity instanceof ZombieEntity && ((ZombieEntityAccess) this.myEntity).getIsBreakingBlock()) ||
                (this.myEntity instanceof SkeletonEntity && ((SkeletonEntityAccess) this.myEntity).getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }
}
