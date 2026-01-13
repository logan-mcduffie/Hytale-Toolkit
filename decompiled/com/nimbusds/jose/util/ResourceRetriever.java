package com.nimbusds.jose.util;

import java.io.IOException;
import java.net.URL;

public interface ResourceRetriever {
   Resource retrieveResource(URL var1) throws IOException;
}
