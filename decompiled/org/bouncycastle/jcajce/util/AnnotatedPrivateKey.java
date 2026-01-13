package org.bouncycastle.jcajce.util;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnnotatedPrivateKey implements PrivateKey {
   public static final String LABEL = "label";
   private final PrivateKey key;
   private final Map<String, Object> annotations;

   AnnotatedPrivateKey(PrivateKey var1, String var2) {
      this.key = var1;
      this.annotations = Collections.singletonMap("label", var2);
   }

   AnnotatedPrivateKey(PrivateKey var1, Map<String, Object> var2) {
      this.key = var1;
      this.annotations = var2;
   }

   public PrivateKey getKey() {
      return this.key;
   }

   public Map<String, Object> getAnnotations() {
      return this.annotations;
   }

   @Override
   public String getAlgorithm() {
      return this.key.getAlgorithm();
   }

   public Object getAnnotation(String var1) {
      return this.annotations.get(var1);
   }

   public AnnotatedPrivateKey addAnnotation(String var1, Object var2) {
      HashMap var3 = new HashMap<>(this.annotations);
      var3.put(var1, var2);
      return new AnnotatedPrivateKey(this.key, Collections.unmodifiableMap(var3));
   }

   public AnnotatedPrivateKey removeAnnotation(String var1) {
      HashMap var2 = new HashMap<>(this.annotations);
      var2.remove(var1);
      return new AnnotatedPrivateKey(this.key, Collections.unmodifiableMap(var2));
   }

   @Override
   public String getFormat() {
      return this.key.getFormat();
   }

   @Override
   public byte[] getEncoded() {
      return this.key.getEncoded();
   }

   @Override
   public int hashCode() {
      return this.key.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof AnnotatedPrivateKey ? this.key.equals(((AnnotatedPrivateKey)var1).key) : this.key.equals(var1);
   }

   @Override
   public String toString() {
      return this.annotations.containsKey("label") ? this.annotations.get("label").toString() : this.key.toString();
   }
}
