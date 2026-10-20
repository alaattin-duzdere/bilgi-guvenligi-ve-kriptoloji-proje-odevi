package tr.edu.ktu.securechat.crypto.kex;

import tr.edu.ktu.securechat.crypto.KeyExchange;
import tr.edu.ktu.securechat.crypto.KeyExchangeMethod;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

public class RsaKeyExchange implements KeyExchange {

    private byte[] clientSharedKey;
    private KeyPair serverKeyPair;

    @Override
    public KeyExchangeMethod getMethod() {
        return KeyExchangeMethod.RSA_2048;
    }

    @Override
    public byte[] serverGeneratePublicKey() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048, new SecureRandom());
        this.serverKeyPair = keyGen.generateKeyPair();

        return this.serverKeyPair.getPublic().getEncoded();
    }

    @Override
    public byte[] clientInitiate(byte[] serverPublicKeyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(serverPublicKeyBytes);
        PublicKey serverPublicKey = keyFactory.generatePublic(keySpec);

        this.clientSharedKey = new byte[16];
        new SecureRandom().nextBytes(this.clientSharedKey);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        return cipher.doFinal(this.clientSharedKey);
    }

    @Override
    public byte[] serverDeriveKey(byte[] clientPayload) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, this.serverKeyPair.getPrivate());
        return cipher.doFinal(clientPayload);
    }

    @Override
    public byte[] getClientSharedKey() {
        return this.clientSharedKey;
    }
}