package io.sentry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class EnvelopeReader implements IEnvelopeReader {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   @NotNull
   private final ISerializer serializer;

   public EnvelopeReader(@NotNull ISerializer serializer) {
      this.serializer = serializer;
   }

   @Nullable
   @Override
   public SentryEnvelope read(@NotNull InputStream stream) throws IOException {
      byte[] buffer = new byte[1024];
      int streamOffset = 0;
      int envelopeEndHeaderOffset = -1;
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      SentryEnvelope var19;
      try {
         int currentLength;
         while ((currentLength = stream.read(buffer)) > 0) {
            int i = 0;

            while (true) {
               if (envelopeEndHeaderOffset == -1 && i < currentLength) {
                  if (buffer[i] != 10) {
                     i++;
                     continue;
                  }

                  envelopeEndHeaderOffset = streamOffset + i;
               }

               outputStream.write(buffer, 0, currentLength);
               streamOffset += currentLength;
               break;
            }
         }

         byte[] envelopeBytes = outputStream.toByteArray();
         if (envelopeBytes.length == 0) {
            throw new IllegalArgumentException("Empty stream.");
         }

         if (envelopeEndHeaderOffset == -1) {
            throw new IllegalArgumentException("Envelope contains no header.");
         }

         SentryEnvelopeHeader header = this.deserializeEnvelopeHeader(envelopeBytes, 0, envelopeEndHeaderOffset);
         if (header == null) {
            throw new IllegalArgumentException("Envelope header is null.");
         }

         int itemHeaderStartOffset = envelopeEndHeaderOffset + 1;
         List<SentryEnvelopeItem> items = new ArrayList<>();

         label95:
         while (true) {
            int lineBreakIndex = -1;
            int i = itemHeaderStartOffset;

            while (true) {
               if (i < envelopeBytes.length) {
                  if (envelopeBytes[i] != 10) {
                     i++;
                     continue;
                  }

                  lineBreakIndex = i;
               }

               if (lineBreakIndex == -1) {
                  throw new IllegalArgumentException("Invalid envelope. Item at index '" + items.size() + "'. has no header delimiter.");
               }

               SentryEnvelopeItemHeader itemHeader = this.deserializeEnvelopeItemHeader(
                  envelopeBytes, itemHeaderStartOffset, lineBreakIndex - itemHeaderStartOffset
               );
               if (itemHeader == null || itemHeader.getLength() <= 0) {
                  throw new IllegalArgumentException("Item header at index '" + items.size() + "' is null or empty.");
               }

               int payloadEndOffsetExclusive = lineBreakIndex + itemHeader.getLength() + 1;
               if (payloadEndOffsetExclusive > envelopeBytes.length) {
                  throw new IllegalArgumentException(
                     "Invalid length for item at index '"
                        + items.size()
                        + "'. Item is '"
                        + payloadEndOffsetExclusive
                        + "' bytes. There are '"
                        + envelopeBytes.length
                        + "' in the buffer."
                  );
               }

               byte[] envelopeItemBytes = Arrays.copyOfRange(envelopeBytes, lineBreakIndex + 1, payloadEndOffsetExclusive);
               SentryEnvelopeItem item = new SentryEnvelopeItem(itemHeader, envelopeItemBytes);
               items.add(item);
               if (payloadEndOffsetExclusive == envelopeBytes.length) {
                  break label95;
               }

               if (payloadEndOffsetExclusive + 1 == envelopeBytes.length) {
                  if (envelopeBytes[payloadEndOffsetExclusive] != 10) {
                     throw new IllegalArgumentException("Envelope has invalid data following an item.");
                  }
                  break label95;
               }

               itemHeaderStartOffset = payloadEndOffsetExclusive + 1;
               break;
            }
         }

         var19 = new SentryEnvelope(header, items);
      } catch (Throwable var17) {
         try {
            outputStream.close();
         } catch (Throwable var16) {
            var17.addSuppressed(var16);
         }

         throw var17;
      }

      outputStream.close();
      return var19;
   }

   @Nullable
   private SentryEnvelopeHeader deserializeEnvelopeHeader(@NotNull byte[] buffer, int offset, int length) {
      String json = new String(buffer, offset, length, UTF_8);
      StringReader reader = new StringReader(json);

      SentryEnvelopeHeader var6;
      try {
         var6 = this.serializer.deserialize(reader, SentryEnvelopeHeader.class);
      } catch (Throwable var9) {
         try {
            reader.close();
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw var9;
      }

      reader.close();
      return var6;
   }

   @Nullable
   private SentryEnvelopeItemHeader deserializeEnvelopeItemHeader(@NotNull byte[] buffer, int offset, int length) {
      String json = new String(buffer, offset, length, UTF_8);
      StringReader reader = new StringReader(json);

      SentryEnvelopeItemHeader var6;
      try {
         var6 = this.serializer.deserialize(reader, SentryEnvelopeItemHeader.class);
      } catch (Throwable var9) {
         try {
            reader.close();
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw var9;
      }

      reader.close();
      return var6;
   }
}
