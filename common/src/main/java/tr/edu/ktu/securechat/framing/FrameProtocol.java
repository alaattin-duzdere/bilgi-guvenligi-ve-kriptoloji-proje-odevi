package tr.edu.ktu.securechat.framing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public interface FrameProtocol {

    Optional<byte[]> read(InputStream in) throws IOException;

    void write(OutputStream out, byte[] payload) throws IOException;
}