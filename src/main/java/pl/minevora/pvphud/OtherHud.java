package pl.minevora.pvphud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.ArrayList;
import java.util.List;

/** Additional original HUD modules preserved from MINEVORA v1. */
public final class OtherHud {
    private OtherHud() {}
    public static void render(DrawContext ctx,MinecraftClient client,MinevoraConfig c) {
        int sw=client.getWindow().getScaledWidth(),sh=client.getWindow().getScaledHeight();
        int leftY=12,rightY=12;
        if(c.enabled[1]) { // WASD
            String[] keys={" W "," A "," S "," D "};
            boolean[] pressed={client.options.forwardKey.isPressed(),client.options.leftKey.isPressed(),
                client.options.backKey.isPressed(),client.options.rightKey.isPressed()};
            int x=sw-112,y=rightY;
            drawKey(ctx,c,keys[0],x+34,y,pressed[0]);
            for(int i=1;i<4;i++)drawKey(ctx,c,keys[i],x+(i-1)*34,y+25,pressed[i]);
            rightY+=54;
        }
        if(c.enabled[2]) {
            leftY=panel(ctx,c,"CPS  "+MinevoraPvpHud.lClicks.size()+" / "+MinevoraPvpHud.rClicks.size(),12,leftY)+5;
        }
        if(c.enabled[3]) {
            leftY=panel(ctx,c,"FPS  "+client.getCurrentFps(),12,leftY)+5;
        }
        if(c.enabled[4]) {
            List<String> effects=new ArrayList<>();
            for(StatusEffectInstance e:client.player.getStatusEffects()) {
                int sec=Math.max(0,e.getDuration()/20);
                effects.add(e.getEffectType().value().getName().getString()+" "+(sec/60)+":"+String.format("%02d",sec%60));
                if(effects.size()>=5)break;
            }
            if(!effects.isEmpty()) {
                int y=sh-16-effects.size()*16;
                for(String effect:effects)y=panel(ctx,c,effect,12,y)+3;
            }
        }
        if(c.enabled[6] && MinevoraPvpHud.combo>0) {
            int x=sw/2-42;
            panel(ctx,c,"COMBO "+MinevoraPvpHud.combo,x,30);
        }
        if(c.enabled[5]) {
            int x=sw/2,y=sh/2,a=0xFFAC6DFF;
            // Decorative overlay in addition to Minecraft's default crosshair.
            GuiDraw.rect(ctx,x-1,y-1,3,3,a);
            GuiDraw.rect(ctx,x-9,y,6,1,a);GuiDraw.rect(ctx,x+4,y,6,1,a);
            GuiDraw.rect(ctx,x,y-9,1,6,a);GuiDraw.rect(ctx,x,y+4,1,6,a);
        }
    }
    private static int panel(DrawContext ctx,MinevoraConfig cfg,String text,int x,int y){
        HudScaleTransform.beginTile(ctx,cfg,x,y);
        try{
            int w=MinecraftClient.getInstance().textRenderer.getWidth(text)+14;
            GuiDraw.rounded(ctx,x,y,w,16,4,0xDD0C1220);
            GuiDraw.rect(ctx,x,y,2,16,GuiDraw.ACCENT);
            GuiDraw.text(ctx,text,x+7,y+4,GuiDraw.WHITE);
            return y+16;
        }finally{HudScaleTransform.end(ctx);}
    }
    private static void drawKey(DrawContext ctx,MinevoraConfig cfg,String value,int x,int y,boolean pressed){
        HudScaleTransform.beginTile(ctx,cfg,x,y);
        try{
            GuiDraw.outlined(ctx,x,y,29,22,3,pressed?GuiDraw.ACCENT:GuiDraw.BUTTON,GuiDraw.STROKE);
            GuiDraw.centerText(ctx,value,x,y+7,29,GuiDraw.WHITE);
        }finally{HudScaleTransform.end(ctx);}
    }
}
