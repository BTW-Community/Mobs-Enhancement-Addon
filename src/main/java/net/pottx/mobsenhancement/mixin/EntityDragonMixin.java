package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityEnderCrystalAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(EntityDragon.class)
public abstract class EntityDragonMixin extends EntityLiving {
    @Shadow
    public EntityEnderCrystal healingEnderCrystal;
    @Shadow
    public EntityDragonPart dragonPartHead;

    public EntityDragonMixin(World par1World) {
        super(par1World);
    }

    @Shadow
    public abstract boolean attackEntityFromPart(EntityDragonPart par1EntityDragonPart, DamageSource par2DamageSource, int par3);

    /**
     * @author Pot_Tx
     * @reason I didn't come up with a way to inject.
     */
    @Overwrite
    public void updateDragonEnderCrystal()
    {
        if (this.healingEnderCrystal != null)
        {
            if (this.healingEnderCrystal.isDead)
            {
                if (!this.worldObj.isRemote)
                {
                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource((Explosion)null), 10);
                }

                this.healingEnderCrystal = null;
            }
            else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth())
            {
                this.setEntityHealth(this.getHealth() + 1);
            }
        }

        if (this.rand.nextInt(10) == 0)
        {
            float var1 = 32.0F;
            List var2 = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.boundingBox.expand((double)var1, (double)var1, (double)var1));
            EntityEnderCrystal var3 = null;
            double var4 = Double.MAX_VALUE;
            Iterator var6 = var2.iterator();

            while (var6.hasNext())
            {
                EntityEnderCrystal var7 = (EntityEnderCrystal)var6.next();
                double var8 = var7.getDistanceSqToEntity(this);

                if (((EntityEnderCrystalAccess) var7).getIsDried() == (byte) 0 && var8 < var4)
                {
                    var4 = var8;
                    var3 = var7;
                }
            }

            if (var3 != this.healingEnderCrystal) {
                if (this.healingEnderCrystal != null) ((EntityEnderCrystalAccess) this.healingEnderCrystal).setIsHealing(false);
                if (var3 != null) ((EntityEnderCrystalAccess) var3).setIsHealing(true);
            }

            this.healingEnderCrystal = var3;
        }
    }
}
