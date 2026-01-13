package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.DHKeyParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;

class Utils {
   static CryptoServiceProperties getDefaultProperties(String var0, ECKeyParameters var1) {
      return new DefaultServiceProperties(var0, ConstraintUtils.bitsOfSecurityFor(var1.getParameters().getCurve()), var1, CryptoServicePurpose.AGREEMENT);
   }

   static CryptoServiceProperties getDefaultProperties(String var0, DHKeyParameters var1) {
      return new DefaultServiceProperties(var0, ConstraintUtils.bitsOfSecurityFor(var1.getParameters().getP()), var1, CryptoServicePurpose.AGREEMENT);
   }

   static CryptoServiceProperties getDefaultProperties(String var0, X448PrivateKeyParameters var1) {
      return new DefaultServiceProperties(var0, 224, var1, CryptoServicePurpose.AGREEMENT);
   }

   static CryptoServiceProperties getDefaultProperties(String var0, X25519PrivateKeyParameters var1) {
      return new DefaultServiceProperties(var0, 128, var1, CryptoServicePurpose.AGREEMENT);
   }
}
