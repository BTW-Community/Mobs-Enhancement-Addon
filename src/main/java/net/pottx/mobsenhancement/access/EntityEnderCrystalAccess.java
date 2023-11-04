package net.pottx.mobsenhancement.access;

import net.minecraft.src.EntityEnderCrystal;

public interface EntityEnderCrystalAccess {
    void setRespawnCounter(int respawnCounter);

    byte getIsDried();

    void setIsDried(byte isDried);

    EntityEnderCrystal getChargingEnderCrystal();

    boolean getIsOccupied();

    void setIsOccupied(boolean isOccupied);

    boolean getIsHealing();

    void setIsHealing(boolean isHealing);
}
