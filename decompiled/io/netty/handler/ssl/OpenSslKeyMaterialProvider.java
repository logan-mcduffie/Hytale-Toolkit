package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.internal.tcnative.SSL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509KeyManager;

class OpenSslKeyMaterialProvider {
   private final X509KeyManager keyManager;
   private final String password;

   OpenSslKeyMaterialProvider(X509KeyManager keyManager, String password) {
      this.keyManager = keyManager;
      this.password = password;
   }

   static void validateKeyMaterialSupported(X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, boolean allowSignatureFallback) throws SSLException {
      validateSupported(keyCertChain);
      validateSupported(key, keyPassword, allowSignatureFallback);
   }

   private static void validateSupported(PrivateKey key, String password, boolean allowSignatureFallback) throws SSLException {
      if (key != null) {
         if (key.getEncoded() != null || !allowSignatureFallback) {
            long pkeyBio = 0L;
            long pkey = 0L;

            try {
               pkeyBio = ReferenceCountedOpenSslContext.toBIO(UnpooledByteBufAllocator.DEFAULT, key);
               pkey = SSL.parsePrivateKey(pkeyBio, password);
            } catch (Exception var11) {
               throw new SSLException("PrivateKey type not supported " + key.getFormat(), var11);
            } finally {
               SSL.freeBIO(pkeyBio);
               if (pkey != 0L) {
                  SSL.freePrivateKey(pkey);
               }
            }
         }
      }
   }

   private static void validateSupported(X509Certificate[] certificates) throws SSLException {
      if (certificates != null && certificates.length != 0) {
         long chainBio = 0L;
         long chain = 0L;
         PemEncoded encoded = null;

         try {
            encoded = PemX509Certificate.toPEM(UnpooledByteBufAllocator.DEFAULT, true, certificates);
            chainBio = ReferenceCountedOpenSslContext.toBIO(UnpooledByteBufAllocator.DEFAULT, encoded.retain());
            chain = SSL.parseX509Chain(chainBio);
         } catch (Exception var10) {
            throw new SSLException("Certificate type not supported", var10);
         } finally {
            SSL.freeBIO(chainBio);
            if (chain != 0L) {
               SSL.freeX509Chain(chain);
            }

            if (encoded != null) {
               encoded.release();
            }
         }
      }
   }

   X509KeyManager keyManager() {
      return this.keyManager;
   }

   OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator allocator, String alias) throws Exception {
      X509Certificate[] certificates = this.keyManager.getCertificateChain(alias);
      if (certificates != null && certificates.length != 0) {
         PrivateKey key = this.keyManager.getPrivateKey(alias);
         PemEncoded encoded = PemX509Certificate.toPEM(allocator, true, certificates);
         long chainBio = 0L;
         long pkeyBio = 0L;
         long chain = 0L;
         long pkey = 0L;

         Object var15;
         try {
            chainBio = ReferenceCountedOpenSslContext.toBIO(allocator, encoded.retain());
            chain = SSL.parseX509Chain(chainBio);
            OpenSslKeyMaterial keyMaterial;
            if (key instanceof OpenSslPrivateKey) {
               keyMaterial = ((OpenSslPrivateKey)key).newKeyMaterial(chain, certificates);
            } else {
               pkeyBio = ReferenceCountedOpenSslContext.toBIO(allocator, key);
               pkey = key == null ? 0L : SSL.parsePrivateKey(pkeyBio, this.password);
               keyMaterial = new DefaultOpenSslKeyMaterial(chain, pkey, certificates);
            }

            chain = 0L;
            pkey = 0L;
            var15 = keyMaterial;
         } finally {
            SSL.freeBIO(chainBio);
            SSL.freeBIO(pkeyBio);
            if (chain != 0L) {
               SSL.freeX509Chain(chain);
            }

            if (pkey != 0L) {
               SSL.freePrivateKey(pkey);
            }

            encoded.release();
         }

         return (OpenSslKeyMaterial)var15;
      } else {
         return null;
      }
   }

   void destroy() {
   }
}
