package net.pottx.mobsenhancement;

import net.minecraft.src.*;

public class EntityAISmartAttackOnCollide extends EntityAIBase
{
    public World worldObj;
    public EntityLiving attacker;
    public EntityLiving entityTarget;

    public int attackTick;
    public float field_75440_e;
    public boolean field_75437_f;

    public PathEntity entityPathEntity;
    public Class classTarget;
    private int field_75445_i;

    private int minHealth;
    private boolean shouldFlee;

    public EntityAISmartAttackOnCollide(EntityLiving par1EntityLiving, Class classTarget, float par2, boolean par3, int iminHealth)
    {
        this.attackTick = 0;
        this.attacker = par1EntityLiving;
        this.worldObj = par1EntityLiving.worldObj;
        this.classTarget = classTarget;
        this.field_75440_e = par2;
        this.field_75437_f = par3;
        this.minHealth = iminHealth;
        this.setMutexBits(3);
    }

    public boolean shouldExecute()
    {
        EntityLiving var1 = this.attacker.getAttackTarget();

        if (var1 == null) {
            return false;
        } else {
            this.shouldFlee = var1 instanceof EntityPlayer || var1.getAttackTarget() == this.attacker;
            if (this.classTarget != null && !this.classTarget.isAssignableFrom(var1.getClass())) {
                return false;
            } else if (this.shouldFlee && this.attacker.getHealth() < minHealth) {
                return false;
            } else {
                this.entityTarget = var1;
                this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(this.entityTarget);
                return this.entityPathEntity != null;
            }
        }
    }

    public boolean continueExecuting() {
        if (this.attacker.getHealth() < this.minHealth) {
            return false;
        } else {
            EntityLiving var1 = this.attacker.getAttackTarget();
            return var1 == null ? false : (!this.entityTarget.isEntityAlive() ? false : (!this.field_75437_f ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistance(MathHelper.floor_double(this.entityTarget.posX), MathHelper.floor_double(this.entityTarget.posY), MathHelper.floor_double(this.entityTarget.posZ))));
        }
    }

    public void startExecuting()
    {
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.field_75440_e);
        this.field_75445_i = 0;
    }

    public void resetTask()
    {
    	// FCMOD: Added
    	if ( attacker.getAttackTarget() == entityTarget )
    	{
    		attacker.setAttackTarget( null );
    	}
    	// END FCMOD
        this.entityTarget = null;
        this.attacker.getNavigator().clearPathEntity();
    }

    public void updateTask()
    {
        this.attacker.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);

        if ((this.field_75437_f || this.attacker.getEntitySenses().canSee(this.entityTarget)) && --this.field_75445_i <= 0)
        {
            this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
            this.attacker.getNavigator().tryMoveToEntityLiving(this.entityTarget, this.field_75440_e);
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        double dCombinedWidth = attacker.width + entityTarget.width;
        double dToolLength = this.attacker.getHeldItem().getItem().isItemTool(this.attacker.getHeldItem()) ? 2.0D : 0.0D;
        double var1 = dCombinedWidth * dCombinedWidth + dToolLength;
        
        if ( entityTarget == attacker.riddenByEntity )
        {
        	return;
        }

        if (this.attacker.getDistanceSq(this.entityTarget.posX, this.entityTarget.boundingBox.minY, this.entityTarget.posZ) <= var1)
        {
            if (this.attackTick <= 0)
            {
                this.attackTick = 20;

                if (this.attacker.getHeldItem() != null)
                {
                    this.attacker.swingItem();
                }

                this.attacker.attackEntityAsMob(this.entityTarget);
            }
        }
    }
}
