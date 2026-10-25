# MINEVORA PvP HUD 2.0.14 — Neon Blue Lite

## Zmiany (24.10.2026)
- Usunięto generator / szybkie presety układu HUD: PVP, MINIMAL, KLASYCZNY.
- Uproszczono główne menu moda do pięciu kart: **Mod Menu, HUD, Armor, Crosshair, Settings**.
- CPS i FPS pozostają dostępne w Mod Menu bez osobnych niepotrzebnych zakładek. Usunięto osobne zakładki Chat i Keystrokes, bez zmian sterowania w grze.
- Zachowano niebiesko-fioletowy styl kompatybilny z MINEVORA Launcher 19.
- Zachowano **półprzezroczyste panele i regulację 25–85%**, realne ikony Minecraft, podgląd zbroi, możliwość przesuwania elementów oraz skrót H do ukrywania HUD-u.
- Zachowano istniejące ustawienia; nie przenosimy starej konfiguracji bez pytania.

## Instalacja
Zamknij Minecraft, uruchom Fabric 1.21.1 w MINEVORA Launcher, kliknij pobieranie PvP HUD z GitHuba. Mod Fabric wymaga Fabric API.
Prawy Shift / P otwiera menu; H włącza i wyłącza HUD w trakcie gry. Mod nie jest portem Forge/NeoForge.

## Nowość 2.0.15 — HP, głód i licznik jedzenia

W grze pojawiają się opcjonalne, niezależne kafelki z **prawdziwymi danymi lokalnego gracza**:

- **HP** — aktualne oraz maksymalne zdrowie własnej postaci.
- **GŁÓD** — poziom najedzenia w skali 0–20.
- **JEDZENIE** — suma sztuk przedmiotów z komponentem FOOD w głównym ekwipunku oraz drugiej ręce.

To są **wskaźniki informacyjne**, nie sposób ustawiania zdrowia, głodu czy ekwipunku na serwerze. Podczas gry modyfikacja wartości nie jest wykonywana; wszystkie dane pochodzą z Minecrafta.

Trzy przełączniki `showHealth`, `showHunger`, `showFoodCount` są zapisane w `config/minevora_pvp.json` w sekcji `_minevora_ui`. Launcher MINEVORA ma osobną zakładkę **PvP HUD**, w której można ustawić widoczność tych wskaźników przed uruchomieniem gry. Mod przeładowuje konfigurację z dysku w trakcie gry, jeśli okno ustawień nie jest otwarte.

**Zgodność:** tylko Minecraft Java 1.21.1 + Fabric + odpowiednie Fabric API. Zmiany są testowane na gałęzi rozwojowej; nie są jeszcze automatycznie dostępne przez funkcję instalacji najnowszego *opublikowanego* wydania w launcherze. Poczekaj na wydanie z podpisanym sumą SHA256 plikiem JAR w GitHub Releases. Nie instaluj automatycznie plików przypadkowego pochodzenia.
