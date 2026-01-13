package io.sentry;

import io.sentry.protocol.ViewHierarchy;
import java.io.File;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Attachment {
   @Nullable
   private byte[] bytes;
   @Nullable
   private final JsonSerializable serializable;
   @Nullable
   private final Callable<byte[]> byteProvider;
   @Nullable
   private String pathname;
   @NotNull
   private final String filename;
   @Nullable
   private final String contentType;
   private final boolean addToTransactions;
   @Nullable
   private String attachmentType = "event.attachment";
   private static final String DEFAULT_ATTACHMENT_TYPE = "event.attachment";
   private static final String VIEW_HIERARCHY_ATTACHMENT_TYPE = "event.view_hierarchy";

   public Attachment(@NotNull byte[] bytes, @NotNull String filename) {
      this(bytes, filename, null);
   }

   public Attachment(@NotNull byte[] bytes, @NotNull String filename, @Nullable String contentType) {
      this(bytes, filename, contentType, false);
   }

   public Attachment(@NotNull byte[] bytes, @NotNull String filename, @Nullable String contentType, boolean addToTransactions) {
      this(bytes, filename, contentType, "event.attachment", addToTransactions);
   }

   public Attachment(@NotNull byte[] bytes, @NotNull String filename, @Nullable String contentType, @Nullable String attachmentType, boolean addToTransactions) {
      this.bytes = bytes;
      this.serializable = null;
      this.byteProvider = null;
      this.filename = filename;
      this.contentType = contentType;
      this.attachmentType = attachmentType;
      this.addToTransactions = addToTransactions;
   }

   public Attachment(
      @NotNull JsonSerializable serializable,
      @NotNull String filename,
      @Nullable String contentType,
      @Nullable String attachmentType,
      boolean addToTransactions
   ) {
      this.bytes = null;
      this.serializable = serializable;
      this.byteProvider = null;
      this.filename = filename;
      this.contentType = contentType;
      this.attachmentType = attachmentType;
      this.addToTransactions = addToTransactions;
   }

   public Attachment(
      @NotNull Callable<byte[]> byteProvider,
      @NotNull String filename,
      @Nullable String contentType,
      @Nullable String attachmentType,
      boolean addToTransactions
   ) {
      this.bytes = null;
      this.serializable = null;
      this.byteProvider = byteProvider;
      this.filename = filename;
      this.contentType = contentType;
      this.attachmentType = attachmentType;
      this.addToTransactions = addToTransactions;
   }

   public Attachment(@NotNull String pathname) {
      this(pathname, new File(pathname).getName());
   }

   public Attachment(@NotNull String pathname, @NotNull String filename) {
      this(pathname, filename, null);
   }

   public Attachment(@NotNull String pathname, @NotNull String filename, @Nullable String contentType) {
      this(pathname, filename, contentType, "event.attachment", false);
   }

   public Attachment(
      @NotNull String pathname, @NotNull String filename, @Nullable String contentType, @Nullable String attachmentType, boolean addToTransactions
   ) {
      this.pathname = pathname;
      this.filename = filename;
      this.serializable = null;
      this.byteProvider = null;
      this.contentType = contentType;
      this.attachmentType = attachmentType;
      this.addToTransactions = addToTransactions;
   }

   public Attachment(@NotNull String pathname, @NotNull String filename, @Nullable String contentType, boolean addToTransactions) {
      this.pathname = pathname;
      this.filename = filename;
      this.serializable = null;
      this.byteProvider = null;
      this.contentType = contentType;
      this.addToTransactions = addToTransactions;
   }

   public Attachment(
      @NotNull String pathname, @NotNull String filename, @Nullable String contentType, boolean addToTransactions, @Nullable String attachmentType
   ) {
      this.pathname = pathname;
      this.filename = filename;
      this.serializable = null;
      this.byteProvider = null;
      this.contentType = contentType;
      this.addToTransactions = addToTransactions;
      this.attachmentType = attachmentType;
   }

   @Nullable
   public byte[] getBytes() {
      return this.bytes;
   }

   @Nullable
   public JsonSerializable getSerializable() {
      return this.serializable;
   }

   @Nullable
   public String getPathname() {
      return this.pathname;
   }

   @NotNull
   public String getFilename() {
      return this.filename;
   }

   @Nullable
   public String getContentType() {
      return this.contentType;
   }

   boolean isAddToTransactions() {
      return this.addToTransactions;
   }

   @Nullable
   public String getAttachmentType() {
      return this.attachmentType;
   }

   @Nullable
   public Callable<byte[]> getByteProvider() {
      return this.byteProvider;
   }

   @NotNull
   public static Attachment fromScreenshot(byte[] screenshotBytes) {
      return new Attachment(screenshotBytes, "screenshot.png", "image/png", false);
   }

   @NotNull
   public static Attachment fromByteProvider(
      @NotNull Callable<byte[]> provider, @NotNull String filename, @Nullable String contentType, boolean addToTransactions
   ) {
      return new Attachment(provider, filename, contentType, "event.attachment", addToTransactions);
   }

   @NotNull
   public static Attachment fromViewHierarchy(ViewHierarchy viewHierarchy) {
      return new Attachment(viewHierarchy, "view-hierarchy.json", "application/json", "event.view_hierarchy", false);
   }

   @NotNull
   public static Attachment fromThreadDump(byte[] bytes) {
      return new Attachment(bytes, "thread-dump.txt", "text/plain", false);
   }
}
