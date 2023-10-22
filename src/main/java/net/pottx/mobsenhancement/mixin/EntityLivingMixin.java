package net.pottx.mobsenhancement.mixin;

import net.pottx.mobsenhancement.access.EntityLivingAccess;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends Entity implements EntityLivingAccess {
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

        float yaw = entity.rotationYaw >= 0 ? entity.rotationYaw % 360 : entity.rotationYaw % 360 + 360;
        double angel = Math.atan2(entity.posX - this.posX, entity.posZ - this.posZ) * 180 / Math.PI;

        boolean isInSight = this.getDistanceSqToEntity(entity) < absDist * absDist || Math.abs(yaw - angel) < 75;

        return isInSight && (canTopBeSeen || canCenterBeSeen || canBottomBeSeen || canEyeBeSeen);
    }
}
