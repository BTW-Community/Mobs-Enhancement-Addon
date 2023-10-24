package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.SkeletonEntity;
import net.pottx.mobsenhancement.EntityAIFleeFromEnemy;
import net.pottx.mobsenhancement.EntityAISmartAttackOnCollide;
import net.pottx.mobsenhancement.EntityAISmartArrowAttack;
import net.pottx.mobsenhancement.access.EntityArrowAccess;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SkeletonEntity.class)
public abstract class SkeletonEntityMixin extends EntitySkeleton {
    public SkeletonEntityMixin(World world) {
        super(world);
    }

    @Unique
    private EntityAISmartArrowAttack aiSmartRangedAttack;

    @Unique
    private EntityAISmartAttackOnCollide aiSmartMeleeAttack;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addFleeFromEnemyTask(CallbackInfo ci) {
        tasks.addTask(3, new EntityAIFleeFromEnemy(this, EntityPlayer.class, 0.375F, 24.0F, 5));
    }

    @Inject(
            method = "entityInit()V",
            at = @At(value = "TAIL")
    )
    private void setSmartAttackAI(CallbackInfo ci) {
        this.aiSmartRangedAttack = new EntityAISmartArrowAttack(this, 0.375F, 60, 6, 20F , 6F);
        this.aiSmartMeleeAttack = new EntityAISmartAttackOnCollide(this, EntityPlayer.class, 0.375F, false, 6);
    }

    @Inject(
            method = {"attackEntityWithRangedAttack(Lnet/minecraft/src/EntityLiving;F)V", "method_4552"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EnchantmentHelper;getEnchantmentLevel(ILnet/minecraft/src/ItemStack;)I"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void resetArrowForPrediction(EntityLiving target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow) {
        ((EntityArrowAccess)arrow).resetForPrediction(this, target, 1.6F, 6F);
    }

    @ModifyArgs(
            method = "setCombatTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;removeTask(Lnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void removeSmartRangedAttackAI(Args args) {
        args.set(0, this.aiSmartRangedAttack);
    }

    @ModifyArgs(
            method = "setCombatTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;removeTask(Lnet/minecraft/src/EntityAIBase;)V", ordinal = 0)
    )
    private void removeSmartMeleeAttackAI(Args args) {
        args.set(0, this.aiSmartMeleeAttack);
    }

    @ModifyArgs(
            method = "setCombatTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 0)
    )
    private void addSmartRangedAttackAI(Args args) {
        args.set(1, this.aiSmartRangedAttack);
    }

    @ModifyArgs(
            method = "setCombatTask()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAITasks;addTask(ILnet/minecraft/src/EntityAIBase;)V", ordinal = 1)
    )
    private void addSmartMeleeAttackAI(Args args) {
        args.set(1, this.aiSmartMeleeAttack);
    }

    @Override
    public void addRandomArmor() {
        super.addRandomArmor();

        if (getHeldItem().itemID != Item.bow.itemID) {
            equipmentDropChances[0] = 0.99F;
        }
    }
}
