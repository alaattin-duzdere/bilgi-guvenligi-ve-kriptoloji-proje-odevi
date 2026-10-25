package tr.edu.ktu.securechat.framing;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

public final class LengthPrefixedFrameProtocol implements FrameProtocol {

    public static final int DEFAULT_MAX_FRAME_SIZE = 16 * 1024 * 1024; // 16 MB
    private static final int HEADER_SIZE = 4;

    private final int maxFrameSize;

    public LengthPrefixedFrameProtocol() {
        this(DEFAULT_MAX_FRAME_SIZE);
    }

    public LengthPrefixedFrameProtocol(int maxFrameSize) {
        if (maxFrameSize <= 0) throw new IllegalArgumentException("maxFrameSize > 0 olmalı");
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    public Optional<byte[]> read(InputStream in) throws IOException {
        byte[] header = in.readNBytes(HEADER_SIZE);
        if (header.length == 0) {
            return Optional.empty();
        }
        if (header.length < HEADER_SIZE) {
            throw new EOFException("Frame başlığı yarım kaldı");
        }

        int length = ByteBuffer.wrap(header).getInt();
        if (length < 0 || length > maxFrameSize) {
            throw new FrameException("Geçersiz frame uzunluğu: " + length);
        }

        byte[] payload = in.readNBytes(length);
        if (payload.length < length) {
            throw new EOFException("Frame gövdesi yarım kaldı");
        }
        return Optional.of(payload);
    }

    @Override
    public void write(OutputStream out, byte[] payload) throws IOException {
        if (payload.length > maxFrameSize) {
            throw new FrameException("Frame çok büyük: " + payload.length);
        }
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + payload.length);
        buffer.putInt(payload.length).put(payload);
        out.write(buffer.array());
        out.flush();
    }
}
