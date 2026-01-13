package com.nimbusds.jose;

import java.util.Map;

public interface JSONSerializable {
   Map<String, Object> toGeneralJSONObject();

   Map<String, Object> toFlattenedJSONObject();
}
