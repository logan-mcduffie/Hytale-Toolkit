package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CompressedDataParser {
   private ASN1Integer _version;
   private AlgorithmIdentifier _compressionAlgorithm;
   private ContentInfoParser _encapContentInfo;

   public CompressedDataParser(ASN1SequenceParser var1) throws IOException {
      this._version = (ASN1Integer)var1.readObject();
      this._compressionAlgorithm = AlgorithmIdentifier.getInstance(var1.readObject().toASN1Primitive());
      this._encapContentInfo = new ContentInfoParser((ASN1SequenceParser)var1.readObject());
   }

   public ASN1Integer getVersion() {
      return this._version;
   }

   public AlgorithmIdentifier getCompressionAlgorithmIdentifier() {
      return this._compressionAlgorithm;
   }

   public ContentInfoParser getEncapContentInfo() {
      return this._encapContentInfo;
   }
}
