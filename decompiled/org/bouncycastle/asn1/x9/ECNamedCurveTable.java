package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.anssi.ANSSINamedCurves;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.internal.asn1.cryptlib.CryptlibObjectIdentifiers;

public class ECNamedCurveTable {
   public static X9ECParameters getByName(String var0) {
      X9ECParameters var1 = X962NamedCurves.getByName(var0);
      if (var1 == null) {
         var1 = SECNamedCurves.getByName(var0);
      }

      if (var1 == null) {
         var1 = NISTNamedCurves.getByName(var0);
      }

      if (var1 == null) {
         var1 = TeleTrusTNamedCurves.getByName(var0);
      }

      if (var1 == null) {
         var1 = ANSSINamedCurves.getByName(var0);
      }

      if (var1 == null) {
         var1 = ECGOST3410NamedCurves.getByNameX9(var0);
      }

      if (var1 == null) {
         var1 = GMNamedCurves.getByName(var0);
      }

      return var1;
   }

   public static X9ECParametersHolder getByNameLazy(String var0) {
      X9ECParametersHolder var1 = X962NamedCurves.getByNameLazy(var0);
      if (null == var1) {
         var1 = SECNamedCurves.getByNameLazy(var0);
      }

      if (null == var1) {
         var1 = NISTNamedCurves.getByNameLazy(var0);
      }

      if (null == var1) {
         var1 = TeleTrusTNamedCurves.getByNameLazy(var0);
      }

      if (null == var1) {
         var1 = ANSSINamedCurves.getByNameLazy(var0);
      }

      if (null == var1) {
         var1 = ECGOST3410NamedCurves.getByNameLazy(var0);
      }

      if (null == var1) {
         var1 = GMNamedCurves.getByNameLazy(var0);
      }

      return var1;
   }

   public static ASN1ObjectIdentifier getOID(String var0) {
      ASN1ObjectIdentifier var1 = X962NamedCurves.getOID(var0);
      if (var1 == null) {
         var1 = SECNamedCurves.getOID(var0);
      }

      if (var1 == null) {
         var1 = NISTNamedCurves.getOID(var0);
      }

      if (var1 == null) {
         var1 = TeleTrusTNamedCurves.getOID(var0);
      }

      if (var1 == null) {
         var1 = ANSSINamedCurves.getOID(var0);
      }

      if (var1 == null) {
         var1 = ECGOST3410NamedCurves.getOID(var0);
      }

      if (var1 == null) {
         var1 = GMNamedCurves.getOID(var0);
      }

      if (var1 == null && var0.equals("curve25519")) {
         var1 = CryptlibObjectIdentifiers.curvey25519;
      }

      return var1;
   }

   public static String getName(ASN1ObjectIdentifier var0) {
      String var1 = X962NamedCurves.getName(var0);
      if (var1 == null) {
         var1 = SECNamedCurves.getName(var0);
      }

      if (var1 == null) {
         var1 = NISTNamedCurves.getName(var0);
      }

      if (var1 == null) {
         var1 = TeleTrusTNamedCurves.getName(var0);
      }

      if (var1 == null) {
         var1 = ANSSINamedCurves.getName(var0);
      }

      if (var1 == null) {
         var1 = ECGOST3410NamedCurves.getName(var0);
      }

      if (var1 == null) {
         var1 = GMNamedCurves.getName(var0);
      }

      if (var1 == null) {
         var1 = CustomNamedCurves.getName(var0);
      }

      return var1;
   }

   public static X9ECParameters getByOID(ASN1ObjectIdentifier var0) {
      X9ECParameters var1 = X962NamedCurves.getByOID(var0);
      if (var1 == null) {
         var1 = SECNamedCurves.getByOID(var0);
      }

      if (var1 == null) {
         var1 = TeleTrusTNamedCurves.getByOID(var0);
      }

      if (var1 == null) {
         var1 = ANSSINamedCurves.getByOID(var0);
      }

      if (var1 == null) {
         var1 = ECGOST3410NamedCurves.getByOIDX9(var0);
      }

      if (var1 == null) {
         var1 = GMNamedCurves.getByOID(var0);
      }

      return var1;
   }

   public static X9ECParametersHolder getByOIDLazy(ASN1ObjectIdentifier var0) {
      X9ECParametersHolder var1 = X962NamedCurves.getByOIDLazy(var0);
      if (null == var1) {
         var1 = SECNamedCurves.getByOIDLazy(var0);
      }

      if (null == var1) {
         var1 = TeleTrusTNamedCurves.getByOIDLazy(var0);
      }

      if (null == var1) {
         var1 = ANSSINamedCurves.getByOIDLazy(var0);
      }

      if (null == var1) {
         var1 = ECGOST3410NamedCurves.getByOIDLazy(var0);
      }

      if (null == var1) {
         var1 = GMNamedCurves.getByOIDLazy(var0);
      }

      return var1;
   }

   public static Enumeration getNames() {
      Vector var0 = new Vector();
      addEnumeration(var0, X962NamedCurves.getNames());
      addEnumeration(var0, SECNamedCurves.getNames());
      addEnumeration(var0, NISTNamedCurves.getNames());
      addEnumeration(var0, TeleTrusTNamedCurves.getNames());
      addEnumeration(var0, ANSSINamedCurves.getNames());
      addEnumeration(var0, ECGOST3410NamedCurves.getNames());
      addEnumeration(var0, GMNamedCurves.getNames());
      return var0.elements();
   }

   private static void addEnumeration(Vector var0, Enumeration var1) {
      while (var1.hasMoreElements()) {
         var0.addElement(var1.nextElement());
      }
   }
}
