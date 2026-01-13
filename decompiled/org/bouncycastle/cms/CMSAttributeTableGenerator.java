package org.bouncycastle.cms;

import java.util.Map;
import org.bouncycastle.asn1.cms.AttributeTable;

public interface CMSAttributeTableGenerator {
   String CONTENT_TYPE = "contentType";
   String DIGEST = "digest";
   String SIGNATURE = "encryptedDigest";
   String DIGEST_ALGORITHM_IDENTIFIER = "digestAlgID";
   String MAC_ALGORITHM_IDENTIFIER = "macAlgID";
   String SIGNATURE_ALGORITHM_IDENTIFIER = "signatureAlgID";

   AttributeTable getAttributes(Map var1) throws CMSAttributeTableGenerationException;
}
