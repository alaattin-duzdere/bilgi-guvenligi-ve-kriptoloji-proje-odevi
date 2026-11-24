package securechat.crypto.kex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KyberKeyExchangeTest {

    @Test
    void clientAndServerDeriveTheSameAes128KeyUsingMlKem768() throws Exception {
        KyberKeyExchange server = new KyberKeyExchange();
        KyberKeyExchange client = new KyberKeyExchange();

        byte[] serverPublicKey = server.serverGeneratePublicKey();
        byte[] encapsulation = client.clientInitiate(serverPublicKey);
        assertEquals(1088, encapsulation.length);

        byte[] serverKey = server.serverDeriveKey(encapsulation);
        byte[] clientKey = client.getClientSharedKey();

        assertEquals(16, clientKey.length);
        assertArrayEquals(clientKey, serverKey);
    }
}
