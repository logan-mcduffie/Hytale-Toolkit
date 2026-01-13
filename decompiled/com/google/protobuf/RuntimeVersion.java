package com.google.protobuf;

import java.util.Locale;
import java.util.logging.Logger;

public final class RuntimeVersion {
   public static final RuntimeVersion.RuntimeDomain OSS_DOMAIN = RuntimeVersion.RuntimeDomain.PUBLIC;
   public static final int OSS_MAJOR = 4;
   public static final int OSS_MINOR = 33;
   public static final int OSS_PATCH = 0;
   public static final String OSS_SUFFIX = "";
   public static final RuntimeVersion.RuntimeDomain DOMAIN = OSS_DOMAIN;
   public static final int MAJOR = 4;
   public static final int MINOR = 33;
   public static final int PATCH = 0;
   public static final String SUFFIX = "";
   private static final int MAX_WARNING_COUNT = 20;
   static int majorWarningLoggedCount = 0;
   static int minorWarningLoggedCount = 0;
   static boolean preleaseRuntimeWarningLogged = false;
   private static final String VERSION_STRING = versionString(4, 33, 0, "");
   private static final Logger logger = Logger.getLogger(RuntimeVersion.class.getName());

   public static void validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain domain, int major, int minor, int patch, String suffix, String location) {
      validateProtobufGencodeVersionImpl(domain, major, minor, patch, suffix, location);
   }

   private static void validateProtobufGencodeVersionImpl(RuntimeVersion.RuntimeDomain domain, int major, int minor, int patch, String suffix, String location) {
      if (!checkDisabled()) {
         if (major < 0 || minor < 0 || patch < 0) {
            throw new RuntimeVersion.ProtobufRuntimeVersionException("Invalid gencode version: " + versionString(major, minor, patch, suffix));
         } else if (domain != DOMAIN) {
            throw new RuntimeVersion.ProtobufRuntimeVersionException(
               String.format(
                  Locale.US,
                  "Detected mismatched Protobuf Gencode/Runtime domains when loading %s: gencode %s, runtime %s. Cross-domain usage of Protobuf is not supported.",
                  location,
                  domain,
                  DOMAIN
               )
            );
         } else {
            String gencodeVersionString = null;
            if (!"".isEmpty() && !preleaseRuntimeWarningLogged) {
               if (gencodeVersionString == null) {
                  gencodeVersionString = versionString(major, minor, patch, suffix);
               }

               logger.warning(
                  String.format(
                     Locale.US,
                     " Protobuf prelease version %s in use. This is not recommended for production use.\n You can ignore this message if you are deliberately testing a prerelease. Otherwise you should switch to a non-prerelease Protobuf version.",
                     VERSION_STRING
                  )
               );
               preleaseRuntimeWarningLogged = true;
            }

            if (major != 4 || minor != 33 || patch != 0 || !suffix.equals("")) {
               if (major != 4) {
                  if (major != 3 || majorWarningLoggedCount >= 20) {
                     throw new RuntimeVersion.ProtobufRuntimeVersionException(
                        String.format(
                           Locale.US,
                           "Detected mismatched Protobuf Gencode/Runtime major versions when loading %s: gencode %s, runtime %s. Same major version is required.",
                           location,
                           versionString(major, minor, patch, suffix),
                           VERSION_STRING
                        )
                     );
                  }

                  gencodeVersionString = versionString(major, minor, patch, suffix);
                  logger.warning(
                     String.format(
                        Locale.US,
                        " Protobuf gencode version %s is exactly one major version older than the runtime version %s at %s. Please update the gencode to avoid compatibility violations in the next runtime release.",
                        gencodeVersionString,
                        VERSION_STRING,
                        location
                     )
                  );
                  majorWarningLoggedCount++;
               }

               if (33 < minor || minor == 33 && 0 < patch) {
                  if (gencodeVersionString == null) {
                     gencodeVersionString = versionString(major, minor, patch, suffix);
                  }

                  throw new RuntimeVersion.ProtobufRuntimeVersionException(
                     String.format(
                        Locale.US,
                        "Detected incompatible Protobuf Gencode/Runtime versions when loading %s: gencode %s, runtime %s. Runtime version cannot be older than the linked gencode version.",
                        location,
                        gencodeVersionString,
                        VERSION_STRING
                     )
                  );
               } else if (!suffix.isEmpty() || !"".isEmpty()) {
                  if (!suffix.isEmpty()) {
                     if (gencodeVersionString == null) {
                        gencodeVersionString = versionString(major, minor, patch, suffix);
                     }

                     throw new RuntimeVersion.ProtobufRuntimeVersionException(
                        String.format(
                           Locale.US,
                           "Detected mismatched Protobuf Gencode/Runtime version suffixes when loading %s: gencode %s, runtime %s. Prerelease gencode must be used with the same runtime.",
                           location,
                           gencodeVersionString,
                           VERSION_STRING
                        )
                     );
                  } else if (major == 4 && minor == 33 && patch == 0) {
                     if (gencodeVersionString == null) {
                        gencodeVersionString = versionString(major, minor, patch, suffix);
                     }

                     throw new RuntimeVersion.ProtobufRuntimeVersionException(
                        String.format(
                           Locale.US,
                           "Detected mismatched Protobuf Gencode/Runtime version suffixes when loading %s: gencode %s, runtime %s. Prelease runtimes must only be used with exact match gencode (including suffix) or non-prerelease gencode versions of a lower version.",
                           location,
                           gencodeVersionString,
                           VERSION_STRING
                        )
                     );
                  }
               }
            }
         }
      }
   }

   private static String versionString(int major, int minor, int patch, String suffix) {
      return String.format(Locale.US, "%d.%d.%d%s", major, minor, patch, suffix);
   }

   private static boolean checkDisabled() {
      String disableFlag = System.getenv("TEMPORARILY_DISABLE_PROTOBUF_VERSION_CHECK");
      return disableFlag != null && disableFlag.equals("true");
   }

   private RuntimeVersion() {
   }

   public static final class ProtobufRuntimeVersionException extends RuntimeException {
      public ProtobufRuntimeVersionException(String message) {
         super(message);
      }
   }

   public static enum RuntimeDomain {
      GOOGLE_INTERNAL,
      PUBLIC;
   }
}
