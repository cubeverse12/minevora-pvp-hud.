package pl.minevora.pvphud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.ArrayList;
import java.util.List;

/** CPS, FPS, potions and combo are independent, draggable HUD tiles. */
public final class OtherHud {
    private OtherHud() {}

    public static String previewText(String id) {
        return switch (id) {
            case "cps" -> "CPS  8 / 5";
            case "fps" -> "FPS  144";
            case "potions" -> "Speed II  0:45";
            case "combo" -> "COMBO  3";
            default -> "";
        };
    }
    private static int textWidth(MinecraftClient client, String value) {
        return client.textRenderer.getWidth(value) + 14;
    }
    public static int previewWidth(MinecraftClient client, String id) {
        return textWidth(client, previewText(id));
    }
    public static int pixelX(MinecraftClient client,MinevoraConfig c,String id,int width) {
        int sw=client.getWindow().getScaledWidth();
        return Math.round(c.tileX(id)*Math.max(0,sw-width*c.hudScale));
    }
    public static int pixelY(MinecraftClient client,MinevoraConfig c,String id,int height) {
        int sh=client.getWindow().getScaledHeight();
        return Math.round(c.tileY(id)*Math.max(0,sh-height*c.hudScale));
    }
    public static void renderPreview(DrawContext ctx,MinevoraConfig cfg,String id,int x,int y) {
        panel(ctx,cfg,previewText(id),x,y);
    }
    public static void render(DrawContext ctx,MinecraftClient client,MinevoraConfig c) {
        int sw=client.getWindow().getScaledWidth(),sh=client.getWindow().getScaledHeight();
        if(c.enabled[2]) {
            String text="CPS  "+MinevoraPvpHud.lClicks.size()+" / "+MinevoraPvpHud.rClicks.size();
            panel(ctx,c,text,pixelX(client,c,"cps",textWidth(client,text)),
                pixelY(client,c,"cps",16));
        }
        if(c.enabled[3]) {
            String text="FPS  "+client.getCurrentFps();
            panel(ctx,c,text,pixelX(client,c,"fps",textWidth(client,text)),
                pixelY(client,c,"fps",16));
        }
        if(c.enabled[4]) {
            List<String> effects=new ArrayList<>();
            for(StatusEffectInstance e:client.player.getStatusEffects()) {
                int sec=Math.max(0,e.getDuration()/20);
                effects.add(e.getEffectType().value().getName().getString()+" "+(sec/60)+":"+String.format("%02d",sec%60));
                if(effects.size()>=5)break;
            }
            if(!effects.isEmpty()) {
                int maxWidth=0;
                for(String t:effects)maxWidth=Math.max(maxWidth,textWidth(client,t));
                int totalHeight=effects.size()*19-3;
                int x=pixelX(client,c,"potions",maxWidth),y=pixelY(client,c,"potions",totalHeight);
                for(String effect:effects){ panel(ctx,c,effect,x,y);y+=19; }
            }
        }
        if(c.enabled[6] && MinevoraPvpHud.combo>0) {
            String text="COMBO "+MinevoraPvpHud.combo;
            panel(ctx,c,text,pixelX(client,c,"combo",textWidth(client,text)),
                pixelY(client,c,"combo",16));
        }
        if(c.enabled[5]) {
            int x=sw/2,y=sh/2,a=0xFFAC6DFF;
            // Always centered: the crosshair must coincide with the player's aim.
            GuiDraw.rect(ctx,x-1,y-1,3,3,a);
            GuiDraw.rect(ctx,x-9,y,6,1,a);GuiDraw.rect(ctx,x+4,y,6,1,a);
            GuiDraw.rect(ctx,x,y-9,1,6,a);GuiDraw.rect(ctx,x,y+4,1,6,a);
        }
    }
    private static void panel(DrawContext ctx,MinevoraConfig cfg,String text,int x,int y){
        HudScaleTransform.beginTile(ctx,cfg,x,y);
        try {
            int w=MinecraftClient.getInstance().textRenderer.getWidth(text)+14;
            GuiDraw.rounded(ctx,x,y,w,16,4,0xDD0C1220);
            GuiDraw.rect(ctx,x,y,2,16,GuiDraw.ACCENT);
            GuiDraw.text(ctx,text,x+7,y+4,GuiDraw.WHITE);
        }finally{HudScaleTransform.end(ctx);}
    }
}
