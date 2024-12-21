package tfc.btvr.mixin.client.vr.model;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.model.ModelBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MobRenderer.class, remap = false)
public interface LivingRendererAccessor {
	@Accessor("mainModel")
	ModelBase getMainModel();
}
