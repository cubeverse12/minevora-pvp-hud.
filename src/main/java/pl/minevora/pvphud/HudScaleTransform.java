package pl.minevora.pvphud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
public final class HudScaleTransform{
    private HudScaleTransform(){}
    public static void beginArmor(DrawContext ctx,MinevoraConfig cfg,int sw,int sh,int x,int y,int w,int h){
        float s=cfg.hudScale*(.68f/.74f);
        float px=cfg.hudX*Math.max(0f,sw-w*s),py=cfg.hudY*Math.max(0f,sh-h*s);
        MatrixStack m=ctx.getMatrices();m.push();m.translate(px-x*s,py-y*s,0);m.scale(s,s,1f);
    }
    public static void beginTile(DrawContext ctx,MinevoraConfig cfg,int x,int y){
        MatrixStack m=ctx.getMatrices();m.push();m.translate(x,y,0);m.scale(cfg.hudScale,cfg.hudScale,1f);m.translate(-x,-y,0);
    }
    public static void end(DrawContext ctx){ctx.getMatrices().pop();}
}
