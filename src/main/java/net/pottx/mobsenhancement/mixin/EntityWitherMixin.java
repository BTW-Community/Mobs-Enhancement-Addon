package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAISmartArrowAttack;
import net.pottx.mobsenhancement.access.WitherEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob implements IRangedAttackMob {
    @Shadow
    public abstract int getWatchedTargetId(int par1);
    public EntityWitherMixin(World par1World) {
        super(par1World);
    }

    @ModifyArgs(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void modifyArrowAttackTask(Args args) {
        args.set(1, new EntityAISmartArrowAttack(this, this.moveSpeed, 40, 0, 30.0F, 0.0F));
    }

    @Redirect(
            method = "onLivingUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getEntityByID(I)Lnet/minecraft/src/Entity;", ordinal = 0)
    )
    private Entity stayStillWhenDoingSpecialAttack(World world, int var1) {
        if (((WitherEntityAccess) this).getIsDoingSpecialAttack()) {
            return null;
        } else {
            return this.worldObj.getEntityByID(this.getWatchedTargetId(0));
        }
    }

    @Redirect(
            method = "onLivingUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MathHelper;sqrt_double(D)F", ordinal = 0)
    )
    private float keepFartherDistance(double var6) {
        if (var6 > 64D && !((WitherEntityAccess) this).getIsDoingSpecialAttack()) {
            return 1.5F * MathHelper.sqrt_double(var6);
        } else {
            return Float.MAX_VALUE;
        }
    }
}
