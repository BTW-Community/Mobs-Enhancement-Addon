package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.EndermanEntity;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityPlayerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin extends EntityPlayer {
    public EntityPlayerMPMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "isInGloom()Z",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void gloomIfStarringAtEnd(CallbackInfoReturnable<Boolean> cir) {
        if (((EntityPlayerAccess)this).isCloseToEnd()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "updateGloomState()V",
            at = @At("HEAD")
    )
    private void fasterInGloomCountWhenCloseToEnd(CallbackInfo ci) {
        if (!this.isDead && ((EntityPlayerAccess)this).isCloseToEnd()) {
            inGloomCounter += 3;
        }
    }
}
