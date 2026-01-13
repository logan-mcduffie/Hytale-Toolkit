package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.ArrayUtils;

@Immutable
public final class JWEAlgorithm extends Algorithm {
   private static final long serialVersionUID = 1L;
   @Deprecated
   public static final JWEAlgorithm RSA1_5 = new JWEAlgorithm("RSA1_5", Requirement.REQUIRED);
   @Deprecated
   public static final JWEAlgorithm RSA_OAEP = new JWEAlgorithm("RSA-OAEP", Requirement.OPTIONAL);
   public static final JWEAlgorithm RSA_OAEP_256 = new JWEAlgorithm("RSA-OAEP-256", Requirement.OPTIONAL);
   public static final JWEAlgorithm RSA_OAEP_384 = new JWEAlgorithm("RSA-OAEP-384", Requirement.OPTIONAL);
   public static final JWEAlgorithm RSA_OAEP_512 = new JWEAlgorithm("RSA-OAEP-512", Requirement.OPTIONAL);
   public static final JWEAlgorithm A128KW = new JWEAlgorithm("A128KW", Requirement.RECOMMENDED);
   public static final JWEAlgorithm A192KW = new JWEAlgorithm("A192KW", Requirement.OPTIONAL);
   public static final JWEAlgorithm A256KW = new JWEAlgorithm("A256KW", Requirement.RECOMMENDED);
   public static final JWEAlgorithm DIR = new JWEAlgorithm("dir", Requirement.RECOMMENDED);
   public static final JWEAlgorithm ECDH_ES = new JWEAlgorithm("ECDH-ES", Requirement.RECOMMENDED);
   public static final JWEAlgorithm ECDH_ES_A128KW = new JWEAlgorithm("ECDH-ES+A128KW", Requirement.RECOMMENDED);
   public static final JWEAlgorithm ECDH_ES_A192KW = new JWEAlgorithm("ECDH-ES+A192KW", Requirement.OPTIONAL);
   public static final JWEAlgorithm ECDH_ES_A256KW = new JWEAlgorithm("ECDH-ES+A256KW", Requirement.RECOMMENDED);
   public static final JWEAlgorithm ECDH_1PU = new JWEAlgorithm("ECDH-1PU", Requirement.OPTIONAL);
   public static final JWEAlgorithm ECDH_1PU_A128KW = new JWEAlgorithm("ECDH-1PU+A128KW", Requirement.OPTIONAL);
   public static final JWEAlgorithm ECDH_1PU_A192KW = new JWEAlgorithm("ECDH-1PU+A192KW", Requirement.OPTIONAL);
   public static final JWEAlgorithm ECDH_1PU_A256KW = new JWEAlgorithm("ECDH-1PU+A256KW", Requirement.OPTIONAL);
   public static final JWEAlgorithm A128GCMKW = new JWEAlgorithm("A128GCMKW", Requirement.OPTIONAL);
   public static final JWEAlgorithm A192GCMKW = new JWEAlgorithm("A192GCMKW", Requirement.OPTIONAL);
   public static final JWEAlgorithm A256GCMKW = new JWEAlgorithm("A256GCMKW", Requirement.OPTIONAL);
   public static final JWEAlgorithm PBES2_HS256_A128KW = new JWEAlgorithm("PBES2-HS256+A128KW", Requirement.OPTIONAL);
   public static final JWEAlgorithm PBES2_HS384_A192KW = new JWEAlgorithm("PBES2-HS384+A192KW", Requirement.OPTIONAL);
   public static final JWEAlgorithm PBES2_HS512_A256KW = new JWEAlgorithm("PBES2-HS512+A256KW", Requirement.OPTIONAL);

   public JWEAlgorithm(String name, Requirement req) {
      super(name, req);
   }

   public JWEAlgorithm(String name) {
      super(name, null);
   }

