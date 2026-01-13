package joptsimple.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class PathConverter implements ValueConverter<Path> {
   private final PathProperties[] pathProperties;

   public PathConverter(PathProperties... pathProperties) {
      this.pathProperties = pathProperties;
   }

   public Path convert(String value) {
      Path path = Paths.get(value);
      if (this.pathProperties != null) {
         for (PathProperties each : this.pathProperties) {
            if (!each.accept(path)) {
               throw new ValueConversionException(this.message(each.getMessageKey(), path.toString()));
            }
         }
      }

      return path;
   }

   @Override
   public Class<Path> valueType() {
      return Path.class;
   }

   @Override
   public String valuePattern() {
      return null;
   }

   private String message(String errorKey, String value) {
      ResourceBundle bundle = ResourceBundle.getBundle("joptsimple.ExceptionMessages");
      Object[] arguments = new Object[]{value, this.valuePattern()};
      String template = bundle.getString(PathConverter.class.getName() + "." + errorKey + ".message");
      return new MessageFormat(template).format(arguments);
   }
}
