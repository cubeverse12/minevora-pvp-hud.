package pl.minevora.pvphud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Pixel-friendly dark UI primitives, no third-party UI library required. */
public final class GuiDraw {
    public static final int PANEL=0xF20C101A, BUTTON=0xFF171D2A, STROKE=0xFF283044;
    public static final int WHITE=0xFFF2F4FF, MUTED=0xFFA8B7D6, ACCENT=0xFF743BFA, CYAN=0xFF32E6EE;
    private GuiDraw() {}

    public static void rect(DrawContext c,int x,int y,int w,int h,int color){
        if(w>0 && h>0)c.fill(x,y,x+w,y+h,color);
    }
    public static void rounded(DrawContext c,int x,int y,int w,int h,int r,int color) {
        if (w<=0 || h<=0) return;
        r=Math.min(Math.min(r,w/2),h/2);
        rect(c,x+r,y,w-2*r,h,color);
        rect(c,x,y+r,r,h-2*r,color);
        rect(c,x+w-r,y+r,r,h-2*r,color);
        for(int i=0;i<r;i++) {
            int a=(int)Math.ceil(r-Math.sqrt(r*r-(r-i-.5)*(r-i-.5)));
            rect(c,x+a,y+i,w-a*2,1,color);
            rect(c,x+a,y+h-i-1,w-a*2,1,color);
        }
    }
    public static void outlined(DrawContext c,int x,int y,int w,int h,int r,int fill,int border) {
        rounded(c,x,y,w,h,r,border);
        rounded(c,x+1,y+1,w-2,h-2,Math.max(0,r-1),fill);
    }
    public static void text(DrawContext c,String value,int x,int y,int color) {
        c.drawTextWithShadow(MinecraftClient.getInstance().textRenderer,value,x,y,color);
    }
    public static void bigText(DrawContext c,String text,int x,int y,float scale,int color) {
        c.getMatrices().push();
        c.getMatrices().translate(x,y,0);
        c.getMatrices().scale(scale,scale,1);
        text(c,text,0,0,color);
        c.getMatrices().pop();
    }
    public static void centerText(DrawContext c,String text,int x,int y,int width,int color) {
        int textW=MinecraftClient.getInstance().textRenderer.getWidth(text);
        text(c,text,x+(width-textW)/2,y,color);
    }
    public static void line(DrawContext c,int x1,int y1,int x2,int y2,int color) {
        rect(c,x1,y1,x2-x1+1,1,color); rect(c,x1,y1,1,y2-y1+1,color);
    }
    public static boolean inside(double mouseX,double mouseY,int x,int y,int w,int h) {
        return mouseX>=x && mouseX<x+w && mouseY>=y && mouseY<y+h;
    }
    public static void chip(DrawContext c,String label,int x,int y,int w,boolean selected) {
        outlined(c,x,y,w,27,5,selected?ACCENT:BUTTON,selected?0xFF9B6BFF:STROKE);
        centerText(c,label,x,y+9,w,selected?WHITE:MUTED);
    }
    public static void toggle(DrawContext c,int x,int y,boolean on) {
        rounded(c,x,y,39,19,9,on?ACCENT:0xFF343A4B);
        rounded(c,x+(on?23:3),y+3,13,13,6,0xFFF9F9FF);
    }
}
