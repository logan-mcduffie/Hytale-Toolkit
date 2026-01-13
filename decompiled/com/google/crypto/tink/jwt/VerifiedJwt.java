package com.google.crypto.tink.jwt;

import com.google.errorprone.annotations.Immutable;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Immutable
public final class VerifiedJwt {
   private final RawJwt rawJwt;

   VerifiedJwt(RawJwt rawJwt) {
      this.rawJwt = rawJwt;
   }

   public String getTypeHeader() throws JwtInvalidException {
      return this.rawJwt.getTypeHeader();
   }

   public boolean hasTypeHeader() {
      return this.rawJwt.hasTypeHeader();
   }

   public String getIssuer() throws JwtInvalidException {
      return this.rawJwt.getIssuer();
   }

   public boolean hasIssuer() {
      return this.rawJwt.hasIssuer();
   }

   public String getSubject() throws JwtInvalidException {
      return this.rawJwt.getSubject();
   }

   public boolean hasSubject() {
      return this.rawJwt.hasSubject();
   }

   public List<String> getAudiences() throws JwtInvalidException {
      return this.rawJwt.getAudiences();
   }

   public boolean hasAudiences() {
      return this.rawJwt.hasAudiences();
   }

   public String getJwtId() throws JwtInvalidException {
      return this.rawJwt.getJwtId();
   }

   public boolean hasJwtId() {
      return this.rawJwt.hasJwtId();
   }

   public Instant getExpiration() throws JwtInvalidException {
      return this.rawJwt.getExpiration();
   }

   public boolean hasExpiration() {
      return this.rawJwt.hasExpiration();
   }

   public Instant getNotBefore() throws JwtInvalidException {
      return this.rawJwt.getNotBefore();
   }

   public boolean hasNotBefore() {
      return this.rawJwt.hasNotBefore();
   }

   public Instant getIssuedAt() throws JwtInvalidException {
      return this.rawJwt.getIssuedAt();
   }

   public boolean hasIssuedAt() {
      return this.rawJwt.hasIssuedAt();
   }

   public Boolean getBooleanClaim(String name) throws JwtInvalidException {
      return this.rawJwt.getBooleanClaim(name);
   }

   public Double getNumberClaim(String name) throws JwtInvalidException {
      return this.rawJwt.getNumberClaim(name);
   }

   public String getStringClaim(String name) throws JwtInvalidException {
      return this.rawJwt.getStringClaim(name);
   }

   public boolean isNullClaim(String name) {
      return this.rawJwt.isNullClaim(name);
   }

   public String getJsonObjectClaim(String name) throws JwtInvalidException {
      return this.rawJwt.getJsonObjectClaim(name);
   }

   public String getJsonArrayClaim(String name) throws JwtInvalidException {
      return this.rawJwt.getJsonArrayClaim(name);
   }

   public boolean hasBooleanClaim(String name) {
      return this.rawJwt.hasBooleanClaim(name);
   }

   public boolean hasNumberClaim(String name) {
      return this.rawJwt.hasNumberClaim(name);
   }

   public boolean hasStringClaim(String name) {
      return this.rawJwt.hasStringClaim(name);
   }

   public boolean hasJsonObjectClaim(String name) {
      return this.rawJwt.hasJsonObjectClaim(name);
   }

   public boolean hasJsonArrayClaim(String name) {
      return this.rawJwt.hasJsonArrayClaim(name);
   }

   public Set<String> customClaimNames() {
      return this.rawJwt.customClaimNames();
   }

   @Override
   public String toString() {
      return "verified{" + this.rawJwt + "}";
   }
}
