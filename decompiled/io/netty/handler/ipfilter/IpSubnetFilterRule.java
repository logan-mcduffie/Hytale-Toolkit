package io.netty.handler.ipfilter;

import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class IpSubnetFilterRule implements IpFilterRule, Comparable<IpSubnetFilterRule> {
   private final IpFilterRule filterRule;
   private final String ipAddress;

   public IpSubnetFilterRule(String ipAddressWithCidr, IpFilterRuleType ruleType) {
      try {
         String[] ipAndCidr = ipAddressWithCidr.split("/");
         if (ipAndCidr.length != 2) {
            throw new IllegalArgumentException("ipAddressWithCidr: " + ipAddressWithCidr + " (expected: \"<ip-address>/<mask-size>\")");
         } else {
            this.ipAddress = ipAndCidr[0];
            int cidrPrefix = Integer.parseInt(ipAndCidr[1]);
            this.filterRule = selectFilterRule(SocketUtils.addressByName(this.ipAddress), cidrPrefix, ruleType);
         }
      } catch (UnknownHostException var5) {
         throw new IllegalArgumentException("ipAddressWithCidr", var5);
      }
   }

   public IpSubnetFilterRule(String ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
      try {
         this.ipAddress = ipAddress;
         this.filterRule = selectFilterRule(SocketUtils.addressByName(ipAddress), cidrPrefix, ruleType);
      } catch (UnknownHostException var5) {
         throw new IllegalArgumentException("ipAddress", var5);
      }
   }

   public IpSubnetFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
      this.ipAddress = ipAddress.getHostAddress();
      this.filterRule = selectFilterRule(ipAddress, cidrPrefix, ruleType);
   }

   private static IpFilterRule selectFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
      ObjectUtil.checkNotNull(ipAddress, "ipAddress");
      ObjectUtil.checkNotNull(ruleType, "ruleType");
      if (ipAddress instanceof Inet4Address) {
         return new IpSubnetFilterRule.Ip4SubnetFilterRule((Inet4Address)ipAddress, cidrPrefix, ruleType);
      } else if (ipAddress instanceof Inet6Address) {
         return new IpSubnetFilterRule.Ip6SubnetFilterRule((Inet6Address)ipAddress, cidrPrefix, ruleType);
      } else {
         throw new IllegalArgumentException("Only IPv4 and IPv6 addresses are supported");
      }
   }

   @Override
   public boolean matches(InetSocketAddress remoteAddress) {
      return this.filterRule.matches(remoteAddress);
   }

   @Override
   public IpFilterRuleType ruleType() {
      return this.filterRule.ruleType();
   }

   String getIpAddress() {
      return this.ipAddress;
   }

   IpFilterRule getFilterRule() {
      return this.filterRule;
   }

   public int compareTo(IpSubnetFilterRule ipSubnetFilterRule) {
      return this.filterRule instanceof IpSubnetFilterRule.Ip4SubnetFilterRule
         ? compareInt(
            ((IpSubnetFilterRule.Ip4SubnetFilterRule)this.filterRule).networkAddress,
            ((IpSubnetFilterRule.Ip4SubnetFilterRule)ipSubnetFilterRule.filterRule).networkAddress
         )
         : ((IpSubnetFilterRule.Ip6SubnetFilterRule)this.filterRule)
            .networkAddress
            .compareTo(((IpSubnetFilterRule.Ip6SubnetFilterRule)ipSubnetFilterRule.filterRule).networkAddress);
   }

   int compareTo(InetSocketAddress inetSocketAddress) {
      if (this.filterRule instanceof IpSubnetFilterRule.Ip4SubnetFilterRule) {
         IpSubnetFilterRule.Ip4SubnetFilterRule ip4SubnetFilterRule = (IpSubnetFilterRule.Ip4SubnetFilterRule)this.filterRule;
         return compareInt(
            ip4SubnetFilterRule.networkAddress, NetUtil.ipv4AddressToInt((Inet4Address)inetSocketAddress.getAddress()) & ip4SubnetFilterRule.subnetMask
         );
      } else {
         IpSubnetFilterRule.Ip6SubnetFilterRule ip6SubnetFilterRule = (IpSubnetFilterRule.Ip6SubnetFilterRule)this.filterRule;
         return ip6SubnetFilterRule.networkAddress
            .compareTo(IpSubnetFilterRule.Ip6SubnetFilterRule.ipToInt((Inet6Address)inetSocketAddress.getAddress()).and(ip6SubnetFilterRule.networkAddress));
      }
   }

   private static int compareInt(int x, int y) {
      return x < y ? -1 : (x == y ? 0 : 1);
   }

   static final class Ip4SubnetFilterRule implements IpFilterRule {
      private final int networkAddress;
      private final int subnetMask;
      private final IpFilterRuleType ruleType;

      private Ip4SubnetFilterRule(Inet4Address ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
         if (cidrPrefix >= 0 && cidrPrefix <= 32) {
            this.subnetMask = prefixToSubnetMask(cidrPrefix);
            this.networkAddress = NetUtil.ipv4AddressToInt(ipAddress) & this.subnetMask;
            this.ruleType = ruleType;
         } else {
            throw new IllegalArgumentException(String.format("IPv4 requires the subnet prefix to be in range of [0,32]. The prefix was: %d", cidrPrefix));
         }
      }

      @Override
      public boolean matches(InetSocketAddress remoteAddress) {
         InetAddress inetAddress = remoteAddress.getAddress();
         if (inetAddress instanceof Inet4Address) {
            int ipAddress = NetUtil.ipv4AddressToInt((Inet4Address)inetAddress);
            return (ipAddress & this.subnetMask) == this.networkAddress;
         } else {
            return false;
         }
      }

      @Override
      public IpFilterRuleType ruleType() {
         return this.ruleType;
      }

      private static int prefixToSubnetMask(int cidrPrefix) {
         return (int)(-1L << 32 - cidrPrefix);
      }
   }

   static final class Ip6SubnetFilterRule implements IpFilterRule {
      private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1L);
      private final BigInteger networkAddress;
      private final BigInteger subnetMask;
      private final IpFilterRuleType ruleType;

      private Ip6SubnetFilterRule(Inet6Address ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
         if (cidrPrefix >= 0 && cidrPrefix <= 128) {
            this.subnetMask = prefixToSubnetMask(cidrPrefix);
            this.networkAddress = ipToInt(ipAddress).and(this.subnetMask);
            this.ruleType = ruleType;
         } else {
            throw new IllegalArgumentException(String.format("IPv6 requires the subnet prefix to be in range of [0,128]. The prefix was: %d", cidrPrefix));
         }
      }

      @Override
      public boolean matches(InetSocketAddress remoteAddress) {
         InetAddress inetAddress = remoteAddress.getAddress();
         if (!(inetAddress instanceof Inet6Address)) {
            return false;
         } else {
            BigInteger ipAddress = ipToInt((Inet6Address)inetAddress);
            return ipAddress.and(this.subnetMask).equals(this.subnetMask) || ipAddress.and(this.subnetMask).equals(this.networkAddress);
         }
      }

      @Override
      public IpFilterRuleType ruleType() {
         return this.ruleType;
      }

      private static BigInteger ipToInt(Inet6Address ipAddress) {
         byte[] octets = ipAddress.getAddress();

         assert octets.length == 16;

         return new BigInteger(octets);
      }

      private static BigInteger prefixToSubnetMask(int cidrPrefix) {
         return MINUS_ONE.shiftLeft(128 - cidrPrefix);
      }
   }
}
