package tr.edu.ktu.securechat.crypto;

public enum CommunicationMode {
    INSECURE("Guvensiz"),
    SECURE("Guvenli");

    private final String displayName;

    CommunicationMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}