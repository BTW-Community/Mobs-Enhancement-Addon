package net.pottx.mobsenhancement;

import net.minecraft.src.Entity;
import net.minecraft.src.IEntitySelector;

public class EntityAIFleeFromEntitySelector implements IEntitySelector {
    private final EntityAIFleeFromEnemy entityFleeAI;
    public EntityAIFleeFromEntitySelector(EntityAIFleeFromEnemy entityFleeAI) {
        this.entityFleeAI = entityFleeAI;
    }
    @Override
    public boolean isEntityApplicable(Entity entity) {
        return entity.isEntityAlive() && this.entityFleeAI.getHost().getEntitySenses().canSee(entity);
    }
}
