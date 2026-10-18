Contributing · MD
# Katkı Rehberi – Güvenli Haberleşme Projesi
 
Bu proje, Sunucu – İstemci – İstemci mimarisinde çalışan, güvensiz ve güvenli (AES-128) modları olan bir haberleşme sistemidir. Anahtar dağıtımı için **RSA-2048, Diffie-Hellman (2048 bit), ECC (ECDH) ve Kyber (ML-KEM-768)** yöntemleri uygulanır; AES ise ayrıca **kütüphanesiz (manuel)** olarak kodlanır.
 
Bu rehber, 3 kişilik ekibin aynı depoda düzenli çalışması için **branch**, **commit** ve **Pull Request** kurallarını tanımlar. Rapordaki "Görev Dağılımı ve GitHub commit özeti" bölümü doğrudan bu geçmişten üretileceği için kurallara uymak puanı da etkiler.
 
---
 
# 1. Git Branch İsimlendirme Kuralı
 
Branch adı **üç parçadan** oluşur: **ne** yapıldığını ve kodun **neresinde** yapıldığını hemen anlatır.
 
```
tür/alan/açıklama
```
 
**Örnek:**
```
feat/kex-kyber/add-mlkem-encapsulation
```
 
> Branch adlarında Türkçe karakter kullanılmaz; açıklama kısmı İngilizce veya ASCII Türkçe olabilir (ekip tek bir dilde karar verip sürdürmelidir).
 
---
 
## 1.1 Değişiklik Türü (`tür`)
 
| Önek | Kategori | Amaç |
| :--- | :--- | :--- |
| **`feat`** | Özellik | Yeni özellik veya yeni işlevsellik ekleme. |
| **`fix`** | Hata düzeltme | Hatalı davranışı düzeltme. |
| **`refactor`** | Yeniden yapılandırma | Hata düzeltmeyen, özellik eklemeyen kod düzenlemesi. |
| **`perf`** | Performans | Ölçüm sonuçlarını veya hızı etkileyen iyileştirme (ör. anahtar üretim süresi). |
| **`test`** | Test | Yalnızca test ekleme/düzeltme (ör. manuel AES ↔ kütüphane karşılaştırma testi). |
| **`docs`** | Dokümantasyon | README, rapor, yorum satırı değişiklikleri. |
| **`chore`** | Bakım | Bağımlılık, derleme, yapılandırma gibi kodu etkilemeyen işler. |
| **`hotfix`** | Acil düzeltme | Sunum/teslim öncesi çalışan sistemi bozan kritik hata. |
 
---
 
## 1.2 Proje Alanı (`alan`)
 
Platform ve dil serbest olduğu için alanlar **bileşene göre** tanımlanmıştır. Sunucu, istemci ve ortak protokol kodu ayrı alanlardır.
 
### Sunucu Alanları (Önek: `server-`)
 
| Alan | Kapsam | Örnek |
| :--- | :--- | :--- |
| **`server-net`** | Soket/bağlantı yönetimi, iki istemciyi kabul etme, mesaj yönlendirme (relay). | `feat/server-net/relay-message-to-peer` |
| **`server-crypto`** | Gelen veriyi çözme, diğer istemcinin anahtarıyla yeniden şifreleme, istemci başına anahtar tutma. | `feat/server-crypto/reencrypt-for-peer` |
| **`server-storage`** | Gelen PDF parçalarını birleştirme ve diske kaydetme, ekrana yazdırma. | `fix/server-storage/merge-pdf-chunks-in-order` |
 
### İstemci Alanları (Önek: `client-`)
 
| Alan | Kapsam | Örnek |
| :--- | :--- | :--- |
| **`client-ui`** | Arayüz: metin girişi, PDF seçme, mod ve anahtar yöntemi seçimi, ekran çıktıları. | `feat/client-ui/add-kex-method-selector` |
| **`client-net`** | Sunucuya bağlanma, gönderme/alma, ACK işleme. | `fix/client-net/handle-connection-drop` |
| **`client-file`** | PDF okuma, parçalara (chunk) bölme, alınan parçaları birleştirme. | `feat/client-file/split-pdf-into-chunks` |
| **`client-crypto`** | İstemci tarafı AES şifreleme/çözme ve anahtar değişimi çağrıları. | `refactor/client-crypto/extract-session-key-class` |
 
### Anahtar Dağıtım Alanları (Önek: `kex-`)
 
