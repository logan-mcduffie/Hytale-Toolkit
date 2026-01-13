package org.bouncycastle.pkcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Strings;

class MacDataGenerator {
   private PKCS12MacCalculatorBuilder builder;

   MacDataGenerator(PKCS12MacCalculatorBuilder var1) {
      this.builder = var1;
   }

   public MacData build(char[] var1, byte[] var2) throws PKCSException {
      MacCalculator var3;
      try {
         var3 = this.builder.build(var1);
         OutputStream var4 = var3.getOutputStream();
         var4.write(var2);
         var4.close();
      } catch (Exception var9) {
         throw new PKCSException("unable to process data: " + var9.getMessage(), var9);
      }

      AlgorithmIdentifier var10 = var3.getAlgorithmIdentifier();
      DigestInfo var5 = new DigestInfo(this.builder.getDigestAlgorithmIdentifier(), var3.getMac());
      byte[] var6;
      int var7;
      if (PKCSObjectIdentifiers.id_PBMAC1.equals(var5.getAlgorithmId().getAlgorithm())) {
         var6 = Strings.toUTF8ByteArray("NOT USED".toCharArray());
         var7 = 1;
      } else {
         PKCS12PBEParams var8 = PKCS12PBEParams.getInstance(var10.getParameters());
         var6 = var8.getIV();
         var7 = var8.getIterations().intValue();
      }

      return new MacData(var5, var6, var7);
   }
}
