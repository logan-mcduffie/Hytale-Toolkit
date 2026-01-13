package org.bouncycastle.jcajce.provider.asymmetric.edec;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

class Utils {
   static boolean isValidPrefix(byte[] var0, byte[] var1) {
      if (var1.length < var0.length) {
         return !isValidPrefix(var0, var0);
      } else {
         int var2 = 0;

         for (int var3 = 0; var3 != var0.length; var3++) {
            var2 |= var0[var3] ^ var1[var3];
         }

         return var2 == 0;
      }
   }

   static String keyToString(String var0, String var1, AsymmetricKeyParameter var2) {
      StringBuilder var3 = new StringBuilder();
      String var4 = Strings.lineSeparator();
      byte[] var5;
      if (var2 instanceof X448PublicKeyParameters) {
         var5 = ((X448PublicKeyParameters)var2).getEncoded();
      } else if (var2 instanceof Ed448PublicKeyParameters) {
         var5 = ((Ed448PublicKeyParameters)var2).getEncoded();
      } else if (var2 instanceof X25519PublicKeyParameters) {
         var5 = ((X25519PublicKeyParameters)var2).getEncoded();
      } else {
         var5 = ((Ed25519PublicKeyParameters)var2).getEncoded();
      }

      var3.append(var1)
         .append(" ")
         .append(var0)
         .append(" [")
         .append(generateKeyFingerprint(var5))
         .append("]")
         .append(var4)
         .append("    public data: ")
         .append(Hex.toHexString(var5))
         .append(var4);
      return var3.toString();
   }

   private static String generateKeyFingerprint(byte[] var0) {
      return new Fingerprint(var0).toString();
   }
}
