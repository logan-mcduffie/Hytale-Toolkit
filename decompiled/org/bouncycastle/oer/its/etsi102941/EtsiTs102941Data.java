package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;

public class EtsiTs102941Data extends ASN1Object {
   private final Version version;
   private final EtsiTs102941DataContent content;

   public EtsiTs102941Data(Version var1, EtsiTs102941DataContent var2) {
      this.version = var1;
      this.content = var2;
   }

   private EtsiTs102941Data(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.version = Version.getInstance(var1.getObjectAt(0));
         this.content = EtsiTs102941DataContent.getInstance(var1.getObjectAt(1));
      }
   }

   public static EtsiTs102941Data getInstance(Object var0) {
      if (var0 instanceof EtsiTs102941Data) {
         return (EtsiTs102941Data)var0;
      } else if (var0 != null) {
         return var0 instanceof Opaque
            ? new EtsiTs102941Data(ASN1Sequence.getInstance(((Opaque)var0).getContent()))
            : new EtsiTs102941Data(ASN1Sequence.getInstance(var0));
      } else {
         return null;
      }
   }

   public Version getVersion() {
      return this.version;
   }

   public EtsiTs102941DataContent getContent() {
      return this.content;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.version, this.content});
   }
}
