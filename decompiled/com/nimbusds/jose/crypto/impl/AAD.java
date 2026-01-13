package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class AAD {
   public static byte[] compute(JWEHeader jweHeader) {
      return compute(jweHeader.toBase64URL());
   }

   public static byte[] compute(Base64URL encodedJWEHeader) {
      return encodedJWEHeader.toString().getBytes(StandardCharsets.US_ASCII);
   }

   public static byte[] computeLength(byte[] aad) throws IntegerOverflowException {
      int bitLength = ByteUtils.safeBitLength(aad);
      return ByteBuffer.allocate(8).putLong(bitLength).array();
   }
}
