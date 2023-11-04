package net.pottx.mobsenhancement.mixin;

import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.access.EntityLivingAccess;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends Entity implements EntityLivingAccess {
    @Shadow
    public float rotationYawHead;

    public EntityLivingMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "func_96121_ay()I",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void returnBiggerPathSearchRange(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(32);
    }

    @Inject(
            method = "canEntityBeSeen(Lnet/minecraft/src/Entity;)Z",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void checkOtherPoints(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        boolean canTopBeSeen = worldObj.rayTraceBlocks_do_do(
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.height, entity.posZ ), false, true ) == null;

        boolean canCenterBeSeen = worldObj.rayTraceBlocks_do_do(
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + (entity.height / 2F), entity.posZ ), false, true ) == null;

        boolean canBottomBeSeen = worldObj.rayTraceBlocks_do_do(
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY, entity.posZ ), false, true ) == null;

        boolean canEyeBeSeen = worldObj.rayTraceBlocks_do_do(
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ ), false, true ) == null;

        cir.setReturnValue(canTopBeSeen || canCenterBeSeen || canBottomBeSeen || canEyeBeSeen);
    }

    @Unique
    public boolean realisticCanEntityBeSeen(Entity entity, double absDist) {
        boolean canTopBeSeen = MEAUtils.rayTraceBlocks_do_do_do( worldObj,
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.height, entity.posZ ), false, true ) == null;

        boolean canCenterBeSeen = MEAUtils.rayTraceBlocks_do_do_do( worldObj,
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + (entity.height / 2F), entity.posZ ), false, true ) == null;

        boolean canBottomBeSeen = MEAUtils.rayTraceBlocks_do_do_do( worldObj,
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY, entity.posZ ), false, true ) == null;

        boolean canEyeBeSeen = MEAUtils.rayTraceBlocks_do_do_do( worldObj,
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ ), false, true ) == null;

        float yaw = this.rotationYawHead >= 0 ? this.rotationYawHead % 360 : this.rotationYawHead % 360 + 360;
        double angel = Math.atan2(entity.posX - this.posX, entity.posZ - this.posZ) * 180 / Math.PI;
        angel = angel >= 0 ? 360 - angel : 0 - angel;

        double realAbsDist = absDist;
        if (entity.isSneaking()) realAbsDist *= 0.5D;

        boolean isInSight = this.getDistanceSqToEntity(entity) < realAbsDist * realAbsDist || Math.abs(yaw - angel) < 75 || Math.abs(yaw - angel) > 185;

        return isInSight && (canTopBeSeen || canCenterBeSeen || canBottomBeSeen || canEyeBeSeen);
    }

    @Unique
    public boolean realisticCanEntityBeSensed(Entity entity) {
        boolean canTopBeSeen = MEAUtils.rayTraceBlocks_do_do_do( worldObj,
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.height, entity.posZ ), false, true ) == null;

        boolean canCenterBeSeen = MEAUtils.rayTraceBlocks_do_do_do( worldObj,
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + (entity.height / 2F), entity.posZ ), false, true ) == null;

        boolean canBottomBeSeen = MEAUtils.rayTraceBlocks_do_do_do( worldObj,
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY, entity.posZ ), false, true ) == null;

        boolean canEyeBeSeen = MEAUtils.rayTraceBlocks_do_do_do( worldObj,
                worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)getEyeHeight(), posZ ),
                worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ ), false, true ) == null;

        return canTopBeSeen || canCenterBeSeen || canBottomBeSeen || canEyeBeSeen;
    }
}
