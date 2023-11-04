package net.pottx.mobsenhancement;

import btw.block.BTWBlocks;
import net.pottx.mobsenhancement.access.EntityMobAccess;
import net.pottx.mobsenhancement.access.EntityPlayerAccess;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.mixin.EntityAccess;
import net.pottx.mobsenhancement.mixin.EntityLivingAccess;

import java.util.Random;

public class MEAUtils {
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

    public static MovingObjectPosition rayTraceBlocks_do_do_do(World world, Vec3 startVec, Vec3 endVec, boolean bHitFluidSources, boolean bIgnoreIgnorableBlockingBlocks)
    {
        if ( !Double.isNaN(startVec.xCoord) && !Double.isNaN(startVec.yCoord) && !Double.isNaN(startVec.zCoord) &&
                !Double.isNaN(endVec.xCoord) && !Double.isNaN(endVec.yCoord) && !Double.isNaN(endVec.zCoord) )
        {
            Vec3 currentVec = world.getWorldVec3Pool().getVecFromPool( startVec.xCoord, startVec.yCoord, startVec.zCoord );

            double dTotalDeltaX = endVec.xCoord - startVec.xCoord;
            double dTotalDeltaY = endVec.yCoord - startVec.yCoord;
            double dTotalDeltaZ = endVec.zCoord - startVec.zCoord;

            int iEndPosI = MathHelper.floor_double(endVec.xCoord);
            int iEndPosJ = MathHelper.floor_double(endVec.yCoord);
            int iEndPosK = MathHelper.floor_double(endVec.zCoord);

            int iCurrentPosI = MathHelper.floor_double(currentVec.xCoord);
            int iCurrentPosJ = MathHelper.floor_double(currentVec.yCoord);
            int iCurrentPosK = MathHelper.floor_double(currentVec.zCoord);

            double dProportionOfLengthToNextBlockBoundaryX;
            double dProportionOfLengthToNextBlockBoundaryY;
            double dProportionOfLengthToNextBlockBoundaryZ;

            int iAxisFinishedCount = 0;

            int iIncrementI = -1;

            double dNextBlockBoundaryX = (double)iCurrentPosI;
            double dBlockBoundaryIncrementX = -1D;

            if ( iEndPosI > iCurrentPosI )
            {
                iIncrementI = 1;

                dNextBlockBoundaryX += 1D;
                dBlockBoundaryIncrementX = 1D;
            }
            else if ( iEndPosI == iCurrentPosI )
            {
                iIncrementI = 0;

                iAxisFinishedCount++;
            }

            int iIncrementJ = -1;

            double dNextBlockBoundaryY = (double)iCurrentPosJ;
            double dBlockBoundaryIncrementY = -1D;

            if ( iEndPosJ > iCurrentPosJ )
            {
                iIncrementJ = 1;

                dNextBlockBoundaryY += 1D;
                dBlockBoundaryIncrementY = 1D;
            }
            else if ( iEndPosJ == iCurrentPosJ )
            {
                iIncrementJ = 0;

                iAxisFinishedCount++;
            }

            int iIncrementK = -1;

            double dNextBlockBoundaryZ = (double)iCurrentPosK;
            double dBlockBoundaryIncrementZ = -1D;

            if ( iEndPosK > iCurrentPosK )
            {
                iIncrementK = 1;

                dNextBlockBoundaryZ += 1D;
                dBlockBoundaryIncrementZ = 1D;
            }
            else if ( iEndPosK == iCurrentPosK )
            {
                iIncrementK = 0;

                iAxisFinishedCount++;
            }

            int iTempCount = 200;

            while ( iTempCount-- >= 0 )
            {
                int iCurrentBlockID = world.getBlockId( iCurrentPosI, iCurrentPosJ, iCurrentPosK );

                if ( iCurrentBlockID > 0 )
                {
                    Block currentBlock = Block.blocksList[iCurrentBlockID];

                    int[] transparentBlockIDList = {Block.glass.blockID, Block.thinGlass.blockID, Block.fenceIron.blockID, Block.leaves.blockID, BTWBlocks.gratePane.blockID, BTWBlocks.slatsPane.blockID};
                    boolean isCurrentBlockTransparent = false;
                    for (int transparentBlockID : transparentBlockIDList) {
                        if (iCurrentBlockID == transparentBlockID) {
                            isCurrentBlockTransparent = true;
                            break;
                        }
                    }

                    boolean isCurrentBlockIgnorable = currentBlock.getCollisionBoundingBoxFromPool(world, iCurrentPosI, iCurrentPosJ, iCurrentPosK ) == null || isCurrentBlockTransparent;

                    if ( !bIgnoreIgnorableBlockingBlocks || !isCurrentBlockIgnorable )
                    {
                        int iFirstBlockMetadata = world.getBlockMetadata( iCurrentPosI, iCurrentPosJ, iCurrentPosK );

                        if (  currentBlock.canCollideCheck( iFirstBlockMetadata, bHitFluidSources ) )
                        {
                            MovingObjectPosition collisionPosition = currentBlock.collisionRayTrace(world, iCurrentPosI, iCurrentPosJ, iCurrentPosK, currentVec, endVec);

                            if ( collisionPosition != null )
                            {
                                return collisionPosition;
                            }
                        }
                    }
                }

                if ( iAxisFinishedCount >= 3 )
                {
                    return null;
                }

                if ( iIncrementI != 0 )
                {
                    dProportionOfLengthToNextBlockBoundaryX = ( dNextBlockBoundaryX - currentVec.xCoord ) / dTotalDeltaX;
                }
                else
                {
                    dProportionOfLengthToNextBlockBoundaryX = 999.0D;
                }


                if ( iIncrementJ != 0 )
                {
                    dProportionOfLengthToNextBlockBoundaryY = ( dNextBlockBoundaryY - currentVec.yCoord ) / dTotalDeltaY;
                }
                else
                {
                    dProportionOfLengthToNextBlockBoundaryY = 999.0D;
                }


                if ( iIncrementK != 0 )
                {
                    dProportionOfLengthToNextBlockBoundaryZ = ( dNextBlockBoundaryZ - currentVec.zCoord ) / dTotalDeltaZ;
                }
                else
                {
                    dProportionOfLengthToNextBlockBoundaryZ = 999.0D;
                }

                if ( dProportionOfLengthToNextBlockBoundaryX < dProportionOfLengthToNextBlockBoundaryY &&
                        dProportionOfLengthToNextBlockBoundaryX < dProportionOfLengthToNextBlockBoundaryZ )
                {
                    currentVec.xCoord = dNextBlockBoundaryX;
                    currentVec.yCoord += dTotalDeltaY * dProportionOfLengthToNextBlockBoundaryX;
                    currentVec.zCoord += dTotalDeltaZ * dProportionOfLengthToNextBlockBoundaryX;

                    iCurrentPosI += iIncrementI;
                    dNextBlockBoundaryX += dBlockBoundaryIncrementX;

                    if ( iCurrentPosI == iEndPosI )
                    {
                        iAxisFinishedCount++;
                        iIncrementI = 0;
                    }

                }
                else if (dProportionOfLengthToNextBlockBoundaryY < dProportionOfLengthToNextBlockBoundaryZ)
                {
                    currentVec.xCoord += dTotalDeltaX * dProportionOfLengthToNextBlockBoundaryY;
                    currentVec.yCoord = dNextBlockBoundaryY;
                    currentVec.zCoord += dTotalDeltaZ * dProportionOfLengthToNextBlockBoundaryY;

                    iCurrentPosJ += iIncrementJ;
                    dNextBlockBoundaryY += dBlockBoundaryIncrementY;

                    if ( iCurrentPosJ == iEndPosJ )
                    {
                        iAxisFinishedCount++;
                        iIncrementJ = 0;
                    }
                }
                else
                {
                    currentVec.xCoord += dTotalDeltaX * dProportionOfLengthToNextBlockBoundaryZ;
                    currentVec.yCoord += dTotalDeltaY * dProportionOfLengthToNextBlockBoundaryZ;
                    currentVec.zCoord = dNextBlockBoundaryZ;

                    iCurrentPosK += iIncrementK;
                    dNextBlockBoundaryZ += dBlockBoundaryIncrementZ;

                    if ( iCurrentPosK == iEndPosK )
                    {
                        iAxisFinishedCount++;
                        iIncrementK = 0;
                    }
                }
            }
        }

        return null;
    }
}
