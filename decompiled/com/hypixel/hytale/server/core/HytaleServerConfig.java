package com.hypixel.hytale.server.core;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.DocumentContainingCodec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.codecs.map.ObjectMapCodec;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.auth.AuthCredentialStoreProvider;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.universe.playerdata.DefaultPlayerStorageProvider;
import com.hypixel.hytale.server.core.universe.playerdata.DiskPlayerStorageProvider;
import com.hypixel.hytale.server.core.universe.playerdata.PlayerStorageProvider;
import com.hypixel.hytale.server.core.util.BsonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class HytaleServerConfig {
   public static final int VERSION = 3;
   public static final int DEFAULT_MAX_VIEW_RADIUS = 32;
   @Nonnull
   public static final Path PATH = Path.of("config.json");
   @Nonnull
   public static final BuilderCodec<HytaleServerConfig> CODEC = BuilderCodec.builder(HytaleServerConfig.class, HytaleServerConfig::new)
      .versioned()
      .codecVersion(3)
      .append(new KeyedCodec<>("ServerName", Codec.STRING), (o, s) -> o.serverName = s, o -> o.serverName)
      .add()
      .append(new KeyedCodec<>("MOTD", Codec.STRING), (o, s) -> o.motd = s, o -> o.motd)
      .add()
      .append(new KeyedCodec<>("Password", Codec.STRING), (o, s) -> o.password = s, o -> o.password)
      .add()
      .append(new KeyedCodec<>("MaxPlayers", Codec.INTEGER), (o, i) -> o.maxPlayers = i, o -> o.maxPlayers)
      .add()
      .append(new KeyedCodec<>("MaxViewRadius", Codec.INTEGER), (o, i) -> o.maxViewRadius = i, o -> o.maxViewRadius)
      .add()
      .append(new KeyedCodec<>("LocalCompressionEnabled", Codec.BOOLEAN), (o, i) -> o.localCompressionEnabled = i, o -> o.localCompressionEnabled)
      .add()
      .append(new KeyedCodec<>("Defaults", HytaleServerConfig.Defaults.CODEC), (o, obj) -> o.defaults = obj, o -> o.defaults)
      .add()
      .append(
         new KeyedCodec<>("ConnectionTimeouts", HytaleServerConfig.ConnectionTimeouts.CODEC), (o, m) -> o.connectionTimeouts = m, o -> o.connectionTimeouts
      )
      .add()
      .append(new KeyedCodec<>("RateLimit", HytaleServerConfig.RateLimitConfig.CODEC), (o, m) -> o.rateLimitConfig = m, o -> o.rateLimitConfig)
      .add()
      .append(new KeyedCodec<>("Modules", new MapCodec<>(HytaleServerConfig.Module.CODEC, ConcurrentHashMap::new, false)), (o, m) -> {
         o.modules = m;
         o.unmodifiableModules = Collections.unmodifiableMap(m);
      }, o -> o.modules)
      .add()
      .append(new KeyedCodec<>("LogLevels", new MapCodec<>(Codec.LOG_LEVEL, ConcurrentHashMap::new, false)), (o, m) -> {
         o.logLevels = m;
         o.unmodifiableLogLevels = Collections.unmodifiableMap(o.logLevels);
      }, o -> o.logLevels)
      .add()
      .<Map>append(
         new KeyedCodec<>(
            "Plugins",
            new ObjectMapCodec<>(
               HytaleServerConfig.ModConfig.CODEC, Object2ObjectOpenHashMap::new, PluginIdentifier::toString, PluginIdentifier::fromString, false
            )
         ),
         (o, i) -> o.legacyPluginConfig = i,
         o -> null
      )
      .setVersionRange(0, 2)
      .add()
      .append(
         new KeyedCodec<>(
            "Mods",
            new ObjectMapCodec<>(HytaleServerConfig.ModConfig.CODEC, ConcurrentHashMap::new, PluginIdentifier::toString, PluginIdentifier::fromString, false)
         ),
         (o, i) -> o.modConfig = i,
         o -> o.modConfig
      )
      .add()
      .append(
         new KeyedCodec<>("DisplayTmpTagsInStrings", Codec.BOOLEAN),
         (o, displayTmpTagsInStrings) -> o.displayTmpTagsInStrings = displayTmpTagsInStrings,
         o -> o.displayTmpTagsInStrings
      )
      .add()
      .append(new KeyedCodec<>("PlayerStorage", PlayerStorageProvider.CODEC), (o, obj) -> o.playerStorageProvider = obj, o -> o.playerStorageProvider)
      .add()
      .append(new KeyedCodec<>("AuthCredentialStore", Codec.BSON_DOCUMENT), (o, value) -> o.authCredentialStoreConfig = value, o -> o.authCredentialStoreConfig)
      .add()
      .afterDecode(config -> {
         config.defaults.hytaleServerConfig = config;
         config.connectionTimeouts.hytaleServerConfig = config;
         config.rateLimitConfig.hytaleServerConfig = config;
         config.modules.values().forEach(m -> m.setHytaleServerConfig(config));
         if (config.legacyPluginConfig != null && !config.legacyPluginConfig.isEmpty()) {
            for (Entry<PluginIdentifier, HytaleServerConfig.ModConfig> entry : config.legacyPluginConfig.entrySet()) {
               config.modConfig.putIfAbsent(entry.getKey(), entry.getValue());
            }

            config.legacyPluginConfig = null;
            config.markChanged();
         }
      })
      .build();
   @Nonnull
   private final transient AtomicBoolean hasChanged = new AtomicBoolean();
   private String serverName = "Hytale Server";
   private String motd = "";
   private String password = "";
   private int maxPlayers = 100;
   private int maxViewRadius = 32;
   private boolean localCompressionEnabled;
   @Nonnull
   private HytaleServerConfig.Defaults defaults = new HytaleServerConfig.Defaults(this);
   @Nonnull
   private HytaleServerConfig.ConnectionTimeouts connectionTimeouts = new HytaleServerConfig.ConnectionTimeouts(this);
   @Nonnull
   private HytaleServerConfig.RateLimitConfig rateLimitConfig = new HytaleServerConfig.RateLimitConfig(this);
   @Nonnull
   private Map<String, HytaleServerConfig.Module> modules = new ConcurrentHashMap<>();
   @Nonnull
   private Map<String, Level> logLevels = Collections.emptyMap();
   @Nullable
   private transient Map<PluginIdentifier, HytaleServerConfig.ModConfig> legacyPluginConfig;
   @Nonnull
   private Map<PluginIdentifier, HytaleServerConfig.ModConfig> modConfig = new ConcurrentHashMap<>();
   @Nonnull
   private Map<String, HytaleServerConfig.Module> unmodifiableModules = Collections.unmodifiableMap(this.modules);
   @Nonnull
   private Map<String, Level> unmodifiableLogLevels = Collections.unmodifiableMap(this.logLevels);
   @Nonnull
   private PlayerStorageProvider playerStorageProvider = PlayerStorageProvider.CODEC.getDefault();
   @Nullable
   private BsonDocument authCredentialStoreConfig = null;
   @Nullable
   private transient AuthCredentialStoreProvider authCredentialStoreProvider = null;
   private boolean displayTmpTagsInStrings;

   public String getServerName() {
      return this.serverName;
   }

   public void setServerName(@Nonnull String serverName) {
      this.serverName = serverName;
      this.markChanged();
   }

   public String getMotd() {
      return this.motd;
   }

   public void setMotd(@Nonnull String motd) {
      this.motd = motd;
      this.markChanged();
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(@Nonnull String password) {
      this.password = password;
      this.markChanged();
   }

   public boolean isDisplayTmpTagsInStrings() {
      return this.displayTmpTagsInStrings;
   }

   public void setDisplayTmpTagsInStrings(boolean displayTmpTagsInStrings) {
      this.displayTmpTagsInStrings = displayTmpTagsInStrings;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public void setMaxPlayers(int maxPlayers) {
      this.maxPlayers = maxPlayers;
      this.markChanged();
   }

   public int getMaxViewRadius() {
      return this.maxViewRadius;
   }

   public void setMaxViewRadius(int maxViewRadius) {
      this.maxViewRadius = maxViewRadius;
      this.markChanged();
   }

   public boolean isLocalCompressionEnabled() {
      return this.localCompressionEnabled;
   }

   public void setLocalCompressionEnabled(boolean localCompression) {
      this.localCompressionEnabled = localCompression;
      this.markChanged();
   }

   @Nonnull
   public HytaleServerConfig.Defaults getDefaults() {
      return this.defaults;
   }

   public void setDefaults(@Nonnull HytaleServerConfig.Defaults defaults) {
      this.defaults = defaults;
      this.markChanged();
   }

   @Nonnull
   public HytaleServerConfig.ConnectionTimeouts getConnectionTimeouts() {
      return this.connectionTimeouts;
   }

   public void setConnectionTimeouts(@Nonnull HytaleServerConfig.ConnectionTimeouts connectionTimeouts) {
      this.connectionTimeouts = connectionTimeouts;
      this.markChanged();
   }

   @Nonnull
   public HytaleServerConfig.RateLimitConfig getRateLimitConfig() {
      return this.rateLimitConfig;
   }

   public void setRateLimitConfig(@Nonnull HytaleServerConfig.RateLimitConfig rateLimitConfig) {
      this.rateLimitConfig = rateLimitConfig;
      this.markChanged();
   }

   @Nonnull
   public Map<String, HytaleServerConfig.Module> getModules() {
      return this.unmodifiableModules;
   }

   @Nonnull
   public HytaleServerConfig.Module getModule(String moduleName) {
      return this.modules.computeIfAbsent(moduleName, k -> new HytaleServerConfig.Module(this));
   }

   public void setModules(@Nonnull Map<String, HytaleServerConfig.Module> modules) {
      this.modules = modules;
      this.markChanged();
   }

   @Nonnull
   public Map<String, Level> getLogLevels() {
      return this.unmodifiableLogLevels;
   }

   public void setLogLevels(@Nonnull Map<String, Level> logLevels) {
      this.logLevels = logLevels;
      this.markChanged();
   }

   @Nonnull
   public Map<PluginIdentifier, HytaleServerConfig.ModConfig> getModConfig() {
      return this.modConfig;
   }

   public void setModConfig(@Nonnull Map<PluginIdentifier, HytaleServerConfig.ModConfig> modConfig) {
      this.modConfig = modConfig;
      this.markChanged();
   }

   @Nonnull
   public PlayerStorageProvider getPlayerStorageProvider() {
      return this.playerStorageProvider;
   }

   public void setPlayerStorageProvider(@Nonnull PlayerStorageProvider playerStorageProvider) {
      this.playerStorageProvider = playerStorageProvider;
      this.markChanged();
   }

   @Nonnull
   public AuthCredentialStoreProvider getAuthCredentialStoreProvider() {
      if (this.authCredentialStoreProvider != null) {
         return this.authCredentialStoreProvider;
      } else {
         if (this.authCredentialStoreConfig != null) {
            this.authCredentialStoreProvider = AuthCredentialStoreProvider.CODEC.decode(this.authCredentialStoreConfig);
         } else {
            this.authCredentialStoreProvider = AuthCredentialStoreProvider.CODEC.getDefault();
         }

         return this.authCredentialStoreProvider;
      }
   }

   public void setAuthCredentialStoreProvider(@Nonnull AuthCredentialStoreProvider provider) {
      this.authCredentialStoreProvider = provider;
      this.authCredentialStoreConfig = (BsonDocument)AuthCredentialStoreProvider.CODEC.encode(provider);
      this.markChanged();
   }

   public void removeModule(@Nonnull String module) {
      this.modules.remove(module);
      this.markChanged();
   }

   public void markChanged() {
      this.hasChanged.set(true);
   }

   public boolean consumeHasChanged() {
      return this.hasChanged.getAndSet(false);
   }

   @Nonnull
   public static HytaleServerConfig load() {
      return load(PATH);
   }

   @Nonnull
   public static HytaleServerConfig load(@Nonnull Path path) {
      if (!Files.isRegularFile(path)) {
         HytaleServerConfig hytaleServerConfig = new HytaleServerConfig();
         if (!Options.getOptionSet().has(Options.BARE)) {
            save(hytaleServerConfig).join();
         }

         return hytaleServerConfig;
      } else {
         try {
            HytaleServerConfig config = RawJsonReader.readSyncWithBak(path, CODEC, HytaleLogger.getLogger());
            if (config == null) {
               throw new RuntimeException("Failed to load server config from " + path);
            } else {
               return config;
            }
         } catch (Exception var2) {
            throw new RuntimeException("Failed to read server config!", var2);
         }
      }
   }

   @Nonnull
   public static CompletableFuture<Void> save(@Nonnull HytaleServerConfig hytaleServerConfig) {
      return save(PATH, hytaleServerConfig);
   }

   @Nonnull
   public static CompletableFuture<Void> save(@Nonnull Path path, @Nonnull HytaleServerConfig hytaleServerConfig) {
      BsonDocument document = CODEC.encode(hytaleServerConfig, ExtraInfo.THREAD_LOCAL.get()).asDocument();
      return BsonUtil.writeDocument(path, document);
   }

   static {
      PlayerStorageProvider.CODEC.register(Priority.DEFAULT, "Hytale", DefaultPlayerStorageProvider.class, DefaultPlayerStorageProvider.CODEC);
      PlayerStorageProvider.CODEC.register("Disk", DiskPlayerStorageProvider.class, DiskPlayerStorageProvider.CODEC);
      HytaleServerConfig.Module.BUILDER_CODEC_BUILDER
         .addField(
            new KeyedCodec<>("Modules", new MapCodec<>(HytaleServerConfig.Module.CODEC, ConcurrentHashMap::new, false)),
            (o, m) -> o.modules = m,
            o -> o.modules
         );
   }

   public static class ConnectionTimeouts {
      public static final Duration DEFAULT_INITIAL_TIMEOUT = Duration.of(10L, ChronoUnit.SECONDS);
      public static final Duration DEFAULT_AUTH_TIMEOUT = Duration.of(30L, ChronoUnit.SECONDS);
      public static final Duration DEFAULT_PLAY_TIMEOUT = Duration.of(1L, ChronoUnit.MINUTES);
      public static final Codec<HytaleServerConfig.ConnectionTimeouts> CODEC = BuilderCodec.builder(
            HytaleServerConfig.ConnectionTimeouts.class, HytaleServerConfig.ConnectionTimeouts::new
         )
         .addField(new KeyedCodec<>("InitialTimeout", Codec.DURATION), (o, d) -> o.initialTimeout = d, o -> o.initialTimeout)
         .addField(new KeyedCodec<>("AuthTimeout", Codec.DURATION), (o, d) -> o.authTimeout = d, o -> o.authTimeout)
         .addField(new KeyedCodec<>("PlayTimeout", Codec.DURATION), (o, d) -> o.playTimeout = d, o -> o.playTimeout)
         .addField(
            new KeyedCodec<>("JoinTimeouts", new MapCodec<>(Codec.DURATION, ConcurrentHashMap::new, false)), (o, m) -> o.joinTimeouts = m, o -> o.joinTimeouts
         )
         .build();
      private Duration initialTimeout;
      private Duration authTimeout;
      private Duration playTimeout;
      private Map<String, Duration> joinTimeouts = new ConcurrentHashMap<>();
      @Nonnull
      private Map<String, Duration> unmodifiableJoinTimeouts = Collections.unmodifiableMap(this.joinTimeouts);
      private transient HytaleServerConfig hytaleServerConfig;

      public ConnectionTimeouts() {
      }

      public ConnectionTimeouts(HytaleServerConfig hytaleServerConfig) {
         this.hytaleServerConfig = hytaleServerConfig;
      }

      public Duration getInitialTimeout() {
         return this.initialTimeout != null ? this.initialTimeout : DEFAULT_INITIAL_TIMEOUT;
      }

      public void setInitialTimeout(Duration initialTimeout) {
         this.initialTimeout = initialTimeout;
         this.hytaleServerConfig.markChanged();
      }

      public Duration getAuthTimeout() {
         return this.authTimeout != null ? this.authTimeout : DEFAULT_AUTH_TIMEOUT;
      }

      public void setAuthTimeout(Duration authTimeout) {
         this.authTimeout = authTimeout;
         this.hytaleServerConfig.markChanged();
      }

      public Duration getPlayTimeout() {
         return this.playTimeout != null ? this.playTimeout : DEFAULT_PLAY_TIMEOUT;
      }

      public void setPlayTimeout(Duration playTimeout) {
         this.playTimeout = playTimeout;
         this.hytaleServerConfig.markChanged();
      }

      @Nonnull
      public Map<String, Duration> getJoinTimeouts() {
         return this.unmodifiableJoinTimeouts;
      }

      public void setJoinTimeouts(Map<String, Duration> joinTimeouts) {
         this.joinTimeouts = joinTimeouts;
         this.hytaleServerConfig.markChanged();
      }
   }

   public static class Defaults {
      public static final KeyedCodec<String> WORLD = new KeyedCodec<>("World", Codec.STRING);
      public static final KeyedCodec<GameMode> GAMEMODE = new KeyedCodec<>("GameMode", ProtocolCodecs.GAMEMODE_LEGACY);
      public static final BuilderCodec<HytaleServerConfig.Defaults> CODEC = BuilderCodec.builder(
            HytaleServerConfig.Defaults.class, HytaleServerConfig.Defaults::new
         )
         .addField(WORLD, (o, i) -> o.world = i, o -> o.world)
         .addField(GAMEMODE, (o, s) -> o.gameMode = s, o -> o.gameMode)
         .build();
      private transient HytaleServerConfig hytaleServerConfig;
      private String world = "default";
      private GameMode gameMode = GameMode.Adventure;

      private Defaults() {
      }

      private Defaults(HytaleServerConfig hytaleServerConfig) {
         this.hytaleServerConfig = hytaleServerConfig;
      }

      public String getWorld() {
         return this.world;
      }

      public void setWorld(String world) {
         this.world = world;
         this.hytaleServerConfig.markChanged();
      }

      public GameMode getGameMode() {
         return this.gameMode;
      }

      public void setGameMode(GameMode gameMode) {
         this.gameMode = gameMode;
         this.hytaleServerConfig.markChanged();
      }
   }

   public static class ModConfig {
      public static final BuilderCodec<HytaleServerConfig.ModConfig> CODEC = BuilderCodec.builder(
            HytaleServerConfig.ModConfig.class, HytaleServerConfig.ModConfig::new
         )
         .append(new KeyedCodec<>("Enabled", Codec.BOOLEAN), (modConfig, enabled) -> modConfig.enabled = enabled, modConfig -> modConfig.enabled)
         .add()
         .append(
            new KeyedCodec<>("RequiredVersion", SemverRange.CODEC),
            (modConfig, semverRange) -> modConfig.requiredVersion = semverRange,
            modConfig -> modConfig.requiredVersion
         )
         .add()
         .build();
      @Nullable
      private Boolean enabled;
      @Nullable
      private SemverRange requiredVersion;

      @Nullable
      public Boolean getEnabled() {
         return this.enabled;
      }

      public void setEnabled(Boolean enabled) {
         this.enabled = enabled;
      }

      @Nullable
      public SemverRange getRequiredVersion() {
         return this.requiredVersion;
      }

      public void setRequiredVersion(SemverRange requiredVersion) {
         this.requiredVersion = requiredVersion;
      }

      public static void setBoot(HytaleServerConfig serverConfig, PluginIdentifier identifier, boolean enabled) {
         serverConfig.getModConfig().computeIfAbsent(identifier, id -> new HytaleServerConfig.ModConfig()).enabled = enabled;
      }
   }

   public static class Module {
      @Nonnull
      protected static BuilderCodec.Builder<HytaleServerConfig.Module> BUILDER_CODEC_BUILDER = BuilderCodec.builder(
            HytaleServerConfig.Module.class, HytaleServerConfig.Module::new
         )
         .addField(new KeyedCodec<>("Enabled", Codec.BOOLEAN), (o, i) -> o.enabled = i, o -> o.enabled);
      @Nonnull
      protected static BuilderCodec<HytaleServerConfig.Module> BUILDER_CODEC = BUILDER_CODEC_BUILDER.build();
      @Nonnull
      public static final DocumentContainingCodec<HytaleServerConfig.Module> CODEC = new DocumentContainingCodec<>(
         BUILDER_CODEC, (o, i) -> o.document = i, o -> o.document
      );
      private transient HytaleServerConfig hytaleServerConfig;
      private Boolean enabled;
      @Nonnull
      private Map<String, HytaleServerConfig.Module> modules = new ConcurrentHashMap<>();
      @Nonnull
      private BsonDocument document = new BsonDocument();

      private Module() {
      }

      private Module(@Nonnull HytaleServerConfig hytaleServerConfig) {
         this.hytaleServerConfig = hytaleServerConfig;
      }

      public boolean isEnabled(boolean def) {
         return this.enabled != null ? this.enabled : def;
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
         this.hytaleServerConfig.markChanged();
      }

      public Boolean getEnabled() {
         return this.enabled;
      }

      @Nonnull
      public Map<String, HytaleServerConfig.Module> getModules() {
         return Collections.unmodifiableMap(this.modules);
      }

      @Nonnull
      public HytaleServerConfig.Module getModule(@Nonnull String moduleName) {
         return this.modules.computeIfAbsent(moduleName, k -> new HytaleServerConfig.Module(this.hytaleServerConfig));
      }

      public void setModules(@Nonnull Map<String, HytaleServerConfig.Module> modules) {
         this.modules = modules;
         this.hytaleServerConfig.markChanged();
      }

      @Nonnull
      public BsonDocument getDocument() {
         return this.document;
      }

      @Nullable
      public <T> T decode(@Nonnull Codec<T> codec) {
         return codec.decode(this.document);
      }

      public <T> void encode(@Nonnull Codec<T> codec, @Nonnull T t) {
         this.document = codec.encode(t).asDocument();
      }

      @Nonnull
      public <T> Optional<T> getData(@Nonnull KeyedCodec<T> keyedCodec) {
         return keyedCodec.get(this.document);
      }

      @Nullable
      public <T> T getDataOrNull(@Nonnull KeyedCodec<T> keyedCodec) {
         return keyedCodec.getOrNull(this.document);
      }

      public <T> T getDataNow(@Nonnull KeyedCodec<T> keyedCodec) {
         return keyedCodec.getNow(this.document);
      }

      public <T> void put(@Nonnull KeyedCodec<T> keyedCodec, T t) {
         keyedCodec.put(this.document, t);
         this.hytaleServerConfig.markChanged();
      }

      public void setDocument(@Nonnull BsonDocument document) {
         this.document = document;
         this.hytaleServerConfig.markChanged();
      }

      void setHytaleServerConfig(@Nonnull HytaleServerConfig hytaleServerConfig) {
         this.hytaleServerConfig = hytaleServerConfig;
         this.modules.values().forEach(module -> module.setHytaleServerConfig(hytaleServerConfig));
      }
   }

   public static class RateLimitConfig {
      public static final int DEFAULT_PACKETS_PER_SECOND = 2000;
      public static final int DEFAULT_BURST_CAPACITY = 500;
      public static final Codec<HytaleServerConfig.RateLimitConfig> CODEC = BuilderCodec.builder(
            HytaleServerConfig.RateLimitConfig.class, HytaleServerConfig.RateLimitConfig::new
         )
         .addField(new KeyedCodec<>("Enabled", Codec.BOOLEAN), (o, b) -> o.enabled = b, o -> o.enabled)
         .addField(new KeyedCodec<>("PacketsPerSecond", Codec.INTEGER), (o, i) -> o.packetsPerSecond = i, o -> o.packetsPerSecond)
         .addField(new KeyedCodec<>("BurstCapacity", Codec.INTEGER), (o, i) -> o.burstCapacity = i, o -> o.burstCapacity)
         .build();
      private Boolean enabled;
      private Integer packetsPerSecond;
      private Integer burstCapacity;
      transient HytaleServerConfig hytaleServerConfig;

      public RateLimitConfig() {
      }

      public RateLimitConfig(HytaleServerConfig hytaleServerConfig) {
         this.hytaleServerConfig = hytaleServerConfig;
      }

      public boolean isEnabled() {
         return this.enabled != null ? this.enabled : true;
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
         if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
         }
      }

      public int getPacketsPerSecond() {
         return this.packetsPerSecond != null ? this.packetsPerSecond : 2000;
      }

      public void setPacketsPerSecond(int packetsPerSecond) {
         this.packetsPerSecond = packetsPerSecond;
         if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
         }
      }

      public int getBurstCapacity() {
         return this.burstCapacity != null ? this.burstCapacity : 500;
      }

      public void setBurstCapacity(int burstCapacity) {
         this.burstCapacity = burstCapacity;
         if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
         }
      }
   }
}
