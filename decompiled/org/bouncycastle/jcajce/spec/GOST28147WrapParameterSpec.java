package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class GOST28147WrapParameterSpec implements AlgorithmParameterSpec {
   private byte[] ukm = null;
   private byte[] sBox = null;
   private static Map oidMappings = new HashMap();

   public GOST28147WrapParameterSpec(byte[] var1) {
      this.sBox = new byte[var1.length];
      System.arraycopy(var1, 0, this.sBox, 0, var1.length);
   }

   public GOST28147WrapParameterSpec(byte[] var1, byte[] var2) {
      this(var1);
      this.ukm = new byte[var2.length];
      System.arraycopy(var2, 0, this.ukm, 0, var2.length);
   }

   public GOST28147WrapParameterSpec(String var1) {
      this.sBox = GOST28147Engine.getSBox(var1);
   }

   public GOST28147WrapParameterSpec(String var1, byte[] var2) {
      this(var1);
      this.ukm = new byte[var2.length];
      System.arraycopy(var2, 0, this.ukm, 0, var2.length);
   }

   public GOST28147WrapParameterSpec(ASN1ObjectIdentifier var1, byte[] var2) {
      this(getName(var1));
      this.ukm = Arrays.clone(var2);
   }

   public byte[] getSBox() {
      return Arrays.clone(this.sBox);
   }

   public byte[] getUKM() {
      return Arrays.clone(this.ukm);
   }

   private static String getName(ASN1ObjectIdentifier var0) {
      String var1 = (String)oidMappings.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("unknown OID: " + var0);
      } else {
         return var1;
      }
   }

   static {
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, "E-A");
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet, "E-B");
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet, "E-C");
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet, "E-D");
      oidMappings.put(RosstandartObjectIdentifiers.id_tc26_gost_28147_param_Z, "Param-Z");
   }
}
