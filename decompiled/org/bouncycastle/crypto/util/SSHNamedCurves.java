package org.bouncycastle.crypto.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Strings;

public class SSHNamedCurves {
   private static final Map<ASN1ObjectIdentifier, String> oidToName = Collections.unmodifiableMap(new HashMap<ASN1ObjectIdentifier, String>() {
      {
         for (String var2 : SSHNamedCurves.oidMap.keySet()) {
            this.put(SSHNamedCurves.oidMap.get(var2), var2);
         }
      }
   });
   private static final Map<String, ASN1ObjectIdentifier> oidMap = Collections.unmodifiableMap(new HashMap<String, ASN1ObjectIdentifier>() {
      {
         this.put("nistp256", SECObjectIdentifiers.secp256r1);
         this.put("nistp384", SECObjectIdentifiers.secp384r1);
         this.put("nistp521", SECObjectIdentifiers.secp521r1);
         this.put("nistk163", SECObjectIdentifiers.sect163k1);
         this.put("nistp192", SECObjectIdentifiers.secp192r1);
         this.put("nistp224", SECObjectIdentifiers.secp224r1);
         this.put("nistk233", SECObjectIdentifiers.sect233k1);
         this.put("nistb233", SECObjectIdentifiers.sect233r1);
         this.put("nistk283", SECObjectIdentifiers.sect283k1);
         this.put("nistk409", SECObjectIdentifiers.sect409k1);
         this.put("nistb409", SECObjectIdentifiers.sect409r1);
         this.put("nistt571", SECObjectIdentifiers.sect571k1);
      }
   });
   private static final Map<String, String> curveNameToSSHName = Collections.unmodifiableMap(
      new HashMap<String, String>() {
         {
            String[][] var1 = new String[][]{
               {"secp256r1", "nistp256"},
               {"secp384r1", "nistp384"},
               {"secp521r1", "nistp521"},
               {"sect163k1", "nistk163"},
               {"secp192r1", "nistp192"},
               {"secp224r1", "nistp224"},
               {"sect233k1", "nistk233"},
               {"sect233r1", "nistb233"},
               {"sect283k1", "nistk283"},
               {"sect409k1", "nistk409"},
               {"sect409r1", "nistb409"},
               {"sect571k1", "nistt571"}
            };

            for (int var2 = 0; var2 != var1.length; var2++) {
               String[] var3 = var1[var2];
               this.put(var3[0], var3[1]);
            }
         }
      }
   );
   private static HashMap<ECCurve, String> curveMap = new HashMap<ECCurve, String>() {
      {
         Enumeration var1 = CustomNamedCurves.getNames();

         while (var1.hasMoreElements()) {
            String var2 = (String)var1.nextElement();
            ECCurve var3 = CustomNamedCurves.getByNameLazy(var2).getCurve();
            this.put(var3, var2);
         }
      }
   };

   public static ASN1ObjectIdentifier getByName(String var0) {
      return oidMap.get(var0);
   }

   public static X9ECParameters getParameters(String var0) {
      return NISTNamedCurves.getByOID(oidMap.get(Strings.toLowerCase(var0)));
   }

   public static X9ECParameters getParameters(ASN1ObjectIdentifier var0) {
      return NISTNamedCurves.getByOID(var0);
   }

   public static String getName(ASN1ObjectIdentifier var0) {
      return oidToName.get(var0);
   }

   public static String getNameForParameters(ECDomainParameters var0) {
      return var0 instanceof ECNamedDomainParameters ? getName(((ECNamedDomainParameters)var0).getName()) : getNameForParameters(var0.getCurve());
   }

   public static String getNameForParameters(ECCurve var0) {
      return curveNameToSSHName.get(curveMap.get(var0));
   }
}
