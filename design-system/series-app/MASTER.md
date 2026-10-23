# Series App visual system

Android catalog for the existing Series CRUD. Scope: home, collection, editor and deletion confirmation. Kotlin and Material 3 Compose; existing API and data remain the authority.

## Research and decisions

UI/UX Pro Max: `colors.csv`, verified match **Video Streaming/OTT** for cinema surfaces and a red accent. The generic design-system queries returned web landing-page structures; those were not adopted. `stacks/jetpack-compose.csv` supplied semantics and accessibility guidance. Native composition follows Material lists and Android accessibility defaults.

- https://m3.material.io/components/lists/overview
- https://developer.android.com/develop/ui/compose/accessibility/api-defaults

## Appearance

Cinema ink #111216, paper #F8F5F0, raised surface #1C1D23. Dark coral #FF8978 with dark ink #3B0A05; light accent #A73527 with white. Secondary dark text #B9B7BE. Both light and dark themes have independent tokens. Dark is the first-run default; the user's selection is saved locally.

Native sans serif for interface text and scalable platform serif only for decorative cover lettering. Four original typographic genre covers are drawn with Compose, because the API has no poster field. These covers are visual identifiers, not official series artwork.

## Interaction

48dp touch targets, 24dp page gutters, readable supporting text, a primary action that does not overlap list content. Native ripples, labeled search, selected category filters, contextual delete, confirmation dialog, inline validation and visible loading. Layouts scroll and constrain reading width on larger screens. Honor system animation scaling through native Compose transitions.

## Boundaries

Do not imply video playback, watch progress, streaming availability or external ratings. All counts and series shown come from Django. Search and category filtering operate on the loaded collection. No EventFlow or additional exercise is included.
