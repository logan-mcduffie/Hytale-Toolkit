package org.jline.builtins;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ClasspathResourceUtil {
   public static Path getResourcePath(String name) throws IOException, URISyntaxException {
      return getResourcePath(name, ClasspathResourceUtil.class.getClassLoader());
   }

   public static Path getResourcePath(String name, Class<?> clazz) throws IOException, URISyntaxException {
      URL resource = clazz.getResource(name);
      if (resource == null) {
         throw new IOException("Resource not found: " + name);
      } else {
         return getResourcePath(resource);
      }
   }

   public static Path getResourcePath(String name, ClassLoader classLoader) throws IOException, URISyntaxException {
      URL resource = classLoader.getResource(name);
      if (resource == null) {
         throw new IOException("Resource not found: " + name);
      } else {
         return getResourcePath(resource);
      }
   }

   public static Path getResourcePath(URL resource) throws IOException, URISyntaxException {
      URI uri = resource.toURI();
      String scheme = uri.getScheme();
      if (scheme.equals("file")) {
         return Paths.get(uri);
      } else if (!scheme.equals("jar")) {
         throw new IllegalArgumentException("Cannot convert to Path: " + uri);
      } else {
         String s = uri.toString();
         int separator = s.indexOf("!/");
         String entryName = s.substring(separator + 2);
         URI fileURI = URI.create(s.substring(0, separator));
         FileSystem fs = FileSystems.newFileSystem(fileURI, new HashMap<>());
         return fs.getPath(entryName);
      }
   }
}
