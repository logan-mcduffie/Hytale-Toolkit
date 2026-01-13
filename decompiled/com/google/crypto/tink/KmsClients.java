package com.google.crypto.tink;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

public final class KmsClients {
   private static List<KmsClient> autoClients;
   private static final CopyOnWriteArrayList<KmsClient> clients = new CopyOnWriteArrayList<>();

   public static void add(KmsClient client) {
      clients.add(client);
   }

   public static KmsClient get(String keyUri) throws GeneralSecurityException {
      for (KmsClient client : clients) {
         if (client.doesSupport(keyUri)) {
            return client;
         }
      }

      throw new GeneralSecurityException("No KMS client does support: " + keyUri);
   }

   @Deprecated
   public static synchronized KmsClient getAutoLoaded(String keyUri) throws GeneralSecurityException {
      if (autoClients == null) {
         autoClients = loadAutoKmsClients();
      }

      for (KmsClient client : autoClients) {
         if (client.doesSupport(keyUri)) {
            return client;
         }
      }

      throw new GeneralSecurityException("No KMS client does support: " + keyUri);
   }

   static void reset() {
      clients.clear();
   }

   private static List<KmsClient> loadAutoKmsClients() {
      List<KmsClient> clients = new ArrayList<>();

      for (KmsClient element : ServiceLoader.load(KmsClient.class)) {
         clients.add(element);
      }

      return Collections.unmodifiableList(clients);
   }

   private KmsClients() {
   }
}
