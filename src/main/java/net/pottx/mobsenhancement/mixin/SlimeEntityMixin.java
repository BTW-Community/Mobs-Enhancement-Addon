package net.pottx.mobsenhancement.mixin;

import btw.entity.mob.SlimeEntity;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.pottx.mobsenhancement.MEAEffectManager;
import net.pottx.mobsenhancement.MEAUtils;
import net.pottx.mobsenhancement.access.SlimeEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin extends EntitySlime implements SlimeEntityAccess {
    @Unique
    public boolean isMagma;
    @Unique
    private static final int IS_CORE_DATA_WATCHER_ID = 25;
    @Unique
    public int mergeCooldownCounter;
    @Unique
    public boolean isMerging = false;

    public SlimeEntityMixin(World par1World) {
        super(par1World);
    }

    @Shadow
    protected abstract void setSlimeSize(int iSize);

    @Unique
    public void setMagma() {
        this.isMagma = true;
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void addIsCoreData(CallbackInfo ci) {
        this.isMagma = false;
        dataWatcher.addObject(IS_CORE_DATA_WATCHER_ID, (byte) 0);
        this.mergeCooldownCounter = 40;
        if (this.getSlimeSize() < 4 && this.rand.nextInt(4) == 0) this.setIsCore((byte) 1);
    }

    @Inject(
            method = "updateEntityActionState()V",
            at = @At(value = "INVOKE", target = "Lbtw/entity/mob/SlimeEntity;faceEntity(Lnet/minecraft/src/Entity;FF)V", shift = At.Shift.BY, by = 2)
    )
    private void doMergeCheck(CallbackInfo ci) {
        if (!this.isMagma) {
            if (this.getIsCore() == (byte) 1) {
                this.mergeCooldownCounter--;

                if (mergeCooldownCounter <= 0) {
                    List closeSlimes = this.worldObj.getEntitiesWithinAABB(SlimeEntity.class, this.boundingBox.expand(12.0D, 6.0D, 12.0D));

                    for (int i = closeSlimes.size() - 1; i >= 0; i--) {
                        if (((SlimeEntityMixin) closeSlimes.get(i)).getIsCore() == (byte) 1 ||
                                ((SlimeEntity) closeSlimes.get(i)).getSlimeSize() != this.getSlimeSize() ||
                                !((SlimeEntity) closeSlimes.get(i)).isEntityAlive()) {
                            closeSlimes.remove(i);
                        }
                    }

                    if (closeSlimes.size() >= 2) {
                        if (this.isMerging) {
                            faceEntity((SlimeEntity) closeSlimes.get(0), 10.0F, 20.0F);

                            double checkRange = this.getSlimeSize() == 1 ? 0.75D : 1.0D;
                            List veryCloseSlimes = this.worldObj.getEntitiesWithinAABB(SlimeEntity.class, this.boundingBox.expand(checkRange, checkRange, checkRange));

                            for (int i = veryCloseSlimes.size() - 1; i >= 0; i--) {
                                if (((SlimeEntityMixin) veryCloseSlimes.get(i)).getIsCore() == (byte) 1 ||
                                        ((SlimeEntity) veryCloseSlimes.get(i)).getSlimeSize() != this.getSlimeSize() ||
                                        !((SlimeEntity) veryCloseSlimes.get(i)).isEntityAlive()) {
                                    veryCloseSlimes.remove(i);
                                }
                            }

                            if (veryCloseSlimes.size() >= 2) {
                                this.worldObj.playAuxSFX(MEAEffectManager.SLIME_MERGE_EFFECT_ID,
                                        MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);

                                this.setSlimeSize(this.getSlimeSize() * 2);
                                if (this.getSlimeSize() == 4) this.setIsCore((byte) 0);
                                for (int i = 0; i < 2; i++) {
                                    ((SlimeEntityMixin) veryCloseSlimes.get(i)).simpleSetDead();
                                }

                                this.isMerging = false;
                                this.mergeCooldownCounter = 40;
                                for (Object closeSlime : closeSlimes) {
                                    ((SlimeEntityMixin) closeSlime).isMerging = false;
                                }
                            }
                        } else {
                            this.isMerging = true;
                            for (Object closeSlime : closeSlimes) {
                                ((SlimeEntityMixin) closeSlime).isMerging = true;
                            }
                        }
                    } else if (this.isMerging) {
                        this.isMerging = false;
                        this.mergeCooldownCounter = 40;
                        for (Object closeSlime : closeSlimes) {
                            ((SlimeEntityMixin) closeSlime).isMerging = false;
                        }
                    }
                }
            } else {
                List closeSlimes = this.worldObj.getEntitiesWithinAABB(SlimeEntity.class, this.boundingBox.expand(12.0D, 6.0D, 12.0D));

                SlimeEntity coreSlime = null;

                for (int i = 0; i < closeSlimes.size(); i++) {
                    if (((SlimeEntityMixin) closeSlimes.get(i)).getIsCore() == (byte) 1 && ((SlimeEntity) closeSlimes.get(i)).isEntityAlive()) {
                        coreSlime = (SlimeEntity) closeSlimes.get(i);
                        break;
                    }
                }

                if (this.isMerging) {
                    if (coreSlime != null) {
                        faceEntity(coreSlime, 10F, 20F);
                    } else {
                        this.isMerging = false;
                    }
                }
            }
        }
    }

    @Inject(
            method = "setSlimeSize(I)V",
            at = @At(value = "TAIL")
    )
    private void onlyDropXpIfSmall(int iSize, CallbackInfo ci) {
        if (!this.isMagma) {
            if (iSize == 1) {
                this.experienceValue = 2;
            } else {
                this.experienceValue = 0;
            }
        }
    }

    @Unique
    public EntitySlime simpleCreateInstance() {
        EntitySlime instance = this.createInstance();
        ((SlimeEntityMixin)instance).setIsCore((byte)0);
        return instance;
    }

    @Unique
    public void simpleSetDead() {
        this.isDead = true;
    }

    @Unique
    public byte getIsCore() {
        return this.dataWatcher.getWatchableObjectByte(IS_CORE_DATA_WATCHER_ID);
    }

    @Unique
    public void setIsCore(byte isCore) {
        this.dataWatcher.updateObject(IS_CORE_DATA_WATCHER_ID, isCore);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setByte("IsCore", this.getIsCore());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);

        this.setIsCore(par1NBTTagCompound.getByte("IsCore"));
    }

    @Override
    public String getTexture() {
        if (!this.isMagma) {
            if (this.getIsCore() == (byte) 1) {
                if (this.getSlimeSize() == 1) return "/meatextures/core_slime_1.png";
                else if (this.getSlimeSize() == 2) return "/meatextures/core_slime_2.png";
            }
        }

        return super.getTexture();
    }

    @Override
    public void setDead() {
        int var1 = this.getSlimeSize();

        if (!this.worldObj.isRemote && var1 > 1 && this.getHealth() <= 0)
        {
            int var2 = 2 + this.rand.nextInt(3);

            boolean coreNeeded = this.getSlimeSize() == (byte)4 || this.getIsCore() == (byte)1;

            for (int var3 = 0; var3 < var2; ++var3)
            {
                float var4 = ((float)(var3 % 2) - 0.5F) * (float)var1 / 40.0F;
                float var5 = ((float)(var3 / 2) - 0.5F) * (float)var1 / 40.0F;
                EntitySlime var6 = this.simpleCreateInstance();
                if (coreNeeded) {
                    ((SlimeEntityMixin)var6).setIsCore((byte)1);
                    coreNeeded = false;
                }
                ((SlimeEntityMixin)var6).setSlimeSize(var1 / 2);
                var6.setLocationAndAngles(this.posX + (double)var4, this.posY + 0.5D, this.posZ + (double)var5, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(var6);
            }
        }

        this.isDead = true;
    }

    @Override
    public int getMaxHealth()
    {
        int i = MEAUtils.getGameProgressMobsLevel(this.worldObj);
        i = i > 0 ? 1 : 0;
        int var1 = this.getSlimeSize() + i;

        return var1 * var1;
    }
}
