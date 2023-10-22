package net.pottx.mobsenhancement.mixin;


import btw.entity.mob.GhastEntity;
import net.pottx.mobsenhancement.Utils;
import net.pottx.mobsenhancement.access.EntityArrowAccess;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityArrow.class)
public abstract class EntityArrowMixin extends Entity implements IProjectile, EntityArrowAccess {
    public EntityArrowMixin(World world) {
        super(world);
    }

    @Redirect(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;canBeCollidedWith()Z")
    )
    private boolean notCollideWithGhasts(Entity var10) {
        return var10.canBeCollidedWith() && !(var10 instanceof GhastEntity);
    }

    @Unique
    public void resetForPrediction(EntityLiving owner, EntityLiving target, float arrowVelocity, float deviation) {
        double initRelativeX = target.posX - owner.posX;
        double relativeY = target.boundingBox.minY + (double)(target.height / 3.0F) - this.posY;
        double initRelativeZ = target.posZ - owner.posZ;
        double relativeX = Utils.predictRelativeXZOnRangedHit(target, initRelativeX, relativeY, initRelativeZ, arrowVelocity)[0];
        double relativeZ = Utils.predictRelativeXZOnRangedHit(target, initRelativeX, relativeY, initRelativeZ, arrowVelocity)[1];
        double horizontalDist = (double)MathHelper.sqrt_double(relativeX * relativeX + relativeZ * relativeZ);

        if (horizontalDist >= 1.0E-7D)
        {
            float var14 = (float)(Math.atan2(relativeZ, relativeX) * 180.0D / Math.PI) - 90.0F;
            float var15 = (float)(-(Math.atan2(relativeY, horizontalDist) * 180.0D / Math.PI));
            double var16 = relativeX / horizontalDist;
            double var18 = relativeZ / horizontalDist;
            this.setLocationAndAngles(owner.posX + var16, this.posY, owner.posZ + var18, var14, var15);
            this.yOffset = 0.0F;
            float var20 = (float)horizontalDist * 0.2F;
            this.setThrowableHeading(relativeX, relativeY + (double)var20, relativeZ, arrowVelocity, deviation);
        }
    }
}
