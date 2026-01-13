package org.bouncycastle.pkix.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import org.bouncycastle.pkix.util.filter.Filter;
import org.bouncycastle.pkix.util.filter.TrustedInput;
import org.bouncycastle.pkix.util.filter.UntrustedInput;
import org.bouncycastle.pkix.util.filter.UntrustedUrlInput;

public class LocalizedMessage {
   protected final String id;
   protected final String resource;
   public static final String DEFAULT_ENCODING = "ISO-8859-1";
   protected String encoding = "ISO-8859-1";
   protected LocalizedMessage.FilteredArguments arguments;
   protected LocalizedMessage.FilteredArguments extraArgs = null;
   protected Filter filter = null;
   protected ClassLoader loader = null;

   public LocalizedMessage(String var1, String var2) throws NullPointerException {
      if (var1 != null && var2 != null) {
         this.id = var2;
         this.resource = var1;
         this.arguments = new LocalizedMessage.FilteredArguments();
      } else {
         throw new NullPointerException();
      }
   }

   public LocalizedMessage(String var1, String var2, String var3) throws NullPointerException, UnsupportedEncodingException {
      if (var1 != null && var2 != null) {
         this.id = var2;
         this.resource = var1;
         this.arguments = new LocalizedMessage.FilteredArguments();
         if (!Charset.isSupported(var3)) {
            throw new UnsupportedEncodingException("The encoding \"" + var3 + "\" is not supported.");
         } else {
            this.encoding = var3;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public LocalizedMessage(String var1, String var2, Object[] var3) throws NullPointerException {
      if (var1 != null && var2 != null && var3 != null) {
         this.id = var2;
         this.resource = var1;
         this.arguments = new LocalizedMessage.FilteredArguments(var3);
      } else {
         throw new NullPointerException();
      }
   }

   public LocalizedMessage(String var1, String var2, String var3, Object[] var4) throws NullPointerException, UnsupportedEncodingException {
      if (var1 != null && var2 != null && var4 != null) {
         this.id = var2;
         this.resource = var1;
         this.arguments = new LocalizedMessage.FilteredArguments(var4);
         if (!Charset.isSupported(var3)) {
            throw new UnsupportedEncodingException("The encoding \"" + var3 + "\" is not supported.");
         } else {
            this.encoding = var3;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public String getEntry(String var1, Locale var2, TimeZone var3) throws MissingEntryException {
      String var4 = this.id;
      if (var1 != null) {
         var4 = var4 + "." + var1;
      }

      try {
         ResourceBundle var5;
         if (this.loader == null) {
            var5 = ResourceBundle.getBundle(this.resource, var2);
         } else {
            var5 = ResourceBundle.getBundle(this.resource, var2, this.loader);
         }

         String var6 = var5.getString(var4);
         if (!this.encoding.equals("ISO-8859-1")) {
            var6 = new String(var6.getBytes("ISO-8859-1"), this.encoding);
         }

         if (!this.arguments.isEmpty()) {
            var6 = this.formatWithTimeZone(var6, this.arguments.getFilteredArgs(var2), var2, var3);
         }

         return this.addExtraArgs(var6, var2);
      } catch (MissingResourceException var7) {
         throw new MissingEntryException(
            "Can't find entry " + var4 + " in resource file " + this.resource + ".",
            this.resource,
            var4,
            var2,
            this.loader != null ? this.loader : this.getClassLoader()
         );
      } catch (UnsupportedEncodingException var8) {
         throw new RuntimeException(var8);
      }
   }

   protected String formatWithTimeZone(String var1, Object[] var2, Locale var3, TimeZone var4) {
      MessageFormat var5 = new MessageFormat(" ");
      var5.setLocale(var3);
      var5.applyPattern(var1);
      if (!var4.equals(TimeZone.getDefault())) {
         Format[] var6 = var5.getFormats();

         for (int var7 = 0; var7 < var6.length; var7++) {
            if (var6[var7] instanceof DateFormat) {
               DateFormat var8 = (DateFormat)var6[var7];
               var8.setTimeZone(var4);
               var5.setFormat(var7, var8);
            }
         }
      }

      return var5.format(var2);
   }

   protected String addExtraArgs(String var1, Locale var2) {
      if (this.extraArgs != null) {
         StringBuilder var3 = new StringBuilder(var1);
         Object[] var4 = this.extraArgs.getFilteredArgs(var2);

         for (int var5 = 0; var5 < var4.length; var5++) {
            var3.append(var4[var5]);
         }

         var1 = var3.toString();
      }

      return var1;
   }

   public void setFilter(Filter var1) {
      this.arguments.setFilter(var1);
      if (this.extraArgs != null) {
         this.extraArgs.setFilter(var1);
      }

      this.filter = var1;
   }

   public Filter getFilter() {
      return this.filter;
   }

   public void setClassLoader(ClassLoader var1) {
      this.loader = var1;
   }

   public ClassLoader getClassLoader() {
      return this.loader;
   }

   public String getId() {
      return this.id;
   }

   public String getResource() {
      return this.resource;
   }

   public Object[] getArguments() {
      return this.arguments.getArguments();
   }

   public void setExtraArgument(Object var1) {
      this.setExtraArguments(new Object[]{var1});
   }

   public void setExtraArguments(Object[] var1) {
      if (var1 != null) {
         this.extraArgs = new LocalizedMessage.FilteredArguments(var1);
         this.extraArgs.setFilter(this.filter);
      } else {
         this.extraArgs = null;
      }
   }

   public Object[] getExtraArgs() {
      return this.extraArgs == null ? null : this.extraArgs.getArguments();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Resource: \"").append(this.resource);
      var1.append("\" Id: \"").append(this.id).append("\"");
      var1.append(" Arguments: ").append(this.arguments.getArguments().length).append(" normal");
      if (this.extraArgs != null && this.extraArgs.getArguments().length > 0) {
         var1.append(", ").append(this.extraArgs.getArguments().length).append(" extra");
      }

      var1.append(" Encoding: ").append(this.encoding);
      var1.append(" ClassLoader: ").append(this.loader);
      return var1.toString();
   }

   protected static class FilteredArguments {
      protected static final int NO_FILTER = 0;
      protected static final int FILTER = 1;
      protected static final int FILTER_URL = 2;
      protected Filter filter = null;
      protected boolean[] isLocaleSpecific;
      protected int[] argFilterType;
      protected Object[] arguments;
      protected Object[] unpackedArgs;
      protected Object[] filteredArgs;

      FilteredArguments() {
         this(new Object[0]);
      }

      FilteredArguments(Object[] var1) {
         this.arguments = var1;
         this.unpackedArgs = new Object[var1.length];
         this.filteredArgs = new Object[var1.length];
         this.isLocaleSpecific = new boolean[var1.length];
         this.argFilterType = new int[var1.length];

         for (int var2 = 0; var2 < var1.length; var2++) {
            if (var1[var2] instanceof TrustedInput) {
               this.unpackedArgs[var2] = ((TrustedInput)var1[var2]).getInput();
               this.argFilterType[var2] = 0;
            } else if (var1[var2] instanceof UntrustedInput) {
               this.unpackedArgs[var2] = ((UntrustedInput)var1[var2]).getInput();
               if (var1[var2] instanceof UntrustedUrlInput) {
                  this.argFilterType[var2] = 2;
               } else {
                  this.argFilterType[var2] = 1;
               }
            } else {
               this.unpackedArgs[var2] = var1[var2];
               this.argFilterType[var2] = 1;
            }

            this.isLocaleSpecific[var2] = this.unpackedArgs[var2] instanceof LocaleString;
         }
      }

      public boolean isEmpty() {
         return this.unpackedArgs.length == 0;
      }

      public Object[] getArguments() {
         return this.arguments;
      }

      public Object[] getFilteredArgs(Locale var1) {
         Object[] var2 = new Object[this.unpackedArgs.length];

         for (int var3 = 0; var3 < this.unpackedArgs.length; var3++) {
            Object var4;
            if (this.filteredArgs[var3] != null) {
               var4 = this.filteredArgs[var3];
            } else {
               var4 = this.unpackedArgs[var3];
               if (this.isLocaleSpecific[var3]) {
                  var4 = ((LocaleString)var4).getLocaleString(var1);
                  var4 = this.filter(this.argFilterType[var3], var4);
               } else {
                  var4 = this.filter(this.argFilterType[var3], var4);
                  this.filteredArgs[var3] = var4;
               }
            }

            var2[var3] = var4;
         }

         return var2;
      }

      private Object filter(int var1, Object var2) {
         if (this.filter != null) {
            Object var3 = null == var2 ? "null" : var2;
            switch (var1) {
               case 0:
                  return var3;
               case 1:
                  return this.filter.doFilter(var3.toString());
               case 2:
                  return this.filter.doFilterUrl(var3.toString());
               default:
                  return null;
            }
         } else {
            return var2;
         }
      }

      public Filter getFilter() {
         return this.filter;
      }

      public void setFilter(Filter var1) {
         if (var1 != this.filter) {
            for (int var2 = 0; var2 < this.unpackedArgs.length; var2++) {
               this.filteredArgs[var2] = null;
            }
         }

         this.filter = var1;
      }
   }
}
