package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.CreeperEntity;
import btw.entity.mob.behavior.CreeperSwellBehavior;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperSwellBehavior.class)
public abstract class CreeperSwellBehaviorMixin extends EntityAICreeperSwell {
    @Shadow(remap = false)
    private CreeperEntity myCreeper;

    public CreeperSwellBehaviorMixin(EntityCreeper par1EntityCreeper) {
        super(par1EntityCreeper);
    }

    @Inject(
            method = "shouldExecute()Z",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void igniteFurtherAcrossWall(CallbackInfoReturnable<Boolean> cir) {
        EntityLiving target = this.myCreeper.getAttackTarget();

        if (target == null) {
            cir.setReturnValue(false);
        } else {
            double explodeDistanceSq = this.myCreeper.getEntitySenses().canSee(this.myCreeper.getAttackTarget()) ? 9.0D : 16.0D;
            cir.setReturnValue(this.myCreeper.getCreeperState() > 0 || this.myCreeper.getDistanceSqToEntity(target) < explodeDistanceSq);
        }
    }

    @Redirect(
            method = "updateTask()V",
            at = @At(value = "INVOKE", target = "Lbtw/entity/mob/CreeperEntity;getDistanceSqToEntity(Lnet/minecraft/src/Entity;)D")
    )
    private double calmDownFurtherAcrossWall(CreeperEntity creeperEntity, Entity par1Entity) {
        double distance = this.myCreeper.getDistanceSqToEntity(creeperAttackTarget);
        return this.myCreeper.getEntitySenses().canSee(creeperAttackTarget) ? distance : distance - 1.0D;
    }

    @Redirect(
            method = "updateTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z")
    )
    private boolean dontCheckSenses(EntitySenses entitySenses, Entity par1Entity) {
        return true;
    }
}
