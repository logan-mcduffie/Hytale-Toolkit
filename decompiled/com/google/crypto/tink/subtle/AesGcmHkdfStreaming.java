package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingKey;
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingParameters;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@AccessesPartialKey
public final class AesGcmHkdfStreaming extends NonceBasedStreamingAead {
   private static final int NONCE_SIZE_IN_BYTES = 12;
   private static final int NONCE_PREFIX_IN_BYTES = 7;
   private static final int TAG_SIZE_IN_BYTES = 16;
   private final int keySizeInBytes;
   private final int ciphertextSegmentSize;
   private final int plaintextSegmentSize;
   private final int firstSegmentOffset;
   private final String hkdfAlg;
   private final byte[] ikm;

   private static Buffer toBuffer(ByteBuffer b) {
      return b;
   }

   public AesGcmHkdfStreaming(byte[] ikm, String hkdfAlg, int keySizeInBytes, int ciphertextSegmentSize, int firstSegmentOffset) throws InvalidAlgorithmParameterException {
      if (ikm.length >= 16 && ikm.length >= keySizeInBytes) {
         Validators.validateAesKeySize(keySizeInBytes);
         if (ciphertextSegmentSize <= firstSegmentOffset + this.getHeaderLength() + 16) {
            throw new InvalidAlgorithmParameterException("ciphertextSegmentSize too small");
         } else {
            this.ikm = Arrays.copyOf(ikm, ikm.length);
            this.hkdfAlg = hkdfAlg;
            this.keySizeInBytes = keySizeInBytes;
            this.ciphertextSegmentSize = ciphertextSegmentSize;
            this.firstSegmentOffset = firstSegmentOffset;
            this.plaintextSegmentSize = ciphertextSegmentSize - 16;
         }
      } else {
         throw new InvalidAlgorithmParameterException("ikm too short, must be >= " + Math.max(16, keySizeInBytes));
      }
   }

   private AesGcmHkdfStreaming(AesGcmHkdfStreamingKey key) throws GeneralSecurityException {
      this.ikm = key.getInitialKeyMaterial().toByteArray(InsecureSecretKeyAccess.get());
      String hkdfAlgString = "";
      if (key.getParameters().getHkdfHashType().equals(AesGcmHkdfStreamingParameters.HashType.SHA1)) {
         hkdfAlgString = "HmacSha1";
      } else if (key.getParameters().getHkdfHashType().equals(AesGcmHkdfStreamingParameters.HashType.SHA256)) {
         hkdfAlgString = "HmacSha256";
      } else {
         if (!key.getParameters().getHkdfHashType().equals(AesGcmHkdfStreamingParameters.HashType.SHA512)) {
            throw new GeneralSecurityException("Unknown HKDF algorithm " + key.getParameters().getHkdfHashType());
         }

         hkdfAlgString = "HmacSha512";
      }

      this.hkdfAlg = hkdfAlgString;
      this.keySizeInBytes = key.getParameters().getDerivedAesGcmKeySizeBytes();
      this.ciphertextSegmentSize = key.getParameters().getCiphertextSegmentSizeBytes();
      this.firstSegmentOffset = 0;
      this.plaintextSegmentSize = this.ciphertextSegmentSize - 16;
   }

   public static StreamingAead create(AesGcmHkdfStreamingKey key) throws GeneralSecurityException {
      return new AesGcmHkdfStreaming(key);
   }

   public AesGcmHkdfStreaming.AesGcmHkdfStreamEncrypter newStreamSegmentEncrypter(byte[] aad) throws GeneralSecurityException {
      return new AesGcmHkdfStreaming.AesGcmHkdfStreamEncrypter(aad);
   }

   public AesGcmHkdfStreaming.AesGcmHkdfStreamDecrypter newStreamSegmentDecrypter() throws GeneralSecurityException {
      return new AesGcmHkdfStreaming.AesGcmHkdfStreamDecrypter();
   }

   @Override
   public int getPlaintextSegmentSize() {
      return this.plaintextSegmentSize;
   }

   @Override
   public int getCiphertextSegmentSize() {
      return this.ciphertextSegmentSize;
   }

   @Override
   public int getHeaderLength() {
      return 1 + this.keySizeInBytes + 7;
   }

   @Override
   public int getCiphertextOffset() {
      return this.getHeaderLength() + this.firstSegmentOffset;
   }

   @Override
   public int getCiphertextOverhead() {
      return 16;
   }

   public int getFirstSegmentOffset() {
      return this.firstSegmentOffset;
   }

   public long expectedCiphertextSize(long plaintextSize) {
      long offset = this.getCiphertextOffset();
      long fullSegments = (plaintextSize + offset) / this.plaintextSegmentSize;
      long ciphertextSize = fullSegments * this.ciphertextSegmentSize;
      long lastSegmentSize = (plaintextSize + offset) % this.plaintextSegmentSize;
      if (lastSegmentSize > 0L) {
         ciphertextSize += lastSegmentSize + 16L;
      }

      return ciphertextSize;
   }

