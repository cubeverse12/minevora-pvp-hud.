# MINEVORA PvP HUD 2.0 — Fabric 1.21.1

To jest **projekt źródłowy prawdziwego, klienckiego moda Minecraft Java**. Interfejs powstał na bazie dostarczonego obrazka. Nie jest to jeszcze gotowy skompilowany plik `.jar`.

## Co zawiera
- Nowy ekran MINEVORA: ciemny motyw, fioletowe przyciski, zakładki Mod Menu / HUD / Armor / Keystrokes / Crosshair / Chat / Settings.
- Armor HUD z prawdziwymi ikonami elementów ekwipunku i aktualną trwałością, widoczny w grze.
- Wybór układu Vertical / Horizontal / Compact / Modern, ustawienia wyglądu, suwaki, kolory, widoczność czterech elementów zbroi, głównej i drugiej ręki, przenoszenie panelu.
- Przyciśnij **Prawy Shift**, by otworzyć ekran ustawień. Klawisz można zmienić w ustawieniach sterowania Minecraft.
- Pozostałe moduły poprzedniego projektu: FPS, CPS, Keystrokes, efekty mikstur, Crosshair, Combo.
- Zapis automatyczny do `config/minevora_pvp.json`. Starsze ustawienia z launchera MINEVORA są w miarę możliwości zachowane.
- Grafika podglądu przygotowana na podstawie przesłanej ilustracji; gra pobiera rzeczywiste wartości wyposażenia podczas rozgrywki.

## Zbuduj `.jar` na GitHub bez lokalnego Gradle
1. Wejdź na swoje repozytorium `cubeverse12/minevora-pvp-hud.` na GitHubie.
2. Zrób kopię zapasową obecnych plików, zanim zastąpisz je wersją 2.0.
3. Rozpakuj tę paczkę, a następnie wgraj jej **zawartość** do katalogu głównego repozytorium. Muszą tam trafić m.in. `build.gradle`, `gradle.properties`, `src/main/...` i `.github/workflows/build-mod.yml`.
4. Otwórz kartę **Actions** w repozytorium. Jeśli GitHub poprosi, włącz Workflows.
5. Wybierz **Build MINEVORA PvP HUD** i uruchom **Run workflow** (lub zrób commit na gałęzi `main`).
6. Jeśli budowanie zakończy się na zielono, otwórz uruchomienie workflow, z sekcji **Artifacts** pobierz `minevora-pvp-hud-fabric-1.21.1`.
7. Wypakuj pobrane archiwum: do instalacji służy plik `.jar` **bez** fragmentu `-sources` w nazwie.
8. W launcherze użyj instancji **Minecraft Java 1.21.1 + Fabric Loader + Fabric API**. Wrzuć `.jar` do folderu `mods` **tej samej instancji**, którą uruchamiasz w launcherze MINEVORA.
9. Uruchom świat, wciśnij **Prawy Shift**, wybierz zakładkę **Armor**.

W Windows folder dla *oficjalnej instalacji* Minecraft to zwykle `%appdata%\.minecraft\mods`, ale **MINEVORA Launcher może mieć odrębny katalog gry**: sprawdź w nim ścieżkę instancji.

## Kompilacja lokalna
Zainstaluj **Java JDK 21** i **Gradle 8.12**, w folderze projektu uruchom `gradle build`. Wynik będzie w `build/libs/`. Projekt wymaga połączenia z internetem podczas pobierania zależności Maven i Fabric.

## Zgodność
Paczka jest dla **Minecraft Java 1.21.1 z Fabric**. Jeden plik `.jar` nie działa automatycznie we wszystkich wersjach Minecrafta, ani jednocześnie na Fabric, Forge i NeoForge — porty wymagają oddzielnej implementacji/kompilacji.

Mod działa po stronie klienta i nie instaluje się na serwerze. Nie dodaje autoataków, nie wysyła dodatkowych pakietów ani nie omija zabezpieczeń serwerów.

## Stan testów
Sprawdzono składnię oraz spójność klas Java przy użyciu lokalnych atrap interfejsów API. **Nie uruchomiono prawdziwego Minecrafta ani kompilacji Fabric/Gradle**, bo środowisko nie ma dostępu do zależności Fabric. Jeśli kompilacja w Actions pokaże błędy API, prześlij jej log — nie instaluj niezweryfikowanego pliku.