   public static JWEAlgorithm parse(String s) {
      if (s.equals(RSA1_5.getName())) {
         return RSA1_5;
      } else if (s.equals(RSA_OAEP.getName())) {
         return RSA_OAEP;
      } else if (s.equals(RSA_OAEP_256.getName())) {
         return RSA_OAEP_256;
      } else if (s.equals(RSA_OAEP_384.getName())) {
         return RSA_OAEP_384;
      } else if (s.equals(RSA_OAEP_512.getName())) {
         return RSA_OAEP_512;
      } else if (s.equals(A128KW.getName())) {
         return A128KW;
      } else if (s.equals(A192KW.getName())) {
         return A192KW;
      } else if (s.equals(A256KW.getName())) {
         return A256KW;
      } else if (s.equals(DIR.getName())) {
         return DIR;
      } else if (s.equals(ECDH_ES.getName())) {
         return ECDH_ES;
      } else if (s.equals(ECDH_ES_A128KW.getName())) {
         return ECDH_ES_A128KW;
      } else if (s.equals(ECDH_ES_A192KW.getName())) {
         return ECDH_ES_A192KW;
      } else if (s.equals(ECDH_ES_A256KW.getName())) {
         return ECDH_ES_A256KW;
      } else if (s.equals(ECDH_1PU.getName())) {
         return ECDH_1PU;
      } else if (s.equals(ECDH_1PU_A128KW.getName())) {
         return ECDH_1PU_A128KW;
      } else if (s.equals(ECDH_1PU_A192KW.getName())) {
         return ECDH_1PU_A192KW;
      } else if (s.equals(ECDH_1PU_A256KW.getName())) {
         return ECDH_1PU_A256KW;
      } else if (s.equals(A128GCMKW.getName())) {
         return A128GCMKW;
      } else if (s.equals(A192GCMKW.getName())) {
         return A192GCMKW;
      } else if (s.equals(A256GCMKW.getName())) {
         return A256GCMKW;
      } else if (s.equals(PBES2_HS256_A128KW.getName())) {
         return PBES2_HS256_A128KW;
      } else if (s.equals(PBES2_HS384_A192KW.getName())) {
         return PBES2_HS384_A192KW;
      } else {
         return s.equals(PBES2_HS512_A256KW.getName()) ? PBES2_HS512_A256KW : new JWEAlgorithm(s);
      }
   }

   public static final class Family extends AlgorithmFamily<JWEAlgorithm> {
      private static final long serialVersionUID = 1L;
      public static final JWEAlgorithm.Family RSA = new JWEAlgorithm.Family(
         JWEAlgorithm.RSA1_5, JWEAlgorithm.RSA_OAEP, JWEAlgorithm.RSA_OAEP_256, JWEAlgorithm.RSA_OAEP_384, JWEAlgorithm.RSA_OAEP_512
      );
      public static final JWEAlgorithm.Family AES_KW = new JWEAlgorithm.Family(JWEAlgorithm.A128KW, JWEAlgorithm.A192KW, JWEAlgorithm.A256KW);
      public static final JWEAlgorithm.Family ECDH_ES = new JWEAlgorithm.Family(
         JWEAlgorithm.ECDH_ES, JWEAlgorithm.ECDH_ES_A128KW, JWEAlgorithm.ECDH_ES_A192KW, JWEAlgorithm.ECDH_ES_A256KW
      );
      public static final JWEAlgorithm.Family ECDH_1PU = new JWEAlgorithm.Family(
         JWEAlgorithm.ECDH_1PU, JWEAlgorithm.ECDH_1PU_A128KW, JWEAlgorithm.ECDH_1PU_A192KW, JWEAlgorithm.ECDH_1PU_A256KW
      );
      public static final JWEAlgorithm.Family AES_GCM_KW = new JWEAlgorithm.Family(JWEAlgorithm.A128GCMKW, JWEAlgorithm.A192GCMKW, JWEAlgorithm.A256GCMKW);
      public static final JWEAlgorithm.Family PBES2 = new JWEAlgorithm.Family(
         JWEAlgorithm.PBES2_HS256_A128KW, JWEAlgorithm.PBES2_HS384_A192KW, JWEAlgorithm.PBES2_HS512_A256KW
      );
      public static final JWEAlgorithm.Family ASYMMETRIC = new JWEAlgorithm.Family(
         ArrayUtils.concat(RSA.toArray(new JWEAlgorithm[0]), ECDH_ES.toArray(new JWEAlgorithm[0]))
      );
      public static final JWEAlgorithm.Family SYMMETRIC = new JWEAlgorithm.Family(
         ArrayUtils.concat(AES_KW.toArray(new JWEAlgorithm[0]), AES_GCM_KW.toArray(new JWEAlgorithm[0]), new JWEAlgorithm[]{JWEAlgorithm.DIR})
      );

      public Family(JWEAlgorithm... algs) {
         super(algs);
      }
   }
}
