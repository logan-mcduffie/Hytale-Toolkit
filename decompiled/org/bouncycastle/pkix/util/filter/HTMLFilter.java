package org.bouncycastle.pkix.util.filter;

public class HTMLFilter implements Filter {
   @Override
   public String doFilter(String var1) {
      StringBuilder var2 = new StringBuilder(var1);

      for (byte var3 = 0; var3 < var2.length(); var3 += 4) {
         char var4 = var2.charAt(var3);
         switch (var4) {
            case '"':
               var2.replace(var3, var3 + 1, "&#34");
               break;
            case '#':
               var2.replace(var3, var3 + 1, "&#35");
               break;
            case '$':
            case '*':
            case ',':
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case '=':
            default:
               var3 -= 3;
               break;
            case '%':
               var2.replace(var3, var3 + 1, "&#37");
               break;
            case '&':
               var2.replace(var3, var3 + 1, "&#38");
               break;
            case '\'':
               var2.replace(var3, var3 + 1, "&#39");
               break;
            case '(':
               var2.replace(var3, var3 + 1, "&#40");
               break;
            case ')':
               var2.replace(var3, var3 + 1, "&#41");
               break;
            case '+':
               var2.replace(var3, var3 + 1, "&#43");
               break;
            case '-':
               var2.replace(var3, var3 + 1, "&#45");
               break;
            case ';':
               var2.replace(var3, var3 + 1, "&#59");
               break;
            case '<':
               var2.replace(var3, var3 + 1, "&#60");
               break;
            case '>':
               var2.replace(var3, var3 + 1, "&#62");
         }
      }

      return var2.toString();
   }

   @Override
   public String doFilterUrl(String var1) {
      return this.doFilter(var1);
   }
}
