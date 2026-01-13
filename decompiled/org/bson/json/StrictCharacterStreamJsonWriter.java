package org.bson.json;

import java.io.IOException;
import java.io.Writer;
import org.bson.BSONException;
import org.bson.BsonInvalidOperationException;
import org.bson.assertions.Assertions;

public final class StrictCharacterStreamJsonWriter implements StrictJsonWriter {
   private final Writer writer;
   private final StrictCharacterStreamJsonWriterSettings settings;
   private StrictCharacterStreamJsonWriter.StrictJsonContext context = new StrictCharacterStreamJsonWriter.StrictJsonContext(
      null, StrictCharacterStreamJsonWriter.JsonContextType.TOP_LEVEL, ""
   );
   private StrictCharacterStreamJsonWriter.State state = StrictCharacterStreamJsonWriter.State.INITIAL;
   private int curLength;
   private boolean isTruncated;

   public StrictCharacterStreamJsonWriter(Writer writer, StrictCharacterStreamJsonWriterSettings settings) {
      this.writer = writer;
      this.settings = settings;
   }

   public int getCurrentLength() {
      return this.curLength;
   }

   @Override
   public void writeStartObject(String name) {
      this.writeName(name);
      this.writeStartObject();
   }

   @Override
   public void writeStartArray(String name) {
      this.writeName(name);
      this.writeStartArray();
   }

   @Override
   public void writeBoolean(String name, boolean value) {
      Assertions.notNull("name", name);
      this.writeName(name);
      this.writeBoolean(value);
   }

   @Override
   public void writeNumber(String name, String value) {
      Assertions.notNull("name", name);
      Assertions.notNull("value", value);
      this.writeName(name);
      this.writeNumber(value);
   }

   @Override
   public void writeString(String name, String value) {
      Assertions.notNull("name", name);
      Assertions.notNull("value", value);
      this.writeName(name);
      this.writeString(value);
   }

   @Override
   public void writeRaw(String name, String value) {
      Assertions.notNull("name", name);
      Assertions.notNull("value", value);
      this.writeName(name);
      this.writeRaw(value);
   }

   @Override
   public void writeNull(String name) {
      this.writeName(name);
      this.writeNull();
   }

   @Override
   public void writeName(String name) {
      Assertions.notNull("name", name);
      this.checkState(StrictCharacterStreamJsonWriter.State.NAME);
      if (this.context.hasElements) {
         this.write(",");
      }

      if (this.settings.isIndent()) {
         this.write(this.settings.getNewLineCharacters());
         this.write(this.context.indentation);
      } else if (this.context.hasElements) {
         this.write(" ");
      }

      this.writeStringHelper(name);
      this.write(": ");
      this.state = StrictCharacterStreamJsonWriter.State.VALUE;
   }

   @Override
   public void writeBoolean(boolean value) {
      this.checkState(StrictCharacterStreamJsonWriter.State.VALUE);
      this.preWriteValue();
      this.write(value ? "true" : "false");
      this.setNextState();
   }

   @Override
   public void writeNumber(String value) {
      Assertions.notNull("value", value);
      this.checkState(StrictCharacterStreamJsonWriter.State.VALUE);
      this.preWriteValue();
      this.write(value);
      this.setNextState();
   }

   @Override
   public void writeString(String value) {
      Assertions.notNull("value", value);
      this.checkState(StrictCharacterStreamJsonWriter.State.VALUE);
      this.preWriteValue();
      this.writeStringHelper(value);
      this.setNextState();
   }

   @Override
   public void writeRaw(String value) {
      Assertions.notNull("value", value);
      this.checkState(StrictCharacterStreamJsonWriter.State.VALUE);
      this.preWriteValue();
      this.write(value);
      this.setNextState();
   }

   @Override
   public void writeNull() {
      this.checkState(StrictCharacterStreamJsonWriter.State.VALUE);
      this.preWriteValue();
      this.write("null");
      this.setNextState();
   }

   @Override
   public void writeStartObject() {
      if (this.state != StrictCharacterStreamJsonWriter.State.INITIAL && this.state != StrictCharacterStreamJsonWriter.State.VALUE) {
         throw new BsonInvalidOperationException("Invalid state " + this.state);
      } else {
         this.preWriteValue();
         this.write("{");
         this.context = new StrictCharacterStreamJsonWriter.StrictJsonContext(
            this.context, StrictCharacterStreamJsonWriter.JsonContextType.DOCUMENT, this.settings.getIndentCharacters()
         );
         this.state = StrictCharacterStreamJsonWriter.State.NAME;
      }
   }

   @Override
   public void writeStartArray() {
      this.preWriteValue();
      this.write("[");
      this.context = new StrictCharacterStreamJsonWriter.StrictJsonContext(
         this.context, StrictCharacterStreamJsonWriter.JsonContextType.ARRAY, this.settings.getIndentCharacters()
      );
      this.state = StrictCharacterStreamJsonWriter.State.VALUE;
   }

