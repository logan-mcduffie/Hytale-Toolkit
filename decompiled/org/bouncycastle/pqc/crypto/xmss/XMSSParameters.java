package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Integers;

public final class XMSSParameters {
   private static final Map<Integer, XMSSParameters> paramsLookupTable;
   private final XMSSOid oid;
   private final int height;
   private final int k;
   private final ASN1ObjectIdentifier treeDigestOID;
   private final int winternitzParameter;
   private final String treeDigest;
   private final int treeDigestSize;
   private final WOTSPlusParameters wotsPlusParams;

   public XMSSParameters(int var1, Digest var2) {
      this(var1, DigestUtil.getDigestOID(var2.getAlgorithmName()));
   }

   public XMSSParameters(int var1, ASN1ObjectIdentifier var2) {
      if (var1 < 2) {
         throw new IllegalArgumentException("height must be >= 2");
      } else if (var2 == null) {
         throw new NullPointerException("digest == null");
      } else {
         this.height = var1;
         this.k = this.determineMinK();
         this.treeDigest = DigestUtil.getDigestName(var2);
         this.treeDigestOID = var2;
         this.wotsPlusParams = new WOTSPlusParameters(var2);
         this.treeDigestSize = this.wotsPlusParams.getTreeDigestSize();
         this.winternitzParameter = this.wotsPlusParams.getWinternitzParameter();
         this.oid = DefaultXMSSOid.lookup(this.treeDigest, this.treeDigestSize, this.winternitzParameter, this.wotsPlusParams.getLen(), var1);
      }
   }

   private int determineMinK() {
      for (int var1 = 2; var1 <= this.height; var1++) {
         if ((this.height - var1) % 2 == 0) {
            return var1;
         }
      }

      throw new IllegalStateException("should never happen...");
   }

   public int getTreeDigestSize() {
      return this.treeDigestSize;
   }

   public ASN1ObjectIdentifier getTreeDigestOID() {
      return this.treeDigestOID;
   }

   public int getHeight() {
      return this.height;
   }

   String getTreeDigest() {
      return this.treeDigest;
   }

   int getLen() {
      return this.wotsPlusParams.getLen();
   }

   int getWinternitzParameter() {
      return this.winternitzParameter;
   }

   WOTSPlus getWOTSPlus() {
      return new WOTSPlus(this.wotsPlusParams);
   }

   XMSSOid getOid() {
      return this.oid;
   }

   int getK() {
      return this.k;
   }

   public static XMSSParameters lookupByOID(int var0) {
      return paramsLookupTable.get(Integers.valueOf(var0));
   }

   static {
      HashMap var0 = new HashMap();
      var0.put(Integers.valueOf(1), new XMSSParameters(10, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(2), new XMSSParameters(16, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(3), new XMSSParameters(20, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(4), new XMSSParameters(10, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(5), new XMSSParameters(16, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(6), new XMSSParameters(20, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(7), new XMSSParameters(10, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(8), new XMSSParameters(16, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(9), new XMSSParameters(20, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(10), new XMSSParameters(10, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(11), new XMSSParameters(16, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(12), new XMSSParameters(20, NISTObjectIdentifiers.id_shake256));
      paramsLookupTable = Collections.unmodifiableMap(var0);
   }
}
