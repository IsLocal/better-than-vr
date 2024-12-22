package tfc.btvr.util.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenMainMenu;
import net.minecraft.client.gui.ScreenPause;
import net.minecraft.client.gui.container.ScreenContainer;
import net.minecraft.client.gui.container.ScreenInventory;
import net.minecraft.client.gui.container.ScreenInventoryCreative;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import org.lwjgl.input.Mouse;
import tfc.btvr.BTVR;
import tfc.btvr.itf.VRScreenData;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.openvr.SVRControllerInput;
import tfc.btvr.mixin.client.vr.selection.MinecraftAccessor;
import tfc.btvr.util.ScreenUtil;
import tfc.btvr.util.config.Config;

import java.util.ArrayList;

public class Bindings {
	
	private static final ArrayList<VRBinding> typicalBindings = new ArrayList<>();
	
	// interaction controls
	private static final VRBinding LEFT_CLICK = new ButtonBinding("gameplay", "Attack", () -> {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null)
			((MinecraftAccessor) mc).invokeClickMouse(0, true, false);
	}, null, null);
	private static final VRBinding RIGHT_CLICK = new ButtonBinding("gameplay", "UseItem", () -> {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null)
			((MinecraftAccessor) mc).invokeClickMouse(1, true, false);
	}, null, null);
	
	// hotbar controls
	private static final VRBinding NEXT_SLOT = new ButtonBinding("gameplay", "HotbarRight", () -> {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null)
			mc.thePlayer.inventory.changeCurrentItem(-1);
	}, null, null);
	private static final VRBinding PREV_SLOT = new ButtonBinding("gameplay", "HotbarLeft", () -> {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null)
			mc.thePlayer.inventory.changeCurrentItem(1);
	}, null, null);
	
	// gui controls
	private static final VRBinding PAUSE_GAME = new ButtonBinding("gameplay", "Pause", () -> {
		Minecraft mc = Minecraft.getMinecraft();
		
		Player player = BTVR.getMenuPlayer();
		if (player != null) {
			if (mc.currentScreen instanceof ScreenMainMenu) {
				player.setPos(
						((VRScreenData) mc.currentScreen).better_than_vr$getPosition()[0],
						((VRScreenData) mc.currentScreen).better_than_vr$getPosition()[1] - 1 - 0.99 + player.heightOffset,
						((VRScreenData) mc.currentScreen).better_than_vr$getPosition()[2]
				);
			}
		}
		
		if (mc.currentScreen == null) mc.displayScreen(new ScreenPause());
		else mc.displayScreen(null);
	}, null, null);
	private static final VRBinding OPEN_INV = new ButtonBinding("gameplay", "OpenInventory", () -> {
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.currentScreen == null && mc.thePlayer != null) {
			if (mc.thePlayer.gamemode == Gamemode.creative)
				mc.displayScreen(new ScreenInventoryCreative(mc.thePlayer));
			else mc.displayScreen(new ScreenInventory(mc.thePlayer));
		} else if (mc.currentScreen instanceof ScreenContainer) mc.displayScreen(null);
	}, null, null);
	
	private static boolean rotateActive = false;
	// motion controls
	private static final VRBinding ROTATE = new PositionBinding("gameplay", "Rotate", (x, y) -> {
		if (Config.SMOOTH_ROTATION.get()) {
			VRManager.yAddRot += (float) ((Config.ROTATION_SPEED.get() / 4) * x);
		} else {
			boolean rotating = x != 0;
			if (rotating && !rotateActive)
				VRManager.yAddRot += (float) (Config.ROTATION_SPEED.get() * Math.signum(x));
			rotateActive = rotating;
		}
	});
	
	public static void renderTick(Minecraft mc) {
		if (mc.currentScreen != null) {
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), mc.currentScreen, true, SVRControllerInput.getInput("gameplay", "UseItem"));
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), mc.currentScreen, false, SVRControllerInput.getInput("gameplay", "Attack"));
		} else {
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), null, true, false);
			ScreenUtil.click(Mouse.getX(), Mouse.getY(), null, false, false);
		}
		
		PAUSE_GAME.tick();
	}
	
	public static void postTick(Minecraft mc) {
		ROTATE.tick();
	}
	
	public static void primaryTick(Minecraft mc) {
		OPEN_INV.tick();
		
		if (mc.currentScreen == null) for (VRBinding typicalBinding : typicalBindings) typicalBinding.tick();
		else for (VRBinding typicalBinding : typicalBindings) typicalBinding.forceRelease();
	}
	
	public static void addBinding(String translation, VRBinding binding) {
		addSpecial(translation, binding);
		typicalBindings.add(binding);
	}
	
	public static void addSpecial(String translation, VRBinding binding) {
	}
	
	static {
		addBinding("btvr.gameplay.attack", LEFT_CLICK);
		addBinding("btvr.gameplay.use_item", RIGHT_CLICK);
		
		addBinding("btvr.gameplay.hotbar_right", NEXT_SLOT);
		addBinding("btvr.gameplay.hotbar_left", PREV_SLOT);
		
		addSpecial("btvr.gameplay.rotate", ROTATE);
		addSpecial("btvr.gameplay.open_inv", OPEN_INV);
		addSpecial("btvr.gameplay.pause", PAUSE_GAME);
	}
}
