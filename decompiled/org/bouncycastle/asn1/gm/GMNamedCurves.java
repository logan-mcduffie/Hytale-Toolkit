package org.bouncycastle.asn1.gm;

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

public class GMNamedCurves {
   static X9ECParametersHolder sm2p256v1 = new X9ECParametersHolder() {
      @Override
      protected ECCurve createCurve() {
         BigInteger var1 = GMNamedCurves.fromHex("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF");
         BigInteger var2 = GMNamedCurves.fromHex("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC");
         BigInteger var3 = GMNamedCurves.fromHex("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93");
         BigInteger var4 = GMNamedCurves.fromHex("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123");
         BigInteger var5 = BigInteger.valueOf(1L);
         return GMNamedCurves.configureCurve(new ECCurve.Fp(var1, var2, var3, var4, var5, true));
      }

      @Override
      protected X9ECParameters createParameters() {
         Object var1 = null;
         ECCurve var2 = this.getCurve();
         X9ECPoint var3 = GMNamedCurves.configureBasepoint(
            var2, "0432C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"
         );
         return new X9ECParameters(var2, var3, var2.getOrder(), var2.getCofactor(), (byte[])var1);
      }
   };
   static X9ECParametersHolder wapip192v1 = new X9ECParametersHolder() {
      @Override
      protected ECCurve createCurve() {
         BigInteger var1 = GMNamedCurves.fromHex("BDB6F4FE3E8B1D9E0DA8C0D46F4C318CEFE4AFE3B6B8551F");
         BigInteger var2 = GMNamedCurves.fromHex("BB8E5E8FBC115E139FE6A814FE48AAA6F0ADA1AA5DF91985");
         BigInteger var3 = GMNamedCurves.fromHex("1854BEBDC31B21B7AEFC80AB0ECD10D5B1B3308E6DBF11C1");
         BigInteger var4 = GMNamedCurves.fromHex("BDB6F4FE3E8B1D9E0DA8C0D40FC962195DFAE76F56564677");
         BigInteger var5 = BigInteger.valueOf(1L);
         return GMNamedCurves.configureCurve(new ECCurve.Fp(var1, var2, var3, var4, var5, true));
      }

      @Override
      protected X9ECParameters createParameters() {
         Object var1 = null;
         ECCurve var2 = this.getCurve();
         X9ECPoint var3 = GMNamedCurves.configureBasepoint(
            var2, "044AD5F7048DE709AD51236DE65E4D4B482C836DC6E410664002BB3A02D4AAADACAE24817A4CA3A1B014B5270432DB27D2"
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
      defineCurve("wapip192v1", GMObjectIdentifiers.wapip192v1, wapip192v1);
      defineCurve("wapi192v1", GMObjectIdentifiers.wapi192v1, wapip192v1);
      defineCurve("sm2p256v1", GMObjectIdentifiers.sm2p256v1, sm2p256v1);
   }
}
