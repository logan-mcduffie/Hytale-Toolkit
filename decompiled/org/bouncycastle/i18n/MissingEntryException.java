package org.bouncycastle.i18n;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

public class MissingEntryException extends RuntimeException {
   protected final String resource;
   protected final String key;
   protected final ClassLoader loader;
   protected final Locale locale;
   private String debugMsg;

   public MissingEntryException(String var1, String var2, String var3, Locale var4, ClassLoader var5) {
      super(var1);
      this.resource = var2;
      this.key = var3;
      this.locale = var4;
      this.loader = var5;
   }

   public MissingEntryException(String var1, Throwable var2, String var3, String var4, Locale var5, ClassLoader var6) {
      super(var1, var2);
      this.resource = var3;
      this.key = var4;
      this.locale = var5;
      this.loader = var6;
   }

   public String getKey() {
      return this.key;
   }

   public String getResource() {
      return this.resource;
   }

   public ClassLoader getClassLoader() {
      return this.loader;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public String getDebugMsg() {
      if (this.debugMsg == null) {
         this.debugMsg = "Can not find entry " + this.key + " in resource file " + this.resource + " for the locale " + this.locale + ".";
         if (this.loader instanceof URLClassLoader) {
            URL[] var1 = ((URLClassLoader)this.loader).getURLs();
            this.debugMsg = this.debugMsg + " The following entries in the classpath were searched: ";

            for (int var2 = 0; var2 != var1.length; var2++) {
               this.debugMsg = this.debugMsg + var1[var2] + " ";
            }
         }
      }

      return this.debugMsg;
   }
}
