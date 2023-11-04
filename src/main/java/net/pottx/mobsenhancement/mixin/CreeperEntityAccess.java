package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreeperEntity.class)
public interface CreeperEntityAccess {
    @Accessor(value = "determinedToExplode", remap = false)
    void setIsDeterminedToExplode(boolean determinedToExplode);
}
