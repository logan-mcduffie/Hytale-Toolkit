package io.netty.handler.ssl;

import java.util.Arrays;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

final class OpenSslKeyMaterialManager {
   static final String KEY_TYPE_RSA = "RSA";
   static final String KEY_TYPE_DH_RSA = "DH_RSA";
   static final String KEY_TYPE_EC = "EC";
   static final String KEY_TYPE_EC_EC = "EC_EC";
   static final String KEY_TYPE_EC_RSA = "EC_RSA";
   private static final int TYPE_RSA = 1;
   private static final int TYPE_DH_RSA = 2;
   private static final int TYPE_EC = 4;
   private static final int TYPE_EC_EC = 8;
   private static final int TYPE_EC_RSA = 16;
   private final OpenSslKeyMaterialProvider provider;
   private final boolean hasTmpDhKeys;

   OpenSslKeyMaterialManager(OpenSslKeyMaterialProvider provider, boolean hasTmpDhKeys) {
      this.provider = provider;
      this.hasTmpDhKeys = hasTmpDhKeys;
   }

   void setKeyMaterialServerSide(ReferenceCountedOpenSslEngine engine) throws SSLException {
      String[] authMethods = engine.authMethods();
      if (authMethods.length == 0) {
         throw new SSLHandshakeException("Unable to find key material");
      } else {
         int seenTypes = 0;

         for (String authMethod : authMethods) {
            int typeBit = resolveKeyTypeBit(authMethod);
            if (typeBit != 0 && (seenTypes & typeBit) == 0) {
               seenTypes |= typeBit;
               String keyType = keyTypeString(typeBit);
               String alias = this.chooseServerAlias(engine, keyType);
               if (alias != null) {
                  this.setKeyMaterial(engine, alias);
                  return;
               }
            }
         }

         if (!this.hasTmpDhKeys || authMethods.length != 1 || !"DH_anon".equals(authMethods[0]) && !"ECDH_anon".equals(authMethods[0])) {
            throw new SSLHandshakeException("Unable to find key material for auth method(s): " + Arrays.toString((Object[])authMethods));
         }
      }
   }

   private static int resolveKeyTypeBit(String authMethod) {
      switch (authMethod) {
         case "RSA":
         case "DHE_RSA":
         case "ECDHE_RSA":
            return 1;
         case "DH_RSA":
            return 2;
         case "ECDHE_ECDSA":
            return 4;
         case "ECDH_ECDSA":
            return 8;
         case "ECDH_RSA":
            return 16;
         default:
            return 0;
      }
   }

   private static String keyTypeString(int typeBit) {
      switch (typeBit) {
         case 1:
            return "RSA";
         case 2:
            return "DH_RSA";
         case 4:
            return "EC";
         case 8:
            return "EC_EC";
         case 16:
            return "EC_RSA";
         default:
            return null;
      }
   }

   void setKeyMaterialClientSide(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) throws SSLException {
      String alias = this.chooseClientAlias(engine, keyTypes, issuer);
      if (alias != null) {
         this.setKeyMaterial(engine, alias);
      }
   }

   private void setKeyMaterial(ReferenceCountedOpenSslEngine engine, String alias) throws SSLException {
      OpenSslKeyMaterial keyMaterial = null;

      try {
         keyMaterial = this.provider.chooseKeyMaterial(engine.alloc, alias);
         if (keyMaterial != null) {
            engine.setKeyMaterial(keyMaterial);
            return;
         }
      } catch (SSLException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new SSLException(var10);
      } finally {
         if (keyMaterial != null) {
            keyMaterial.release();
         }
      }
   }

   private String chooseClientAlias(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) {
      X509KeyManager manager = this.provider.keyManager();
      return manager instanceof X509ExtendedKeyManager
         ? ((X509ExtendedKeyManager)manager).chooseEngineClientAlias(keyTypes, issuer, engine)
         : manager.chooseClientAlias(keyTypes, issuer, null);
   }

   private String chooseServerAlias(ReferenceCountedOpenSslEngine engine, String type) {
      X509KeyManager manager = this.provider.keyManager();
      return manager instanceof X509ExtendedKeyManager
         ? ((X509ExtendedKeyManager)manager).chooseEngineServerAlias(type, null, engine)
         : manager.chooseServerAlias(type, null, null);
   }
}
