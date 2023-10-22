package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityMob;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityCreeper.class)
public abstract class EntityCreeperMixin extends EntityMob {
    public EntityCreeperMixin(World par1World) {
        super(par1World);
    }

    @ModifyArgs(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;", ordinal = 0)
    )
    private void resetExplosionCenter(Args args){
        args.set(2, this.posY + 1);
    }

    @ModifyArgs(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;", ordinal = 1)
    )
    private void resetExplosionCenterPowered(Args args){
        args.set(2, this.posY + 1);
    }
}
