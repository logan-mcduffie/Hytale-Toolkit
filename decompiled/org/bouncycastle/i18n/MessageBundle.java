package org.bouncycastle.i18n;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.TimeZone;

public class MessageBundle extends TextBundle {
   public static final String TITLE_ENTRY = "title";

   public MessageBundle(String var1, String var2) throws NullPointerException {
      super(var1, var2);
   }

   public MessageBundle(String var1, String var2, String var3) throws NullPointerException, UnsupportedEncodingException {
      super(var1, var2, var3);
   }

   public MessageBundle(String var1, String var2, Object[] var3) throws NullPointerException {
      super(var1, var2, var3);
   }

   public MessageBundle(String var1, String var2, String var3, Object[] var4) throws NullPointerException, UnsupportedEncodingException {
      super(var1, var2, var3, var4);
   }

   public String getTitle(Locale var1, TimeZone var2) throws MissingEntryException {
      return this.getEntry("title", var1, var2);
   }

   public String getTitle(Locale var1) throws MissingEntryException {
      return this.getEntry("title", var1, TimeZone.getDefault());
   }
}
