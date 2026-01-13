package org.bouncycastle.pkix.jcajce;

import org.bouncycastle.asn1.x509.ReasonFlags;

class ReasonsMask {
   private int _reasons;
   static final ReasonsMask allReasons = new ReasonsMask(33023);

   ReasonsMask(ReasonFlags var1) {
      this._reasons = var1.intValue();
   }

   private ReasonsMask(int var1) {
      this._reasons = var1;
   }

   ReasonsMask() {
      this(0);
   }

   void addReasons(ReasonsMask var1) {
      this._reasons = this._reasons | var1.getReasons();
   }

   boolean isAllReasons() {
      return this._reasons == allReasons._reasons;
   }

   ReasonsMask intersect(ReasonsMask var1) {
      ReasonsMask var2 = new ReasonsMask();
      var2.addReasons(new ReasonsMask(this._reasons & var1.getReasons()));
      return var2;
   }

   boolean hasNewReasons(ReasonsMask var1) {
      return (this._reasons | var1.getReasons() ^ this._reasons) != 0;
   }

   int getReasons() {
      return this._reasons;
   }
}
