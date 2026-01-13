package joptsimple.internal;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {
   private Messages() {
      throw new UnsupportedOperationException();
   }

   public static String message(Locale locale, String bundleName, Class<?> type, String key, Object... args) {
      ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
      String template = bundle.getString(type.getName() + '.' + key);
      MessageFormat format = new MessageFormat(template);
      format.setLocale(locale);
      return format.format(args);
   }
}
