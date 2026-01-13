package org.bouncycastle.jce.provider;

import java.util.Date;

class CertStatus {
   public static final int UNREVOKED = 11;
   public static final int UNDETERMINED = 12;
   int certStatus = 11;
   Date revocationDate = null;

   public Date getRevocationDate() {
      return this.revocationDate;
   }

   public void setRevocationDate(Date var1) {
      this.revocationDate = var1;
   }

   public int getCertStatus() {
      return this.certStatus;
   }

   public void setCertStatus(int var1) {
      this.certStatus = var1;
   }
}
