package io.netty.handler.ssl.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class OpenJdkSelfSignedCertGenerator {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenJdkSelfSignedCertGenerator.class);
   private static final MethodHandle CERT_INFO_SET_HANDLE;
   private static final MethodHandle ISSUER_NAME_CONSTRUCTOR;
   private static final MethodHandle CERT_IMPL_CONSTRUCTOR;
   private static final MethodHandle X509_CERT_INFO_CONSTRUCTOR;
   private static final MethodHandle CERTIFICATE_VERSION_CONSTRUCTOR;
   private static final MethodHandle CERTIFICATE_SUBJECT_NAME_CONSTRUCTOR;
   private static final MethodHandle X500_NAME_CONSTRUCTOR;
   private static final MethodHandle CERTIFICATE_SERIAL_NUMBER_CONSTRUCTOR;
   private static final MethodHandle CERTIFICATE_VALIDITY_CONSTRUCTOR;
   private static final MethodHandle CERTIFICATE_X509_KEY_CONSTRUCTOR;
   private static final MethodHandle CERTIFICATE_ALORITHM_ID_CONSTRUCTOR;
   private static final MethodHandle CERT_IMPL_GET_HANDLE;
   private static final MethodHandle CERT_IMPL_SIGN_HANDLE;
   private static final MethodHandle ALGORITHM_ID_GET_HANDLE;
   private static final boolean SUPPORTED;

   static String[] generate(String fqdn, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter, String algorithm) throws Exception {
      if (!SUPPORTED) {
         throw new UnsupportedOperationException(OpenJdkSelfSignedCertGenerator.class.getSimpleName() + " not supported on the used JDK version");
      } else {
         try {
            PrivateKey key = keypair.getPrivate();
            Object info = (Object)X509_CERT_INFO_CONSTRUCTOR.invoke();
            Object owner = (Object)X500_NAME_CONSTRUCTOR.invoke((String)("CN=" + fqdn));
            CERT_INFO_SET_HANDLE.invoke((Object)info, (String)"version", (Object)(Object)CERTIFICATE_VERSION_CONSTRUCTOR.invoke((int)2));
            CERT_INFO_SET_HANDLE.invoke(
               (Object)info, (String)"serialNumber", (Object)(Object)CERTIFICATE_SERIAL_NUMBER_CONSTRUCTOR.invoke((BigInteger)(new BigInteger(64, random)))
            );

            try {
               CERT_INFO_SET_HANDLE.invoke((Object)info, (String)"subject", (Object)(Object)CERTIFICATE_SUBJECT_NAME_CONSTRUCTOR.invoke((Object)owner));
            } catch (CertificateException var12) {
               CERT_INFO_SET_HANDLE.invoke((Object)info, (String)"subject", (Object)owner);
            }

            try {
               CERT_INFO_SET_HANDLE.invoke((Object)info, (String)"issuer", (Object)(Object)ISSUER_NAME_CONSTRUCTOR.invoke((Object)owner));
            } catch (CertificateException var11) {
               CERT_INFO_SET_HANDLE.invoke((Object)info, (String)"issuer", (Object)owner);
            }

            CERT_INFO_SET_HANDLE.invoke(
               (Object)info, (String)"validity", (Object)(Object)CERTIFICATE_VALIDITY_CONSTRUCTOR.invoke((Date)notBefore, (Date)notAfter)
            );
            CERT_INFO_SET_HANDLE.invoke((Object)info, (String)"key", (Object)(Object)CERTIFICATE_X509_KEY_CONSTRUCTOR.invoke((PublicKey)keypair.getPublic()));
            CERT_INFO_SET_HANDLE.invoke(
               (Object)info,
               (String)"algorithmID",
               (Object)(Object)CERTIFICATE_ALORITHM_ID_CONSTRUCTOR.invoke((Object)(Object)ALGORITHM_ID_GET_HANDLE.invoke((String)"1.2.840.113549.1.1.11"))
            );
            Object cert = (Object)CERT_IMPL_CONSTRUCTOR.invoke((Object)info);
            CERT_IMPL_SIGN_HANDLE.invoke((Object)cert, (PrivateKey)key, (Object)(algorithm.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256withRSA"));
            CERT_INFO_SET_HANDLE.invoke(
               (Object)info, (String)"algorithmID.algorithm", (Object)(Object)CERT_IMPL_GET_HANDLE.invoke((Object)cert, (String)"x509.algorithm")
            );
            cert = (Object)CERT_IMPL_CONSTRUCTOR.invoke((Object)info);
            CERT_IMPL_SIGN_HANDLE.invoke((Object)cert, (PrivateKey)key, (Object)(algorithm.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256withRSA"));
            X509Certificate x509Cert = (X509Certificate)cert;
            x509Cert.verify(keypair.getPublic());
            return SelfSignedCertificate.newSelfSignedCertificate(fqdn, key, x509Cert);
         } catch (Throwable var13) {
            if (var13 instanceof Exception) {
               throw (Exception)var13;
            } else if (var13 instanceof Error) {
               throw (Error)var13;
            } else {
               throw new IllegalStateException(var13);
            }
         }
      }
   }

   private OpenJdkSelfSignedCertGenerator() {
   }

   static {
      final Lookup lookup = MethodHandles.lookup();
      MethodHandle certInfoSetHandle = null;
      MethodHandle x509CertInfoConstructor = null;
      MethodHandle issuerNameConstructor = null;
      MethodHandle certImplConstructor = null;
      MethodHandle x500NameConstructor = null;
      MethodHandle certificateVersionConstructor = null;
      MethodHandle certificateSubjectNameConstructor = null;
      MethodHandle certificateSerialNumberConstructor = null;
      MethodHandle certificateValidityConstructor = null;
      MethodHandle certificateX509KeyConstructor = null;
      MethodHandle certificateAlgorithmIdConstructor = null;
      MethodHandle certImplGetHandle = null;
      MethodHandle certImplSignHandle = null;
      MethodHandle algorithmIdGetHandle = null;

      boolean supported;
      try {
         Object maybeClasses = AccessController.doPrivileged(
            new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     List<Class<?>> classes = new ArrayList<>();
                     classes.add(Class.forName("sun.security.x509.X509CertInfo", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                     classes.add(Class.forName("sun.security.x509.X500Name", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                     classes.add(
                        Class.forName("sun.security.x509.CertificateIssuerName", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class))
                     );
                     classes.add(Class.forName("sun.security.x509.X509CertImpl", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                     classes.add(
                        Class.forName("sun.security.x509.CertificateVersion", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class))
                     );
                     classes.add(
                        Class.forName("sun.security.x509.CertificateSubjectName", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class))
                     );
                     classes.add(
                        Class.forName(
                           "sun.security.x509.CertificateSerialNumber", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)
                        )
                     );
                     classes.add(
                        Class.forName("sun.security.x509.CertificateValidity", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class))
                     );
                     classes.add(
                        Class.forName("sun.security.x509.CertificateX509Key", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class))
                     );
                     classes.add(Class.forName("sun.security.x509.AlgorithmId", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                     classes.add(
                        Class.forName("sun.security.x509.CertificateAlgorithmId", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class))
                     );
                     return classes;
                  } catch (Throwable var2) {
                     return var2;
                  }
               }
            }
         );
         if (!(maybeClasses instanceof List)) {
            throw (Throwable)maybeClasses;
         }

         List<Class<?>> classes = (List<Class<?>>)maybeClasses;
         final Class<?> x509CertInfoClass = classes.get(0);
         final Class<?> x500NameClass = classes.get(1);
         final Class<?> certificateIssuerNameClass = classes.get(2);
         final Class<?> x509CertImplClass = classes.get(3);
         final Class<?> certificateVersionClass = classes.get(4);
         final Class<?> certificateSubjectNameClass = classes.get(5);
         final Class<?> certificateSerialNumberClass = classes.get(6);
         final Class<?> certificateValidityClass = classes.get(7);
         final Class<?> certificateX509KeyClass = classes.get(8);
         final Class<?> algorithmIdClass = classes.get(9);
         final Class<?> certificateAlgorithmIdClass = classes.get(10);
         Object maybeConstructors = AccessController.doPrivileged(
            new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     List<MethodHandle> constructors = new ArrayList<>();
                     constructors.add(lookup.unreflectConstructor(x509CertInfoClass.getConstructor()).asType(MethodType.methodType(x509CertInfoClass)));
                     constructors.add(
                        lookup.unreflectConstructor(certificateIssuerNameClass.getConstructor(x500NameClass))
                           .asType(MethodType.methodType(certificateIssuerNameClass, x500NameClass))
                     );
                     constructors.add(
                        lookup.unreflectConstructor(x509CertImplClass.getConstructor(x509CertInfoClass))
                           .asType(MethodType.methodType(x509CertImplClass, x509CertInfoClass))
                     );
                     constructors.add(
                        lookup.unreflectConstructor(x500NameClass.getConstructor(String.class)).asType(MethodType.methodType(x500NameClass, String.class))
                     );
                     constructors.add(
                        lookup.unreflectConstructor(certificateVersionClass.getConstructor(int.class))
                           .asType(MethodType.methodType(certificateVersionClass, int.class))
                     );
                     constructors.add(
                        lookup.unreflectConstructor(certificateSubjectNameClass.getConstructor(x500NameClass))
                           .asType(MethodType.methodType(certificateSubjectNameClass, x500NameClass))
                     );
                     constructors.add(
                        lookup.unreflectConstructor(certificateSerialNumberClass.getConstructor(BigInteger.class))
                           .asType(MethodType.methodType(certificateSerialNumberClass, BigInteger.class))
                     );
                     constructors.add(
                        lookup.unreflectConstructor(certificateValidityClass.getConstructor(Date.class, Date.class))
                           .asType(MethodType.methodType(certificateValidityClass, Date.class, Date.class))
                     );
                     constructors.add(
                        lookup.unreflectConstructor(certificateX509KeyClass.getConstructor(PublicKey.class))
                           .asType(MethodType.methodType(certificateX509KeyClass, PublicKey.class))
                     );
                     constructors.add(
                        lookup.unreflectConstructor(certificateAlgorithmIdClass.getConstructor(algorithmIdClass))
                           .asType(MethodType.methodType(certificateAlgorithmIdClass, algorithmIdClass))
                     );
                     return constructors;
                  } catch (Throwable var2) {
                     return var2;
                  }
               }
            }
         );
         if (!(maybeConstructors instanceof List)) {
            throw (Throwable)maybeConstructors;
         }

         List<MethodHandle> constructorList = (List<MethodHandle>)maybeConstructors;
         x509CertInfoConstructor = constructorList.get(0);
         issuerNameConstructor = constructorList.get(1);
         certImplConstructor = constructorList.get(2);
         x500NameConstructor = constructorList.get(3);
         certificateVersionConstructor = constructorList.get(4);
         certificateSubjectNameConstructor = constructorList.get(5);
         certificateSerialNumberConstructor = constructorList.get(6);
         certificateValidityConstructor = constructorList.get(7);
         certificateX509KeyConstructor = constructorList.get(8);
         certificateAlgorithmIdConstructor = constructorList.get(9);
         Object maybeMethodHandles = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  List<MethodHandle> methods = new ArrayList<>();
                  methods.add(lookup.findVirtual(x509CertInfoClass, "set", MethodType.methodType(void.class, String.class, Object.class)));
                  methods.add(lookup.findVirtual(x509CertImplClass, "get", MethodType.methodType(Object.class, String.class)));
                  methods.add(lookup.findVirtual(x509CertImplClass, "sign", MethodType.methodType(void.class, PrivateKey.class, String.class)));
                  methods.add(lookup.findStatic(algorithmIdClass, "get", MethodType.methodType(algorithmIdClass, String.class)));
                  return methods;
               } catch (Throwable var2) {
                  return var2;
               }
            }
         });
         if (!(maybeMethodHandles instanceof List)) {
            throw (Throwable)maybeMethodHandles;
         }

         List<MethodHandle> methodHandles = (List<MethodHandle>)maybeMethodHandles;
         certInfoSetHandle = methodHandles.get(0);
         certImplGetHandle = methodHandles.get(1);
         certImplSignHandle = methodHandles.get(2);
         algorithmIdGetHandle = methodHandles.get(3);
         supported = true;
      } catch (Throwable var31) {
         supported = false;
         logger.debug(OpenJdkSelfSignedCertGenerator.class.getSimpleName() + " not supported", var31);
      }

      CERT_INFO_SET_HANDLE = certInfoSetHandle;
      X509_CERT_INFO_CONSTRUCTOR = x509CertInfoConstructor;
      ISSUER_NAME_CONSTRUCTOR = issuerNameConstructor;
      CERTIFICATE_VERSION_CONSTRUCTOR = certificateVersionConstructor;
      CERTIFICATE_SUBJECT_NAME_CONSTRUCTOR = certificateSubjectNameConstructor;
      CERT_IMPL_CONSTRUCTOR = certImplConstructor;
      X500_NAME_CONSTRUCTOR = x500NameConstructor;
      CERTIFICATE_SERIAL_NUMBER_CONSTRUCTOR = certificateSerialNumberConstructor;
      CERTIFICATE_VALIDITY_CONSTRUCTOR = certificateValidityConstructor;
      CERTIFICATE_X509_KEY_CONSTRUCTOR = certificateX509KeyConstructor;
      CERT_IMPL_GET_HANDLE = certImplGetHandle;
      CERT_IMPL_SIGN_HANDLE = certImplSignHandle;
      ALGORITHM_ID_GET_HANDLE = algorithmIdGetHandle;
      CERTIFICATE_ALORITHM_ID_CONSTRUCTOR = certificateAlgorithmIdConstructor;
      SUPPORTED = supported;
   }
}
