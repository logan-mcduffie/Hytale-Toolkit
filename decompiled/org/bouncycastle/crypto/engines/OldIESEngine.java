package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.util.Pack;

public class OldIESEngine extends IESEngine {
   public OldIESEngine(BasicAgreement var1, DerivationFunction var2, Mac var3) {
      super(var1, var2, var3);
   }

   public OldIESEngine(BasicAgreement var1, DerivationFunction var2, Mac var3, BufferedBlockCipher var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected byte[] getLengthTag(byte[] var1) {
      byte[] var2 = new byte[4];
      if (var1 != null) {
         Pack.intToBigEndian(var1.length * 8, var2, 0);
      }

      return var2;
   }
}
