package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityCreeper.class)
public abstract class EntityCreeperMixin extends EntityMob {
    public EntityCreeperMixin(World par1World) {
        super(par1World);
    }

    @ModifyArgs(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 7)
    )
    private void modifyNearestAttackablePlayerTask(Args args) {
        args.set(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 32.0F, 0, ((EntityMobAccess)this).getCanXray() == (byte)0));
    }

    @ModifyArgs(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;", ordinal = 0)
    )
    private void resetExplosionCenter(Args args){
        args.set(2, this.posY + 0.5 * this.height);
    }

    @ModifyArgs(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;", ordinal = 1)
    )
    private void resetExplosionCenterPowered(Args args){
        args.set(2, this.posY + 0.5 * this.height);
    }
}
