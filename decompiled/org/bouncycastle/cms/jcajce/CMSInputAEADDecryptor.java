package org.bouncycastle.cms.jcajce;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.InputStreamWithMAC;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputAEADDecryptor;

class CMSInputAEADDecryptor implements InputAEADDecryptor {
   private final AlgorithmIdentifier contentEncryptionAlgorithm;
   private final Cipher dataCipher;
   private InputStream inputStream;

   CMSInputAEADDecryptor(AlgorithmIdentifier var1, Cipher var2) {
      this.contentEncryptionAlgorithm = var1;
      this.dataCipher = var2;
   }

   @Override
   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.contentEncryptionAlgorithm;
   }

   @Override
   public InputStream getInputStream(InputStream var1) {
      this.inputStream = var1;
      return new CipherInputStream(var1, this.dataCipher);
   }

   @Override
   public OutputStream getAADStream() {
      return checkForAEAD() ? new JceAADStream(this.dataCipher) : null;
   }

   @Override
   public byte[] getMAC() {
      return this.inputStream instanceof InputStreamWithMAC ? ((InputStreamWithMAC)this.inputStream).getMAC() : null;
   }

   private static boolean checkForAEAD() {
      return AccessController.<Boolean>doPrivileged(new PrivilegedAction() {
         @Override
         public Object run() {
            try {
               return Cipher.class.getMethod("updateAAD", byte[].class) != null;
            } catch (Exception var2) {
               return Boolean.FALSE;
            }
         }
      });
   }
}
