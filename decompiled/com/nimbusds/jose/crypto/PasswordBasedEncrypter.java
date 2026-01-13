package com.nimbusds.jose.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AESKW;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.crypto.impl.PBKDF2;
import com.nimbusds.jose.crypto.impl.PRFParams;
import com.nimbusds.jose.crypto.impl.PasswordBasedCryptoProvider;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Arrays;
import javax.crypto.SecretKey;

@ThreadSafe
public class PasswordBasedEncrypter extends PasswordBasedCryptoProvider implements JWEEncrypter {
   public static final int MIN_SALT_LENGTH = 8;
   private final int saltLength;
   public static final int MIN_RECOMMENDED_ITERATION_COUNT = 1000;
   private final int iterationCount;

   public PasswordBasedEncrypter(byte[] password, int saltLength, int iterationCount) {
      super(password);
      if (saltLength < 8) {
         throw new IllegalArgumentException("The minimum salt length (p2s) is 8 bytes");
      } else {
         this.saltLength = saltLength;
         if (iterationCount < 1000) {
            throw new IllegalArgumentException("The minimum recommended iteration count (p2c) is 1000");
         } else {
            this.iterationCount = iterationCount;
         }
      }
   }

   public PasswordBasedEncrypter(String password, int saltLength, int iterationCount) {
      this(password.getBytes(StandardCharset.UTF_8), saltLength, iterationCount);
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
      EncryptionMethod enc = header.getEncryptionMethod();
      byte[] salt = new byte[this.saltLength];
      this.getJCAContext().getSecureRandom().nextBytes(salt);
      byte[] formattedSalt = PBKDF2.formatSalt(alg, salt);
      PRFParams prfParams = PRFParams.resolve(alg, this.getJCAContext().getMACProvider());
      SecretKey psKey = PBKDF2.deriveKey(this.getPassword(), formattedSalt, this.iterationCount, prfParams, this.getJCAContext().getProvider());
      JWEHeader updatedHeader = new JWEHeader.Builder(header).pbes2Salt(Base64URL.encode(salt)).pbes2Count(this.iterationCount).build();
      SecretKey cek = ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
      Base64URL encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, psKey, this.getJCAContext().getKeyEncryptionProvider()));
      byte[] updatedAAD = Arrays.equals(AAD.compute(header), aad) ? AAD.compute(updatedHeader) : aad;
      return ContentCryptoProvider.encrypt(updatedHeader, clearText, updatedAAD, cek, encryptedKey, this.getJCAContext());
   }

   public int getSaltLength() {
      return this.saltLength;
   }

   public int getIterationCount() {
      return this.iterationCount;
   }
}
