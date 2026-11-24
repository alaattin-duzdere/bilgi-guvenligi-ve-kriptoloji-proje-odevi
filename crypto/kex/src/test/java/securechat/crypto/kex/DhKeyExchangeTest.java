package securechat.crypto.kex;

import org.junit.jupiter.api.Test;

import javax.crypto.interfaces.DHPublicKey;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DhKeyExchangeTest {

    @Test
    void clientAndServerDeriveTheSameAes128KeyUsingDh2048() throws Exception {
        DhKeyExchange server = new DhKeyExchange();
        DhKeyExchange client = new DhKeyExchange();

        byte[] serverPublicKey = server.serverGeneratePublicKey();
        DHPublicKey dhPublicKey = (DHPublicKey) KeyFactory.getInstance("DH")
                .generatePublic(new X509EncodedKeySpec(serverPublicKey));
        assertEquals(2048, dhPublicKey.getParams().getP().bitLength());

        byte[] clientPublicKey = client.clientInitiate(serverPublicKey);
        byte[] serverKey = server.serverDeriveKey(clientPublicKey);
        byte[] clientKey = client.getClientSharedKey();

        assertEquals(16, clientKey.length);
        assertArrayEquals(clientKey, serverKey);
    }
}
