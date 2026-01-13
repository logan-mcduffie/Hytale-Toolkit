package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.TeeOutputStream;

public class CMSAuthenticatedDataGenerator extends CMSAuthenticatedGenerator {
   public CMSAuthenticatedData generate(CMSTypedData var1, MacCalculator var2) throws CMSException {
      return this.generate(var1, var2, null);
   }

   public CMSAuthenticatedData generate(CMSTypedData var1, MacCalculator var2, final DigestCalculator var3) throws CMSException {
      ASN1EncodableVector var6 = CMSUtils.getRecipentInfos(var2.getKey(), this.recipientInfoGenerators);
      AuthenticatedData var7;
      if (var3 != null) {
         BEROctetString var4;
         try {
            ByteArrayOutputStream var8 = new ByteArrayOutputStream();
            TeeOutputStream var9 = new TeeOutputStream(var3.getOutputStream(), var8);
            var1.write(var9);
            var9.close();
            var4 = new BEROctetString(var8.toByteArray());
         } catch (IOException var14) {
            throw new CMSException("unable to perform digest calculation: " + var14.getMessage(), var14);
         }

         Map var17 = Collections.unmodifiableMap(
            this.getBaseParameters(var1.getContentType(), var3.getAlgorithmIdentifier(), var2.getAlgorithmIdentifier(), var3.getDigest())
         );
         if (this.authGen == null) {
            this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
         }

         DERSet var21 = new DERSet(this.authGen.getAttributes(var17).toASN1EncodableVector());

         DEROctetString var5;
         try {
            OutputStream var10 = var2.getOutputStream();
            var10.write(var21.getEncoded("DER"));
            var10.close();
            var5 = new DEROctetString(var2.getMac());
         } catch (IOException var13) {
            throw new CMSException("unable to perform MAC calculation: " + var13.getMessage(), var13);
         }

         ASN1Set var24 = CMSUtils.getAttrBERSet(this.unauthGen);
         ContentInfo var11 = new ContentInfo(var1.getContentType(), var4);
         var7 = new AuthenticatedData(
            this.originatorInfo, new DERSet(var6), var2.getAlgorithmIdentifier(), var3.getAlgorithmIdentifier(), var11, var21, var5, var24
         );
      } else {
         BEROctetString var15;
         DEROctetString var16;
         try {
            ByteArrayOutputStream var18 = new ByteArrayOutputStream();
            TeeOutputStream var22 = new TeeOutputStream(var18, var2.getOutputStream());
            var1.write(var22);
            var22.close();
            var15 = new BEROctetString(var18.toByteArray());
            var16 = new DEROctetString(var2.getMac());
         } catch (IOException var12) {
            throw new CMSException("unable to perform MAC calculation: " + var12.getMessage(), var12);
         }

         ASN1Set var19 = CMSUtils.getAttrBERSet(this.unauthGen);
         ContentInfo var23 = new ContentInfo(var1.getContentType(), var15);
         var7 = new AuthenticatedData(this.originatorInfo, new DERSet(var6), var2.getAlgorithmIdentifier(), null, var23, null, var16, var19);
      }

      ContentInfo var20 = new ContentInfo(CMSObjectIdentifiers.authenticatedData, var7);
      return new CMSAuthenticatedData(var20, new DigestCalculatorProvider() {
         @Override
         public DigestCalculator get(AlgorithmIdentifier var1) throws OperatorCreationException {
            return var3;
         }
      });
   }
}
