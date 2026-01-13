package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECNamedDomainParameters extends ECDomainParameters {
   private ASN1ObjectIdentifier name;

   public static ECNamedDomainParameters lookup(ASN1ObjectIdentifier var0) {
      X9ECParameters var1 = CustomNamedCurves.getByOID(var0);
      if (var1 == null) {
         var1 = ECNamedCurveTable.getByOID(var0);
      }

      return new ECNamedDomainParameters(var0, var1);
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, ECCurve var2, ECPoint var3, BigInteger var4) {
      this(var1, var2, var3, var4, ECConstants.ONE, null);
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, ECCurve var2, ECPoint var3, BigInteger var4, BigInteger var5) {
      this(var1, var2, var3, var4, var5, null);
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, ECCurve var2, ECPoint var3, BigInteger var4, BigInteger var5, byte[] var6) {
      super(var2, var3, var4, var5, var6);
      this.name = var1;
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, ECDomainParameters var2) {
      super(var2.getCurve(), var2.getG(), var2.getN(), var2.getH(), var2.getSeed());
      this.name = var1;
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, X9ECParameters var2) {
      super(var2);
      this.name = var1;
   }

   public ASN1ObjectIdentifier getName() {
      return this.name;
   }
}
