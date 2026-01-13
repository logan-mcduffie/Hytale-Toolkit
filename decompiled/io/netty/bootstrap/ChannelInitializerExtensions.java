package io.netty.bootstrap;

import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

abstract class ChannelInitializerExtensions {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializerExtensions.class);
   private static volatile ChannelInitializerExtensions implementation;

   private ChannelInitializerExtensions() {
   }

   static ChannelInitializerExtensions getExtensions() {
      ChannelInitializerExtensions impl = implementation;
      if (impl == null) {
         synchronized (ChannelInitializerExtensions.class) {
            impl = implementation;
            if (impl != null) {
               return impl;
            }

            String extensionProp = SystemPropertyUtil.get("io.netty.bootstrap.extensions");
            logger.debug("-Dio.netty.bootstrap.extensions: {}", extensionProp);
            if ("serviceload".equalsIgnoreCase(extensionProp)) {
               impl = new ChannelInitializerExtensions.ServiceLoadingExtensions(true);
            } else if ("log".equalsIgnoreCase(extensionProp)) {
               impl = new ChannelInitializerExtensions.ServiceLoadingExtensions(false);
            } else {
               impl = new ChannelInitializerExtensions.EmptyExtensions();
            }

            implementation = impl;
         }
      }

      return impl;
   }

   abstract Collection<ChannelInitializerExtension> extensions(ClassLoader var1);

   private static final class EmptyExtensions extends ChannelInitializerExtensions {
      private EmptyExtensions() {
      }

      @Override
      Collection<ChannelInitializerExtension> extensions(ClassLoader cl) {
         return Collections.emptyList();
      }
   }

   private static final class ServiceLoadingExtensions extends ChannelInitializerExtensions {
      private final boolean loadAndCache;
      private WeakReference<ClassLoader> classLoader;
      private Collection<ChannelInitializerExtension> extensions;

      ServiceLoadingExtensions(boolean loadAndCache) {
         this.loadAndCache = loadAndCache;
      }

      @Override
      synchronized Collection<ChannelInitializerExtension> extensions(ClassLoader cl) {
         ClassLoader configured = this.classLoader == null ? null : this.classLoader.get();
         if (configured == null || configured != cl) {
            Collection<ChannelInitializerExtension> loaded = serviceLoadExtensions(this.loadAndCache, cl);
            this.classLoader = new WeakReference<>(cl);
            this.extensions = (Collection<ChannelInitializerExtension>)(this.loadAndCache ? loaded : Collections.emptyList());
         }

         return this.extensions;
      }

      private static Collection<ChannelInitializerExtension> serviceLoadExtensions(boolean load, ClassLoader cl) {
         List<ChannelInitializerExtension> extensions = new ArrayList<>();

         for (ChannelInitializerExtension extension : ServiceLoader.load(ChannelInitializerExtension.class, cl)) {
            extensions.add(extension);
         }

         if (!extensions.isEmpty()) {
            Collections.sort(extensions, new Comparator<ChannelInitializerExtension>() {
               public int compare(ChannelInitializerExtension a, ChannelInitializerExtension b) {
                  return Double.compare(a.priority(), b.priority());
               }
            });
            ChannelInitializerExtensions.logger
               .info("ServiceLoader {}(s) {}: {}", ChannelInitializerExtension.class.getSimpleName(), load ? "registered" : "detected", extensions);
            return Collections.unmodifiableList(extensions);
         } else {
            ChannelInitializerExtensions.logger
               .debug("ServiceLoader {}(s) {}: []", ChannelInitializerExtension.class.getSimpleName(), load ? "registered" : "detected");
            return Collections.emptyList();
         }
      }
   }
}
