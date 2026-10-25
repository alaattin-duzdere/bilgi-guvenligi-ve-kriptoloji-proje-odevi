package tr.edu.ktu.securechat.crypto.kex;

import tr.edu.ktu.securechat.crypto.KeyExchange;
import tr.edu.ktu.securechat.crypto.KeyExchangeMethod;

import javax.crypto.KeyAgreement;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class EcdhKeyExchange implements KeyExchange {

    private byte[] clientSharedKey;
    private KeyPair serverKeyPair;

    @Override
    public KeyExchangeMethod getMethod() {
        return KeyExchangeMethod.ECDH;
    }

    @Override
    public byte[] serverGeneratePublicKey() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"));
        this.serverKeyPair = keyGen.generateKeyPair();

        return this.serverKeyPair.getPublic().getEncoded();
    }

    @Override
    public byte[] clientInitiate(byte[] serverPublicKeyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPublicKeyBytes);
        PublicKey serverPublicKey = keyFactory.generatePublic(x509KeySpec);

        KeyPairGenerator clientKeyGen = KeyPairGenerator.getInstance("EC");
        clientKeyGen.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair clientKeyPair = clientKeyGen.generateKeyPair();

        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(clientKeyPair.getPrivate());
        keyAgreement.doPhase(serverPublicKey, true);

        byte[] sharedSecret = keyAgreement.generateSecret();
        this.clientSharedKey = Arrays.copyOfRange(sharedSecret, 0, 16);

        return clientKeyPair.getPublic().getEncoded();
    }

    @Override
    public byte[] serverDeriveKey(byte[] clientPayload) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPayload);
        PublicKey clientPublicKey = keyFactory.generatePublic(x509KeySpec);

        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(this.serverKeyPair.getPrivate());
        keyAgreement.doPhase(clientPublicKey, true);

        byte[] sharedSecret = keyAgreement.generateSecret();
        return Arrays.copyOfRange(sharedSecret, 0, 16);
    }

    @Override
    public byte[] getClientSharedKey() {
        return this.clientSharedKey;
    }
}