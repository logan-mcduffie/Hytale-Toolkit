package org.bouncycastle.util.test;

import org.bouncycastle.util.encoders.Hex;

public class TestRandomData extends FixedSecureRandom {
   public TestRandomData(String var1) {
      super(new FixedSecureRandom.Source[]{new FixedSecureRandom.Data(Hex.decode(var1))});
   }

   public TestRandomData(byte[] var1) {
      super(new FixedSecureRandom.Source[]{new FixedSecureRandom.Data(var1)});
   }
}
