package com.hypixel.hytale.builtin.instances.command;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class InstanceEditNewCommand extends AbstractAsyncCommand {
   private final RequiredArg<String> instanceNameArg = this.withRequiredArg("instanceName", "server.commands.instances.edit.arg.name", ArgTypes.STRING);
   private final OptionalArg<String> packName = this.withOptionalArg("pack", "server.commands.instances.edit.arg.packName", ArgTypes.STRING);

   public InstanceEditNewCommand() {
      super("new", "server.commands.instances.edit.new.desc");
   }

   @Nonnull
   @Override
   public CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
      if (AssetModule.get().getBaseAssetPack().isImmutable()) {
         context.sendMessage(Message.translation("server.commands.instances.edit.assetsImmutable"));
         return CompletableFuture.completedFuture(null);
      } else {
         String packId = this.packName.get(context);
         AssetPack pack;
         if (packId != null) {
            pack = AssetModule.get().getAssetPack(packId);
            if (pack == null) {
               throw new IllegalArgumentException("Unknown asset pack: " + packId);
            }
         } else {
            pack = AssetModule.get().getBaseAssetPack();
         }

         String name = this.instanceNameArg.get(context);
         Path path = pack.getRoot().resolve("Server").resolve("Instances").resolve(name);
         WorldConfig defaultConfig = new WorldConfig();

         try {
            Files.createDirectories(path);
         } catch (IOException var8) {
            context.sendMessage(Message.translation("server.commands.instances.createDirectory.failed").param("errormsg", var8.getMessage()));
            return CompletableFuture.completedFuture(null);
         }

         return WorldConfig.save(path.resolve("instance.bson"), defaultConfig)
            .thenRun(() -> context.sendMessage(Message.translation("server.commands.instances.createdInstanceAssetConfig").param("name", name)));
      }
   }
}
