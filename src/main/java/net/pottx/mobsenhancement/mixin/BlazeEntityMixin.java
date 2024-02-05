package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.BlazeEntity;
import net.minecraft.src.EntityBlaze;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlazeEntity.class)
public abstract class BlazeEntityMixin extends EntityBlaze {
    public BlazeEntityMixin(World par1World) {
        super(par1World);
    }

    @Override
    public int getMaxHealth() {
        return MEAUtils.getGameProgressMobsLevel(this.worldObj) > 1 ? 24 : 20;
    }
}
