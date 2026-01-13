package org.bouncycastle.asn1.anssi;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class ANSSINamedCurves {
   static X9ECParametersHolder FRP256v1 = new X9ECParametersHolder() {
      @Override
      protected ECCurve createCurve() {
         BigInteger var1 = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C03");
         BigInteger var2 = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C00");
         BigInteger var3 = ANSSINamedCurves.fromHex("EE353FCA5428A9300D4ABA754A44C00FDFEC0C9AE4B1A1803075ED967B7BB73F");
         BigInteger var4 = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B53DC67E140D2BF941FFDD459C6D655E1");
         BigInteger var5 = BigInteger.valueOf(1L);
         return ANSSINamedCurves.configureCurve(new ECCurve.Fp(var1, var2, var3, var4, var5, true));
      }

      @Override
      protected X9ECParameters createParameters() {
         Object var1 = null;
         ECCurve var2 = this.createCurve();
         X9ECPoint var3 = ANSSINamedCurves.configureBasepoint(
            var2, "04B6B3D4C356C139EB31183D4749D423958C27D2DCAF98B70164C97A2DD98F5CFF6142E0F7C8B204911F9271F0F3ECEF8C2701C307E8E4C9E183115A1554062CFB"
         );
         return new X9ECParameters(var2, var3, var2.getOrder(), var2.getCofactor(), (byte[])var1);
      }
   };
   static final Hashtable objIds = new Hashtable();
   static final Hashtable curves = new Hashtable();
   static final Hashtable names = new Hashtable();

   private static X9ECPoint configureBasepoint(ECCurve var0, String var1) {
      X9ECPoint var2 = new X9ECPoint(var0, Hex.decodeStrict(var1));
      WNafUtil.configureBasepoint(var2.getPoint());
      return var2;
   }

   private static ECCurve configureCurve(ECCurve var0) {
      return var0;
   }

   private static BigInteger fromHex(String var0) {
      return new BigInteger(1, Hex.decodeStrict(var0));
   }

   static void defineCurve(String var0, ASN1ObjectIdentifier var1, X9ECParametersHolder var2) {
      objIds.put(Strings.toLowerCase(var0), var1);
      names.put(var1, var0);
      curves.put(var1, var2);
   }

   public static X9ECParameters getByName(String var0) {
      ASN1ObjectIdentifier var1 = getOID(var0);
      return var1 == null ? null : getByOID(var1);
   }

   public static X9ECParametersHolder getByNameLazy(String var0) {
      ASN1ObjectIdentifier var1 = getOID(var0);
      return var1 == null ? null : getByOIDLazy(var1);
   }

   public static X9ECParameters getByOID(ASN1ObjectIdentifier var0) {
      X9ECParametersHolder var1 = getByOIDLazy(var0);
      return var1 == null ? null : var1.getParameters();
   }

   public static X9ECParametersHolder getByOIDLazy(ASN1ObjectIdentifier var0) {
      return (X9ECParametersHolder)curves.get(var0);
   }

   public static ASN1ObjectIdentifier getOID(String var0) {
      return (ASN1ObjectIdentifier)objIds.get(Strings.toLowerCase(var0));
   }

   public static String getName(ASN1ObjectIdentifier var0) {
      return (String)names.get(var0);
   }

   public static Enumeration getNames() {
      return names.elements();
   }

   static {
      defineCurve("FRP256v1", ANSSIObjectIdentifiers.FRP256v1, FRP256v1);
   }
}
