package org.bouncycastle.jcajce.provider.keystore.util;

import java.io.IOException;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class ParameterUtil {
   public static char[] extractPassword(LoadStoreParameter var0) throws IOException {
      ProtectionParameter var1 = var0.getProtectionParameter();
      if (var1 == null) {
         return null;
      } else if (var1 instanceof PasswordProtection) {
         return ((PasswordProtection)var1).getPassword();
      } else if (var1 instanceof CallbackHandlerProtection) {
         CallbackHandler var2 = ((CallbackHandlerProtection)var1).getCallbackHandler();
         PasswordCallback var3 = new PasswordCallback("password: ", false);

         try {
            var2.handle(new Callback[]{var3});
            return var3.getPassword();
         } catch (UnsupportedCallbackException var5) {
            throw new IllegalArgumentException("PasswordCallback not recognised: " + var5.getMessage(), var5);
         }
      } else {
         throw new IllegalArgumentException("no support for protection parameter of type " + var1.getClass().getName());
      }
   }
}
