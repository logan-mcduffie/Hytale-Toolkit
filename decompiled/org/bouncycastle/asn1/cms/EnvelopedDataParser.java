package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1Util;

public class EnvelopedDataParser {
   private ASN1SequenceParser _seq;
   private ASN1Integer _version;
   private ASN1Encodable _nextObject;
   private boolean _originatorInfoCalled;

   public EnvelopedDataParser(ASN1SequenceParser var1) throws IOException {
      this._seq = var1;
      this._version = ASN1Integer.getInstance(var1.readObject());
   }

   public ASN1Integer getVersion() {
      return this._version;
   }

   public OriginatorInfo getOriginatorInfo() throws IOException {
      this._originatorInfoCalled = true;
      if (this._nextObject == null) {
         this._nextObject = this._seq.readObject();
      }

      if (this._nextObject instanceof ASN1TaggedObjectParser) {
         ASN1TaggedObjectParser var1 = (ASN1TaggedObjectParser)this._nextObject;
         if (var1.hasContextTag(0)) {
            ASN1SequenceParser var2 = (ASN1SequenceParser)var1.parseBaseUniversal(false, 16);
            this._nextObject = null;
            return OriginatorInfo.getInstance(var2.getLoadedObject());
         }
      }

      return null;
   }

   public ASN1SetParser getRecipientInfos() throws IOException {
      if (!this._originatorInfoCalled) {
         this.getOriginatorInfo();
      }

      if (this._nextObject == null) {
         this._nextObject = this._seq.readObject();
      }

      ASN1SetParser var1 = (ASN1SetParser)this._nextObject;
      this._nextObject = null;
      return var1;
   }

   public EncryptedContentInfoParser getEncryptedContentInfo() throws IOException {
      if (this._nextObject == null) {
         this._nextObject = this._seq.readObject();
      }

      if (this._nextObject != null) {
         ASN1SequenceParser var1 = (ASN1SequenceParser)this._nextObject;
         this._nextObject = null;
         return new EncryptedContentInfoParser(var1);
      } else {
         return null;
      }
   }

   public ASN1SetParser getUnprotectedAttrs() throws IOException {
      if (this._nextObject == null) {
         this._nextObject = this._seq.readObject();
      }

      if (this._nextObject != null) {
         ASN1TaggedObjectParser var1 = (ASN1TaggedObjectParser)this._nextObject;
         this._nextObject = null;
         return (ASN1SetParser)ASN1Util.parseContextBaseUniversal(var1, 1, false, 17);
      } else {
         return null;
      }
   }
}
