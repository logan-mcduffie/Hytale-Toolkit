package io.netty.handler.ssl;

@Deprecated
public interface OpenSslApplicationProtocolNegotiator extends ApplicationProtocolNegotiator {
   ApplicationProtocolConfig.Protocol protocol();

   ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior();

   ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior();
}
