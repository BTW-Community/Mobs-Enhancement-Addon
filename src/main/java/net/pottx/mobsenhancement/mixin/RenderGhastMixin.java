package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGhast.class)
public abstract class RenderGhastMixin extends RenderLiving {
    public RenderGhastMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @Inject(
            method = "preRenderGhast(Lnet/minecraft/src/EntityGhast;F)V",
            at = @At(value = "TAIL")
    )
    private void enableGLAlphaFunc(CallbackInfo ci) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
}
