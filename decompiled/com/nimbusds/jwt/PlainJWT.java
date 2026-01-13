package com.nimbusds.jwt;

import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.PlainHeader;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.text.ParseException;
import java.util.Map;

@ThreadSafe
public class PlainJWT extends PlainObject implements JWT {
   private static final long serialVersionUID = 1L;
   private JWTClaimsSet claimsSet;

   public PlainJWT(JWTClaimsSet claimsSet) {
      super(claimsSet.toPayload());
      this.claimsSet = claimsSet;
   }

   public PlainJWT(PlainHeader header, JWTClaimsSet claimsSet) {
      super(header, claimsSet.toPayload());
      this.claimsSet = claimsSet;
   }

   public PlainJWT(Base64URL firstPart, Base64URL secondPart) throws ParseException {
      super(firstPart, secondPart);
   }

   @Override
   public JWTClaimsSet getJWTClaimsSet() throws ParseException {
      if (this.claimsSet != null) {
         return this.claimsSet;
      } else {
         Map<String, Object> jsonObject = this.getPayload().toJSONObject();
         if (jsonObject == null) {
            throw new ParseException("Payload of unsecured JOSE object is not a valid JSON object", 0);
         } else {
            this.claimsSet = JWTClaimsSet.parse(jsonObject);
            return this.claimsSet;
         }
      }
   }

   @Override
   protected void setPayload(Payload payload) {
      this.claimsSet = null;
      super.setPayload(payload);
   }

   public static PlainJWT parse(String s) throws ParseException {
      Base64URL[] parts = JOSEObject.split(s);
      if (!parts[2].toString().isEmpty()) {
         throw new ParseException("Unexpected third Base64URL part in the unsecured JWT object", 0);
      } else {
         return new PlainJWT(parts[0], parts[1]);
      }
   }
}
