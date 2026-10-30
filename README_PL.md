# MINEVORA PvP HUD 2.0.15 — serduszka i przesuwane kafelki

## Nowości
- **Serca nad graczami:** czerwone serduszka nad widocznymi postaciami innych graczy (do 24 bloków). Rozmiar i ilość pokazują stan zdrowia dostępny po stronie klienta. Na niektórych serwerach klient nie otrzymuje aktualnego HP innych graczy; wtedy wskazanie może być niepełne lub nieaktualne. Mod nie omija ograniczeń serwera.
- **Dowolne pozycje kafelków HUD:** oddzielnie przeciągaj **Armor, CPS, FPS, Potion HUD i Combo**; zachowują pozycję po ponownym uruchomieniu.
- **Włączenie/wyłączenie serduszek:** menu HUD → „Serca nad graczami”.
- **Przywrócenie domyślnego układu:** ekran edycji → „Reset”.
- **Celownik** pozostaje wyśrodkowany, ponieważ musi wskazywać faktyczny punkt celowania.

## Użytkowanie
1. Uruchom Minecraft **Fabric 1.21.1** z Fabric API i tym modem w folderze `mods`.
2. Naciśnij **prawy Shift** (alternatywnie **P**), aby otworzyć ustawienia PvP.
3. Wybierz **HUD → PRZESUN KAFELKI** albo **Armor → USTAW HUD**.
4. Przeciągaj kafelki myszką. Strzałki przesuwają wybrany kafelek o 1 piksel, **Gotowe** zapisuje układ.
5. Naciśnij **H**, aby ukryć/pokazać cały HUD. Pozycje i ustawienia są zapisane w `config/minevora_pvp.json`.

## Zgodność
Fabric 1.21.1, Java 21, Fabric API. Ta wersja nie jest modem na Forge/NeoForge ani na wszystkie wersje Minecrafta. Wydanie musi zostać skompilowane i przetestowane w grze przed dystrybucją; samo wgranie kodu źródłowego na GitHub nie instaluje aktualizacji w launcherze.
