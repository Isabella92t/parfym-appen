# ParfymAppen 🌿💧

ParfymAppen är en Android-applikation byggd med Jetpack Compose som hjälper parfymskapare att hålla ordning på sina eteriska oljor och komponera unika doftrecept.

## Funktioner

-   **Hantera Eteriska Oljor**: Lägg till och organisera oljor i kategorier som Topnoter, Mellannoter, Basnoter och Övrigt.
-   **Intensitetsmätning**: Ange intensitet (Låg, Mellan, Hög) för varje olja med visuella dropp-indikatorer.
-   **Sökfunktion**: Hitta snabbt rätt olja i ditt bibliotek.
-   **Receptskapare**: Komponera parfymer genom att välja ingredienser och ange antal droppar.
-   **Automatisk Procentberäkning**: Appen räknar ut den procentuella fördelningen mellan topp-, mellan- och basnoter i realtid.
-   **Doft-hyllan**: Visualisera dina sparade parfymer på en snygg "hylla" med egna flaskor.
-   **Lokal Lagring**: Dina oljor och recept sparas automatiskt på enheten.

## Teknikstack

-   **Språk**: Kotlin
-   **UI-ramverk**: Jetpack Compose (Material 3)
-   **Arkitektur**: State-driven UI med `SnapshotStateList` för reaktiv datahantering.
-   **Lagring**: `SharedPreferences` med JSON-serialisering för enkel persistens.
-   **Animationer**: Compose `InfiniteTransition` för interaktiva element (t.ex. vibrerande flaskor vid radering).

## Skärmar

1.  **Hem**: Enkel navigering mellan oljebiblioteket och parfymhyllan.
2.  **Eteriska Oljor**: Hantera ditt lager av råmaterial.
3.  **Mina Parfymer**: En överblick av alla dina skapelser.
4.  **Skapa/Recept**: Detaljerad vy för att blanda och anteckna kring dina dofter.

---
Utvecklad som ett modernt verktyg för doftkreatörer.
