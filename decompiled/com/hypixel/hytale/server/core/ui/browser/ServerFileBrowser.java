package com.hypixel.hytale.server.core.ui.browser;

import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.common.util.StringCompareUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerFileBrowser {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final Value<String> BUTTON_HIGHLIGHTED = Value.ref("Pages/BasicTextButton.ui", "SelectedLabelStyle");
   @Nonnull
   private final FileBrowserConfig config;
   @Nonnull
   private Path root;
   @Nonnull
   private Path currentDir;
   @Nonnull
   private String searchQuery;
   @Nonnull
   private final Set<String> selectedItems;

   public ServerFileBrowser(@Nonnull FileBrowserConfig config) {
      this.config = config;
      this.selectedItems = new LinkedHashSet<>();
      this.searchQuery = "";
      if (!config.roots().isEmpty()) {
         this.root = config.roots().get(0).path();
      } else {
         this.root = Paths.get("");
      }

      this.currentDir = this.root.getFileSystem().getPath("");
   }

   public ServerFileBrowser(@Nonnull FileBrowserConfig config, @Nullable Path initialRoot, @Nullable Path initialDir) {
      this(config);
      if (initialRoot != null && Files.isDirectory(initialRoot)) {
         this.root = initialRoot;
         this.currentDir = this.root.getFileSystem().getPath("");
      }

      if (initialDir != null) {
         this.currentDir = initialDir;
      }
   }

   public void buildRootSelector(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
      if (this.config.enableRootSelector() && this.config.rootSelectorId() != null) {
         ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

         for (FileBrowserConfig.RootEntry rootEntry : this.config.roots()) {
            entries.add(new DropdownEntryInfo(rootEntry.displayName(), rootEntry.path().toString()));
         }

         commandBuilder.set(this.config.rootSelectorId() + ".Entries", entries);
         commandBuilder.set(this.config.rootSelectorId() + ".Value", this.root.toString());
         eventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            this.config.rootSelectorId(),
            new EventData().append("@Root", this.config.rootSelectorId() + ".Value"),
            false
         );
      } else {
         if (this.config.rootSelectorId() != null) {
            commandBuilder.set(this.config.rootSelectorId() + ".Visible", false);
         }
      }
   }

   public void buildSearchInput(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
      if (this.config.enableSearch() && this.config.searchInputId() != null) {
         if (!this.searchQuery.isEmpty()) {
            commandBuilder.set(this.config.searchInputId() + ".Value", this.searchQuery);
         }

         eventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged, this.config.searchInputId(), EventData.of("@SearchQuery", this.config.searchInputId() + ".Value"), false
         );
      }
   }

   public void buildCurrentPath(@Nonnull UICommandBuilder commandBuilder) {
      if (this.config.currentPathId() != null) {
         String rootDisplay = this.root.toString().replace("\\", "/");
         String relativeDisplay = this.currentDir.toString().isEmpty() ? "" : "/" + this.currentDir.toString().replace("\\", "/");
         String displayPath = rootDisplay + relativeDisplay;
         commandBuilder.set(this.config.currentPathId() + ".Text", displayPath);
      }
   }

   public void buildFileList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
      commandBuilder.clear(this.config.listElementId());
      List<FileListProvider.FileEntry> entries;
      if (this.config.customProvider() != null) {
         entries = this.config.customProvider().getFiles(this.currentDir, this.searchQuery);
      } else if (!this.searchQuery.isEmpty() && this.config.enableSearch()) {
         entries = this.buildSearchResults();
      } else {
         entries = this.buildDirectoryListing();
      }

      int buttonIndex = 0;
      if (this.config.enableDirectoryNav() && !this.currentDir.toString().isEmpty() && this.searchQuery.isEmpty()) {
         commandBuilder.append(this.config.listElementId(), "Pages/BasicTextButton.ui");
         commandBuilder.set(this.config.listElementId() + "[0].Text", "../");
         eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, this.config.listElementId() + "[0]", EventData.of("File", ".."));
         buttonIndex++;
      }

      for (FileListProvider.FileEntry entry : entries) {
         String displayText = entry.isDirectory() ? entry.displayName() + "/" : entry.displayName();
         commandBuilder.append(this.config.listElementId(), "Pages/BasicTextButton.ui");
         commandBuilder.set(this.config.listElementId() + "[" + buttonIndex + "].Text", displayText);
         if (!entry.isDirectory()) {
            commandBuilder.set(this.config.listElementId() + "[" + buttonIndex + "].Style", BUTTON_HIGHLIGHTED);
         }

         String eventKey = !this.searchQuery.isEmpty() && !entry.isDirectory() ? "SearchResult" : "File";
         eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, this.config.listElementId() + "[" + buttonIndex + "]", EventData.of(eventKey, entry.name())
         );
         buttonIndex++;
      }
   }

   public void buildUI(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
      this.buildRootSelector(commandBuilder, eventBuilder);
      this.buildSearchInput(commandBuilder, eventBuilder);
      this.buildCurrentPath(commandBuilder);
      this.buildFileList(commandBuilder, eventBuilder);
   }

   public boolean handleEvent(@Nonnull FileBrowserEventData data) {
      if (data.getSearchQuery() != null) {
         this.searchQuery = data.getSearchQuery().trim().toLowerCase();
         return true;
      } else if (data.getRoot() != null) {
         Path newRoot = this.findConfigRoot(data.getRoot());
         if (newRoot == null) {
            newRoot = Path.of(data.getRoot());
         }

         this.setRoot(newRoot);
         this.currentDir = this.root.getFileSystem().getPath("");
         this.searchQuery = "";
         return true;
      } else if (data.getFile() != null) {
         String fileName = data.getFile();
         if ("..".equals(fileName)) {
            this.navigateUp();
            return true;
         } else {
            if (this.config.enableDirectoryNav()) {
               Path targetPath = this.root.resolve(this.currentDir.toString()).resolve(fileName);
               if (Files.isDirectory(targetPath)) {
                  this.currentDir = PathUtil.relativize(this.root, targetPath);
                  return true;
               }
            }

            return false;
         }
      } else {
         return data.getSearchResult() != null ? false : false;
      }
   }

   private List<FileListProvider.FileEntry> buildDirectoryListing() {
      List<FileListProvider.FileEntry> entries = new ObjectArrayList<>();
      Path path = this.root.resolve(this.currentDir.toString());
      if (!Files.isDirectory(path)) {
         return entries;
      } else {
         try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path file : stream) {
               String fileName = file.getFileName().toString();
               if (!fileName.startsWith(".")) {
                  boolean isDirectory = Files.isDirectory(file);
                  if (isDirectory || this.matchesExtension(fileName)) {
                     entries.add(new FileListProvider.FileEntry(fileName, isDirectory));
                  }
               }
            }
         } catch (IOException var10) {
            LOGGER.atSevere().withCause(var10).log("Error listing directory: %s", path);
         }

         entries.sort((a, b) -> {
            if (a.isDirectory() == b.isDirectory()) {
               return a.name().compareToIgnoreCase(b.name());
            } else {
               return a.isDirectory() ? -1 : 1;
            }
         });
         return entries;
      }
   }

   private List<FileListProvider.FileEntry> buildSearchResults() {
      final List<Path> allFiles = new ObjectArrayList<>();
      if (!Files.isDirectory(this.root)) {
         return List.of();
      } else {
         try {
            Files.walkFileTree(this.root, new SimpleFileVisitor<Path>() {
               @Nonnull
               public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                  String fileName = file.getFileName().toString();
                  if (ServerFileBrowser.this.matchesExtension(fileName)) {
                     allFiles.add(file);
                  }

                  return FileVisitResult.CONTINUE;
               }
            });
         } catch (IOException var8) {
            LOGGER.atSevere().withCause(var8).log("Error walking directory: %s", this.root);
         }

         Object2IntMap<Path> matchScores = new Object2IntOpenHashMap<>(allFiles.size());

         for (Path file : allFiles) {
            String fileName = file.getFileName().toString();
            String baseName = this.removeExtensions(fileName);
            int score = StringCompareUtil.getFuzzyDistance(baseName, this.searchQuery, Locale.ENGLISH);
            if (score > 0) {
               matchScores.put(file, score);
            }
         }

         return matchScores.keySet().stream().sorted(Comparator.comparingInt(matchScores::getInt).reversed()).limit(this.config.maxResults()).map(filex -> {
            Path relativePath = PathUtil.relativize(this.root, filex);
            String fileNamex = filex.getFileName().toString();
            String displayName = this.removeExtensions(fileNamex);
            return new FileListProvider.FileEntry(relativePath.toString(), displayName, false, matchScores.getInt(filex));
         }).collect(Collectors.toList());
      }
   }

   private boolean matchesExtension(@Nonnull String fileName) {
      if (this.config.allowedExtensions().isEmpty()) {
         return true;
      } else {
         for (String ext : this.config.allowedExtensions()) {
            if (fileName.endsWith(ext)) {
               return true;
            }
         }

         return false;
      }
   }

   private String removeExtensions(@Nonnull String fileName) {
      for (String ext : this.config.allowedExtensions()) {
         if (fileName.endsWith(ext)) {
            return fileName.substring(0, fileName.length() - ext.length());
         }
      }

      return fileName;
   }

   @Nonnull
   public Path getRoot() {
      return this.root;
   }

   public void setRoot(@Nonnull Path root) {
      if (Files.isDirectory(root)) {
         this.root = root;
      }
   }

   @Nonnull
   public Path getCurrentDir() {
      return this.currentDir;
   }

   public void setCurrentDir(@Nonnull Path currentDir) {
      this.currentDir = currentDir;
   }

   @Nonnull
   public String getSearchQuery() {
      return this.searchQuery;
   }

   public void setSearchQuery(@Nonnull String searchQuery) {
      this.searchQuery = searchQuery;
   }

   public void navigateUp() {
      if (!this.currentDir.toString().isEmpty()) {
         Path parent = this.currentDir.getParent();
         this.currentDir = parent != null ? parent : Paths.get("");
      }
   }

   public void navigateTo(@Nonnull Path relativePath) {
      Path targetPath = this.root.resolve(this.currentDir.toString()).resolve(relativePath.toString());
      if (targetPath.normalize().startsWith(this.root.normalize())) {
         if (Files.isDirectory(targetPath)) {
            this.currentDir = PathUtil.relativize(this.root, targetPath);
         }
      }
   }

   @Nonnull
   public Set<String> getSelectedItems() {
      return Collections.unmodifiableSet(this.selectedItems);
   }

   public void addSelection(@Nonnull String item) {
      if (this.config.enableMultiSelect()) {
         this.selectedItems.add(item);
      } else {
         this.selectedItems.clear();
         this.selectedItems.add(item);
      }
   }

   public void clearSelection() {
      this.selectedItems.clear();
   }

   @Nonnull
   public FileBrowserConfig getConfig() {
      return this.config;
   }

   @Nullable
   public Path resolveSecure(@Nonnull String relativePath) {
      Path resolved = this.root.resolve(relativePath);
      return !resolved.normalize().startsWith(this.root.normalize()) ? null : resolved;
   }

   @Nullable
   public Path resolveFromCurrent(@Nonnull String fileName) {
      Path resolved = this.root.resolve(this.currentDir.toString()).resolve(fileName);
      return !resolved.normalize().startsWith(this.root.normalize()) ? null : resolved;
   }

   @Nullable
   private Path findConfigRoot(@Nonnull String pathStr) {
      for (FileBrowserConfig.RootEntry rootEntry : this.config.roots()) {
         if (rootEntry.path().toString().equals(pathStr)) {
            return rootEntry.path();
         }
      }

      return null;
   }
}
