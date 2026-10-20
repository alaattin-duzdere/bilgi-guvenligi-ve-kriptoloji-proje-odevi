package tr.edu.ktu.securechat.crypto;

/**
 * Anahtar paylasim/degisim yontemleri icin ortak arayuz.
 * RSA-2048, Diffie-Hellman, ECDH ve Kyber (ML-KEM-768) bu arayuzu uygular.
 */
public interface KeyExchange {

    KeyExchangeMethod getMethod();

    byte[] serverGeneratePublicKey() throws Exception;

    byte[] clientInitiate(byte[] serverPublicKey) throws Exception;

    byte[] serverDeriveKey(byte[] clientPayload) throws Exception;

    byte[] getClientSharedKey();
}

