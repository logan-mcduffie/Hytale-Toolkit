package com.nimbusds.jose.proc;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JOSEMatcher {
   private final Set<Class<? extends JOSEObject>> classes;
   private final Set<Algorithm> algs;
   private final Set<EncryptionMethod> encs;
   private final Set<URI> jkus;
   private final Set<String> kids;

   public JOSEMatcher(Set<Class<? extends JOSEObject>> classes, Set<Algorithm> algs, Set<EncryptionMethod> encs, Set<URI> jkus, Set<String> kids) {
      this.classes = classes;
      this.algs = algs;
      this.encs = encs;
      this.jkus = jkus;
      this.kids = kids;
   }

   public Set<Class<? extends JOSEObject>> getJOSEClasses() {
      return this.classes;
   }

   public Set<Algorithm> getAlgorithms() {
      return this.algs;
   }

   public Set<EncryptionMethod> getEncryptionMethods() {
      return this.encs;
   }

   public Set<URI> getJWKURLs() {
      return this.jkus;
   }

   public Set<String> getKeyIDs() {
      return this.kids;
   }

   public boolean matches(JOSEObject joseObject) {
      if (this.classes != null) {
         boolean pass = false;

         for (Class<? extends JOSEObject> c : this.classes) {
            if (c != null && c.isInstance(joseObject)) {
               pass = true;
               break;
            }
         }

         if (!pass) {
            return false;
         }
      }

      if (this.algs != null && !this.algs.contains(joseObject.getHeader().getAlgorithm())) {
         return false;
      } else {
         if (this.encs != null) {
            if (!(joseObject instanceof JWEObject)) {
               return false;
            }

            JWEObject jweObject = (JWEObject)joseObject;
            if (!this.encs.contains(jweObject.getHeader().getEncryptionMethod())) {
               return false;
            }
         }

         if (this.jkus != null) {
            URI jku;
            if (joseObject instanceof JWSObject) {
               jku = ((JWSObject)joseObject).getHeader().getJWKURL();
            } else if (joseObject instanceof JWEObject) {
               jku = ((JWEObject)joseObject).getHeader().getJWKURL();
            } else {
               jku = null;
            }

            if (!this.jkus.contains(jku)) {
               return false;
            }
         }

         if (this.kids != null) {
            String kid;
            if (joseObject instanceof JWSObject) {
               kid = ((JWSObject)joseObject).getHeader().getKeyID();
            } else if (joseObject instanceof JWEObject) {
               kid = ((JWEObject)joseObject).getHeader().getKeyID();
            } else {
               kid = null;
            }

            return this.kids.contains(kid);
         } else {
            return true;
         }
      }
   }

   public static class Builder {
      private Set<Class<? extends JOSEObject>> classes;
      private Set<Algorithm> algs;
      private Set<EncryptionMethod> encs;
      private Set<URI> jkus;
      private Set<String> kids;

      public JOSEMatcher.Builder joseClass(Class<? extends JOSEObject> clazz) {
         if (clazz == null) {
            this.classes = null;
         } else {
            this.classes = new HashSet<>(Collections.singletonList(clazz));
         }

         return this;
      }

      public JOSEMatcher.Builder joseClasses(Class<? extends JOSEObject>... classes) {
         this.joseClasses(new HashSet<>(Arrays.asList(classes)));
         return this;
      }

      public JOSEMatcher.Builder joseClasses(Set<Class<? extends JOSEObject>> classes) {
         this.classes = classes;
         return this;
      }

      public JOSEMatcher.Builder algorithm(Algorithm alg) {
         if (alg == null) {
            this.algs = null;
         } else {
            this.algs = new HashSet<>(Collections.singletonList(alg));
         }

         return this;
      }

      public JOSEMatcher.Builder algorithms(Algorithm... algs) {
         this.algorithms(new HashSet<>(Arrays.asList(algs)));
         return this;
      }

      public JOSEMatcher.Builder algorithms(Set<Algorithm> algs) {
         this.algs = algs;
         return this;
      }

      public JOSEMatcher.Builder encryptionMethod(EncryptionMethod enc) {
         if (enc == null) {
            this.encs = null;
         } else {
            this.encs = new HashSet<>(Collections.singletonList(enc));
         }

         return this;
      }

      public JOSEMatcher.Builder encryptionMethods(EncryptionMethod... encs) {
         this.encryptionMethods(new HashSet<>(Arrays.asList(encs)));
         return this;
      }

      public JOSEMatcher.Builder encryptionMethods(Set<EncryptionMethod> encs) {
         this.encs = encs;
         return this;
      }

      public JOSEMatcher.Builder jwkURL(URI jku) {
         if (jku == null) {
            this.jkus = null;
         } else {
            this.jkus = new HashSet<>(Collections.singletonList(jku));
         }

         return this;
      }

      public JOSEMatcher.Builder jwkURLs(URI... jkus) {
         this.jwkURLs(new HashSet<>(Arrays.asList(jkus)));
         return this;
      }

      public JOSEMatcher.Builder jwkURLs(Set<URI> jkus) {
         this.jkus = jkus;
         return this;
      }

      public JOSEMatcher.Builder keyID(String kid) {
         if (kid == null) {
            this.kids = null;
         } else {
            this.kids = new HashSet<>(Collections.singletonList(kid));
         }

         return this;
      }

      public JOSEMatcher.Builder keyIDs(String... ids) {
         this.keyIDs(new HashSet<>(Arrays.asList(ids)));
         return this;
      }

      public JOSEMatcher.Builder keyIDs(Set<String> kids) {
         this.kids = kids;
         return this;
      }

      public JOSEMatcher build() {
         return new JOSEMatcher(this.classes, this.algs, this.encs, this.jkus, this.kids);
      }
   }
}
