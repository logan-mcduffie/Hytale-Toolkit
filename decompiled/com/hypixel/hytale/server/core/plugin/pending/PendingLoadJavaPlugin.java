package com.hypixel.hytale.server.core.plugin.pending;

import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginClassLoader;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PendingLoadJavaPlugin extends PendingLoadPlugin {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   @Nonnull
   private final PluginClassLoader urlClassLoader;

   public PendingLoadJavaPlugin(@Nullable Path path, @Nonnull PluginManifest manifest, @Nonnull PluginClassLoader urlClassLoader) {
      super(path, manifest);
      this.urlClassLoader = urlClassLoader;
   }

   @Nonnull
   @Override
   public PendingLoadPlugin createSubPendingLoadPlugin(@Nonnull PluginManifest manifest) {
      return new PendingLoadJavaPlugin(this.getPath(), manifest, this.urlClassLoader);
   }

   @Override
   public boolean isInServerClassPath() {
      return this.urlClassLoader.isInServerClassPath();
   }

   @Nullable
   public JavaPlugin load() {
      try {
         PluginManifest manifest = this.getManifest();
         Class<?> mainClass = this.urlClassLoader.loadLocalClass(manifest.getMain());
         if (JavaPlugin.class.isAssignableFrom(mainClass)) {
            Constructor<?> constructor = mainClass.getConstructor(JavaPluginInit.class);
            Path dataDirectory = PluginManager.MODS_PATH.resolve(manifest.getGroup() + "_" + manifest.getName());
            JavaPluginInit init = new JavaPluginInit(manifest, dataDirectory, this.getPath(), this.urlClassLoader);
            return (JavaPlugin)constructor.newInstance(init);
         }

         throw new ClassCastException(manifest.getMain() + " does not extend JavaPlugin");
      } catch (ClassNotFoundException var6) {
         LOGGER.at(Level.SEVERE).withCause(var6).log("Failed to load plugin %s. Failed to find main class!", this.getPath());
      } catch (NoSuchMethodException var7) {
         LOGGER.at(Level.SEVERE).withCause(var7).log("Failed to load plugin %s. Requires default constructor!", this.getPath());
      } catch (Throwable var8) {
         LOGGER.at(Level.SEVERE).withCause(var8).log("Failed to load plugin %s", this.getPath());
      }

      return null;
   }

   @Nonnull
   @Override
   public String toString() {
      return "PendingLoadJavaPlugin{" + super.toString() + "}";
   }
}
