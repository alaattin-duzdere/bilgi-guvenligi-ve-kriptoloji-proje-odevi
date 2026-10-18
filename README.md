# Güvenli Haberleşme Projesi

**Karadeniz Teknik Üniversitesi – Of Teknoloji Fakültesi – Yazılım Mühendisliği Bölümü**
**Ders:** Kriptoloji | **Dönem:** 2026–2027 | **Öğretim Üyesi:** Dr. Hakan AYDIN

Sunucu – İstemci – İstemci mimarisinde, **güvensiz** ve **güvenli** modda metin ve PDF iletimi yapan; AES anahtar dağıtımı için **RSA-2048, Diffie-Hellman (2048 bit), ECC (ECDH) ve Kyber (ML-KEM-768)** yöntemlerini karşılaştıran bir Java masaüstü uygulaması.

> **Not:** RSA, DH, ECDH ve Kyber veriyi şifrelemek için değil, **AES-128 anahtarını güvenle paylaşmak** için kullanılır. Verinin kendisi her zaman AES ile şifrelenir.

> **Proje durumu:** Geliştirme aşamasında. Bu belge proje ilerledikçe güncellenecektir.

---

##  Özellikler

- **3 bileşen:** 1 sunucu + 2 istemci. İstemciler birbirine doğrudan bağlanmaz; her mesaj önce sunucuya gider, sunucu onu diğer istemciye iletir.
- **İki tür veri:** Klavyeden yazılan metin ve dosya seçiciyle seçilen PDF dosyası.
- **Güvensiz mod:** Veri şifrelenmeden gönderilir.
- **Güvenli mod:** Önce istemci ile sunucu arasında AES-128 anahtarı paylaşılır. Metin ve dosya parçaları bu anahtarla şifrelenerek gönderilir. Sunucu veriyi çözer, ekrana yazar (dosyaysa kaydeder) ve diğer istemciye **o istemcinin anahtarıyla yeniden şifreleyerek** iletir.
- **4 anahtar dağıtım yöntemi:** RSA-2048, Diffie-Hellman (2048 bit), ECDH, Kyber (ML-KEM-768). Program başlatılırken kullanıcı birini seçer.
- **Kütüphanesiz AES-128:** Uygulama varsayılan olarak, round yapısı, S-box ve permütasyonlar dahil elle kodlanmış AES-128 kullanır. Kütüphaneli (JCA/JCE) AES ise karşılaştırma ve yedek amaçlı aynı modülde ayrı bir pakette tutulur.

---

## Kullanılan Tekonolojiler

| Bileşen | Seçim |
|---------|-------|
| Platform | Masaüstü |
| Programlama dili | Java 25 (LTS) |
| Derleme aracı | Maven |
| Arayüz | JavaFX |
| Ağ iletişimi | TCP soket (`java.net`), port `5000` |
| RSA / DH / ECDH / kütüphaneli AES | JCA/JCE (`javax.crypto`, `java.security`) |
| Kyber (ML-KEM-768) | Önce JCA/JCE; yetersiz kalırsa Bouncy Castle |
| Test | JUnit 5 |

---

## Sistem Mimarisi

Sistem en az 2 bilgisayarda çalıştırılır: bir bilgisayarda sunucu, diğerinde istemciler.

```
┌──────────────────┐         ┌──────────────────────┐         ┌──────────────────┐
│    İstemci A     │◄───────►│        SUNUCU        │◄───────►│    İstemci B     │
└──────────────────┘   TCP   └──────────────────────┘   TCP   └──────────────────┘
```

### Güvenli Mod Akışı

1. İstemci sunucuya bağlanır; güvenli mod ve anahtar dağıtım yöntemi belirlenir.
2. Seçilen yönteme göre AES-128 anahtarı paylaşılır.
3. İstemci metin/PDF parçalarını AES ile şifreleyip sunucuya gönderir.
4. Sunucu veriyi çözer, ekrana yazar (dosyaysa kaydeder).
5. Sunucu veriyi hedef istemcinin anahtarıyla yeniden şifreleyip iletir.
6. Hedef istemci çözer ve ACK gönderir.

### Anahtar Dağıtım Yöntemleri

| Yöntem | Anahtar nasıl paylaşılır? |
|--------|---------------------------|
| **RSA-2048** | İstemci rastgele AES anahtarı üretir, sunucunun açık anahtarıyla şifreleyip gönderir. |
| **Diffie-Hellman (2048 bit)** | İki taraf açık değerlerini karşılıklı gönderir, ortak anahtarı kendileri hesaplar. |
| **ECC (ECDH)** | DH ile aynı fikir, eliptik eğri üzerinde. |
| **Kyber (ML-KEM-768)** | Sunucu açık anahtar gönderir, istemci gizli değeri kapsüller (encapsulate), sunucu açar (decapsulate). |

---

## Proje Yapısı

Proje, **çok modüllü bir Maven projesidir.**

```
.
├── server/          # Sunucu uygulaması
├── client/          # İstemci uygulaması
├── crypto/
│   ├── kex/         # RSA, DH, ECC, Kyber (ML-KEM)
│   └── aes/         # Kütüphanesiz AES-128 + karşılaştırma testleri
├── bench/           # Anahtar üretme / değişim süresi ve paket boyutu ölçümleri
├── captures/        # .pcapng dosyaları (her mod için en az bir tane)
├── report/          # Rapor kaynakları ve şekiller
├── CONTRIBUTING.md
└── README.md        # Kurulum, hangi bilgisayarda hangi program çalışır
```


| Modül | İçerik | Bağımlılıkları |
|-------|--------|----------------|
| `common` | Mesaj formatı, `AesCipher` ve `KeyExchange` arayüzleri, sabitler | – |
| `kex` | Dört anahtar dağıtım yönteminin uygulaması | `common` |
| `aes` | `manual` paketi: elle kodlanmış AES-128 (varsayılan); `library` paketi: JCA/JCE tabanlı AES | `common` |
| `server` | Bağlantı kabulü, mesaj çözme ve yeniden şifreleyip yönlendirme | `common`, `keyexchange`, `aes` |
| `client` | JavaFX arayüzü, sunucu bağlantısı, metin/PDF gönderimi | `common`, `keyexchange`, `aes`, JavaFX |

`aes` modülündeki `manual` ve `library` paketleri aynı `AesCipher` arayüzünü uygular. Böylece uygulama hangisinin kullanıldığından bağımsız çalışır; varsayılan olarak `manual` seçilidir. Manuel AES'in doğruluğu, `library` ile karşılaştıran testlerle kontrol edilir.

---

## Kurulum

### Gereksinimler

- Java 25 (JDK)
- Maven 3.9+
- Aynı ağda en az 2 bilgisayar

### Derleme

```bash
git clone https://github.com/alaattin-duzdere/bilgi-guvenligi-ve-kriptoloji-proje-odevi.git
cd bilgi-guvenligi-ve-kriptoloji-proje-odevi
mvn clean package
```
