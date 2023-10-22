package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.ZombieEntity;
import net.pottx.mobsenhancement.EntityAIBreakBlock;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.EntityAISmartAttackOnCollide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends EntityZombie {
    public ZombieEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addBreakBlockTask(CallbackInfo ci) {
        this.tasks.addTask(1, new EntityAIBreakBlock(this));
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
        args.set(1, new EntityAISmartAttackOnCollide(this, EntityPlayer.class, this.moveSpeed, false, 0));
    }
}
