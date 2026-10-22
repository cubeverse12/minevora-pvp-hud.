package pl.minevora.pvphud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayDeque;

/** Client-side only. Open the MINEVORA settings screen with Right Shift. */
public final class MinevoraPvpHud implements ClientModInitializer {
    public static MinevoraConfig CONFIG;
    public static int combo = 0, ticks = 0;
    private static long lastHit = 0;
    private static KeyBinding openMenu;
    private static boolean leftDown, rightDown;
    public static final ArrayDeque<Long> lClicks = new ArrayDeque<>(), rClicks = new ArrayDeque<>();

    @Override public void onInitializeClient() {
        CONFIG = new MinevoraConfig();
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.minevorapvphud.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.minevorapvphud"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) client.setScreen(new MinevoraScreen());
            if (++ticks % 20 == 0 && !(client.currentScreen instanceof MinevoraScreen)) CONFIG.reloadIfUpdated();
            if (System.nanoTime()-lastHit > 2_500_000_000L) combo=0;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient && player == MinecraftClient.getInstance().player) {
                combo++; lastHit=System.nanoTime();
            }
            return ActionResult.PASS;
        });
        HudRenderCallback.EVENT.register((ctx, counter) -> {
            MinecraftClient c = MinecraftClient.getInstance();
            if (c.player == null || c.world == null || c.currentScreen instanceof MinevoraScreen) return;
            if (c.currentScreen == null) {
                recordClicks(c);
                ArmorOverlay.renderGame(ctx,c,CONFIG);
                OtherHud.render(ctx,c,CONFIG);
            }
        });
    }
    private static void recordClicks(MinecraftClient c) {
        long window = c.getWindow().getHandle(), now = System.nanoTime();
        boolean left=GLFW.glfwGetMouseButton(window,GLFW.GLFW_MOUSE_BUTTON_LEFT)==GLFW.GLFW_PRESS;
        boolean right=GLFW.glfwGetMouseButton(window,GLFW.GLFW_MOUSE_BUTTON_RIGHT)==GLFW.GLFW_PRESS;
        if(left && !leftDown) lClicks.addLast(now);
        if(right && !rightDown) rClicks.addLast(now);
        leftDown=left; rightDown=right;
        while (!lClicks.isEmpty() && now-lClicks.peekFirst()>1_000_000_000L) lClicks.removeFirst();
        while (!rClicks.isEmpty() && now-rClicks.peekFirst()>1_000_000_000L) rClicks.removeFirst();
    }
}
