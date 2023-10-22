package net.pottx.mobsenhancement.access;

import net.minecraft.src.EntityLiving;

public interface EntityArrowAccess {
    void resetForPrediction(EntityLiving owner, EntityLiving target, float arrowVelocity, float deviation);
}
