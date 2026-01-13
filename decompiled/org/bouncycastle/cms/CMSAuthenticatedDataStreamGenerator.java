package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.io.TeeOutputStream;

public class CMSAuthenticatedDataStreamGenerator extends CMSAuthenticatedGenerator {
   private int bufferSize;
   private boolean berEncodeRecipientSet;
   private MacCalculator macCalculator;

   public void setBufferSize(int var1) {
      this.bufferSize = var1;
   }

   public void setBEREncodeRecipients(boolean var1) {
      this.berEncodeRecipientSet = var1;
   }

   public OutputStream open(OutputStream var1, MacCalculator var2) throws CMSException {
      return this.open(CMSObjectIdentifiers.data, var1, var2);
   }

   public OutputStream open(OutputStream var1, MacCalculator var2, DigestCalculator var3) throws CMSException {
      return this.open(CMSObjectIdentifiers.data, var1, var2, var3);
   }

   public OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, MacCalculator var3) throws CMSException {
      return this.open(var1, var2, var3, null);
   }

   public OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, MacCalculator var3, DigestCalculator var4) throws CMSException {
      this.macCalculator = var3;

      try {
         ASN1EncodableVector var5 = CMSUtils.getRecipentInfos(var3.getKey(), this.recipientInfoGenerators);
         BERSequenceGenerator var6 = new BERSequenceGenerator(var2);
         var6.addObject((ASN1Primitive)CMSObjectIdentifiers.authenticatedData);
         BERSequenceGenerator var7 = new BERSequenceGenerator(var6.getRawOutputStream(), 0, true);
         var7.addObject((ASN1Primitive)(new ASN1Integer(AuthenticatedData.calculateVersion(this.originatorInfo))));
         CMSUtils.addOriginatorInfoToGenerator(var7, this.originatorInfo);
         CMSUtils.addRecipientInfosToGenerator(var5, var7, this.berEncodeRecipientSet);
         AlgorithmIdentifier var8 = var3.getAlgorithmIdentifier();
         var7.getRawOutputStream().write(var8.getEncoded());
         if (var4 != null) {
            var7.addObject((ASN1Primitive)(new DERTaggedObject(false, 1, var4.getAlgorithmIdentifier())));
         }

         BERSequenceGenerator var9 = new BERSequenceGenerator(var7.getRawOutputStream());
         var9.addObject((ASN1Primitive)var1);
         OutputStream var10 = CMSUtils.createBEROctetOutputStream(var9.getRawOutputStream(), 0, true, this.bufferSize);
         TeeOutputStream var11;
         if (var4 != null) {
            var11 = new TeeOutputStream(var10, var4.getOutputStream());
         } else {
            var11 = new TeeOutputStream(var10, var3.getOutputStream());
         }

         return new CMSAuthenticatedDataStreamGenerator.CmsAuthenticatedDataOutputStream(var3, var4, var1, var11, var6, var7, var9);
      } catch (IOException var12) {
         throw new CMSException("exception decoding algorithm parameters.", var12);
      }
   }

   private class CmsAuthenticatedDataOutputStream extends OutputStream {
      private OutputStream dataStream;
      private BERSequenceGenerator cGen;
      private BERSequenceGenerator envGen;
      private BERSequenceGenerator eiGen;
      private MacCalculator macCalculator;
      private DigestCalculator digestCalculator;
      private ASN1ObjectIdentifier contentType;

      public CmsAuthenticatedDataOutputStream(
         MacCalculator nullx,
         DigestCalculator nullxx,
         ASN1ObjectIdentifier nullxxx,
         OutputStream nullxxxx,
         BERSequenceGenerator nullxxxxx,
         BERSequenceGenerator nullxxxxxx,
         BERSequenceGenerator nullxxxxxxx
      ) {
         this.macCalculator = nullx;
         this.digestCalculator = nullxx;
         this.contentType = nullxxx;
         this.dataStream = nullxxxx;
         this.cGen = nullxxxxx;
         this.envGen = nullxxxxxx;
         this.eiGen = nullxxxxxxx;
      }

      @Override
      public void write(int var1) throws IOException {
         this.dataStream.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.dataStream.write(var1, var2, var3);
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this.dataStream.write(var1);
      }

      @Override
      public void close() throws IOException {
         this.dataStream.close();
         this.eiGen.close();
         Map var1;
         if (this.digestCalculator != null) {
            var1 = Collections.unmodifiableMap(
               CMSAuthenticatedDataStreamGenerator.this.getBaseParameters(
                  this.contentType,
                  this.digestCalculator.getAlgorithmIdentifier(),
                  this.macCalculator.getAlgorithmIdentifier(),
                  this.digestCalculator.getDigest()
               )
            );
            if (CMSAuthenticatedDataStreamGenerator.this.authGen == null) {
               CMSAuthenticatedDataStreamGenerator.this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
            }

            DERSet var2 = new DERSet(CMSAuthenticatedDataStreamGenerator.this.authGen.getAttributes(var1).toASN1EncodableVector());
            OutputStream var3 = this.macCalculator.getOutputStream();
            var3.write(var2.getEncoded("DER"));
            var3.close();
            this.envGen.addObject((ASN1Primitive)(new DERTaggedObject(false, 2, var2)));
         } else {
            var1 = Collections.EMPTY_MAP;
         }

         this.envGen.addObject((ASN1Primitive)(new DEROctetString(this.macCalculator.getMac())));
         CMSUtils.addAttriSetToGenerator(this.envGen, CMSAuthenticatedDataStreamGenerator.this.unauthGen, 3, var1);
         this.envGen.close();
         this.cGen.close();
      }
   }
}
