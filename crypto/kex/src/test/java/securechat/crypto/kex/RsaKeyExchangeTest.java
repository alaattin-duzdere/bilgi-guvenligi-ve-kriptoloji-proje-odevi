package securechat.crypto.kex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RsaKeyExchangeTest {

    @Test
    void clientAndServerShareTheSameAes128Key() throws Exception {
        RsaKeyExchange server = new RsaKeyExchange();
        RsaKeyExchange client = new RsaKeyExchange();

        byte[] serverPublicKey = server.serverGeneratePublicKey();
        byte[] encryptedKey = client.clientInitiate(serverPublicKey);
        byte[] serverKey = server.serverDeriveKey(encryptedKey);
        byte[] clientKey = client.getClientSharedKey();

        assertEquals(16, clientKey.length);
        assertArrayEquals(clientKey, serverKey);
    }
}
