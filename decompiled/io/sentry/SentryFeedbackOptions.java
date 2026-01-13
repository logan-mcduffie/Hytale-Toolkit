package io.sentry;

import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class SentryFeedbackOptions {
   private boolean isNameRequired = false;
   private boolean showName = true;
   private boolean isEmailRequired = false;
   private boolean showEmail = true;
   private boolean useSentryUser = true;
   private boolean showBranding = true;
   @NotNull
   private CharSequence formTitle = "Report a Bug";
   @NotNull
   private CharSequence submitButtonLabel = "Send Bug Report";
   @NotNull
   private CharSequence cancelButtonLabel = "Cancel";
   @NotNull
   private CharSequence nameLabel = "Name";
   @NotNull
   private CharSequence namePlaceholder = "Your Name";
   @NotNull
   private CharSequence emailLabel = "Email";
   @NotNull
   private CharSequence emailPlaceholder = "your.email@example.org";
   @NotNull
   private CharSequence isRequiredLabel = " (Required)";
   @NotNull
   private CharSequence messageLabel = "Description";
   @NotNull
   private CharSequence messagePlaceholder = "What's the bug? What did you expect?";
   @NotNull
   private CharSequence successMessageText = "Thank you for your report!";
   @Nullable
   private Runnable onFormOpen;
   @Nullable
   private Runnable onFormClose;
   @Nullable
   private SentryFeedbackOptions.SentryFeedbackCallback onSubmitSuccess;
   @Nullable
   private SentryFeedbackOptions.SentryFeedbackCallback onSubmitError;
   @NotNull
   private SentryFeedbackOptions.IDialogHandler iDialogHandler;

   public SentryFeedbackOptions(@NotNull SentryFeedbackOptions.IDialogHandler iDialogHandler) {
      this.iDialogHandler = iDialogHandler;
   }

   public SentryFeedbackOptions(@NotNull SentryFeedbackOptions other) {
      this.isNameRequired = other.isNameRequired;
      this.showName = other.showName;
      this.isEmailRequired = other.isEmailRequired;
      this.showEmail = other.showEmail;
      this.useSentryUser = other.useSentryUser;
      this.showBranding = other.showBranding;
      this.formTitle = other.formTitle;
      this.submitButtonLabel = other.submitButtonLabel;
      this.cancelButtonLabel = other.cancelButtonLabel;
      this.nameLabel = other.nameLabel;
      this.namePlaceholder = other.namePlaceholder;
      this.emailLabel = other.emailLabel;
      this.emailPlaceholder = other.emailPlaceholder;
      this.isRequiredLabel = other.isRequiredLabel;
      this.messageLabel = other.messageLabel;
      this.messagePlaceholder = other.messagePlaceholder;
      this.successMessageText = other.successMessageText;
      this.onFormOpen = other.onFormOpen;
      this.onFormClose = other.onFormClose;
      this.onSubmitSuccess = other.onSubmitSuccess;
      this.onSubmitError = other.onSubmitError;
      this.iDialogHandler = other.iDialogHandler;
   }

   public boolean isNameRequired() {
      return this.isNameRequired;
   }

   public void setNameRequired(boolean isNameRequired) {
      this.isNameRequired = isNameRequired;
   }

   public boolean isShowName() {
      return this.showName;
   }

   public void setShowName(boolean showName) {
      this.showName = showName;
   }

   public boolean isEmailRequired() {
      return this.isEmailRequired;
   }

   public void setEmailRequired(boolean isEmailRequired) {
      this.isEmailRequired = isEmailRequired;
   }

   public boolean isShowEmail() {
      return this.showEmail;
   }

   public void setShowEmail(boolean showEmail) {
      this.showEmail = showEmail;
   }

   public boolean isUseSentryUser() {
      return this.useSentryUser;
   }

   public void setUseSentryUser(boolean useSentryUser) {
      this.useSentryUser = useSentryUser;
   }

   public boolean isShowBranding() {
      return this.showBranding;
   }

   public void setShowBranding(boolean showBranding) {
      this.showBranding = showBranding;
   }

   @NotNull
   public CharSequence getFormTitle() {
      return this.formTitle;
   }

   public void setFormTitle(@NotNull CharSequence formTitle) {
      this.formTitle = formTitle;
   }

   @NotNull
   public CharSequence getSubmitButtonLabel() {
      return this.submitButtonLabel;
   }

   public void setSubmitButtonLabel(@NotNull CharSequence submitButtonLabel) {
      this.submitButtonLabel = submitButtonLabel;
   }

   @NotNull
   public CharSequence getCancelButtonLabel() {
      return this.cancelButtonLabel;
   }

   public void setCancelButtonLabel(@NotNull CharSequence cancelButtonLabel) {
      this.cancelButtonLabel = cancelButtonLabel;
   }

   @NotNull
   public CharSequence getNameLabel() {
      return this.nameLabel;
   }

   public void setNameLabel(@NotNull CharSequence nameLabel) {
      this.nameLabel = nameLabel;
   }

   @NotNull
   public CharSequence getNamePlaceholder() {
      return this.namePlaceholder;
   }

   public void setNamePlaceholder(@NotNull CharSequence namePlaceholder) {
      this.namePlaceholder = namePlaceholder;
   }

   @NotNull
   public CharSequence getEmailLabel() {
      return this.emailLabel;
   }

   public void setEmailLabel(@NotNull CharSequence emailLabel) {
      this.emailLabel = emailLabel;
   }

   @NotNull
   public CharSequence getEmailPlaceholder() {
      return this.emailPlaceholder;
   }

   public void setEmailPlaceholder(@NotNull CharSequence emailPlaceholder) {
      this.emailPlaceholder = emailPlaceholder;
   }

   @NotNull
   public CharSequence getIsRequiredLabel() {
      return this.isRequiredLabel;
   }

   public void setIsRequiredLabel(@NotNull CharSequence isRequiredLabel) {
      this.isRequiredLabel = isRequiredLabel;
   }

   @NotNull
   public CharSequence getMessageLabel() {
      return this.messageLabel;
   }

   public void setMessageLabel(@NotNull CharSequence messageLabel) {
      this.messageLabel = messageLabel;
   }

   @NotNull
   public CharSequence getMessagePlaceholder() {
      return this.messagePlaceholder;
   }

   public void setMessagePlaceholder(@NotNull CharSequence messagePlaceholder) {
      this.messagePlaceholder = messagePlaceholder;
   }

   @NotNull
   public CharSequence getSuccessMessageText() {
      return this.successMessageText;
   }

   public void setSuccessMessageText(@NotNull CharSequence successMessageText) {
      this.successMessageText = successMessageText;
   }

   @Nullable
   public Runnable getOnFormOpen() {
      return this.onFormOpen;
   }

   public void setOnFormOpen(@Nullable Runnable onFormOpen) {
      this.onFormOpen = onFormOpen;
   }

   @Nullable
   public Runnable getOnFormClose() {
      return this.onFormClose;
   }

   public void setOnFormClose(@Nullable Runnable onFormClose) {
      this.onFormClose = onFormClose;
   }

   @Nullable
   public SentryFeedbackOptions.SentryFeedbackCallback getOnSubmitSuccess() {
      return this.onSubmitSuccess;
   }

   public void setOnSubmitSuccess(@Nullable SentryFeedbackOptions.SentryFeedbackCallback onSubmitSuccess) {
      this.onSubmitSuccess = onSubmitSuccess;
   }

   @Nullable
   public SentryFeedbackOptions.SentryFeedbackCallback getOnSubmitError() {
      return this.onSubmitError;
   }

   public void setOnSubmitError(@Nullable SentryFeedbackOptions.SentryFeedbackCallback onSubmitError) {
      this.onSubmitError = onSubmitError;
   }

   @Internal
   public void setDialogHandler(@NotNull SentryFeedbackOptions.IDialogHandler iDialogHandler) {
      this.iDialogHandler = iDialogHandler;
   }

   @Internal
   @NotNull
   public SentryFeedbackOptions.IDialogHandler getDialogHandler() {
      return this.iDialogHandler;
   }

   @Override
   public String toString() {
      return "SentryFeedbackOptions{isNameRequired="
         + this.isNameRequired
         + ", showName="
         + this.showName
         + ", isEmailRequired="
         + this.isEmailRequired
         + ", showEmail="
         + this.showEmail
         + ", useSentryUser="
         + this.useSentryUser
         + ", showBranding="
         + this.showBranding
         + ", formTitle='"
         + this.formTitle
         + '\''
         + ", submitButtonLabel='"
         + this.submitButtonLabel
         + '\''
         + ", cancelButtonLabel='"
         + this.cancelButtonLabel
         + '\''
         + ", nameLabel='"
         + this.nameLabel
         + '\''
         + ", namePlaceholder='"
         + this.namePlaceholder
         + '\''
         + ", emailLabel='"
         + this.emailLabel
         + '\''
         + ", emailPlaceholder='"
         + this.emailPlaceholder
         + '\''
         + ", isRequiredLabel='"
         + this.isRequiredLabel
         + '\''
         + ", messageLabel='"
         + this.messageLabel
         + '\''
         + ", messagePlaceholder='"
         + this.messagePlaceholder
         + '\''
         + '}';
   }

   @Internal
   public interface IDialogHandler {
      void showDialog(@Nullable SentryId var1, @Nullable SentryFeedbackOptions.OptionsConfigurator var2);
   }

   public interface OptionsConfigurator {
      void configure(@NotNull SentryFeedbackOptions var1);
   }

   public interface SentryFeedbackCallback {
      void call(@NotNull Feedback var1);
   }
}
