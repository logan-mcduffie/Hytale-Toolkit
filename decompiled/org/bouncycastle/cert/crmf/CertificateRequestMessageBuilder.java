package org.bouncycastle.cert.crmf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.crmf.SubsequentMessage;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.ContentSigner;

public class CertificateRequestMessageBuilder {
   private final BigInteger certReqId;
   private ExtensionsGenerator extGenerator;
   private CertTemplateBuilder templateBuilder;
   private List controls;
   private ContentSigner popSigner;
   private PKMACBuilder pkmacBuilder;
   private char[] password;
   private GeneralName sender;
   private int popoType = 2;
   private POPOPrivKey popoPrivKey;
   private ASN1Null popRaVerified;
   private PKMACValue agreeMAC;
   private AttributeTypeAndValue[] regInfo;

   public CertificateRequestMessageBuilder(BigInteger var1) {
      this.certReqId = var1;
      this.extGenerator = new ExtensionsGenerator();
      this.templateBuilder = new CertTemplateBuilder();
      this.controls = new ArrayList();
      this.regInfo = null;
   }

   public CertificateRequestMessageBuilder setRegInfo(AttributeTypeAndValue[] var1) {
      this.regInfo = var1;
      return this;
   }

   public CertificateRequestMessageBuilder setPublicKey(SubjectPublicKeyInfo var1) {
      if (var1 != null) {
         this.templateBuilder.setPublicKey(var1);
      }

      return this;
   }

   public CertificateRequestMessageBuilder setIssuer(X500Name var1) {
      if (var1 != null) {
         this.templateBuilder.setIssuer(var1);
      }

      return this;
   }

   public CertificateRequestMessageBuilder setSubject(X500Name var1) {
      if (var1 != null) {
         this.templateBuilder.setSubject(var1);
      }

      return this;
   }

   public CertificateRequestMessageBuilder setSerialNumber(BigInteger var1) {
      if (var1 != null) {
         this.templateBuilder.setSerialNumber(new ASN1Integer(var1));
      }

      return this;
   }

   public CertificateRequestMessageBuilder setSerialNumber(ASN1Integer var1) {
      if (var1 != null) {
         this.templateBuilder.setSerialNumber(var1);
      }

      return this;
   }

   public CertificateRequestMessageBuilder setValidity(Date var1, Date var2) {
      this.templateBuilder.setValidity(new OptionalValidity(this.createTime(var1), this.createTime(var2)));
      return this;
   }

   private Time createTime(Date var1) {
      return var1 != null ? new Time(var1) : null;
   }

   public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      CRMFUtil.addExtension(this.extGenerator, var1, var2, var3);
      return this;
   }

   public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) {
      this.extGenerator.addExtension(var1, var2, var3);
      return this;
   }

   public CertificateRequestMessageBuilder addControl(Control var1) {
      this.controls.add(var1);
      return this;
   }

   public CertificateRequestMessageBuilder setProofOfPossessionSigningKeySigner(ContentSigner var1) {
      if (this.popoPrivKey == null && this.popRaVerified == null && this.agreeMAC == null) {
         this.popSigner = var1;
         return this;
      } else {
         throw new IllegalStateException("only one proof of possession allowed");
      }
   }

   public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(SubsequentMessage var1) {
      if (this.popSigner == null && this.popRaVerified == null && this.agreeMAC == null) {
         this.popoType = 2;
         this.popoPrivKey = new POPOPrivKey(var1);
         return this;
      } else {
         throw new IllegalStateException("only one proof of possession allowed");
      }
   }

   public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(int var1, SubsequentMessage var2) {
      if (this.popSigner != null || this.popRaVerified != null || this.agreeMAC != null) {
         throw new IllegalStateException("only one proof of possession allowed");
      } else if (var1 != 2 && var1 != 3) {
         throw new IllegalArgumentException("type must be ProofOfPossession.TYPE_KEY_ENCIPHERMENT or ProofOfPossession.TYPE_KEY_AGREEMENT");
      } else {
         this.popoType = var1;
         this.popoPrivKey = new POPOPrivKey(var2);
         return this;
      }
   }

   public CertificateRequestMessageBuilder setProofOfPossessionAgreeMAC(PKMACValue var1) {
      if (this.popSigner == null && this.popRaVerified == null && this.popoPrivKey == null) {
         this.agreeMAC = var1;
         return this;
      } else {
         throw new IllegalStateException("only one proof of possession allowed");
      }
   }

   public CertificateRequestMessageBuilder setProofOfPossessionRaVerified() {
      if (this.popSigner == null && this.popoPrivKey == null) {
         this.popRaVerified = DERNull.INSTANCE;
         return this;
      } else {
         throw new IllegalStateException("only one proof of possession allowed");
      }
   }

   public CertificateRequestMessageBuilder setAuthInfoPKMAC(PKMACBuilder var1, char[] var2) {
      this.pkmacBuilder = var1;
      this.password = var2;
      return this;
   }

   public CertificateRequestMessageBuilder setAuthInfoSender(X500Name var1) {
      return this.setAuthInfoSender(new GeneralName(var1));
   }

   public CertificateRequestMessageBuilder setAuthInfoSender(GeneralName var1) {
      this.sender = var1;
      return this;
   }

   public CertificateRequestMessage build() throws CRMFException {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(new ASN1Integer(this.certReqId));
      if (!this.extGenerator.isEmpty()) {
         this.templateBuilder.setExtensions(this.extGenerator.generate());
      }

      var1.add(this.templateBuilder.build());
      if (!this.controls.isEmpty()) {
         ASN1EncodableVector var2 = new ASN1EncodableVector();

         for (Control var4 : this.controls) {
            var2.add(new AttributeTypeAndValue(var4.getType(), var4.getValue()));
         }

         var1.add(new DERSequence(var2));
      }

      CertRequest var7 = CertRequest.getInstance(new DERSequence(var1));
      ProofOfPossession var8;
      if (this.popSigner != null) {
         CertTemplate var9 = var7.getCertTemplate();
         ProofOfPossessionSigningKeyBuilder var5;
         if (var9.getSubject() != null && var9.getPublicKey() != null) {
            var5 = new ProofOfPossessionSigningKeyBuilder(var7);
         } else {
            SubjectPublicKeyInfo var6 = var7.getCertTemplate().getPublicKey();
            var5 = new ProofOfPossessionSigningKeyBuilder(var6);
            if (this.sender != null) {
               var5.setSender(this.sender);
            } else {
               var5.setPublicKeyMac(this.pkmacBuilder, this.password);
            }
         }

         var8 = new ProofOfPossession(var5.build(this.popSigner));
      } else if (this.popoPrivKey != null) {
         var8 = new ProofOfPossession(this.popoType, this.popoPrivKey);
      } else if (this.agreeMAC != null) {
         var8 = new ProofOfPossession(3, new POPOPrivKey(this.agreeMAC));
      } else if (this.popRaVerified != null) {
         var8 = new ProofOfPossession();
      } else {
         var8 = new ProofOfPossession();
      }

      CertReqMsg var10 = new CertReqMsg(var7, var8, this.regInfo);
      return new CertificateRequestMessage(var10);
   }
}
