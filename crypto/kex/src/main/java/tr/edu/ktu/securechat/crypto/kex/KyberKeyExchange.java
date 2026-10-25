package tr.edu.ktu.securechat.crypto.kex;

import tr.edu.ktu.securechat.crypto.KeyExchange;
import tr.edu.ktu.securechat.crypto.KeyExchangeMethod;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KEM;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class KyberKeyExchange implements KeyExchange {

    private byte[] clientSharedKey;
    private KeyPair serverKeyPair;

    public KyberKeyExchange() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Override
    public KeyExchangeMethod getMethod() {
        return KeyExchangeMethod.KYBER_768;
    }

    @Override
    public byte[] serverGeneratePublicKey() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ML-KEM-768", BouncyCastleProvider.PROVIDER_NAME);
        this.serverKeyPair = keyGen.generateKeyPair();

        return this.serverKeyPair.getPublic().getEncoded();
    }

    @Override
    public byte[] clientInitiate(byte[] serverPublicKeyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("ML-KEM-768", BouncyCastleProvider.PROVIDER_NAME);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(serverPublicKeyBytes);
        PublicKey serverPublicKey = keyFactory.generatePublic(keySpec);

        KEM kem = KEM.getInstance("ML-KEM-768", BouncyCastleProvider.PROVIDER_NAME);
        KEM.Encapsulator encapsulator = kem.newEncapsulator(serverPublicKey);
        KEM.Encapsulated encapsulated = encapsulator.encapsulate();

        byte[] sharedSecret = encapsulated.key().getEncoded();
        this.clientSharedKey = Arrays.copyOfRange(sharedSecret, 0, 16);

        return encapsulated.encapsulation();
    }

    @Override
    public byte[] serverDeriveKey(byte[] clientPayload) throws Exception {
        KEM kem = KEM.getInstance("ML-KEM-768", BouncyCastleProvider.PROVIDER_NAME);
        KEM.Decapsulator decapsulator = kem.newDecapsulator(this.serverKeyPair.getPrivate());
        
        byte[] sharedSecret = decapsulator.decapsulate(clientPayload).getEncoded();

        return Arrays.copyOfRange(sharedSecret, 0, 16);
    }

    @Override
    public byte[] getClientSharedKey() {
        return this.clientSharedKey;
    }
}