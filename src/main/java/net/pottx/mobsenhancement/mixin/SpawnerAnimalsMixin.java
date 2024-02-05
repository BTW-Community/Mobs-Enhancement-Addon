package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.SpawnerAnimals;
import net.minecraft.src.WorldServer;
import net.pottx.mobsenhancement.MEAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpawnerAnimals.class)
public abstract class SpawnerAnimalsMixin {
    @ModifyVariable(
            method = "findChunksForSpawning(Lnet/minecraft/src/WorldServer;ZZZ)I",
            at = @At(value = "STORE"),
            ordinal = 7
    )
    private static int spawnHostileGroups(int var17, WorldServer par0WorldServer, boolean par1, boolean par2, boolean par3) {
        if (MEAUtils.getGameProgressMobsLevel(par0WorldServer) > 2) {
            return -9;
        } else {
            return 0;
        }
    }
}
