package org.bouncycastle.jcajce.spec;

import java.security.spec.ECParameterSpec;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.util.Arrays;

public class DSTU4145ParameterSpec extends ECParameterSpec {
   private final byte[] dke;
   private final ECDomainParameters parameters;

   public DSTU4145ParameterSpec(ECDomainParameters var1) {
      this(var1, EC5Util.convertToSpec(var1), DSTU4145Params.getDefaultDKE());
   }

   private DSTU4145ParameterSpec(ECDomainParameters var1, ECParameterSpec var2, byte[] var3) {
      super(var2.getCurve(), var2.getGenerator(), var2.getOrder(), var2.getCofactor());
      this.parameters = var1;
      this.dke = Arrays.clone(var3);
   }

   public byte[] getDKE() {
      return Arrays.clone(this.dke);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof DSTU4145ParameterSpec) {
         DSTU4145ParameterSpec var2 = (DSTU4145ParameterSpec)var1;
         return this.parameters.equals(var2.parameters);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.parameters.hashCode();
   }
}
