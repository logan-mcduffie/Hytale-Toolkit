package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Iterable;

public class RecipientInformationStore implements Iterable<RecipientInformation> {
   private final List all;
   private final Map table = new HashMap();

   public RecipientInformationStore(RecipientInformation var1) {
      this.all = new ArrayList(1);
      this.all.add(var1);
      RecipientId var2 = var1.getRID();
      this.table.put(var2, this.all);
   }

   public RecipientInformationStore(Collection<RecipientInformation> var1) {
      for (RecipientInformation var3 : var1) {
         RecipientId var4 = var3.getRID();
         ArrayList var5 = (ArrayList)this.table.get(var4);
         if (var5 == null) {
            var5 = new ArrayList(1);
            this.table.put(var4, var5);
         }

         var5.add(var3);
      }

      this.all = new ArrayList(var1);
   }

   public RecipientInformation get(RecipientId var1) {
      Collection var2 = this.getRecipients(var1);
      return var2.size() == 0 ? null : (RecipientInformation)var2.iterator().next();
   }

   public int size() {
      return this.all.size();
   }

   public Collection<RecipientInformation> getRecipients() {
      return new ArrayList<>(this.all);
   }

   public Collection<RecipientInformation> getRecipients(RecipientId var1) {
      if (var1 instanceof PKIXRecipientId) {
         PKIXRecipientId var2 = (PKIXRecipientId)var1;
         X500Name var3 = var2.getIssuer();
         byte[] var4 = var2.getSubjectKeyIdentifier();
         if (var3 != null && var4 != null) {
            ArrayList var5 = new ArrayList();
            ArrayList var6 = (ArrayList)this.table.get(new PKIXRecipientId(var2.getType(), var3, var2.getSerialNumber(), null));
            if (var6 != null) {
               var5.addAll(var6);
            }

            ArrayList var7 = (ArrayList)this.table.get(new PKIXRecipientId(var2.getType(), null, null, var4));
            if (var7 != null) {
               var5.addAll(var7);
            }

            return var5;
         }
      }

      ArrayList var8 = (ArrayList)this.table.get(var1);
      return var8 == null ? new ArrayList<>() : new ArrayList<>(var8);
   }

   @Override
   public Iterator<RecipientInformation> iterator() {
      return this.getRecipients().iterator();
   }
}
