package tr.edu.ktu.securechat.protocol;

public enum MessageType {
    HELLO,
    KEY_EXCHANGE_INIT,
    KEY_EXCHANGE_RESPONSE,
    KEY_EXCHANGE_COMPLETE,

    TEXT_MESSAGE,
    FILE_START,
    FILE_CHUNK,
    FILE_END,

    ACK,
    ERROR
}
