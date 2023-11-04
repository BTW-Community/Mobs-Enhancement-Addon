package net.pottx.mobsenhancement.mixin;

import btw.block.blocks.MobSpawnerBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(MobSpawnerBlock.class)
public abstract class MobSpawnerBlockMixin extends BlockMobSpawner {
    protected MobSpawnerBlockMixin(int par1) {
        super(par1);
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metaData) {
        super.onBlockDestroyedByPlayer(world, x, y, z, metaData);

        double centerX = x + 0.5;
        double centerY = y + 0.5;
        double centerZ = z + 0.5;

        List closePlayers = world.getEntitiesWithinAABB(EntityPlayer.class,
                AxisAlignedBB.getBoundingBox(
                        centerX - 6.0D, centerY - 7.0D, centerZ - 6.0D,
                        centerX + 6.0D, centerY + 5.0D, centerZ + 6.0D));

        if (!closePlayers.isEmpty()) {
            for (Object closePlayer : closePlayers) {
                world.playSoundAtEntity((EntityPlayer)closePlayer, "mob.ghast.scream", 1.0F, 0.5F);
                ((EntityPlayer)closePlayer).addPotionEffect(new PotionEffect(Potion.blindness.id, 60, 0, true));
                ((EntityPlayer)closePlayer).addPotionEffect(new PotionEffect(Potion.wither.id, 240, 1));
            }
        }
    }
}
