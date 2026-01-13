package org.bouncycastle.openssl;

import java.io.IOException;
import org.bouncycastle.operator.OperatorCreationException;

public class PEMEncryptedKeyPair {
   private final String dekAlgName;
   private final byte[] iv;
   private final byte[] keyBytes;
   private final PEMKeyPairParser parser;

   PEMEncryptedKeyPair(String var1, byte[] var2, byte[] var3, PEMKeyPairParser var4) {
      this.dekAlgName = var1;
      this.iv = var2;
      this.keyBytes = var3;
      this.parser = var4;
   }

   public String getDekAlgName() {
      return this.dekAlgName;
   }

   public PEMKeyPair decryptKeyPair(PEMDecryptorProvider var1) throws IOException {
      try {
         PEMDecryptor var2 = var1.get(this.dekAlgName);
         return this.parser.parse(var2.decrypt(this.keyBytes, this.iv));
      } catch (IOException var3) {
         throw var3;
      } catch (OperatorCreationException var4) {
         throw new PEMException("cannot create extraction operator: " + var4.getMessage(), var4);
      } catch (Exception var5) {
         throw new PEMException("exception processing key pair: " + var5.getMessage(), var5);
      }
   }
}
