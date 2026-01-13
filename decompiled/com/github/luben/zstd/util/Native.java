package com.github.luben.zstd.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;

public enum Native {
   private static final String nativePathOverride = "ZstdNativePath";
   private static final String tempFolderOverride = "ZstdTempFolder";
   private static final String libnameShort = "zstd-jni-1.5.7-6";
   private static final String libname = "libzstd-jni-1.5.7-6";
   private static final String errorMsg = "Unsupported OS/arch, cannot find "
      + resourceName()
      + " or load "
      + "zstd-jni-1.5.7-6"
      + " from system libraries. Please try building from source the jar or providing "
      + "libzstd-jni-1.5.7-6"
      + " in your system.";
   private static AtomicBoolean loaded = new AtomicBoolean(false);

   private static String osName() {
      String var0 = System.getProperty("os.name").toLowerCase().replace(' ', '_');
      if (var0.startsWith("win")) {
         return "win";
      } else {
         return var0.startsWith("mac") ? "darwin" : var0;
      }
   }

   private static String libExtension() {
      if (osName().contains("os_x") || osName().contains("darwin")) {
         return "dylib";
      } else {
         return osName().contains("win") ? "dll" : "so";
      }
   }

   private static String resourceName() {
      String var0 = osName();
      String var1 = System.getProperty("os.arch");
      if (var0.equals("darwin") && var1.equals("amd64")) {
         var1 = "x86_64";
      }

      return "/" + var0 + "/" + var1 + "/" + "libzstd-jni-1.5.7-6" + "." + libExtension();
   }

   public static synchronized void assumeLoaded() {
      loaded.set(true);
   }

   public static synchronized boolean isLoaded() {
      return loaded.get();
   }

   private static void loadLibrary(final String var0) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary(var0);
            return null;
         }
      });
   }

   private static void loadLibraryFile(final String var0) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.load(var0);
            return null;
         }
      });
   }

   public static synchronized void load() {
      String var0 = System.getProperty("ZstdTempFolder");
      if (var0 == null) {
         load(null);
      } else {
         load(new File(var0));
      }
   }

   public static synchronized void load(File var0) {
      if (!loaded.get()) {
         String var1 = resourceName();
         String var2 = System.getProperty("ZstdNativePath");
         if (var2 != null) {
            loadLibraryFile(var2);
            loaded.set(true);
         } else {
            try {
               Class.forName("org.osgi.framework.BundleEvent");
               loadLibrary("libzstd-jni-1.5.7-6");
               loaded.set(true);
            } catch (Throwable var28) {
               InputStream var3 = Native.class.getResourceAsStream(var1);
               if (var3 == null) {
                  try {
                     loadLibrary("zstd-jni-1.5.7-6");
                     loaded.set(true);
                  } catch (UnsatisfiedLinkError var22) {
                     UnsatisfiedLinkError var29 = new UnsatisfiedLinkError(var22.getMessage() + "\n" + errorMsg);
                     var29.setStackTrace(var22.getStackTrace());
                     throw var29;
                  }
               } else {
                  File var4 = null;
                  FileOutputStream var5 = null;

                  try {
                     var4 = File.createTempFile("libzstd-jni-1.5.7-6", "." + libExtension(), var0);
                     var4.deleteOnExit();
                     var5 = new FileOutputStream(var4);
                     byte[] var6 = new byte[4096];

                     while (true) {
                        int var30 = var3.read(var6);
                        if (var30 == -1) {
                           try {
                              var5.flush();
                              var5.close();
                              var5 = null;
                           } catch (IOException var25) {
                           }

                           try {
                              loadLibraryFile(var4.getAbsolutePath());
                           } catch (UnsatisfiedLinkError var24) {
                              try {
                                 loadLibrary("zstd-jni-1.5.7-6");
                              } catch (UnsatisfiedLinkError var23) {
                                 UnsatisfiedLinkError var9 = new UnsatisfiedLinkError(var24.getMessage() + "\n" + var23.getMessage() + "\n" + errorMsg);
                                 var9.setStackTrace(var23.getStackTrace());
                                 throw var9;
                              }
                           }

                           loaded.set(true);
                           return;
                        }

                        var5.write(var6, 0, var30);
                     }
                  } catch (IOException var26) {
                     ExceptionInInitializerError var7 = new ExceptionInInitializerError("Cannot unpack libzstd-jni-1.5.7-6: " + var26.getMessage());
                     var7.setStackTrace(var26.getStackTrace());
                     throw var7;
                  } finally {
                     try {
                        var3.close();
                        if (var5 != null) {
                           var5.close();
                        }

                        if (var4 != null && var4.exists()) {
                           var4.delete();
                        }
                     } catch (IOException var21) {
                     }
                  }
               }
            }
         }
      }
   }
}
