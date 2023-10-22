package net.pottx.mobsenhancement.mixin;

import btw.block.BTWBlocks;
import net.minecraft.src.BiomeDecorator;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenMinable;
import net.minecraft.src.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BiomeDecorator.class)
public abstract class BiomeDecoratorMixin {
    @Shadow
    protected int chunk_X;
    @Shadow
    protected int chunk_Z;
    @Shadow
    protected Random randomGenerator;
    @Shadow
    protected World currentWorld;
    @Unique
    protected WorldGenerator silverfishGenFirstStrata;
    @Unique
    protected WorldGenerator silverfishGenSecondStrata;
    @Unique
    protected WorldGenerator silverfishGenThirdStrata;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void setSilverfishGen(CallbackInfo ci) {
        this.silverfishGenFirstStrata = new WorldGenMinable(BTWBlocks.infestedStone.blockID, 4);
        this.silverfishGenSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, 8);
        this.silverfishGenThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, 16);
    }

    @Inject(
            method = "decorate()V",
            at = @At(value = "HEAD")
    )
    private void addSilverfishGenToDecoration(CallbackInfo ci) {
        this.genSilverfish(9, 64);
    }

    @Unique
    protected void genSilverfish(int numClusters, int maxY) {
        for (int i=0; i<numClusters; i++) {
            int x = this.chunk_X + this.randomGenerator.nextInt(16);
            int y = this.randomGenerator.nextInt(maxY);
            int z = this.chunk_Z + this.randomGenerator.nextInt(16);

            if ( y <= 48 + this.randomGenerator.nextInt( 2 ) )
            {
                if ( y <= 24 + this.randomGenerator.nextInt( 2 ) )
                {
                    silverfishGenThirdStrata.generate(this.currentWorld, this.randomGenerator, x, y, z);
                }
                silverfishGenSecondStrata.generate(this.currentWorld, this.randomGenerator, x, y, z);
            }
            silverfishGenFirstStrata.generate(this.currentWorld, this.randomGenerator, x, y, z);
        }
    }
}
