package tr.edu.ktu.securechat.crypto;

/**
 * AES-128 sifreleme ve cozme islemleri icin ortak arayuz.
 * Hem manuel (kutuphanesiz) hem de kutuphaneli (JCA/JCE) implementasyonlar
 * bu arayuzu uygular.
 */
public interface AesCipher {

    byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv) throws Exception;

    byte[] decrypt(byte[] ciphertext, byte[] key, byte[] iv) throws Exception;

    String getName();
}
