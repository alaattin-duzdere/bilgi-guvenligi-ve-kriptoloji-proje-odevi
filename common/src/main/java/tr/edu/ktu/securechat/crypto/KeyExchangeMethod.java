package tr.edu.ktu.securechat.crypto;

/**
 * Desteklenen anahtar dagitim yontemleri.
 */
public enum KeyExchangeMethod {
    RSA_2048("RSA-2048"),
    DIFFIE_HELLMAN_2048("Diffie-Hellman-2048"),
    ECDH("ECDH"),
    KYBER_768("Kyber-ML-KEM-768");

    private final String displayName;

    KeyExchangeMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
