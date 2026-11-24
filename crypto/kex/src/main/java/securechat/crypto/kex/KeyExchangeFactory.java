package securechat.crypto.kex;

import securechat.crypto.KeyExchange;
import securechat.crypto.KeyExchangeMethod;

public final class KeyExchangeFactory {

    private KeyExchangeFactory() {
    }

    public static KeyExchange create(KeyExchangeMethod method) {
        return switch (method) {
            case RSA_2048 -> new RsaKeyExchange();
            case DIFFIE_HELLMAN_2048 -> new DhKeyExchange();
            case ECDH -> new EcdhKeyExchange();
            case KYBER_768 -> new KyberKeyExchange();
        };
    }
}
