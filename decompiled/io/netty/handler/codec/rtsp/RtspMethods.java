package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Map;

public final class RtspMethods {
   public static final HttpMethod OPTIONS = HttpMethod.OPTIONS;
   public static final HttpMethod DESCRIBE = HttpMethod.valueOf("DESCRIBE");
   public static final HttpMethod ANNOUNCE = HttpMethod.valueOf("ANNOUNCE");
   public static final HttpMethod SETUP = HttpMethod.valueOf("SETUP");
   public static final HttpMethod PLAY = HttpMethod.valueOf("PLAY");
   public static final HttpMethod PAUSE = HttpMethod.valueOf("PAUSE");
   public static final HttpMethod TEARDOWN = HttpMethod.valueOf("TEARDOWN");
   public static final HttpMethod GET_PARAMETER = HttpMethod.valueOf("GET_PARAMETER");
   public static final HttpMethod SET_PARAMETER = HttpMethod.valueOf("SET_PARAMETER");
   public static final HttpMethod REDIRECT = HttpMethod.valueOf("REDIRECT");
   public static final HttpMethod RECORD = HttpMethod.valueOf("RECORD");
   private static final Map<String, HttpMethod> methodMap = new HashMap<>();

   public static HttpMethod valueOf(String name) {
      name = ObjectUtil.checkNonEmptyAfterTrim(name, "name").toUpperCase();
      HttpMethod result = methodMap.get(name);
      return result != null ? result : HttpMethod.valueOf(name);
   }

   private RtspMethods() {
   }

   static {
      methodMap.put(DESCRIBE.toString(), DESCRIBE);
      methodMap.put(ANNOUNCE.toString(), ANNOUNCE);
      methodMap.put(GET_PARAMETER.toString(), GET_PARAMETER);
      methodMap.put(OPTIONS.toString(), OPTIONS);
      methodMap.put(PAUSE.toString(), PAUSE);
      methodMap.put(PLAY.toString(), PLAY);
      methodMap.put(RECORD.toString(), RECORD);
      methodMap.put(REDIRECT.toString(), REDIRECT);
      methodMap.put(SETUP.toString(), SETUP);
      methodMap.put(SET_PARAMETER.toString(), SET_PARAMETER);
      methodMap.put(TEARDOWN.toString(), TEARDOWN);
   }
}
