package tfc.btvr.mixin.client.vr.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.OpenGLHelper;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRRenderManager;

import java.nio.IntBuffer;

@Mixin(value = OpenGLHelper.class, remap = false)
public class GLHelperMixin {
	@Inject(at = @At("TAIL"), method = "testCapabilities")
	private static void postTestCapabilities(Minecraft minecraft, CallbackInfo ci) {
		if (BTVRSetup.checkVR()) {
			MemoryStack ms = MemoryStack.stackPush();
			IntBuffer w = ms.mallocInt(1);
			IntBuffer h = ms.mallocInt(1);
			BTVRSetup.getSize(w, h);
			VRRenderManager.init(w, h);
			ms.pop();
		}
	}
}
