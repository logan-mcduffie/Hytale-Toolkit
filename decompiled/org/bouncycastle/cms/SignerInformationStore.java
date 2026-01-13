package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.util.Iterable;

public class SignerInformationStore implements Iterable<SignerInformation> {
   private List all = new ArrayList();
   private Map table = new HashMap();

   public SignerInformationStore(SignerInformation var1) {
      this.all = new ArrayList(1);
      this.all.add(var1);
      SignerId var2 = var1.getSID();
      this.table.put(var2, this.all);
   }

   public SignerInformationStore(Collection<SignerInformation> var1) {
      for (SignerInformation var3 : var1) {
         SignerId var4 = var3.getSID();
         ArrayList var5 = (ArrayList)this.table.get(var4);
         if (var5 == null) {
            var5 = new ArrayList(1);
            this.table.put(var4, var5);
         }

         var5.add(var3);
      }

      this.all = new ArrayList(var1);
   }

   public SignerInformation get(SignerId var1) {
      Collection var2 = this.getSigners(var1);
      return var2.size() == 0 ? null : (SignerInformation)var2.iterator().next();
   }

   public int size() {
      return this.all.size();
   }

   public Collection<SignerInformation> getSigners() {
      return new ArrayList<>(this.all);
   }

   public Collection<SignerInformation> getSigners(SignerId var1) {
      if (var1.getIssuer() != null && var1.getSubjectKeyIdentifier() != null) {
         ArrayList var5 = new ArrayList();
         Collection var3 = this.getSigners(new SignerId(var1.getIssuer(), var1.getSerialNumber()));
         if (var3 != null) {
            var5.addAll(var3);
         }

         Collection var4 = this.getSigners(new SignerId(var1.getSubjectKeyIdentifier()));
         if (var4 != null) {
            var5.addAll(var4);
         }

         return var5;
      } else {
         ArrayList var2 = (ArrayList)this.table.get(var1);
         return var2 == null ? new ArrayList<>() : new ArrayList<>(var2);
      }
   }

   @Override
   public Iterator<SignerInformation> iterator() {
      return this.getSigners().iterator();
   }
}
