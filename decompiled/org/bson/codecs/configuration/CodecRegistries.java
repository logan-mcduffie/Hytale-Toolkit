package org.bson.codecs.configuration;

import java.util.Arrays;
import java.util.List;
import org.bson.codecs.Codec;
import org.bson.internal.ProvidersCodecRegistry;

public final class CodecRegistries {
   public static CodecRegistry fromCodecs(Codec<?>... codecs) {
      return fromCodecs(Arrays.asList(codecs));
   }

   public static CodecRegistry fromCodecs(List<? extends Codec<?>> codecs) {
      return fromProviders(new MapOfCodecsProvider(codecs));
   }

   public static CodecRegistry fromProviders(CodecProvider... providers) {
      return fromProviders(Arrays.asList(providers));
   }

   public static CodecRegistry fromProviders(List<? extends CodecProvider> providers) {
      return new ProvidersCodecRegistry(providers);
   }

   public static CodecRegistry fromRegistries(CodecRegistry... registries) {
      return fromRegistries(Arrays.asList(registries));
   }

   public static CodecRegistry fromRegistries(List<? extends CodecRegistry> registries) {
      return new ProvidersCodecRegistry(registries);
   }

   private CodecRegistries() {
   }
}
