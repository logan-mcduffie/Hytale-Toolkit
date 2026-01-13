package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Deprecated
public class DefaultCookie extends io.netty.handler.codec.http.cookie.DefaultCookie implements Cookie {
   private String comment;
   private String commentUrl;
   private boolean discard;
   private Set<Integer> ports = Collections.emptySet();
   private Set<Integer> unmodifiablePorts = this.ports;
   private int version;

   public DefaultCookie(String name, String value) {
      super(name, value);
   }

   @Deprecated
   @Override
   public String getName() {
      return this.name();
   }

   @Deprecated
   @Override
   public String getValue() {
      return this.value();
   }

   @Deprecated
   @Override
   public String getDomain() {
      return this.domain();
   }

   @Deprecated
   @Override
   public String getPath() {
      return this.path();
   }

   @Deprecated
   @Override
   public String getComment() {
      return this.comment();
   }

   @Deprecated
   @Override
   public String comment() {
      return this.comment;
   }

   @Deprecated
   @Override
   public void setComment(String comment) {
      this.comment = this.validateValue("comment", comment);
   }

   @Deprecated
   @Override
   public String getCommentUrl() {
      return this.commentUrl();
   }

   @Deprecated
   @Override
   public String commentUrl() {
      return this.commentUrl;
   }

   @Deprecated
   @Override
   public void setCommentUrl(String commentUrl) {
      this.commentUrl = this.validateValue("commentUrl", commentUrl);
   }

   @Deprecated
   @Override
   public boolean isDiscard() {
      return this.discard;
   }

   @Deprecated
   @Override
   public void setDiscard(boolean discard) {
      this.discard = discard;
   }

   @Deprecated
   @Override
   public Set<Integer> getPorts() {
      return this.ports();
   }

   @Deprecated
   @Override
   public Set<Integer> ports() {
      if (this.unmodifiablePorts == null) {
         this.unmodifiablePorts = Collections.unmodifiableSet(this.ports);
      }

      return this.unmodifiablePorts;
   }

   @Deprecated
   @Override
   public void setPorts(int... ports) {
      ObjectUtil.checkNotNull(ports, "ports");
      int[] portsCopy = (int[])ports.clone();
      if (portsCopy.length == 0) {
         this.unmodifiablePorts = this.ports = Collections.emptySet();
      } else {
         Set<Integer> newPorts = new TreeSet<>();

         for (int p : portsCopy) {
            if (p <= 0 || p > 65535) {
               throw new IllegalArgumentException("port out of range: " + p);
            }

            newPorts.add(p);
         }

         this.ports = newPorts;
         this.unmodifiablePorts = null;
      }
   }

   @Deprecated
   @Override
   public void setPorts(Iterable<Integer> ports) {
      Set<Integer> newPorts = new TreeSet<>();

      for (int p : ports) {
         if (p <= 0 || p > 65535) {
            throw new IllegalArgumentException("port out of range: " + p);
         }

         newPorts.add(p);
      }

      if (newPorts.isEmpty()) {
         this.unmodifiablePorts = this.ports = Collections.emptySet();
      } else {
         this.ports = newPorts;
         this.unmodifiablePorts = null;
      }
   }

   @Deprecated
   @Override
   public long getMaxAge() {
      return this.maxAge();
   }

   @Deprecated
   @Override
   public int getVersion() {
      return this.version();
   }

   @Deprecated
   @Override
   public int version() {
      return this.version;
   }

   @Deprecated
   @Override
   public void setVersion(int version) {
      this.version = version;
   }
}
