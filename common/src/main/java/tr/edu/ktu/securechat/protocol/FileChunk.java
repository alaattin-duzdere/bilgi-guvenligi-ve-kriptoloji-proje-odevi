package tr.edu.ktu.securechat.protocol;

import java.io.Serializable;

/**
 * Dosya transferi (ozellikle PDF) icin parca (chunk) veri modeli.
 */
public class FileChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileId;
    private String fileName;
    private int sequenceNumber;
    private int totalChunks;
    private long totalFileSize;
    private byte[] data;

    public FileChunk() {
    }

    public FileChunk(String fileId, String fileName, int sequenceNumber, int totalChunks, long totalFileSize, byte[] data) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.sequenceNumber = sequenceNumber;
        this.totalChunks = totalChunks;
        this.totalFileSize = totalFileSize;
        this.data = data;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
