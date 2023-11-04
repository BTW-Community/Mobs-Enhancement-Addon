package net.pottx.mobsenhancement.mixin;

import btw.entity.LightningBoltEntity;
import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityEnderCrystalAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(EntityEnderCrystal.class)
public abstract class EntityEnderCrystalMixin extends Entity implements EntityEnderCrystalAccess {
    @Unique
    private static final int IS_DRIED_DATA_WATCHER_ID = 25;
    @Unique
    public EntityEnderCrystal chargingEnderCrystal;
    @Unique
    public boolean isOccupied;
    @Unique
    public boolean isHealing;
    @Unique
    private int chargingCounter;

    public EntityEnderCrystalMixin(World par1World) {
        super(par1World);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addIsDriedData(CallbackInfo ci) {
        dataWatcher.addObject(IS_DRIED_DATA_WATCHER_ID, (byte) 0);
    }

    @Redirect(
            method = "attackEntityFrom(Lnet/minecraft/src/DamageSource;I)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderCrystal;isEntityInvulnerable()Z")
    )
    private boolean doIsDriedCheck(EntityEnderCrystal entityEnderCrystal) {
        return ((EntityEnderCrystalAccess) entityEnderCrystal).getIsDried() == (byte) 1 || entityEnderCrystal.isEntityInvulnerable();
    }

    @Redirect(
            method = "onUpdate()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockId(III)I")
    )
    private int noFireIfDried(World world, int par1, int par2, int par3) {
        if (this.getIsDried() == (byte) 1) {
            return Block.fire.blockID;
        } else {
            return this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        }
    }

    @Inject(
            method = "onUpdate()V",
            at = @At(value = "TAIL")
    )
    private void doChargeCycle (CallbackInfo ci) {
        if (this.getIsDried() == (byte) 1 &&
                this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Block.fire.blockID) {
            this.worldObj.setBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
        }

        if (!this.worldObj.isRemote && this.getIsDried() == (byte) 1 && this.chargingCounter >= 640) {
            this.setIsDried((byte) 0);
        }

        if (this.chargingEnderCrystal != null) {
            if (this.getIsDried() == (byte) 0 || ((EntityEnderCrystalAccess) this.chargingEnderCrystal).getIsDried() == (byte) 1) {
                ((EntityEnderCrystalAccess) this.chargingEnderCrystal).setIsOccupied(false);
                this.chargingEnderCrystal = null;
            } else if (this.chargingCounter < 640) {
                this.chargingCounter++;
            }
        }

        if (this.getIsDried() == (byte) 1) {
            List nearCrystals = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.boundingBox.expand(32D, 32D, 32D));
            EntityEnderCrystal nearestChargerCrystal = null;
            double smallestDistance = Double.MAX_VALUE;
            Iterator nearCrystalsIterator = nearCrystals.iterator();

            while (nearCrystalsIterator.hasNext())
            {
                EntityEnderCrystal chargerCrystal = (EntityEnderCrystal)nearCrystalsIterator.next();
                double distance = chargerCrystal.getDistanceSqToEntity(this);

                if (((EntityEnderCrystalAccess) chargerCrystal).getIsDried() == (byte) 0 &&
                        (chargerCrystal == this.chargingEnderCrystal || !((EntityEnderCrystalAccess) chargerCrystal).getIsOccupied()) &&
                        !((EntityEnderCrystalAccess) chargerCrystal).getIsHealing() &&
                        distance < smallestDistance)
                {
                    smallestDistance = distance;
                    nearestChargerCrystal = chargerCrystal;
                }
            }

            if (nearestChargerCrystal != this.chargingEnderCrystal) {
                if (this.chargingEnderCrystal != null) ((EntityEnderCrystalAccess) this.chargingEnderCrystal).setIsOccupied(false);
                if (nearestChargerCrystal != null) ((EntityEnderCrystalAccess) nearestChargerCrystal).setIsOccupied(true);

                this.chargingEnderCrystal = nearestChargerCrystal;
            }
        }
    }

    @Inject(
            method = "attackEntityFrom(Lnet/minecraft/src/DamageSource;I)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderCrystal;setDead()V")
    )
    private void avengeDestroyer(DamageSource par1DamageSource, int par2, CallbackInfoReturnable<Boolean> cir) {
        Entity destoryer = par1DamageSource.getSourceOfDamage();
        if (destoryer instanceof EntityArrow) {
            destoryer = ((EntityArrow) destoryer).shootingEntity;
        } else if (destoryer instanceof EntityThrowable) {
            destoryer = ((EntityThrowable) destoryer).getThrower();
        }

        if (destoryer instanceof EntityLiving) {
            this.worldObj.addWeatherEffect(EntityList.createEntityOfType(LightningBoltEntity.class, this.worldObj,
                    destoryer.posX, destoryer.posY, destoryer.posZ));
        }
    }

    @Override
    public void setDead() {
        this.setIsDried((byte) 1);
        this.chargingCounter = 0;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Inject(
            method = "writeEntityToNBT(Lnet/minecraft/src/NBTTagCompound;)V",
            at = @At(value = "HEAD")
    )
    private void writeIsDried(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        par1NBTTagCompound.setByte("IsDried", this.getIsDried());
    }

    @Inject(
            method = "readEntityFromNBT(Lnet/minecraft/src/NBTTagCompound;)V",
            at = @At(value = "HEAD")
    )
    private void readIsDried(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        this.setIsDried(par1NBTTagCompound.getByte("IsDried"));
    }

    @Unique
    public byte getIsDried() {
        return this.dataWatcher.getWatchableObjectByte(IS_DRIED_DATA_WATCHER_ID);
    }

    @Unique
    public void setIsDried(byte isDried) {
        this.dataWatcher.updateObject(IS_DRIED_DATA_WATCHER_ID, isDried);
    }

    @Unique
    public EntityEnderCrystal getChargingEnderCrystal() {
        return this.chargingEnderCrystal;
    }

    @Unique
    public boolean getIsOccupied() {
        return this.isOccupied;
    }

    @Unique
    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    @Unique
    public boolean getIsHealing() {
        return this.isHealing;
    }

    @Unique
    public void setIsHealing(boolean isHealing) {
        this.isHealing = isHealing;
    }
}
