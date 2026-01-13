package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public final class NativeLibraryLoader {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);
   private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
   private static final File WORKDIR;
   private static final boolean DELETE_NATIVE_LIB_AFTER_LOADING;
   private static final boolean TRY_TO_PATCH_SHADED_ID;
   private static final boolean DETECT_NATIVE_LIBRARY_DUPLICATES;
   private static final byte[] UNIQUE_ID_BYTES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(CharsetUtil.US_ASCII);

   public static void loadFirstAvailable(ClassLoader loader, String... names) {
      List<Throwable> suppressed = new ArrayList<>();

      for (String name : names) {
         try {
            load(name, loader);
            logger.debug("Loaded library with name '{}'", name);
            return;
         } catch (Throwable var8) {
            suppressed.add(var8);
         }
      }

      IllegalArgumentException iae = new IllegalArgumentException("Failed to load any of the given libraries: " + Arrays.toString((Object[])names));
      ThrowableUtil.addSuppressedAndClear(iae, suppressed);
      throw iae;
   }

   private static String calculateMangledPackagePrefix() {
      String maybeShaded = NativeLibraryLoader.class.getName();
      String expected = "io!netty!util!internal!NativeLibraryLoader".replace('!', '.');
      if (!maybeShaded.endsWith(expected)) {
         throw new UnsatisfiedLinkError(
            String.format("Could not find prefix added to %s to get %s. When shading, only adding a package prefix is supported", expected, maybeShaded)
         );
      } else {
         return maybeShaded.substring(0, maybeShaded.length() - expected.length()).replace("_", "_1").replace('.', '_');
      }
   }

   public static void load(String originalName, ClassLoader loader) {
      String mangledPackagePrefix = calculateMangledPackagePrefix();
      String name = mangledPackagePrefix + originalName;
      List<Throwable> suppressed = new ArrayList<>();

      try {
         loadLibrary(loader, name, false);
      } catch (Throwable var34) {
         suppressed.add(var34);
         String libname = System.mapLibraryName(name);
         String path = "META-INF/native/" + libname;
         File tmpFile = null;
         URL url = getResource(path, loader);

         try {
            if (url == null) {
               if (!PlatformDependent.isOsx()) {
                  FileNotFoundException fnf = new FileNotFoundException(path);
                  ThrowableUtil.addSuppressedAndClear(fnf, suppressed);
                  throw fnf;
               }

               String fileName = path.endsWith(".jnilib") ? "META-INF/native/lib" + name + ".dynlib" : "META-INF/native/lib" + name + ".jnilib";
               url = getResource(fileName, loader);
               if (url == null) {
                  FileNotFoundException fnf = new FileNotFoundException(fileName);
                  ThrowableUtil.addSuppressedAndClear(fnf, suppressed);
                  throw fnf;
               }
            }

            int index = libname.lastIndexOf(46);
            String prefix = libname.substring(0, index);
            String suffix = libname.substring(index);
            tmpFile = PlatformDependent.createTempFile(prefix, suffix, WORKDIR);
            InputStream in = url.openStream();

            try {
               OutputStream out = new FileOutputStream(tmpFile);

               try {
                  byte[] buffer = new byte[8192];

                  int length;
                  while ((length = in.read(buffer)) > 0) {
                     out.write(buffer, 0, length);
                  }

                  out.flush();
                  if (shouldShadedLibraryIdBePatched(mangledPackagePrefix)) {
                     tryPatchShadedLibraryIdAndSign(tmpFile, originalName);
                  }
               } catch (Throwable var29) {
                  try {
                     out.close();
                  } catch (Throwable var28) {
                     var29.addSuppressed(var28);
                  }

                  throw var29;
               }

               out.close();
            } catch (Throwable var30) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var27) {
                     var30.addSuppressed(var27);
                  }
               }

               throw var30;
            }

            if (in != null) {
               in.close();
            }

            loadLibrary(loader, tmpFile.getPath(), true);
         } catch (UnsatisfiedLinkError var31) {
            try {
               if (tmpFile != null && tmpFile.isFile() && tmpFile.canRead() && !NativeLibraryLoader.NoexecVolumeDetector.canExecuteExecutable(tmpFile)) {
                  String message = String.format(
                     "%s exists but cannot be executed even when execute permissions set; check volume for \"noexec\" flag; use -D%s=[path] to set native working directory separately.",
                     tmpFile.getPath(),
                     "io.netty.native.workdir"
                  );
                  logger.info(message);
                  suppressed.add(ThrowableUtil.unknownStackTrace(new UnsatisfiedLinkError(message), NativeLibraryLoader.class, "load"));
               }
            } catch (Throwable var26) {
               suppressed.add(var26);
               logger.debug("Error checking if {} is on a file store mounted with noexec", tmpFile, var26);
            }

            ThrowableUtil.addSuppressedAndClear(var31, suppressed);
            throw var31;
         } catch (Exception var32) {
            UnsatisfiedLinkError ule = new UnsatisfiedLinkError("could not load a native library: " + name);
            ule.initCause(var32);
            ThrowableUtil.addSuppressedAndClear(ule, suppressed);
            throw ule;
         } finally {
            if (tmpFile != null && (!DELETE_NATIVE_LIB_AFTER_LOADING || !tmpFile.delete())) {
               tmpFile.deleteOnExit();
            }
         }
      }
   }

   private static URL getResource(String path, ClassLoader loader) {
      Enumeration<URL> urls;
      try {
         if (loader == null) {
            urls = ClassLoader.getSystemResources(path);
         } else {
            urls = loader.getResources(path);
         }
      } catch (IOException var11) {
         throw new RuntimeException("An error occurred while getting the resources for " + path, var11);
      }

      List<URL> urlsList = Collections.list(urls);
      int size = urlsList.size();
      switch (size) {
         case 0:
            return null;
         case 1:
            return urlsList.get(0);
         default:
            if (!DETECT_NATIVE_LIBRARY_DUPLICATES) {
               logger.warn("Multiple resources found for '" + path + "' with different content: " + urlsList + ". Please fix your dependency graph.");
               return urlsList.get(0);
            } else {
               try {
                  MessageDigest md = MessageDigest.getInstance("SHA-256");
                  URL url = urlsList.get(0);
                  byte[] digest = digest(md, url);
                  boolean allSame = true;
                  if (digest == null) {
                     allSame = false;
                  } else {
                     for (int i = 1; i < size; i++) {
                        byte[] digest2 = digest(md, urlsList.get(i));
                        if (digest2 == null || !Arrays.equals(digest, digest2)) {
                           allSame = false;
                           break;
                        }
                     }
                  }

                  if (allSame) {
                     return url;
                  }
               } catch (NoSuchAlgorithmException var12) {
                  logger.debug("Don't support SHA-256, can't check if resources have same content.", (Throwable)var12);
               }

               throw new IllegalStateException("Multiple resources found for '" + path + "' with different content: " + urlsList);
            }
      }
   }

   private static byte[] digest(MessageDigest digest, URL url) {
      try {
         InputStream in = url.openStream();

         byte[] var5;
         try {
            byte[] bytes = new byte[8192];

            int i;
            while ((i = in.read(bytes)) != -1) {
               digest.update(bytes, 0, i);
            }

            var5 = digest.digest();
         } catch (Throwable var7) {
            if (in != null) {
               try {
                  in.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (in != null) {
            in.close();
         }

         return var5;
      } catch (IOException var8) {
         logger.debug("Can't read resource.", (Throwable)var8);
         return null;
      }
   }

   static void tryPatchShadedLibraryIdAndSign(File libraryFile, String originalName) {
      if (!new File("/Library/Developer/CommandLineTools").exists()) {
         logger.debug("Can't patch shaded library id as CommandLineTools are not installed. Consider installing CommandLineTools with 'xcode-select --install'");
      } else {
         String newId = new String(generateUniqueId(originalName.length()), CharsetUtil.UTF_8);
         if (tryExec("install_name_tool -id " + newId + " " + libraryFile.getAbsolutePath())) {
            tryExec("codesign -s - " + libraryFile.getAbsolutePath());
         }
      }
   }

   private static boolean tryExec(String cmd) {
      try {
         int exitValue = Runtime.getRuntime().exec(cmd).waitFor();
         if (exitValue != 0) {
            logger.debug("Execution of '{}' failed: {}", cmd, exitValue);
            return false;
         }

         logger.debug("Execution of '{}' succeed: {}", cmd, exitValue);
         return true;
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
      } catch (IOException var3) {
         logger.info("Execution of '{}' failed.", cmd, var3);
      } catch (SecurityException var4) {
         logger.error("Execution of '{}' failed.", cmd, var4);
      }

      return false;
   }

   private static boolean shouldShadedLibraryIdBePatched(String packagePrefix) {
      return TRY_TO_PATCH_SHADED_ID && PlatformDependent.isOsx() && !packagePrefix.isEmpty();
   }

   private static byte[] generateUniqueId(int length) {
      byte[] idBytes = new byte[length];

      for (int i = 0; i < idBytes.length; i++) {
         idBytes[i] = UNIQUE_ID_BYTES[java.util.concurrent.ThreadLocalRandom.current().nextInt(UNIQUE_ID_BYTES.length)];
      }

      return idBytes;
   }

   private static void loadLibrary(ClassLoader loader, String name, boolean absolute) {
      Throwable suppressed = null;

      try {
         try {
            Class<?> newHelper = tryToLoadClass(loader, NativeLibraryUtil.class);
            loadLibraryByHelper(newHelper, name, absolute);
            logger.debug("Successfully loaded the library {}", name);
            return;
         } catch (UnsatisfiedLinkError var5) {
            suppressed = var5;
         } catch (Exception var6) {
            suppressed = var6;
         }

         NativeLibraryUtil.loadLibrary(name, absolute);
         logger.debug("Successfully loaded the library {}", name);
      } catch (NoSuchMethodError var7) {
         if (suppressed != null) {
            ThrowableUtil.addSuppressed(var7, suppressed);
         }

         throw new LinkageError("Possible multiple incompatible native libraries on the classpath for '" + name + "'?", var7);
      } catch (UnsatisfiedLinkError var8) {
         if (suppressed != null) {
            ThrowableUtil.addSuppressed(var8, suppressed);
         }

         throw var8;
      }
   }

   private static void loadLibraryByHelper(final Class<?> helper, final String name, final boolean absolute) throws UnsatisfiedLinkError {
      Object ret = AccessController.doPrivileged(new PrivilegedAction<Object>() {
         @Override
         public Object run() {
            try {
               Method method = helper.getMethod("loadLibrary", String.class, boolean.class);
               method.setAccessible(true);
               return method.invoke(null, name, absolute);
            } catch (Exception var2) {
               return var2;
            }
         }
      });
      if (ret instanceof Throwable) {
         Throwable t = (Throwable)ret;

         assert !(t instanceof UnsatisfiedLinkError) : t + " should be a wrapper throwable";

         Throwable cause = t.getCause();
         if (cause instanceof UnsatisfiedLinkError) {
            throw (UnsatisfiedLinkError)cause;
         } else {
            UnsatisfiedLinkError ule = new UnsatisfiedLinkError(t.getMessage());
            ule.initCause(t);
            throw ule;
         }
      }
   }

   private static Class<?> tryToLoadClass(final ClassLoader loader, final Class<?> helper) throws ClassNotFoundException {
      try {
         return Class.forName(helper.getName(), false, loader);
      } catch (ClassNotFoundException var5) {
         if (loader == null) {
            throw var5;
         } else {
            try {
               final byte[] classBinary = classToByteArray(helper);
               return AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
                  public Class<?> run() {
                     try {
                        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                        defineClass.setAccessible(true);
                        return (Class<?>)defineClass.invoke(loader, helper.getName(), classBinary, 0, classBinary.length);
                     } catch (Exception var2) {
                        throw new IllegalStateException("Define class failed!", var2);
                     }
                  }
               });
            } catch (RuntimeException | Error | ClassNotFoundException var4) {
               ThrowableUtil.addSuppressed(var4, var5);
               throw var4;
            }
         }
      }
   }

   private static byte[] classToByteArray(Class<?> clazz) throws ClassNotFoundException {
      String fileName = clazz.getName();
      int lastDot = fileName.lastIndexOf(46);
      if (lastDot > 0) {
         fileName = fileName.substring(lastDot + 1);
      }

      URL classUrl = clazz.getResource(fileName + ".class");
      if (classUrl == null) {
         throw new ClassNotFoundException(clazz.getName());
      } else {
         byte[] buf = new byte[1024];
         ByteArrayOutputStream out = new ByteArrayOutputStream(4096);

         try {
            InputStream in = classUrl.openStream();

            byte[] var12;
            try {
               while ((r = in.read(buf)) != -1) {
                  out.write(buf, 0, r);
               }

               var12 = out.toByteArray();
            } catch (Throwable var10) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (in != null) {
               in.close();
            }

            return var12;
         } catch (IOException var11) {
            throw new ClassNotFoundException(clazz.getName(), var11);
         }
      }
   }

   private NativeLibraryLoader() {
   }

   static {
      String workdir = SystemPropertyUtil.get("io.netty.native.workdir");
      if (workdir != null) {
         File f = new File(workdir);
         if (!f.exists() && !f.mkdirs()) {
            throw new ExceptionInInitializerError(new IOException("Custom native workdir mkdirs failed: " + workdir));
         }

         try {
            f = f.getAbsoluteFile();
         } catch (Exception var3) {
         }

         WORKDIR = f;
         logger.debug("-Dio.netty.native.workdir: " + WORKDIR);
      } else {
         WORKDIR = PlatformDependent.tmpdir();
         logger.debug("-Dio.netty.native.workdir: " + WORKDIR + " (io.netty.tmpdir)");
      }

      DELETE_NATIVE_LIB_AFTER_LOADING = SystemPropertyUtil.getBoolean("io.netty.native.deleteLibAfterLoading", true);
      logger.debug("-Dio.netty.native.deleteLibAfterLoading: {}", DELETE_NATIVE_LIB_AFTER_LOADING);
      TRY_TO_PATCH_SHADED_ID = SystemPropertyUtil.getBoolean("io.netty.native.tryPatchShadedId", true);
      logger.debug("-Dio.netty.native.tryPatchShadedId: {}", TRY_TO_PATCH_SHADED_ID);
      DETECT_NATIVE_LIBRARY_DUPLICATES = SystemPropertyUtil.getBoolean("io.netty.native.detectNativeLibraryDuplicates", true);
      logger.debug("-Dio.netty.native.detectNativeLibraryDuplicates: {}", DETECT_NATIVE_LIBRARY_DUPLICATES);
   }

   private static final class NoexecVolumeDetector {
      private static boolean canExecuteExecutable(File file) throws IOException {
         if (file.canExecute()) {
            return true;
         } else {
            Set<PosixFilePermission> existingFilePermissions = Files.getPosixFilePermissions(file.toPath());
            Set<PosixFilePermission> executePermissions = EnumSet.of(
               PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE
            );
            if (existingFilePermissions.containsAll(executePermissions)) {
               return false;
            } else {
               Set<PosixFilePermission> newPermissions = EnumSet.copyOf(existingFilePermissions);
               newPermissions.addAll(executePermissions);
               Files.setPosixFilePermissions(file.toPath(), newPermissions);
               return file.canExecute();
            }
         }
      }
   }
}
