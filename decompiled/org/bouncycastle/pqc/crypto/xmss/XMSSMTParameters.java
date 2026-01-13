package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Integers;

public final class XMSSMTParameters {
   private static final Map<Integer, XMSSMTParameters> paramsLookupTable;
   private final XMSSOid oid;
   private final XMSSParameters xmssParams;
   private final int height;
   private final int layers;

   public XMSSMTParameters(int var1, int var2, Digest var3) {
      this(var1, var2, DigestUtil.getDigestOID(var3.getAlgorithmName()));
   }

   public XMSSMTParameters(int var1, int var2, ASN1ObjectIdentifier var3) {
      this.height = var1;
      this.layers = var2;
      this.xmssParams = new XMSSParameters(xmssTreeHeight(var1, var2), var3);
      this.oid = DefaultXMSSMTOid.lookup(this.getTreeDigest(), this.getTreeDigestSize(), this.getWinternitzParameter(), this.getLen(), this.getHeight(), var2);
   }

   private static int xmssTreeHeight(int var0, int var1) throws IllegalArgumentException {
      if (var0 < 2) {
         throw new IllegalArgumentException("totalHeight must be > 1");
      } else if (var0 % var1 != 0) {
         throw new IllegalArgumentException("layers must divide totalHeight without remainder");
      } else if (var0 / var1 == 1) {
         throw new IllegalArgumentException("height / layers must be greater than 1");
      } else {
         return var0 / var1;
      }
   }

   public int getHeight() {
      return this.height;
   }

   public int getLayers() {
      return this.layers;
   }

   protected XMSSParameters getXMSSParameters() {
      return this.xmssParams;
   }

   protected WOTSPlus getWOTSPlus() {
      return this.xmssParams.getWOTSPlus();
   }

   protected String getTreeDigest() {
      return this.xmssParams.getTreeDigest();
   }

   public int getTreeDigestSize() {
      return this.xmssParams.getTreeDigestSize();
   }

   public ASN1ObjectIdentifier getTreeDigestOID() {
      return this.xmssParams.getTreeDigestOID();
   }

   int getWinternitzParameter() {
      return this.xmssParams.getWinternitzParameter();
   }

   protected int getLen() {
      return this.xmssParams.getLen();
   }

   protected XMSSOid getOid() {
      return this.oid;
   }

   public static XMSSMTParameters lookupByOID(int var0) {
      return paramsLookupTable.get(Integers.valueOf(var0));
   }

   static {
      HashMap var0 = new HashMap();
      var0.put(Integers.valueOf(1), new XMSSMTParameters(20, 2, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(2), new XMSSMTParameters(20, 4, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(3), new XMSSMTParameters(40, 2, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(4), new XMSSMTParameters(40, 4, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(5), new XMSSMTParameters(40, 8, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(6), new XMSSMTParameters(60, 3, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(7), new XMSSMTParameters(60, 6, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(8), new XMSSMTParameters(60, 12, NISTObjectIdentifiers.id_sha256));
      var0.put(Integers.valueOf(9), new XMSSMTParameters(20, 2, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(10), new XMSSMTParameters(20, 4, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(11), new XMSSMTParameters(40, 2, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(12), new XMSSMTParameters(40, 4, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(13), new XMSSMTParameters(40, 8, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(14), new XMSSMTParameters(60, 3, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(15), new XMSSMTParameters(60, 6, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(16), new XMSSMTParameters(60, 12, NISTObjectIdentifiers.id_sha512));
      var0.put(Integers.valueOf(17), new XMSSMTParameters(20, 2, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(18), new XMSSMTParameters(20, 4, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(19), new XMSSMTParameters(40, 2, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(20), new XMSSMTParameters(40, 4, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(21), new XMSSMTParameters(40, 8, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(22), new XMSSMTParameters(60, 3, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(23), new XMSSMTParameters(60, 6, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(24), new XMSSMTParameters(60, 12, NISTObjectIdentifiers.id_shake128));
      var0.put(Integers.valueOf(25), new XMSSMTParameters(20, 2, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(26), new XMSSMTParameters(20, 4, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(27), new XMSSMTParameters(40, 2, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(28), new XMSSMTParameters(40, 4, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(29), new XMSSMTParameters(40, 8, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(30), new XMSSMTParameters(60, 3, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(31), new XMSSMTParameters(60, 6, NISTObjectIdentifiers.id_shake256));
      var0.put(Integers.valueOf(32), new XMSSMTParameters(60, 12, NISTObjectIdentifiers.id_shake256));
      paramsLookupTable = Collections.unmodifiableMap(var0);
   }
}
