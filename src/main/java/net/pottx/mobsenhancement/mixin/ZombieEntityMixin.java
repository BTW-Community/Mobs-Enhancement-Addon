package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.ZombieEntity;
import btw.entity.mob.villager.VillagerEntity;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAIBreakBlock;
import net.pottx.mobsenhancement.EntityAISmartAttackOnCollide;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends EntityZombie {
    @Shadow
    private IEntitySelector targetEntitySelector;

    public ZombieEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addBreakBlockTask(CallbackInfo ci) {
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);

        this.tasks.addTask(1, new EntityAIBreakBlock(this));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 24.0F, 0,
                ((EntityMobAccess)this).getCanXray() == (byte)0));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, VillagerEntity.class, 24.0F, 0, false));
        this.targetTasks.addTask( 2, new EntityAINearestAttackableTarget(this, EntityCreature.class, 24.0F, 0,
                false, false, targetEntitySelector));

    }

    @Inject(
            method = "func_96121_ay()I",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void returnBiggerPathSearchRange(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(32);
    }

    @ModifyArgs(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void replaceAttackOnCollideTask(Args args) {
        args.set(1, new EntityAISmartAttackOnCollide(this, this.moveSpeed, false, 0));
    }

    @ModifyArgs(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 5)
    )
    private void modifyNearestAttackableVillagerTask(Args args) {
        args.set(1, new EntityAINearestAttackableTarget(this, EntityCreature.class, 24F, 0,
                false, false, targetEntitySelector));
    }
}