Her yöntem kendi alanına sahiptir; böylece üç kişi aynı anda farklı yöntemler üzerinde çakışmadan çalışabilir.
 
| Alan | Kapsam | Örnek |
| :--- | :--- | :--- |
| **`kex-rsa`** | RSA-2048: istemci AES anahtarı üretir, sunucunun açık anahtarıyla şifreleyip gönderir. | `feat/kex-rsa/encrypt-aes-key-with-pubkey` |
| **`kex-dh`** | Diffie-Hellman (2048 bit): açık değer alışverişi, ortak anahtar türetme. | `feat/kex-dh/add-2048-bit-group` |
| **`kex-ecc`** | ECDH: eliptik eğri üzerinde anahtar anlaşması. | `feat/kex-ecc/implement-ecdh-exchange` |
| **`kex-kyber`** | Kyber (ML-KEM-768): açık anahtar, encapsulate / decapsulate. | `feat/kex-kyber/add-decapsulate-on-server` |
| **`kex-bench`** | Ölçüm: anahtar üretme (ms), anahtar değişimi (ms), ağdaki paket boyutu (byte); 10 ölçümün ortalaması ve tablo. | `feat/kex-bench/measure-key-generation-time` |
 
### Manuel AES Alanları (Önek: `aes-`)
 
| Alan | Kapsam | Örnek |
| :--- | :--- | :--- |
| **`aes-manual`** | Kütüphanesiz AES-128: S-box, round yapısı, permütasyonlar, key expansion, çalışma modu, padding. | `feat/aes-manual/implement-subbytes-shiftrows` |
| **`aes-compare`** | Aynı girdi için manuel ↔ kütüphaneli sonuç karşılaştırması; padding / mod / IV farklarının incelenmesi. | `test/aes-compare/compare-with-library-output` |
 
### Ortak / Çapraz Alanlar (Önek yok)
 
Birden fazla bileşeni ilgilendiren değişiklikler içindir.
 
