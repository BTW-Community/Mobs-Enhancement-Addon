package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.MagmaCubeEntity;
import net.pottx.mobsenhancement.Utils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MagmaCubeEntity.class)
public abstract class MagmaCubeEntityMixin extends EntityMagmaCube {
    public MagmaCubeEntityMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava)) {
            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 10, 2, true));
        }
    }

    @Override
    public void setDead() {
        if (this.getSlimeSize() == 1) {
            World world = this.worldObj;

            int lavaPosX = MathHelper.floor_double(this.posX);
            int lavaPosY = MathHelper.floor_double(this.posY);
            int lavaPosZ = MathHelper.floor_double(this.posZ);

            boolean canPlaceLava = world.isAirBlock(lavaPosX, lavaPosY, lavaPosZ);

            if (canPlaceLava) {
                Utils.placeNonPersistentLava(world, lavaPosX, lavaPosY, lavaPosZ);
            }
        }
        super.setDead();
    }
}
