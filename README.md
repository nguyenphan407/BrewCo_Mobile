# BrewCo (Cafe Brewco) - Android App

Ứng dụng Android (Jetpack Compose) cho hệ thống BrewCo: xem sản phẩm, đặt hàng, voucher, thanh toán (VNPay), quản trị (một số màn hình admin).

## Yêu cầu
- Android Studio (khuyến nghị bản mới)
- JDK 11
- Android SDK:
  - `minSdk`: 24
  - `targetSdk`: 35

## Cách chạy
### Chạy bằng Android Studio
- Mở thư mục project
- Sync Gradle
- Chọn thiết bị (emulator/real device)
- Run module `app`

### Chạy bằng terminal
```bash
./gradlew :app:assembleDebug
```

## Cấu hình API (Base URL)
Backend mặc định trong tài liệu API: `http://localhost:8080/api`

Nếu chạy backend trên máy test bằng **điện thoại/emulator**, cần đổi host cho đúng:
- Emulator Android Studio: dùng `http://10.0.2.2:8080/api`
- Điện thoại thật: dùng IP LAN của máy chạy backend, ví dụ `http://192.168.1.10:8080/api`

Khai báo base URL qua Gradle property:
- `gradle.properties`: `API_BASE_URL=<your-url>/`
- BuildConfig sinh: `BuildConfig.API_BASE_URL`
- Retrofit dùng tại: `app/src/main/java/com/example/brewco/data/api/ApiClient.kt`

## Tài liệu API
Xem chi tiết endpoints trong:
- `API_DOCUMENTATION.md`

Tóm tắt các endpoint chính:
- Auth: `/auth/login`, `/auth/register`, `/auth/refresh`
- Product: `/products`, `/products/{id}`, `/products/search`
- Category: `/categories`, `/categories/{id}`
- Cart/Order: `/cart`, `/orders`, `/orders/{id}`
- Voucher: `/vouchers`, `/vouchers/apply`