| Alan | Kapsam | Örnek |
| :--- | :--- | :--- |
| **`protocol`** | İstemci ↔ sunucu mesaj formatı (mod bilgisi, mesaj tipi, chunk başlığı, ACK), el sıkışma akışı. | `feat/protocol/define-message-header` |
| **`secure-mode`** | Güvenli modun uçtan uca akışı (anahtar paylaşımından ACK'ye kadar). | `fix/secure-mode/fix-ack-after-last-chunk` |
| **`insecure-mode`** | Şifresiz modun uçtan uca akışı (Wireshark'ta açık içerik gösterimi için). | `feat/insecure-mode/send-plaintext-message` |
| **`capture`** | Wireshark yakalamaları (`.pcapng`) ve ekran görüntüleri. | `docs/capture/add-secure-mode-pcapng` |
| **`report`** | Teknik rapor (6–10 sayfa PDF), şekiller, tablolar, kaynakça. | `docs/report/add-architecture-diagram` |
| **`infra`** | Ağ/güvenlik duvarı ayarları, Docker, çalıştırma betikleri, IP/port yapılandırması. | `chore/infra/add-firewall-setup-notes` |
| **`deps`** | Kütüphane kurulumu ve sürüm güncellemeleri (ör. ML-KEM kütüphanesi). | `chore/deps/pin-mlkem-library-version` |
 
---
 
## 1.3 Açıklama (`açıklama`)
 
Çalışmanın kısa ve net özeti.
 
* **Biçim:** Tamamı **küçük harf**, **kebab-case** (tire ile: `-`).
* **Kural:** Kısa tutun (en fazla 3–5 kelime).
|  İyi örnek | Kaçının |
| :--- | :--- |
| `add-pdf-chunk-sending` | `adding_pdf_chunk_sending_to_the_client` |
| `fix-padding-bug` | `FixPaddingBug` |
 
### Örnek Branch İsimleri
 
| Amaç | Branch adı |
| :--- | :--- |
| Sunucuda mesaj yönlendirme | `feat/server-net/relay-message-to-peer` |
| PDF parça sırası hatası | `fix/server-storage/merge-pdf-chunks-in-order` |
| Kyber el sıkışması | `feat/kex-kyber/add-encapsulation-flow` |
| ECDH süre ölçümü | `feat/kex-bench/measure-ecdh-exchange-time` |
| Manuel AES MixColumns | `feat/aes-manual/implement-mixcolumns` |
| Manuel AES doğrulama | `test/aes-compare/add-known-answer-tests` |
| Mesaj formatı tanımı | `feat/protocol/define-message-header` |
| Wireshark kayıtları | `docs/capture/add-insecure-mode-pcapng` |
| Rapor: anahtar yöntemleri | `docs/report/write-key-exchange-section` |
| Güvenlik duvarı sorunu | `chore/infra/document-firewall-rules` |
| Sunum öncesi acil hata | `hotfix/secure-mode/fix-session-key-reset` |
 
---
 
# 2. Git Commit Mesajı Kuralı
 
Düzenli bir commit geçmişi, değişiklikleri anlamayı kolaylaştırır ve rapordaki **commit özetinin** doğru çıkmasını sağlar.
 
## 2.1 Genel Biçim
 
```
tür(kapsam): açıklama
 
[isteğe bağlı gövde]
 
[isteğe bağlı alt bilgi]
```
 
**Örnek:**
```
fix(server-storage): merge pdf chunks in order
 
Chunks were appended in arrival order, which corrupted the
saved PDF when packets were reordered.
 
Chunks are now sorted by sequence number before writing.
 
Closes #12
```
 
---
 
## 2.2 Başlık (Header)
 
İlk satırdır; kısa ve bilgilendirici olmalı, **yaklaşık 50 karakteri** geçmemelidir.
 
### A. Tür (Zorunlu)
 
Emir kipi, şimdiki zaman kullanılır ("add", "added" değil).
 
| Tür | Açıklama |
| :--- | :--- |
| **feat** | Yeni özellik. |
| **fix** | Hata düzeltme. |
| **perf** | Performans iyileştirmesi. |
| **docs** | Yalnızca dokümantasyon / rapor / yakalama dosyası. |
| **style** | Biçimlendirme, boşluk, noktalı virgül vb. |
| **refactor** | Hata düzeltmeyen, özellik eklemeyen yeniden yapılandırma. |
| **test** | Test ekleme veya düzeltme. |
| **chore** | Bakım, yapılandırma, bağımlılık, derleme işleri. |
 
### B. Kapsam (Bu projede **zorunlu**)
 
Kapsam, branch'teki **alan** ile aynı adı taşır; böylece hangi bileşenin değiştiği geçmişte hemen görünür.
 
| Bileşen | Örnek kapsamlar | Tam örnek |
| :--- | :--- | :--- |
| **Sunucu** | `server-net`, `server-crypto`, `server-storage` | `feat(server-crypto): re-encrypt data for peer` |
| **İstemci** | `client-ui`, `client-net`, `client-file`, `client-crypto` | `fix(client-file): read pdf in 1 KB chunks` |
| **Anahtar dağıtımı** | `kex-rsa`, `kex-dh`, `kex-ecc`, `kex-kyber`, `kex-bench` | `feat(kex-kyber): add ML-KEM-768 decapsulate` |
| **Manuel AES** | `aes-manual`, `aes-compare` | `feat(aes-manual): add key expansion` |
| **Ortak** | `protocol`, `secure-mode`, `insecure-mode`, `capture`, `report`, `infra`, `deps` | `docs(capture): add secure mode pcapng` |
 
### C. Açıklama Kuralları
 
* **Emir kipi kullanın:** Fiille başlayın ("add", "update", "remove").
* **Uzunluk:** Başlık satırının tamamı **50 karakter** civarında olmalı.
* **Büyük harf yok:** İlk harf küçük (AES, RSA, ECDH gibi kısaltmalar hariç).
* **Noktalama yok:** Başlık nokta ile bitmez.
---
 
## 2.3 Gövde (Body)
 
Değişikliğin **neden** gerektiğini anlatır.
 
* **Gerekçe:** Sadece *nasıl* değil, *neden* yapıldığını yazın.
* **Ayırma:** Başlıktan **tek bir boş satırla** ayrılır.
* **Satır genişliği:** Satırları **72 karakterde** bölün.
* **Karşılaştırma:** Önceki davranış ile yeni davranışı belirtin.
### Örnek Gövde
```
fix(aes-manual): correct padding in last block
 
The manual implementation did not pad the final block, so the
output differed from the library result for inputs whose length
was not a multiple of 16 bytes.
 
PKCS#7 padding is now applied before encryption, matching the
library output.
```
 
---
 
## 2.4 Alt Bilgi (Footer)
 
### A. Kırıcı Değişiklikler (Breaking Changes)
 
Mesaj formatı veya el sıkışma akışı gibi **geriye dönük uyumsuz** değişikliklerde (ör. sunucu ve istemcinin birlikte güncellenmesi gerekiyorsa):
 
1. Başlıkta tür/kapsamdan sonra `!` ekleyin: `feat(protocol)!:`
2. Alt bilgiyi `BREAKING CHANGE:` ile başlatıp geçiş adımlarını yazın.
```
feat(protocol)!: add sequence number to chunk header
 
BREAKING CHANGE: Chunk header now carries a 4-byte sequence
number. Server and both clients must be updated together.
 
Closes #8
```
 
### B. Issue Takibi
 
* `Closes #123`
* `Fixes #45`
---
 
# 3. Çalışma Akışı
 
1. **`main`** her zaman çalışır durumda olmalıdır. Doğrudan `main`'e commit atılmaz.
2. Her iş için `main`'den, **1. bölümdeki kurala göre** yeni bir branch açılır.
3. İş bitince **Pull Request (PR)** açılır; **en az 1 ekip arkadaşı** inceler ve onaylar.
4. Birleştirmeden önce branch'in `main` ile güncel olduğundan emin olun (`git pull --rebase origin main`).
5. Aynı dosya üzerinde birden fazla kişi çalışacaksa önce ekip içinde haberleşin; özellikle `protocol` alanındaki değişiklikler **mutlaka** herkese duyurulmalıdır, çünkü sunucu ve iki istemciyi birden etkiler.
## Pull Request Başlığı
 
PR başlığı, commit başlığı kuralına uyar:
 
```
feat(kex-ecc): implement ECDH key exchange
```
 
## PR Kontrol Listesi
 
- [ ] Kod derleniyor / çalışıyor (güvenli ve güvensiz modda denendi)
- [ ] İlgili alan için test veya manuel doğrulama yapıldı
- [ ] Anahtar dağıtımı değiştiyse `kex-bench` ölçümleri güncellendi
- [ ] Protokol değiştiyse sunucu ve istemciler birlikte güncellendi
- [ ] README (kurulum / çalıştırma adımları) gerekiyorsa güncellendi
- [ ] Depoya gizli anahtar veya kişisel veri eklenmedi
---
 
# 4. Depo Düzeni (Öneri)
 
Dil ve platform serbest olduğundan klasör adları ekibin seçimine göre uyarlanabilir; ancak bileşenlerin ayrı tutulması önerilir.
 
```
.
├── server/          # Sunucu uygulaması
├── client/          # İstemci uygulaması
├── crypto/
│   ├── kex/         # RSA, DH, ECC, Kyber (ML-KEM)
│   └── aes-manual/  # Kütüphanesiz AES-128 + karşılaştırma testleri
├── bench/           # Anahtar üretme / değişim süresi ve paket boyutu ölçümleri
├── captures/        # .pcapng dosyaları (her mod için en az bir tane)
├── report/          # Rapor kaynakları ve şekiller
├── CONTRIBUTING.md
└── README.md        # Kurulum, hangi bilgisayarda hangi program çalışır
```
 
---
 
# 5. Ekip İçi Ek Kurallar
 
* **Kendi adınızla commit atın.** Rapordaki görev dağılımı tablosu ve commit özeti için herkes `git config user.name` ve `user.email` ayarını gerçek adıyla yapmalıdır. Özet şu komutla çıkarılabilir:
```
  git shortlog -sn --no-merges
```
* **Gizli bilgi paylaşılmaz.** Özel anahtarlar (RSA private key vb.), şifreler ve oturum anahtarları depoya eklenmez; `.gitignore` kullanın. Örnek/test anahtarları açıkça "test" olarak adlandırılmalıdır.
* **Yakalama dosyaları (`.pcapng`)** `captures/` altında tutulur ve dosya adı modu ile yöntemi belirtir (ör. `secure-kyber-server-side.pcapng`, `insecure-client-side.pcapng`). Yakalamanın hangi bilgisayarda yapıldığı README veya rapordaki ekte belirtilir.
* **Ölçüm sonuçları** (`kex-bench`) tekrarlanabilir olmalıdır: 10 ölçümün ortalaması alınır, hangi makinede ve hangi kütüphane sürümüyle ölçüldüğü not edilir.
* **Manuel AES** değişiklikleri, kütüphaneli sonuçla karşılaştırma testi (`aes-compare`) geçmeden birleştirilmez.
* **Küçük ve sık commit** atın; tek seferde devasa commit yapmayın.
 
Claude finished the response
