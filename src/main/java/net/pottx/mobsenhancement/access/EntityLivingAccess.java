package net.pottx.mobsenhancement.access;

import net.minecraft.src.Entity;

public interface EntityLivingAccess {
    boolean realisticCanEntityBeSeen(Entity entity, double absDist);
}
