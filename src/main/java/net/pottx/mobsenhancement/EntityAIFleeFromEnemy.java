package net.pottx.mobsenhancement;

import net.minecraft.src.*;

import java.util.List;

public class EntityAIFleeFromEnemy extends EntityAIBase {
    public final IEntitySelector entitySelector = new EntityAIFleeFromEntitySelector(this);
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
            List closeEntities = this.theEntity.worldObj.selectEntitiesWithinAABB(this.targetEntityClass, this.theEntity.boundingBox.expand(this.distanceFromTarget, 3.0D, this.distanceFromTarget), this.entitySelector);

            if (closeEntities.isEmpty()) return false;

            for (int i=0; i<closeEntities.size(); i++) {
                if (((EntityCreature)closeEntities.get(i)).getAttackTarget() == this.theEntity
                        && this.theEntity.getEntitySenses().canSee((EntityLiving)closeEntities.get(i))) {
                    this.targetEntity = (EntityLiving)closeEntities.get(i);
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

    public EntityLiving getHost() {
        return this.theEntity;
    }
}