   private static Cipher cipherInstance() throws GeneralSecurityException {
      return EngineFactory.CIPHER.getInstance("AES/GCM/NoPadding");
   }

   private byte[] randomSalt() {
      return Random.randBytes(this.keySizeInBytes);
   }

   private static GCMParameterSpec paramsForSegment(byte[] prefix, long segmentNr, boolean last) throws GeneralSecurityException {
      ByteBuffer nonce = ByteBuffer.allocate(12);
      nonce.order(ByteOrder.BIG_ENDIAN);
      nonce.put(prefix);
      SubtleUtil.putAsUnsigedInt(nonce, segmentNr);
      nonce.put((byte)(last ? 1 : 0));
      return new GCMParameterSpec(128, nonce.array());
   }

   private static byte[] randomNonce() {
      return Random.randBytes(7);
   }

   private SecretKeySpec deriveKeySpec(byte[] salt, byte[] aad) throws GeneralSecurityException {
      byte[] key = Hkdf.computeHkdf(this.hkdfAlg, this.ikm, salt, aad, this.keySizeInBytes);
      return new SecretKeySpec(key, "AES");
   }

   class AesGcmHkdfStreamDecrypter implements StreamSegmentDecrypter {
      private SecretKeySpec keySpec;
      private Cipher cipher;
      private byte[] noncePrefix;

      @Override
      public synchronized void init(ByteBuffer header, byte[] aad) throws GeneralSecurityException {
         if (header.remaining() != AesGcmHkdfStreaming.this.getHeaderLength()) {
            throw new InvalidAlgorithmParameterException("Invalid header length");
         } else {
            byte firstByte = header.get();
            if (firstByte != AesGcmHkdfStreaming.this.getHeaderLength()) {
               throw new GeneralSecurityException("Invalid ciphertext");
            } else {
               this.noncePrefix = new byte[7];
               byte[] salt = new byte[AesGcmHkdfStreaming.this.keySizeInBytes];
               header.get(salt);
               header.get(this.noncePrefix);
               this.keySpec = AesGcmHkdfStreaming.this.deriveKeySpec(salt, aad);
               this.cipher = AesGcmHkdfStreaming.cipherInstance();
            }
         }
      }

      @Override
      public synchronized void decryptSegment(ByteBuffer ciphertext, int segmentNr, boolean isLastSegment, ByteBuffer plaintext) throws GeneralSecurityException {
         GCMParameterSpec params = AesGcmHkdfStreaming.paramsForSegment(this.noncePrefix, (long)segmentNr, isLastSegment);
         this.cipher.init(2, this.keySpec, params);
         this.cipher.doFinal(ciphertext, plaintext);
      }
   }

   class AesGcmHkdfStreamEncrypter implements StreamSegmentEncrypter {
      private final SecretKeySpec keySpec;
      private final Cipher cipher;
      private final byte[] noncePrefix;
      private final ByteBuffer header;
      private long encryptedSegments = 0L;

      public AesGcmHkdfStreamEncrypter(byte[] aad) throws GeneralSecurityException {
         this.cipher = AesGcmHkdfStreaming.cipherInstance();
         this.encryptedSegments = 0L;
         byte[] salt = AesGcmHkdfStreaming.this.randomSalt();
         this.noncePrefix = AesGcmHkdfStreaming.randomNonce();
         this.header = ByteBuffer.allocate(AesGcmHkdfStreaming.this.getHeaderLength());
         this.header.put((byte)AesGcmHkdfStreaming.this.getHeaderLength());
         this.header.put(salt);
         this.header.put(this.noncePrefix);
         AesGcmHkdfStreaming.toBuffer(this.header).flip();
         this.keySpec = AesGcmHkdfStreaming.this.deriveKeySpec(salt, aad);
      }

      @Override
      public ByteBuffer getHeader() {
         return this.header.asReadOnlyBuffer();
      }

      @Override
      public synchronized void encryptSegment(ByteBuffer plaintext, boolean isLastSegment, ByteBuffer ciphertext) throws GeneralSecurityException {
         this.cipher.init(1, this.keySpec, AesGcmHkdfStreaming.paramsForSegment(this.noncePrefix, this.encryptedSegments, isLastSegment));
         this.encryptedSegments++;
         this.cipher.doFinal(plaintext, ciphertext);
      }

      @Override
      public synchronized void encryptSegment(ByteBuffer part1, ByteBuffer part2, boolean isLastSegment, ByteBuffer ciphertext) throws GeneralSecurityException {
         this.cipher.init(1, this.keySpec, AesGcmHkdfStreaming.paramsForSegment(this.noncePrefix, this.encryptedSegments, isLastSegment));
         this.encryptedSegments++;
         if (part2.hasRemaining()) {
            this.cipher.update(part1, ciphertext);
            this.cipher.doFinal(part2, ciphertext);
         } else {
            this.cipher.doFinal(part1, ciphertext);
         }
      }
   }
}
