package com.nimbusds.jwt;

import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.text.ParseException;
import java.util.Map;

@ThreadSafe
public class SignedJWT extends JWSObject implements JWT {
   private static final long serialVersionUID = 1L;
   private JWTClaimsSet claimsSet;

   public SignedJWT(JWSHeader header, JWTClaimsSet claimsSet) {
      super(header, claimsSet.toPayload());
      this.claimsSet = claimsSet;
   }

   public SignedJWT(Base64URL firstPart, Base64URL secondPart, Base64URL thirdPart) throws ParseException {
      super(firstPart, secondPart, thirdPart);
   }

   @Override
   public JWTClaimsSet getJWTClaimsSet() throws ParseException {
      if (this.claimsSet != null) {
         return this.claimsSet;
      } else {
         Map<String, Object> json = this.getPayload().toJSONObject();
         if (json == null) {
            throw new ParseException("Payload of JWS object is not a valid JSON object", 0);
         } else {
            this.claimsSet = JWTClaimsSet.parse(json);
            return this.claimsSet;
         }
      }
   }

   @Override
   protected void setPayload(Payload payload) {
      this.claimsSet = null;
      super.setPayload(payload);
   }

   public static SignedJWT parse(String s) throws ParseException {
      Base64URL[] parts = JOSEObject.split(s);
      if (parts.length != 3) {
         throw new ParseException("Unexpected number of Base64URL parts, must be three", 0);
      } else {
         return new SignedJWT(parts[0], parts[1], parts[2]);
      }
   }
}
