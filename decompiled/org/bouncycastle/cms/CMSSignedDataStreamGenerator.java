package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CMSSignedDataStreamGenerator extends CMSSignedGenerator {
   private int _bufferSize;

   public CMSSignedDataStreamGenerator() {
   }

   public CMSSignedDataStreamGenerator(DigestAlgorithmIdentifierFinder var1) {
      super(var1);
   }

   public void setBufferSize(int var1) {
      this._bufferSize = var1;
   }

   public OutputStream open(OutputStream var1) throws IOException {
      return this.open(var1, false);
   }

   public OutputStream open(OutputStream var1, boolean var2) throws IOException {
      return this.open(CMSObjectIdentifiers.data, var1, var2);
   }

   public OutputStream open(OutputStream var1, boolean var2, OutputStream var3) throws IOException {
      return this.open(CMSObjectIdentifiers.data, var1, var2, var3);
   }

   public OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, boolean var3) throws IOException {
      return this.open(var1, var2, var3, null);
   }

   public OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, boolean var3, OutputStream var4) throws IOException {
      BERSequenceGenerator var5 = new BERSequenceGenerator(var2);
      var5.addObject((ASN1Primitive)CMSObjectIdentifiers.signedData);
      BERSequenceGenerator var6 = new BERSequenceGenerator(var5.getRawOutputStream(), 0, true);
      var6.addObject((ASN1Primitive)this.calculateVersion(var1));
      HashSet var7 = new HashSet();

      for (SignerInformation var9 : this._signers) {
         CMSUtils.addDigestAlgs(var7, var9, this.digestAlgIdFinder);
      }

      for (SignerInfoGenerator var14 : this.signerGens) {
         var7.add(var14.getDigestAlgorithm());
      }

      var6.getRawOutputStream().write(CMSUtils.convertToDlSet(var7).getEncoded());
      BERSequenceGenerator var13 = new BERSequenceGenerator(var6.getRawOutputStream());
      var13.addObject((ASN1Primitive)var1);
      OutputStream var15 = var3 ? CMSUtils.createBEROctetOutputStream(var13.getRawOutputStream(), 0, true, this._bufferSize) : null;
      OutputStream var10 = CMSUtils.getSafeTeeOutputStream(var4, var15);
      OutputStream var11 = CMSUtils.attachSignersToOutputStream(this.signerGens, var10);
      return new CMSSignedDataStreamGenerator.CmsSignedDataOutputStream(var11, var1, var5, var6, var13);
   }

   public List<AlgorithmIdentifier> getDigestAlgorithms() {
      ArrayList var1 = new ArrayList();

      for (SignerInformation var3 : this._signers) {
         AlgorithmIdentifier var4 = CMSSignedHelper.INSTANCE.fixDigestAlgID(var3.getDigestAlgorithmID(), this.digestAlgIdFinder);
         var1.add(var4);
      }

      for (SignerInfoGenerator var6 : this.signerGens) {
         var1.add(var6.getDigestAlgorithm());
      }

      return var1;
   }

   private ASN1Integer calculateVersion(ASN1ObjectIdentifier var1) {
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      if (this.certs != null) {
         for (Object var7 : this.certs) {
            if (var7 instanceof ASN1TaggedObject) {
               ASN1TaggedObject var8 = (ASN1TaggedObject)var7;
               if (var8.getTagNo() == 1) {
                  var4 = true;
               } else if (var8.getTagNo() == 2) {
                  var5 = true;
               } else if (var8.getTagNo() == 3) {
                  var2 = true;
               }
            }
         }
      }

      if (var2) {
         return new ASN1Integer(5L);
      } else {
         if (this.crls != null) {
            for (Object var10 : this.crls) {
               if (var10 instanceof ASN1TaggedObject) {
                  var3 = true;
               }
            }
         }

         if (var3) {
            return new ASN1Integer(5L);
         } else if (var5) {
            return new ASN1Integer(4L);
         } else if (var4) {
            return new ASN1Integer(3L);
         } else if (checkForVersion3(this._signers, this.signerGens)) {
            return new ASN1Integer(3L);
         } else {
            return !CMSObjectIdentifiers.data.equals(var1) ? new ASN1Integer(3L) : new ASN1Integer(1L);
         }
      }
   }

   private static boolean checkForVersion3(List var0, List var1) {
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         SignerInfo var3 = ((SignerInformation)var2.next()).toASN1Structure();
         if (var3.getVersion().hasValue(3)) {
            return true;
         }
      }

      for (SignerInfoGenerator var5 : var1) {
         if (var5.getGeneratedVersion() == 3) {
            return true;
         }
      }

      return false;
   }

   private class CmsSignedDataOutputStream extends OutputStream {
      private OutputStream _out;
      private ASN1ObjectIdentifier _contentOID;
      private BERSequenceGenerator _sGen;
      private BERSequenceGenerator _sigGen;
      private BERSequenceGenerator _eiGen;

      public CmsSignedDataOutputStream(
         OutputStream nullx, ASN1ObjectIdentifier nullxx, BERSequenceGenerator nullxxx, BERSequenceGenerator nullxxxx, BERSequenceGenerator nullxxxxx
      ) {
         this._out = nullx;
         this._contentOID = nullxx;
         this._sGen = nullxxx;
         this._sigGen = nullxxxx;
         this._eiGen = nullxxxxx;
      }

      @Override
      public void write(int var1) throws IOException {
         this._out.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this._out.write(var1, var2, var3);
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this._out.write(var1);
      }

      @Override
      public void close() throws IOException {
         this._out.close();
         this._eiGen.close();
         CMSSignedDataStreamGenerator.this.digests.clear();
         if (CMSSignedDataStreamGenerator.this.certs.size() != 0) {
            ASN1Set var1 = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.certs);
            this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 0, var1).getEncoded());
         }

         if (CMSSignedDataStreamGenerator.this.crls.size() != 0) {
            ASN1Set var6 = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.crls);
            this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 1, var6).getEncoded());
         }

         ASN1EncodableVector var7 = new ASN1EncodableVector();

         for (SignerInfoGenerator var3 : CMSSignedDataStreamGenerator.this.signerGens) {
            try {
               var7.add(var3.generate(this._contentOID));
               byte[] var4 = var3.getCalculatedDigest();
               CMSSignedDataStreamGenerator.this.digests.put(var3.getDigestAlgorithm().getAlgorithm().getId(), var4);
            } catch (CMSException var5) {
               throw new CMSStreamException("exception generating signers: " + var5.getMessage(), var5);
            }
         }

         for (SignerInformation var9 : CMSSignedDataStreamGenerator.this._signers) {
            var7.add(var9.toASN1Structure());
         }

         this._sigGen.getRawOutputStream().write(new DERSet(var7).getEncoded());
         this._sigGen.close();
         this._sGen.close();
      }
   }
}
