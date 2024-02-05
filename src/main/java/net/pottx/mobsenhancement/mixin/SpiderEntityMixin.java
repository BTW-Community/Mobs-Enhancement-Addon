package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.SpiderEntity;
import net.minecraft.src.EntitySpider;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpiderEntity.class)
public abstract class SpiderEntityMixin extends EntitySpider
{
    public SpiderEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addNaturalPotion(CallbackInfo ci) {
        if (MEAUtils.getGameProgressMobsLevel(this.worldObj) > 0) {
            int i = this.rand.nextInt(16);

            if (i == 0) {
                this.addPotionEffect(new PotionEffect(1, Integer.MAX_VALUE));
            } else if (i == 1) {
                this.addPotionEffect(new PotionEffect(5, Integer.MAX_VALUE));
            } else if (i == 2) {
                this.addPotionEffect(new PotionEffect(14, Integer.MAX_VALUE));
            }
        }
    }

    @Override
    public int getMaxHealth() {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        return i > 0 ? 20 : 16;
    }
}
