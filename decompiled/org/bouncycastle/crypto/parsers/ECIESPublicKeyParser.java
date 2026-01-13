package org.bouncycastle.crypto.parsers;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.io.Streams;

public class ECIESPublicKeyParser implements KeyParser {
   private ECDomainParameters ecParams;

   public ECIESPublicKeyParser(ECDomainParameters var1) {
      this.ecParams = var1;
   }

   @Override
   public AsymmetricKeyParameter readKey(InputStream var1) throws IOException {
      int var2 = var1.read();
      if (var2 < 0) {
         throw new EOFException();
      } else {
         boolean var3;
         switch (var2) {
            case 0:
               throw new IOException("Sender's public key invalid.");
            case 1:
            case 5:
            default:
               throw new IOException("Sender's public key has invalid point encoding 0x" + Integer.toString(var2, 16));
            case 2:
            case 3:
               var3 = true;
               break;
            case 4:
            case 6:
            case 7:
               var3 = false;
         }

         ECCurve var4 = this.ecParams.getCurve();
         int var5 = var4.getAffinePointEncodingLength(var3);
         byte[] var6 = new byte[var5];
         var6[0] = (byte)var2;
         int var7 = var5 - 1;
         if (Streams.readFully(var1, var6, 1, var7) != var7) {
            throw new EOFException();
         } else {
            return new ECPublicKeyParameters(var4.decodePoint(var6), this.ecParams);
         }
      }
   }
}
