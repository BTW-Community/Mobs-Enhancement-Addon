package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.SkeletonEntity;
import btw.entity.mob.ZombieEntity;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityAILookIdle;
import net.minecraft.src.EntityLiving;
import net.pottx.mobsenhancement.access.SkeletonEntityAccess;
import net.pottx.mobsenhancement.access.ZombieEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAILookIdle.class)
public abstract class EntityAILookIdleMixin extends EntityAIBase {
    @Shadow
    private EntityLiving idleEntity;

    @Inject(
            method = "shouldExecute()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void shouldNotExecuteIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.idleEntity instanceof ZombieEntity && ((ZombieEntityAccess) this.idleEntity).getIsBreakingBlock()) ||
                (this.idleEntity instanceof SkeletonEntity && ((SkeletonEntityAccess) this.idleEntity).getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "continueExecuting()Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void notContinueIfBreaking(CallbackInfoReturnable<Boolean> cir) {
        if ((this.idleEntity instanceof ZombieEntity && ((ZombieEntityAccess) this.idleEntity).getIsBreakingBlock()) ||
                (this.idleEntity instanceof SkeletonEntity && ((SkeletonEntityAccess) this.idleEntity).getIsBreakingTorch())) {
            cir.setReturnValue(false);
        }
    }
}
