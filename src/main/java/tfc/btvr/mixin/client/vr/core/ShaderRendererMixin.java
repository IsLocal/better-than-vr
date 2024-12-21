package tfc.btvr.mixin.client.vr.core;

import net.minecraft.client.render.shader.ShadersRenderer;
import org.lwjgl.opengl.Display;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.generic.Eye;
import tfc.btvr.lwjgl3.openvr.SEye;

@Mixin(value = ShadersRenderer.class, remap = false)
public class ShaderRendererMixin {
	@Inject(at = @At("HEAD"), method = "setupFramebuffer")
	public void preSetupFBO(CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
	
		if (SEye.getActiveEye() != null) {
			Eye active = Eye.getActiveEye();
			
			((ShadersRenderer) (Object) this).mc.resolution.width = active.width;
			((ShadersRenderer) (Object) this).mc.resolution.height = active.height;
		} else {
			((ShadersRenderer) (Object) this).mc.resolution.width = Display.getWidth();
			((ShadersRenderer) (Object) this).mc.resolution.height = Display.getHeight();
		}
	}
	
	@Redirect(method = "setupFramebuffer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"))
	public void doNotPrint(Logger instance, String s, Object[] objects) {
	}
}
