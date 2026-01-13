package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ERSDirectoryDataGroup extends ERSDataGroup {
   public ERSDirectoryDataGroup(File var1) throws FileNotFoundException {
      super(buildGroup(var1));
   }

   private static List<ERSData> buildGroup(File var0) throws FileNotFoundException {
      if (var0.isDirectory()) {
         File[] var1 = var0.listFiles();
         ArrayList var2 = new ArrayList(var1.length);

         for (int var3 = 0; var3 != var1.length; var3++) {
            if (var1[var3].isDirectory()) {
               if (var1[var3].listFiles().length != 0) {
                  var2.add(new ERSDirectoryDataGroup(var1[var3]));
               }
            } else {
               var2.add(new ERSFileData(var1[var3]));
            }
         }

         return var2;
      } else {
         throw new IllegalArgumentException("file reference does not refer to directory");
      }
   }

   public List<ERSFileData> getFiles() {
      ArrayList var1 = new ArrayList();

      for (int var2 = 0; var2 != this.dataObjects.size(); var2++) {
         if (this.dataObjects.get(var2) instanceof ERSFileData) {
            var1.add((ERSFileData)this.dataObjects.get(var2));
         }
      }

      return var1;
   }

   public List<ERSDirectoryDataGroup> getSubdirectories() {
      ArrayList var1 = new ArrayList();

      for (int var2 = 0; var2 != this.dataObjects.size(); var2++) {
         if (this.dataObjects.get(var2) instanceof ERSDirectoryDataGroup) {
            var1.add((ERSDirectoryDataGroup)this.dataObjects.get(var2));
         }
      }

      return var1;
   }
}
