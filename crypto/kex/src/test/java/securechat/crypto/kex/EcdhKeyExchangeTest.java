package securechat.crypto.kex;

import org.junit.jupiter.api.Test;

import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EcdhKeyExchangeTest {

    @Test
    void clientAndServerDeriveTheSameAes128KeyUsingP256() throws Exception {
        EcdhKeyExchange server = new EcdhKeyExchange();
        EcdhKeyExchange client = new EcdhKeyExchange();

        byte[] serverPublicKey = server.serverGeneratePublicKey();
        ECPublicKey ecPublicKey = (ECPublicKey) KeyFactory.getInstance("EC")
                .generatePublic(new X509EncodedKeySpec(serverPublicKey));
        AlgorithmParameters p256 = AlgorithmParameters.getInstance("EC");
        p256.init(new ECGenParameterSpec("secp256r1"));
        assertEquals(p256.getParameterSpec(ECParameterSpec.class).getCurve(),
                ecPublicKey.getParams().getCurve());

        byte[] clientPublicKey = client.clientInitiate(serverPublicKey);
        byte[] serverKey = server.serverDeriveKey(clientPublicKey);
        byte[] clientKey = client.getClientSharedKey();

        assertEquals(16, clientKey.length);
        assertArrayEquals(clientKey, serverKey);
    }
}
