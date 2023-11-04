package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.WitchEntity;
import btw.entity.mob.villager.VillagerEntity;
import net.minecraft.src.EntityAINearestAttackableTarget;
import net.minecraft.src.EntityWitch;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.EntityAIFleeFromExplosion;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends EntityWitch {
    public WitchEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addFleeFromExplosionTask(CallbackInfo ci) {
        tasks.addTask(1, new EntityAIFleeFromExplosion(this, 0.375F, 4.0F));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, VillagerEntity.class, 24.0F, 0, ((EntityMobAccess)this).getCanXray() == (byte)0));
    }
}
