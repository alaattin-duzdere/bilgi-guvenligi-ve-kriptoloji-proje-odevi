package securechat.crypto.aes.manual;

import securechat.crypto.AesCipher;
import java.util.Arrays;
import static securechat.crypto.aes.manual.Aes128Block.BLOCK_SIZE;

public final class ManualAesCipher implements AesCipher {
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 16;

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv) {
        Aes128Block aes = new Aes128Block(key);
        byte[] initialCounter = initialCounter(iv);
        byte[] ciphertext = new byte[Math.addExact(plaintext.length, TAG_SIZE)];

        applyCounterMode(aes, initialCounter, plaintext, ciphertext, plaintext.length);
        byte[] tag = authenticate(aes, initialCounter, ciphertext, plaintext.length);
        System.arraycopy(tag, 0, ciphertext, plaintext.length, TAG_SIZE);

        return ciphertext;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] key, byte[] iv) {
        Aes128Block aes = new Aes128Block(key);
        byte[] initialCounter = initialCounter(iv);

        if (ciphertext.length < TAG_SIZE) {
            throw new IllegalArgumentException("GCM doğrulama etiketi eksik");
        }

        int length = ciphertext.length - TAG_SIZE;
        byte[] tag = authenticate(aes, initialCounter, ciphertext, length);

        // Açık veriyi üretmeden önce etiketin bütün baytlarını kontrol et.
        int difference = 0;
        for (int i = 0; i < TAG_SIZE; i++) {
            difference |= tag[i] ^ ciphertext[length + i];
        }
        if (difference != 0) {
            throw new SecurityException("GCM doğrulaması başarısız");
        }

        byte[] plaintext = new byte[length];
        applyCounterMode(aes, initialCounter, ciphertext, plaintext, length);
        return plaintext;
    }

    @Override
    public String getName() {
        return "Manual AES-128-GCM";
    }

    private static byte[] initialCounter(byte[] iv) {
        if (iv.length != IV_SIZE) {
            throw new IllegalArgumentException("Bu uygulamada IV 12 bayt olmalı");
        }
        byte[] counter = Arrays.copyOf(iv, BLOCK_SIZE);
        counter[BLOCK_SIZE - 1] = 1;
        return counter;
    }

    private static void applyCounterMode(Aes128Block aes, byte[] initialCounter,
                                         byte[] input, byte[] output, int length) {
        byte[] counter = initialCounter.clone();

        for (int offset = 0; offset < length; ) {
            // Sayacın son 32 bitini artır. Taşmayı bir önceki bayta aktar.
            for (int i = BLOCK_SIZE - 1; i >= IV_SIZE; i--) {
                if (++counter[i] != 0) {
                    break;
                }
            }

            byte[] stream = aes.encryptBlock(counter);
            int blockLength = Math.min(BLOCK_SIZE, length - offset);
            for (int i = 0; i < blockLength; i++) {
                output[offset + i] = (byte) (input[offset + i] ^ stream[i]);
            }
            offset += blockLength;
        }
    }

    private static byte[] authenticate(Aes128Block aes, byte[] initialCounter,
                                       byte[] ciphertext, int length) {
        byte[] hashKey = aes.encryptBlock(new byte[BLOCK_SIZE]);
        byte[] hash = new byte[BLOCK_SIZE];

        for (int offset = 0; offset < length; ) {
            int blockLength = Math.min(BLOCK_SIZE, length - offset);
            for (int i = 0; i < blockLength; i++) {
                hash[i] ^= ciphertext[offset + i];
            }
            hash = multiply(hash, hashKey);
            offset += blockLength;
        }

        // Ek doğrulanan veri (AAD) boş. Uzunluk bloğunun son 8 baytı
        // şifreli verinin bit cinsinden uzunluğunu taşır.
        long bitLength = (long) length * Byte.SIZE;
        for (int i = BLOCK_SIZE - 1; i >= BLOCK_SIZE / 2; i--) {
            hash[i] ^= (byte) bitLength;
            bitLength >>>= Byte.SIZE;
        }
        hash = multiply(hash, hashKey);

        byte[] tag = aes.encryptBlock(initialCounter);
        for (int i = 0; i < TAG_SIZE; i++) {
            tag[i] ^= hash[i];
        }
        return tag;
    }

    // GHASH için GF(2^128) alanında çarpma.
    private static byte[] multiply(byte[] left, byte[] right) {
        byte[] product = new byte[BLOCK_SIZE];
        byte[] factor = right.clone();

        for (int bit = 0; bit < BLOCK_SIZE * Byte.SIZE; bit++) {
            int bitMask = -((left[bit / Byte.SIZE] >>> (7 - bit % Byte.SIZE)) & 1);
            int reductionMask = -(factor[BLOCK_SIZE - 1] & 1);
            int carry = 0;

            for (int i = 0; i < BLOCK_SIZE; i++) {
                int value = factor[i] & 0xff;
                product[i] ^= (byte) (value & bitMask);
                factor[i] = (byte) ((value >>> 1) | carry);
                carry = (value & 1) << 7;
            }
            factor[0] ^= (byte) (0xe1 & reductionMask);
        }
        return product;
    }
}