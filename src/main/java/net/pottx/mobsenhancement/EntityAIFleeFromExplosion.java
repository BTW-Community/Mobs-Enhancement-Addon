package net.pottx.mobsenhancement;

import btw.entity.MiningChargeEntity;
import btw.entity.mob.CreeperEntity;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;

public class EntityAIFleeFromExplosion extends EntityAIBase {
    private EntityCreature theEntity;
    private Entity targetEntity;
    private PathEntity fleePath;
    private float distanceFromTarget;
    private float entityMoveSpeed;

    public EntityAIFleeFromExplosion(EntityCreature theEntity, float entityMoveSpeed, float distanceFromEntity) {
        this.theEntity = theEntity;
        this.entityMoveSpeed = entityMoveSpeed;
        this.distanceFromTarget = distanceFromEntity;
    }

    @Override
    public boolean shouldExecute() {
        List closeCharges = this.theEntity.worldObj.getEntitiesWithinAABB(MiningChargeEntity.class,
                this.theEntity.boundingBox.expand(this.distanceFromTarget, this.distanceFromTarget, this.distanceFromTarget));
        List closeKegs = this.theEntity.worldObj.getEntitiesWithinAABB(EntityTNTPrimed.class,
                this.theEntity.boundingBox.expand(this.distanceFromTarget * 1.5F, this.distanceFromTarget * 1.5F, this.distanceFromTarget * 1.5F));
        List closeCreepers = this.theEntity.worldObj.getEntitiesWithinAABB(CreeperEntity.class,
                this.theEntity.boundingBox.expand(this.distanceFromTarget, this.distanceFromTarget, this.distanceFromTarget));

        List closeEntities = new ArrayList();

        closeEntities.addAll(closeCharges);
        closeEntities.addAll(closeKegs);

        for (Object closeCreeper : closeCreepers) {
            if (((CreeperEntity)closeCreeper).getCreeperState() > 0) {
                closeEntities.add(closeCreeper);
            }
        }

        if (closeEntities.isEmpty()) return false;

        for (Object closeEntity : closeEntities) {
            if (this.theEntity.getEntitySenses().canSee((Entity)closeEntity)) {
                this.targetEntity = (Entity)closeEntity;
                break;
            }
        }

        Vec3 destination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 6, 4, this.theEntity.worldObj.getWorldVec3Pool().getVecFromPool(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ));

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
