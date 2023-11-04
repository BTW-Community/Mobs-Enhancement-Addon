package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLiving.class)
public interface EntityLivingAccess {
    @Invoker("addRandomArmor")
    public void invokeAddRandomArmor();
}
