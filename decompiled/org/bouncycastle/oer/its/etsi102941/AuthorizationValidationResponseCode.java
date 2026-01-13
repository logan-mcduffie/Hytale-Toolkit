package org.bouncycastle.oer.its.etsi102941;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;

public class AuthorizationValidationResponseCode extends ASN1Enumerated {
   public static final AuthorizationValidationResponseCode ok = new AuthorizationValidationResponseCode(0);
   public static final AuthorizationValidationResponseCode cantparse = new AuthorizationValidationResponseCode(1);
   public static final AuthorizationValidationResponseCode badcontenttype = new AuthorizationValidationResponseCode(2);
   public static final AuthorizationValidationResponseCode imnottherecipient = new AuthorizationValidationResponseCode(3);
   public static final AuthorizationValidationResponseCode unknownencryptionalgorithm = new AuthorizationValidationResponseCode(4);
   public static final AuthorizationValidationResponseCode decryptionfailed = new AuthorizationValidationResponseCode(5);
   public static final AuthorizationValidationResponseCode invalidaa = new AuthorizationValidationResponseCode(6);
   public static final AuthorizationValidationResponseCode invalidaasignature = new AuthorizationValidationResponseCode(7);
   public static final AuthorizationValidationResponseCode wrongea = new AuthorizationValidationResponseCode(8);
   public static final AuthorizationValidationResponseCode unknownits = new AuthorizationValidationResponseCode(9);
   public static final AuthorizationValidationResponseCode invalidsignature = new AuthorizationValidationResponseCode(10);
   public static final AuthorizationValidationResponseCode invalidencryptionkey = new AuthorizationValidationResponseCode(11);
   public static final AuthorizationValidationResponseCode deniedpermissions = new AuthorizationValidationResponseCode(12);
   public static final AuthorizationValidationResponseCode deniedtoomanycerts = new AuthorizationValidationResponseCode(13);
   public static final AuthorizationValidationResponseCode deniedrequest = new AuthorizationValidationResponseCode(14);

   public AuthorizationValidationResponseCode(int var1) {
      super(var1);
      this.assertValues();
   }

   public AuthorizationValidationResponseCode(BigInteger var1) {
      super(var1);
      this.assertValues();
   }

   public AuthorizationValidationResponseCode(byte[] var1) {
      super(var1);
      this.assertValues();
   }

   private AuthorizationValidationResponseCode(ASN1Enumerated var1) {
      super(var1.getValue());
      this.assertValues();
   }

   protected void assertValues() {
      if (this.getValue().intValue() < 0 || this.getValue().intValue() > 14) {
         throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
      }
   }

   public static AuthorizationValidationResponseCode getInstance(Object var0) {
      if (var0 instanceof AuthorizationValidationResponseCode) {
         return (AuthorizationValidationResponseCode)var0;
      } else {
         return var0 != null ? new AuthorizationValidationResponseCode(ASN1Enumerated.getInstance(var0)) : null;
      }
   }
}
