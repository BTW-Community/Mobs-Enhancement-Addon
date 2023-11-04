package net.pottx.mobsenhancement;

import btw.client.fx.EffectHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.MathHelper;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class MEAEffectManager {
    public static final int SLIME_MERGE_EFFECT_ID = 4095;
    public static final int WITHER_SUMMON_EFFECT_ID = 4094;

    public static void initEffects() {
        EffectHandler.effectMap.put(SLIME_MERGE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.slime.big", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
            world.spawnParticle("largeexplode", x, y, z, 0.0D, 0.0D, 0.0D);
        });

        EffectHandler.effectMap.put(WITHER_SUMMON_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            float angel = world.rand.nextFloat() * 360F;
            double smokeX = x + MathHelper.cos(angel) * 0.5D;
            double smokeZ = z + MathHelper.sin(angel) * 0.5D;
            world.spawnParticle("smoke", smokeX, y + data * 0.125D, smokeZ, 0.0D, 0.0D, 0.0D);
        });
    }
}
