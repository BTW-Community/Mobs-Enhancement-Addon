package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityGhast.class)
public interface EntityGhastAccess {
    @Accessor("explosionStrength")
    public void setExplosionStrength(int explosionStrength);
}
