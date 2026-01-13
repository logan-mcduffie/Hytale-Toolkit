package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.CollectionUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.util.DateUtils;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@ThreadSafe
public class DefaultJWTClaimsVerifier<C extends SecurityContext> implements JWTClaimsSetVerifier<C>, ClockSkewAware {
   public static final int DEFAULT_MAX_CLOCK_SKEW_SECONDS = 60;
   private int maxClockSkew = 60;
   private final Set<String> acceptedAudienceValues;
   private final JWTClaimsSet exactMatchClaims;
   private final Set<String> requiredClaims;
   private final Set<String> prohibitedClaims;

   @Deprecated
   public DefaultJWTClaimsVerifier() {
      this(null, null, null, null);
   }

   public DefaultJWTClaimsVerifier(JWTClaimsSet exactMatchClaims, Set<String> requiredClaims) {
      this(null, exactMatchClaims, requiredClaims, null);
   }

   public DefaultJWTClaimsVerifier(String requiredAudience, JWTClaimsSet exactMatchClaims, Set<String> requiredClaims) {
      this(requiredAudience != null ? Collections.singleton(requiredAudience) : null, exactMatchClaims, requiredClaims, null);
   }

   public DefaultJWTClaimsVerifier(Set<String> acceptedAudience, JWTClaimsSet exactMatchClaims, Set<String> requiredClaims, Set<String> prohibitedClaims) {
      this.acceptedAudienceValues = acceptedAudience != null ? Collections.unmodifiableSet(acceptedAudience) : null;
      this.exactMatchClaims = exactMatchClaims != null ? exactMatchClaims : new JWTClaimsSet.Builder().build();
      Set<String> requiredClaimsCopy = new HashSet<>(this.exactMatchClaims.getClaims().keySet());
      if (this.acceptedAudienceValues != null && !CollectionUtils.containsNull(this.acceptedAudienceValues)) {
         requiredClaimsCopy.add("aud");
      }

      if (requiredClaims != null) {
         requiredClaimsCopy.addAll(requiredClaims);
      }

      this.requiredClaims = Collections.unmodifiableSet(requiredClaimsCopy);
      this.prohibitedClaims = prohibitedClaims != null ? Collections.unmodifiableSet(prohibitedClaims) : Collections.emptySet();
   }

   public Set<String> getAcceptedAudienceValues() {
      return this.acceptedAudienceValues;
   }

   public JWTClaimsSet getExactMatchClaims() {
      return this.exactMatchClaims;
   }

   public Set<String> getRequiredClaims() {
      return this.requiredClaims;
   }

   public Set<String> getProhibitedClaims() {
      return this.prohibitedClaims;
   }

   @Override
   public int getMaxClockSkew() {
      return this.maxClockSkew;
   }

   @Override
   public void setMaxClockSkew(int maxClockSkewSeconds) {
      this.maxClockSkew = maxClockSkewSeconds;
   }

   @Override
   public void verify(JWTClaimsSet claimsSet, C context) throws BadJWTException {
      if (this.acceptedAudienceValues != null) {
         List<String> audList = claimsSet.getAudience();
         if (audList != null && !audList.isEmpty()) {
            boolean audMatch = false;

            for (String aud : audList) {
               if (this.acceptedAudienceValues.contains(aud)) {
                  audMatch = true;
                  break;
               }
            }

            if (!audMatch) {
               throw new BadJWTException("JWT aud claim rejected");
            }
         } else if (!CollectionUtils.containsNull(this.acceptedAudienceValues)) {
            throw new BadJWTException("JWT missing required aud claim");
         }
      }

      if (!claimsSet.getClaims().keySet().containsAll(this.requiredClaims)) {
         SortedSet<String> missingClaims = new TreeSet<>(this.requiredClaims);
         missingClaims.removeAll(claimsSet.getClaims().keySet());
         throw new BadJWTException("JWT missing required claims: " + missingClaims);
      } else {
         SortedSet<String> presentProhibitedClaims = new TreeSet<>();

         for (String prohibited : this.prohibitedClaims) {
            if (claimsSet.getClaims().containsKey(prohibited)) {
               presentProhibitedClaims.add(prohibited);
            }
         }

         if (!presentProhibitedClaims.isEmpty()) {
            throw new BadJWTException("JWT has prohibited claims: " + presentProhibitedClaims);
         } else {
            for (String exactMatch : this.exactMatchClaims.getClaims().keySet()) {
               Object actualClaim = claimsSet.getClaim(exactMatch);
               Object expectedClaim = this.exactMatchClaims.getClaim(exactMatch);
               if (!Objects.equals(expectedClaim, actualClaim)) {
                  throw new BadJWTException("JWT " + exactMatch + " claim value rejected");
               }
            }

            Date now = this.currentTime();
            if (now != null) {
               Date exp = claimsSet.getExpirationTime();
               if (exp != null && !DateUtils.isAfter(exp, now, this.maxClockSkew)) {
                  throw new ExpiredJWTException("Expired JWT");
               }

               Date nbf = claimsSet.getNotBeforeTime();
               if (nbf != null && !DateUtils.isBefore(nbf, now, this.maxClockSkew)) {
                  throw new BadJWTException("JWT before use time");
               }
            }
         }
      }
   }

   protected Date currentTime() {
      return new Date();
   }
}
