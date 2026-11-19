# NotMe Tools (Android APK + GitHub Actions Auto-Build)

Aplikasi Android native yang membungkus ketiga layanan web yang sudah dibuat:
1. **Top4Top MP3 Auto-Uploader** (`https://t4tup.notmemc.my.id`)
2. **TikSave Downloader** (TikTok MP4 & MP3)
3. **YTSave Downloader** (YouTube MP4 HD & MP3)

## Cara Build `.apk` Otomatis di GitHub
1. Buat repository baru di GitHub bernama **`NotMe-Tools`** (atau **`NotMe Tools`**).
2. Ekstrak seluruh isi file ZIP ini, lalu upload/push ke repository tersebut.
3. Buka tab **Actions** pada repository GitHub -> klik workflow **Build NotMe Tools APK** (otomatis berjalan saat push, atau klik **Run workflow**).
4. Setelah selesai (sekitar 1-2 menit), unduh file **`NotMe-Tools-v1.0.0.apk`** pada bagian **Artifacts** di halaman workflow run tersebut.

## Mengubah Domain Server di Dalam Aplikasi
Ketuk ikon pengaturan (gerigi) di pojok kanan atas aplikasi **NotMe Tools** untuk mengubah alamat domain masing-masing layanan kapan saja tanpa perlu build ulang APK.
