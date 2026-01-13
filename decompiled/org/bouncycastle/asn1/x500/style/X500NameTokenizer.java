package org.bouncycastle.asn1.x500.style;

public class X500NameTokenizer {
   private final String value;
   private final char separator;
   private int index;

   public X500NameTokenizer(String var1) {
      this(var1, ',');
   }

   public X500NameTokenizer(String var1, char var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 != '"' && var2 != '\\') {
         this.value = var1;
         this.separator = var2;
         this.index = var1.length() < 1 ? 0 : -1;
      } else {
         throw new IllegalArgumentException("reserved separator character");
      }
   }

   public boolean hasMoreTokens() {
      return this.index < this.value.length();
   }

   public String nextToken() {
      if (this.index >= this.value.length()) {
         return null;
      } else {
         boolean var1 = false;
         boolean var2 = false;
         int var3 = this.index + 1;

         while (++this.index < this.value.length()) {
            char var4 = this.value.charAt(this.index);
            if (var2) {
               var2 = false;
            } else if (var4 == '"') {
               var1 = !var1;
            } else if (!var1) {
               if (var4 == '\\') {
                  var2 = true;
               } else if (var4 == this.separator) {
                  return this.value.substring(var3, this.index);
               }
            }
         }

         if (!var2 && !var1) {
            return this.value.substring(var3, this.index);
         } else {
            throw new IllegalArgumentException("badly formatted directory string");
         }
      }
   }
}
