package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Store;

public class CMSSignedGenerator {
   public static final String DATA = CMSObjectIdentifiers.data.getId();
   public static final String DIGEST_SHA1 = OIWObjectIdentifiers.idSHA1.getId();
   public static final String DIGEST_SHA224 = NISTObjectIdentifiers.id_sha224.getId();
   public static final String DIGEST_SHA256 = NISTObjectIdentifiers.id_sha256.getId();
   public static final String DIGEST_SHA384 = NISTObjectIdentifiers.id_sha384.getId();
   public static final String DIGEST_SHA512 = NISTObjectIdentifiers.id_sha512.getId();
   public static final String DIGEST_MD5 = PKCSObjectIdentifiers.md5.getId();
   public static final String DIGEST_GOST3411 = CryptoProObjectIdentifiers.gostR3411.getId();
   public static final String DIGEST_RIPEMD128 = TeleTrusTObjectIdentifiers.ripemd128.getId();
   public static final String DIGEST_RIPEMD160 = TeleTrusTObjectIdentifiers.ripemd160.getId();
   public static final String DIGEST_RIPEMD256 = TeleTrusTObjectIdentifiers.ripemd256.getId();
   public static final String ENCRYPTION_RSA = PKCSObjectIdentifiers.rsaEncryption.getId();
   public static final String ENCRYPTION_DSA = X9ObjectIdentifiers.id_dsa_with_sha1.getId();
   public static final String ENCRYPTION_ECDSA = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
   public static final String ENCRYPTION_RSA_PSS = PKCSObjectIdentifiers.id_RSASSA_PSS.getId();
   public static final String ENCRYPTION_GOST3410 = CryptoProObjectIdentifiers.gostR3410_94.getId();
   public static final String ENCRYPTION_ECGOST3410 = CryptoProObjectIdentifiers.gostR3410_2001.getId();
   public static final String ENCRYPTION_ECGOST3410_2012_256 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256.getId();
   public static final String ENCRYPTION_ECGOST3410_2012_512 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512.getId();
   private static final String ENCRYPTION_ECDSA_WITH_SHA1 = ENCRYPTION_ECDSA;
   private static final String ENCRYPTION_ECDSA_WITH_SHA224 = X9ObjectIdentifiers.ecdsa_with_SHA224.getId();
   private static final String ENCRYPTION_ECDSA_WITH_SHA256 = X9ObjectIdentifiers.ecdsa_with_SHA256.getId();
   private static final String ENCRYPTION_ECDSA_WITH_SHA384 = X9ObjectIdentifiers.ecdsa_with_SHA384.getId();
   private static final String ENCRYPTION_ECDSA_WITH_SHA512 = X9ObjectIdentifiers.ecdsa_with_SHA512.getId();
   private static final Set NO_PARAMS = new HashSet();
   private static final Map EC_ALGORITHMS = new HashMap();
   protected List certs = new ArrayList();
   protected List crls = new ArrayList();
   protected List _signers = new ArrayList();
   protected List signerGens = new ArrayList();
   protected Map digests = new HashMap();
   protected DigestAlgorithmIdentifierFinder digestAlgIdFinder;

   protected CMSSignedGenerator() {
      this(new DefaultDigestAlgorithmIdentifierFinder());
   }

   protected CMSSignedGenerator(DigestAlgorithmIdentifierFinder var1) {
      this.digestAlgIdFinder = var1;
   }

   protected Map getBaseParameters(ASN1ObjectIdentifier var1, AlgorithmIdentifier var2, byte[] var3) {
      HashMap var4 = new HashMap();
      var4.put("contentType", var1);
      var4.put("digestAlgID", var2);
      var4.put("digest", Arrays.clone(var3));
      return var4;
   }

   public void addCertificate(X509CertificateHolder var1) throws CMSException {
      this.certs.add(var1.toASN1Structure());
   }

   public void addCertificates(Store var1) throws CMSException {
      this.certs.addAll(CMSUtils.getCertificatesFromStore(var1));
   }

   public void addCRL(X509CRLHolder var1) {
      this.crls.add(var1.toASN1Structure());
   }

   public void addCRLs(Store var1) throws CMSException {
      this.crls.addAll(CMSUtils.getCRLsFromStore(var1));
   }

   public void addAttributeCertificate(X509AttributeCertificateHolder var1) throws CMSException {
      this.certs.add(new DERTaggedObject(false, 2, var1.toASN1Structure()));
   }

   public void addAttributeCertificates(Store var1) throws CMSException {
      this.certs.addAll(CMSUtils.getAttributeCertificatesFromStore(var1));
   }

   public void addOtherRevocationInfo(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      OtherRevocationInfoFormat var3 = new OtherRevocationInfoFormat(var1, var2);
      CMSUtils.validateInfoFormat(var3);
      this.crls.add(new DERTaggedObject(false, 1, var3));
   }

   public void addOtherRevocationInfo(ASN1ObjectIdentifier var1, Store var2) {
      this.crls.addAll(CMSUtils.getOthersFromStore(var1, var2));
   }

   public void addSigners(SignerInformationStore var1) {
      Iterator var2 = var1.getSigners().iterator();

      while (var2.hasNext()) {
         this._signers.add(var2.next());
      }
   }

   public void addSignerInfoGenerator(SignerInfoGenerator var1) {
      this.signerGens.add(var1);
   }

   public Map getGeneratedDigests() {
      return new HashMap(this.digests);
   }

   static {
      NO_PARAMS.add(ENCRYPTION_DSA);
      NO_PARAMS.add(ENCRYPTION_ECDSA);
      NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA224);
      NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA256);
      NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA384);
      NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA512);
      EC_ALGORITHMS.put(DIGEST_SHA1, ENCRYPTION_ECDSA_WITH_SHA1);
      EC_ALGORITHMS.put(DIGEST_SHA224, ENCRYPTION_ECDSA_WITH_SHA224);
      EC_ALGORITHMS.put(DIGEST_SHA256, ENCRYPTION_ECDSA_WITH_SHA256);
      EC_ALGORITHMS.put(DIGEST_SHA384, ENCRYPTION_ECDSA_WITH_SHA384);
      EC_ALGORITHMS.put(DIGEST_SHA512, ENCRYPTION_ECDSA_WITH_SHA512);
   }
}
