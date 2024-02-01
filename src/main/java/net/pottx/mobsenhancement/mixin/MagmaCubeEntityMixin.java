package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.MagmaCubeEntity;
import net.pottx.mobsenhancement.MEAUtils;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.SlimeEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaCubeEntity.class)
public abstract class MagmaCubeEntityMixin extends EntityMagmaCube {
    public MagmaCubeEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void setIsMagma(CallbackInfo ci) {
        ((SlimeEntityAccess)this).setMagma();
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
                MEAUtils.placeNonPersistentLava(world, lavaPosX, lavaPosY, lavaPosZ);
            }
        }
        super.setDead();
    }

    @Override
    public int getMaxHealth()
    {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        i = i > 1 ? 1 : 0;
        int var1 = this.getSlimeSize() + i;

        return var1 * var1;
    }
}
