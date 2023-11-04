package net.pottx.mobsenhancement.mixin;

import net.pottx.mobsenhancement.access.EntityLivingAccess;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityAITarget.class)
public abstract class EntityAITargetMixin extends EntityAIBase {
    @Shadow
    protected EntityLiving taskOwner;

    @Redirect(
            method = "isSuitableTarget(Lnet/minecraft/src/EntityLiving;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z")
    )
    private boolean doRealisticCanSeeCheck(EntitySenses entitySenses, Entity par1Entity) {
        return ((EntityLivingAccess)this.taskOwner).realisticCanEntityBeSeen(par1Entity, 4);
    }

    @Redirect(
            method = "continueExecuting()Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z")
    )
    private boolean doRealisticCanSenseCheck(EntitySenses entitySenses, Entity par1Entity) {
        return ((EntityLivingAccess)this.taskOwner).realisticCanEntityBeSensed(par1Entity);
    }
}
