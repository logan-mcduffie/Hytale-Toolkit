package org.bouncycastle.pkix.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.qualified.Iso4217CurrencyCode;
import org.bouncycastle.asn1.x509.qualified.MonetaryValue;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.pkix.PKIXNameConstraintValidator;
import org.bouncycastle.pkix.PKIXNameConstraintValidatorException;
import org.bouncycastle.pkix.util.ErrorBundle;
import org.bouncycastle.pkix.util.LocaleString;
import org.bouncycastle.pkix.util.filter.TrustedInput;
import org.bouncycastle.pkix.util.filter.UntrustedInput;
import org.bouncycastle.pkix.util.filter.UntrustedUrlInput;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Objects;

public class PKIXCertPathReviewer extends CertPathValidatorUtilities {
   private static final String QC_STATEMENT = Extension.qCStatements.getId();
   private static final String CRL_DIST_POINTS = Extension.cRLDistributionPoints.getId();
   private static final String AUTH_INFO_ACCESS = Extension.authorityInfoAccess.getId();
   private static final String RESOURCE_NAME = "org.bouncycastle.pkix.CertPathReviewerMessages";
   private static final int NAME_CHECK_MAX = 1024;
   protected CertPath certPath;
   protected PKIXParameters pkixParams;
   protected Date currentDate;
   protected Date validDate;
   protected List certs;
   protected int n;
   protected List[] notifications;
   protected List[] errors;
   protected TrustAnchor trustAnchor;
   protected PublicKey subjectPublicKey;
   protected PolicyNode policyTree;
   private boolean initialized;

   public void init(CertPath var1, PKIXParameters var2) throws CertPathReviewerException {
      if (this.initialized) {
         throw new IllegalStateException("object is already initialized!");
      } else {
         this.initialized = true;
         if (var1 == null) {
            throw new NullPointerException("certPath was null");
         } else {
            List var3 = var1.getCertificates();
            if (var3.size() != 1) {
               HashSet var4 = new HashSet();

               for (TrustAnchor var6 : var2.getTrustAnchors()) {
                  var4.add(var6.getTrustedCert());
               }

               ArrayList var8 = new ArrayList();

               for (int var9 = 0; var9 != var3.size(); var9++) {
                  if (!var4.contains(var3.get(var9))) {
                     var8.add(var3.get(var9));
                  }
               }

               try {
                  CertificateFactory var10 = CertificateFactory.getInstance("X.509", "BC");
                  this.certPath = var10.generateCertPath(var8);
               } catch (GeneralSecurityException var7) {
                  throw new IllegalStateException("unable to rebuild certpath");
               }

               this.certs = var8;
            } else {
               this.certPath = var1;
               this.certs = var1.getCertificates();
            }

            this.n = this.certs.size();
            if (this.certs.isEmpty()) {
               throw new CertPathReviewerException(createErrorBundle("CertPathReviewer.emptyCertPath"));
            } else {
               this.pkixParams = (PKIXParameters)var2.clone();
               this.currentDate = new Date();
               this.validDate = getValidityDate(this.pkixParams, this.currentDate);
               this.notifications = null;
               this.errors = null;
               this.trustAnchor = null;
               this.subjectPublicKey = null;
               this.policyTree = null;
            }
         }
      }
   }

   public PKIXCertPathReviewer(CertPath var1, PKIXParameters var2) throws CertPathReviewerException {
      this.init(var1, var2);
   }

   public PKIXCertPathReviewer() {
   }

   public CertPath getCertPath() {
      return this.certPath;
   }

   public int getCertPathSize() {
      return this.n;
   }

   public List[] getErrors() {
      this.doChecks();
      return this.errors;
   }

   public List getErrors(int var1) {
      this.doChecks();
      return this.errors[var1 + 1];
   }

   public List[] getNotifications() {
      this.doChecks();
      return this.notifications;
   }

   public List getNotifications(int var1) {
      this.doChecks();
      return this.notifications[var1 + 1];
   }

   public PolicyNode getPolicyTree() {
      this.doChecks();
      return this.policyTree;
   }

   public PublicKey getSubjectPublicKey() {
      this.doChecks();
      return this.subjectPublicKey;
   }

   public TrustAnchor getTrustAnchor() {
      this.doChecks();
      return this.trustAnchor;
   }

   public boolean isValidCertPath() {
      this.doChecks();
      boolean var1 = true;

      for (int var2 = 0; var2 < this.errors.length; var2++) {
         if (!this.errors[var2].isEmpty()) {
            var1 = false;
            break;
         }
      }

      return var1;
   }

   protected void addNotification(ErrorBundle var1) {
      this.notifications[0].add(var1);
   }

