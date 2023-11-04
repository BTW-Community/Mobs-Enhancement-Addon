package net.pottx.mobsenhancement.mixin;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityWither;
import net.minecraft.src.IEntitySelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityWither.class)
public interface EntityWitherAccess {
    @Accessor("field_82223_h")
    public int[] getField_82223_h();

    @Accessor("field_82224_i")
    public int[] getField_82224_i();

    @Accessor("field_82222_j")
    public int getField_82222_j();

    @Accessor("field_82222_j")
    public void setField_82222_j(int field_82222_j);

    @Accessor("attackEntitySelector")
    public static IEntitySelector getAttackEntitySelector() {
        throw new AssertionError();
    }

    @Invoker("func_82209_a")
    public void invokeFunc_82209_a(int par1, double par2, double par4, double par6, boolean par8);

    @Invoker("func_82216_a")
    public void invokeFunc_82216_a(int par1, EntityLiving par2EntityLiving);
}