   @Override
   public void writeEndObject() {
      this.checkState(StrictCharacterStreamJsonWriter.State.NAME);
      if (this.settings.isIndent() && this.context.hasElements) {
         this.write(this.settings.getNewLineCharacters());
         this.write(this.context.parentContext.indentation);
      }

      this.write("}");
      this.context = this.context.parentContext;
      if (this.context.contextType == StrictCharacterStreamJsonWriter.JsonContextType.TOP_LEVEL) {
         this.state = StrictCharacterStreamJsonWriter.State.DONE;
      } else {
         this.setNextState();
      }
   }

   @Override
   public void writeEndArray() {
      this.checkState(StrictCharacterStreamJsonWriter.State.VALUE);
      if (this.context.contextType != StrictCharacterStreamJsonWriter.JsonContextType.ARRAY) {
         throw new BsonInvalidOperationException("Can't end an array if not in an array");
      } else {
         if (this.settings.isIndent() && this.context.hasElements) {
            this.write(this.settings.getNewLineCharacters());
            this.write(this.context.parentContext.indentation);
         }

         this.write("]");
         this.context = this.context.parentContext;
         if (this.context.contextType == StrictCharacterStreamJsonWriter.JsonContextType.TOP_LEVEL) {
            this.state = StrictCharacterStreamJsonWriter.State.DONE;
         } else {
            this.setNextState();
         }
      }
   }

   @Override
   public boolean isTruncated() {
      return this.isTruncated;
   }

   void flush() {
      try {
         this.writer.flush();
      } catch (IOException var2) {
         this.throwBSONException(var2);
      }
   }

   Writer getWriter() {
      return this.writer;
   }

   private void preWriteValue() {
      if (this.context.contextType == StrictCharacterStreamJsonWriter.JsonContextType.ARRAY) {
         if (this.context.hasElements) {
            this.write(",");
         }

         if (this.settings.isIndent()) {
            this.write(this.settings.getNewLineCharacters());
            this.write(this.context.indentation);
         } else if (this.context.hasElements) {
            this.write(" ");
         }
      }

      this.context.hasElements = true;
   }

   private void setNextState() {
      if (this.context.contextType == StrictCharacterStreamJsonWriter.JsonContextType.ARRAY) {
         this.state = StrictCharacterStreamJsonWriter.State.VALUE;
      } else {
         this.state = StrictCharacterStreamJsonWriter.State.NAME;
      }
   }

   private void writeStringHelper(String str) {
      this.write('"');

      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         switch (c) {
            case '\b':
               this.write("\\b");
               break;
            case '\t':
               this.write("\\t");
               break;
            case '\n':
               this.write("\\n");
               break;
            case '\f':
               this.write("\\f");
               break;
            case '\r':
               this.write("\\r");
               break;
            case '"':
               this.write("\\\"");
               break;
            case '\\':
               this.write("\\\\");
               break;
            default:
               switch (Character.getType(c)) {
                  case 1:
                  case 2:
                  case 3:
                  case 5:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                     this.write(c);
                     break;
                  case 4:
                  case 6:
                  case 7:
                  case 8:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  default:
                     this.write("\\u");
                     this.write(Integer.toHexString((c & '\uf000') >> 12));
                     this.write(Integer.toHexString((c & 3840) >> 8));
                     this.write(Integer.toHexString((c & 240) >> 4));
                     this.write(Integer.toHexString(c & 15));
               }
         }
      }

      this.write('"');
   }

   private void write(String str) {
      try {
         if (this.settings.getMaxLength() != 0 && str.length() + this.curLength >= this.settings.getMaxLength()) {
            this.writer.write(str.substring(0, this.settings.getMaxLength() - this.curLength));
            this.curLength = this.settings.getMaxLength();
            this.isTruncated = true;
         } else {
            this.writer.write(str);
            this.curLength = this.curLength + str.length();
         }
      } catch (IOException var3) {
         this.throwBSONException(var3);
      }
   }

   private void write(char c) {
      try {
         if (this.settings.getMaxLength() != 0 && this.curLength >= this.settings.getMaxLength()) {
            this.isTruncated = true;
         } else {
            this.writer.write(c);
            this.curLength++;
         }
      } catch (IOException var3) {
         this.throwBSONException(var3);
      }
   }

   private void checkState(StrictCharacterStreamJsonWriter.State requiredState) {
      if (this.state != requiredState) {
         throw new BsonInvalidOperationException("Invalid state " + this.state);
      }
   }

   private void throwBSONException(IOException e) {
      throw new BSONException("Wrapping IOException", e);
   }

   private static enum JsonContextType {
      TOP_LEVEL,
      DOCUMENT,
      ARRAY;
   }

   private static enum State {
      INITIAL,
      NAME,
      VALUE,
      DONE;
   }

   private static class StrictJsonContext {
      private final StrictCharacterStreamJsonWriter.StrictJsonContext parentContext;
      private final StrictCharacterStreamJsonWriter.JsonContextType contextType;
      private final String indentation;
      private boolean hasElements;

      StrictJsonContext(
         StrictCharacterStreamJsonWriter.StrictJsonContext parentContext, StrictCharacterStreamJsonWriter.JsonContextType contextType, String indentChars
      ) {
         this.parentContext = parentContext;
         this.contextType = contextType;
         this.indentation = parentContext == null ? indentChars : parentContext.indentation + indentChars;
      }
   }
}
