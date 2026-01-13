package org.bouncycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class QCStatement extends ASN1Object implements ETSIQCObjectIdentifiers, RFC3739QCObjectIdentifiers {
   ASN1ObjectIdentifier qcStatementId;
   ASN1Encodable qcStatementInfo;

   public static QCStatement getInstance(Object var0) {
      if (var0 instanceof QCStatement) {
         return (QCStatement)var0;
      } else {
         return var0 != null ? new QCStatement(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private QCStatement(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.qcStatementId = ASN1ObjectIdentifier.getInstance(var2.nextElement());
      if (var2.hasMoreElements()) {
         this.qcStatementInfo = (ASN1Encodable)var2.nextElement();
      }
   }

   public QCStatement(ASN1ObjectIdentifier var1) {
      this.qcStatementId = var1;
      this.qcStatementInfo = null;
   }

   public QCStatement(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.qcStatementId = var1;
      this.qcStatementInfo = var2;
   }

   public ASN1ObjectIdentifier getStatementId() {
      return this.qcStatementId;
   }

   public ASN1Encodable getStatementInfo() {
      return this.qcStatementInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.qcStatementId);
      if (this.qcStatementInfo != null) {
         var1.add(this.qcStatementInfo);
      }

      return new DERSequence(var1);
   }
}
