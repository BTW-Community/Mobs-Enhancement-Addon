package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreeperEntity.class)
public interface CreeperEntityAccess {
    @Accessor("determinedToExplode")
    public void setIsDeterminedToExplode(boolean determinedToExplode);
}
