package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class RuleBasedIpFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
   private final boolean acceptIfNotFound;
   private final List<IpFilterRule> rules;

   public RuleBasedIpFilter(IpFilterRule... rules) {
      this(true, rules);
   }

   public RuleBasedIpFilter(boolean acceptIfNotFound, IpFilterRule... rules) {
      ObjectUtil.checkNotNull(rules, "rules");
      this.acceptIfNotFound = acceptIfNotFound;
      this.rules = new ArrayList<>(rules.length);

      for (IpFilterRule rule : rules) {
         if (rule != null) {
            this.rules.add(rule);
         }
      }
   }

   protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
      for (IpFilterRule rule : this.rules) {
         if (rule.matches(remoteAddress)) {
            return rule.ruleType() == IpFilterRuleType.ACCEPT;
         }
      }

      return this.acceptIfNotFound;
   }
}
