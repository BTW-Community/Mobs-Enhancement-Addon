package net.pottx.mobsenhancement;

import btw.entity.mob.SkeletonEntity;
import btw.entity.mob.ZombieEntity;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.ZombieEntityAccess;

public class EntityAIBreakBlock extends EntityAIBase {
    private EntityLiving theEntity;
    private int targetPosX;
    private int targetPosY;
    private int targetPosZ;
    private int breakingTime;
    private int maxBreakingTime;
    private int breakingProgress = -1;
    private int breakingCooldownCounter = 0;
    private Block targetBlock;
    private boolean hasStoppedBlockBreaking;

    public EntityAIBreakBlock(EntityLiving entityLiving) {
        this.theEntity = entityLiving;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theEntity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            return false;
        } else if (this.theEntity.getAttackTarget() != null) {
            Entity targetEntity = this.theEntity.getAttackTarget();
            PathEntity currentPath = this.theEntity.getNavigator().getPath();
            if (currentPath != null) {
                PathPoint finalPathPoint = currentPath.getFinalPathPoint();
                if (finalPathPoint != null &&
                        finalPathPoint.distanceTo(new PathPoint(MathHelper.floor_double(targetEntity.posX), MathHelper.floor_double(targetEntity.posY), MathHelper.floor_double(targetEntity.posZ))) < 1) {
                    return false;
                }
            }

            float yaw = this.theEntity.rotationYawHead >= 0 ? this.theEntity.rotationYawHead % 360 : this.theEntity.rotationYawHead % 360 + 360;
            double angel;
            boolean lor = Math.random() < 0.5;
            int[][] dxzList;

            if (yaw < 22.5 || yaw > 337.5) {
                dxzList = lor ? new int[][]{{0, 0}, {0, 1}, {1, 1}, {-1, 1}} : new int[][]{{0, 0}, {0, 1}, {-1, 1}, {1, 1}};
            } else if (yaw <= 67.5) {
                dxzList = lor ? new int[][]{{0, 0}, {0, 1}, {-1, 0}, {-1, 1}} : new int[][]{{0, 0}, {-1, 0}, {0, 1}, {-1, 1}};
            } else if (yaw < 112.5) {
                dxzList = lor ? new int[][]{{0, 0}, {-1, 0}, {-1, 1}, {-1, -1}} : new int[][]{{0, 0}, {-1, 0}, {-1, -1}, {-1, 1}};
            } else if (yaw <= 157.5) {
                dxzList = lor ? new int[][]{{0, 0}, {-1, 0}, {0, -1}, {-1, -1}} : new int[][]{{0, 0}, {0, -1}, {-1, 0}, {-1, -1}};
            } else if (yaw < 202.5) {
                dxzList = lor ? new int[][]{{0, 0}, {0, -1}, {-1, -1}, {1, -1}} : new int[][]{{0, 0}, {0, -1}, {1, -1}, {-1, -1}};
            } else if (yaw <= 247.5) {
                dxzList = lor ? new int[][]{{0, 0}, {0, -1}, {1, 0}, {1, -1}} : new int[][]{{0, 0}, {1, 0}, {0, -1}, {1, -1}};
            } else if (yaw < 292.5) {
                dxzList = lor ? new int[][]{{0, 0}, {1, 0}, {1, -1}, {1, 1}} : new int[][]{{0, 0}, {1, 0}, {1, 1}, {1, -1}};
            } else {
                dxzList = lor ? new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}} : new int[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
            }

