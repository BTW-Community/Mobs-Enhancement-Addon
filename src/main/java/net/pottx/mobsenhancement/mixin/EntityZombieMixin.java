package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityZombie.class)
public abstract class EntityZombieMixin extends EntityMob {
    public EntityZombieMixin(World par1World) {
        super(par1World);
    }

    @ModifyArgs(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 10)
    )
    private void modifyNearestAttackablePlayerTask(Args args) {
        args.set(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 24.0F, 0, true));
    }

    @ModifyArgs(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 11)
    )
    private void modifyNearestAttackableVillagerTask(Args args) {
        args.set(1, new EntityAINearestAttackableTarget(this, EntityVillager.class, 24.0F, 0, false));
    }
}
