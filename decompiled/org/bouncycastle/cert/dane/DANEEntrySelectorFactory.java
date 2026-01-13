package org.bouncycastle.cert.dane;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class DANEEntrySelectorFactory {
   private final DigestCalculator digestCalculator;

   public DANEEntrySelectorFactory(DigestCalculator var1) {
      this.digestCalculator = var1;
   }

   public DANEEntrySelector createSelector(String var1) throws DANEException {
      byte[] var2 = Strings.toUTF8ByteArray(var1.substring(0, var1.indexOf(64)));

      try {
         OutputStream var3 = this.digestCalculator.getOutputStream();
         var3.write(var2);
         var3.close();
      } catch (IOException var5) {
         throw new DANEException("Unable to calculate digest string: " + var5.getMessage(), var5);
      }

      byte[] var6 = this.digestCalculator.getDigest();
      String var4 = Strings.fromByteArray(Hex.encode(var6)) + "._smimecert." + var1.substring(var1.indexOf(64) + 1);
      return new DANEEntrySelector(var4);
   }
}