            for (int dy : new int[]{1, 0}) {

                for (int[] dxz : dxzList) {

                    this.targetPosX = MathHelper.floor_double(this.theEntity.posX) + dxz[0];
                    this.targetPosY = MathHelper.floor_double(this.theEntity.posY) + dy;
                    this.targetPosZ = MathHelper.floor_double(this.theEntity.posZ) + dxz[1];
                    angel = Math.atan2(targetPosX + 0.5F - this.theEntity.posX, targetPosZ + 0.5F - this.theEntity.posZ) * 180 / Math.PI;
                    angel = angel >= 0 ? 360 - angel : 0 - angel;

                    if (this.theEntity.getDistanceSq((double) this.targetPosX + 0.5F, this.theEntity.posY, (double) this.targetPosZ + 0.5F) <= 2.25D
                            && (Math.abs(yaw - (float) angel) <= 45 || Math.abs(yaw - (float) angel) >= 315) && Math.abs(this.theEntity.motionX) < 0.25D && Math.abs(this.theEntity.motionY + 0.0784000015258789D) < 0.01D && Math.abs(this.theEntity.motionZ) < 0.25D) {
                        ItemStack tool = this.theEntity.getHeldItem();
                        this.targetBlock = this.findBreakableBlock(this.targetPosX, this.targetPosY, this.targetPosZ, tool);

                        if (this.targetBlock != null) {
                            if (tool != null && tool.canHarvestBlock(this.theEntity.worldObj, this.targetBlock, this.targetPosX, this.targetPosY, this.targetPosZ)) {
                                this.maxBreakingTime = (int) (this.targetBlock.getBlockHardness(this.theEntity.worldObj, this.targetPosX, this.targetPosY, this.targetPosZ) * 2560 / 15);
                            } else {
                                this.maxBreakingTime = (int) (this.targetBlock.getBlockHardness(this.theEntity.worldObj, this.targetPosX, this.targetPosY, this.targetPosZ) * 2560);
                            }

                            this.breakingCooldownCounter --;
                            if (this.breakingCooldownCounter <= 0) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean continueExecuting() {
        return !this.hasStoppedBlockBreaking;
    }

    public void resetTask() {
        super.resetTask();

        if (this.theEntity instanceof ZombieEntity) {
            ((ZombieEntityAccess) this.theEntity).setIsBreakingBlock(false);
        }

        this.breakingCooldownCounter = 20;
        this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.entityId, this.targetPosX, this.targetPosY, this.targetPosZ, -1);
    }

    public void startExecuting() {
        if (this.theEntity instanceof ZombieEntity) {
            ((ZombieEntityAccess) this.theEntity).setIsBreakingBlock(true);
        }

        this.hasStoppedBlockBreaking = false;
        this.breakingTime = 0;
    }

    public void updateTask() {
        float yaw = this.theEntity.rotationYawHead >= 0 ? this.theEntity.rotationYawHead % 360 : this.theEntity.rotationYawHead % 360 + 360;
        double angel = Math.atan2(targetPosX + 0.5F - this.theEntity.posX, targetPosZ + 0.5F - this.theEntity.posZ) * 180 / Math.PI;
        angel = angel >= 0 ? 360 - angel : 0 - angel;
        if (this.theEntity.getDistanceSq((double)this.targetPosX + 0.5F, this.theEntity.posY, (double)this.targetPosZ + 0.5F) > 2.25D
                || (Math.abs(yaw - (float)angel) > 45 && Math.abs(yaw - (float)angel) < 315) || Math.abs(this.theEntity.motionX) >= 0.25D || Math.abs(this.theEntity.motionY + 0.0784000015258789D) >= 0.01D || Math.abs(this.theEntity.motionZ) >= 0.25D || this.theEntity.isLivingDead
                || this.theEntity.worldObj.getBlockId(this.targetPosX, this.targetPosY, this.targetPosZ) != this.targetBlock.blockID) {
            this.hasStoppedBlockBreaking = true;
        }

        if (this.theEntity.getAttackTarget() != null) {
            Entity targetEntity = this.theEntity.getAttackTarget();
            PathEntity pathToTargetEntity = this.theEntity.getNavigator().getPathToEntity(targetEntity);
            if (pathToTargetEntity != null) {
                PathPoint finalPathPoint = pathToTargetEntity.getFinalPathPoint();
                if (finalPathPoint != null &&
                        finalPathPoint.distanceTo(new PathPoint(MathHelper.floor_double(targetEntity.posX), MathHelper.floor_double(targetEntity.posY), MathHelper.floor_double(targetEntity.posZ))) < 1) {
                    this.hasStoppedBlockBreaking = true;
                }
            }
        }

        if (!this.hasStoppedBlockBreaking) {
            ++this.breakingTime;
            int progress = (int) ((float) this.breakingTime / this.maxBreakingTime * 10.0F);

            if (progress != this.breakingProgress) {
                this.theEntity.swingItem();
                this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.entityId, this.targetPosX, this.targetPosY, this.targetPosZ, progress);
                this.breakingProgress = progress;
                this.theEntity.worldObj.playAuxSFX(2001, this.targetPosX, this.targetPosY, this.targetPosZ, this.targetBlock.blockID);
            }

            if (this.breakingTime >= this.maxBreakingTime) {
                this.theEntity.swingItem();
                this.hasStoppedBlockBreaking = true;
                this.targetBlock.dropComponentItemsOnBadBreak(this.theEntity.worldObj, this.targetPosX, this.targetPosY, this.targetPosZ, this.theEntity.worldObj.getBlockMetadata(this.targetPosX, this.targetPosY, this.targetPosZ), 1.0F);
                this.theEntity.worldObj.setBlockToAir(this.targetPosX, this.targetPosY, this.targetPosZ);
                this.theEntity.worldObj.playAuxSFX(2001, this.targetPosX, this.targetPosY, this.targetPosZ, this.targetBlock.blockID);
            }

            this.theEntity.getNavigator().clearPathEntity();
            this.theEntity.getLookHelper().setLookPosition((double) this.targetPosX + 0.5F, (double) this.targetPosY + 0.5F, (double) this.targetPosZ + 0.5F, 15F, (float) this.theEntity.getVerticalFaceSpeed() * 1.5F);
        }
    }

    private Block findBreakableBlock(int x, int y, int z, ItemStack tool) {
        int blockId = this.theEntity.worldObj.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        boolean canBreak = false;
        if (blockId != 0) {
            if (tool != null) {
                canBreak = tool.canHarvestBlock(this.theEntity.worldObj, block, x, y, z) || (block.getBlockHardness(this.theEntity.worldObj, x, y, z) > 0.0D && block.getBlockHardness(this.theEntity.worldObj, x, y, z) <= 1.5D);
            } else if (!(this.theEntity instanceof SkeletonEntity)){
                canBreak = block.getBlockHardness(this.theEntity.worldObj, x, y, z) > 0.0D && block.getBlockHardness(this.theEntity.worldObj, x, y, z) <= 1.5D;
            }
        }
        return canBreak ? block : null;
    }
}
