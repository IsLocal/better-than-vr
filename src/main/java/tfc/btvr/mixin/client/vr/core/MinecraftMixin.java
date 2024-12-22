package tfc.btvr.mixin.client.vr.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ScaledResolution;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.core.Timer;
import net.minecraft.core.util.debug.Debug;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.VRController;
import tfc.btvr.itf.WindowAccessor;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.openvr.SEye;
import tfc.btvr.lwjgl3.openvr.SVRControllerInput;
import tfc.btvr.mixin.client.access.ResolutionAccessor;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftMixin {
	@Shadow
	private Timer timer;
	
	@Shadow
	public boolean skipRenderWorld;
	
	@Shadow
	public PlayerController playerController;
	
	@Shadow
	public WorldRenderer worldRenderer;
	
	@Shadow
	public GameSettings gameSettings;
	
	@Shadow
	@Final
	public ScaledResolution resolution;
	
	@Shadow
	public Screen currentScreen;
	
	@Shadow
	public Renderer renderer;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;endRenderGame(F)V", shift = At.Shift.AFTER), method = "run")
	public void postRender(CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		if (currentScreen != null)
			VRRenderManager.blitUI();
	}
	
	boolean alt = false;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;beginRenderGame(F)V", shift = At.Shift.BEFORE), method = "run")
	public void preRender(CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		VRManager.tick();
		
		VRRenderManager.startFrame(resolution, (float) gameSettings.renderScale.value.scale, gameSettings.renderScale.value.useLinearFiltering, this.timer.partialTicks);
		
		if (VRManager.inStandby) return; // no reason to render VR if the player's not in VR yet
		
		ResolutionAccessor accessor = (ResolutionAccessor) resolution;
		WindowAccessor window = (WindowAccessor) accessor.getGameWindow();

//		boolean rrw = VRSystem.VRSystem_ShouldApplicationReduceRenderingWork();
		boolean rrw = false;
//		if (VRCompositor.VRCompositor_CanRenderScene()) {
		Debug.change("vr");
		// draw left
		if (!rrw || alt) {
			VRRenderManager.start(0);
			window.better_than_vr$overrideSize(SEye.getActiveEye().width, SEye.getActiveEye().height);
			
			this.renderer.beginRenderGame(this.timer.partialTicks);
			
			GL11.glEnable(3008);
			if (!this.skipRenderWorld) {
//				if (this.playerController != null) {
//					this.playerController.setPartialTime(this.timer.partialTicks);
//				}
				
				this.worldRenderer.updateCameraAndRender(this.timer.partialTicks);
			}
			
			this.renderer.endRenderGame(this.timer.partialTicks);
		}
		
		Debug.change("vr");
		if (!rrw || !alt) {
			// draw right
			VRRenderManager.start(1);
			window.better_than_vr$overrideSize(SEye.getActiveEye().width, SEye.getActiveEye().height);
			
			this.renderer.beginRenderGame(this.timer.partialTicks);
			GL11.glEnable(3008);
			if (!this.skipRenderWorld) {
//				if (this.playerController != null) {
//					this.playerController.setPartialTime(this.timer.partialTicks);
//				}
				
				this.worldRenderer.updateCameraAndRender(this.timer.partialTicks);
			}
			
			this.renderer.endRenderGame(this.timer.partialTicks);
		}
//		}
		VRRenderManager.frameFinished(rrw, alt);
		alt = !alt;
		
		// reset to non-vr
		VRRenderManager.start(-1);
		
		window.better_than_vr$overrideSize(-1, -1);
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/WorldClient;updateEntities()V"), method = "runTick")
	public void preTick(CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		VRManager.tickGame((Minecraft) (Object) this);
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/WorldClient;updateEntities()V", shift = At.Shift.AFTER), method = "runTick")
	public void postTick(CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		VRManager.postTick((Minecraft) (Object) this);
	}
	
	@ModifyVariable(argsOnly = true, ordinal = 0, at = @At("HEAD"), method = "mineBlocks")
	public boolean isOn(boolean value) {
		if (!BTVRSetup.checkVR()) return value;
		
		value = value || SVRControllerInput.getInput("gameplay", "Attack");
		if (value) ((VRController) playerController).better_than_vr$cancelMine();
		return value;
	}
	
	@Inject(at = @At("RETURN"), method = "shutdown")
	public void postShutdown(CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		VRManager.shutdown();
	}
}
