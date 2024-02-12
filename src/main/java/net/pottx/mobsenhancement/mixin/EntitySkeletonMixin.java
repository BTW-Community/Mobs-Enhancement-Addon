package net.pottx.mobsenhancement.mixin;

import btw.item.BTWItems;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntitySkeleton.class)
public abstract class EntitySkeletonMixin extends EntityMob {
    @Shadow private EntityAIArrowAttack aiArrowAttack;

    public EntitySkeletonMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void resetMoveSpeed(CallbackInfo ci) {
        this.moveSpeed = 0.375F;
    }

    @Inject(
            method = "getMaxHealth()I",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void returnSmallerMaxHealth(CallbackInfoReturnable<Integer> cir) {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        i = i > 1 ? 20 : (i > 0 ? 16 : 12);

        cir.setReturnValue(i);
    }

    @Inject(
            method = "addRandomArmor()V",
            at = @At(value = "TAIL")
    )
    private void resetRandomWeapon(CallbackInfo ci) {
        if (this.rand.nextInt(8) == 0) {
            this.setCurrentItemOrArmor(0, this.rand.nextInt(4) == 0 ? new ItemStack(Item.axeStone) : new ItemStack(BTWItems.boneClub));
        }
    }

    @ModifyArgs(
            method = "addRandomArmor()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySkeleton;setCurrentItemOrArmor(ILnet/minecraft/src/ItemStack;)V")
    )
    private void setDamagedBow(Args args) {
        ItemStack bow = new ItemStack(Item.bow);
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        i = i > 1 ? 15 : (i > 0 ? 9 : 5);
        bow.setItemDamage(384 - (2 + this.rand.nextInt(i)));
        args.set(1, bow);
    }

    @ModifyArgs(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 7)
    )
    private void modifyNearestAttackableTargetTask(Args args) {
        args.set(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 24.0F, 0, ((EntityMobAccess)this).getCanXray() == (byte)0));
    }

    @ModifyArgs(
            method = "<init>(Lnet/minecraft/src/World;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 2)
    )
    private void modifyFleeSunTask(Args args) {
        args.set(1, new EntityAIFleeSun(this, 0.375F));
    }
}
