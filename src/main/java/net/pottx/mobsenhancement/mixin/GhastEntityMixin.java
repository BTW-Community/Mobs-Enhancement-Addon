package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.GhastEntity;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhastEntity.class)
public abstract class GhastEntityMixin extends EntityGhast {
    public GhastEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void replaceTexture(CallbackInfo ci) {
        this.texture = "/meatextures/ghast.png";
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
        if (!par1DamageSource.isMagicDamage() && !"fireball".equals(par1DamageSource.getDamageType())) {
            return false;
        } else {
            super.attackEntityFrom(par1DamageSource, par2);
        }
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        byte var1 = this.dataWatcher.getWatchableObjectByte(16);
        this.texture = var1 == 1 ? "/meatextures/ghast_fire.png" : "/meatextures/ghast.png";
    }
}
