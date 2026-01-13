package org.bouncycastle.dvcs;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformation;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Arrays;

public class DVCSRequestInfo {
   private DVCSRequestInformation data;

   public DVCSRequestInfo(byte[] var1) {
      this(DVCSRequestInformation.getInstance(var1));
   }

   public DVCSRequestInfo(DVCSRequestInformation var1) {
      this.data = var1;
   }

   public DVCSRequestInformation toASN1Structure() {
      return this.data;
   }

   public int getVersion() {
      return this.data.getVersion();
   }

   public int getServiceType() {
      return this.data.getService().getValue().intValue();
   }

   public BigInteger getNonce() {
      return this.data.getNonce();
   }

   public Date getRequestTime() throws DVCSParsingException {
      DVCSTime var1 = this.data.getRequestTime();
      if (var1 == null) {
         return null;
      } else {
         try {
            if (var1.getGenTime() != null) {
               return var1.getGenTime().getDate();
            } else {
               TimeStampToken var2 = new TimeStampToken(var1.getTimeStampToken());
               return var2.getTimeStampInfo().getGenTime();
            }
         } catch (Exception var3) {
            throw new DVCSParsingException("unable to extract time: " + var3.getMessage(), var3);
         }
      }
   }

   public GeneralNames getRequester() {
      return this.data.getRequester();
   }

   public PolicyInformation getRequestPolicy() {
      return this.data.getRequestPolicy() != null ? this.data.getRequestPolicy() : null;
   }

   public GeneralNames getDVCSNames() {
      return this.data.getDVCS();
   }

   public GeneralNames getDataLocations() {
      return this.data.getDataLocations();
   }

   public static boolean validate(DVCSRequestInfo var0, DVCSRequestInfo var1) {
      DVCSRequestInformation var2 = var0.data;
      DVCSRequestInformation var3 = var1.data;
      if (var2.getVersion() != var3.getVersion()) {
         return false;
      } else if (!clientEqualsServer(var2.getService(), var3.getService())) {
         return false;
      } else if (!clientEqualsServer(var2.getRequestTime(), var3.getRequestTime())) {
         return false;
      } else if (!clientEqualsServer(var2.getRequestPolicy(), var3.getRequestPolicy())) {
         return false;
      } else if (!clientEqualsServer(var2.getExtensions(), var3.getExtensions())) {
         return false;
      } else {
         if (var2.getNonce() != null) {
            if (var3.getNonce() == null) {
               return false;
            }

            byte[] var4 = var2.getNonce().toByteArray();
            byte[] var5 = var3.getNonce().toByteArray();
            if (var5.length < var4.length) {
               return false;
            }

            if (!Arrays.areEqual(var4, Arrays.copyOfRange(var5, 0, var4.length))) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean clientEqualsServer(Object var0, Object var1) {
      return var0 == null && var1 == null || var0 != null && var0.equals(var1);
   }
}
