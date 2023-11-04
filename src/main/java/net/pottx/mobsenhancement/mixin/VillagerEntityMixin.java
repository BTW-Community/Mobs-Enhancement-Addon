package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.SkeletonEntity;
import btw.entity.mob.WitchEntity;
import btw.entity.mob.villager.VillagerEntity;
import net.minecraft.src.EntityAIAvoidEntity;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.EntityAIFleeFromExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends EntityVillager {
    public VillagerEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/src/World;I)V",
            at = @At(value = "TAIL")
    )
    private void addFleeFromExplosionTask(CallbackInfo ci) {
        tasks.addTask(1, new EntityAIFleeFromExplosion(this, 0.35F, 4.0F));
        tasks.addTask(1, new EntityAIAvoidEntity(this, SkeletonEntity.class, 20.0F, 0.3F, 0.35F));
        tasks.addTask(1, new EntityAIAvoidEntity(this, WitchEntity.class, 20.0F, 0.3F, 0.35F));
    }
}
