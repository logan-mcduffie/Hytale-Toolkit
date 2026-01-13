package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.proto.EcdsaSignatureEncoding;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.Enums;
import com.google.protobuf.ByteString;
import java.math.BigInteger;
import java.security.GeneralSecurityException;

public final class SigUtil {
   static final String INVALID_PARAMS = "Invalid ECDSA parameters";

   public static Enums.HashType toHashType(HashType hash) throws GeneralSecurityException {
      switch (hash) {
         case SHA256:
            return Enums.HashType.SHA256;
         case SHA384:
            return Enums.HashType.SHA384;
         case SHA512:
            return Enums.HashType.SHA512;
         default:
            throw new GeneralSecurityException("unsupported hash type: " + hash.name());
      }
   }

   public static EllipticCurves.CurveType toCurveType(EllipticCurveType type) throws GeneralSecurityException {
      switch (type) {
         case NIST_P256:
            return EllipticCurves.CurveType.NIST_P256;
         case NIST_P384:
            return EllipticCurves.CurveType.NIST_P384;
         case NIST_P521:
            return EllipticCurves.CurveType.NIST_P521;
         default:
            throw new GeneralSecurityException("unknown curve type: " + type.name());
      }
   }

   public static EllipticCurves.EcdsaEncoding toEcdsaEncoding(EcdsaSignatureEncoding encoding) throws GeneralSecurityException {
      switch (encoding) {
         case IEEE_P1363:
            return EllipticCurves.EcdsaEncoding.IEEE_P1363;
         case DER:
            return EllipticCurves.EcdsaEncoding.DER;
         default:
            throw new GeneralSecurityException("unknown ECDSA encoding: " + encoding.name());
      }
   }

   public static ByteString toUnsignedIntByteString(BigInteger i) {
      byte[] twosComplement = i.toByteArray();
      return twosComplement[0] == 0 ? ByteString.copyFrom(twosComplement, 1, twosComplement.length - 1) : ByteString.copyFrom(twosComplement);
   }

   private SigUtil() {
   }
}
