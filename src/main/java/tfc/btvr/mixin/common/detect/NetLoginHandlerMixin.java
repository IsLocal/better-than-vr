package tfc.btvr.mixin.common.detect;

import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfc.btvr.lwjgl3.MPManager;

@Mixin(value = PacketHandlerLogin.class, remap = false)
public abstract class NetLoginHandlerMixin {
//	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/net/ServerConfigurationManager;playerLoggedIn(Lnet/minecraft/server/entity/player/EntityPlayerMP;)V", shift = At.Shift.BEFORE), method = "doLogin", locals = LocalCapture.CAPTURE_FAILHARD)
//	public void postInit(Packet1Login packet1login, CallbackInfo ci, EntityPlayerMP entityplayermp, WorldServer worldserver, ChunkCoordinates chunkcoordinates, NetServerHandler netserverhandler, Exception e) {
//		entityplayermp.playerNetServerHandler.sendPacket(new Packet3Chat(MPManager.ackServerMsg));
//	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/net/handler/PacketHandlerServer;sendPacket(Lnet/minecraft/core/net/packet/Packet;)V", ordinal = 6), method = "doLogin")
	public void sendVR(PacketHandlerServer instance, Packet packet) {
		instance.sendPacket(packet);
		instance.sendPacket(new PacketChat(MPManager.ackServerMsg));
	}
}
