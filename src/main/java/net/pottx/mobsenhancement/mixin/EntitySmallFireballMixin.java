package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySmallFireball.class)
public abstract class EntitySmallFireballMixin extends EntityFireball {
    public EntitySmallFireballMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "onImpact(Lnet/minecraft/src/MovingObjectPosition;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySmallFireball;setDead()V")
    )
    private void explodeAfterWither(MovingObjectPosition par1MovingObjectPosition, CallbackInfo ci) {
        if (MEAUtils.getGameProgressMobsLevel(this.worldObj) > 1) {
            this.worldObj.newExplosion((Entity) null, this.posX, this.posY, this.posZ, 1, true, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
        }
    }
}
