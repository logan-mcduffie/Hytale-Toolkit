package org.bouncycastle.pkix.jcajce;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PKIXPolicyNode implements PolicyNode {
   protected List children;
   protected int depth;
   protected Set expectedPolicies;
   protected PolicyNode parent;
   protected Set policyQualifiers;
   protected String validPolicy;
   protected boolean critical;

   public PKIXPolicyNode(List var1, int var2, Set var3, PolicyNode var4, Set var5, String var6, boolean var7) {
      this.children = var1;
      this.depth = var2;
      this.expectedPolicies = var3;
      this.parent = var4;
      this.policyQualifiers = var5;
      this.validPolicy = var6;
      this.critical = var7;
   }

   public void addChild(PKIXPolicyNode var1) {
      this.children.add(var1);
      var1.setParent(this);
   }

   @Override
   public Iterator getChildren() {
      return this.children.iterator();
   }

   @Override
   public int getDepth() {
      return this.depth;
   }

   @Override
   public Set getExpectedPolicies() {
      return this.expectedPolicies;
   }

   @Override
   public PolicyNode getParent() {
      return this.parent;
   }

   @Override
   public Set getPolicyQualifiers() {
      return this.policyQualifiers;
   }

   @Override
   public String getValidPolicy() {
      return this.validPolicy;
   }

   public boolean hasChildren() {
      return !this.children.isEmpty();
   }

   @Override
   public boolean isCritical() {
      return this.critical;
   }

   public void removeChild(PKIXPolicyNode var1) {
      this.children.remove(var1);
   }

   public void setCritical(boolean var1) {
      this.critical = var1;
   }

   public void setParent(PKIXPolicyNode var1) {
      this.parent = var1;
   }

   @Override
   public String toString() {
      return this.toString("");
   }

   public String toString(String var1) {
      StringBuffer var2 = new StringBuffer();
      var2.append(var1);
      var2.append(this.validPolicy);
      var2.append(" {\n");

      for (int var3 = 0; var3 < this.children.size(); var3++) {
         var2.append(((PKIXPolicyNode)this.children.get(var3)).toString(var1 + "    "));
      }

      var2.append(var1);
      var2.append("}\n");
      return var2.toString();
   }

   @Override
   public Object clone() {
      return this.copy();
   }

   public PKIXPolicyNode copy() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.expectedPolicies.iterator();

      while (var2.hasNext()) {
         var1.add(new String((String)var2.next()));
      }

      HashSet var3 = new HashSet();
      var2 = this.policyQualifiers.iterator();

      while (var2.hasNext()) {
         var3.add(new String((String)var2.next()));
      }

      PKIXPolicyNode var4 = new PKIXPolicyNode(new ArrayList(), this.depth, var1, null, var3, new String(this.validPolicy), this.critical);
      var2 = this.children.iterator();

      while (var2.hasNext()) {
         PKIXPolicyNode var5 = ((PKIXPolicyNode)var2.next()).copy();
         var5.setParent(var4);
         var4.addChild(var5);
      }

      return var4;
   }

   public void setExpectedPolicies(Set var1) {
      this.expectedPolicies = var1;
   }
}
