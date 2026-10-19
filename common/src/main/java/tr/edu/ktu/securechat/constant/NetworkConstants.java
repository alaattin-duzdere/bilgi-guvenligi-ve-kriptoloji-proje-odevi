package tr.edu.ktu.securechat.constant;

public final class NetworkConstants {

    private NetworkConstants() {
    }

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 5000;

    public static final int AES_KEY_SIZE_BYTES = 16; // 128-bit
    public static final int AES_IV_SIZE_BYTES = 16;  // 128-bit IV
    public static final int DEFAULT_CHUNK_SIZE_BYTES = 1024; // 1 KB
}
