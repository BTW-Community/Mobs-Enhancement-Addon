package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.EndermanEntity;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityPlayerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLiving implements ICommandSender, EntityPlayerAccess {
    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Unique
    public double realMotionX = 0;
    @Unique
    public double realMotionY = 0;
    @Unique
    public double realMotionZ = 0;

    @Unique
    public double realPrevPosX = this.posX;
    @Unique
    public double realPrevPosY = this.posY;
    @Unique
    public double realPrevPosZ = this.posZ;

    @Inject(
            method = "onUpdate()V",
            at = @At("TAIL")
    )
    private void updateRealMotion(CallbackInfo ci) {
        this.realMotionX = this.posX - this.realPrevPosX;
        this.realMotionY = this.posY - this.realPrevPosY;
        this.realMotionZ = this.posZ - this.realPrevPosZ;
        this.realPrevPosX = this.posX;
        this.realPrevPosY = this.posY;
        this.realPrevPosZ = this.posZ;
    }

    @Override
    public double getRealMotionX() {return this.realMotionX;}
    @Override
    public double getRealMotionY() {return this.realMotionY;}
    @Override
    public double getRealMotionZ() {return this.realMotionZ;}

    @Unique
    public boolean isCloseToEnd() {
        List veryCloseDragons = this.worldObj.selectEntitiesWithinAABB(EntityDragon.class, this.boundingBox.expand(8.0D, 8.0D, 8.0D), IEntitySelector.selectAnything);

        List veryCloseEndermen = this.worldObj.selectEntitiesWithinAABB(EndermanEntity.class, this.boundingBox.expand(4.0D, 4.0D, 4.0D), IEntitySelector.selectAnything);

        if (!veryCloseDragons.isEmpty() || !veryCloseEndermen.isEmpty()) return true;

        //List closeDragons = this.worldObj.selectEntitiesWithinAABB(EntityDragon.class, this.boundingBox.expand(128.0D, 128.0D, 128.0D), IEntitySelector.selectAnything);

        List closeEndermen = this.worldObj.selectEntitiesWithinAABB(EndermanEntity.class, this.boundingBox.expand(64.0D, 64.0D, 64.0D), IEntitySelector.selectAnything);

        Vec3 vLook = this.getLook(1F).normalize();

        Vec3 vDelta;

        double dDist;

        double dotDelta;

        if (!closeEndermen.isEmpty()) {
            for (Object closeEnderman : closeEndermen) {
                vDelta = worldObj.getWorldVec3Pool().getVecFromPool(((EntityLiving) closeEnderman).posX - this.posX, (((EntityLiving) closeEnderman).posY + ((EntityLiving) closeEnderman).getEyeHeight()) - (this.posY + this.getEyeHeight()), ((EntityLiving) closeEnderman).posZ - this.posZ);

                dDist = vDelta.lengthVector();

                vDelta = vDelta.normalize();

                dotDelta = vLook.dotProduct(vDelta);

                if (dotDelta > 1D - 0.025D / dDist) {
                    return this.canEntityBeSeen((EntityLiving) closeEnderman);
                }
            }
        }

        return false;
    }
}
