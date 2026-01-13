package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@ChannelHandler.Sharable
public class IpSubnetFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
   private final boolean acceptIfNotFound;
   private final IpSubnetFilterRule[] ipv4Rules;
   private final IpSubnetFilterRule[] ipv6Rules;
   private final IpFilterRuleType ipFilterRuleTypeIPv4;
   private final IpFilterRuleType ipFilterRuleTypeIPv6;

   public IpSubnetFilter(IpSubnetFilterRule... rules) {
      this(true, Arrays.asList(ObjectUtil.checkNotNull(rules, "rules")));
   }

   public IpSubnetFilter(boolean acceptIfNotFound, IpSubnetFilterRule... rules) {
      this(acceptIfNotFound, Arrays.asList(ObjectUtil.checkNotNull(rules, "rules")));
   }

   public IpSubnetFilter(List<IpSubnetFilterRule> rules) {
      this(true, rules);
   }

   public IpSubnetFilter(boolean acceptIfNotFound, List<IpSubnetFilterRule> rules) {
      ObjectUtil.checkNotNull(rules, "rules");
      this.acceptIfNotFound = acceptIfNotFound;
      int numAcceptIPv4 = 0;
      int numRejectIPv4 = 0;
      int numAcceptIPv6 = 0;
      int numRejectIPv6 = 0;
      List<IpSubnetFilterRule> unsortedIPv4Rules = new ArrayList<>();
      List<IpSubnetFilterRule> unsortedIPv6Rules = new ArrayList<>();

      for (IpSubnetFilterRule ipSubnetFilterRule : rules) {
         ObjectUtil.checkNotNull(ipSubnetFilterRule, "rule");
         if (ipSubnetFilterRule.getFilterRule() instanceof IpSubnetFilterRule.Ip4SubnetFilterRule) {
            unsortedIPv4Rules.add(ipSubnetFilterRule);
            if (ipSubnetFilterRule.ruleType() == IpFilterRuleType.ACCEPT) {
               numAcceptIPv4++;
            } else {
               numRejectIPv4++;
            }
         } else {
            unsortedIPv6Rules.add(ipSubnetFilterRule);
            if (ipSubnetFilterRule.ruleType() == IpFilterRuleType.ACCEPT) {
               numAcceptIPv6++;
            } else {
               numRejectIPv6++;
            }
         }
      }

      if (numAcceptIPv4 == 0 && numRejectIPv4 > 0) {
         this.ipFilterRuleTypeIPv4 = IpFilterRuleType.REJECT;
      } else if (numAcceptIPv4 > 0 && numRejectIPv4 == 0) {
         this.ipFilterRuleTypeIPv4 = IpFilterRuleType.ACCEPT;
      } else {
         this.ipFilterRuleTypeIPv4 = null;
      }

      if (numAcceptIPv6 == 0 && numRejectIPv6 > 0) {
         this.ipFilterRuleTypeIPv6 = IpFilterRuleType.REJECT;
      } else if (numAcceptIPv6 > 0 && numRejectIPv6 == 0) {
         this.ipFilterRuleTypeIPv6 = IpFilterRuleType.ACCEPT;
      } else {
         this.ipFilterRuleTypeIPv6 = null;
      }

      this.ipv4Rules = unsortedIPv4Rules.isEmpty() ? null : sortAndFilter(unsortedIPv4Rules);
      this.ipv6Rules = unsortedIPv6Rules.isEmpty() ? null : sortAndFilter(unsortedIPv6Rules);
   }

   protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) {
      if (this.ipv4Rules != null && remoteAddress.getAddress() instanceof Inet4Address) {
         int indexOf = Arrays.binarySearch(this.ipv4Rules, remoteAddress, IpSubnetFilterRuleComparator.INSTANCE);
         if (indexOf >= 0) {
            if (this.ipFilterRuleTypeIPv4 == null) {
               return this.ipv4Rules[indexOf].ruleType() == IpFilterRuleType.ACCEPT;
            }

            return this.ipFilterRuleTypeIPv4 == IpFilterRuleType.ACCEPT;
         }
      } else if (this.ipv6Rules != null) {
         int indexOf = Arrays.binarySearch(this.ipv6Rules, remoteAddress, IpSubnetFilterRuleComparator.INSTANCE);
         if (indexOf >= 0) {
            if (this.ipFilterRuleTypeIPv6 == null) {
               return this.ipv6Rules[indexOf].ruleType() == IpFilterRuleType.ACCEPT;
            }

            return this.ipFilterRuleTypeIPv6 == IpFilterRuleType.ACCEPT;
         }
      }

      return this.acceptIfNotFound;
   }

   private static IpSubnetFilterRule[] sortAndFilter(List<IpSubnetFilterRule> rules) {
      Collections.sort(rules);
      Iterator<IpSubnetFilterRule> iterator = rules.iterator();
      List<IpSubnetFilterRule> toKeep = new ArrayList<>();
      IpSubnetFilterRule parentRule = iterator.hasNext() ? iterator.next() : null;
      if (parentRule != null) {
         toKeep.add(parentRule);
      }

      while (iterator.hasNext()) {
         IpSubnetFilterRule childRule = iterator.next();
         if (!parentRule.matches(new InetSocketAddress(childRule.getIpAddress(), 1))) {
            toKeep.add(childRule);
            parentRule = childRule;
         }
      }

      return toKeep.toArray(new IpSubnetFilterRule[0]);
   }
}
