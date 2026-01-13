package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.GOST3410KeyParameters;

class Utils {
   static CryptoServiceProperties getDefaultProperties(String var0, DSAKeyParameters var1, boolean var2) {
      return new DefaultServiceProperties(var0, ConstraintUtils.bitsOfSecurityFor(var1.getParameters().getP()), var1, getPurpose(var2));
   }

   static CryptoServiceProperties getDefaultProperties(String var0, GOST3410KeyParameters var1, boolean var2) {
      return new DefaultServiceProperties(var0, ConstraintUtils.bitsOfSecurityFor(var1.getParameters().getP()), var1, getPurpose(var2));
   }

   static CryptoServiceProperties getDefaultProperties(String var0, ECKeyParameters var1, boolean var2) {
      return new DefaultServiceProperties(var0, ConstraintUtils.bitsOfSecurityFor(var1.getParameters().getCurve()), var1, getPurpose(var2));
   }

   static CryptoServiceProperties getDefaultProperties(String var0, int var1, CipherParameters var2, boolean var3) {
      return new DefaultServiceProperties(var0, var1, var2, getPurpose(var3));
   }

   static CryptoServicePurpose getPurpose(boolean var0) {
      return var0 ? CryptoServicePurpose.SIGNING : CryptoServicePurpose.VERIFYING;
   }
}
