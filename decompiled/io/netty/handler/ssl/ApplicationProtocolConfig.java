package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;

public final class ApplicationProtocolConfig {
   public static final ApplicationProtocolConfig DISABLED = new ApplicationProtocolConfig();
   private final List<String> supportedProtocols;
   private final ApplicationProtocolConfig.Protocol protocol;
   private final ApplicationProtocolConfig.SelectorFailureBehavior selectorBehavior;
   private final ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedBehavior;

   public ApplicationProtocolConfig(
      ApplicationProtocolConfig.Protocol protocol,
      ApplicationProtocolConfig.SelectorFailureBehavior selectorBehavior,
      ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedBehavior,
      Iterable<String> supportedProtocols
   ) {
      this(protocol, selectorBehavior, selectedBehavior, ApplicationProtocolUtil.toList(supportedProtocols));
   }

   public ApplicationProtocolConfig(
      ApplicationProtocolConfig.Protocol protocol,
      ApplicationProtocolConfig.SelectorFailureBehavior selectorBehavior,
      ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedBehavior,
      String... supportedProtocols
   ) {
      this(protocol, selectorBehavior, selectedBehavior, ApplicationProtocolUtil.toList(supportedProtocols));
   }

   private ApplicationProtocolConfig(
      ApplicationProtocolConfig.Protocol protocol,
      ApplicationProtocolConfig.SelectorFailureBehavior selectorBehavior,
      ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedBehavior,
      List<String> supportedProtocols
   ) {
      this.supportedProtocols = Collections.unmodifiableList(ObjectUtil.checkNotNull(supportedProtocols, "supportedProtocols"));
      this.protocol = ObjectUtil.checkNotNull(protocol, "protocol");
      this.selectorBehavior = ObjectUtil.checkNotNull(selectorBehavior, "selectorBehavior");
      this.selectedBehavior = ObjectUtil.checkNotNull(selectedBehavior, "selectedBehavior");
      if (protocol == ApplicationProtocolConfig.Protocol.NONE) {
         throw new IllegalArgumentException(
            "protocol (" + ApplicationProtocolConfig.Protocol.NONE + ") must not be " + ApplicationProtocolConfig.Protocol.NONE + '.'
         );
      } else {
         ObjectUtil.checkNonEmpty(supportedProtocols, "supportedProtocols");
      }
   }

   private ApplicationProtocolConfig() {
      this.supportedProtocols = Collections.emptyList();
      this.protocol = ApplicationProtocolConfig.Protocol.NONE;
      this.selectorBehavior = ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
      this.selectedBehavior = ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
   }

   public List<String> supportedProtocols() {
      return this.supportedProtocols;
   }

   public ApplicationProtocolConfig.Protocol protocol() {
      return this.protocol;
   }

   public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
      return this.selectorBehavior;
   }

   public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
      return this.selectedBehavior;
   }

   public static enum Protocol {
      NONE,
      NPN,
      ALPN,
      NPN_AND_ALPN;
   }

   public static enum SelectedListenerFailureBehavior {
      ACCEPT,
      FATAL_ALERT,
      CHOOSE_MY_LAST_PROTOCOL;
   }

   public static enum SelectorFailureBehavior {
      FATAL_ALERT,
      NO_ADVERTISE,
      CHOOSE_MY_LAST_PROTOCOL;
   }
}
