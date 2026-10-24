package pl.minevora.pvphud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/** Visual configuration UI inspired by the supplied MINEVORA reference. */
public final class MinevoraScreen extends Screen {
    private static final int W=1100,H=750;
    private static final String[] TABS={"Mod Menu","HUD","Armor","Keystrokes","Crosshair","Chat","Settings"};
    private static final int[] TAB_X={290,394,474,566,681,791,870};
    private static final int[] TAB_W={96,72,84,107,100,71,99};
    private static final Identifier PREVIEW=Identifier.of("minevorapvphud","textures/gui/preview.png");
    private static final int[] COLORS={0xFFFFFFFF,0xFF41CDFF,0xFF386CFF,0xFF7951FA,0xFF55DD79,0xFFFF647B};
    private final MinevoraConfig c = MinevoraPvpHud.CONFIG;
    private int tab=2;
    private boolean minimized=false, draggingHud=false, moveMode=false;
    private String slider="";
    private double originX,originY,scale;
    private int previewX,previewY;

    public MinevoraScreen() {super(Text.literal("MINEVORA PvP Mod"));}
    @Override protected void init() {super.init();}
    @Override public boolean shouldPause() {return false;}

    private void computeBounds() {
        scale=Math.min((width-16)/(double)W,(height-16)/(double)H);
        originX=(width-W*scale)*.5;originY=(height-H*scale)*.5;
    }
    private double sx(double value){return (value-originX)/scale;}
    private double sy(double value){return (value-originY)/scale;}
    @Override public void render(DrawContext d,int mouseX,int mouseY,float delta) {
        computeBounds();
        // Keep the world visibly rendered while the menu is open.
        GuiDraw.rect(d,0,0,width,height,0x0605142A);
        d.getMatrices().push();
        try {
            d.getMatrices().translate(originX,originY,0);
            d.getMatrices().scale((float)scale,(float)scale,1);
            GuiDraw.setMenuOpacity(1f-c.menuTransparency);
            drawWindow(d);
        } finally {
            GuiDraw.setMenuOpacity(1f); // Never tint the game HUD or other screens.
            d.getMatrices().pop();
        }
        // Avoid super.render() to prevent Minecraft background blur above the menu.
    }
    private void drawWindow(DrawContext d) {
        int panelH=minimized?85:718;
        if(tab==2 && !minimized) {
            // Transparent Armor preview reveals the actual Minecraft scene.
            GuiDraw.rounded(d,0,0,W,181,18,0xFF2D5B9B);
            GuiDraw.rounded(d,2,2,W-4,179,17,GuiDraw.PANEL);
            GuiDraw.rect(d,0,181,576,506,GuiDraw.PANEL);
            GuiDraw.rect(d,1078,181,22,506,GuiDraw.PANEL);
            GuiDraw.rounded(d,0,687,W,31,18,0xFF2D5B9B);
            GuiDraw.rounded(d,2,687,W-4,29,16,GuiDraw.PANEL);
        } else {
            GuiDraw.rounded(d,0,0,W,panelH,18,0xFF2D5B9B);
            GuiDraw.rounded(d,2,2,W-4,panelH-4,17,GuiDraw.PANEL);
        }
        // Subtle two-tone header, using Minecraft rectangles (no missing Unicode icons).
        GuiDraw.rect(d,18,76,1064,2,GuiDraw.ACCENT);
        GuiDraw.rect(d,18,79,1064,1,GuiDraw.PURPLE);
        GuiDraw.outlined(d,17,14,40,50,9,0xFF09254B,0xFF337FFF);
        GuiDraw.bigText(d,"M",24,24,3.4f,GuiDraw.CYAN);
        GuiDraw.bigText(d,"MINEVORA",66,28,2.6f,GuiDraw.WHITE);
        GuiDraw.bigText(d,"PvP Mod",208,39,1.15f,GuiDraw.SOFT_BLUE);
        for(int i=0;i<TABS.length;i++) {
            int x=TAB_X[i],w=TAB_W[i];
            boolean active=tab==i;
            if(active)GuiDraw.rounded(d,x-2,13,w+4,59,9,0x513986FF);
            GuiDraw.outlined(d,x,15,w,55,8,active?0xFF164DB4:0xFF102039,
                active?0xFF67B8FF:GuiDraw.STROKE);
            if(active)GuiDraw.rect(d,x+9,17,w-18,2,GuiDraw.PURPLE);
            // Render actual Minecraft items so icons never depend on system fonts.
            d.drawItem(new ItemStack(switch(i){case 0->Items.NETHER_STAR;case 1->Items.CLOCK;case 2->Items.DIAMOND_CHESTPLATE;case 3->Items.REDSTONE;case 4->Items.TARGET;case 5->Items.WRITABLE_BOOK;default->Items.COMPARATOR;}),x+w/2-8,25);
            GuiDraw.centerText(d,TABS[i],x,52,w,active?GuiDraw.WHITE:GuiDraw.MUTED);
        }
        GuiDraw.chip(d,minimized?"+":"-",1004,20,40,false);
        GuiDraw.chip(d,"X",1050,20,35,false);
        if(minimized){GuiDraw.text(d,"Naciśnij +, aby rozwinąć panel.",30,65,GuiDraw.MUTED);return;}
        GuiDraw.rect(d,18,83,W-36,1,0xFF2C518B);
        if(tab==2) {
            // Do not cover the world behind the right-hand live preview.
            GuiDraw.rounded(d,18,86,1064,94,12,0xFF0C1B34);
            GuiDraw.rect(d,18,180,558,510,0xFF0C1B34);
            GuiDraw.rect(d,1078,180,4,510,0xFF0C1B34);
            GuiDraw.rect(d,18,690,1064,7,0xFF0C1B34);
        } else {
            GuiDraw.rounded(d,18,86,1064,611,12,0xFF0C1B34);
        }
        GuiDraw.rect(d,18,86,1064,1,0xFF294F88);
        if(tab==2)drawArmor(d);
        else if(tab==0)drawModules(d);
        else if(tab==1)drawHudTab(d);
        else if(tab==3)drawKeystrokesTab(d);
        else if(tab==4)drawCrosshairTab(d);
        else if(tab==5)drawChatTab(d);
        else drawSettingsTab(d);
        GuiDraw.text(d,"MINEVORA 2.0.13  •  Fabric 1.21.1  •  Neon Blue / zapis automatyczny",28,705,0xFF7D8AAA);
        GuiDraw.text(d,"RIGHT SHIFT — Otwórz / Zamknij",854,705,0xFF7D8AAA);
    }
    private String iconFor(int i){return new String[]{"▦","▣","◆","⌨","⊕","☏","⚙"}[i];}
    private void heading(DrawContext d,String title,String subtitle) {
        GuiDraw.bigText(d,title,88,104,1.95f,GuiDraw.WHITE);
        GuiDraw.text(d,subtitle,88,134,GuiDraw.MUTED);
        GuiDraw.outlined(d,36,105,36,36,8,0xFF102B56,0xFF2F83FF);
        d.drawItem(new ItemStack(Items.DIAMOND_CHESTPLATE),46,114);
    }
    private void drawArmor(DrawContext d) {
        heading(d,"Armor HUD","Customize how your armor, items and durability are displayed in-game.");
        GuiDraw.chip(d,"USTAW HUD",745,107,93,false);
        GuiDraw.chip(d,c.previewOn?"◉  Mod Preview":"⊘  Mod Preview",848,107,129,c.previewOn);
        GuiDraw.chip(d,"↻ Reset Settings",984,107,98,false);
        GuiDraw.text(d,"⌃   Style",36,160,GuiDraw.WHITE);
        GuiDraw.neonFrame(d,31,178,528,405,9);
        rowLabel(d,"Display Style",198);
        chips(d,new String[]{"Vertical","Horizontal","Compact","Modern"},new int[]{241,316,409,490},new int[]{72,89,77,65},198,c.style);
        rowLabel(d,"Text Color",229);
        swatch(d,513,226,c.textColor);
        rowLabel(d,"Dynamic Color",260);
        GuiDraw.toggle(d,513,256,c.dynamicColor);
        rowLabel(d,"Damage Display Type",291);
        chips(d,new String[]{"Value","Percent","Bar"},new int[]{345,417,498},new int[]{68,77,55},289,c.damageMode);
        rowLabel(d,"Tool Location",322);
        chips(d,new String[]{"Left","Right"},new int[]{447,504},new int[]{55,49},320,c.toolLocation);
        rowLabel(d,"Text Alignment",353);
        chips(d,new String[]{"Left","Center","Right"},new int[]{364,426,496},new int[]{58,66,57},351,c.alignment);
        rowLabel(d,"List Mode",384);
        chips(d,new String[]{"Normal","Minimal","Outlined"},new int[]{333,408,482},new int[]{70,70,71},382,c.listMode);
        rowLabel(d,"Spacing",415);
        slider(d,295,416,251,Math.min(1,c.spacing/12f),String.valueOf(c.spacing));
        rowLabel(d,"Background",446);
        GuiDraw.toggle(d,513,442,c.background);
        rowLabel(d,"Background Color",477);
        swatch(d,513,474,c.bgColor);
        rowLabel(d,"Border",508);
        GuiDraw.toggle(d,513,504,c.border);
        rowLabel(d,"Border Color",539);
        swatch(d,513,536,c.borderColor);
        rowLabel(d,"Border Thickness",569);
        slider(d,295,566,251,Math.min(1,c.borderThickness/5f),String.valueOf(c.borderThickness));
        GuiDraw.text(d,"⌄   Show",36,600,GuiDraw.WHITE);
        GuiDraw.neonFrame(d,31,617,528,76,9);
        showItem(d,"Helmet",48,631,219,c.helmet);
        showItem(d,"Chestplate",48,666,219,c.chestplate);
        showItem(d,"Leggings",310,631,504,c.leggings);
        showItem(d,"Boots",310,666,504,c.boots);
        drawPreview(d);
    }
    private void rowLabel(DrawContext d,String label,int y) {GuiDraw.text(d,label,50,y,GuiDraw.MUTED);}
    private void chips(DrawContext d,String[] names,int[] xs,int[] ws,int y,String current) {
        for(int i=0;i<names.length;i++) GuiDraw.chip(d,names[i],xs[i],y-6,ws[i],current.equals(names[i]));
    }
    private void swatch(DrawContext d,int x,int y,int color){
        GuiDraw.outlined(d,x,y,40,22,4,color,0xFF5B8BCC);
    }
    private void slider(DrawContext d,int x,int y,int w,float fraction,String value) {
        GuiDraw.rounded(d,x,y-3,42,24,4,GuiDraw.BUTTON);
        GuiDraw.centerText(d,value,x,y+4,42,GuiDraw.WHITE);
        int trackX=x+55;
        GuiDraw.rounded(d,trackX,y+8,w-55,4,2,0xFF29436B);
        int to=trackX+Math.round(Math.max(0,Math.min(1,fraction))*(w-55));
        GuiDraw.rounded(d,trackX,y+8,to-trackX,4,2,GuiDraw.ACCENT);
        GuiDraw.rounded(d,to-7,y+3,14,14,7,GuiDraw.CYAN);
    }
    private void showItem(DrawContext d,String title,int x,int y,int tx,boolean on) {
        GuiDraw.outlined(d,x,y-7,30,28,5,0xFF102B4B,0xFF275589);
        d.drawItem(new ItemStack(title.equals("Helmet")?Items.DIAMOND_HELMET:title.equals("Chestplate")?Items.DIAMOND_CHESTPLATE:title.equals("Leggings")?Items.DIAMOND_LEGGINGS:Items.DIAMOND_BOOTS),x+7,y-1);
        GuiDraw.text(d,"Show "+title,x+42,y+3,GuiDraw.MUTED);
        GuiDraw.toggle(d,tx,y-3,on);
    }
    private void drawPreview(DrawContext d){
        // Transparent preview frame; Minecraft's real world is visible behind the live armor item preview.
        GuiDraw.rect(d,576,179,504,2,0xFF3A85E8);
        GuiDraw.rect(d,576,179,2,512,0xFF3A85E8);
        GuiDraw.rect(d,1078,179,2,512,0xFF3A85E8);
        GuiDraw.rect(d,576,689,504,2,0xFF3A85E8);
        if(!c.previewOn) {
            GuiDraw.bigText(d,"Podgląd wyłączony",682,400,1.7f,GuiDraw.MUTED);
            GuiDraw.text(d,"Włącz przyciskiem Mod Preview.",702,430,GuiDraw.MUTED);
            return;
        }
        GuiDraw.rect(d,579,182,498,505,0x10081C3A);
        GuiDraw.rounded(d,592,193,215,24,6,0xBC102B55);
        GuiDraw.rect(d,592,219,215,2,GuiDraw.ACCENT);
        GuiDraw.text(d,"PODGLAD HUD NA ZYWO",601,201,0xFFE8EEFF);
        int width=ArmorOverlay.panelWidth(c),height=ArmorOverlay.panelHeight(c,6);
        previewX=579+Math.round(c.hudX*Math.max(1,498-width));
        previewY=182+Math.round(c.hudY*Math.max(1,505-height));
        // In the preview item icons and values update with each setting.
        // In-game Armor HUD should retain its normal opacity even in a transparent menu.
        GuiDraw.setMenuOpacity(1f);
        try {
            ArmorOverlay.renderPreview(d,client,c,previewX,previewY);
        } finally {
            GuiDraw.setMenuOpacity(1f-c.menuTransparency);
        }
    }
    private void genericPanel(DrawContext d,String title,String desc){
        heading(d,title,desc);
        GuiDraw.neonFrame(d,32,178,1038,510,12);
    }
    private void moduleLine(DrawContext d,int n,String name,String sub,int x,int y) {
        GuiDraw.outlined(d,x,y,470,76,8,0xFF10213C,0xFF294F86);
        GuiDraw.rect(d,x+1,y+14,3,48,n==0?GuiDraw.CYAN:GuiDraw.ACCENT);
        d.drawItem(new ItemStack(switch(n){
            case 0->Items.DIAMOND_CHESTPLATE;
            case 2->Items.REDSTONE;
            case 3->Items.CLOCK;
            case 4->Items.POTION;
            case 5->Items.TARGET;
            default->Items.IRON_SWORD;
        }),x+18,y+26);
        GuiDraw.bigText(d,name,x+58,y+15,1.4f,GuiDraw.WHITE);
        GuiDraw.text(d,sub,x+58,y+43,GuiDraw.MUTED);
        GuiDraw.toggle(d,x+412,y+26,c.enabled[n]);
    }
    private void drawModules(DrawContext d) {
        genericPanel(d,"Mod Menu","Włącz i wyłącz moduły PvP HUD jednym kliknięciem.");
        String[] names={"Armor HUD","CPS Counter","FPS Counter","Potion HUD","Crosshair Editor","Combo Counter"};
        String[] desc={"Ikony i trwałość wyposażenia","Kliknięcia LPM / PPM","Aktualna liczba klatek","Efekty mikstur i czas","Dodatkowy celownik","Licznik lokalnych prób ataku"};
        int[] modules={0,2,3,4,5,6};
        for(int i=0;i<modules.length;i++)moduleLine(d,modules[i],names[i],desc[i],i%2==0?54:547,194+(i/2)*112);
        GuiDraw.outlined(d,54,546,963,83,9,c.hudVisible?0xFF142A28:0xFF2A1724,c.hudVisible?0xFF2D8D69:0xFF92445A);
        GuiDraw.bigText(d,"MINEVORA PvP HUD",76,559,1.6f,GuiDraw.WHITE);
        GuiDraw.text(d,c.hudVisible?"STATUS: WLACZONY":"STATUS: WYLACZONY",76,591,c.hudVisible?0xFF65E29E:0xFFFF7289);
        GuiDraw.chip(d,c.hudVisible?"WYLACZ HUD":"WLACZ HUD",829,571,155,c.hudVisible);
        GuiDraw.text(d,"Kliknij panel, aby wlaczyc lub wylaczyc HUD w trakcie gry. Skrot: H.",55,654,GuiDraw.MUTED);
    }
    private void drawHudTab(DrawContext d) {
        genericPanel(d,"HUD Editor","Przesuwaj panel zbroi i zmieniaj jego położenie na ekranie.");
        GuiDraw.text(d,"Położenie poziome",67,241,GuiDraw.WHITE);
        slider(d,248,235,680,c.hudX,Math.round(c.hudX*100)+"%");
        GuiDraw.text(d,"Położenie pionowe",67,312,GuiDraw.WHITE);
        slider(d,248,306,680,c.hudY,Math.round(c.hudY*100)+"%");
        GuiDraw.text(d,"Armor HUD",67,393,GuiDraw.WHITE);
        GuiDraw.toggle(d,260,388,c.enabled[0]);
        GuiDraw.text(d,"Elementy wyposażenia",67,450,GuiDraw.WHITE);
        GuiDraw.chip(d,c.tool?"Miecz  ON":"Miecz OFF",248,439,122,c.tool);
        GuiDraw.chip(d,c.offhand?"Druga ręka  ON":"Druga ręka OFF",384,439,142,c.offhand);
        GuiDraw.text(d,"Wybierz gotowy profil HUD",67,506,GuiDraw.WHITE);
        GuiDraw.chip(d,"PVP",248,516,120,false);
        GuiDraw.chip(d,"MINIMAL",381,516,120,false);
        GuiDraw.chip(d,"KLASYCZNY",514,516,147,false);
        GuiDraw.text(d,"Armor -> USTAW HUD: przeciągnij panel w dowolne miejsce.",67,574,GuiDraw.MUTED);
        GuiDraw.text(d,"Caly PvP HUD podczas gry",67,610,GuiDraw.WHITE);
        GuiDraw.toggle(d,260,603,c.hudVisible);
        GuiDraw.text(d,"Wlaczenie i wylaczenie bez restartu. Skrot: H.",67,649,GuiDraw.MUTED);
    }
    private void drawKeystrokesTab(DrawContext d){
        genericPanel(d,"Keystrokes","Kliknięcia myszy i CPS — bez kafelków WASD.");
        GuiDraw.text(d,"Pokaż licznik CPS",65,230,GuiDraw.WHITE);
        GuiDraw.toggle(d,342,225,c.enabled[2]);
        GuiDraw.neonFrame(d,102,297,390,120,9);
        GuiDraw.neonFrame(d,518,297,390,120,9);
        GuiDraw.bigText(d,"LPM",232,311,1.8f,GuiDraw.CYAN);
        GuiDraw.bigText(d,"PPM",643,311,1.8f,GuiDraw.ACCENT);
        GuiDraw.bigText(d,String.valueOf(MinevoraPvpHud.lClicks.size()),281,351,2.8f,GuiDraw.WHITE);
        GuiDraw.bigText(d,String.valueOf(MinevoraPvpHud.rClicks.size()),688,351,2.8f,GuiDraw.WHITE);
        GuiDraw.text(d,"Kliknięcia z ostatniej sekundy.",102,449,GuiDraw.MUTED);
        GuiDraw.text(d,"WASD usunięte z HUD-u; normalne poruszanie działa bez zmian.",102,474,GuiDraw.MUTED);
    }
    private void drawCrosshairTab(DrawContext d){
        genericPanel(d,"Crosshair Editor","Włącz dodatkowy, neonowo-niebieski celownik PvP.");
        GuiDraw.text(d,"Crosshair",65,241,GuiDraw.WHITE);
        GuiDraw.toggle(d,260,233,c.enabled[5]);
        int x=760,y=346;
        GuiDraw.rect(d,x-1,y-1,3,3,GuiDraw.PURPLE);
        GuiDraw.rect(d,x-22,y,15,2,GuiDraw.ACCENT);
        GuiDraw.rect(d,x+9,y,15,2,GuiDraw.ACCENT);
        GuiDraw.rect(d,x,y-22,2,15,GuiDraw.ACCENT);
        GuiDraw.rect(d,x,y+9,2,15,GuiDraw.ACCENT);
        GuiDraw.text(d,"Celownik Minecraft nadal może być widoczny pod nakładką.",65,438,GuiDraw.MUTED);
    }
    private void drawChatTab(DrawContext d){
        genericPanel(d,"Chat","Szybki dostęp do czatu Minecraft.");
        GuiDraw.text(d,"Mod korzysta z czatu wbudowanego w grę.",65,257,GuiDraw.WHITE);
        GuiDraw.chip(d,"Otwórz czat Minecraft",65,319,215,true);
        GuiDraw.text(d,"Ta wersja nie modyfikuje ani nie odczytuje wiadomości serwera.",65,385,GuiDraw.MUTED);
    }
    private void drawSettingsTab(DrawContext d){
        genericPanel(d,"Settings","Ustawienia i skalowanie HUD podczas gry.");
        GuiDraw.text(d,"Kolor tekstu Armor HUD",65,230,GuiDraw.WHITE);
        for(int i=0;i<6;i++)swatch(d,272+i*49,223,COLORS[i]);
        GuiDraw.text(d,"Kolor obramowania",65,287,GuiDraw.WHITE);
        for(int i=0;i<6;i++)swatch(d,272+i*49,280,COLORS[i]);
        GuiDraw.text(d,"Widocznosc liczb",65,354,GuiDraw.WHITE);
        GuiDraw.toggle(d,280,346,c.showText);
        GuiDraw.text(d,"Pokaz caly PvP HUD",65,404,GuiDraw.WHITE);
        GuiDraw.toggle(d,280,397,c.hudVisible);
        GuiDraw.text(d,"Rozmiar kafelkow i Armor HUD: "+Math.round(c.hudScale*100)+"%",65,457,GuiDraw.WHITE);
        GuiDraw.rect(d,300,488,304,5,GuiDraw.STROKE);
        GuiDraw.rect(d,300,488,(int)((c.hudScale-.5f)*608),5,GuiDraw.ACCENT);
        GuiDraw.rounded(d,300+(int)((c.hudScale-.5f)*608)-6,482,12,17,5,GuiDraw.CYAN);
        GuiDraw.text(d,"50%",260,485,GuiDraw.MUTED);
        GuiDraw.text(d,"100%",618,485,GuiDraw.MUTED);
        GuiDraw.text(d,"Przezroczystosc menu: "+Math.round(c.menuTransparency*100)+"%",65,532,GuiDraw.WHITE);
        GuiDraw.rect(d,300,568,304,5,GuiDraw.STROKE);
        GuiDraw.rect(d,300,568,Math.round((c.menuTransparency-.25f)/.60f*304),5,GuiDraw.ACCENT);
        GuiDraw.rounded(d,300+Math.round((c.menuTransparency-.25f)/.60f*304)-6,562,12,17,5,GuiDraw.CYAN);
        GuiDraw.text(d,"25%",260,565,GuiDraw.MUTED);
        GuiDraw.text(d,"85%",618,565,GuiDraw.MUTED);
        GuiDraw.chip(d,"RESET ALL SETTINGS",65,613,230,false);
        GuiDraw.text(d,"H: pokaz / ukryj HUD",325,623,GuiDraw.MUTED);
        GuiDraw.text(d,"Zapis: config/minevora_pvp.json",65,664,GuiDraw.MUTED);
    }
    private int findChip(double mx,double my,String[] names,int[] xs,int[] ws,int y){
        for(int i=0;i<names.length;i++)if(GuiDraw.inside(mx,my,xs[i],y-6,ws[i],27))return i;
        return -1;
    }
    private boolean toggleClick(double mx,double my,int x,int y) {return GuiDraw.inside(mx,my,x,y,43,23);}
    private int nextColor(int current) {
        for(int i=0;i<COLORS.length;i++)if(COLORS[i]==current)return COLORS[(i+1)%COLORS.length];
        return COLORS[0];
    }
    private void updateHudDrag(double mx,double my) {
        int w=ArmorOverlay.panelWidth(c),h=ArmorOverlay.panelHeight(c,6);
        c.hudX=clamp((float)((mx-579-w/2.0)/Math.max(1,498-w)));
        c.hudY=clamp((float)((my-182-16)/Math.max(1,505-h)));
    }
    private float clamp(float value) {return Math.max(0f,Math.min(1f,value));}
    @Override public boolean mouseClicked(double mouseX,double mouseY,int button) {
        computeBounds(); double x=sx(mouseX),y=sy(mouseY);
        if(button!=GLFW.GLFW_MOUSE_BUTTON_LEFT)return super.mouseClicked(mouseX,mouseY,button);
        if(GuiDraw.inside(x,y,1050,20,35,27)){close();return true;}
        if(GuiDraw.inside(x,y,1004,20,40,27)){minimized=!minimized;return true;}
        if(minimized)return true;
        for(int i=0;i<TABS.length;i++) {
            if(GuiDraw.inside(x,y,TAB_X[i],15,TAB_W[i],55)){tab=i;return true;}
        }
        boolean changed=false;
        switch(tab) {
            case 2 -> changed=clickArmor(x,y);
            case 0 -> {
                if(GuiDraw.inside(x,y,54,546,963,83)){
                    c.hudVisible=!c.hudVisible;changed=true;
                } else {
                    int[] modules={0,2,3,4,5,6};
                    for(int i=0;i<modules.length;i++){
                        int bx=i%2==0?54:547,by=194+(i/2)*112;
                        if(GuiDraw.inside(x,y,bx,by,470,76)){
                            c.enabled[modules[i]]=!c.enabled[modules[i]];changed=true;break;
                        }
                    }
                }
            }
            case 1 -> {
                if(GuiDraw.inside(x,y,248,229,680,31)){c.hudX=clamp((float)((x-303)/625.0));changed=true;}
                if(GuiDraw.inside(x,y,248,302,680,31)){c.hudY=clamp((float)((x-303)/625.0));changed=true;}
                if(toggleClick(x,y,260,388)){c.enabled[0]=!c.enabled[0];changed=true;}
                if(toggleClick(x,y,260,603)){c.hudVisible=!c.hudVisible;changed=true;}
                if(GuiDraw.inside(x,y,248,439,122,27)){c.tool=!c.tool;changed=true;}
                if(GuiDraw.inside(x,y,384,439,142,27)){c.offhand=!c.offhand;changed=true;}
                if(GuiDraw.inside(x,y,248,516,120,27)){
                    c.style="Vertical";c.hudScale=.68f;c.background=true;c.border=true;c.spacing=2;c.showText=true;changed=true;
                }
                if(GuiDraw.inside(x,y,381,516,120,27)){
                    c.style="Compact";c.hudScale=.55f;c.background=false;c.border=false;c.spacing=0;c.showText=false;changed=true;
                }
                if(GuiDraw.inside(x,y,514,516,147,27)){
                    c.style="Vertical";c.hudScale=.74f;c.background=true;c.border=true;c.spacing=2;c.showText=true;changed=true;
                }
            }
            case 3 -> {if(toggleClick(x,y,342,225)){c.enabled[2]=!c.enabled[2];changed=true;}}
            case 4 -> {if(toggleClick(x,y,260,233)){c.enabled[5]=!c.enabled[5];changed=true;}}
            case 5 -> {if(GuiDraw.inside(x,y,65,319,215,27)){client.setScreen(new ChatScreen(""));return true;}}
            case 6 -> changed=clickSettings(x,y);
        }
        if(changed)c.save();
        return true;
    }
    private boolean clickArmor(double x,double y){
        if(GuiDraw.inside(x,y,848,107,129,27)){c.previewOn=!c.previewOn;return true;}
        if(GuiDraw.inside(x,y,984,107,98,27)){c.reset();return true;}
        if(GuiDraw.inside(x,y,745,107,93,27)){client.setScreen(new HudPositionScreen());return true;}
        if(c.previewOn && ((moveMode && GuiDraw.inside(x,y,579,182,498,505)) ||
                GuiDraw.inside(x,y,previewX-5,previewY-5,ArmorOverlay.panelWidth(c)+10,ArmorOverlay.panelHeight(c,6)+10))){
            draggingHud=true;updateHudDrag(x,y);return true;
        }
        int idx;
        String[] styles={"Vertical","Horizontal","Compact","Modern"};
        idx=findChip(x,y,styles,new int[]{241,316,409,490},new int[]{72,89,77,65},198);
        if(idx>=0){c.style=styles[idx];return true;}
        String[] dmg={"Value","Percent","Bar"};
        idx=findChip(x,y,dmg,new int[]{345,417,498},new int[]{68,77,55},289);
        if(idx>=0){c.damageMode=dmg[idx];return true;}
        String[] location={"Left","Right"};
        idx=findChip(x,y,location,new int[]{447,504},new int[]{55,49},320);
        if(idx>=0){c.toolLocation=location[idx];return true;}
        String[] align={"Left","Center","Right"};
        idx=findChip(x,y,align,new int[]{364,426,496},new int[]{58,66,57},351);
        if(idx>=0){c.alignment=align[idx];return true;}
        String[] modes={"Normal","Minimal","Outlined"};
        idx=findChip(x,y,modes,new int[]{333,408,482},new int[]{70,70,71},382);
        if(idx>=0){c.listMode=modes[idx];return true;}
        if(toggleClick(x,y,513,256)){c.dynamicColor=!c.dynamicColor;return true;}
        if(toggleClick(x,y,513,442)){c.background=!c.background;return true;}
        if(toggleClick(x,y,513,504)){c.border=!c.border;return true;}
        if(GuiDraw.inside(x,y,513,226,40,22)){c.textColor=nextColor(c.textColor);return true;}
        if(GuiDraw.inside(x,y,513,474,40,22)){
            c.bgColor=c.bgColor==0xDB0A1020?0xDE25183C:c.bgColor==0xDE25183C?0xE0162634:0xDB0A1020;return true;
        }
        if(GuiDraw.inside(x,y,513,536,40,22)){c.borderColor=nextColor(c.borderColor);return true;}
        if(GuiDraw.inside(x,y,295,409,251,25)){slider="spacing";slide(x);return true;}
        if(GuiDraw.inside(x,y,295,559,251,25)){slider="thickness";slide(x);return true;}
        if(toggleClick(x,y,219,628)){c.helmet=!c.helmet;return true;}
        if(toggleClick(x,y,219,663)){c.chestplate=!c.chestplate;return true;}
        if(toggleClick(x,y,504,628)){c.leggings=!c.leggings;return true;}
        if(toggleClick(x,y,504,663)){c.boots=!c.boots;return true;}
        return false;
    }
    private boolean clickSettings(double x,double y){
        for(int i=0;i<6;i++){
            if(GuiDraw.inside(x,y,272+i*49,223,40,22)){c.textColor=COLORS[i];return true;}
            if(GuiDraw.inside(x,y,272+i*49,280,40,22)){c.borderColor=COLORS[i];return true;}
        }
        if(toggleClick(x,y,280,346)){c.showText=!c.showText;return true;}
        if(toggleClick(x,y,280,397)){c.hudVisible=!c.hudVisible;return true;}
        if(GuiDraw.inside(x,y,285,477,345,30)){c.hudScale=Math.max(.5f,Math.min(1f,.5f+(float)((x-300)/304)*.5f));return true;}
        if(GuiDraw.inside(x,y,285,555,345,32)){
            slider="menuTransparency";
            updateMenuTransparency(x);
            return true;
        }
        if(GuiDraw.inside(x,y,65,613,230,27)){c.reset();return true;}
        return false;
    }
    private void updateMenuTransparency(double x) {
        c.menuTransparency=Math.max(.25f,Math.min(.85f,.25f+((float)(x-300)/304f)*.60f));
        c.save();
    }
    private void slide(double x) {
        float fraction=clamp((float)((x-350)/196.0));
        if(slider.equals("spacing"))c.spacing=Math.round(fraction*12);
        if(slider.equals("thickness"))c.borderThickness=Math.round(fraction*5);
        c.save();
    }
    @Override public boolean mouseDragged(double mouseX,double mouseY,int button,double deltaX,double deltaY) {
        double x=sx(mouseX),y=sy(mouseY);
        if(draggingHud){updateHudDrag(x,y);return true;}
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT && tab==6 && slider.equals("menuTransparency")){
            updateMenuTransparency(x);
            return true;
        }
        if(tab==6&&button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&GuiDraw.inside(x,y,285,475,345,32)){
            c.hudScale=Math.max(.5f,Math.min(1f,.5f+(float)((x-300)/304)*.5f));c.save();return true;
        }
        if(!slider.isEmpty()){slide(x);return true;}
        return super.mouseDragged(mouseX,mouseY,button,deltaX,deltaY);
    }
    @Override public boolean mouseReleased(double mouseX,double mouseY,int button) {
        if(draggingHud || !slider.isEmpty()){
            draggingHud=false;slider="";c.save();return true;
        }
        return super.mouseReleased(mouseX,mouseY,button);
    }
    @Override public void close(){c.save();super.close();}
}
