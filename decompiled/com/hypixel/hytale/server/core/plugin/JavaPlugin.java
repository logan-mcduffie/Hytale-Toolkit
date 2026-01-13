package com.hypixel.hytale.server.core.plugin;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.asset.AssetModule;
import java.nio.file.Path;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public abstract class JavaPlugin extends PluginBase {
   @Nonnull
   private final Path file;
   @Nonnull
   private final PluginClassLoader classLoader;

   public JavaPlugin(@Nonnull JavaPluginInit init) {
      super(init);
      this.file = init.getFile();
      this.classLoader = init.getClassLoader();
      this.classLoader.setPlugin(this);
   }

   @Nonnull
   public Path getFile() {
      return this.file;
   }

   @Override
   protected void start0() {
      super.start0();
      if (this.getManifest().includesAssetPack()) {
         AssetModule assetModule = AssetModule.get();
         String id = new PluginIdentifier(this.getManifest()).toString();
         AssetPack existing = assetModule.getAssetPack(id);
         if (existing != null) {
            this.getLogger().at(Level.WARNING).log("Asset pack %s already exists, skipping embedded pack", id);
            return;
         }

         assetModule.registerPack(id, this.file, this.getManifest());
      }
   }

   @Nonnull
   public PluginClassLoader getClassLoader() {
      return this.classLoader;
   }

   @Nonnull
   @Override
   public final PluginType getType() {
      return PluginType.PLUGIN;
   }
}
