package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.ObjectUtil;

public final class RtspVersions {
   public static final HttpVersion RTSP_1_0 = new HttpVersion("RTSP", 1, 0, true);

   public static HttpVersion valueOf(String text) {
      ObjectUtil.checkNotNull(text, "text");
      text = text.trim().toUpperCase();
      return "RTSP/1.0".equals(text) ? RTSP_1_0 : new HttpVersion(text, true);
   }

   private RtspVersions() {
   }
}
