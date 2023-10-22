package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccess {
    @Accessor("isInWeb")
    public boolean getIsInWeb();
}
