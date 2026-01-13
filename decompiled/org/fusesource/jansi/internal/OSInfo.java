package org.fusesource.jansi.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;

public class OSInfo {
   public static final String X86 = "x86";
   public static final String X86_64 = "x86_64";
   public static final String IA64_32 = "ia64_32";
   public static final String IA64 = "ia64";
   public static final String PPC = "ppc";
   public static final String PPC64 = "ppc64";
   public static final String ARM64 = "arm64";
   private static final HashMap<String, String> archMapping = new HashMap<>();

   public static void main(String[] args) {
      if (args.length >= 1) {
         if ("--os".equals(args[0])) {
            System.out.print(getOSName());
            return;
         }

         if ("--arch".equals(args[0])) {
            System.out.print(getArchName());
            return;
         }
      }

      System.out.print(getNativeLibFolderPathForCurrentOS());
   }

   public static String getNativeLibFolderPathForCurrentOS() {
      return getOSName() + "/" + getArchName();
   }

   public static String getOSName() {
      return translateOSNameToFolderName(System.getProperty("os.name"));
   }

   public static boolean isAndroid() {
      return System.getProperty("java.runtime.name", "").toLowerCase().contains("android");
   }

   public static boolean isAlpine() {
      try {
         for (String line : Files.readAllLines(Paths.get("/etc/os-release"))) {
            if (line.startsWith("ID") && line.toLowerCase(Locale.ROOT).contains("alpine")) {
               return true;
            }
         }
      } catch (Throwable var2) {
      }

      return false;
   }

   static String getHardwareName() {
      try {
         Process p = Runtime.getRuntime().exec("uname -m");
         p.waitFor();
         InputStream in = p.getInputStream();

         String var2;
         try {
            var2 = readFully(in);
         } finally {
            in.close();
         }

         return var2;
      } catch (Throwable var7) {
         System.err.println("Error while running uname -m: " + var7.getMessage());
         return "unknown";
      }
   }

   private static String readFully(InputStream in) throws IOException {
      int readLen = 0;
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      byte[] buf = new byte[32];

      while ((readLen = in.read(buf, 0, buf.length)) >= 0) {
         b.write(buf, 0, readLen);
      }

      return b.toString();
   }

   static String resolveArmArchType() {
      if (System.getProperty("os.name").contains("Linux")) {
         String armType = getHardwareName();
         if (armType.startsWith("armv6")) {
            return "armv6";
         }

         if (armType.startsWith("armv7")) {
            return "armv7";
         }

         if (armType.startsWith("armv5")) {
            return "arm";
         }

         if (armType.equals("aarch64")) {
            return "arm64";
         }

         String abi = System.getProperty("sun.arch.abi");
         if (abi != null && abi.startsWith("gnueabihf")) {
            return "armv7";
         }
      }

      return "arm";
   }

   public static String getArchName() {
      String osArch = System.getProperty("os.arch");
      if (isAndroid()) {
         return "android-arm";
      } else {
         if (osArch.startsWith("arm")) {
            osArch = resolveArmArchType();
         } else {
            String lc = osArch.toLowerCase(Locale.US);
            if (archMapping.containsKey(lc)) {
               return archMapping.get(lc);
            }
         }

         return translateArchNameToFolderName(osArch);
      }
   }

   static String translateOSNameToFolderName(String osName) {
      if (osName.contains("Windows")) {
         return "Windows";
      } else if (osName.contains("Mac") || osName.contains("Darwin")) {
         return "Mac";
      } else if (osName.contains("Linux")) {
         return "Linux";
      } else {
         return osName.contains("AIX") ? "AIX" : osName.replaceAll("\\W", "");
      }
   }

   static String translateArchNameToFolderName(String archName) {
      return archName.replaceAll("\\W", "");
   }

   static {
      archMapping.put("x86", "x86");
      archMapping.put("i386", "x86");
      archMapping.put("i486", "x86");
      archMapping.put("i586", "x86");
      archMapping.put("i686", "x86");
      archMapping.put("pentium", "x86");
      archMapping.put("x86_64", "x86_64");
      archMapping.put("amd64", "x86_64");
      archMapping.put("em64t", "x86_64");
      archMapping.put("universal", "x86_64");
      archMapping.put("ia64", "ia64");
      archMapping.put("ia64w", "ia64");
      archMapping.put("ia64_32", "ia64_32");
      archMapping.put("ia64n", "ia64_32");
      archMapping.put("ppc", "ppc");
      archMapping.put("power", "ppc");
      archMapping.put("powerpc", "ppc");
      archMapping.put("power_pc", "ppc");
      archMapping.put("power_rs", "ppc");
      archMapping.put("ppc64", "ppc64");
      archMapping.put("power64", "ppc64");
      archMapping.put("powerpc64", "ppc64");
      archMapping.put("power_pc64", "ppc64");
      archMapping.put("power_rs64", "ppc64");
      archMapping.put("aarch64", "arm64");
   }
}
