package tfc.btvr.mixin.client.access;

import net.minecraft.client.render.window.GameWindowGLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.btvr.itf.WindowAccessor;

@Mixin(value = GameWindowGLFW.class, remap = false)
public class GLFWWindowMixin implements WindowAccessor {
	@Unique
	int overrideWidth = -1;
	
	@Inject(at = @At("HEAD"), method = "getWidthPixels", cancellable = true)
	public void preGetWidth(CallbackInfoReturnable<Integer> cir) {
		if (overrideWidth != -1) cir.setReturnValue(overrideWidth);
	}
	
	@Unique
	int overrideHeight = -1;
	
	@Inject(at = @At("HEAD"), method = "getHeightPixels", cancellable = true)
	public void preGetHeight(CallbackInfoReturnable<Integer> cir) {
		if (overrideHeight != -1) cir.setReturnValue(overrideHeight);
	}
	
	@Override
	public void better_than_vr$overrideSize(int x, int y) {
		this.overrideWidth = x;
		this.overrideHeight = y;
	}
}
