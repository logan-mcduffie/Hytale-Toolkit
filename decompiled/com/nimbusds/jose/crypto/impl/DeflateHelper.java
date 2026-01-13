package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.DeflateUtils;

@ThreadSafe
public class DeflateHelper {
   public static byte[] applyCompression(JWEHeader jweHeader, byte[] bytes) throws JOSEException {
      CompressionAlgorithm compressionAlg = jweHeader.getCompressionAlgorithm();
      if (compressionAlg == null) {
         return bytes;
      } else if (compressionAlg.equals(CompressionAlgorithm.DEF)) {
         try {
            return DeflateUtils.compress(bytes);
         } catch (Exception var4) {
            throw new JOSEException("Couldn't compress plain text: " + var4.getMessage(), var4);
         }
      } else {
         throw new JOSEException("Unsupported compression algorithm: " + compressionAlg);
      }
   }

   public static byte[] applyDecompression(JWEHeader jweHeader, byte[] bytes) throws JOSEException {
      CompressionAlgorithm compressionAlg = jweHeader.getCompressionAlgorithm();
      if (compressionAlg == null) {
         return bytes;
      } else if (compressionAlg.equals(CompressionAlgorithm.DEF)) {
         try {
            return DeflateUtils.decompress(bytes);
         } catch (Exception var4) {
            throw new JOSEException("Couldn't decompress plain text: " + var4.getMessage(), var4);
         }
      } else {
         throw new JOSEException("Unsupported compression algorithm: " + compressionAlg);
      }
   }
}
