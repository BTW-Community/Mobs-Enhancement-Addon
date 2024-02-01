package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.ZombiePigmanEntity;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ZombiePigmanEntity.class)
public abstract class ZombiePigmanEntityMixin extends EntityPigZombie {
    public ZombiePigmanEntityMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        float f = MEAUtils.getGameProgressMobsLevel(this.worldObj) > 1 ? 6.0F : 1.5F;

        EntityPlayer closestPlayer = this.worldObj.getClosestPlayerToEntity(this, f);

        if (closestPlayer != null) {
            this.becomeAngryAt(closestPlayer);
        }
    }
}
