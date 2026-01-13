package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.x509.DigestInfo;

public class MessageImprint {
   private final DigestInfo messageImprint;

   public MessageImprint(DigestInfo var1) {
      this.messageImprint = var1;
   }

   public DigestInfo toASN1Structure() {
      return this.messageImprint;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return var1 instanceof MessageImprint ? this.messageImprint.equals(((MessageImprint)var1).messageImprint) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.messageImprint.hashCode();
   }
}
