package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.PigEntity;
import net.minecraft.src.EntityPig;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.EntityAIFleeFromExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PigEntity.class)
public abstract class PigEntityMixin extends EntityPig {
    public PigEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addFleeFromExplosionTask(CallbackInfo ci) {
        tasks.addTask(2, new EntityAIFleeFromExplosion(this, 0.38F, 4.0F));
    }
}
