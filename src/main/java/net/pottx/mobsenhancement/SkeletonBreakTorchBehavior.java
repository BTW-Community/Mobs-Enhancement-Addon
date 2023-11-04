package net.pottx.mobsenhancement;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

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

            for (int i = -16; i <= 16; i++) {
                for (int j = -16; j <= 16; j++) {
                    for (int k = -15; k <= 17; k++) {
                        this.targetPosX = myX + i;
                        this.targetPosY = myY + k;
                        this.targetPosZ = myZ + j;

                        int blockID = this.mySkeleton.worldObj.getBlockId(targetPosX, targetPosY, targetPosZ);
                        if (blockID == BTWBlocks.finiteBurningTorch.blockID ||
                                blockID == BTWBlocks.infiniteBurningTorch.blockID ||
                                blockID == Block.torchRedstoneActive.blockID) {
                            if (this.mySkeleton.rand.nextInt(10) == 0) {
                                this.targetBlock = Block.blocksList[blockID];
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean continueExecuting() {
        return this.mySkeleton.worldObj.getBlockId(targetPosX, targetPosY, targetPosZ) == this.targetBlock.blockID && this.mySkeleton.getAttackTarget() == null;
    }

    public void resetTask() {
        this.mySkeleton.getNavigator().clearPathEntity();
    }

    public void startExecuting() {
        PathEntity pathToTorch = this.mySkeleton.getNavigator().getPathToXYZ(this.targetPosX + 0.5D, this.targetPosY, this.targetPosZ + 0.5D);
        this.mySkeleton.getNavigator().setPath(pathToTorch, 0.375F);
    }

    public void updateTask() {
        if (this.mySkeleton.getDistanceSq(this.targetPosX + 0.5D, this.targetPosY, this.targetPosZ + 0.5D) < 16D) {
            this.mySkeleton.getLookHelper().setLookPosition(this.targetPosX + 0.5D, this.targetPosY + 0.5D, this.targetPosZ + 0.5D, 10F, this.mySkeleton.getVerticalFaceSpeed());

            if (this.mySkeleton.getDistanceSq(this.targetPosX + 0.5D, this.targetPosY - 0.5D, this.targetPosZ + 0.5D) < 2D) {
                this.mySkeleton.swingItem();
                this.mySkeleton.worldObj.setBlockToAir(this.targetPosX, this.targetPosY, this.targetPosZ);
                this.mySkeleton.worldObj.playAuxSFX(2001, this.targetPosX, this.targetPosY, this.targetPosZ, this.targetBlock.blockID);
            }
        }
    }
}
