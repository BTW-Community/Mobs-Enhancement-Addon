package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityCreeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityCreeper.class)
public interface EntityCreeperAccess {
    @Accessor("fuseTime")
    public void setFuseTime(int fuseTime);
}
