package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DomainWildcardMappingBuilder<V> {
   private final V defaultValue;
   private final Map<String, V> map;

   public DomainWildcardMappingBuilder(V defaultValue) {
      this(4, defaultValue);
   }

   public DomainWildcardMappingBuilder(int initialCapacity, V defaultValue) {
      this.defaultValue = ObjectUtil.checkNotNull(defaultValue, "defaultValue");
      this.map = new LinkedHashMap<>(initialCapacity);
   }

   public DomainWildcardMappingBuilder<V> add(String hostname, V output) {
      this.map.put(this.normalizeHostName(hostname), ObjectUtil.checkNotNull(output, "output"));
      return this;
   }

   private String normalizeHostName(String hostname) {
      ObjectUtil.checkNotNull(hostname, "hostname");
      if (!hostname.isEmpty() && hostname.charAt(0) != '.') {
         hostname = DomainWildcardMappingBuilder.ImmutableDomainWildcardMapping.normalize(ObjectUtil.checkNotNull(hostname, "hostname"));
         if (hostname.charAt(0) != '*') {
            return hostname;
         } else if (hostname.length() >= 3 && hostname.charAt(1) == '.') {
            return hostname.substring(1);
         } else {
            throw new IllegalArgumentException("Wildcard Hostname '" + hostname + "'not valid");
         }
      } else {
         throw new IllegalArgumentException("Hostname '" + hostname + "' not valid");
      }
   }

   public Mapping<String, V> build() {
      return new DomainWildcardMappingBuilder.ImmutableDomainWildcardMapping<>(this.defaultValue, this.map);
   }

   private static final class ImmutableDomainWildcardMapping<V> implements Mapping<String, V> {
      private static final String REPR_HEADER = "ImmutableDomainWildcardMapping(default: ";
      private static final String REPR_MAP_OPENING = ", map: ";
      private static final String REPR_MAP_CLOSING = ")";
      private final V defaultValue;
      private final Map<String, V> map;

      ImmutableDomainWildcardMapping(V defaultValue, Map<String, V> map) {
         this.defaultValue = defaultValue;
         this.map = new LinkedHashMap<>(map);
      }

      public V map(String hostname) {
         if (hostname != null) {
            hostname = normalize(hostname);
            V value = this.map.get(hostname);
            if (value != null) {
               return value;
            }

            int idx = hostname.indexOf(46);
            if (idx != -1) {
               value = this.map.get(hostname.substring(idx));
               if (value != null) {
                  return value;
               }
            }
         }

         return this.defaultValue;
      }

      static String normalize(String hostname) {
         return DomainNameMapping.normalizeHostname(hostname);
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("ImmutableDomainWildcardMapping(default: ").append(this.defaultValue).append(", map: ").append('{');

         for (Entry<String, V> entry : this.map.entrySet()) {
            String hostname = entry.getKey();
            if (hostname.charAt(0) == '.') {
               hostname = '*' + hostname;
            }

            sb.append(hostname).append('=').append(entry.getValue()).append(", ");
         }

         sb.setLength(sb.length() - 2);
         return sb.append('}').append(")").toString();
      }
   }
}
