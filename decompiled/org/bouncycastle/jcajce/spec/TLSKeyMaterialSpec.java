package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

public class TLSKeyMaterialSpec implements KeySpec {
   public static final String MASTER_SECRET = "master secret";
   public static final String KEY_EXPANSION = "key expansion";
   private final byte[] secret;
   private final String label;
   private final int length;
   private final byte[] seed;

   public TLSKeyMaterialSpec(byte[] var1, String var2, int var3, byte[]... var4) {
      this.secret = Arrays.clone(var1);
      this.label = var2;
      this.length = var3;
      this.seed = Arrays.concatenate(var4);
   }

   public String getLabel() {
      return this.label;
   }

   public int getLength() {
      return this.length;
   }

   public byte[] getSecret() {
      return Arrays.clone(this.secret);
   }

   public byte[] getSeed() {
      return Arrays.clone(this.seed);
   }
}
