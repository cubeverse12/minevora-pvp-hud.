package pl.minevora.pvphud;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.Reader;
import java.util.*;

/** Settings shared with the original MINEVORA launcher via config/minevora_pvp.json. */
public final class MinevoraConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String[] LEGACY = { "Armor HUD", "Keystrokes (WASD)", "CPS Counter", "FPS Counter", "Potion HUD", "Crosshair Editor", "Combo / Hit Color" };
    public boolean[] enabled = {true, false, true, true, true, true, false};
    public String style = "Vertical";
    public String damageMode = "Value";
    public String toolLocation = "Right";
    public String alignment = "Right";
    public String listMode = "Normal";
    public int spacing = 2, borderThickness = 1;
    public boolean dynamicColor = true, background = true, border = true;
    public boolean helmet = true, chestplate = true, leggings = true, boots = true, tool = true, offhand = true;
    public boolean showText = true;
    public int textColor = 0xFFFFFFFF, bgColor = 0xDB0A1730, borderColor = 0xFF386CFF;
    public float hudX = .82f, hudY = .31f;
    public boolean previewOn = true;
    public boolean hudVisible = true;
    public float hudScale = .74f;
    public float menuTransparency = .65f; // 65%: world visible behind menu by default
    private JsonObject existing = new JsonObject();
    private Path file;
    private long stamp = -1L;

    public MinevoraConfig() {
        file = MinecraftClient.getInstance().runDirectory.toPath().resolve("config/minevora_pvp.json");
        reloadIfUpdated();
    }

    private static boolean bool(JsonObject o, String key, boolean fallback) {
        try { return o.has(key) ? o.get(key).getAsBoolean() : fallback; } catch (Exception ex) { return fallback; }
    }
    private static String string(JsonObject o, String key, String fallback) {
        try { return o.has(key) ? o.get(key).getAsString() : fallback; } catch (Exception ex) { return fallback; }
    }
    private static int integer(JsonObject o, String key, int fallback) {
        try { return o.has(key) ? o.get(key).getAsInt() : fallback; } catch (Exception ex) { return fallback; }
    }
    private static float decimal(JsonObject o, String key, float fallback) {
        try { return o.has(key) ? o.get(key).getAsFloat() : fallback; } catch (Exception ex) { return fallback; }
    }
    public synchronized void reloadIfUpdated() {
        try {
            if (!Files.isRegularFile(file)) return;
            long modified = Files.getLastModifiedTime(file).toMillis();
            if (stamp == modified) return;
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (!parsed.isJsonObject()) return;
                existing = parsed.getAsJsonObject();
                for (int i = 0; i < LEGACY.length; i++) {
                    if (i == 1) continue; // Ignore saved legacy WASD enable state.
                    if (existing.has(LEGACY[i]) && existing.get(LEGACY[i]).isJsonObject()) {
                        enabled[i] = bool(existing.getAsJsonObject(LEGACY[i]),"enabled",enabled[i]);
                    }
                }
                if (existing.has("_minevora_ui") && existing.get("_minevora_ui").isJsonObject()) {
                    JsonObject o = existing.getAsJsonObject("_minevora_ui");
                    style = string(o,"style",style); damageMode = string(o,"damageMode",damageMode);
                    toolLocation = string(o,"toolLocation",toolLocation); alignment = string(o,"alignment",alignment);
                    listMode = string(o,"listMode",listMode); spacing = integer(o,"spacing",spacing);
                    borderThickness = integer(o,"borderThickness",borderThickness);
                    dynamicColor = bool(o,"dynamicColor",dynamicColor);
                    background = bool(o,"background",background); border = bool(o,"border",border);
                    helmet = bool(o,"helmet",helmet); chestplate = bool(o,"chestplate",chestplate);
                    leggings = bool(o,"leggings",leggings); boots = bool(o,"boots",boots);
                    tool = bool(o,"tool",tool); offhand = bool(o,"offhand",offhand);
                    showText = bool(o,"showText",showText);
                    textColor = integer(o,"textColor",textColor); bgColor = integer(o,"bgColor",bgColor);
                    borderColor = integer(o,"borderColor",borderColor);
                    hudX = decimal(o,"hudX",hudX); hudY = decimal(o,"hudY",hudY);
                    hudVisible=bool(o,"hudVisible",hudVisible);
                    hudScale=Math.max(.5f,Math.min(1f,decimal(o,"hudScale",hudScale)));
                    menuTransparency=Math.max(.25f,Math.min(.85f,decimal(o,"menuTransparency",menuTransparency)));
                }
                enabled[1] = false; // WASD can never be restored from saved settings.
                stamp = modified;
            }
        } catch (Exception ignored) { /* Recover from unfinished edits by launcher. */ }
    }
    public synchronized void save() {
        try {
            Files.createDirectories(file.getParent());
            enabled[1] = false;
            for (int i = 0; i < LEGACY.length; i++) {
                JsonObject o = existing.has(LEGACY[i]) && existing.get(LEGACY[i]).isJsonObject()
                    ? existing.getAsJsonObject(LEGACY[i]) : new JsonObject();
                o.addProperty("enabled",enabled[i]);
                if (!o.has("position")) o.addProperty("position",i==0 ? "Prawy środek" : "Lewy górny");
                if (!o.has("color")) o.addProperty("color","#386cff");
                existing.add(LEGACY[i],o);
            }
            JsonObject o = new JsonObject();
            o.addProperty("style",style); o.addProperty("damageMode",damageMode);
            o.addProperty("toolLocation",toolLocation); o.addProperty("alignment",alignment);
            o.addProperty("listMode",listMode); o.addProperty("spacing",spacing);
            o.addProperty("borderThickness",borderThickness);
            o.addProperty("dynamicColor",dynamicColor); o.addProperty("background",background);
            o.addProperty("border",border); o.addProperty("helmet",helmet); o.addProperty("chestplate",chestplate);
            o.addProperty("leggings",leggings); o.addProperty("boots",boots);
            o.addProperty("tool",tool); o.addProperty("offhand",offhand); o.addProperty("showText",showText);
            o.addProperty("textColor",textColor); o.addProperty("bgColor",bgColor);
            o.addProperty("borderColor",borderColor); o.addProperty("hudX",hudX); o.addProperty("hudY",hudY);
            o.addProperty("hudVisible",hudVisible);o.addProperty("hudScale",hudScale);
            o.addProperty("menuTransparency",menuTransparency);
            existing.add("_minevora_ui",o);
            Path temp = file.resolveSibling("minevora_pvp.json.tmp");
            Files.writeString(temp,GSON.toJson(existing), StandardCharsets.UTF_8);
            try { Files.move(temp,file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temp,file,StandardCopyOption.REPLACE_EXISTING); }
            stamp = Files.getLastModifiedTime(file).toMillis();
        } catch (Exception ignored) { /* Not fatal to gameplay. */ }
    }
    public void reset() {
        enabled = new boolean[]{true,false,true,true,true,true,false};
        style="Vertical"; damageMode="Value"; toolLocation="Right"; alignment="Right";
        listMode="Normal"; spacing=2; borderThickness=1;
        dynamicColor=true; background=true; border=true;
        helmet=true; chestplate=true; leggings=true; boots=true; tool=true; offhand=true; showText=true;
        textColor=0xFFFFFFFF; bgColor=0xDB0A1730; borderColor=0xFF386CFF;
        hudX=.82f;hudY=.31f;hudVisible=true;hudScale=.74f;menuTransparency=.65f;
        save();
    }
}
