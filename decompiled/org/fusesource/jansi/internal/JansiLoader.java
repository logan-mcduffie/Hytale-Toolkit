package org.fusesource.jansi.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class JansiLoader {
   private static boolean loaded = false;
   private static String nativeLibraryPath;
   private static String nativeLibrarySourceUrl;

   public static synchronized boolean initialize() {
      if (!loaded) {
         Thread cleanup = new Thread(JansiLoader::cleanup, "cleanup");
         cleanup.setPriority(1);
         cleanup.setDaemon(true);
         cleanup.start();
      }

      try {
         loadJansiNativeLibrary();
      } catch (Exception var1) {
         if (!Boolean.parseBoolean(System.getProperty("jansi.graceful", "true"))) {
            throw new RuntimeException(
               "Unable to load jansi native library. You may want set the `jansi.graceful` system property to true to be able to use Jansi on your platform",
               var1
            );
         }
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
      return new File(System.getProperty("jansi.tmpdir", System.getProperty("java.io.tmpdir")));
   }

   static void cleanup() {
      String tempFolder = getTempDir().getAbsolutePath();
      File dir = new File(tempFolder);
      File[] nativeLibFiles = dir.listFiles(new FilenameFilter() {
         private final String searchPattern = "jansi-" + JansiLoader.getVersion();

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
                  System.err.println("Failed to delete old native lib" + var9.getMessage());
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
      String extractedLibFileName = String.format("jansi-%s-%s-%s", getVersion(), uuid, libraryFileName);
      String extractedLckFileName = extractedLibFileName + ".lck";
      File extractedLibFile = new File(targetFolder, extractedLibFileName);
      File extractedLckFile = new File(targetFolder, extractedLckFileName);

      try {
         try {
            InputStream in = JansiLoader.class.getResourceAsStream(nativeLibraryFilePath);

            try {
               if (!extractedLckFile.exists()) {
                  new FileOutputStream(extractedLckFile).close();
               }

               Files.copy(in, extractedLibFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Throwable var24) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var23) {
                     var24.addSuppressed(var23);
                  }
               }

               throw var24;
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
         InputStream var29 = JansiLoader.class.getResourceAsStream(nativeLibraryFilePath);

         try {
            InputStream extractedLibIn = new FileInputStream(extractedLibFile);

            try {
               String eq = contentsEquals(var29, extractedLibIn);
               if (eq != null) {
                  throw new RuntimeException(String.format("Failed to write a native library file at %s because %s", extractedLibFile, eq));
               }
            } catch (Throwable var26) {
               try {
                  extractedLibIn.close();
               } catch (Throwable var22) {
                  var26.addSuppressed(var22);
               }

               throw var26;
            }

            extractedLibIn.close();
         } catch (Throwable var27) {
            if (var29 != null) {
               try {
                  var29.close();
               } catch (Throwable var21) {
                  var27.addSuppressed(var21);
               }
            }

            throw var27;
         }

         if (var29 != null) {
            var29.close();
         }

         if (loadNativeLibrary(extractedLibFile)) {
            nativeLibrarySourceUrl = JansiLoader.class.getResource(nativeLibraryFilePath).toExternalForm();
            return true;
         }
      } catch (IOException var28) {
         System.err.println(var28.getMessage());
      }

      return false;
   }

   private static String randomUUID() {
      return Long.toHexString(new Random().nextLong());
   }

   private static boolean loadNativeLibrary(File libPath) {
      if (libPath.exists()) {
         try {
            String path = libPath.getAbsolutePath();
            System.load(path);
            nativeLibraryPath = path;
            return true;
         } catch (UnsatisfiedLinkError var2) {
            if (!libPath.canExecute()) {
               System.err
                  .printf(
                     "Failed to load native library:%s. The native library file at %s is not executable, make sure that the directory is mounted on a partition without the noexec flag, or set the jansi.tmpdir system property to point to a proper location.  osinfo: %s%n",
                     libPath.getName(),
                     libPath,
                     OSInfo.getNativeLibFolderPathForCurrentOS()
                  );
            } else {
               System.err.printf("Failed to load native library:%s. osinfo: %s%n", libPath.getName(), OSInfo.getNativeLibFolderPathForCurrentOS());
            }

            System.err.println(var2);
            return false;
         }
      } else {
         return false;
      }
   }

   private static void loadJansiNativeLibrary() throws Exception {
      if (!loaded) {
         List<String> triedPaths = new LinkedList<>();
         String jansiNativeLibraryPath = System.getProperty("library.jansi.path");
         String jansiNativeLibraryName = System.getProperty("library.jansi.name");
         if (jansiNativeLibraryName == null) {
            jansiNativeLibraryName = System.mapLibraryName("jansi");

            assert jansiNativeLibraryName != null;

            if (jansiNativeLibraryName.endsWith(".dylib")) {
               jansiNativeLibraryName = jansiNativeLibraryName.replace(".dylib", ".jnilib");
            }
         }

         if (jansiNativeLibraryPath != null) {
            String withOs = jansiNativeLibraryPath + "/" + OSInfo.getNativeLibFolderPathForCurrentOS();
            if (loadNativeLibrary(new File(withOs, jansiNativeLibraryName))) {
               loaded = true;
               return;
            }

            triedPaths.add(withOs);
            if (loadNativeLibrary(new File(jansiNativeLibraryPath, jansiNativeLibraryName))) {
               loaded = true;
               return;
            }

            triedPaths.add(jansiNativeLibraryPath);
         }

         String packagePath = JansiLoader.class.getPackage().getName().replace('.', '/');
         jansiNativeLibraryPath = String.format("/%s/native/%s", packagePath, OSInfo.getNativeLibFolderPathForCurrentOS());
         boolean hasNativeLib = hasResource(jansiNativeLibraryPath + "/" + jansiNativeLibraryName);
         if (hasNativeLib) {
            String tempFolder = getTempDir().getAbsolutePath();
            if (extractAndLoadLibraryFile(jansiNativeLibraryPath, jansiNativeLibraryName, tempFolder)) {
               loaded = true;
               return;
            }

            triedPaths.add(jansiNativeLibraryPath);
         }

         String javaLibraryPath = System.getProperty("java.library.path", "");

         for (String ldPath : javaLibraryPath.split(File.pathSeparator)) {
            if (!ldPath.isEmpty()) {
               if (loadNativeLibrary(new File(ldPath, jansiNativeLibraryName))) {
                  loaded = true;
                  return;
               }

               triedPaths.add(ldPath);
            }
         }

         throw new Exception(
            String.format(
               "No native library found for os.name=%s, os.arch=%s, paths=[%s]",
               OSInfo.getOSName(),
               OSInfo.getArchName(),
               String.join(File.pathSeparator, triedPaths)
            )
         );
      }
   }

   private static boolean hasResource(String path) {
      return JansiLoader.class.getResource(path) != null;
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
      URL versionFile = JansiLoader.class.getResource("/org/fusesource/jansi/jansi.properties");
      String version = "unknown";

      try {
         if (versionFile != null) {
            Properties versionData = new Properties();
            versionData.load(versionFile.openStream());
            version = versionData.getProperty("version", version);
            version = version.trim().replaceAll("[^0-9.]", "");
         }
      } catch (IOException var3) {
         System.err.println(var3);
      }

      return version;
   }
}
