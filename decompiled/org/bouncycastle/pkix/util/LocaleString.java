package org.bouncycastle.pkix.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class LocaleString extends LocalizedMessage {
   public LocaleString(String var1, String var2) {
      super(var1, var2);
   }

   public LocaleString(String var1, String var2, String var3) throws NullPointerException, UnsupportedEncodingException {
      super(var1, var2, var3);
   }

   public LocaleString(String var1, String var2, String var3, Object[] var4) throws NullPointerException, UnsupportedEncodingException {
      super(var1, var2, var3, var4);
   }

   public String getLocaleString(Locale var1) {
      return this.getEntry(null, var1, null);
   }
}
