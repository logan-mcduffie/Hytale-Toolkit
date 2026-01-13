package org.bson;

import java.io.IOException;
import java.io.InputStream;

public interface BSONDecoder {
   BSONObject readObject(byte[] var1);

   BSONObject readObject(InputStream var1) throws IOException;

   int decode(byte[] var1, BSONCallback var2);

   int decode(InputStream var1, BSONCallback var2) throws IOException;
}
