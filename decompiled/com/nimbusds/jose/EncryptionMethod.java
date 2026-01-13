package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.Immutable;

@Immutable
public final class EncryptionMethod extends Algorithm {
   private static final long serialVersionUID = 1L;
   private final int cekBitLength;
   public static final EncryptionMethod A128CBC_HS256 = new EncryptionMethod("A128CBC-HS256", Requirement.REQUIRED, 256);
   public static final EncryptionMethod A192CBC_HS384 = new EncryptionMethod("A192CBC-HS384", Requirement.OPTIONAL, 384);
   public static final EncryptionMethod A256CBC_HS512 = new EncryptionMethod("A256CBC-HS512", Requirement.REQUIRED, 512);
   public static final EncryptionMethod A128CBC_HS256_DEPRECATED = new EncryptionMethod("A128CBC+HS256", Requirement.OPTIONAL, 256);
   public static final EncryptionMethod A256CBC_HS512_DEPRECATED = new EncryptionMethod("A256CBC+HS512", Requirement.OPTIONAL, 512);
   public static final EncryptionMethod A128GCM = new EncryptionMethod("A128GCM", Requirement.RECOMMENDED, 128);
   public static final EncryptionMethod A192GCM = new EncryptionMethod("A192GCM", Requirement.OPTIONAL, 192);
   public static final EncryptionMethod A256GCM = new EncryptionMethod("A256GCM", Requirement.RECOMMENDED, 256);
   public static final EncryptionMethod XC20P = new EncryptionMethod("XC20P", Requirement.OPTIONAL, 256);

   public EncryptionMethod(String name, Requirement req, int cekBitLength) {
      super(name, req);
      this.cekBitLength = cekBitLength;
   }

   public EncryptionMethod(String name, Requirement req) {
      this(name, req, 0);
   }

   public EncryptionMethod(String name) {
      this(name, null, 0);
   }

   public int cekBitLength() {
      return this.cekBitLength;
   }

   public static EncryptionMethod parse(String s) {
      if (s.equals(A128CBC_HS256.getName())) {
         return A128CBC_HS256;
      } else if (s.equals(A192CBC_HS384.getName())) {
         return A192CBC_HS384;
      } else if (s.equals(A256CBC_HS512.getName())) {
         return A256CBC_HS512;
      } else if (s.equals(A128GCM.getName())) {
         return A128GCM;
      } else if (s.equals(A192GCM.getName())) {
         return A192GCM;
      } else if (s.equals(A256GCM.getName())) {
         return A256GCM;
      } else if (s.equals(A128CBC_HS256_DEPRECATED.getName())) {
         return A128CBC_HS256_DEPRECATED;
      } else if (s.equals(A256CBC_HS512_DEPRECATED.getName())) {
         return A256CBC_HS512_DEPRECATED;
      } else {
         return s.equals(XC20P.getName()) ? XC20P : new EncryptionMethod(s);
      }
   }

   public static final class Family extends AlgorithmFamily<EncryptionMethod> {
      private static final long serialVersionUID = 1L;
      public static final EncryptionMethod.Family AES_CBC_HMAC_SHA = new EncryptionMethod.Family(
         EncryptionMethod.A128CBC_HS256, EncryptionMethod.A192CBC_HS384, EncryptionMethod.A256CBC_HS512
      );
      public static final EncryptionMethod.Family AES_GCM = new EncryptionMethod.Family(
         EncryptionMethod.A128GCM, EncryptionMethod.A192GCM, EncryptionMethod.A256GCM
      );

      public Family(EncryptionMethod... encs) {
         super(encs);
      }
   }
}
