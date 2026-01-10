# BrewCo Mobile – Admin & Booked scopes

This repo mirrors BrewCo_old 1:1 for the Booked and admin product/category flows. Other screens (Cart, History, etc.) remain untouched except for required navigation hooks.

## Scope touched
- Booked screen: dynamic categories/products, search, bottom sheet, banners (assets reused from `BrewCo_old`).
- Admin product: list, CRUD with Cloudinary upload, validation, delete confirm.
- Admin category: list, search, CRUD dialogs, delete confirm.

## Notes / TODO
- Strings added only for admin category; other literals stay inline to match legacy until a full i18n pass is planned.
- Assets are limited to what the three screens need; no global renames.
- If you add navigation items, prefer additive changes only—avoid refactors of existing screens.

Build before committing any changes:
```
./gradlew :app:assembleDebug
```
