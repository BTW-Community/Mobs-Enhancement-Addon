package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.CreeperEntity;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends EntityCreeper {
    @Shadow(remap = false)
    public abstract int getNeuteredState();

    public CreeperEntityMixin(World par1World) {
        super(par1World);
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
        if (par1DamageSource.isExplosion() && this.getNeuteredState() == 0) {
            ((CreeperEntityAccess)this).setIsDeterminedToExplode(true);
            this.setCreeperState(1);
            return false;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void resetFuseTimeAndXray(CallbackInfo ci) {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);

        ((EntityCreeperAccess)this).setFuseTime(i > 0 ? 20 : 25);

        if (((EntityMobAccess)this).getCanXray() == (byte)1 && this.rand.nextInt(2) == 0) ((EntityMobAccess)this).setCanXray((byte)0);

        if (i > 0 && this.rand.nextInt(8) == 0)
        {
            this.dataWatcher.updateObject(17, Byte.valueOf((byte)1));
        }

        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);
    }

    @Inject(
            method = "interact(Lnet/minecraft/src/EntityPlayer;)Z",
            at = @At(value = "INVOKE", target = "Lbtw/entity/mob/CreeperEntity;setNeuteredState(I)V"),
            cancellable = true
    )
    private void explodeWithChance(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        int i = this.getPowered() ? 2 : 8;
        if (this.rand.nextInt(i) == 0) {
            boolean mobGriefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

            if (this.getPowered()) {
                this.worldObj.createExplosion(this, this.posX, this.posY + 0.5 * this.height, this.posZ, 6F, mobGriefing);
            } else {
                this.worldObj.createExplosion(this, this.posX, this.posY + 0.5 * this.height, this.posZ, 3F, mobGriefing);
            }

            this.setDead();
            cir.setReturnValue(super.interact(player));
        }
    }

    @Override
    public int getMaxHealth() {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        return i > 0 ? 20 : 16;
    }
}
