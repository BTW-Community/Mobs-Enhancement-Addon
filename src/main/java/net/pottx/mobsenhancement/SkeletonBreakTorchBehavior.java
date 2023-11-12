package net.pottx.mobsenhancement;

import btw.block.BTWBlocks;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.SkeletonEntityAccess;

public class SkeletonBreakTorchBehavior extends EntityAIBase {
    private EntitySkeleton mySkeleton;
    private int targetPosX;
    private int targetPosY;
    private int targetPosZ;
    private Block targetBlock;

    public SkeletonBreakTorchBehavior(EntitySkeleton mySkeleton) {
        this.mySkeleton = mySkeleton;
    };

    public boolean shouldExecute() {
        if (this.mySkeleton.getAttackTarget() != null || !this.mySkeleton.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            return false;
        } else {
            int myX = MathHelper.floor_double(this.mySkeleton.posX);
            int myY = MathHelper.floor_double(this.mySkeleton.posY);
            int myZ = MathHelper.floor_double(this.mySkeleton.posZ);

            int checkX;
            int checkY;
            int checkZ;
            double minDistSq = 8192D;

            for (int i = -16; i <= 16; i++) {
                for (int j = -16; j <= 16; j++) {
                    for (int k = -15; k <= 17; k++) {
                        checkX = myX + i;
                        checkY = myY + k;
                        checkZ = myZ + j;

                        int blockID = this.mySkeleton.worldObj.getBlockId(checkX, checkY, checkZ);
                        if ((blockID == BTWBlocks.finiteBurningTorch.blockID ||
                                blockID == BTWBlocks.infiniteBurningTorch.blockID ||
                                blockID == Block.torchWood.blockID ||
                                blockID == Block.torchRedstoneActive.blockID) &&
                                (this.mySkeleton.getDistanceSq(checkX + 0.5D, checkY - 0.5D, checkZ + 0.5D)) < minDistSq) {

                            minDistSq = this.mySkeleton.getDistanceSq(checkX + 0.5D, checkY - 0.5D, checkZ + 0.5D);

                            this.targetBlock = Block.blocksList[blockID];
                            this.targetPosX = checkX;
                            this.targetPosY = checkY;
                            this.targetPosZ = checkZ;
                        }
                    }
                }
            }

            if (this.targetBlock != null && Math.random() < 0.25D) {
                return true;
            }
        }

        return false;
    }

    public boolean continueExecuting() {
        return this.mySkeleton.worldObj.getBlockId(targetPosX, targetPosY, targetPosZ) == this.targetBlock.blockID && this.mySkeleton.getAttackTarget() == null;
    }

    public void resetTask() {
        ((SkeletonEntityAccess) this.mySkeleton).setIsBreakingTorch(false);
        this.mySkeleton.getNavigator().clearPathEntity();
        this.targetBlock = null;
    }

    public void startExecuting() {
        ((SkeletonEntityAccess) this.mySkeleton).setIsBreakingTorch(true);
    }

    public void updateTask() {
        if (this.mySkeleton.getDistanceSq(this.targetPosX + 0.5D, this.targetPosY, this.targetPosZ + 0.5D) < 25D) {
            PathEntity pathToTorch = this.mySkeleton.getNavigator().getPathToXYZ(this.targetPosX + 0.5D, this.targetPosY, this.targetPosZ + 0.5D);
            this.mySkeleton.getNavigator().setPath(pathToTorch, 0.375F);
            this.mySkeleton.getLookHelper().setLookPosition(this.targetPosX + 0.5D, this.targetPosY + 0.5D, this.targetPosZ + 0.5D, 10F, this.mySkeleton.getVerticalFaceSpeed());

            if (this.mySkeleton.swingProgressInt <= 0 && this.mySkeleton.getDistanceSq(this.targetPosX + 0.5D, this.targetPosY - 0.5D, this.targetPosZ + 0.5D) < 6.25D) {
                this.mySkeleton.swingItem();
                this.mySkeleton.worldObj.setBlockToAir(this.targetPosX, this.targetPosY, this.targetPosZ);
                this.mySkeleton.worldObj.playAuxSFX(2001, this.targetPosX, this.targetPosY, this.targetPosZ, this.targetBlock.blockID);
            }
        }
    }
}