   protected void addNotification(ErrorBundle var1, int var2) {
      if (var2 >= -1 && var2 < this.n) {
         this.notifications[var2 + 1].add(var1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected void addError(ErrorBundle var1) {
      this.errors[0].add(var1);
   }

   protected void addError(ErrorBundle var1, int var2) {
      if (var2 >= -1 && var2 < this.n) {
         this.errors[var2 + 1].add(var1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected void doChecks() {
      if (!this.initialized) {
         throw new IllegalStateException("Object not initialized. Call init() first.");
      } else {
         if (this.notifications == null) {
            this.notifications = new List[this.n + 1];
            this.errors = new List[this.n + 1];

            for (int var1 = 0; var1 < this.notifications.length; var1++) {
               this.notifications[var1] = new ArrayList();
               this.errors[var1] = new ArrayList();
            }

            this.checkSignatures();
            this.checkNameConstraints();
            this.checkPathLength();
            this.checkPolicy();
            this.checkCriticalExtensions();
         }
      }
   }

   private void checkNameConstraints() {
      Object var1 = null;
      PKIXNameConstraintValidator var2 = new PKIXNameConstraintValidator();

      try {
         for (int var3 = this.certs.size() - 1; var3 > 0; var3--) {
            int var4 = this.n - var3;
            var1 = (X509Certificate)this.certs.get(var3);
            if (!isSelfIssued((X509Certificate)var1)) {
               X500Principal var5 = getSubjectPrincipal((X509Certificate)var1);
               ASN1InputStream var6 = new ASN1InputStream(new ByteArrayInputStream(var5.getEncoded()));

               ASN1Sequence var7;
               try {
                  var7 = (ASN1Sequence)var6.readObject();
               } catch (IOException var18) {
                  ErrorBundle var9 = createErrorBundle("CertPathReviewer.ncSubjectNameError", new Object[]{new UntrustedInput(var5)});
                  throw new CertPathReviewerException(var9, var18, this.certPath, var3);
               }

               try {
                  var2.checkPermittedDN(var7);
               } catch (PKIXNameConstraintValidatorException var17) {
                  ErrorBundle var26 = createErrorBundle("CertPathReviewer.notPermittedDN", new Object[]{new UntrustedInput(var5.getName())});
                  throw new CertPathReviewerException(var26, var17, this.certPath, var3);
               }

               try {
                  var2.checkExcludedDN(var7);
               } catch (PKIXNameConstraintValidatorException var16) {
                  ErrorBundle var27 = createErrorBundle("CertPathReviewer.excludedDN", new Object[]{new UntrustedInput(var5.getName())});
                  throw new CertPathReviewerException(var27, var16, this.certPath, var3);
               }

               ASN1Sequence var8;
               try {
                  var8 = (ASN1Sequence)getExtensionValue((X509Extension)var1, SUBJECT_ALTERNATIVE_NAME);
               } catch (AnnotatedException var15) {
                  ErrorBundle var10 = createErrorBundle("CertPathReviewer.subjAltNameExtError");
                  throw new CertPathReviewerException(var10, var15, this.certPath, var3);
               }

               if (var8 != null) {
                  if (var8.size() > 1024) {
                     ErrorBundle var30 = createErrorBundle("CertPathReviewer.subjAltNameExtError");
                     throw new CertPathReviewerException(var30, this.certPath, var3);
                  }

                  for (int var28 = 0; var28 < var8.size(); var28++) {
                     GeneralName var31 = GeneralName.getInstance(var8.getObjectAt(var28));

                     try {
                        var2.checkPermitted(var31);
                        var2.checkExcluded(var31);
                     } catch (PKIXNameConstraintValidatorException var14) {
                        ErrorBundle var12 = createErrorBundle("CertPathReviewer.notPermittedEmail", new Object[]{new UntrustedInput(var31)});
                        throw new CertPathReviewerException(var12, var14, this.certPath, var3);
                     }
                  }
               }
            }

            ASN1Sequence var21;
            try {
               var21 = (ASN1Sequence)getExtensionValue((X509Extension)var1, NAME_CONSTRAINTS);
            } catch (AnnotatedException var13) {
               ErrorBundle var23 = createErrorBundle("CertPathReviewer.ncExtError");
               throw new CertPathReviewerException(var23, var13, this.certPath, var3);
            }

            if (var21 != null) {
               NameConstraints var22 = NameConstraints.getInstance(var21);
               GeneralSubtree[] var24 = var22.getPermittedSubtrees();
               if (var24 != null) {
                  var2.intersectPermittedSubtree(var24);
               }

               GeneralSubtree[] var25 = var22.getExcludedSubtrees();
               if (var25 != null) {
                  for (int var29 = 0; var29 != var25.length; var29++) {
                     var2.addExcludedSubtree(var25[var29]);
                  }
               }
            }
         }
      } catch (CertPathReviewerException var19) {
         this.addError(var19.getErrorMessage(), var19.getIndex());
      }
   }

   private void checkPathLength() {
      int var1 = this.n;
      int var2 = 0;
      Object var3 = null;

      for (int var5 = this.certs.size() - 1; var5 > 0; var5--) {
         int var4 = this.n - var5;
         var3 = (X509Certificate)this.certs.get(var5);
         if (!isSelfIssued((X509Certificate)var3)) {
            if (var1 <= 0) {
               ErrorBundle var6 = createErrorBundle("CertPathReviewer.pathLengthExtended");
               this.addError(var6);
            }

            var1--;
            var2++;
         }

         BasicConstraints var12;
         try {
            var12 = BasicConstraints.getInstance(getExtensionValue((X509Extension)var3, BASIC_CONSTRAINTS));
         } catch (AnnotatedException var9) {
            ErrorBundle var8 = createErrorBundle("CertPathReviewer.processLengthConstError");
            this.addError(var8, var5);
            var12 = null;
         }

         if (var12 != null && var12.isCA()) {
            ASN1Integer var7 = var12.getPathLenConstraintInteger();
            if (var7 != null) {
               var1 = Math.min(var1, var7.intPositiveValueExact());
            }
         }
      }

      ErrorBundle var11 = createErrorBundle("CertPathReviewer.totalPathLength", new Object[]{Integers.valueOf(var2)});
      this.addNotification(var11);
   }

   private void checkSignatures() {
      TrustAnchor var1 = null;
      X500Principal var2 = null;
      ErrorBundle var3 = createErrorBundle(
         "CertPathReviewer.certPathValidDate", new Object[]{new TrustedInput(this.validDate), new TrustedInput(this.currentDate)}
      );
      this.addNotification(var3);

      try {
         X509Certificate var34 = (X509Certificate)this.certs.get(this.certs.size() - 1);
         Collection var37 = this.getTrustAnchors(var34, this.pkixParams.getTrustAnchors());
         if (var37.size() > 1) {
            ErrorBundle var5 = createErrorBundle(
               "CertPathReviewer.conflictingTrustAnchors", new Object[]{Integers.valueOf(var37.size()), new UntrustedInput(var34.getIssuerX500Principal())}
            );
            this.addError(var5);
         } else if (var37.isEmpty()) {
            ErrorBundle var40 = createErrorBundle(
               "CertPathReviewer.noTrustAnchorFound",
               new Object[]{new UntrustedInput(var34.getIssuerX500Principal()), Integers.valueOf(this.pkixParams.getTrustAnchors().size())}
            );
            this.addError(var40);
         } else {
            var1 = (TrustAnchor)var37.iterator().next();
            PublicKey var41;
            if (var1.getTrustedCert() != null) {
               var41 = var1.getTrustedCert().getPublicKey();
            } else {
               var41 = var1.getCAPublicKey();
            }

            try {
               CertPathValidatorUtilities.verifyX509Certificate(var34, var41, this.pkixParams.getSigProvider());
            } catch (SignatureException var30) {
               ErrorBundle var7 = createErrorBundle("CertPathReviewer.trustButInvalidCert");
               this.addError(var7);
            } catch (Exception var31) {
            }
         }
      } catch (CertPathReviewerException var32) {
         this.addError(var32.getErrorMessage());
      } catch (Throwable var33) {
         ErrorBundle var4 = createErrorBundle("CertPathReviewer.unknown", new Object[]{new UntrustedInput(var33.getMessage()), new UntrustedInput(var33)});
         this.addError(var4);
      }

      if (var1 != null) {
         X509Certificate var35 = var1.getTrustedCert();

         try {
            if (var35 != null) {
               var2 = getSubjectPrincipal(var35);
            } else {
               var2 = new X500Principal(var1.getCAName());
            }
         } catch (IllegalArgumentException var29) {
            ErrorBundle var42 = createErrorBundle("CertPathReviewer.trustDNInvalid", new Object[]{new UntrustedInput(var1.getCAName())});
            this.addError(var42);
         }

         if (var35 != null) {
            boolean[] var38 = var35.getKeyUsage();
            if (var38 != null && (var38.length <= 5 || !var38[5])) {
               ErrorBundle var43 = createErrorBundle("CertPathReviewer.trustKeyUsage");
               this.addNotification(var43);
            }
         }
      }

      PublicKey var36 = null;
      X500Principal var39 = var2;
      X509Certificate var44 = null;
      AlgorithmIdentifier var6 = null;
      Object var49 = null;
      Object var8 = null;
      if (var1 != null) {
         var44 = var1.getTrustedCert();
         if (var44 != null) {
            var36 = var44.getPublicKey();
         } else {
            var36 = var1.getCAPublicKey();
         }

         try {
            var6 = getAlgorithmIdentifier(var36);
            var49 = var6.getAlgorithm();
            var8 = var6.getParameters();
         } catch (CertPathValidatorException var28) {
            ErrorBundle var10 = createErrorBundle("CertPathReviewer.trustPubKeyError");
            this.addError(var10);
            var6 = null;
         }
      }

      X509Certificate var9 = null;

      for (int var11 = this.certs.size() - 1; var11 >= 0; var11--) {
         int var57 = this.n - var11;
         var9 = (X509Certificate)this.certs.get(var11);
         if (var36 != null) {
            try {
               CertPathValidatorUtilities.verifyX509Certificate(var9, var36, this.pkixParams.getSigProvider());
            } catch (GeneralSecurityException var19) {
               ErrorBundle var13 = createErrorBundle(
                  "CertPathReviewer.signatureNotVerified", new Object[]{var19.getMessage(), var19, var19.getClass().getName()}
               );
               this.addError(var13, var11);
            }
         } else if (isSelfIssued(var9)) {
            try {
               CertPathValidatorUtilities.verifyX509Certificate(var9, var9.getPublicKey(), this.pkixParams.getSigProvider());
               ErrorBundle var12 = createErrorBundle("CertPathReviewer.rootKeyIsValidButNotATrustAnchor");
               this.addError(var12, var11);
            } catch (GeneralSecurityException var27) {
               ErrorBundle var63 = createErrorBundle(
                  "CertPathReviewer.signatureNotVerified", new Object[]{var27.getMessage(), var27, var27.getClass().getName()}
               );
               this.addError(var63, var11);
            }
         } else {
            ErrorBundle var58 = createErrorBundle("CertPathReviewer.NoIssuerPublicKey");
            byte[] var64 = var9.getExtensionValue(Extension.authorityKeyIdentifier.getId());
            if (var64 != null) {
               AuthorityKeyIdentifier var14 = AuthorityKeyIdentifier.getInstance(ASN1OctetString.getInstance(var64).getOctets());
               GeneralNames var15 = var14.getAuthorityCertIssuer();
               if (var15 != null) {
                  GeneralName var16 = var15.getNames()[0];
                  BigInteger var17 = var14.getAuthorityCertSerialNumber();
                  if (var17 != null) {
                     Object[] var18 = new Object[]{
                        new LocaleString("org.bouncycastle.pkix.CertPathReviewerMessages", "missingIssuer"),
                        " \"",
                        var16,
                        "\" ",
                        new LocaleString("org.bouncycastle.pkix.CertPathReviewerMessages", "missingSerial"),
                        " ",
                        var17
                     };
                     var58.setExtraArguments(var18);
                  }
               }
            }

            this.addError(var58, var11);
         }

         try {
            var9.checkValidity(this.validDate);
         } catch (CertificateNotYetValidException var25) {
            ErrorBundle var66 = createErrorBundle("CertPathReviewer.certificateNotYetValid", new Object[]{new TrustedInput(var9.getNotBefore())});
            this.addError(var66, var11);
         } catch (CertificateExpiredException var26) {
            ErrorBundle var65 = createErrorBundle("CertPathReviewer.certificateExpired", new Object[]{new TrustedInput(var9.getNotAfter())});
            this.addError(var65, var11);
         }

         if (this.pkixParams.isRevocationEnabled()) {
            CRLDistPoint var59 = null;

            try {
               ASN1Primitive var67 = getExtensionValue(var9, CRL_DIST_POINTS);
               if (var67 != null) {
                  var59 = CRLDistPoint.getInstance(var67);
               }
            } catch (AnnotatedException var24) {
               ErrorBundle var73 = createErrorBundle("CertPathReviewer.crlDistPtExtError");
               this.addError(var73, var11);
            }

            AuthorityInformationAccess var68 = null;

            try {
               ASN1Primitive var74 = getExtensionValue(var9, AUTH_INFO_ACCESS);
               if (var74 != null) {
                  var68 = AuthorityInformationAccess.getInstance(var74);
               }
            } catch (AnnotatedException var23) {
               ErrorBundle var78 = createErrorBundle("CertPathReviewer.crlAuthInfoAccError");
               this.addError(var78, var11);
            }

            Vector var75 = this.getCRLDistUrls(var59);
            Vector var79 = this.getOCSPUrls(var68);
            Iterator var80 = var75.iterator();

            while (var80.hasNext()) {
               ErrorBundle var82 = createErrorBundle("CertPathReviewer.crlDistPoint", new Object[]{new UntrustedUrlInput(var80.next())});
               this.addNotification(var82, var11);
            }

            var80 = var79.iterator();

            while (var80.hasNext()) {
               ErrorBundle var83 = createErrorBundle("CertPathReviewer.ocspLocation", new Object[]{new UntrustedUrlInput(var80.next())});
               this.addNotification(var83, var11);
            }

            try {
               this.checkRevocation(this.pkixParams, var9, this.validDate, var44, var36, var75, var79, var11);
            } catch (CertPathReviewerException var22) {
               this.addError(var22.getErrorMessage(), var11);
            }
         }

         if (var39 != null && !var9.getIssuerX500Principal().equals(var39)) {
            ErrorBundle var60 = createErrorBundle("CertPathReviewer.certWrongIssuer", new Object[]{var39.getName(), var9.getIssuerX500Principal().getName()});
            this.addError(var60, var11);
         }

         if (var57 != this.n) {
            if (var9 != null && var9.getVersion() == 1) {
               ErrorBundle var61 = createErrorBundle("CertPathReviewer.noCACert");
               this.addError(var61, var11);
            }

            try {
               BasicConstraints var62 = BasicConstraints.getInstance(getExtensionValue(var9, BASIC_CONSTRAINTS));
               if (var62 != null) {
                  if (!var62.isCA()) {
                     ErrorBundle var69 = createErrorBundle("CertPathReviewer.noCACert");
                     this.addError(var69, var11);
                  }
               } else {
                  ErrorBundle var70 = createErrorBundle("CertPathReviewer.noBasicConstraints");
                  this.addError(var70, var11);
               }
            } catch (AnnotatedException var21) {
               ErrorBundle var76 = createErrorBundle("CertPathReviewer.errorProcesingBC");
               this.addError(var76, var11);
            }

            boolean[] var71 = var9.getKeyUsage();
            if (var71 != null && (var71.length <= 5 || !var71[5])) {
               ErrorBundle var77 = createErrorBundle("CertPathReviewer.noCertSign");
               this.addError(var77, var11);
            }
         }

         var44 = var9;
         var39 = var9.getSubjectX500Principal();

         try {
            var36 = getNextWorkingKey(this.certs, var11);
            var6 = getAlgorithmIdentifier(var36);
            var49 = var6.getAlgorithm();
            var8 = var6.getParameters();
         } catch (CertPathValidatorException var20) {
            ErrorBundle var72 = createErrorBundle("CertPathReviewer.pubKeyError");
            this.addError(var72, var11);
            var6 = null;
            var49 = null;
            var8 = null;
         }
      }

      this.trustAnchor = var1;
      this.subjectPublicKey = var36;
   }

   private void checkPolicy() {
      Set var1 = this.pkixParams.getInitialPolicies();
      ArrayList[] var2 = new ArrayList[this.n + 1];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = new ArrayList();
      }

      HashSet var37 = new HashSet();
      var37.add("2.5.29.32.0");
      PKIXPolicyNode var4 = new PKIXPolicyNode(new ArrayList(), 0, var37, null, new HashSet(), "2.5.29.32.0", false);
      var2[0].add(var4);
      int var5;
      if (this.pkixParams.isExplicitPolicyRequired()) {
         var5 = 0;
      } else {
         var5 = this.n + 1;
      }

      int var6;
      if (this.pkixParams.isAnyPolicyInhibited()) {
         var6 = 0;
      } else {
         var6 = this.n + 1;
      }

      int var7;
      if (this.pkixParams.isPolicyMappingInhibited()) {
         var7 = 0;
      } else {
         var7 = this.n + 1;
      }

      HashSet var8 = null;
      X509Certificate var9 = null;

      try {
         int var10;
         for (var10 = this.certs.size() - 1; var10 >= 0; var10--) {
            int var11 = this.n - var10;
            var9 = (X509Certificate)this.certs.get(var10);

            ASN1Sequence var12;
            try {
               var12 = (ASN1Sequence)getExtensionValue(var9, CERTIFICATE_POLICIES);
            } catch (AnnotatedException var33) {
               ErrorBundle var14 = createErrorBundle("CertPathReviewer.policyExtError");
               throw new CertPathReviewerException(var14, var33, this.certPath, var10);
            }

            if (var12 != null && var4 != null) {
               Enumeration var13 = var12.getObjects();
               HashSet var51 = new HashSet();

               while (var13.hasMoreElements()) {
                  PolicyInformation var15 = PolicyInformation.getInstance(var13.nextElement());
                  ASN1ObjectIdentifier var16 = var15.getPolicyIdentifier();
                  var51.add(var16.getId());
                  if (!"2.5.29.32.0".equals(var16.getId())) {
                     Set var17;
                     try {
                        var17 = getQualifierSet(var15.getPolicyQualifiers());
                     } catch (CertPathValidatorException var32) {
                        ErrorBundle var19 = createErrorBundle("CertPathReviewer.policyQualifierError");
                        throw new CertPathReviewerException(var19, var32, this.certPath, var10);
                     }

                     boolean var18 = processCertD1i(var11, var2, var16, var17);
                     if (!var18) {
                        processCertD1ii(var11, var2, var16, var17);
                     }
                  }
               }

               if (var8 != null && !var8.contains("2.5.29.32.0")) {
                  Iterator var61 = var8.iterator();
                  HashSet var79 = new HashSet();

                  while (var61.hasNext()) {
                     Object var92 = var61.next();
                     if (var51.contains(var92)) {
                        var79.add(var92);
                     }
                  }

                  var8 = var79;
               } else {
                  var8 = var51;
               }

               if (var6 > 0 || var11 < this.n && isSelfIssued(var9)) {
                  var13 = var12.getObjects();

                  while (var13.hasMoreElements()) {
                     PolicyInformation var62 = PolicyInformation.getInstance(var13.nextElement());
                     if ("2.5.29.32.0".equals(var62.getPolicyIdentifier().getId())) {
                        Set var80;
                        try {
                           var80 = getQualifierSet(var62.getPolicyQualifiers());
                        } catch (CertPathValidatorException var31) {
                           ErrorBundle var105 = createErrorBundle("CertPathReviewer.policyQualifierError");
                           throw new CertPathReviewerException(var105, var31, this.certPath, var10);
                        }

                        ArrayList var93 = var2[var11 - 1];

                        label435:
                        for (int var106 = 0; var106 < var93.size(); var106++) {
                           PKIXPolicyNode var116 = (PKIXPolicyNode)var93.get(var106);
                           Iterator var20 = var116.getExpectedPolicies().iterator();

                           while (true) {
                              String var22;
                              while (true) {
                                 if (!var20.hasNext()) {
                                    continue label435;
                                 }

                                 Object var21 = var20.next();
                                 if (var21 instanceof String) {
                                    var22 = (String)var21;
                                    break;
                                 }

                                 if (var21 instanceof ASN1ObjectIdentifier) {
                                    var22 = ((ASN1ObjectIdentifier)var21).getId();
                                    break;
                                 }
                              }

                              boolean var23 = false;
                              Iterator var24 = var116.getChildren();

                              while (var24.hasNext()) {
                                 PKIXPolicyNode var25 = (PKIXPolicyNode)var24.next();
                                 if (var22.equals(var25.getValidPolicy())) {
                                    var23 = true;
                                 }
                              }

                              if (!var23) {
                                 HashSet var127 = new HashSet();
                                 var127.add(var22);
                                 PKIXPolicyNode var26 = new PKIXPolicyNode(new ArrayList(), var11, var127, var116, var80, var22, false);
                                 var116.addChild(var26);
                                 var2[var11].add(var26);
                              }
                           }
                        }
                        break;
                     }
                  }
               }

               for (int var63 = var11 - 1; var63 >= 0; var63--) {
                  ArrayList var81 = var2[var63];

                  for (int var94 = 0; var94 < var81.size(); var94++) {
                     PKIXPolicyNode var107 = (PKIXPolicyNode)var81.get(var94);
                     if (!var107.hasChildren()) {
                        var4 = removePolicyNode(var4, var2, var107);
                        if (var4 == null) {
                           break;
                        }
                     }
                  }
               }

               Set var64 = var9.getCriticalExtensionOIDs();
               if (var64 != null) {
                  boolean var82 = var64.contains(CERTIFICATE_POLICIES);
                  ArrayList var95 = var2[var11];

                  for (int var108 = 0; var108 < var95.size(); var108++) {
                     PKIXPolicyNode var117 = (PKIXPolicyNode)var95.get(var108);
                     var117.setCritical(var82);
                  }
               }
            }

            if (var12 == null) {
               var4 = null;
            }

            if (var5 <= 0 && var4 == null) {
               ErrorBundle var43 = createErrorBundle("CertPathReviewer.noValidPolicyTree");
               throw new CertPathReviewerException(var43);
            }

            if (var11 != this.n) {
               ASN1Primitive var42;
               try {
                  var42 = getExtensionValue(var9, POLICY_MAPPINGS);
               } catch (AnnotatedException var30) {
                  ErrorBundle var65 = createErrorBundle("CertPathReviewer.policyMapExtError");
                  throw new CertPathReviewerException(var65, var30, this.certPath, var10);
               }

               if (var42 != null) {
                  ASN1Sequence var52 = (ASN1Sequence)var42;

                  for (int var66 = 0; var66 < var52.size(); var66++) {
                     ASN1Sequence var83 = (ASN1Sequence)var52.getObjectAt(var66);
                     ASN1ObjectIdentifier var96 = (ASN1ObjectIdentifier)var83.getObjectAt(0);
                     ASN1ObjectIdentifier var109 = (ASN1ObjectIdentifier)var83.getObjectAt(1);
                     if ("2.5.29.32.0".equals(var96.getId())) {
                        ErrorBundle var119 = createErrorBundle("CertPathReviewer.invalidPolicyMapping");
                        throw new CertPathReviewerException(var119, this.certPath, var10);
                     }

                     if ("2.5.29.32.0".equals(var109.getId())) {
                        ErrorBundle var118 = createErrorBundle("CertPathReviewer.invalidPolicyMapping");
                        throw new CertPathReviewerException(var118, this.certPath, var10);
                     }
                  }
               }

               if (var42 != null) {
                  ASN1Sequence var53 = (ASN1Sequence)var42;
                  HashMap var67 = new HashMap();
                  HashSet var84 = new HashSet();

                  for (int var97 = 0; var97 < var53.size(); var97++) {
                     ASN1Sequence var110 = (ASN1Sequence)var53.getObjectAt(var97);
                     String var120 = ((ASN1ObjectIdentifier)var110.getObjectAt(0)).getId();
                     String var122 = ((ASN1ObjectIdentifier)var110.getObjectAt(1)).getId();
                     if (!var67.containsKey(var120)) {
                        HashSet var125 = new HashSet();
                        var125.add(var122);
                        var67.put(var120, var125);
                        var84.add(var120);
                     } else {
                        Set var126 = (Set)var67.get(var120);
                        var126.add(var122);
                     }
                  }

                  for (String var111 : var84) {
                     if (var7 > 0) {
                        try {
                           prepareNextCertB1(var11, var2, var111, var67, var9);
                        } catch (AnnotatedException var28) {
                           ErrorBundle var124 = createErrorBundle("CertPathReviewer.policyExtError");
                           throw new CertPathReviewerException(var124, var28, this.certPath, var10);
                        } catch (CertPathValidatorException var29) {
                           ErrorBundle var123 = createErrorBundle("CertPathReviewer.policyQualifierError");
                           throw new CertPathReviewerException(var123, var29, this.certPath, var10);
                        }
                     } else if (var7 <= 0) {
                        var4 = prepareNextCertB2(var11, var2, var111, var4);
                     }
                  }
               }

               if (!isSelfIssued(var9)) {
                  if (var5 != 0) {
                     var5--;
                  }

                  if (var7 != 0) {
                     var7--;
                  }

                  if (var6 != 0) {
                     var6--;
                  }
               }

               try {
                  ASN1Sequence var54 = (ASN1Sequence)getExtensionValue(var9, POLICY_CONSTRAINTS);
                  if (var54 != null) {
                     Enumeration var69 = var54.getObjects();

                     while (var69.hasMoreElements()) {
                        ASN1TaggedObject var85 = (ASN1TaggedObject)var69.nextElement();
                        switch (var85.getTagNo()) {
                           case 0:
                              int var100 = ASN1Integer.getInstance(var85, false).intValueExact();
                              if (var100 < var5) {
                                 var5 = var100;
                              }
                              break;
                           case 1:
                              int var99 = ASN1Integer.getInstance(var85, false).intValueExact();
                              if (var99 < var7) {
                                 var7 = var99;
                              }
                        }
                     }
                  }
               } catch (AnnotatedException var35) {
                  ErrorBundle var68 = createErrorBundle("CertPathReviewer.policyConstExtError");
                  throw new CertPathReviewerException(var68, this.certPath, var10);
               }

               try {
                  ASN1Integer var55 = (ASN1Integer)getExtensionValue(var9, INHIBIT_ANY_POLICY);
                  if (var55 != null) {
                     int var71 = var55.intValueExact();
                     if (var71 < var6) {
                        var6 = var71;
                     }
                  }
               } catch (AnnotatedException var27) {
                  ErrorBundle var70 = createErrorBundle("CertPathReviewer.policyInhibitExtError");
                  throw new CertPathReviewerException(var70, this.certPath, var10);
               }
            }
         }

         if (!isSelfIssued(var9) && var5 > 0) {
            var5--;
         }

         try {
            ASN1Sequence var39 = (ASN1Sequence)getExtensionValue(var9, POLICY_CONSTRAINTS);
            if (var39 != null) {
               Enumeration var45 = var39.getObjects();

               while (var45.hasMoreElements()) {
                  ASN1TaggedObject var56 = (ASN1TaggedObject)var45.nextElement();
                  switch (var56.getTagNo()) {
                     case 0:
                        int var72 = ASN1Integer.getInstance(var56, false).intValueExact();
                        if (var72 == 0) {
                           var5 = 0;
                        }
                  }
               }
            }
         } catch (AnnotatedException var34) {
            ErrorBundle var44 = createErrorBundle("CertPathReviewer.policyConstExtError");
            throw new CertPathReviewerException(var44, this.certPath, var10);
         }

         PKIXPolicyNode var40;
         if (var4 == null) {
            if (this.pkixParams.isExplicitPolicyRequired()) {
               ErrorBundle var50 = createErrorBundle("CertPathReviewer.explicitPolicy");
               throw new CertPathReviewerException(var50, this.certPath, var10);
            }

            var40 = null;
         } else if (isAnyPolicy(var1)) {
            if (this.pkixParams.isExplicitPolicyRequired()) {
               if (var8.isEmpty()) {
                  ErrorBundle var48 = createErrorBundle("CertPathReviewer.explicitPolicy");
                  throw new CertPathReviewerException(var48, this.certPath, var10);
               }

               HashSet var47 = new HashSet();

               for (int var59 = 0; var59 < var2.length; var59++) {
                  ArrayList var76 = var2[var59];

                  for (int var89 = 0; var89 < var76.size(); var89++) {
                     PKIXPolicyNode var103 = (PKIXPolicyNode)var76.get(var89);
                     if ("2.5.29.32.0".equals(var103.getValidPolicy())) {
                        Iterator var114 = var103.getChildren();

                        while (var114.hasNext()) {
                           var47.add(var114.next());
                        }
                     }
                  }
               }

               for (PKIXPolicyNode var77 : var47) {
                  String var90 = var77.getValidPolicy();
                  if (!var8.contains(var90)) {
                  }
               }

               if (var4 != null) {
                  for (int var78 = this.n - 1; var78 >= 0; var78--) {
                     ArrayList var91 = var2[var78];

                     for (int var104 = 0; var104 < var91.size(); var104++) {
                        PKIXPolicyNode var115 = (PKIXPolicyNode)var91.get(var104);
                        if (!var115.hasChildren()) {
                           var4 = removePolicyNode(var4, var2, var115);
                        }
                     }
                  }
               }
            }

            var40 = var4;
         } else {
            HashSet var46 = new HashSet();

            for (int var57 = 0; var57 < var2.length; var57++) {
               ArrayList var73 = var2[var57];

               for (int var86 = 0; var86 < var73.size(); var86++) {
                  PKIXPolicyNode var101 = (PKIXPolicyNode)var73.get(var86);
                  if ("2.5.29.32.0".equals(var101.getValidPolicy())) {
                     Iterator var112 = var101.getChildren();

                     while (var112.hasNext()) {
                        PKIXPolicyNode var121 = (PKIXPolicyNode)var112.next();
                        if (!"2.5.29.32.0".equals(var121.getValidPolicy())) {
                           var46.add(var121);
                        }
                     }
                  }
               }
            }

            for (PKIXPolicyNode var74 : var46) {
               String var87 = var74.getValidPolicy();
               if (!var1.contains(var87)) {
                  var4 = removePolicyNode(var4, var2, var74);
               }
            }

            if (var4 != null) {
               for (int var75 = this.n - 1; var75 >= 0; var75--) {
                  ArrayList var88 = var2[var75];

                  for (int var102 = 0; var102 < var88.size(); var102++) {
                     PKIXPolicyNode var113 = (PKIXPolicyNode)var88.get(var102);
                     if (!var113.hasChildren()) {
                        var4 = removePolicyNode(var4, var2, var113);
                     }
                  }
               }
            }

            var40 = var4;
         }

         if (var5 <= 0 && var40 == null) {
            ErrorBundle var49 = createErrorBundle("CertPathReviewer.invalidPolicy");
            throw new CertPathReviewerException(var49);
         }
      } catch (CertPathReviewerException var36) {
         this.addError(var36.getErrorMessage(), var36.getIndex());
         var4 = null;
      }
   }

   private void checkCriticalExtensions() {
      List var1 = this.pkixParams.getCertPathCheckers();
      Iterator var2 = var1.iterator();

      try {
         try {
            while (var2.hasNext()) {
               ((PKIXCertPathChecker)var2.next()).init(false);
            }
         } catch (CertPathValidatorException var10) {
            ErrorBundle var4 = createErrorBundle("CertPathReviewer.certPathCheckerError", new Object[]{var10.getMessage(), var10, var10.getClass().getName()});
            throw new CertPathReviewerException(var4, var10);
         }

         X509Certificate var3 = null;

         for (int var13 = this.certs.size() - 1; var13 >= 0; var13--) {
            var3 = (X509Certificate)this.certs.get(var13);
            Set var5 = var3.getCriticalExtensionOIDs();
            if (var5 != null && !var5.isEmpty()) {
               var5.remove(KEY_USAGE);
               var5.remove(CERTIFICATE_POLICIES);
               var5.remove(POLICY_MAPPINGS);
               var5.remove(INHIBIT_ANY_POLICY);
               var5.remove(ISSUING_DISTRIBUTION_POINT);
               var5.remove(DELTA_CRL_INDICATOR);
               var5.remove(POLICY_CONSTRAINTS);
               var5.remove(BASIC_CONSTRAINTS);
               var5.remove(SUBJECT_ALTERNATIVE_NAME);
               var5.remove(NAME_CONSTRAINTS);
               if (var13 == 0) {
                  var5.remove(Extension.extendedKeyUsage.getId());
               }

               if (var5.contains(QC_STATEMENT) && this.processQcStatements(var3, var13)) {
                  var5.remove(QC_STATEMENT);
               }

               Iterator var6 = var1.iterator();

               while (var6.hasNext()) {
                  try {
                     ((PKIXCertPathChecker)var6.next()).check(var3, var5);
                  } catch (CertPathValidatorException var9) {
                     ErrorBundle var8 = createErrorBundle(
                        "CertPathReviewer.criticalExtensionError", new Object[]{var9.getMessage(), var9, var9.getClass().getName()}
                     );
                     throw new CertPathReviewerException(var8, var9.getCause(), this.certPath, var13);
                  }
               }

               if (!var5.isEmpty()) {
                  Iterator var14 = var5.iterator();

                  while (var14.hasNext()) {
                     ErrorBundle var7 = createErrorBundle("CertPathReviewer.unknownCriticalExt", new Object[]{new ASN1ObjectIdentifier((String)var14.next())});
                     this.addError(var7, var13);
                  }
               }
            }
         }
      } catch (CertPathReviewerException var11) {
         this.addError(var11.getErrorMessage(), var11.getIndex());
      }
   }

   private boolean processQcStatements(X509Certificate var1, int var2) {
      try {
         boolean var3 = false;
         ASN1Sequence var13 = (ASN1Sequence)getExtensionValue(var1, QC_STATEMENT);

         for (int var5 = 0; var5 < var13.size(); var5++) {
            QCStatement var6 = QCStatement.getInstance(var13.getObjectAt(var5));
            if (QCStatement.id_etsi_qcs_QcCompliance.equals(var6.getStatementId())) {
               ErrorBundle var7 = createErrorBundle("CertPathReviewer.QcEuCompliance");
               this.addNotification(var7, var2);
            } else if (!QCStatement.id_qcs_pkixQCSyntax_v1.equals(var6.getStatementId())) {
               if (QCStatement.id_etsi_qcs_QcSSCD.equals(var6.getStatementId())) {
                  ErrorBundle var14 = createErrorBundle("CertPathReviewer.QcSSCD");
                  this.addNotification(var14, var2);
               } else if (QCStatement.id_etsi_qcs_LimiteValue.equals(var6.getStatementId())) {
                  MonetaryValue var15 = MonetaryValue.getInstance(var6.getStatementInfo());
                  Iso4217CurrencyCode var8 = var15.getCurrency();
                  double var9 = var15.getAmount().doubleValue() * Math.pow(10.0, var15.getExponent().doubleValue());
                  ErrorBundle var11;
                  if (var15.getCurrency().isAlphabetic()) {
                     var11 = createErrorBundle(
                        "CertPathReviewer.QcLimitValueAlpha", new Object[]{var15.getCurrency().getAlphabetic(), new TrustedInput(new Double(var9)), var15}
                     );
                  } else {
                     var11 = createErrorBundle(
                        "CertPathReviewer.QcLimitValueNum",
                        new Object[]{Integers.valueOf(var15.getCurrency().getNumeric()), new TrustedInput(new Double(var9)), var15}
                     );
                  }

                  this.addNotification(var11, var2);
               } else {
                  ErrorBundle var16 = createErrorBundle("CertPathReviewer.QcUnknownStatement", new Object[]{var6.getStatementId(), new UntrustedInput(var6)});
                  this.addNotification(var16, var2);
                  var3 = true;
               }
            }
         }

         return !var3;
      } catch (AnnotatedException var12) {
         ErrorBundle var4 = createErrorBundle("CertPathReviewer.QcStatementExtError");
         this.addError(var4, var2);
         return false;
      }
   }

   private String IPtoString(byte[] var1) {
      String var2;
      try {
         var2 = InetAddress.getByAddress(var1).getHostAddress();
      } catch (Exception var6) {
         StringBuilder var4 = new StringBuilder();

         for (int var5 = 0; var5 != var1.length; var5++) {
            var4.append(Integer.toHexString(var1[var5] & 255));
            var4.append(' ');
         }

         var2 = var4.toString();
      }

      return var2;
   }

   protected void checkRevocation(
      PKIXParameters var1, X509Certificate var2, Date var3, X509Certificate var4, PublicKey var5, Vector var6, Vector var7, int var8
   ) throws CertPathReviewerException {
      this.checkCRLs(var1, var2, var3, var4, var5, var6, var8);
   }

   protected void checkCRLs(PKIXParameters var1, X509Certificate var2, Date var3, X509Certificate var4, PublicKey var5, Vector var6, int var7) throws CertPathReviewerException {
      X509CRLStoreSelector var8 = new X509CRLStoreSelector();

      try {
         var8.addIssuerName(getEncodedIssuerPrincipal(var2).getEncoded());
      } catch (IOException var32) {
         ErrorBundle var10 = createErrorBundle("CertPathReviewer.crlIssuerException");
         throw new CertPathReviewerException(var10, var32);
      }

      var8.setCertificateChecking(var2);

      Iterator var9;
      try {
         Set var35 = PKIXCRLUtil.findCRLs(var8, var1);
         var9 = var35.iterator();
         if (var35.isEmpty()) {
            var35 = PKIXCRLUtil.findCRLs(new X509CRLStoreSelector(), var1);
            Iterator var38 = var35.iterator();
            ArrayList var12 = new ArrayList();

            while (var38.hasNext()) {
               var12.add(((X509CRL)var38.next()).getIssuerX500Principal());
            }

            int var13 = var12.size();
            ErrorBundle var14 = createErrorBundle(
               "CertPathReviewer.noCrlInCertstore", new Object[]{new UntrustedInput(var8.getIssuerNames()), new UntrustedInput(var12), Integers.valueOf(var13)}
            );
            this.addNotification(var14, var7);
         }
      } catch (AnnotatedException var34) {
         ErrorBundle var11 = createErrorBundle(
            "CertPathReviewer.crlExtractionError", new Object[]{var34.getCause().getMessage(), var34.getCause(), var34.getCause().getClass().getName()}
         );
         this.addError(var11, var7);
         var9 = new ArrayList().iterator();
      }

      boolean var37 = false;
      X509CRL var39 = null;

      while (var9.hasNext()) {
         var39 = (X509CRL)var9.next();
         Date var40 = var39.getThisUpdate();
         Date var43 = var39.getNextUpdate();
         Object[] var52 = new Object[]{new TrustedInput(var40), new TrustedInput(var43)};
         if (var43 == null || var3.before(var43)) {
            var37 = true;
            ErrorBundle var60 = createErrorBundle("CertPathReviewer.localValidCRL", var52);
            this.addNotification(var60, var7);
            break;
         }

         ErrorBundle var15 = createErrorBundle("CertPathReviewer.localInvalidCRL", var52);
         this.addNotification(var15, var7);
      }

      if (!var37) {
         X500Principal var41 = var2.getIssuerX500Principal();
         X509CRL var44 = null;

         for (String var61 : var6) {
            try {
               var44 = this.getCRL(var61);
               if (var44 != null) {
                  X500Principal var16 = var44.getIssuerX500Principal();
                  if (!var41.equals(var16)) {
                     ErrorBundle var69 = createErrorBundle(
                        "CertPathReviewer.onlineCRLWrongCA",
                        new Object[]{new UntrustedInput(var16.getName()), new UntrustedInput(var41.getName()), new UntrustedUrlInput(var61)}
                     );
                     this.addNotification(var69, var7);
                  } else {
                     Date var17 = var44.getThisUpdate();
                     Date var18 = var44.getNextUpdate();
                     Object[] var19 = new Object[]{new TrustedInput(var17), new TrustedInput(var18), new UntrustedUrlInput(var61)};
                     if (var18 == null || var3.before(var18)) {
                        var37 = true;
                        ErrorBundle var83 = createErrorBundle("CertPathReviewer.onlineValidCRL", var19);
                        this.addNotification(var83, var7);
                        var39 = var44;
                        break;
                     }

                     ErrorBundle var20 = createErrorBundle("CertPathReviewer.onlineInvalidCRL", var19);
                     this.addNotification(var20, var7);
                  }
               }
            } catch (CertPathReviewerException var33) {
               this.addNotification(var33.getErrorMessage(), var7);
            }
         }
      }

      if (var39 != null) {
         if (var4 != null) {
            boolean[] var46 = var4.getKeyUsage();
            if (var46 != null && (var46.length <= 6 || !var46[6])) {
               ErrorBundle var59 = createErrorBundle("CertPathReviewer.noCrlSigningPermited");
               throw new CertPathReviewerException(var59);
            }
         }

         if (var5 == null) {
            ErrorBundle var51 = createErrorBundle("CertPathReviewer.crlNoIssuerPublicKey");
            throw new CertPathReviewerException(var51);
         }

         try {
            var39.verify(var5, "BC");
         } catch (Exception var31) {
            ErrorBundle var54 = createErrorBundle("CertPathReviewer.crlVerifyFailed");
            throw new CertPathReviewerException(var54, var31);
         }

         X509CRLEntry var42 = var39.getRevokedCertificate(var2.getSerialNumber());
         if (var42 != null) {
            String var47 = null;
            if (var42.hasExtensions()) {
               ASN1Enumerated var55;
               try {
                  var55 = ASN1Enumerated.getInstance(getExtensionValue(var42, Extension.reasonCode.getId()));
               } catch (AnnotatedException var30) {
                  ErrorBundle var65 = createErrorBundle("CertPathReviewer.crlReasonExtError");
                  throw new CertPathReviewerException(var65, var30);
               }

               if (var55 != null) {
                  var47 = crlReasons[var55.intValueExact()];
               }
            }

            if (var47 == null) {
               var47 = crlReasons[7];
            }

            LocaleString var56 = new LocaleString("org.bouncycastle.pkix.CertPathReviewerMessages", var47);
            if (!var3.before(var42.getRevocationDate())) {
               ErrorBundle var63 = createErrorBundle("CertPathReviewer.certRevoked", new Object[]{new TrustedInput(var42.getRevocationDate()), var56});
               throw new CertPathReviewerException(var63);
            }

            ErrorBundle var62 = createErrorBundle("CertPathReviewer.revokedAfterValidation", new Object[]{new TrustedInput(var42.getRevocationDate()), var56});
            this.addNotification(var62, var7);
         } else {
            ErrorBundle var48 = createErrorBundle("CertPathReviewer.notRevoked");
            this.addNotification(var48, var7);
         }

         Date var49 = var39.getNextUpdate();
         if (var49 != null && !var3.before(var49)) {
            ErrorBundle var57 = createErrorBundle("CertPathReviewer.crlUpdateAvailable", new Object[]{new TrustedInput(var49)});
            this.addNotification(var57, var7);
         }

         ASN1Primitive var58;
         try {
            var58 = getExtensionValue(var39, ISSUING_DISTRIBUTION_POINT);
         } catch (AnnotatedException var29) {
            ErrorBundle var66 = createErrorBundle("CertPathReviewer.distrPtExtError");
            throw new CertPathReviewerException(var66);
         }

         ASN1Primitive var64;
         try {
            var64 = getExtensionValue(var39, DELTA_CRL_INDICATOR);
         } catch (AnnotatedException var28) {
            ErrorBundle var70 = createErrorBundle("CertPathReviewer.deltaCrlExtError");
            throw new CertPathReviewerException(var70);
         }

         if (var64 != null) {
            X509CRLStoreSelector var67 = new X509CRLStoreSelector();

            try {
               var67.addIssuerName(getIssuerPrincipal(var39).getEncoded());
            } catch (IOException var27) {
               ErrorBundle var74 = createErrorBundle("CertPathReviewer.crlIssuerException");
               throw new CertPathReviewerException(var74, var27);
            }

            var67.setMinCRLNumber(((ASN1Integer)var64).getPositiveValue());

            try {
               var67.setMaxCRLNumber(((ASN1Integer)getExtensionValue(var39, CRL_NUMBER)).getPositiveValue().subtract(BigInteger.valueOf(1L)));
            } catch (AnnotatedException var26) {
               ErrorBundle var75 = createErrorBundle("CertPathReviewer.crlNbrExtError");
               throw new CertPathReviewerException(var75, var26);
            }

            boolean var71 = false;

            Iterator var76;
            try {
               var76 = PKIXCRLUtil.findCRLs(var67, var1).iterator();
            } catch (AnnotatedException var25) {
               ErrorBundle var84 = createErrorBundle("CertPathReviewer.crlExtractionError");
               throw new CertPathReviewerException(var84, var25);
            }

            while (var76.hasNext()) {
               X509CRL var80 = (X509CRL)var76.next();

               ASN1Primitive var85;
               try {
                  var85 = getExtensionValue(var80, ISSUING_DISTRIBUTION_POINT);
               } catch (AnnotatedException var24) {
                  ErrorBundle var22 = createErrorBundle("CertPathReviewer.distrPtExtError");
                  throw new CertPathReviewerException(var22, var24);
               }

               if (Objects.areEqual(var58, var85)) {
                  var71 = true;
                  break;
               }
            }

            if (!var71) {
               ErrorBundle var82 = createErrorBundle("CertPathReviewer.noBaseCRL");
               throw new CertPathReviewerException(var82);
            }
         }

         if (var58 != null) {
            IssuingDistributionPoint var68 = IssuingDistributionPoint.getInstance(var58);
            Object var72 = null;

            try {
               var72 = BasicConstraints.getInstance(getExtensionValue(var2, BASIC_CONSTRAINTS));
            } catch (AnnotatedException var23) {
               ErrorBundle var81 = createErrorBundle("CertPathReviewer.crlBCExtError");
               throw new CertPathReviewerException(var81, var23);
            }

            if (var68.onlyContainsUserCerts() && var72 != null && ((BasicConstraints)var72).isCA()) {
               ErrorBundle var79 = createErrorBundle("CertPathReviewer.crlOnlyUserCert");
               throw new CertPathReviewerException(var79);
            }

            if (var68.onlyContainsCACerts() && (var72 == null || !((BasicConstraints)var72).isCA())) {
               ErrorBundle var78 = createErrorBundle("CertPathReviewer.crlOnlyCaCert");
               throw new CertPathReviewerException(var78);
            }

            if (var68.onlyContainsAttributeCerts()) {
               ErrorBundle var77 = createErrorBundle("CertPathReviewer.crlOnlyAttrCert");
               throw new CertPathReviewerException(var77);
            }
         }
      }

      if (!var37) {
         ErrorBundle var50 = createErrorBundle("CertPathReviewer.noValidCrlFound");
         throw new CertPathReviewerException(var50);
      }
   }

   protected Vector getCRLDistUrls(CRLDistPoint var1) {
      Vector var2 = new Vector();
      if (var1 != null) {
         DistributionPoint[] var3 = var1.getDistributionPoints();

         for (int var4 = 0; var4 < var3.length; var4++) {
            DistributionPointName var5 = var3[var4].getDistributionPoint();
            if (var5.getType() == 0) {
               GeneralName[] var6 = GeneralNames.getInstance(var5.getName()).getNames();

               for (int var7 = 0; var7 < var6.length; var7++) {
                  if (var6[var7].getTagNo() == 6) {
                     String var8 = ((ASN1IA5String)var6[var7].getName()).getString();
                     var2.add(var8);
                  }
               }
            }
         }
      }

      return var2;
   }

   protected Vector getOCSPUrls(AuthorityInformationAccess var1) {
      Vector var2 = new Vector();
      if (var1 != null) {
         AccessDescription[] var3 = var1.getAccessDescriptions();

         for (int var4 = 0; var4 < var3.length; var4++) {
            if (var3[var4].getAccessMethod().equals(AccessDescription.id_ad_ocsp)) {
               GeneralName var5 = var3[var4].getAccessLocation();
               if (var5.getTagNo() == 6) {
                  String var6 = ((ASN1IA5String)var5.getName()).getString();
                  var2.add(var6);
               }
            }
         }
      }

      return var2;
   }

   private X509CRL getCRL(String var1) throws CertPathReviewerException {
      X509CRL var2 = null;

      try {
         URL var3 = new URL(var1);
         if (var3.getProtocol().equals("http") || var3.getProtocol().equals("https")) {
            HttpURLConnection var7 = (HttpURLConnection)var3.openConnection();
            var7.setUseCaches(false);
            var7.setDoInput(true);
            var7.connect();
            if (var7.getResponseCode() != 200) {
               throw new Exception(var7.getResponseMessage());
            }

            CertificateFactory var5 = CertificateFactory.getInstance("X.509", "BC");
            var2 = (X509CRL)var5.generateCRL(var7.getInputStream());
         }

         return var2;
      } catch (Exception var6) {
         ErrorBundle var4 = createErrorBundle(
            "CertPathReviewer.loadCrlDistPointError", new Object[]{new UntrustedInput(var1), var6.getMessage(), var6, var6.getClass().getName()}
         );
         throw new CertPathReviewerException(var4);
      }
   }

   protected Collection getTrustAnchors(X509Certificate var1, Set var2) throws CertPathReviewerException {
      ArrayList var3 = new ArrayList();
      Iterator var4 = var2.iterator();
      X509CertSelector var5 = new X509CertSelector();

      try {
         var5.setSubject(getEncodedIssuerPrincipal(var1).getEncoded());
         byte[] var6 = var1.getExtensionValue(Extension.authorityKeyIdentifier.getId());
         if (var6 != null) {
            AuthorityKeyIdentifier var12 = AuthorityKeyIdentifier.getInstance(JcaX509ExtensionUtils.parseExtensionValue(var6));
            BigInteger var8 = var12.getAuthorityCertSerialNumber();
            if (var8 != null) {
               var5.setSerialNumber(var12.getAuthorityCertSerialNumber());
            } else {
               ASN1OctetString var9 = var12.getKeyIdentifierObject();
               if (var9 != null) {
                  var5.setSubjectKeyIdentifier(var9.getEncoded("DER"));
               }
            }
         }
      } catch (IOException var10) {
         ErrorBundle var7 = createErrorBundle("CertPathReviewer.trustAnchorIssuerError");
         throw new CertPathReviewerException(var7);
      }

      while (var4.hasNext()) {
         TrustAnchor var11 = (TrustAnchor)var4.next();
         if (var11.getTrustedCert() != null) {
            if (var5.match(var11.getTrustedCert())) {
               var3.add(var11);
            }
         } else if (var11.getCAName() != null && var11.getCAPublicKey() != null) {
            X500Principal var13 = getEncodedIssuerPrincipal(var1);
            X500Principal var14 = new X500Principal(var11.getCAName());
            if (var13.equals(var14)) {
               var3.add(var11);
            }
         }
      }

      return var3;
   }

   private static ErrorBundle createErrorBundle(String var0) {
      ErrorBundle var1 = new ErrorBundle("org.bouncycastle.pkix.CertPathReviewerMessages", var0);
      var1.setClassLoader(PKIXCertPathReviewer.class.getClassLoader());
      return var1;
   }

   private static ErrorBundle createErrorBundle(String var0, Object[] var1) {
      ErrorBundle var2 = new ErrorBundle("org.bouncycastle.pkix.CertPathReviewerMessages", var0, var1);
      var2.setClassLoader(PKIXCertPathReviewer.class.getClassLoader());
      return var2;
   }
}
