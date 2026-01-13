package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataStreamGenerator extends CMSEnvelopedGenerator {
   private int _bufferSize;
   private boolean _berEncodeRecipientSet;

   public void setBufferSize(int var1) {
      this._bufferSize = var1;
   }

   public void setBEREncodeRecipients(boolean var1) {
      this._berEncodeRecipientSet = var1;
   }

   private ASN1Integer getVersion(ASN1EncodableVector var1) {
      return this.unprotectedAttributeGenerator != null
         ? new ASN1Integer(EnvelopedData.calculateVersion(this.originatorInfo, new DLSet(var1), new DLSet()))
         : new ASN1Integer(EnvelopedData.calculateVersion(this.originatorInfo, new DLSet(var1), null));
   }

   private OutputStream doOpen(ASN1ObjectIdentifier var1, OutputStream var2, OutputEncryptor var3) throws IOException, CMSException {
      ASN1EncodableVector var4 = CMSUtils.getRecipentInfos(var3.getKey(), this.recipientInfoGenerators);
      return this.open(var1, var2, var4, var3);
   }

   protected OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, ASN1EncodableVector var3, OutputEncryptor var4) throws IOException {
      BERSequenceGenerator var5 = new BERSequenceGenerator(var2);
      var5.addObject((ASN1Primitive)CMSObjectIdentifiers.envelopedData);
      BERSequenceGenerator var6 = new BERSequenceGenerator(var5.getRawOutputStream(), 0, true);
      var6.addObject((ASN1Primitive)this.getVersion(var3));
      CMSUtils.addOriginatorInfoToGenerator(var6, this.originatorInfo);
      CMSUtils.addRecipientInfosToGenerator(var3, var6, this._berEncodeRecipientSet);
      BERSequenceGenerator var7 = new BERSequenceGenerator(var6.getRawOutputStream());
      var7.addObject((ASN1Primitive)var1);
      AlgorithmIdentifier var8 = var4.getAlgorithmIdentifier();
      var7.getRawOutputStream().write(var8.getEncoded());
      OutputStream var9 = CMSUtils.createBEROctetOutputStream(var7.getRawOutputStream(), 0, false, this._bufferSize);
      return new CMSEnvelopedDataStreamGenerator.CmsEnvelopedDataOutputStream(var4, var9, var5, var6, var7);
   }

   protected OutputStream open(OutputStream var1, ASN1EncodableVector var2, OutputEncryptor var3) throws CMSException {
      try {
         return this.open(CMSObjectIdentifiers.data, var1, var2, var3);
      } catch (IOException var5) {
         throw new CMSException("exception decoding algorithm parameters.", var5);
      }
   }

   public OutputStream open(OutputStream var1, OutputEncryptor var2) throws CMSException, IOException {
      return this.doOpen(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), var1, var2);
   }

   public OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, OutputEncryptor var3) throws CMSException, IOException {
      return this.doOpen(var1, var2, var3);
   }

   private class CmsEnvelopedDataOutputStream extends OutputStream {
      private final OutputEncryptor _encryptor;
      private final OutputStream _cOut;
      private OutputStream _octetStream;
      private BERSequenceGenerator _cGen;
      private BERSequenceGenerator _envGen;
      private BERSequenceGenerator _eiGen;

      public CmsEnvelopedDataOutputStream(
         OutputEncryptor nullx, OutputStream nullxx, BERSequenceGenerator nullxxx, BERSequenceGenerator nullxxxx, BERSequenceGenerator nullxxxxx
      ) {
         this._encryptor = nullx;
         this._octetStream = nullxx;
         this._cOut = nullx.getOutputStream(nullxx);
         this._cGen = nullxxx;
         this._envGen = nullxxxx;
         this._eiGen = nullxxxxx;
      }

      @Override
      public void write(int var1) throws IOException {
         this._cOut.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this._cOut.write(var1, var2, var3);
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this._cOut.write(var1);
      }

      @Override
      public void close() throws IOException {
         this._cOut.close();
         if (this._encryptor instanceof OutputAEADEncryptor) {
            this._octetStream.write(((OutputAEADEncryptor)this._encryptor).getMAC());
            this._octetStream.close();
         }

         this._eiGen.close();
         CMSUtils.addAttriSetToGenerator(this._envGen, CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator, 1, Collections.EMPTY_MAP);
         this._envGen.close();
         this._cGen.close();
      }
   }
}
