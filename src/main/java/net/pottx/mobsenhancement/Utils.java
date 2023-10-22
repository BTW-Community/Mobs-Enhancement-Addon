package net.pottx.mobsenhancement;

import net.pottx.mobsenhancement.access.EntityPlayerAccess;
import net.minecraft.src.*;

import java.util.*;

public class Utils {
    public static double[] predictRelativeXZOnRangedHit(EntityLiving target, double initRelativeX, double initRelativeY, double initRelativeZ, float projectileVelocity) {
        double targetMotionX = target.motionX;
        double targetMotionY = target.motionY;
        double targetMotionZ = target.motionZ;
        if (target instanceof EntityPlayer) {
            targetMotionX = ((EntityPlayerAccess)target).getRealMotionX();
            targetMotionY = ((EntityPlayerAccess)target).getRealMotionY();
            targetMotionZ = ((EntityPlayerAccess)target).getRealMotionZ();
        }
        double a = targetMotionX * targetMotionX + targetMotionY * targetMotionY + targetMotionZ * targetMotionZ - (double) projectileVelocity * (double) projectileVelocity;
        double b = 2 * (initRelativeX * targetMotionX + initRelativeY * targetMotionY + initRelativeZ * targetMotionZ);
        double c = initRelativeX * initRelativeX + initRelativeY * initRelativeY + initRelativeZ * initRelativeZ;
        double time = ((double) MathHelper.sqrt_double(b * b - 4 * a * c) - b) / (2 * a);
        double relativeX = initRelativeX - targetMotionX * time;
        double relativeZ = initRelativeZ - targetMotionZ * time;
        return new double[] {relativeX, relativeZ};
    }

    public static void placeNonPersistentLava(World world, int i, int j, int k)
    {
        world.setBlockAndMetadataWithNotify( i, j, k, Block.lavaMoving.blockID, 3 );

        flowLavaIntoBlockIfPossible(world, i + 1, j, k, 4);
        flowLavaIntoBlockIfPossible(world, i - 1, j, k, 4);
        flowLavaIntoBlockIfPossible(world, i, j, k + 1, 4);
        flowLavaIntoBlockIfPossible(world, i, j, k - 1, 4);
    }

    static public void flowLavaIntoBlockIfPossible(World world, int i, int j, int k, int iDecayLevel)
    {
        if ( canLavaDisplaceBlock(world, i, j, k) )
        {
            int iTargetBlockID = world.getBlockId( i, j, k );

            if ( iTargetBlockID > 0 )
            {
                Block.blocksList[iTargetBlockID].onFluidFlowIntoBlock(world, i, j, k, Block.lavaMoving);
            }

            world.setBlockAndMetadataWithNotify( i, j, k, Block.lavaMoving.blockID, iDecayLevel );
        }
    }

    static public boolean canLavaDisplaceBlock(World world, int i, int j, int k)
    {
        Material material = world.getBlockMaterial( i, j, k );

        if ( material == Block.lavaMoving.blockMaterial )
        {
            return false;
        }

        if ( material == Material.water )
        {
            return false;
        }
        else
        {
            Block block = Block.blocksList[world.getBlockId(i, j, k)];

            return block == null || !block.getPreventsFluidFlow(world, i, j, k, Block.waterMoving);
        }
    }
}
