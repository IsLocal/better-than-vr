package tfc.btvr.mixin.client.vr.core;

import net.minecraft.client.render.shader.ShadersRenderer;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.WindowAccessor;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.generic.Eye;
import tfc.btvr.lwjgl3.openvr.SEye;
import tfc.btvr.mixin.client.access.ResolutionAccessor;

@Mixin(value = ShadersRenderer.class, remap = false)
public class ShaderRendererMixin {
	@Inject(at = @At("HEAD"), method = "setupFramebuffer")
	public void preSetupFBO(CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		if (SEye.getActiveEye() != null) {
			Eye active = Eye.getActiveEye();
			
			((WindowAccessor) ((ResolutionAccessor) ((ShadersRenderer) (Object) this).mc.resolution)
					.getGameWindow())
					.better_than_vr$overrideSize(active.width, active.height);
//			((ShadersRenderer) (Object) this).mc.resolution.width = active.width;
//			((ShadersRenderer) (Object) this).mc.resolution.height = active.height;
		} else {
			((WindowAccessor) ((ResolutionAccessor) ((ShadersRenderer) (Object) this).mc.resolution)
					.getGameWindow())
					.better_than_vr$overrideSize(-1, -1);
//			((ShadersRenderer) (Object) this).mc.resolution.width = Display.getWidth();
//			((ShadersRenderer) (Object) this).mc.resolution.height = Display.getHeight();
		}
	}
	
	@Redirect(method = "setupFramebuffer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"))
	public void doNotPrint(Logger instance, String s, Object[] objects) {
	}
}
