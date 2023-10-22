package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.CreeperEntity;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends EntityCreeper {
    public CreeperEntityMixin(World par1World) {
        super(par1World);
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
        if (par1DamageSource.isExplosion()) {
            boolean mobGriefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

            if (this.getPowered()) {
                this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 6F, mobGriefing);
            } else {
                this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 3F, mobGriefing);
            }

            this.setDead();
            return false;
        }

        super.attackEntityFrom(par1DamageSource, par2);
        return true;
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void resetFuseTime(CallbackInfo ci) {
        ((EntityCreeperAccess)this).setFuseTime(20);
    }

    @Inject(
            method = "interact(Lnet/minecraft/src/EntityPlayer;)Z",
            at = @At(value = "INVOKE", target = "Lbtw/entity/mob/CreeperEntity;setNeuteredState(I)V"),
            cancellable = true
    )
    private void explodeWithChance(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (this.rand.nextFloat() < 0.125) {
            boolean mobGriefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

            if (this.getPowered()) {
                this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 6F, mobGriefing);
            } else {
                this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 3F, mobGriefing);
            }

            this.setDead();
            cir.setReturnValue(super.interact(player));
        }
    }
}
