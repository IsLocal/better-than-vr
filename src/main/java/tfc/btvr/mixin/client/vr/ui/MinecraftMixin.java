package tfc.btvr.mixin.client.vr.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ScaledResolution;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.input.InputType;
import net.minecraft.client.input.controller.ControllerInput;
import net.minecraft.core.Timer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.WindowAccessor;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.mixin.client.access.ResolutionAccessor;
import tfc.btvr.util.config.Config;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Shadow
	public Screen currentScreen;
	
	@Shadow
	@Final
	public ScaledResolution resolution;
	
	@Shadow
	public InputType inputType;
	
	@Shadow
	private Timer timer;
	
	@Shadow
	public ControllerInput controllerInput;
	
	@Shadow
	public HudIngame hudIngame;
	
	@Shadow
	public PlayerLocal thePlayer;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;beginRenderGame(F)V", shift = At.Shift.BEFORE), method = "run")
	public void preRender(CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		VRRenderManager.grabUI(true);
		
		if (this.thePlayer != null) {
			ResolutionAccessor accessor = ((ResolutionAccessor) resolution);
			WindowAccessor window = (WindowAccessor) accessor.getGameWindow();
			int sw = resolution.getScaledWidthScreenCoords();
			int sh = resolution.getScaledHeightScreenCoords();
			double swe = resolution.getExactScaledWidthScreenCoords();
			double she = resolution.getExactScaledHeightScreenCoords();
			
			int w = 960;
			
			accessor.setScaledWidth(w);
			accessor.setScaledWidthExact(w);
			accessor.setScaledHeight(w / 2);
			accessor.setScaledHeightExact(w / 2d);
			window.better_than_vr$overrideSize(w, w / 2);
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			GL11.glColorMask(true, true, true, true);
			GL11.glEnable(2929);
			GL11.glEnable(3008);
			GL11.glViewport(0, 0, Config.OVERLAY_RES, Config.OVERLAY_RES / 2);
			hudIngame.renderGameOverlay(this.timer.partialTicks, currentScreen != null, Integer.MIN_VALUE, Integer.MIN_VALUE);
			
			window.better_than_vr$overrideSize(-1, -1);
			accessor.setScaledWidth(sw);
			accessor.setScaledWidthExact(swe);
			accessor.setScaledHeight(sh);
			accessor.setScaledHeightExact(she);
		}
		
		GL11.glViewport(0, 0, resolution.getWidthScreenCoords(), resolution.getHeightScreenCoords());
		VRRenderManager.releaseUI();
	}
}
