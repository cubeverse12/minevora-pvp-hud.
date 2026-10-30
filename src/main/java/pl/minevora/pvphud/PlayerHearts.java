package pl.minevora.pvphud;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Billboarded hearts above other visible players, rendered with normal depth testing.
 * Client-side health is only as accurate as values exposed by the connected server.
 */
public final class PlayerHearts {
    private static final double MAX_DISTANCE_SQUARED=24.0*24.0;
    private static final String HEART="\u2764";
    private static final String ALL_HEARTS=HEART.repeat(10);
    private PlayerHearts() {}

    public static void render(WorldRenderContext context) {
        MinecraftClient client=MinecraftClient.getInstance();
        MinevoraConfig config=MinevoraPvpHud.CONFIG;
        if(config==null || !config.hudVisible || !config.showPlayerHearts
            || client.player==null || context.world()==null
            || context.matrixStack()==null || context.consumers()==null
            || client.currentScreen instanceof MinevoraScreen
            || client.currentScreen instanceof HudPositionScreen) return;
        Vec3d camera=context.camera().getPos();
        MatrixStack stack=context.matrixStack();
        VertexConsumerProvider consumers=context.consumers();
        TextRenderer font=client.textRenderer;
        int textWidth=font.getWidth(ALL_HEARTS);
        for(PlayerEntity player:context.world().getPlayers()) {
            if(player==client.player || !player.isAlive() || player.isSpectator()
                || player.isInvisible() || player.squaredDistanceTo(client.player)>MAX_DISTANCE_SQUARED)continue;
            // Avoid displaying client-only health values outside the range of a normal health bar.
            float max=Math.max(1f,player.getMaxHealth());
            float health=Math.max(0f,Math.min(max,player.getHealth()));
            int filled=Math.max(0,Math.min(10,(int)Math.ceil(10.0*health/max)));
            stack.push();
            try {
                stack.translate(player.getX()-camera.x,
                    player.getY()-camera.y+player.getHeight()+.62,
                    player.getZ()-camera.z);
                stack.multiply(context.camera().getRotation());
                stack.scale(-.025f,-.025f,.025f);
                float start=-textWidth/2f;
                font.draw(ALL_HEARTS,start,0,0xFF5C5266,false,stack.peek().getPositionMatrix(),
                    consumers,TextRenderer.TextLayerType.NORMAL,0x55060B18,
                    LightmapTextureManager.MAX_LIGHT_COORDINATE);
                if(filled>0) {
                    font.draw(HEART.repeat(filled),start,0,0xFFFF5573,true,
                        stack.peek().getPositionMatrix(),consumers,TextRenderer.TextLayerType.NORMAL,
                        0,LightmapTextureManager.MAX_LIGHT_COORDINATE);
                }
            } finally {stack.pop();}
        }
    }
}
