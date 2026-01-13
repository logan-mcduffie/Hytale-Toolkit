package org.jline.nativ;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JLineNativeLoader {
   private static final Logger logger = Logger.getLogger("org.jline");
   private static boolean loaded = false;
   private static String nativeLibraryPath;
   private static String nativeLibrarySourceUrl;

   public static synchronized boolean initialize() {
      if (!loaded) {
         Thread cleanup = new Thread(JLineNativeLoader::cleanup, "cleanup");
         cleanup.setPriority(1);
         cleanup.setDaemon(true);
         cleanup.start();
      }

      try {
         loadJLineNativeLibrary();
      } catch (Exception var1) {
         throw new RuntimeException("Unable to load jline native library: " + var1.getMessage(), var1);
      }

      return loaded;
   }

   public static String getNativeLibraryPath() {
      return nativeLibraryPath;
   }

   public static String getNativeLibrarySourceUrl() {
      return nativeLibrarySourceUrl;
   }

   private static File getTempDir() {
      return new File(System.getProperty("jline.tmpdir", System.getProperty("java.io.tmpdir")));
   }

   static void cleanup() {
      String tempFolder = getTempDir().getAbsolutePath();
      File dir = new File(tempFolder);
      File[] nativeLibFiles = dir.listFiles(new FilenameFilter() {
         private final String searchPattern = "jlinenative-" + JLineNativeLoader.getVersion();

         @Override
         public boolean accept(File dir, String name) {
            return name.startsWith(this.searchPattern) && !name.endsWith(".lck");
         }
      });
      if (nativeLibFiles != null) {
         for (File nativeLibFile : nativeLibFiles) {
            File lckFile = new File(nativeLibFile.getAbsolutePath() + ".lck");
            if (!lckFile.exists()) {
               try {
                  nativeLibFile.delete();
               } catch (SecurityException var9) {
                  logger.log(Level.INFO, "Failed to delete old native lib" + var9.getMessage(), (Throwable)var9);
               }
            }
         }
      }
   }

   private static int readNBytes(InputStream in, byte[] b) throws IOException {
      int n = 0;
      int len = b.length;

      while (n < len) {
         int count = in.read(b, n, len - n);
         if (count <= 0) {
            break;
         }

         n += count;
      }

      return n;
   }

   private static String contentsEquals(InputStream in1, InputStream in2) throws IOException {
      byte[] buffer1 = new byte[8192];
      byte[] buffer2 = new byte[8192];

      do {
         int numRead1 = readNBytes(in1, buffer1);
         int numRead2 = readNBytes(in2, buffer2);
         if (numRead1 <= 0) {
            if (numRead2 > 0) {
               return "EOF on first stream but not second";
            }

            return null;
         }

         if (numRead2 <= 0) {
            return "EOF on second stream but not first";
         }

         if (numRead2 != numRead1) {
            return "Read size different (" + numRead1 + " vs " + numRead2 + ")";
         }
      } while (Arrays.equals(buffer1, buffer2));

      return "Content differs";
   }

   private static boolean extractAndLoadLibraryFile(String libFolderForCurrentOS, String libraryFileName, String targetFolder) {
      String nativeLibraryFilePath = libFolderForCurrentOS + "/" + libraryFileName;
      String uuid = randomUUID();
      String extractedLibFileName = String.format("jlinenative-%s-%s-%s", getVersion(), uuid, libraryFileName);
      String extractedLckFileName = extractedLibFileName + ".lck";
      File extractedLibFile = new File(targetFolder, extractedLibFileName);
      File extractedLckFile = new File(targetFolder, extractedLckFileName);

      try {
         try {
            InputStream in = JLineNativeLoader.class.getResourceAsStream(nativeLibraryFilePath);

            try {
               if (!extractedLckFile.exists()) {
                  new FileOutputStream(extractedLckFile).close();
               }

               OutputStream out = new FileOutputStream(extractedLibFile);

               try {
                  copy(in, out);
               } catch (Throwable var28) {
                  try {
                     out.close();
                  } catch (Throwable var27) {
                     var28.addSuppressed(var27);
                  }

                  throw var28;
               }

               out.close();
            } catch (Throwable var29) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var26) {
                     var29.addSuppressed(var26);
                  }
               }

               throw var29;
            }

            if (in != null) {
               in.close();
            }
         } finally {
            extractedLibFile.deleteOnExit();
            extractedLckFile.deleteOnExit();
         }

         extractedLibFile.setReadable(true);
         extractedLibFile.setWritable(true);
         extractedLibFile.setExecutable(true);
         InputStream var34 = JLineNativeLoader.class.getResourceAsStream(nativeLibraryFilePath);

         try {
            InputStream extractedLibIn = new FileInputStream(extractedLibFile);

            try {
               String eq = contentsEquals(var34, extractedLibIn);
               if (eq != null) {
                  throw new RuntimeException(String.format("Failed to write a native library file at %s because %s", extractedLibFile, eq));
               }
            } catch (Throwable var31) {
               try {
                  extractedLibIn.close();
               } catch (Throwable var25) {
                  var31.addSuppressed(var25);
               }

               throw var31;
            }

            extractedLibIn.close();
         } catch (Throwable var32) {
            if (var34 != null) {
               try {
                  var34.close();
               } catch (Throwable var24) {
                  var32.addSuppressed(var24);
               }
            }

            throw var32;
         }

         if (var34 != null) {
            var34.close();
         }

         if (loadNativeLibrary(extractedLibFile)) {
            nativeLibrarySourceUrl = JLineNativeLoader.class.getResource(nativeLibraryFilePath).toExternalForm();
            return true;
         }
      } catch (IOException var33) {
         log(Level.WARNING, "Unable to load JLine's native library", var33);
      }

      return false;
   }

   private static String randomUUID() {
      return Long.toHexString(new Random().nextLong());
   }

   private static void copy(InputStream in, OutputStream out) throws IOException {
      byte[] buf = new byte[8192];

      int n;
      while ((n = in.read(buf)) > 0) {
         out.write(buf, 0, n);
      }
   }

   private static boolean loadNativeLibrary(File libPath) {
      if (libPath.exists()) {
         try {
            String path = libPath.getAbsolutePath();
            System.load(path);
            nativeLibraryPath = path;
            return true;
         } catch (UnsatisfiedLinkError var2) {
            log(Level.WARNING, "Failed to load native library:" + libPath.getName() + ". osinfo: " + OSInfo.getNativeLibFolderPathForCurrentOS(), var2);
            return false;
         }
      } else {
         return false;
      }
   }

   private static void loadJLineNativeLibrary() throws Exception {
      if (!loaded) {
         List<String> triedPaths = new ArrayList<>();
         String jlineNativeLibraryPath = System.getProperty("library.jline.path");
         String jlineNativeLibraryName = System.getProperty("library.jline.name");
         if (jlineNativeLibraryName == null) {
            jlineNativeLibraryName = System.mapLibraryName("jlinenative");

            assert jlineNativeLibraryName != null;

            if (jlineNativeLibraryName.endsWith(".dylib")) {
               jlineNativeLibraryName = jlineNativeLibraryName.replace(".dylib", ".jnilib");
            }
         }

         if (jlineNativeLibraryPath != null) {
            String withOs = jlineNativeLibraryPath + "/" + OSInfo.getNativeLibFolderPathForCurrentOS();
            if (loadNativeLibrary(new File(withOs, jlineNativeLibraryName))) {
               loaded = true;
               return;
            }

            triedPaths.add(withOs);
            if (loadNativeLibrary(new File(jlineNativeLibraryPath, jlineNativeLibraryName))) {
               loaded = true;
               return;
            }

            triedPaths.add(jlineNativeLibraryPath);
         }

         String packagePath = JLineNativeLoader.class.getPackage().getName().replace('.', '/');
         jlineNativeLibraryPath = String.format("/%s/%s", packagePath, OSInfo.getNativeLibFolderPathForCurrentOS());
         boolean hasNativeLib = hasResource(jlineNativeLibraryPath + "/" + jlineNativeLibraryName);
         if (hasNativeLib) {
            String tempFolder = getTempDir().getAbsolutePath();
            if (extractAndLoadLibraryFile(jlineNativeLibraryPath, jlineNativeLibraryName, tempFolder)) {
               loaded = true;
               return;
            }

            triedPaths.add(jlineNativeLibraryPath);
         }

         String javaLibraryPath = System.getProperty("java.library.path", "");

         for (String ldPath : javaLibraryPath.split(File.pathSeparator)) {
            if (!ldPath.isEmpty()) {
               if (loadNativeLibrary(new File(ldPath, jlineNativeLibraryName))) {
                  loaded = true;
                  return;
               }

               triedPaths.add(ldPath);
            }
         }

         throw new Exception(
            String.format(
               "No native library found for os.name=%s, os.arch=%s, paths=[%s]", OSInfo.getOSName(), OSInfo.getArchName(), join(triedPaths, File.pathSeparator)
            )
         );
      }
   }

   private static boolean hasResource(String path) {
      return JLineNativeLoader.class.getResource(path) != null;
   }

   public static int getMajorVersion() {
      String[] c = getVersion().split("\\.");
      return c.length > 0 ? Integer.parseInt(c[0]) : 1;
   }

   public static int getMinorVersion() {
      String[] c = getVersion().split("\\.");
      return c.length > 1 ? Integer.parseInt(c[1]) : 0;
   }

   public static String getVersion() {
      URL versionFile = JLineNativeLoader.class.getResource("/META-INF/maven/org.jline/jline-native/pom.properties");
      String version = "unknown";

      try {
         if (versionFile != null) {
            Properties versionData = new Properties();
            versionData.load(versionFile.openStream());
            version = versionData.getProperty("version", version);
            version = version.trim().replaceAll("[^0-9.]", "");
         }
      } catch (IOException var3) {
         log(Level.WARNING, "Unable to load jline-native version", var3);
      }

      return version;
   }

   private static String join(List<String> list, String separator) {
      StringBuilder sb = new StringBuilder();
      boolean first = true;

      for (String item : list) {
         if (first) {
            first = false;
         } else {
            sb.append(separator);
         }

         sb.append(item);
      }

      return sb.toString();
   }

   private static void log(Level level, String message, Throwable t) {
      if (logger.isLoggable(level)) {
         if (logger.isLoggable(Level.FINE)) {
            logger.log(level, message, t);
         } else {
            logger.log(level, message + " (caused by: " + t + ", enable debug logging for stacktrace)");
         }
      }
   }
}
