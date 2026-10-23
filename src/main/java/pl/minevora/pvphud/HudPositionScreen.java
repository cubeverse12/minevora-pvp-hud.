package pl.minevora.pvphud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
public final class HudPositionScreen extends Screen{
    private final MinevoraConfig cfg=MinevoraPvpHud.CONFIG;
    private boolean moving;private double offsetX,offsetY;
    public HudPositionScreen(){super(Text.literal("MINEVORA: pozycja Armor HUD"));}
    @Override public boolean shouldPause(){return false;}
    private int ax(){return Math.max(1,width-ArmorOverlay.panelWidth(cfg)-12);}
    private int ay(){return Math.max(1,height-ArmorOverlay.panelHeight(cfg,6)-12);}
    private int x(){return Math.round(cfg.hudX*ax());}
    private int y(){return Math.round(cfg.hudY*ay());}
    private float cap(float f){return Math.min(1f,Math.max(0f,f));}
    private void set(double px,double py){cfg.hudX=cap((float)(px/ax()));cfg.hudY=cap((float)(py/ay()));}
    @Override public void render(DrawContext d,int mx,int my,float delta){
        GuiDraw.rect(d,0,0,width,height,0xDF080E19);
        for(int xx=0;xx<width;xx+=40)GuiDraw.rect(d,xx,0,1,height,0x233F4872);
        for(int yy=0;yy<height;yy+=40)GuiDraw.rect(d,0,yy,width,1,0x233F4872);
        GuiDraw.text(d,"MINEVORA | PRZECIAGNIJ ARMOR HUD",12,14,0xFFFFFFFF);
        GuiDraw.text(d,"Strzalki: przesuwanie 1 px | 1-4: rogi",12,30,0xFFAC98FC);
        GuiDraw.chip(d,"Gotowe",Math.max(12,width-92),12,80,true);
        GuiDraw.chip(d,"Reset",Math.max(12,width-92),45,80,false);
        GuiDraw.outlined(d,x()-4,y()-4,ArmorOverlay.panelWidth(cfg)+8,ArmorOverlay.panelHeight(cfg,6)+8,5,0x332F1754,0xFFA778FF);
        ArmorOverlay.renderPreview(d,MinecraftClient.getInstance(),cfg,x(),y());
    }
    @Override public boolean mouseClicked(double mx,double my,int button){
        if(button!=0)return false;
        if(GuiDraw.inside(mx,my,Math.max(12,width-92),12,80,27)){close();return true;}
        if(GuiDraw.inside(mx,my,Math.max(12,width-92),45,80,27)){cfg.hudX=.82f;cfg.hudY=.31f;cfg.save();return true;}
        if(GuiDraw.inside(mx,my,x()-6,y()-6,ArmorOverlay.panelWidth(cfg)+12,ArmorOverlay.panelHeight(cfg,6)+12)){
            moving=true;offsetX=mx-x();offsetY=my-y();
        }return true;
    }
    @Override public boolean mouseDragged(double mx,double my,int b,double dx,double dy){
        if(b==0&&moving){set(mx-offsetX,my-offsetY);return true;}return false;
    }
    @Override public boolean mouseReleased(double mx,double my,int b){
        if(b==0&&moving){set(mx-offsetX,my-offsetY);moving=false;cfg.save();return true;}return false;
    }
    @Override public boolean keyPressed(int key,int scan,int mods){
        switch(key){
            case GLFW.GLFW_KEY_LEFT -> cfg.hudX=cap(cfg.hudX-1f/ax());
            case GLFW.GLFW_KEY_RIGHT -> cfg.hudX=cap(cfg.hudX+1f/ax());
            case GLFW.GLFW_KEY_UP -> cfg.hudY=cap(cfg.hudY-1f/ay());
            case GLFW.GLFW_KEY_DOWN -> cfg.hudY=cap(cfg.hudY+1f/ay());
            case GLFW.GLFW_KEY_1 -> {cfg.hudX=0;cfg.hudY=0;}
            case GLFW.GLFW_KEY_2 -> {cfg.hudX=1;cfg.hudY=0;}
            case GLFW.GLFW_KEY_3 -> {cfg.hudX=0;cfg.hudY=1;}
            case GLFW.GLFW_KEY_4 -> {cfg.hudX=1;cfg.hudY=1;}
            case GLFW.GLFW_KEY_ESCAPE -> {close();return true;}
            default -> {return super.keyPressed(key,scan,mods);}
        }cfg.save();return true;
    }
    @Override public void close(){cfg.save();MinecraftClient.getInstance().setScreen(new MinevoraScreen());}
}
