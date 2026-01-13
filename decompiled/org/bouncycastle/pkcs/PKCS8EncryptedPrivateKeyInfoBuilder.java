package org.bouncycastle.pkcs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.operator.OutputEncryptor;

public class PKCS8EncryptedPrivateKeyInfoBuilder {
   private PrivateKeyInfo privateKeyInfo;

   public PKCS8EncryptedPrivateKeyInfoBuilder(byte[] var1) {
      this(PrivateKeyInfo.getInstance(var1));
   }

   public PKCS8EncryptedPrivateKeyInfoBuilder(PrivateKeyInfo var1) {
      this.privateKeyInfo = var1;
   }

   public PKCS8EncryptedPrivateKeyInfo build(OutputEncryptor var1) {
      try {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         OutputStream var3 = var1.getOutputStream(var2);
         var3.write(this.privateKeyInfo.getEncoded());
         var3.close();
         return new PKCS8EncryptedPrivateKeyInfo(new EncryptedPrivateKeyInfo(var1.getAlgorithmIdentifier(), var2.toByteArray()));
      } catch (IOException var4) {
         throw new IllegalStateException("cannot encode privateKeyInfo");
      }
   }
}
