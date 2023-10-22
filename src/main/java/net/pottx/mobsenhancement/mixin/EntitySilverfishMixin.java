package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySilverfish.class)
public abstract class EntitySilverfishMixin extends EntityMob {
    public EntitySilverfishMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "attackEntityFrom(Lnet/minecraft/src/DamageSource;I)Z",
            at = @At(value = "RETURN", ordinal = 1)
    )
    private void splitWhenAttacked(DamageSource par1DamageSource, int par2, CallbackInfoReturnable<Boolean> cir) {
        if (par1DamageSource instanceof EntityDamageSource && par2 < this.getHealth()) {
            //this.worldObj.spawnParticle("largeexplode", this.posX, this.posY + 0.25, this.posZ, 0.25D, 0.0D, 0.0D);
            this.split();
        }
    }

    @Unique
    public void split() {
        double vx = this.motionZ * (0.25 + this.rand.nextDouble() * 0.5);
        double vz = 0 - this.motionX * (0.25 + this.rand.nextDouble() * 0.5);
        int hp = MathHelper.ceiling_double_int(this.getHealth() * 0.5);

        if (!this.worldObj.isRemote) {
            EntitySilverfish child1 = (EntitySilverfish) EntityList.createEntityOfType(EntitySilverfish.class, this.worldObj);
            child1.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            child1.setVelocity(vx, 0, vz);
            child1.setEntityHealth(hp);

            EntitySilverfish child2 = (EntitySilverfish) EntityList.createEntityOfType(EntitySilverfish.class, this.worldObj);
            child2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            child2.setVelocity(-vx, 0, -vz);
            child2.setEntityHealth(hp);

            this.worldObj.spawnEntityInWorld(child1);
            this.worldObj.spawnEntityInWorld(child2);

            this.setDead();
        }
    }
}
