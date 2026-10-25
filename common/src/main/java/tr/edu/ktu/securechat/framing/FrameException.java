package tr.edu.ktu.securechat.framing;

import java.io.IOException;

public class FrameException extends IOException {
    public FrameException(String message) {
        super(message);
    }
}
