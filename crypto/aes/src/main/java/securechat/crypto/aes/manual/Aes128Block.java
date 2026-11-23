package securechat.crypto.aes.manual;

import java.util.HexFormat;

final class Aes128Block {
    static final int BLOCK_SIZE = 16;
    private static final int ROUNDS = 10;

    // FIPS 197'deki S-box. HexFormat yalnızca tabloyu baytlara dönüştürür.
    private static final byte[] S_BOX = HexFormat.of().parseHex(
            "637c777bf26b6fc53001672bfed7ab76" +
                    "ca82c97dfa5947f0add4a2af9ca472c0" +
                    "b7fd9326363ff7cc34a5e5f171d83115" +
                    "04c723c31896059a071280e2eb27b275" +
                    "09832c1a1b6e5aa0523bd6b329e32f84" +
                    "53d100ed20fcb15b6acbbe394a4c58cf" +
                    "d0efaafb434d338545f9027f503c9fa8" +
                    "51a3408f929d38f5bcb6da2110fff3d2" +
                    "cd0c13ec5f974417c4a77e3d645d1973" +
                    "60814fdc222a908846eeb814de5e0bdb" +
                    "e0323a0a4906245cc2d3ac629195e479" +
                    "e7c8376d8dd54ea96c56f4ea657aae08" +
                    "ba78252e1ca6b4c6e8dd741f4bbd8b8a" +
                    "703eb5664803f60e613557b986c11d9e" +
                    "e1f8981169d98e949b1e87e9ce5528df" +
                    "8ca1890dbfe6426841992d0fb054bb16");

    private final byte[] roundKeys = new byte[BLOCK_SIZE * (ROUNDS + 1)];

    Aes128Block(byte[] key) {
        if (key.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("AES-128 anahtarı 16 bayt olmalı");
        }

        System.arraycopy(key, 0, roundKeys, 0, BLOCK_SIZE);
        int roundConstant = 1;

        // İlk 16 bayt anahtarın kendisi. Kalan 160 bayt tur anahtarları.
        for (int offset = BLOCK_SIZE; offset < roundKeys.length; offset += 4) {
            int a = roundKeys[offset - 4] & 0xff;
            int b = roundKeys[offset - 3] & 0xff;
            int c = roundKeys[offset - 2] & 0xff;
            int d = roundKeys[offset - 1] & 0xff;

            if (offset % BLOCK_SIZE == 0) {
                int first = a;
                a = S_BOX[b] ^ roundConstant;
                b = S_BOX[c];
                c = S_BOX[d];
                d = S_BOX[first];
                roundConstant = xtime(roundConstant);
            }

            roundKeys[offset]     = (byte) (roundKeys[offset - 16] ^ a);
            roundKeys[offset + 1] = (byte) (roundKeys[offset - 15] ^ b);
            roundKeys[offset + 2] = (byte) (roundKeys[offset - 14] ^ c);
            roundKeys[offset + 3] = (byte) (roundKeys[offset - 13] ^ d);
        }
    }

    byte[] encryptBlock(byte[] block) {
        if (block.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("AES bloğu 16 bayt olmalı");
        }

        byte[] state = block.clone();
        byte[] shifted = new byte[BLOCK_SIZE];
        addRoundKey(state, 0);

        for (int round = 1; round <= ROUNDS; round++) {
            // Matris sütun sırasıyla tutulur. Her satır kendi indeksi kadar
            // sola kayar. Kaynaktan okurken S-box da uygulanır.
            for (int column = 0; column < 4; column++) {
                for (int row = 0; row < 4; row++) {
                    int source = 4 * ((column + row) % 4) + row;
                    shifted[4 * column + row] = S_BOX[state[source] & 0xff];
                }
            }

            if (round < ROUNDS) {
                mixColumns(shifted);
            }
            addRoundKey(shifted, round);

            byte[] previous = state;
            state = shifted;
            shifted = previous;
        }
        return state;
    }

    private void addRoundKey(byte[] state, int round) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= roundKeys[round * BLOCK_SIZE + i];
        }
    }

    private static void mixColumns(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE; i += 4) {
            int a = state[i] & 0xff;
            int b = state[i + 1] & 0xff;
            int c = state[i + 2] & 0xff;
            int d = state[i + 3] & 0xff;
            int sum = a ^ b ^ c ^ d;

            state[i]     = (byte) (a ^ sum ^ xtime(a ^ b));
            state[i + 1] = (byte) (b ^ sum ^ xtime(b ^ c));
            state[i + 2] = (byte) (c ^ sum ^ xtime(c ^ d));
            state[i + 3] = (byte) (d ^ sum ^ xtime(d ^ a));
        }
    }

    // AES'in sonlu alanında bir baytı 2 ile çarpar.
    private static int xtime(int value) {
        return ((value << 1) & 0xff) ^ ((value >>> 7) * 0x1b);
    }
}