package net.pottx.mobsenhancement.mixin;

import btw.block.BTWBlocks;
import btw.entity.mob.WitherEntity;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.WitherDashBehavior;
import net.pottx.mobsenhancement.WitherSummonMinionBehavior;
import net.pottx.mobsenhancement.access.WitherEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin extends EntityWither implements WitherEntityAccess {
    @Unique
    public boolean isDoingSpecialAttack;

    public WitherEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addSpecialAttackTasks(CallbackInfo ci) {
        this.tasks.addTask(1, new WitherSummonMinionBehavior(this));
        this.tasks.addTask(1, new WitherDashBehavior(this));
    }

    @Unique
    public boolean getIsDoingSpecialAttack() {
        return this.isDoingSpecialAttack;
    }

    @Unique
    public void setIsDoingSpecialAttack(boolean isDoingSpecialAttack) {
        this.isDoingSpecialAttack = isDoingSpecialAttack;
    }

    @Override
    public int getMeleeAttackStrength(Entity target) {
        return 10;
    }

    @Override
    protected void updateAITasks()
    {
        int var1;

        if (this.func_82212_n() > 0)
        {
            var1 = this.func_82212_n() - 1;

            if (var1 <= 0)
            {
                this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
                this.worldObj.func_82739_e(1013, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            }

            this.func_82215_s(var1);

            if (this.ticksExisted % 10 == 0)
            {
                this.heal(10);
            }
        }
        else
        {
            super.updateAITasks();
            int var12;

            for (var1 = 1; var1 < 3; ++var1)
            {
                if (!((WitherEntityAccess) this).getIsDoingSpecialAttack() && this.ticksExisted >= ((EntityWitherAccess) this).getField_82223_h()[var1 - 1])
                {
                    ((EntityWitherAccess) this).getField_82223_h()[var1 - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);

                    {
                        int var10001 = var1 - 1;
                        int var10003 = ((EntityWitherAccess) this).getField_82224_i()[var1 - 1];
                        ((EntityWitherAccess) this).getField_82224_i()[var10001] = ((EntityWitherAccess) this).getField_82224_i()[var1 - 1] + 1;

                        if (var10003 > 15)
                        {
                            float var2 = 10.0F;
                            float var3 = 5.0F;
                            double var4 = MathHelper.getRandomDoubleInRange(this.rand, this.posX - (double)var2, this.posX + (double)var2);
                            double var6 = MathHelper.getRandomDoubleInRange(this.rand, this.posY - (double)var3, this.posY + (double)var3);
                            double var8 = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - (double)var2, this.posZ + (double)var2);
                            ((EntityWitherAccess) this).invokeFunc_82209_a(var1 + 1, var4, var6, var8, true);
                            ((EntityWitherAccess) this).getField_82224_i()[var1 - 1] = 0;
                        }
                    }

                    var12 = this.getWatchedTargetId(var1);

                    if (var12 > 0)
                    {
                        Entity var14 = this.worldObj.getEntityByID(var12);

                        if (var14 != null && var14.isEntityAlive() && this.getDistanceSqToEntity(var14) <= 900.0D && this.canEntityBeSeen(var14))
                        {
                            ((EntityWitherAccess) this).invokeFunc_82216_a(var1 + 1, (EntityLiving)var14);
                            ((EntityWitherAccess) this).getField_82223_h()[var1 - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                            ((EntityWitherAccess) this).getField_82224_i()[var1 - 1] = 0;
                        }
                        else
                        {
                            this.func_82211_c(var1, 0);
                        }
                    }
                    else
                    {
                        List var13 = this.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(20.0D, 8.0D, 20.0D), EntityWitherAccess.getAttackEntitySelector());

                        for (int var16 = 0; var16 < 10 && !var13.isEmpty(); ++var16)
                        {
                            EntityLiving var5 = (EntityLiving)var13.get(this.rand.nextInt(var13.size()));

                            if (var5 != this && var5.isEntityAlive() && this.canEntityBeSeen(var5))
                            {
                                if (var5 instanceof EntityPlayer)
                                {
                                    if (!((EntityPlayer)var5).capabilities.disableDamage)
                                    {
                                        this.func_82211_c(var1, var5.entityId);
                                    }
                                }
                                else
                                {
                                    this.func_82211_c(var1, var5.entityId);
                                }

                                break;
                            }

                            var13.remove(var5);
                        }
                    }
                }
            }

            if (this.getAttackTarget() != null)
            {
                this.func_82211_c(0, this.getAttackTarget().entityId);
            }
            else
            {
                this.func_82211_c(0, 0);
            }

            if (!((WitherEntityAccess) this).getIsDoingSpecialAttack() && ((EntityWitherAccess) this).getField_82222_j() > 0)
            {
                ((EntityWitherAccess) this).setField_82222_j(((EntityWitherAccess) this).getField_82222_j() - 1);

                if (((EntityWitherAccess) this).getField_82222_j() == 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
                {
                    var1 = MathHelper.floor_double(this.posY);
                    var12 = MathHelper.floor_double(this.posX);
                    int var15 = MathHelper.floor_double(this.posZ);
                    boolean var18 = false;

                    for (int var17 = -1; var17 <= 1; ++var17)
                    {
                        for (int var19 = -1; var19 <= 1; ++var19)
                        {
                            for (int var7 = 0; var7 <= 3; ++var7)
                            {
                                int var20 = var12 + var17;
                                int var9 = var1 + var7;
                                int var10 = var15 + var19;
                                int var11 = this.worldObj.getBlockId(var20, var9, var10);

                                if (var11 > 0 && var11 != Block.bedrock.blockID && var11 != Block.endPortal.blockID && var11 != Block.endPortalFrame.blockID &&
                                        var11 != BTWBlocks.soulforgedSteelBlock.blockID )
                                {
                                    var18 = this.worldObj.destroyBlock(var20, var9, var10, true) || var18;
                                }
                            }
                        }
                    }

                    if (var18)
                    {
                        this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1012, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    }
                }
            }

            if (this.ticksExisted % 20 == 0)
            {
                this.heal(1);
            }
        }
    }

    @Override
    public boolean meleeAttack(Entity target)
    {
        setLastAttackingEntity( target );

        int iStrength = getMeleeAttackStrength(target);

        if ( isPotionActive( Potion.damageBoost ) )
        {
            iStrength += 3 << getActivePotionEffect( Potion.damageBoost ).getAmplifier();
        }

        if ( isPotionActive( Potion.weakness ) )
        {
            iStrength -= 2 << getActivePotionEffect( Potion.weakness ).getAmplifier();
        }

        int iKnockback = 2;

        boolean bAttackSuccess = target.attackEntityFrom( DamageSource.causeMobDamage( this ),
                iStrength );

        if ( bAttackSuccess )
        {
            target.addVelocity(
                    -MathHelper.sin( rotationYaw * (float)Math.PI / 180F ) * iKnockback * 0.5F,
                    0.1D,
                    MathHelper.cos( rotationYaw * (float)Math.PI / 180F ) * iKnockback * 0.5F );

            motionX *= 0.6D;
            motionZ *= 0.6D;

            int iFireModifier = EnchantmentHelper.getFireAspectModifier( this );

            if ( iFireModifier > 0 )
            {
                target.setFire( iFireModifier * 4 );
            }
            else if ( isBurning() && rand.nextFloat() < 0.6F )
            {
                target.setFire( 4 );
            }
        }

        return bAttackSuccess;
    }
}
