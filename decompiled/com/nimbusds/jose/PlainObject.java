package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.text.ParseException;
import java.util.Objects;

@ThreadSafe
public class PlainObject extends JOSEObject {
   private static final long serialVersionUID = 1L;
   private final PlainHeader header;

   public PlainObject(Payload payload) {
      this.setPayload(Objects.requireNonNull(payload));
      this.header = new PlainHeader();
   }

   public PlainObject(PlainHeader header, Payload payload) {
      this.header = Objects.requireNonNull(header);
      this.setPayload(Objects.requireNonNull(payload));
   }

   public PlainObject(Base64URL firstPart, Base64URL secondPart) throws ParseException {
      try {
         this.header = PlainHeader.parse(Objects.requireNonNull(firstPart));
      } catch (ParseException var4) {
         throw new ParseException("Invalid unsecured header: " + var4.getMessage(), 0);
      }

      this.setPayload(new Payload(Objects.requireNonNull(secondPart)));
      this.setParsedParts(firstPart, secondPart, null);
   }

   public PlainHeader getHeader() {
      return this.header;
   }

   @Override
   public String serialize() {
      return this.header.toBase64URL().toString() + '.' + this.getPayload().toBase64URL().toString() + '.';
   }

   public static PlainObject parse(String s) throws ParseException {
      Base64URL[] parts = JOSEObject.split(s);
      if (!parts[2].toString().isEmpty()) {
         throw new ParseException("Unexpected third Base64URL part", 0);
      } else {
         return new PlainObject(parts[0], parts[1]);
      }
   }
}
