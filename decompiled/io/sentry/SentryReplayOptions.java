package io.sentry;

import io.sentry.protocol.SdkVersion;
import io.sentry.util.SampleRateUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class SentryReplayOptions {
   public static final String TEXT_VIEW_CLASS_NAME = "android.widget.TextView";
   public static final String IMAGE_VIEW_CLASS_NAME = "android.widget.ImageView";
   public static final String WEB_VIEW_CLASS_NAME = "android.webkit.WebView";
   public static final String VIDEO_VIEW_CLASS_NAME = "android.widget.VideoView";
   public static final String ANDROIDX_MEDIA_VIEW_CLASS_NAME = "androidx.media3.ui.PlayerView";
   public static final String EXOPLAYER_CLASS_NAME = "com.google.android.exoplayer2.ui.PlayerView";
   public static final String EXOPLAYER_STYLED_CLASS_NAME = "com.google.android.exoplayer2.ui.StyledPlayerView";
   @Internal
   public static final int MAX_NETWORK_BODY_SIZE = 153600;
   @Nullable
   private Double sessionSampleRate;
   @Nullable
   private Double onErrorSampleRate;
   private Set<String> maskViewClasses = new CopyOnWriteArraySet<>();
   private Set<String> unmaskViewClasses = new CopyOnWriteArraySet<>();
   @Nullable
   private String maskViewContainerClass = null;
   @Nullable
   private String unmaskViewContainerClass = null;
   private SentryReplayOptions.SentryReplayQuality quality = SentryReplayOptions.SentryReplayQuality.MEDIUM;
   private int frameRate = 1;
   private long errorReplayDuration = 30000L;
   private long sessionSegmentDuration = 5000L;
   private long sessionDuration = 3600000L;
   private boolean trackConfiguration = true;
   @Nullable
   private SdkVersion sdkVersion;
   private boolean debug = false;
   @Experimental
   @NotNull
   private ScreenshotStrategyType screenshotStrategy = ScreenshotStrategyType.PIXEL_COPY;
   @NotNull
   private List<String> networkDetailAllowUrls = Collections.emptyList();
   @NotNull
   private List<String> networkDetailDenyUrls = Collections.emptyList();
   private boolean networkCaptureBodies = true;
   @NotNull
   private static final List<String> DEFAULT_HEADERS = Collections.unmodifiableList(Arrays.asList("Content-Type", "Content-Length", "Accept"));
   @NotNull
   private List<String> networkRequestHeaders = DEFAULT_HEADERS;
   @NotNull
   private List<String> networkResponseHeaders = DEFAULT_HEADERS;

   @Internal
   @NotNull
   public static List<String> getNetworkDetailsDefaultHeaders() {
      return DEFAULT_HEADERS;
   }

   public SentryReplayOptions(boolean empty, @Nullable SdkVersion sdkVersion) {
      if (!empty) {
         this.setMaskAllText(true);
         this.setMaskAllImages(true);
         this.maskViewClasses.add("android.webkit.WebView");
         this.maskViewClasses.add("android.widget.VideoView");
         this.maskViewClasses.add("androidx.media3.ui.PlayerView");
         this.maskViewClasses.add("com.google.android.exoplayer2.ui.PlayerView");
         this.maskViewClasses.add("com.google.android.exoplayer2.ui.StyledPlayerView");
         this.sdkVersion = sdkVersion;
      }
   }

   public SentryReplayOptions(@Nullable Double sessionSampleRate, @Nullable Double onErrorSampleRate, @Nullable SdkVersion sdkVersion) {
      this(false, sdkVersion);
      this.sessionSampleRate = sessionSampleRate;
      this.onErrorSampleRate = onErrorSampleRate;
      this.sdkVersion = sdkVersion;
   }

   @Nullable
   public Double getOnErrorSampleRate() {
      return this.onErrorSampleRate;
   }

   public boolean isSessionReplayEnabled() {
      return this.getSessionSampleRate() != null && this.getSessionSampleRate() > 0.0;
   }

   public void setOnErrorSampleRate(@Nullable Double onErrorSampleRate) {
      if (!SampleRateUtils.isValidSampleRate(onErrorSampleRate)) {
         throw new IllegalArgumentException("The value " + onErrorSampleRate + " is not valid. Use null to disable or values >= 0.0 and <= 1.0.");
      } else {
         this.onErrorSampleRate = onErrorSampleRate;
      }
   }

   @Nullable
   public Double getSessionSampleRate() {
      return this.sessionSampleRate;
   }

   public boolean isSessionReplayForErrorsEnabled() {
      return this.getOnErrorSampleRate() != null && this.getOnErrorSampleRate() > 0.0;
   }

   public void setSessionSampleRate(@Nullable Double sessionSampleRate) {
      if (!SampleRateUtils.isValidSampleRate(sessionSampleRate)) {
         throw new IllegalArgumentException("The value " + sessionSampleRate + " is not valid. Use null to disable or values >= 0.0 and <= 1.0.");
      } else {
         this.sessionSampleRate = sessionSampleRate;
      }
   }

   public void setMaskAllText(boolean maskAllText) {
      if (maskAllText) {
         this.addMaskViewClass("android.widget.TextView");
         this.unmaskViewClasses.remove("android.widget.TextView");
      } else {
         this.addUnmaskViewClass("android.widget.TextView");
         this.maskViewClasses.remove("android.widget.TextView");
      }
   }

   public void setMaskAllImages(boolean maskAllImages) {
      if (maskAllImages) {
         this.addMaskViewClass("android.widget.ImageView");
         this.unmaskViewClasses.remove("android.widget.ImageView");
      } else {
         this.addUnmaskViewClass("android.widget.ImageView");
         this.maskViewClasses.remove("android.widget.ImageView");
      }
   }

   @NotNull
   public Set<String> getMaskViewClasses() {
      return this.maskViewClasses;
   }

   public void addMaskViewClass(@NotNull String className) {
      this.maskViewClasses.add(className);
   }

   @NotNull
   public Set<String> getUnmaskViewClasses() {
      return this.unmaskViewClasses;
   }

   public void addUnmaskViewClass(@NotNull String className) {
      this.unmaskViewClasses.add(className);
   }

   @Internal
   @NotNull
   public SentryReplayOptions.SentryReplayQuality getQuality() {
      return this.quality;
   }

   public void setQuality(@NotNull SentryReplayOptions.SentryReplayQuality quality) {
      this.quality = quality;
   }

   @Internal
   public int getFrameRate() {
      return this.frameRate;
   }

   @Internal
   public long getErrorReplayDuration() {
      return this.errorReplayDuration;
   }

   @Internal
   public long getSessionSegmentDuration() {
      return this.sessionSegmentDuration;
   }

   @Internal
   public long getSessionDuration() {
      return this.sessionDuration;
   }

   @Internal
   public void setMaskViewContainerClass(@NotNull String containerClass) {
      this.addMaskViewClass(containerClass);
      this.maskViewContainerClass = containerClass;
   }

   @Internal
   public void setUnmaskViewContainerClass(@NotNull String containerClass) {
      this.unmaskViewContainerClass = containerClass;
   }

   @Internal
   @Nullable
   public String getMaskViewContainerClass() {
      return this.maskViewContainerClass;
   }

   @Internal
   @Nullable
   public String getUnmaskViewContainerClass() {
      return this.unmaskViewContainerClass;
   }

   @Internal
   public boolean isTrackConfiguration() {
      return this.trackConfiguration;
   }

   @Internal
   public void setTrackConfiguration(boolean trackConfiguration) {
      this.trackConfiguration = trackConfiguration;
   }

   @Internal
   @Nullable
   public SdkVersion getSdkVersion() {
      return this.sdkVersion;
   }

   @Internal
   public void setSdkVersion(@Nullable SdkVersion sdkVersion) {
      this.sdkVersion = sdkVersion;
   }

   public boolean isDebug() {
      return this.debug;
   }

   public void setDebug(boolean debug) {
      this.debug = debug;
   }

   @Experimental
   @NotNull
   public ScreenshotStrategyType getScreenshotStrategy() {
      return this.screenshotStrategy;
   }

   @Experimental
   public void setScreenshotStrategy(@NotNull ScreenshotStrategyType screenshotStrategy) {
      this.screenshotStrategy = screenshotStrategy;
   }

   @NotNull
   public List<String> getNetworkDetailAllowUrls() {
      return this.networkDetailAllowUrls;
   }

   public void setNetworkDetailAllowUrls(@NotNull List<String> networkDetailAllowUrls) {
      this.networkDetailAllowUrls = Collections.unmodifiableList(new ArrayList<>(networkDetailAllowUrls));
   }

   @NotNull
   public List<String> getNetworkDetailDenyUrls() {
      return this.networkDetailDenyUrls;
   }

   public void setNetworkDetailDenyUrls(@NotNull List<String> networkDetailDenyUrls) {
      this.networkDetailDenyUrls = Collections.unmodifiableList(new ArrayList<>(networkDetailDenyUrls));
   }

   public boolean isNetworkCaptureBodies() {
      return this.networkCaptureBodies;
   }

   public void setNetworkCaptureBodies(boolean networkCaptureBodies) {
      this.networkCaptureBodies = networkCaptureBodies;
   }

   @NotNull
   public List<String> getNetworkRequestHeaders() {
      return this.networkRequestHeaders;
   }

   public void setNetworkRequestHeaders(@NotNull List<String> networkRequestHeaders) {
      this.networkRequestHeaders = mergeHeaders(DEFAULT_HEADERS, networkRequestHeaders);
   }

   @NotNull
   public List<String> getNetworkResponseHeaders() {
      return this.networkResponseHeaders;
   }

   public void setNetworkResponseHeaders(@NotNull List<String> networkResponseHeaders) {
      this.networkResponseHeaders = mergeHeaders(DEFAULT_HEADERS, networkResponseHeaders);
   }

   @NotNull
   private static List<String> mergeHeaders(@NotNull List<String> defaultHeaders, @NotNull List<String> additionalHeaders) {
      Set<String> merged = new LinkedHashSet<>();
      merged.addAll(defaultHeaders);
      merged.addAll(additionalHeaders);
      return Collections.unmodifiableList(new ArrayList<>(merged));
   }

   public static enum SentryReplayQuality {
      LOW(0.8F, 50000, 10),
      MEDIUM(1.0F, 75000, 30),
      HIGH(1.0F, 100000, 50);

      public final float sizeScale;
      public final int bitRate;
      public final int screenshotQuality;

      private SentryReplayQuality(float sizeScale, int bitRate, int screenshotQuality) {
         this.sizeScale = sizeScale;
         this.bitRate = bitRate;
         this.screenshotQuality = screenshotQuality;
      }

      @NotNull
      public String serializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
