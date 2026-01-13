package org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class OpenSSHPublicKeySpec extends EncodedKeySpec {
   private static final String[] allowedTypes = new String[]{"ssh-rsa", "ssh-ed25519", "ssh-dss"};
   private final String type;

   public OpenSSHPublicKeySpec(byte[] var1) {
      super(var1);
      int var2 = 0;
      int var3 = (var1[var2++] & 255) << 24;
      var3 |= (var1[var2++] & 255) << 16;
      var3 |= (var1[var2++] & 255) << 8;
      var3 |= var1[var2++] & 255;
      if (var2 + var3 >= var1.length) {
         throw new IllegalArgumentException("invalid public key blob: type field longer than blob");
      } else {
         this.type = Strings.fromByteArray(Arrays.copyOfRange(var1, var2, var2 + var3));
         if (!this.type.startsWith("ecdsa")) {
            for (int var4 = 0; var4 < allowedTypes.length; var4++) {
               if (allowedTypes[var4].equals(this.type)) {
                  return;
               }
            }

            throw new IllegalArgumentException("unrecognised public key type " + this.type);
         }
      }
   }

   @Override
   public String getFormat() {
      return "OpenSSH";
   }

   public String getType() {
      return this.type;
   }
}
