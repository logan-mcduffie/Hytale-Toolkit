package com.google.crypto.tink.internal;

import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Immutable
public final class PrefixMap<P> {
   private static final Bytes EMPTY_BYTES = Bytes.copyFrom(new byte[0]);
   private final Map<Bytes, List<P>> entries;

   public Iterable<P> getAllWithMatchingPrefix(byte[] text) {
      final List<P> zeroByteEntriesOrNull = this.entries.get(EMPTY_BYTES);
      final List<P> fiveByteEntriesOrNull = text.length >= 5 ? this.entries.get(Bytes.copyFrom(text, 0, 5)) : null;
      if (zeroByteEntriesOrNull == null && fiveByteEntriesOrNull == null) {
         return new ArrayList<>();
      } else if (zeroByteEntriesOrNull == null) {
         return fiveByteEntriesOrNull;
      } else {
         return (Iterable<P>)(fiveByteEntriesOrNull == null ? zeroByteEntriesOrNull : new Iterable<P>() {
            @Override
            public Iterator<P> iterator() {
               return new PrefixMap.ConcatenatedIterator<>(fiveByteEntriesOrNull.iterator(), zeroByteEntriesOrNull.iterator());
            }
         });
      }
   }

   private PrefixMap(Map<Bytes, List<P>> entries) {
      this.entries = entries;
   }

   public static class Builder<P> {
      private final Map<Bytes, List<P>> entries = new HashMap<>();

      @CanIgnoreReturnValue
      public PrefixMap.Builder<P> put(Bytes prefix, P primitive) throws GeneralSecurityException {
         if (prefix.size() != 0 && prefix.size() != 5) {
            throw new GeneralSecurityException("PrefixMap only supports 0 and 5 byte prefixes");
         } else {
            List<P> listForThisPrefix;
            if (this.entries.containsKey(prefix)) {
               listForThisPrefix = this.entries.get(prefix);
            } else {
               listForThisPrefix = new ArrayList<>();
               this.entries.put(prefix, listForThisPrefix);
            }

            listForThisPrefix.add(primitive);
            return this;
         }
      }

      public PrefixMap<P> build() {
         return new PrefixMap<>(this.entries);
      }
   }

   private static class ConcatenatedIterator<P> implements Iterator<P> {
      private final Iterator<P> it0;
      private final Iterator<P> it1;

      private ConcatenatedIterator(Iterator<P> it0, Iterator<P> it1) {
         this.it0 = it0;
         this.it1 = it1;
      }

      @Override
      public boolean hasNext() {
         return this.it0.hasNext() || this.it1.hasNext();
      }

      @Override
      public P next() {
         return this.it0.hasNext() ? this.it0.next() : this.it1.next();
      }
   }
}
