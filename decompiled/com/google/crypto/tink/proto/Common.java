package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.RuntimeVersion;

public final class Common extends GeneratedFile {
   private static Descriptors.FileDescriptor descriptor;

   private Common() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Common.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0012proto/common.proto\u0012\u0012google.crypto.tink*c\n\u0011EllipticCurveType\u0012\u0011\n\rUNKNOWN_CURVE\u0010\u0000\u0012\r\n\tNIST_P256\u0010\u0002\u0012\r\n\tNIST_P384\u0010\u0003\u0012\r\n\tNIST_P521\u0010\u0004\u0012\u000e\n\nCURVE25519\u0010\u0005*j\n\rEcPointFormat\u0012\u0012\n\u000eUNKNOWN_FORMAT\u0010\u0000\u0012\u0010\n\fUNCOMPRESSED\u0010\u0001\u0012\u000e\n\nCOMPRESSED\u0010\u0002\u0012#\n\u001fDO_NOT_USE_CRUNCHY_UNCOMPRESSED\u0010\u0003*V\n\bHashType\u0012\u0010\n\fUNKNOWN_HASH\u0010\u0000\u0012\b\n\u0004SHA1\u0010\u0001\u0012\n\n\u0006SHA384\u0010\u0002\u0012\n\n\u0006SHA256\u0010\u0003\u0012\n\n\u0006SHA512\u0010\u0004\u0012\n\n\u0006SHA224\u0010\u0005BY\n\u001ccom.google.crypto.tink.protoP\u0001Z7github.com/tink-crypto/tink-go/v2/proto/common_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
