package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.ZombiePigmanEntity;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ZombiePigmanEntity.class)
public abstract class ZombiePigmanEntityMixin extends EntityPigZombie {
    public ZombiePigmanEntityMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        EntityPlayer closestPlayer = this.worldObj.getClosestPlayerToEntity(this, 1.0F);

        if (closestPlayer != null) {
            this.becomeAngryAt(closestPlayer);
        }
    }
}
