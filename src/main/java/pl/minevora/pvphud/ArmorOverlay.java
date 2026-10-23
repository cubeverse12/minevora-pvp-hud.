package pl.minevora.pvphud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import java.util.ArrayList;
import java.util.List;

/** Draw actual armor/tool icons and durability in the chosen layout. */
public final class ArmorOverlay {
    private ArmorOverlay() {}
    private record ArmorSlot(ItemStack item, int value, int max) { }

    private static List<ArmorSlot> items(MinecraftClient client,MinevoraConfig cfg,boolean preview) {
        List<ArmorSlot> rows = new ArrayList<>();
        if (preview) {
            if(cfg.helmet)rows.add(new ArmorSlot(new ItemStack(Items.DIAMOND_HELMET),363,363));
            if(cfg.chestplate)rows.add(new ArmorSlot(new ItemStack(Items.DIAMOND_CHESTPLATE),528,528));
            if(cfg.leggings)rows.add(new ArmorSlot(new ItemStack(Items.DIAMOND_LEGGINGS),495,495));
            if(cfg.boots)rows.add(new ArmorSlot(new ItemStack(Items.DIAMOND_BOOTS),429,429));
            if(cfg.tool)rows.add(new ArmorSlot(new ItemStack(Items.DIAMOND_SWORD),1561,1561));
            if(cfg.offhand)rows.add(new ArmorSlot(new ItemStack(Items.TOTEM_OF_UNDYING),336,400));
            return rows;
        }
        if(client.player==null)return rows;
        if(cfg.helmet)add(rows,client.player.getInventory().getArmorStack(3));
        if(cfg.chestplate)add(rows,client.player.getInventory().getArmorStack(2));
        if(cfg.leggings)add(rows,client.player.getInventory().getArmorStack(1));
        if(cfg.boots)add(rows,client.player.getInventory().getArmorStack(0));
        if(cfg.tool)add(rows,client.player.getMainHandStack());
        if(cfg.offhand)add(rows,client.player.getOffHandStack());
        return rows;
    }
    private static void add(List<ArmorSlot> rows, ItemStack item) {
        if(item==null || item.isEmpty())return;
        int max=item.isDamageable()?item.getMaxDamage():1;
        int remaining=item.isDamageable()?Math.max(0,max-item.getDamage()):1;
        rows.add(new ArmorSlot(item,remaining,max));
    }
    private static int color(ArmorSlot row, MinevoraConfig cfg) {
        float fraction=row.max<=0?1f:Math.max(0f,Math.min(1f,row.value/(float)row.max));
        if(!cfg.dynamicColor)return 0xFF5FEA65;
        if(fraction>.55f)return 0xFF54E573;
        if(fraction>.24f)return 0xFFF4CE3F;
        return 0xFFF35A69;
    }
    public static int panelWidth(MinevoraConfig cfg) { return cfg.style.equals("Compact")?70:cfg.style.equals("Horizontal")?145:117; }
    public static int panelHeight(MinevoraConfig cfg, int count) {
        if(cfg.style.equals("Horizontal"))return 38;
        int row=cfg.style.equals("Compact")?23:27;
        return Math.max(10,8+count*(row+cfg.spacing)+2);
    }
    public static void renderGame(DrawContext ctx,MinecraftClient client,MinevoraConfig cfg) {
        if(!cfg.enabled[0])return;
        List<ArmorSlot> items=items(client,cfg,false);
        if(items.isEmpty())return;
        int width=panelWidth(cfg),height=panelHeight(cfg,items.size());
        int sw=client.getWindow().getScaledWidth(),sh=client.getWindow().getScaledHeight();
        int x=Math.round(cfg.hudX*Math.max(0,sw-width));
        int y=Math.round(cfg.hudY*Math.max(0,sh-height));
        HudScaleTransform.beginArmor(ctx,cfg,sw,sh,x,y,width,height);
        try{render(ctx,client,cfg,items,x,y);}
        finally{HudScaleTransform.end(ctx);}
    }
    public static void renderPreview(DrawContext ctx,MinecraftClient client,MinevoraConfig cfg,int x,int y) {
        if(!cfg.enabled[0])return;
        render(ctx,client,cfg,items(client,cfg,true),x,y);
    }
    private static void render(DrawContext ctx,MinecraftClient client,MinevoraConfig cfg,List<ArmorSlot> rows,int x,int y) {
        if(rows.isEmpty())return;
        int width=panelWidth(cfg),height=panelHeight(cfg,rows.size());
        int bw=Math.max(0,Math.min(5,cfg.borderThickness));
        if(cfg.border && bw>0)GuiDraw.rounded(ctx,x-bw,y-bw,width+2*bw,height+2*bw,7,cfg.borderColor);
        if(cfg.background && !cfg.listMode.equals("Minimal"))GuiDraw.rounded(ctx,x,y,width,height,6,cfg.bgColor);
        boolean horizontal=cfg.style.equals("Horizontal");
        boolean compact=cfg.style.equals("Compact");
        int rowHeight=compact?23:27;
        for(int i=0;i<rows.size();i++) {
            ArmorSlot slot=rows.get(i);
            int rx=horizontal?x+5+i*23:x+3;
            int ry=horizontal?y+7:y+4+i*(rowHeight+cfg.spacing);
            if(horizontal && rx+16>x+width-4)break;
            if(!horizontal && (cfg.style.equals("Modern") || cfg.listMode.equals("Outlined"))) {
                GuiDraw.outlined(ctx,x+2,ry-2,width-4,rowHeight+1,4,0x60202A44,
                    cfg.listMode.equals("Outlined") ? cfg.borderColor : 0xFF353450);
            }
            int iconX=(cfg.toolLocation.equals("Left")&&!horizontal)?x+width-20:rx+1;
            ctx.drawItem(slot.item,iconX,ry);
            if(cfg.showText&&!horizontal&&!compact) {
                String value=cfg.damageMode.equals("Percent")?
                    (slot.max>0?Math.round(slot.value*100f/slot.max):100)+"%" :
                    cfg.damageMode.equals("Bar")?"":String.valueOf(slot.value);
                int tw=client.textRenderer.getWidth(value);
                int tx=cfg.toolLocation.equals("Left")?x+7:rx+22;
                if(cfg.alignment.equals("Right") && !cfg.toolLocation.equals("Left"))tx=x+width-tw-7;
                if(cfg.alignment.equals("Center"))tx=x+(width-tw)/2;
                GuiDraw.text(ctx,value,tx,ry+1,cfg.textColor);
            }
            if (!compact && !horizontal) {
                int bx=cfg.toolLocation.equals("Left")?x+5:rx+22;
                int by=ry+18;
                int barWidth=cfg.toolLocation.equals("Left")?width-30:width-29;
                GuiDraw.rect(ctx,bx,by,barWidth,4,0xFF293444);
                int fill=Math.max(0,Math.min(barWidth,Math.round(barWidth*slot.value/(float)Math.max(1,slot.max))));
                GuiDraw.rect(ctx,bx,by,fill,4,color(slot,cfg));
            }
            if(compact) {
                int tx=cfg.toolLocation.equals("Left")?x+5:rx+22;
                String value=cfg.damageMode.equals("Percent")?
                    (slot.max>0?Math.round(slot.value*100f/slot.max):100)+"%":String.valueOf(slot.value);
                if(cfg.damageMode.equals("Bar")) {
                    GuiDraw.rect(ctx,tx,ry+10,34,5,0xFF354150);
                    GuiDraw.rect(ctx,tx,ry+10,Math.round(34f*slot.value/Math.max(1,slot.max)),5,color(slot,cfg));
                } else if(cfg.showText) GuiDraw.text(ctx,value,tx,ry+5,cfg.textColor);
            }
        }
    }
}
