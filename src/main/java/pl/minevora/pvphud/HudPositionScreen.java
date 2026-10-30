package pl.minevora.pvphud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Mouse editor for each on-screen HUD tile; saved in config/minevora_pvp.json. */
public final class HudPositionScreen extends Screen {
    private static final String[] TILES={"armor","cps","fps","potions","combo"};
    private final MinevoraConfig cfg=MinevoraPvpHud.CONFIG;
    private String selected="armor", dragging=null;
    private double offsetX,offsetY;

    public HudPositionScreen(){super(Text.literal("MINEVORA: uklad PvP HUD"));}
    @Override public boolean shouldPause(){return false;}
    private float scale(String id) {return id.equals("armor")?cfg.hudScale*(.68f/.74f):cfg.hudScale;}
    private int panelWidth(String id) {
        int unscaled=id.equals("armor")?ArmorOverlay.panelWidth(cfg):
            OtherHud.previewWidth(MinecraftClient.getInstance(),id);
        return Math.max(1,(int)Math.ceil(unscaled*scale(id)));
    }
    private int panelHeight(String id){
        return Math.max(1,(int)Math.ceil((id.equals("armor")?
            ArmorOverlay.panelHeight(cfg,6):16)*scale(id)));
    }
    private int x(String id) {
        float proportion=id.equals("armor")?cfg.hudX:cfg.tileX(id);
        return Math.round(proportion*Math.max(0,width-panelWidth(id)));
    }
    private int y(String id) {
        float proportion=id.equals("armor")?cfg.hudY:cfg.tileY(id);
        return Math.round(proportion*Math.max(0,height-panelHeight(id)));
    }
    private static float clamp(double value){
        return (float)Math.max(0,Math.min(1,value));
    }
    private void set(String id,double px,double py) {
        float nx=clamp(px/Math.max(1,width-panelWidth(id)));
        float ny=clamp(py/Math.max(1,height-panelHeight(id)));
        if(id.equals("armor")) {cfg.hudX=nx;cfg.hudY=ny;}
        else cfg.setTilePosition(id,nx,ny);
    }
    private void reset() {
        cfg.hudX=.82f;cfg.hudY=.31f;
        cfg.resetTilePositions();cfg.save();
    }

    @Override public void render(DrawContext d,int mx,int my,float delta){
        GuiDraw.rect(d,0,0,width,height,0xD9080E19);
        for(int xx=0;xx<width;xx+=40)GuiDraw.rect(d,xx,0,1,height,0x233F4872);
        for(int yy=0;yy<height;yy+=40)GuiDraw.rect(d,0,yy,width,1,0x233F4872);
        GuiDraw.text(d,"MINEVORA  |  PRZECIAGNIJ KAFELKI HUD",12,12,GuiDraw.WHITE);
        GuiDraw.text(d,"Kliknij kafelek i przeciagnij. Strzalki: 1 px. Zmiany zapisywane automatycznie.",12,27,GuiDraw.MUTED);
        int buttonsX=Math.max(12,width-92);
        GuiDraw.chip(d,"Gotowe",buttonsX,12,80,true);
        GuiDraw.chip(d,"Reset",buttonsX,45,80,false);

        for(String id:TILES) {
            int px=x(id),py=y(id),w=panelWidth(id),h=panelHeight(id);
            int color=id.equals(selected)?GuiDraw.CYAN:GuiDraw.ACCENT;
            GuiDraw.outlined(d,px-3,py-3,w+6,h+6,4,0x442F1754,color);
            if(id.equals("armor")) {
                float s=scale(id);
                d.getMatrices().push();
                try {
                    d.getMatrices().translate(px,py,0);
                    d.getMatrices().scale(s,s,1f);
                    ArmorOverlay.renderPreview(d,MinecraftClient.getInstance(),cfg,0,0);
                }finally{d.getMatrices().pop();}
            }else {
                OtherHud.renderPreview(d,cfg,id,px,py);
            }
            if(!id.equals("armor") || cfg.enabled[0]) {
                String title=id.equals("armor")?"ARMOR":id.toUpperCase();
                GuiDraw.text(d,title,px,Math.max(45,py-15),color);
            }
        }
    }
    @Override public boolean mouseClicked(double mx,double my,int button){
        if(button!=GLFW.GLFW_MOUSE_BUTTON_LEFT)return super.mouseClicked(mx,my,button);
        int bx=Math.max(12,width-92);
        if(GuiDraw.inside(mx,my,bx,12,80,27)){close();return true;}
        if(GuiDraw.inside(mx,my,bx,45,80,27)){reset();return true;}
        for(int i=TILES.length-1;i>=0;i--) {
            String id=TILES[i];
            if(GuiDraw.inside(mx,my,x(id)-5,y(id)-5,panelWidth(id)+10,panelHeight(id)+10)) {
                selected=id;dragging=id;offsetX=mx-x(id);offsetY=my-y(id);
                return true;
            }
        }
        return true;
    }
    @Override public boolean mouseDragged(double mx,double my,int button,double dx,double dy){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT && dragging!=null) {
            set(dragging,mx-offsetX,my-offsetY);return true;
        }
        return super.mouseDragged(mx,my,button,dx,dy);
    }
    @Override public boolean mouseReleased(double mx,double my,int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT && dragging!=null) {
            set(dragging,mx-offsetX,my-offsetY);dragging=null;cfg.save();return true;
        }
        return super.mouseReleased(mx,my,button);
    }
    @Override public boolean keyPressed(int key,int scan,int mods) {
        if(key==GLFW.GLFW_KEY_ESCAPE){close();return true;}
        int nx=x(selected),ny=y(selected);
        switch(key) {
            case GLFW.GLFW_KEY_LEFT -> nx--;
            case GLFW.GLFW_KEY_RIGHT -> nx++;
            case GLFW.GLFW_KEY_UP -> ny--;
            case GLFW.GLFW_KEY_DOWN -> ny++;
            default -> {return super.keyPressed(key,scan,mods);}
        }
        set(selected,nx,ny);cfg.save();return true;
    }
    @Override public void close(){cfg.save();MinecraftClient.getInstance().setScreen(new MinevoraScreen());}
}
