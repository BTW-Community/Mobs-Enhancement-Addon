package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.SkeletonEntity;
import btw.entity.mob.villager.VillagerEntity;
import net.pottx.mobsenhancement.*;
import net.pottx.mobsenhancement.access.EntityArrowAccess;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import net.pottx.mobsenhancement.access.SkeletonEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SkeletonEntity.class)
public abstract class SkeletonEntityMixin extends EntitySkeleton implements SkeletonEntityAccess {
    @Shadow public abstract void setCombatTask();

    public SkeletonEntityMixin(World world) {
        super(world);
    }

    @Unique
    private EntityAISmartArrowAttack aiSmartRangedAttack;

    @Unique
    private EntityAISmartAttackOnCollide aiSmartMeleeAttack;

    @Unique
    private boolean isBreakingTorch;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addExtraTasks(CallbackInfo ci) {
        this.tasks.removeAllTasksOfClass(EntityAIWatchClosest.class);
        this.tasks.removeAllTasksOfClass(EntityAIFleeSun.class);

        tasks.addTask(2, new EntityAIFleeFromExplosion(this, 0.375F, 4.0F));
        tasks.addTask(3, new EntityAIFleeFromEnemy(this, EntityPlayer.class, 0.375F, 24.0F, 5));
        this.targetTasks.addTask(4, new SkeletonBreakTorchBehavior(this));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, VillagerEntity.class, 24.0F, 0, ((EntityMobAccess)this).getCanXray() == (byte)0));
    }

    @Inject(
            method = "entityInit()V",
            at = @At(value = "TAIL")
    )
    private void setSmartAttackAI(CallbackInfo ci) {
        this.aiSmartRangedAttack = new EntityAISmartArrowAttack(this, 0.375F, 60, 6, 20F , 6F);
        this.aiSmartMeleeAttack = new EntityAISmartAttackOnCollide(this, 0.375F, false, 6);
    }

    @Inject(
            method = {"attackEntityWithRangedAttack(Lnet/minecraft/src/EntityLiving;F)V", "method_4552"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EnchantmentHelper;getEnchantmentLevel(ILnet/minecraft/src/ItemStack;)I"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void resetArrowForPrediction(EntityLiving target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow) {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        float f = i > 2 ? 2F : (i > 1 ? 4F : (i > 0 ? 6F : 8F));
        ((EntityArrowAccess)arrow).resetForPrediction(this, target, 1.6F, f);
    }

    @Inject(
            method = {"attackEntityWithRangedAttack(Lnet/minecraft/src/EntityLiving;F)V", "method_4552"},
            at = @At(value = "TAIL")
    )
    private void damageBow(EntityLiving target, float fDamageModifier, CallbackInfo ci) {
        ItemStack itemStack = this.getHeldItem();
        if (itemStack.getItem() == Item.bow) {
            itemStack.damageItem(1, this);
            if (itemStack.stackSize <= 0) {
                SkeletonEntity skeleton = (SkeletonEntity) EntityList.createEntityOfType(SkeletonEntity.class, this.worldObj);
                skeleton.setSkeletonType(this.getSkeletonType());
                skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                skeleton.setRotationYawHead(this.rotationYawHead);
                skeleton.setEntityHealth(this.health);
                skeleton.setCurrentItemOrArmor(0, (ItemStack) null);
                this.setDead();
                this.worldObj.spawnEntityInWorld(skeleton);
            }
        }
    }

    @Inject(
            method = "initCreature()V",
            at = @At(value = "TAIL")
    )
    private void witherChanceAfterNether(CallbackInfo ci) {
        if (this.worldObj.provider.dimensionId == 0 && this.posY < 32 && getRNG().nextInt( 4 ) == 0)
        {
            setSkeletonType( 1 );
        }
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

    @ModifyVariable(
            method = "dropFewItems(ZI)V",
            at = @At(value = "STORE"),
            ordinal = 1
    )
    private int dropLessBones(int iNumBones, boolean bKilledByPlayer, int iLootingModifier) {
        return this.rand.nextInt(2 + iLootingModifier);
    }

    @Unique
    public boolean getIsBreakingTorch() {
        return this.isBreakingTorch;
    }

    @Unique
    public void setIsBreakingTorch(boolean isBreakingTorch) {
        this.isBreakingTorch = isBreakingTorch;
    }

    @Override
    public void addRandomArmor() {
        super.addRandomArmor();

        if (getHeldItem().itemID != Item.bow.itemID) {
            equipmentDropChances[0] = 0.99F;
        }
    }

    @Redirect(
            method = "onLivingUpdate()V",
            at = @At(value = "INVOKE", target = "Lbtw/entity/mob/SkeletonEntity;checkForCatchFireInSun()V")
    )
    private void skipSunCheck(SkeletonEntity skeletonEntity) {
    }
}
