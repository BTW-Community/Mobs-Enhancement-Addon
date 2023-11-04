package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAISmartArrowAttack;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityWitch.class)
public abstract class EntityWitchMixin extends EntityMob implements IRangedAttackMob {
    public EntityWitchMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void resetMoveSpeed(CallbackInfo ci) {
        this.moveSpeed = 0.375F;
    }

    @ModifyArgs(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void modifyArrowAttackTask(Args args) {
        args.set(1, new EntityAISmartArrowAttack(this, 0.375F, 60, 9, 15.0F, 8F));
    }

    @ModifyArgs(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 6)
    )
    private void modifyNearestAttackableTargetTask(Args args) {
        args.set(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 32.0F, 0, ((EntityMobAccess)this).getCanXray() == (byte)0));
    }

    @ModifyArgs(
            method = "attackEntityWithRangedAttack(Lnet/minecraft/src/EntityLiving;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPotion;setThrowableHeading(DDDFF)V")
    )
    private void resetPotionForPrediction(Args args, EntityLiving par1EntityLiving, float par2) {
        float horizontalDist = MathHelper.sqrt_double(((double)args.get(0)) * ((double)args.get(0)) + ((double)args.get(2)) * ((double)args.get(2)));
        double initRelativeY = ((double)args.get(1)) - horizontalDist * 0.2F;
        double relativeX = MEAUtils.predictRelativeXZOnRangedHit(par1EntityLiving, args.get(0), initRelativeY, args.get(2), 1.2F)[0];
        double relativeZ = MEAUtils.predictRelativeXZOnRangedHit(par1EntityLiving, args.get(0), initRelativeY, args.get(2), 1.2F)[1];
        horizontalDist = MathHelper.sqrt_double(relativeX * relativeX + relativeZ * relativeZ);
        args.set(0, relativeX);
        args.set(1, initRelativeY + (double)(horizontalDist * 0.1F));
        args.set(2, relativeZ);
        args.set(3, 1.2F);
    }
}
