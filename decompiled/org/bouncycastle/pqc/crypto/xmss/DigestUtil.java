package org.bouncycastle.pqc.crypto.xmss;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

class DigestUtil {
   private static Map<String, ASN1ObjectIdentifier> nameToOid = new HashMap<>();
   private static Map<ASN1ObjectIdentifier, String> oidToName = new HashMap<>();

   static Digest getDigest(ASN1ObjectIdentifier var0) {
      if (var0.equals(NISTObjectIdentifiers.id_sha256)) {
         return new SHA256Digest();
      } else if (var0.equals(NISTObjectIdentifiers.id_sha512)) {
         return new SHA512Digest();
      } else if (var0.equals(NISTObjectIdentifiers.id_shake128)) {
         return new SHAKEDigest(128);
      } else if (var0.equals(NISTObjectIdentifiers.id_shake256)) {
         return new SHAKEDigest(256);
      } else {
         throw new IllegalArgumentException("unrecognized digest OID: " + var0);
      }
   }

   static String getDigestName(ASN1ObjectIdentifier var0) {
      String var1 = oidToName.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw new IllegalArgumentException("unrecognized digest oid: " + var0);
      }
   }

   static ASN1ObjectIdentifier getDigestOID(String var0) {
      ASN1ObjectIdentifier var1 = nameToOid.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw new IllegalArgumentException("unrecognized digest name: " + var0);
      }
   }

   static {
      nameToOid.put("SHA-256", NISTObjectIdentifiers.id_sha256);
      nameToOid.put("SHA-512", NISTObjectIdentifiers.id_sha512);
      nameToOid.put("SHAKE128", NISTObjectIdentifiers.id_shake128);
      nameToOid.put("SHAKE256", NISTObjectIdentifiers.id_shake256);
      oidToName.put(NISTObjectIdentifiers.id_sha256, "SHA-256");
      oidToName.put(NISTObjectIdentifiers.id_sha512, "SHA-512");
      oidToName.put(NISTObjectIdentifiers.id_shake128, "SHAKE128");
      oidToName.put(NISTObjectIdentifiers.id_shake256, "SHAKE256");
   }
}
