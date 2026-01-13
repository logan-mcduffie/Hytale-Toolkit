package org.bouncycastle.jce;

import java.io.IOException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;

public class PKCS12Util {
   public static byte[] convertToDefiniteLength(byte[] var0) throws IOException {
      Pfx var1 = Pfx.getInstance(var0);
      return var1.getEncoded("DER");
   }

   public static byte[] convertToDefiniteLength(byte[] var0, char[] var1, String var2) throws IOException {
      Pfx var3 = Pfx.getInstance(var0);
      ContentInfo var4 = var3.getAuthSafe();
      ASN1OctetString var5 = ASN1OctetString.getInstance(var4.getContent());
      ASN1Primitive var6 = ASN1Primitive.fromByteArray(var5.getOctets());
      byte[] var7 = var6.getEncoded("DER");
      var4 = new ContentInfo(var4.getContentType(), new DEROctetString(var7));
      MacData var8 = var3.getMacData();

      try {
         int var9 = var8.getIterationCount().intValue();
         byte[] var10 = ASN1OctetString.getInstance(var4.getContent()).getOctets();
         byte[] var11 = calculatePbeMac(var8.getMac().getAlgorithmId().getAlgorithm(), var8.getSalt(), var9, var1, var10, var2);
         AlgorithmIdentifier var12 = new AlgorithmIdentifier(var8.getMac().getAlgorithmId().getAlgorithm(), DERNull.INSTANCE);
         DigestInfo var13 = new DigestInfo(var12, var11);
         var8 = new MacData(var13, var8.getSalt(), var9);
      } catch (Exception var14) {
         throw new IOException("error constructing MAC: " + var14.toString());
      }

      var3 = new Pfx(var4, var8);
      return var3.getEncoded("DER");
   }

   private static byte[] calculatePbeMac(ASN1ObjectIdentifier var0, byte[] var1, int var2, char[] var3, byte[] var4, String var5) throws Exception {
      SecretKeyFactory var6 = SecretKeyFactory.getInstance(var0.getId(), var5);
      PBEParameterSpec var7 = new PBEParameterSpec(var1, var2);
      PBEKeySpec var8 = new PBEKeySpec(var3);
      SecretKey var9 = var6.generateSecret(var8);
      Mac var10 = Mac.getInstance(var0.getId(), var5);
      var10.init(var9, var7);
      var10.update(var4);
      return var10.doFinal();
   }
}
