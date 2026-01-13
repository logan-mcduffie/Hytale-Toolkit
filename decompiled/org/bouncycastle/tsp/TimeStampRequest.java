package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class TimeStampRequest {
   private static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
   private TimeStampReq req;
   private Extensions extensions;

   private static TimeStampReq parseTimeStampReq(byte[] var0) throws IOException {
      try {
         return TimeStampReq.getInstance(var0);
      } catch (ClassCastException var2) {
         throw new IOException("malformed request: " + var2);
      } catch (IllegalArgumentException var3) {
         throw new IOException("malformed request: " + var3);
      }
   }

   private static TimeStampReq parseTimeStampReq(InputStream var0) throws IOException {
      try {
         return TimeStampReq.getInstance(new ASN1InputStream(var0).readObject());
      } catch (ClassCastException var2) {
         throw new IOException("malformed request: " + var2);
      } catch (IllegalArgumentException var3) {
         throw new IOException("malformed request: " + var3);
      }
   }

   public TimeStampRequest(TimeStampReq var1) {
      this.req = var1;
      this.extensions = var1.getExtensions();
   }

   public TimeStampRequest(byte[] var1) throws IOException {
      this(parseTimeStampReq(var1));
   }

   public TimeStampRequest(InputStream var1) throws IOException {
      this(parseTimeStampReq(var1));
   }

   public TimeStampReq toASN1Structure() {
      return this.req;
   }

   public int getVersion() {
      return this.req.getVersion().intValueExact();
   }

   public MessageImprint getMessageImprint() {
      return this.req.getMessageImprint();
   }

   public ASN1ObjectIdentifier getMessageImprintAlgOID() {
      return this.req.getMessageImprint().getHashAlgorithm().getAlgorithm();
   }

   public AlgorithmIdentifier getMessageImprintAlgID() {
      return this.req.getMessageImprint().getHashAlgorithm();
   }

   public byte[] getMessageImprintDigest() {
      return this.req.getMessageImprint().getHashedMessage();
   }

   public ASN1ObjectIdentifier getReqPolicy() {
      return this.req.getReqPolicy() != null ? this.req.getReqPolicy() : null;
   }

   public BigInteger getNonce() {
      return this.req.getNonce() != null ? this.req.getNonce().getValue() : null;
   }

   public boolean getCertReq() {
      return this.req.getCertReq() != null ? this.req.getCertReq().isTrue() : false;
   }

   public void validate(Set var1, Set var2, Set var3) throws TSPException {
      var1 = this.convert(var1);
      var2 = this.convert(var2);
      var3 = this.convert(var3);
      if (var1 == null) {
         throw new TSPValidationException("no algorithms associated with request", 128);
      } else if (!var1.contains(this.getMessageImprintAlgOID())) {
         throw new TSPValidationException("request contains unknown algorithm", 128);
      } else if (var2 != null && this.getReqPolicy() != null && !var2.contains(this.getReqPolicy())) {
         throw new TSPValidationException("request contains unknown policy", 256);
      } else {
         if (this.getExtensions() != null && var3 != null) {
            Enumeration var4 = this.getExtensions().oids();

            while (var4.hasMoreElements()) {
               ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var4.nextElement();
               if (!var3.contains(var5)) {
                  throw new TSPValidationException("request contains unknown extension", 8388608);
               }
            }
         }

         int var9 = TSPUtil.getDigestLength(this.getMessageImprintAlgOID().getId());
         if (var9 != this.getMessageImprint().getHashedMessageLength()) {
            throw new TSPValidationException("imprint digest the wrong length", 4);
         }
      }
   }

   public byte[] getEncoded() throws IOException {
      return this.req.getEncoded();
   }

   Extensions getExtensions() {
      return this.extensions;
   }

   public boolean hasExtensions() {
      return this.extensions != null;
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      return this.extensions != null ? this.extensions.getExtension(var1) : null;
   }

   public List getExtensionOIDs() {
      return TSPUtil.getExtensionOIDs(this.extensions);
   }

   public Set getNonCriticalExtensionOIDs() {
      return this.extensions == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet<>(Arrays.asList(this.extensions.getNonCriticalExtensionOIDs())));
   }

   public Set getCriticalExtensionOIDs() {
      return this.extensions == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet<>(Arrays.asList(this.extensions.getCriticalExtensionOIDs())));
   }

   private Set convert(Set var1) {
      if (var1 == null) {
         return var1;
      } else {
         HashSet var2 = new HashSet(var1.size());

         for (Object var4 : var1) {
            if (var4 instanceof String) {
               var2.add(new ASN1ObjectIdentifier((String)var4));
            } else {
               var2.add(var4);
            }
         }

         return var2;
      }
   }
}
