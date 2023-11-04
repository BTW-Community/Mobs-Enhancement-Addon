package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.*;
import net.pottx.mobsenhancement.access.EntityEnderCrystalAccess;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderEnderCrystal.class)
public abstract class RenderEnderCrystalMixin extends Render {
    @ModifyArgs(
            method = "doRenderEnderCrystal(Lnet/minecraft/src/EntityEnderCrystal;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderEnderCrystal;loadTexture(Ljava/lang/String;)V")
    )
    private void changeTextureIfDried(Args args, EntityEnderCrystal par1EntityEnderCrystal, double par2, double par4, double par6, float par8, float par9) {
        if (((EntityEnderCrystalAccess) par1EntityEnderCrystal).getIsDried() == (byte) 1) {
            args.set(0, "/meatextures/crystal_dried.png");
        }
    }

    @ModifyArgs(
            method = "doRenderEnderCrystal(Lnet/minecraft/src/EntityEnderCrystal;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ModelBase;render(Lnet/minecraft/src/Entity;FFFFFF)V")
    )
    private void renderStillIfDried(Args args, EntityEnderCrystal par1EntityEnderCrystal, double par2, double par4, double par6, float par8, float par9) {
        if (((EntityEnderCrystalAccess) par1EntityEnderCrystal).getIsDried() == (byte) 1) {
            args.set(2, 0.0F);
            args.set(3, 0.0F);
        }
    }

    @Inject(
            method = "doRender(Lnet/minecraft/src/Entity;DDDFF)V",
            at = @At(value = "TAIL")
    )
    private void renderChargingBeam(Entity par1Entity, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
        this.doRenderChargingBeam((EntityEnderCrystal) par1Entity, par2, par4, par6, par8, par9);
    }
    
    @Unique
    public void doRenderChargingBeam(EntityEnderCrystal par1EntityEnderCrystal, double par2, double par4, double par6, float par8, float par9) {
        if (((EntityEnderCrystalAccess) par1EntityEnderCrystal).getChargingEnderCrystal() != null)
        {
            float var10 = (float)((EntityEnderCrystalAccess) par1EntityEnderCrystal).getChargingEnderCrystal().innerRotation + par9;
            float var11 = MathHelper.sin(var10 * 0.2F) / 2.0F + 0.5F;
            var11 = (var11 * var11 + var11) * 0.2F;
            float var12 = (float)(((EntityEnderCrystalAccess) par1EntityEnderCrystal).getChargingEnderCrystal().posX - par1EntityEnderCrystal.posX - (par1EntityEnderCrystal.prevPosX - par1EntityEnderCrystal.posX) * (double)(1.0F - par9));
            float var13 = (float)((double)var11 + ((EntityEnderCrystalAccess) par1EntityEnderCrystal).getChargingEnderCrystal().posY - par1EntityEnderCrystal.posY - (par1EntityEnderCrystal.prevPosY - par1EntityEnderCrystal.posY) * (double)(1.0F - par9));
            float var14 = (float)(((EntityEnderCrystalAccess) par1EntityEnderCrystal).getChargingEnderCrystal().posZ - par1EntityEnderCrystal.posZ - (par1EntityEnderCrystal.prevPosZ - par1EntityEnderCrystal.posZ) * (double)(1.0F - par9));
            float var15 = MathHelper.sqrt_float(var12 * var12 + var14 * var14);
            float var16 = MathHelper.sqrt_float(var12 * var12 + var13 * var13 + var14 * var14);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6);
            GL11.glRotatef((float)(-Math.atan2((double)var14, (double)var12)) * 180.0F / (float)Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float)(-Math.atan2((double)var15, (double)var13)) * 180.0F / (float)Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
            Tessellator var17 = Tessellator.instance;
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_CULL_FACE);
            this.loadTexture("/mob/enderdragon/beam.png");
            GL11.glShadeModel(GL11.GL_SMOOTH);
            float var18 = 0.0F - ((float)par1EntityEnderCrystal.ticksExisted + par9) * 0.01F;
            float var19 = MathHelper.sqrt_float(var12 * var12 + var13 * var13 + var14 * var14) / 32.0F - ((float)par1EntityEnderCrystal.ticksExisted + par9) * 0.01F;
            var17.startDrawing(5);
            byte var20 = 8;

            for (int var21 = 0; var21 <= var20; ++var21)
            {
                float var22 = MathHelper.sin((float)(var21 % var20) * (float)Math.PI * 2.0F / (float)var20) * 0.75F;
                float var23 = MathHelper.cos((float)(var21 % var20) * (float)Math.PI * 2.0F / (float)var20) * 0.75F;
                float var24 = (float)(var21 % var20) * 1.0F / (float)var20;
                var17.setColorOpaque_I(0);
                var17.addVertexWithUV((double)(var22 * 0.2F), (double)(var23 * 0.2F), 0.0D, (double)var24, (double)var19);
                var17.setColorOpaque_I(16777215);
                var17.addVertexWithUV((double)var22, (double)var23, (double)var16, (double)var24, (double)var18);
            }

            var17.draw();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glShadeModel(GL11.GL_FLAT);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
        }
    }
}
