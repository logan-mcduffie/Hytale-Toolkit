package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.operator.OutputCompressor;

public class CMSCompressedDataStreamGenerator {
   public static final String ZLIB = CMSObjectIdentifiers.zlibCompress.getId();
   private int _bufferSize;

   public void setBufferSize(int var1) {
      this._bufferSize = var1;
   }

   public OutputStream open(OutputStream var1, OutputCompressor var2) throws IOException {
      return this.open(CMSObjectIdentifiers.data, var1, var2);
   }

   public OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, OutputCompressor var3) throws IOException {
      BERSequenceGenerator var4 = new BERSequenceGenerator(var2);
      var4.addObject((ASN1Primitive)CMSObjectIdentifiers.compressedData);
      BERSequenceGenerator var5 = new BERSequenceGenerator(var4.getRawOutputStream(), 0, true);
      var5.addObject((ASN1Primitive)(new ASN1Integer(0L)));
      var5.addObject(var3.getAlgorithmIdentifier());
      BERSequenceGenerator var6 = new BERSequenceGenerator(var5.getRawOutputStream());
      var6.addObject((ASN1Primitive)var1);
      OutputStream var7 = CMSUtils.createBEROctetOutputStream(var6.getRawOutputStream(), 0, true, this._bufferSize);
      return new CMSCompressedDataStreamGenerator.CmsCompressedOutputStream(var3.getOutputStream(var7), var4, var5, var6);
   }

   private static class CmsCompressedOutputStream extends OutputStream {
      private OutputStream _out;
      private BERSequenceGenerator _sGen;
      private BERSequenceGenerator _cGen;
      private BERSequenceGenerator _eiGen;

      CmsCompressedOutputStream(OutputStream var1, BERSequenceGenerator var2, BERSequenceGenerator var3, BERSequenceGenerator var4) {
         this._out = var1;
         this._sGen = var2;
         this._cGen = var3;
         this._eiGen = var4;
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
         this._cGen.close();
         this._sGen.close();
      }
   }
}
