package net.pottx.mobsenhancement;

import net.minecraft.src.*;

import java.util.List;

public class EntityAIFleeFromEnemy extends EntityAIBase {
    private EntityCreature theEntity;
    private Entity targetEntity;
    private PathEntity fleePath;
    private float distanceFromTarget;
    private float entityMoveSpeed;
    private Class targetEntityClass;
    private int maxHealth;

    public EntityAIFleeFromEnemy(EntityCreature theEntity, Class targetEntityClass, float entityMoveSpeed, float distanceFromEntity, int maxHealth) {
        this.theEntity = theEntity;
        this.targetEntityClass = targetEntityClass;
        this.entityMoveSpeed = entityMoveSpeed;
        this.distanceFromTarget = distanceFromEntity;
        this.maxHealth = maxHealth;
    }
    
    @Override
    public boolean shouldExecute() {
        if (this.theEntity.getHealth() > maxHealth) return false;

        if (this.targetEntityClass == EntityPlayer.class) {
            this.targetEntity = this.theEntity.worldObj.getClosestPlayerToEntity(this.theEntity, this.distanceFromTarget);

            if (this.targetEntity == null) return false;
        } else {
            List closeEntities = this.theEntity.worldObj.getEntitiesWithinAABB(this.targetEntityClass, this.theEntity.boundingBox.expand(this.distanceFromTarget, 3.0D, this.distanceFromTarget));

            if (closeEntities.isEmpty()) return false;

            for (Object closeEntity : closeEntities) {
                if (((EntityCreature)closeEntity).getAttackTarget() == this.theEntity
                        && this.theEntity.getEntitySenses().canSee((EntityLiving)closeEntity)
                        && ((EntityLiving)closeEntity).isEntityAlive()) {
                    this.targetEntity = (EntityLiving)closeEntity;
                    break;
                }
            }
        }

        Vec3 destination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, this.theEntity.worldObj.getWorldVec3Pool().getVecFromPool(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ));

        if (destination == null) {
            return false;
        } else if (this.targetEntity.getDistanceSq(destination.xCoord, destination.yCoord, destination.zCoord) <= this.targetEntity.getDistanceSqToEntity(this.theEntity)) {
            return false;
        } else {
            this.fleePath = this.theEntity.getNavigator().getPathToXYZ(destination.xCoord, destination.yCoord, destination.zCoord);
            return true;
        }
    }

    public boolean continueExecuting() {
        return !this.theEntity.getNavigator().noPath();
    }

    public void startExecuting() {
        this.theEntity.getNavigator().setPath(this.fleePath, this.entityMoveSpeed);
    }

    public void resetTask() {
        this.targetEntity = null;
    }
}
