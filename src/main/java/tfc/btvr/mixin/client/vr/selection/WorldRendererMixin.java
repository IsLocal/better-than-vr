package tfc.btvr.mixin.client.vr.selection;

import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.core.util.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.util.config.Config;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/PlayerLocal;getPosition(FZ)Lnet/minecraft/core/util/phys/Vec3;"))
	public Vec3 preGetPos(PlayerLocal instance, float v, boolean headOffset) {
		if (headOffset) {
			if (!BTVRSetup.checkVR()) return instance.getPosition(v, true);
			
			double[] oset = VRHelper.playerRelative(
					Config.TRACE_HAND.get()
			);
			return
					Vec3.getTempVec3(
							instance.x + oset[0],
							instance.bb.minY + oset[1],
							instance.z + oset[2]
					);
		} else {
			return instance.getPosition(v, false);
		}
	}
	
	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/PlayerLocal;getViewVector(F)Lnet/minecraft/core/util/phys/Vec3;"))
	public Vec3 preGetRot(PlayerLocal instance, float v) {
		if (!BTVRSetup.checkVR()) return instance.getViewVector(v);
	
		double[] oset = VRHelper.getTraceVector(
				Config.TRACE_HAND.get()
		);
		return
				Vec3.getTempVec3(
						oset[0],
						oset[1],
						oset[2]
				);
	}
}
