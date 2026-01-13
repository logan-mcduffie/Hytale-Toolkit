package joptsimple;

public interface ValueConverter<V> {
   V convert(String var1);

   Class<? extends V> valueType();

   String valuePattern();
}
