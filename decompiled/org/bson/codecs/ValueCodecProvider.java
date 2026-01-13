package org.bson.codecs;

import java.util.HashMap;
import java.util.Map;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class ValueCodecProvider implements CodecProvider {
   private final Map<Class<?>, Codec<?>> codecs = new HashMap<>();

   public ValueCodecProvider() {
      this.addCodecs();
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return (Codec<T>)this.codecs.get(clazz);
   }

   private void addCodecs() {
      this.addCodec(new BinaryCodec());
      this.addCodec(new BooleanCodec());
      this.addCodec(new DateCodec());
      this.addCodec(new DoubleCodec());
      this.addCodec(new IntegerCodec());
      this.addCodec(new LongCodec());
      this.addCodec(new MinKeyCodec());
      this.addCodec(new MaxKeyCodec());
      this.addCodec(new CodeCodec());
      this.addCodec(new Decimal128Codec());
      this.addCodec(new BigDecimalCodec());
      this.addCodec(new ObjectIdCodec());
      this.addCodec(new CharacterCodec());
      this.addCodec(new StringCodec());
      this.addCodec(new SymbolCodec());
      this.addCodec(new OverridableUuidRepresentationUuidCodec());
      this.addCodec(new ByteCodec());
      this.addCodec(new PatternCodec());
      this.addCodec(new ShortCodec());
      this.addCodec(new ByteArrayCodec());
      this.addCodec(new FloatCodec());
      this.addCodec(new AtomicBooleanCodec());
      this.addCodec(new AtomicIntegerCodec());
      this.addCodec(new AtomicLongCodec());
   }

   private <T> void addCodec(Codec<T> codec) {
      this.codecs.put(codec.getEncoderClass(), codec);
   }

   @Override
   public boolean equals(Object o) {
      return this == o ? true : o != null && this.getClass() == o.getClass();
   }

   @Override
   public int hashCode() {
      return 0;
   }
}
