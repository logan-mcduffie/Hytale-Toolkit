package com.google.protobuf;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;

@CheckReturnValue
public final class Descriptors {
   private static final Logger logger = Logger.getLogger(Descriptors.class.getName());
   private static final int[] EMPTY_INT_ARRAY = new int[0];
   private static final Descriptors.Descriptor[] EMPTY_DESCRIPTORS = new Descriptors.Descriptor[0];
   private static final Descriptors.FieldDescriptor[] EMPTY_FIELD_DESCRIPTORS = new Descriptors.FieldDescriptor[0];
   private static final Descriptors.EnumDescriptor[] EMPTY_ENUM_DESCRIPTORS = new Descriptors.EnumDescriptor[0];
   private static final Descriptors.ServiceDescriptor[] EMPTY_SERVICE_DESCRIPTORS = new Descriptors.ServiceDescriptor[0];
   private static final Descriptors.OneofDescriptor[] EMPTY_ONEOF_DESCRIPTORS = new Descriptors.OneofDescriptor[0];
   private static final ConcurrentHashMap<DescriptorProtos.FeatureSet, DescriptorProtos.FeatureSet> FEATURE_CACHE = new ConcurrentHashMap<>();
   private static volatile DescriptorProtos.FeatureSetDefaults javaEditionDefaults = null;

   static void setTestJavaEditionDefaults(DescriptorProtos.FeatureSetDefaults defaults) {
      javaEditionDefaults = defaults;
   }

   static DescriptorProtos.FeatureSetDefaults getJavaEditionDefaults() {
      Descriptors.Descriptor unused1 = DescriptorProtos.FeatureSetDefaults.getDescriptor();
      Descriptors.FileDescriptor unused2 = JavaFeaturesProto.getDescriptor();
      if (javaEditionDefaults == null) {
         synchronized (Descriptors.class) {
            if (javaEditionDefaults == null) {
               try {
                  ExtensionRegistry registry = ExtensionRegistry.newInstance();
                  registry.add(JavaFeaturesProto.java_);
                  setTestJavaEditionDefaults(
                     DescriptorProtos.FeatureSetDefaults.parseFrom(
                        "\n'\u0018\u0084\u0007\"\u0003Ê>\u0000*\u001d\b\u0001\u0010\u0002\u0018\u0002 \u0003(\u00010\u00028\u0002@\u0001Ê>\n\b\u0001\u0010\u0001\u0018\u0000 \u0001(\u0003\n'\u0018ç\u0007\"\u0003Ê>\u0000*\u001d\b\u0002\u0010\u0001\u0018\u0001 \u0002(\u00010\u00018\u0002@\u0001Ê>\n\b\u0000\u0010\u0001\u0018\u0000 \u0001(\u0003\n'\u0018è\u0007\"\u0013\b\u0001\u0010\u0001\u0018\u0001 \u0002(\u00010\u0001Ê>\u0004\b\u0000\u0010\u0001*\r8\u0002@\u0001Ê>\u0006\u0018\u0000 \u0001(\u0003\n'\u0018é\u0007\"\u001b\b\u0001\u0010\u0001\u0018\u0001 \u0002(\u00010\u00018\u0001@\u0002Ê>\b\b\u0000\u0010\u0001\u0018\u0000(\u0001*\u0005Ê>\u0002 \u0000 æ\u0007(é\u0007"
                           .getBytes(Internal.ISO_8859_1),
                        registry
                     )
                  );
               } catch (Exception var5) {
                  throw new AssertionError(var5);
               }
            }
         }
      }

      return javaEditionDefaults;
   }

   static DescriptorProtos.FeatureSet getEditionDefaults(DescriptorProtos.Edition edition) {
      DescriptorProtos.FeatureSetDefaults javaEditionDefaults = getJavaEditionDefaults();
      if (edition.getNumber() < javaEditionDefaults.getMinimumEdition().getNumber()) {
         throw new IllegalArgumentException(
            "Edition " + edition + " is lower than the minimum supported edition " + javaEditionDefaults.getMinimumEdition() + "!"
         );
      } else if (edition.getNumber() > javaEditionDefaults.getMaximumEdition().getNumber()) {
         throw new IllegalArgumentException(
            "Edition " + edition + " is greater than the maximum supported edition " + javaEditionDefaults.getMaximumEdition() + "!"
         );
      } else {
         DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault found = null;

         for (DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault editionDefault : javaEditionDefaults.getDefaultsList()) {
            if (editionDefault.getEdition().getNumber() > edition.getNumber()) {
               break;
            }

            found = editionDefault;
         }

         if (found == null) {
            throw new IllegalArgumentException("Edition " + edition + " does not have a valid default FeatureSet!");
         } else {
            return found.getFixedFeatures().toBuilder().mergeFrom(found.getOverridableFeatures()).build();
         }
      }
   }

   private static DescriptorProtos.FeatureSet internFeatures(DescriptorProtos.FeatureSet features) {
      DescriptorProtos.FeatureSet cached = FEATURE_CACHE.putIfAbsent(features, features);
      return cached == null ? features : cached;
   }

   private static String computeFullName(final Descriptors.FileDescriptor file, final Descriptors.Descriptor parent, final String name) {
      if (parent != null) {
         return parent.getFullName() + '.' + name;
      } else {
         String packageName = file.getPackage();
         return !packageName.isEmpty() ? packageName + '.' + name : name;
      }
   }

   private static <T> T binarySearch(T[] array, int size, ToIntFunction<T> getter, int number) {
      int left = 0;
      int right = size - 1;

      while (left <= right) {
         int mid = (left + right) / 2;
         T midValue = array[mid];
         int midValueNumber = getter.applyAsInt(midValue);
         if (number < midValueNumber) {
            right = mid - 1;
         } else {
            if (number <= midValueNumber) {
               return midValue;
            }

            left = mid + 1;
         }
      }

      return null;
   }

   public static final class Descriptor extends Descriptors.GenericDescriptor {
      private final int index;
      private DescriptorProtos.DescriptorProto proto;
      private volatile DescriptorProtos.MessageOptions options;
      private final String fullName;
      private final Descriptors.GenericDescriptor parent;
      private final Descriptors.Descriptor[] nestedTypes;
      private final Descriptors.EnumDescriptor[] enumTypes;
      private final Descriptors.FieldDescriptor[] fields;
      private final Descriptors.FieldDescriptor[] fieldsSortedByNumber;
      private final Descriptors.FieldDescriptor[] extensions;
      private final Descriptors.OneofDescriptor[] oneofs;
      private final int realOneofCount;
      private final int[] extensionRangeLowerBounds;
      private final int[] extensionRangeUpperBounds;
      private final boolean placeholder;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.DescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.parent.getFile();
      }

      @Override
      Descriptors.GenericDescriptor getParent() {
         return this.parent;
      }

      public boolean isPlaceholder() {
         return this.placeholder;
      }

      public Descriptors.Descriptor getContainingType() {
         return this.parent instanceof Descriptors.Descriptor ? (Descriptors.Descriptor)this.parent : null;
      }

      public DescriptorProtos.MessageOptions getOptions() {
         if (this.options == null) {
            DescriptorProtos.MessageOptions strippedOptions = this.proto.getOptions();
            if (strippedOptions.hasFeatures()) {
               strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
            }

            synchronized (this) {
               if (this.options == null) {
                  this.options = strippedOptions;
               }
            }
         }

         return this.options;
      }

      public List<Descriptors.FieldDescriptor> getFields() {
         return Collections.unmodifiableList(Arrays.asList(this.fields));
      }

      public int getFieldCount() {
         return this.fields.length;
      }

      public Descriptors.FieldDescriptor getField(int index) {
         return this.fields[index];
      }

      public List<Descriptors.OneofDescriptor> getOneofs() {
         return Collections.unmodifiableList(Arrays.asList(this.oneofs));
      }

      public int getOneofCount() {
         return this.oneofs.length;
      }

      public Descriptors.OneofDescriptor getOneof(int index) {
         return this.oneofs[index];
      }

      public List<Descriptors.OneofDescriptor> getRealOneofs() {
         return Collections.unmodifiableList(Arrays.asList(this.oneofs).subList(0, this.realOneofCount));
      }

      public int getRealOneofCount() {
         return this.realOneofCount;
      }

      public Descriptors.OneofDescriptor getRealOneof(int index) {
         if (index >= this.realOneofCount) {
            throw new ArrayIndexOutOfBoundsException(index);
         } else {
            return this.oneofs[index];
         }
      }

      public List<Descriptors.FieldDescriptor> getExtensions() {
         return Collections.unmodifiableList(Arrays.asList(this.extensions));
      }

      public int getExtensionCount() {
         return this.extensions.length;
      }

      public Descriptors.FieldDescriptor getExtension(int index) {
         return this.extensions[index];
      }

      public List<Descriptors.Descriptor> getNestedTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.nestedTypes));
      }

      public int getNestedTypeCount() {
         return this.nestedTypes.length;
      }

      public Descriptors.Descriptor getNestedType(int index) {
         return this.nestedTypes[index];
      }

      public List<Descriptors.EnumDescriptor> getEnumTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
      }

      public int getEnumTypeCount() {
         return this.enumTypes.length;
      }

      public Descriptors.EnumDescriptor getEnumType(int index) {
         return this.enumTypes[index];
      }

      public boolean isExtensionNumber(final int number) {
         int index = Arrays.binarySearch(this.extensionRangeLowerBounds, number);
         if (index < 0) {
            index = ~index - 1;
         }

         return index >= 0 && number < this.extensionRangeUpperBounds[index];
      }

      public boolean isReservedNumber(final int number) {
         for (DescriptorProtos.DescriptorProto.ReservedRange range : this.proto.getReservedRangeList()) {
            if (range.getStart() <= number && number < range.getEnd()) {
               return true;
            }
         }

         return false;
      }

      public boolean isReservedName(final String name) {
         Internal.checkNotNull(name);

         for (String reservedName : this.proto.getReservedNameList()) {
            if (reservedName.equals(name)) {
               return true;
            }
         }

         return false;
      }

      public boolean isExtendable() {
         return !this.proto.getExtensionRangeList().isEmpty();
      }

      public Descriptors.FieldDescriptor findFieldByName(final String name) {
         Descriptors.GenericDescriptor result = this.getFile().tables.findSymbol(this.fullName + '.' + name);
         return result instanceof Descriptors.FieldDescriptor ? (Descriptors.FieldDescriptor)result : null;
      }

      public Descriptors.FieldDescriptor findFieldByNumber(final int number) {
         return Descriptors.binarySearch(this.fieldsSortedByNumber, this.fieldsSortedByNumber.length, Descriptors.FieldDescriptor.NUMBER_GETTER, number);
      }

      public Descriptors.Descriptor findNestedTypeByName(final String name) {
         Descriptors.GenericDescriptor result = this.getFile().tables.findSymbol(this.fullName + '.' + name);
         return result instanceof Descriptors.Descriptor ? (Descriptors.Descriptor)result : null;
      }

      public Descriptors.EnumDescriptor findEnumTypeByName(final String name) {
         Descriptors.GenericDescriptor result = this.getFile().tables.findSymbol(this.fullName + '.' + name);
         return result instanceof Descriptors.EnumDescriptor ? (Descriptors.EnumDescriptor)result : null;
      }

      Descriptor(final String fullname) throws Descriptors.DescriptorValidationException {
         String name = fullname;
         String packageName = "";
         int pos = fullname.lastIndexOf(46);
         if (pos != -1) {
            name = fullname.substring(pos + 1);
            packageName = fullname.substring(0, pos);
         }

         this.index = 0;
         this.proto = DescriptorProtos.DescriptorProto.newBuilder()
            .setName(name)
            .addExtensionRange(DescriptorProtos.DescriptorProto.ExtensionRange.newBuilder().setStart(1).setEnd(536870912).build())
            .build();
         this.fullName = fullname;
         this.nestedTypes = Descriptors.EMPTY_DESCRIPTORS;
         this.enumTypes = Descriptors.EMPTY_ENUM_DESCRIPTORS;
         this.fields = Descriptors.EMPTY_FIELD_DESCRIPTORS;
         this.fieldsSortedByNumber = Descriptors.EMPTY_FIELD_DESCRIPTORS;
         this.extensions = Descriptors.EMPTY_FIELD_DESCRIPTORS;
         this.oneofs = Descriptors.EMPTY_ONEOF_DESCRIPTORS;
         this.realOneofCount = 0;
         this.parent = new Descriptors.FileDescriptor(packageName, this);
         this.extensionRangeLowerBounds = new int[]{1};
         this.extensionRangeUpperBounds = new int[]{536870912};
         this.placeholder = true;
      }

      private Descriptor(
         final DescriptorProtos.DescriptorProto proto, final Descriptors.FileDescriptor file, final Descriptors.Descriptor parent, final int index
      ) throws Descriptors.DescriptorValidationException {
         if (parent == null) {
            this.parent = file;
         } else {
            this.parent = parent;
         }

         this.index = index;
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
         this.oneofs = proto.getOneofDeclCount() > 0 ? new Descriptors.OneofDescriptor[proto.getOneofDeclCount()] : Descriptors.EMPTY_ONEOF_DESCRIPTORS;

         for (int i = 0; i < proto.getOneofDeclCount(); i++) {
            this.oneofs[i] = new Descriptors.OneofDescriptor(proto.getOneofDecl(i), this, i);
         }

         this.nestedTypes = proto.getNestedTypeCount() > 0 ? new Descriptors.Descriptor[proto.getNestedTypeCount()] : Descriptors.EMPTY_DESCRIPTORS;

         for (int i = 0; i < proto.getNestedTypeCount(); i++) {
            this.nestedTypes[i] = new Descriptors.Descriptor(proto.getNestedType(i), file, this, i);
         }

         this.enumTypes = proto.getEnumTypeCount() > 0 ? new Descriptors.EnumDescriptor[proto.getEnumTypeCount()] : Descriptors.EMPTY_ENUM_DESCRIPTORS;

         for (int i = 0; i < proto.getEnumTypeCount(); i++) {
            this.enumTypes[i] = new Descriptors.EnumDescriptor(proto.getEnumType(i), file, this, i);
         }

         this.fields = proto.getFieldCount() > 0 ? new Descriptors.FieldDescriptor[proto.getFieldCount()] : Descriptors.EMPTY_FIELD_DESCRIPTORS;

         for (int i = 0; i < proto.getFieldCount(); i++) {
            this.fields[i] = new Descriptors.FieldDescriptor(proto.getField(i), file, this, i, false);
         }

         this.fieldsSortedByNumber = proto.getFieldCount() > 0 ? (Descriptors.FieldDescriptor[])this.fields.clone() : Descriptors.EMPTY_FIELD_DESCRIPTORS;
         this.extensions = proto.getExtensionCount() > 0 ? new Descriptors.FieldDescriptor[proto.getExtensionCount()] : Descriptors.EMPTY_FIELD_DESCRIPTORS;

         for (int i = 0; i < proto.getExtensionCount(); i++) {
            this.extensions[i] = new Descriptors.FieldDescriptor(proto.getExtension(i), file, this, i, true);
         }

         for (int i = 0; i < proto.getOneofDeclCount(); i++) {
            this.oneofs[i].fields = new Descriptors.FieldDescriptor[this.oneofs[i].getFieldCount()];
            this.oneofs[i].fieldCount = 0;
         }

         for (int i = 0; i < proto.getFieldCount(); i++) {
            Descriptors.OneofDescriptor oneofDescriptor = this.fields[i].getContainingOneof();
            if (oneofDescriptor != null) {
               oneofDescriptor.fields[oneofDescriptor.fieldCount++] = this.fields[i];
            }
         }

         int syntheticOneofCount = 0;

         for (Descriptors.OneofDescriptor oneof : this.oneofs) {
            if (oneof.isSynthetic()) {
               syntheticOneofCount++;
            } else if (syntheticOneofCount > 0) {
               throw new Descriptors.DescriptorValidationException(this, "Synthetic oneofs must come last.");
            }
         }

         this.realOneofCount = this.oneofs.length - syntheticOneofCount;
         this.placeholder = false;
         file.tables.addSymbol(this);
         if (proto.getExtensionRangeCount() > 0) {
            this.extensionRangeLowerBounds = new int[proto.getExtensionRangeCount()];
            this.extensionRangeUpperBounds = new int[proto.getExtensionRangeCount()];
            int ix = 0;

            for (DescriptorProtos.DescriptorProto.ExtensionRange range : proto.getExtensionRangeList()) {
               this.extensionRangeLowerBounds[ix] = range.getStart();
               this.extensionRangeUpperBounds[ix] = range.getEnd();
               ix++;
            }

            Arrays.sort(this.extensionRangeLowerBounds);
            Arrays.sort(this.extensionRangeUpperBounds);
         } else {
            this.extensionRangeLowerBounds = Descriptors.EMPTY_INT_ARRAY;
            this.extensionRangeUpperBounds = Descriptors.EMPTY_INT_ARRAY;
         }
      }

      private void resolveAllFeatures() throws Descriptors.DescriptorValidationException {
         this.resolveFeatures(this.proto.getOptions().getFeatures());

         for (Descriptors.Descriptor nestedType : this.nestedTypes) {
            nestedType.resolveAllFeatures();
         }

         for (Descriptors.EnumDescriptor enumType : this.enumTypes) {
            enumType.resolveAllFeatures();
         }

         for (Descriptors.OneofDescriptor oneof : this.oneofs) {
            oneof.resolveAllFeatures();
         }

         for (Descriptors.FieldDescriptor field : this.fields) {
            field.resolveAllFeatures();
         }

         for (Descriptors.FieldDescriptor extension : this.extensions) {
            extension.resolveAllFeatures();
         }
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         for (Descriptors.Descriptor nestedType : this.nestedTypes) {
            nestedType.crossLink();
         }

         for (Descriptors.FieldDescriptor field : this.fields) {
            field.crossLink();
         }

         Arrays.sort((Object[])this.fieldsSortedByNumber);
         this.validateNoDuplicateFieldNumbers();

         for (Descriptors.FieldDescriptor extension : this.extensions) {
            extension.crossLink();
         }
      }

      private void validateNoDuplicateFieldNumbers() throws Descriptors.DescriptorValidationException {
         for (int i = 0; i + 1 < this.fieldsSortedByNumber.length; i++) {
            Descriptors.FieldDescriptor old = this.fieldsSortedByNumber[i];
            Descriptors.FieldDescriptor field = this.fieldsSortedByNumber[i + 1];
            if (old.getNumber() == field.getNumber()) {
               throw new Descriptors.DescriptorValidationException(
                  field,
                  "Field number "
                     + field.getNumber()
                     + " has already been used in \""
                     + field.getContainingType().getFullName()
                     + "\" by field \""
                     + old.getName()
                     + "\"."
               );
            }
         }
      }

      private void setProto(final DescriptorProtos.DescriptorProto proto) throws Descriptors.DescriptorValidationException {
         this.proto = proto;
         this.options = null;
         this.resolveFeatures(proto.getOptions().getFeatures());

         for (int i = 0; i < this.nestedTypes.length; i++) {
            this.nestedTypes[i].setProto(proto.getNestedType(i));
         }

         for (int i = 0; i < this.oneofs.length; i++) {
            this.oneofs[i].setProto(proto.getOneofDecl(i));
         }

         for (int i = 0; i < this.enumTypes.length; i++) {
            this.enumTypes[i].setProto(proto.getEnumType(i));
         }

         for (int i = 0; i < this.fields.length; i++) {
            this.fields[i].setProto(proto.getField(i));
         }

         for (int i = 0; i < this.extensions.length; i++) {
            this.extensions[i].setProto(proto.getExtension(i));
         }
      }
   }

   public static class DescriptorValidationException extends Exception {
      private static final long serialVersionUID = 5750205775490483148L;
      private final String name;
      private final Message proto;
      private final String description;

      public String getProblemSymbolName() {
         return this.name;
      }

      public Message getProblemProto() {
         return this.proto;
      }

      public String getDescription() {
         return this.description;
      }

      private DescriptorValidationException(final Descriptors.GenericDescriptor problemDescriptor, final String description) {
         super(problemDescriptor.getFullName() + ": " + description);
         this.name = problemDescriptor.getFullName();
         this.proto = problemDescriptor.toProto();
         this.description = description;
      }

      private DescriptorValidationException(final Descriptors.GenericDescriptor problemDescriptor, final String description, final Throwable cause) {
         this(problemDescriptor, description);
         this.initCause(cause);
      }

      private DescriptorValidationException(final Descriptors.FileDescriptor problemDescriptor, final String description) {
         super(problemDescriptor.getName() + ": " + description);
         this.name = problemDescriptor.getName();
         this.proto = problemDescriptor.toProto();
         this.description = description;
      }
   }

   public static final class EnumDescriptor extends Descriptors.GenericDescriptor implements Internal.EnumLiteMap<Descriptors.EnumValueDescriptor> {
      private final int index;
      private DescriptorProtos.EnumDescriptorProto proto;
      private volatile DescriptorProtos.EnumOptions options;
      private final String fullName;
      private final Descriptors.GenericDescriptor parent;
      private final Descriptors.EnumValueDescriptor[] values;
      private final Descriptors.EnumValueDescriptor[] valuesSortedByNumber;
      private final int distinctNumbers;
      private Map<Integer, WeakReference<Descriptors.EnumValueDescriptor>> unknownValues = null;
      private ReferenceQueue<Descriptors.EnumValueDescriptor> cleanupQueue = null;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.EnumDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.parent.getFile();
      }

      @Override
      Descriptors.GenericDescriptor getParent() {
         return this.parent;
      }

      public boolean isPlaceholder() {
         return false;
      }

      public boolean isClosed() {
         return this.getFeatures().getEnumType() == DescriptorProtos.FeatureSet.EnumType.CLOSED;
      }

      public Descriptors.Descriptor getContainingType() {
         return this.parent instanceof Descriptors.Descriptor ? (Descriptors.Descriptor)this.parent : null;
      }

      public DescriptorProtos.EnumOptions getOptions() {
         if (this.options == null) {
            DescriptorProtos.EnumOptions strippedOptions = this.proto.getOptions();
            if (strippedOptions.hasFeatures()) {
               strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
            }

            synchronized (this) {
               if (this.options == null) {
                  this.options = strippedOptions;
               }
            }
         }

         return this.options;
      }

      public List<Descriptors.EnumValueDescriptor> getValues() {
         return Collections.unmodifiableList(Arrays.asList(this.values));
      }

      public int getValueCount() {
         return this.values.length;
      }

      public Descriptors.EnumValueDescriptor getValue(int index) {
         return this.values[index];
      }

      public boolean isReservedNumber(final int number) {
         for (DescriptorProtos.EnumDescriptorProto.EnumReservedRange range : this.proto.getReservedRangeList()) {
            if (range.getStart() <= number && number <= range.getEnd()) {
               return true;
            }
         }

         return false;
      }

      public boolean isReservedName(final String name) {
         Internal.checkNotNull(name);

         for (String reservedName : this.proto.getReservedNameList()) {
            if (reservedName.equals(name)) {
               return true;
            }
         }

         return false;
      }

      public Descriptors.EnumValueDescriptor findValueByName(final String name) {
         Descriptors.GenericDescriptor result = this.getFile().tables.findSymbol(this.fullName + '.' + name);
         return result instanceof Descriptors.EnumValueDescriptor ? (Descriptors.EnumValueDescriptor)result : null;
      }

      public Descriptors.EnumValueDescriptor findValueByNumber(final int number) {
         return Descriptors.binarySearch(this.valuesSortedByNumber, this.distinctNumbers, Descriptors.EnumValueDescriptor.NUMBER_GETTER, number);
      }

      public Descriptors.EnumValueDescriptor findValueByNumberCreatingIfUnknown(final int number) {
         Descriptors.EnumValueDescriptor result = this.findValueByNumber(number);
         if (result != null) {
            return result;
         } else {
            synchronized (this) {
               if (this.cleanupQueue == null) {
                  this.cleanupQueue = new ReferenceQueue<>();
                  this.unknownValues = new HashMap<>();
               } else {
                  while (true) {
                     Descriptors.EnumDescriptor.UnknownEnumValueReference toClean = (Descriptors.EnumDescriptor.UnknownEnumValueReference)this.cleanupQueue
                        .poll();
                     if (toClean == null) {
                        break;
                     }

                     this.unknownValues.remove(toClean.number);
                  }
               }

               WeakReference<Descriptors.EnumValueDescriptor> reference = this.unknownValues.get(number);
               result = reference == null ? null : reference.get();
               if (result == null) {
                  result = new Descriptors.EnumValueDescriptor(this, number);
                  this.unknownValues.put(number, new Descriptors.EnumDescriptor.UnknownEnumValueReference(number, result));
               }

               return result;
            }
         }
      }

      int getUnknownEnumValueDescriptorCount() {
         return this.unknownValues.size();
      }

      private EnumDescriptor(
         final DescriptorProtos.EnumDescriptorProto proto, final Descriptors.FileDescriptor file, final Descriptors.Descriptor parent, final int index
      ) throws Descriptors.DescriptorValidationException {
         if (parent == null) {
            this.parent = file;
         } else {
            this.parent = parent;
         }

         this.index = index;
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
         if (proto.getValueCount() == 0) {
            throw new Descriptors.DescriptorValidationException(this, "Enums must contain at least one value.");
         } else {
            this.values = new Descriptors.EnumValueDescriptor[proto.getValueCount()];

            for (int i = 0; i < proto.getValueCount(); i++) {
               this.values[i] = new Descriptors.EnumValueDescriptor(proto.getValue(i), this, i);
            }

            this.valuesSortedByNumber = (Descriptors.EnumValueDescriptor[])this.values.clone();
            Arrays.sort(this.valuesSortedByNumber, Descriptors.EnumValueDescriptor.BY_NUMBER);
            int j = 0;

            for (int i = 1; i < proto.getValueCount(); i++) {
               Descriptors.EnumValueDescriptor oldValue = this.valuesSortedByNumber[j];
               Descriptors.EnumValueDescriptor newValue = this.valuesSortedByNumber[i];
               if (oldValue.getNumber() != newValue.getNumber()) {
                  this.valuesSortedByNumber[++j] = newValue;
               }
            }

            this.distinctNumbers = j + 1;
            Arrays.fill(this.valuesSortedByNumber, this.distinctNumbers, proto.getValueCount(), null);
            file.tables.addSymbol(this);
         }
      }

      private void resolveAllFeatures() throws Descriptors.DescriptorValidationException {
         this.resolveFeatures(this.proto.getOptions().getFeatures());

         for (Descriptors.EnumValueDescriptor value : this.values) {
            value.resolveAllFeatures();
         }
      }

      private void setProto(final DescriptorProtos.EnumDescriptorProto proto) throws Descriptors.DescriptorValidationException {
         this.proto = proto;
         this.options = null;
         this.resolveFeatures(proto.getOptions().getFeatures());

         for (int i = 0; i < this.values.length; i++) {
            this.values[i].setProto(proto.getValue(i));
         }
      }

      private static class UnknownEnumValueReference extends WeakReference<Descriptors.EnumValueDescriptor> {
         private final int number;

         private UnknownEnumValueReference(int number, Descriptors.EnumValueDescriptor descriptor) {
            super(descriptor);
            this.number = number;
         }
      }
   }

   public static final class EnumValueDescriptor extends Descriptors.GenericDescriptor implements Internal.EnumLite {
      static final Comparator<Descriptors.EnumValueDescriptor> BY_NUMBER = new Comparator<Descriptors.EnumValueDescriptor>() {
         public int compare(Descriptors.EnumValueDescriptor o1, Descriptors.EnumValueDescriptor o2) {
            return Integer.compare(o1.getNumber(), o2.getNumber());
         }
      };
      static final ToIntFunction<Descriptors.EnumValueDescriptor> NUMBER_GETTER = Descriptors.EnumValueDescriptor::getNumber;
      private final int index;
      private DescriptorProtos.EnumValueDescriptorProto proto;
      private volatile DescriptorProtos.EnumValueOptions options;
      private final String fullName;
      private final Descriptors.EnumDescriptor type;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.EnumValueDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public int getNumber() {
         return this.proto.getNumber();
      }

      @Override
      public String toString() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.type.getFile();
      }

      @Override
      Descriptors.GenericDescriptor getParent() {
         return this.type;
      }

      public Descriptors.EnumDescriptor getType() {
         return this.type;
      }

      public DescriptorProtos.EnumValueOptions getOptions() {
         if (this.options == null) {
            DescriptorProtos.EnumValueOptions strippedOptions = this.proto.getOptions();
            if (strippedOptions.hasFeatures()) {
               strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
            }

            synchronized (this) {
               if (this.options == null) {
                  this.options = strippedOptions;
               }
            }
         }

         return this.options;
      }

      private EnumValueDescriptor(final DescriptorProtos.EnumValueDescriptorProto proto, final Descriptors.EnumDescriptor parent, final int index) throws Descriptors.DescriptorValidationException {
         this.index = index;
         this.proto = proto;
         this.type = parent;
         this.fullName = parent.getFullName() + '.' + proto.getName();
         this.type.getFile().tables.addSymbol(this);
      }

      private EnumValueDescriptor(final Descriptors.EnumDescriptor parent, final Integer number) {
         String name = "UNKNOWN_ENUM_VALUE_" + parent.getName() + "_" + number;
         DescriptorProtos.EnumValueDescriptorProto proto = DescriptorProtos.EnumValueDescriptorProto.newBuilder().setName(name).setNumber(number).build();
         this.index = -1;
         this.proto = proto;
         this.type = parent;
         this.fullName = parent.getFullName() + '.' + proto.getName();
      }

      private void resolveAllFeatures() throws Descriptors.DescriptorValidationException {
         this.resolveFeatures(this.proto.getOptions().getFeatures());
      }

      private void setProto(final DescriptorProtos.EnumValueDescriptorProto proto) throws Descriptors.DescriptorValidationException {
         this.proto = proto;
         this.options = null;
         this.resolveFeatures(proto.getOptions().getFeatures());
      }
   }

   public static final class FieldDescriptor
      extends Descriptors.GenericDescriptor
      implements Comparable<Descriptors.FieldDescriptor>,
      FieldSet.FieldDescriptorLite<Descriptors.FieldDescriptor> {
      private static final ToIntFunction<Descriptors.FieldDescriptor> NUMBER_GETTER = Descriptors.FieldDescriptor::getNumber;
      private static final WireFormat.FieldType[] table = WireFormat.FieldType.values();
      private final int index;
      private DescriptorProtos.FieldDescriptorProto proto;
      private volatile DescriptorProtos.FieldOptions options;
      private final String fullName;
      private String jsonName;
      private final Descriptors.GenericDescriptor parent;
      private final Descriptors.Descriptor extensionScope;
      private final boolean isProto3Optional;
      private volatile Descriptors.FieldDescriptor.RedactionState redactionState;
      private Descriptors.FieldDescriptor.Type type;
      private Descriptors.Descriptor containingType;
      private Descriptors.OneofDescriptor containingOneof;
      private Descriptors.GenericDescriptor typeDescriptor;
      private Object defaultValue;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.FieldDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public int getNumber() {
         return this.proto.getNumber();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      public String getJsonName() {
         String result = this.jsonName;
         if (result != null) {
            return result;
         } else {
            return this.proto.hasJsonName() ? (this.jsonName = this.proto.getJsonName()) : (this.jsonName = fieldNameToJsonName(this.proto.getName()));
         }
      }

      public Descriptors.FieldDescriptor.JavaType getJavaType() {
         return this.getType().getJavaType();
      }

      @Override
      public WireFormat.JavaType getLiteJavaType() {
         return this.getLiteType().getJavaType();
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.parent.getFile();
      }

      @Override
      Descriptors.GenericDescriptor getParent() {
         return this.parent;
      }

      public Descriptors.FieldDescriptor.Type getType() {
         return this.type == Descriptors.FieldDescriptor.Type.MESSAGE
               && (this.typeDescriptor == null || !((Descriptors.Descriptor)this.typeDescriptor).toProto().getOptions().getMapEntry())
               && (this.containingType == null || !this.containingType.toProto().getOptions().getMapEntry())
               && this.features != null
               && this.getFeatures().getMessageEncoding() == DescriptorProtos.FeatureSet.MessageEncoding.DELIMITED
            ? Descriptors.FieldDescriptor.Type.GROUP
            : this.type;
      }

      @Override
      public WireFormat.FieldType getLiteType() {
         return table[this.getType().ordinal()];
      }

      public boolean needsUtf8Check() {
         if (this.getType() != Descriptors.FieldDescriptor.Type.STRING) {
            return false;
         } else if (this.getContainingType().toProto().getOptions().getMapEntry()) {
            return true;
         } else {
            return this.getFeatures().getExtension(JavaFeaturesProto.java_).getUtf8Validation().equals(JavaFeaturesProto.JavaFeatures.Utf8Validation.VERIFY)
               ? true
               : this.getFeatures().getUtf8Validation().equals(DescriptorProtos.FeatureSet.Utf8Validation.VERIFY);
         }
      }

      public boolean isMapField() {
         return this.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && this.isRepeated() && this.getMessageType().toProto().getOptions().getMapEntry();
      }

      public boolean isRequired() {
         return this.getFeatures().getFieldPresence() == DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED;
      }

      @Deprecated
      public boolean isOptional() {
         return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL
            && this.getFeatures().getFieldPresence() != DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED;
      }

      @Override
      public boolean isRepeated() {
         return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
      }

      @Override
      public boolean isPacked() {
         return !this.isPackable() ? false : this.getFeatures().getRepeatedFieldEncoding().equals(DescriptorProtos.FeatureSet.RepeatedFieldEncoding.PACKED);
      }

      public boolean isPackable() {
         return this.isRepeated() && this.getLiteType().isPackable();
      }

      public boolean hasDefaultValue() {
         return this.proto.hasDefaultValue();
      }

      public Object getDefaultValue() {
         if (this.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new UnsupportedOperationException("FieldDescriptor.getDefaultValue() called on an embedded message field.");
         } else {
            return this.defaultValue;
         }
      }

      public DescriptorProtos.FieldOptions getOptions() {
         if (this.options == null) {
            DescriptorProtos.FieldOptions strippedOptions = this.proto.getOptions();
            if (strippedOptions.hasFeatures()) {
               strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
            }

            synchronized (this) {
               if (this.options == null) {
                  this.options = strippedOptions;
               }
            }
         }

         return this.options;
      }

      public boolean isExtension() {
         return this.proto.hasExtendee();
      }

      public Descriptors.Descriptor getContainingType() {
         return this.containingType;
      }

      public Descriptors.OneofDescriptor getContainingOneof() {
         return this.containingOneof;
      }

      public Descriptors.OneofDescriptor getRealContainingOneof() {
         return this.containingOneof != null && !this.containingOneof.isSynthetic() ? this.containingOneof : null;
      }

      boolean hasOptionalKeyword() {
         return this.isProto3Optional
            || this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO2
               && !this.isRequired()
               && !this.isRepeated()
               && this.getContainingOneof() == null;
      }

      public boolean hasPresence() {
         return this.isRepeated()
            ? false
            : this.isProto3Optional
               || this.getType() == Descriptors.FieldDescriptor.Type.MESSAGE
               || this.getType() == Descriptors.FieldDescriptor.Type.GROUP
               || this.isExtension()
               || this.getContainingOneof() != null
               || this.getFeatures().getFieldPresence() != DescriptorProtos.FeatureSet.FieldPresence.IMPLICIT;
      }

      boolean isGroupLike() {
         if (this.getType() != Descriptors.FieldDescriptor.Type.GROUP) {
            return false;
         } else if (!this.getMessageType().getName().toLowerCase().equals(this.getName())) {
            return false;
         } else if (this.getMessageType().getFile() != this.getFile()) {
            return false;
         } else {
            return this.isExtension()
               ? this.getMessageType().getContainingType() == this.getExtensionScope()
               : this.getMessageType().getContainingType() == this.getContainingType();
         }
      }

      public Descriptors.Descriptor getExtensionScope() {
         if (!this.isExtension()) {
            throw new UnsupportedOperationException(String.format("This field is not an extension. (%s)", this.fullName));
         } else {
            return this.extensionScope;
         }
      }

      public Descriptors.Descriptor getMessageType() {
         if (this.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new UnsupportedOperationException(String.format("This field is not of message type. (%s)", this.fullName));
         } else {
            return (Descriptors.Descriptor)this.typeDescriptor;
         }
      }

      public Descriptors.EnumDescriptor getEnumType() {
         if (this.getJavaType() != Descriptors.FieldDescriptor.JavaType.ENUM) {
            throw new UnsupportedOperationException(String.format("This field is not of enum type. (%s)", this.fullName));
         } else {
            return (Descriptors.EnumDescriptor)this.typeDescriptor;
         }
      }

      public boolean legacyEnumFieldTreatedAsClosed() {
         return this.getFile().getDependencies().isEmpty()
            ? this.getType() == Descriptors.FieldDescriptor.Type.ENUM && this.getEnumType().isClosed()
            : this.getType() == Descriptors.FieldDescriptor.Type.ENUM
               && (this.getFeatures().getExtension(JavaFeaturesProto.java_).getLegacyClosedEnum() || this.getEnumType().isClosed());
      }

      public int compareTo(final Descriptors.FieldDescriptor other) {
         if (other.containingType != this.containingType) {
            throw new IllegalArgumentException("FieldDescriptors can only be compared to other FieldDescriptors for fields of the same message type.");
         } else {
            return this.getNumber() - other.getNumber();
         }
      }

      @Override
      public String toString() {
         return this.getFullName();
      }

      private static String fieldNameToJsonName(String name) {
         int length = name.length();
         StringBuilder result = new StringBuilder(length);
         boolean isNextUpperCase = false;

         for (int i = 0; i < length; i++) {
            char ch = name.charAt(i);
            if (ch == '_') {
               isNextUpperCase = true;
            } else if (isNextUpperCase) {
               if ('a' <= ch && ch <= 'z') {
                  ch = (char)(ch - 'a' + 65);
               }

               result.append(ch);
               isNextUpperCase = false;
            } else {
               result.append(ch);
            }
         }

         return result.toString();
      }

      private FieldDescriptor(
         final DescriptorProtos.FieldDescriptorProto proto,
         final Descriptors.FileDescriptor file,
         final Descriptors.Descriptor parent,
         final int index,
         final boolean isExtension
      ) throws Descriptors.DescriptorValidationException {
         this.index = index;
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
         if (proto.hasType()) {
            this.type = Descriptors.FieldDescriptor.Type.valueOf(proto.getType());
         }

         this.isProto3Optional = proto.getProto3Optional();
         if (this.getNumber() <= 0) {
            throw new Descriptors.DescriptorValidationException(this, "Field numbers must be positive integers.");
         } else {
            if (isExtension) {
               if (!proto.hasExtendee()) {
                  throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.extendee not set for extension field.");
               }

               this.containingType = null;
               if (parent != null) {
                  this.extensionScope = parent;
                  this.parent = parent;
               } else {
                  this.extensionScope = null;
                  this.parent = Internal.checkNotNull(file);
               }

               if (proto.hasOneofIndex()) {
                  throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.oneof_index set for extension field.");
               }

               this.containingOneof = null;
            } else {
               if (proto.hasExtendee()) {
                  throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.extendee set for non-extension field.");
               }

               this.containingType = parent;
               if (proto.hasOneofIndex()) {
                  if (proto.getOneofIndex() < 0 || proto.getOneofIndex() >= parent.toProto().getOneofDeclCount()) {
                     throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.oneof_index is out of range for type " + parent.getName());
                  }

                  this.containingOneof = parent.getOneofs().get(proto.getOneofIndex());
                  this.containingOneof.fieldCount++;
                  this.parent = Internal.checkNotNull(this.containingOneof);
               } else {
                  this.containingOneof = null;
                  this.parent = Internal.checkNotNull(parent);
               }

               this.extensionScope = null;
            }

            file.tables.addSymbol(this);
         }
      }

      private static Descriptors.FieldDescriptor.RedactionState isOptionSensitive(Descriptors.FieldDescriptor field, Object value) {
         if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            if (field.isRepeated()) {
               for (Descriptors.EnumValueDescriptor v : (List)value) {
                  if (v.getOptions().getDebugRedact()) {
                     return Descriptors.FieldDescriptor.RedactionState.of(true, false);
                  }
               }
            } else if (((Descriptors.EnumValueDescriptor)value).getOptions().getDebugRedact()) {
               return Descriptors.FieldDescriptor.RedactionState.of(true, false);
            }
         } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            if (field.isRepeated()) {
               for (Message m : (List)value) {
                  for (Entry<Descriptors.FieldDescriptor, Object> entry : m.getAllFields().entrySet()) {
                     Descriptors.FieldDescriptor.RedactionState state = isOptionSensitive(entry.getKey(), entry.getValue());
                     if (state.redact) {
                        return state;
                     }
                  }
               }
            } else {
               for (Entry<Descriptors.FieldDescriptor, Object> entryx : ((Message)value).getAllFields().entrySet()) {
                  Descriptors.FieldDescriptor.RedactionState state = isOptionSensitive(entryx.getKey(), entryx.getValue());
                  if (state.redact) {
                     return state;
                  }
               }
            }
         }

         return Descriptors.FieldDescriptor.RedactionState.of(false);
      }

      Descriptors.FieldDescriptor.RedactionState getRedactionState() {
         Descriptors.FieldDescriptor.RedactionState state = this.redactionState;
         if (state == null) {
            synchronized (this) {
               state = this.redactionState;
               if (state == null) {
                  DescriptorProtos.FieldOptions options = this.getOptions();
                  state = Descriptors.FieldDescriptor.RedactionState.of(options.getDebugRedact());

                  for (Entry<Descriptors.FieldDescriptor, Object> entry : options.getAllFields().entrySet()) {
                     state = Descriptors.FieldDescriptor.RedactionState.combine(state, isOptionSensitive(entry.getKey(), entry.getValue()));
                     if (state.redact) {
                        break;
                     }
                  }

                  this.redactionState = state;
               }
            }
         }

         return state;
      }

      private void resolveAllFeatures() throws Descriptors.DescriptorValidationException {
         this.resolveFeatures(this.proto.getOptions().getFeatures());
      }

      @Override
      DescriptorProtos.FeatureSet inferLegacyProtoFeatures() {
         if (this.getFile().getEdition().getNumber() >= DescriptorProtos.Edition.EDITION_2023.getNumber()) {
            return DescriptorProtos.FeatureSet.getDefaultInstance();
         } else {
            DescriptorProtos.FeatureSet.Builder features = null;
            if (this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED) {
               features = DescriptorProtos.FeatureSet.newBuilder();
               features.setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED);
            }

            if (this.proto.getType() == DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP) {
               if (features == null) {
                  features = DescriptorProtos.FeatureSet.newBuilder();
               }

               features.setMessageEncoding(DescriptorProtos.FeatureSet.MessageEncoding.DELIMITED);
            }

            if (this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO2 && this.proto.getOptions().getPacked()) {
               if (features == null) {
                  features = DescriptorProtos.FeatureSet.newBuilder();
               }

               features.setRepeatedFieldEncoding(DescriptorProtos.FeatureSet.RepeatedFieldEncoding.PACKED);
            }

            if (this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO3
               && this.proto.getOptions().hasPacked()
               && !this.proto.getOptions().getPacked()) {
               if (features == null) {
                  features = DescriptorProtos.FeatureSet.newBuilder();
               }

               features.setRepeatedFieldEncoding(DescriptorProtos.FeatureSet.RepeatedFieldEncoding.EXPANDED);
            }

            return features != null ? features.build() : DescriptorProtos.FeatureSet.getDefaultInstance();
         }
      }

      @Override
      void validateFeatures() throws Descriptors.DescriptorValidationException {
         if (this.containingType != null
            && this.containingType.toProto().getOptions().getMessageSetWireFormat()
            && this.isExtension()
            && (this.isRequired() || this.isRepeated() || this.getType() != Descriptors.FieldDescriptor.Type.MESSAGE)) {
            throw new Descriptors.DescriptorValidationException(this, "Extensions of MessageSets may not be required or repeated messages.");
         }
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         if (this.proto.hasExtendee()) {
            Descriptors.GenericDescriptor extendee = this.getFile()
               .tables
               .lookupSymbol(this.proto.getExtendee(), this, Descriptors.FileDescriptorTables.SearchFilter.TYPES_ONLY);
            if (!(extendee instanceof Descriptors.Descriptor)) {
               throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getExtendee() + "\" is not a message type.");
            }

            this.containingType = (Descriptors.Descriptor)extendee;
            if (!this.getContainingType().isExtensionNumber(this.getNumber())) {
               throw new Descriptors.DescriptorValidationException(
                  this, '"' + this.getContainingType().getFullName() + "\" does not declare " + this.getNumber() + " as an extension number."
               );
            }
         }

         if (this.proto.hasTypeName()) {
            Descriptors.GenericDescriptor typeDescriptor = this.getFile()
               .tables
               .lookupSymbol(this.proto.getTypeName(), this, Descriptors.FileDescriptorTables.SearchFilter.TYPES_ONLY);
            if (!this.proto.hasType()) {
               if (typeDescriptor instanceof Descriptors.Descriptor) {
                  this.type = Descriptors.FieldDescriptor.Type.MESSAGE;
               } else {
                  if (!(typeDescriptor instanceof Descriptors.EnumDescriptor)) {
                     throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not a type.");
                  }

                  this.type = Descriptors.FieldDescriptor.Type.ENUM;
               }
            }

            if (this.type.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               if (!(typeDescriptor instanceof Descriptors.Descriptor)) {
                  throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not a message type.");
               }

               this.typeDescriptor = typeDescriptor;
               if (this.proto.hasDefaultValue()) {
                  throw new Descriptors.DescriptorValidationException(this, "Messages can't have default values.");
               }
            } else {
               if (this.type.getJavaType() != Descriptors.FieldDescriptor.JavaType.ENUM) {
                  throw new Descriptors.DescriptorValidationException(this, "Field with primitive type has type_name.");
               }

               if (!(typeDescriptor instanceof Descriptors.EnumDescriptor)) {
                  throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not an enum type.");
               }

               this.typeDescriptor = typeDescriptor;
            }
         } else if (this.type.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE
            || this.type.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
            throw new Descriptors.DescriptorValidationException(this, "Field with message or enum type missing type_name.");
         }

         if (this.proto.getOptions().getPacked() && !this.isPackable()) {
            throw new Descriptors.DescriptorValidationException(this, "[packed = true] can only be specified for repeated primitive fields.");
         } else {
            if (this.proto.hasDefaultValue()) {
               if (this.isRepeated()) {
                  throw new Descriptors.DescriptorValidationException(this, "Repeated fields cannot have default values.");
               }

               try {
                  switch (this.type) {
                     case DOUBLE:
                        if (this.proto.getDefaultValue().equals("inf")) {
                           this.defaultValue = Double.POSITIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("-inf")) {
                           this.defaultValue = Double.NEGATIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("nan")) {
                           this.defaultValue = Double.NaN;
                        } else {
                           this.defaultValue = Double.valueOf(this.proto.getDefaultValue());
                        }
                        break;
                     case FLOAT:
                        if (this.proto.getDefaultValue().equals("inf")) {
                           this.defaultValue = Float.POSITIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("-inf")) {
                           this.defaultValue = Float.NEGATIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("nan")) {
                           this.defaultValue = Float.NaN;
                        } else {
                           this.defaultValue = Float.valueOf(this.proto.getDefaultValue());
                        }
                        break;
                     case INT64:
                     case SFIXED64:
                     case SINT64:
                        this.defaultValue = TextFormat.parseInt64(this.proto.getDefaultValue());
                        break;
                     case UINT64:
                     case FIXED64:
                        this.defaultValue = TextFormat.parseUInt64(this.proto.getDefaultValue());
                        break;
                     case INT32:
                     case SFIXED32:
                     case SINT32:
                        this.defaultValue = TextFormat.parseInt32(this.proto.getDefaultValue());
                        break;
                     case FIXED32:
                     case UINT32:
                        this.defaultValue = TextFormat.parseUInt32(this.proto.getDefaultValue());
                        break;
                     case BOOL:
                        this.defaultValue = Boolean.valueOf(this.proto.getDefaultValue());
                        break;
                     case STRING:
                        this.defaultValue = this.proto.getDefaultValue();
                        break;
                     case GROUP:
                     case MESSAGE:
                        throw new Descriptors.DescriptorValidationException(this, "Message type had default value.");
                     case BYTES:
                        try {
                           this.defaultValue = TextFormat.unescapeBytes(this.proto.getDefaultValue());
                           break;
                        } catch (TextFormat.InvalidEscapeSequenceException var2) {
                           throw new Descriptors.DescriptorValidationException(this, "Couldn't parse default value: " + var2.getMessage(), var2);
                        }
                     case ENUM:
                        this.defaultValue = this.getEnumType().findValueByName(this.proto.getDefaultValue());
                        if (this.defaultValue == null) {
                           throw new Descriptors.DescriptorValidationException(this, "Unknown enum default value: \"" + this.proto.getDefaultValue() + '"');
                        }
                  }
               } catch (NumberFormatException var3) {
                  throw new Descriptors.DescriptorValidationException(this, "Could not parse default value: \"" + this.proto.getDefaultValue() + '"', var3);
               }
            } else if (this.isRepeated()) {
               this.defaultValue = Collections.emptyList();
            } else {
               switch (this.type.getJavaType()) {
                  case ENUM:
                     this.defaultValue = this.getEnumType().getValue(0);
                     break;
                  case MESSAGE:
                     this.defaultValue = null;
                     break;
                  default:
                     this.defaultValue = this.type.getJavaType().defaultDefault;
               }
            }
         }
      }

      private void setProto(final DescriptorProtos.FieldDescriptorProto proto) throws Descriptors.DescriptorValidationException {
         this.proto = proto;
         this.options = null;
         this.resolveFeatures(proto.getOptions().getFeatures());
      }

      @Override
      public boolean internalMessageIsImmutable(Object message) {
         return message instanceof MessageLite;
      }

      @Override
      public void internalMergeFrom(Object to, Object from) {
         ((Message.Builder)to).mergeFrom((Message)from);
      }

      static {
         if (Descriptors.FieldDescriptor.Type.types.length != DescriptorProtos.FieldDescriptorProto.Type.values().length) {
            throw new RuntimeException("descriptor.proto has a new declared type but Descriptors.java wasn't updated.");
         }
      }

      public static enum JavaType {
         INT(0),
         LONG(0L),
         FLOAT(0.0F),
         DOUBLE(0.0),
         BOOLEAN(false),
         STRING(""),
         BYTE_STRING(ByteString.EMPTY),
         ENUM(null),
         MESSAGE(null);

         private final Object defaultDefault;

         private JavaType(final Object defaultDefault) {
            this.defaultDefault = defaultDefault;
         }
      }

      static final class RedactionState {
         private static final Descriptors.FieldDescriptor.RedactionState FALSE_FALSE = new Descriptors.FieldDescriptor.RedactionState(false, false);
         private static final Descriptors.FieldDescriptor.RedactionState FALSE_TRUE = new Descriptors.FieldDescriptor.RedactionState(false, true);
         private static final Descriptors.FieldDescriptor.RedactionState TRUE_FALSE = new Descriptors.FieldDescriptor.RedactionState(true, false);
         private static final Descriptors.FieldDescriptor.RedactionState TRUE_TRUE = new Descriptors.FieldDescriptor.RedactionState(true, true);
         final boolean redact;
         final boolean report;

         private RedactionState(boolean redact, boolean report) {
            this.redact = redact;
            this.report = report;
         }

         private static Descriptors.FieldDescriptor.RedactionState of(boolean redact) {
            return of(redact, false);
         }

         private static Descriptors.FieldDescriptor.RedactionState of(boolean redact, boolean report) {
            if (redact) {
               return report ? TRUE_TRUE : TRUE_FALSE;
            } else {
               return report ? FALSE_TRUE : FALSE_FALSE;
            }
         }

         private static Descriptors.FieldDescriptor.RedactionState combine(
            Descriptors.FieldDescriptor.RedactionState lhs, Descriptors.FieldDescriptor.RedactionState rhs
         ) {
            return of(lhs.redact || rhs.redact, rhs.report);
         }
      }

      public static enum Type {
         DOUBLE(Descriptors.FieldDescriptor.JavaType.DOUBLE),
         FLOAT(Descriptors.FieldDescriptor.JavaType.FLOAT),
         INT64(Descriptors.FieldDescriptor.JavaType.LONG),
         UINT64(Descriptors.FieldDescriptor.JavaType.LONG),
         INT32(Descriptors.FieldDescriptor.JavaType.INT),
         FIXED64(Descriptors.FieldDescriptor.JavaType.LONG),
         FIXED32(Descriptors.FieldDescriptor.JavaType.INT),
         BOOL(Descriptors.FieldDescriptor.JavaType.BOOLEAN),
         STRING(Descriptors.FieldDescriptor.JavaType.STRING),
         GROUP(Descriptors.FieldDescriptor.JavaType.MESSAGE),
         MESSAGE(Descriptors.FieldDescriptor.JavaType.MESSAGE),
         BYTES(Descriptors.FieldDescriptor.JavaType.BYTE_STRING),
         UINT32(Descriptors.FieldDescriptor.JavaType.INT),
         ENUM(Descriptors.FieldDescriptor.JavaType.ENUM),
         SFIXED32(Descriptors.FieldDescriptor.JavaType.INT),
         SFIXED64(Descriptors.FieldDescriptor.JavaType.LONG),
         SINT32(Descriptors.FieldDescriptor.JavaType.INT),
         SINT64(Descriptors.FieldDescriptor.JavaType.LONG);

         private static final Descriptors.FieldDescriptor.Type[] types = values();
         private final Descriptors.FieldDescriptor.JavaType javaType;

         private Type(Descriptors.FieldDescriptor.JavaType javaType) {
            this.javaType = javaType;
         }

         public DescriptorProtos.FieldDescriptorProto.Type toProto() {
            return DescriptorProtos.FieldDescriptorProto.Type.forNumber(this.ordinal() + 1);
         }

         public Descriptors.FieldDescriptor.JavaType getJavaType() {
            return this.javaType;
         }

         public static Descriptors.FieldDescriptor.Type valueOf(final DescriptorProtos.FieldDescriptorProto.Type type) {
            return types[type.getNumber() - 1];
         }
      }
   }

   public static final class FileDescriptor extends Descriptors.GenericDescriptor {
      private DescriptorProtos.FileDescriptorProto proto;
      private volatile DescriptorProtos.FileOptions options;
      private final Descriptors.Descriptor[] messageTypes;
      private final Descriptors.EnumDescriptor[] enumTypes;
      private final Descriptors.ServiceDescriptor[] services;
      private final Descriptors.FieldDescriptor[] extensions;
      private final Descriptors.FileDescriptor[] dependencies;
      private final Descriptors.FileDescriptor[] publicDependencies;
      private final Descriptors.FileDescriptorTables tables;
      private final boolean placeholder;
      private volatile boolean featuresResolved;

      public DescriptorProtos.FileDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this;
      }

      @Override
      Descriptors.GenericDescriptor getParent() {
         return null;
      }

      public boolean isPlaceholder() {
         return this.placeholder;
      }

      @Override
      public String getFullName() {
         return this.proto.getName();
      }

      public String getPackage() {
         return this.proto.getPackage();
      }

      public DescriptorProtos.FileOptions getOptions() {
         if (this.options == null) {
            DescriptorProtos.FileOptions strippedOptions = this.proto.getOptions();
            if (strippedOptions.hasFeatures()) {
               strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
            }

            synchronized (this) {
               if (this.options == null) {
                  this.options = strippedOptions;
               }
            }
         }

         return this.options;
      }

      public List<Descriptors.Descriptor> getMessageTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.messageTypes));
      }

      public int getMessageTypeCount() {
         return this.messageTypes.length;
      }

      public Descriptors.Descriptor getMessageType(int index) {
         return this.messageTypes[index];
      }

      public List<Descriptors.EnumDescriptor> getEnumTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
      }

      public int getEnumTypeCount() {
         return this.enumTypes.length;
      }

      public Descriptors.EnumDescriptor getEnumType(int index) {
         return this.enumTypes[index];
      }

      public List<Descriptors.ServiceDescriptor> getServices() {
         return Collections.unmodifiableList(Arrays.asList(this.services));
      }

      public int getServiceCount() {
         return this.services.length;
      }

      public Descriptors.ServiceDescriptor getService(int index) {
         return this.services[index];
      }

      public List<Descriptors.FieldDescriptor> getExtensions() {
         return Collections.unmodifiableList(Arrays.asList(this.extensions));
      }

      public int getExtensionCount() {
         return this.extensions.length;
      }

      public Descriptors.FieldDescriptor getExtension(int index) {
         return this.extensions[index];
      }

      public List<Descriptors.FileDescriptor> getDependencies() {
         return Collections.unmodifiableList(Arrays.asList(this.dependencies));
      }

      public List<Descriptors.FileDescriptor> getPublicDependencies() {
         return Collections.unmodifiableList(Arrays.asList(this.publicDependencies));
      }

      DescriptorProtos.Edition getEdition() {
         String var1 = this.proto.getSyntax();
         switch (var1) {
            case "editions":
               return this.proto.getEdition();
            case "proto3":
               return DescriptorProtos.Edition.EDITION_PROTO3;
            default:
               return DescriptorProtos.Edition.EDITION_PROTO2;
         }
      }

      public void copyHeadingTo(DescriptorProtos.FileDescriptorProto.Builder protoBuilder) {
         protoBuilder.setName(this.getName()).setSyntax(this.proto.getSyntax());
         if (!this.getPackage().isEmpty()) {
            protoBuilder.setPackage(this.getPackage());
         }

         if (this.proto.getSyntax().equals("editions")) {
            protoBuilder.setEdition(this.proto.getEdition());
         }

         if (this.proto.hasOptions() && !this.proto.getOptions().equals(DescriptorProtos.FileOptions.getDefaultInstance())) {
            protoBuilder.setOptions(this.proto.getOptions());
         }
      }

      public Descriptors.Descriptor findMessageTypeByName(String name) {
         if (name.indexOf(46) != -1) {
            return null;
         } else {
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
               name = packageName + '.' + name;
            }

            Descriptors.GenericDescriptor result = this.tables.findSymbol(name);
            return result instanceof Descriptors.Descriptor && result.getFile() == this ? (Descriptors.Descriptor)result : null;
         }
      }

      public Descriptors.EnumDescriptor findEnumTypeByName(String name) {
         if (name.indexOf(46) != -1) {
            return null;
         } else {
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
               name = packageName + '.' + name;
            }

            Descriptors.GenericDescriptor result = this.tables.findSymbol(name);
            return result instanceof Descriptors.EnumDescriptor && result.getFile() == this ? (Descriptors.EnumDescriptor)result : null;
         }
      }

      public Descriptors.ServiceDescriptor findServiceByName(String name) {
         if (name.indexOf(46) != -1) {
            return null;
         } else {
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
               name = packageName + '.' + name;
            }

            Descriptors.GenericDescriptor result = this.tables.findSymbol(name);
            return result instanceof Descriptors.ServiceDescriptor && result.getFile() == this ? (Descriptors.ServiceDescriptor)result : null;
         }
      }

      public Descriptors.FieldDescriptor findExtensionByName(String name) {
         if (name.indexOf(46) != -1) {
            return null;
         } else {
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
               name = packageName + '.' + name;
            }

            Descriptors.GenericDescriptor result = this.tables.findSymbol(name);
            return result instanceof Descriptors.FieldDescriptor && result.getFile() == this ? (Descriptors.FieldDescriptor)result : null;
         }
      }

      public static Descriptors.FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto, Descriptors.FileDescriptor[] dependencies) throws Descriptors.DescriptorValidationException {
         return buildFrom(proto, dependencies, false);
      }

      public static Descriptors.FileDescriptor buildFrom(
         DescriptorProtos.FileDescriptorProto proto, Descriptors.FileDescriptor[] dependencies, boolean allowUnknownDependencies
      ) throws Descriptors.DescriptorValidationException {
         return buildFrom(proto, dependencies, allowUnknownDependencies, false);
      }

      private static Descriptors.FileDescriptor buildFrom(
         DescriptorProtos.FileDescriptorProto proto,
         Descriptors.FileDescriptor[] dependencies,
         boolean allowUnknownDependencies,
         boolean allowUnresolvedFeatures
      ) throws Descriptors.DescriptorValidationException {
         Descriptors.FileDescriptorTables tables = new Descriptors.FileDescriptorTables(dependencies, allowUnknownDependencies);
         Descriptors.FileDescriptor result = new Descriptors.FileDescriptor(proto, dependencies, tables, allowUnknownDependencies);
         result.crossLink();
         if (!allowUnresolvedFeatures) {
            result.resolveAllFeaturesInternal();
         }

         return result;
      }

      private static byte[] latin1Cat(final String[] strings) {
         if (strings.length == 1) {
            return strings[0].getBytes(Internal.ISO_8859_1);
         } else {
            StringBuilder descriptorData = new StringBuilder();

            for (String part : strings) {
               descriptorData.append(part);
            }

            return descriptorData.toString().getBytes(Internal.ISO_8859_1);
         }
      }

      private static Descriptors.FileDescriptor[] findDescriptors(
         final Class<?> descriptorOuterClass, final String[] dependencyClassNames, final String[] dependencyFileNames
      ) {
         List<Descriptors.FileDescriptor> descriptors = new ArrayList<>();

         for (int i = 0; i < dependencyClassNames.length; i++) {
            try {
               Class<?> clazz = descriptorOuterClass.getClassLoader().loadClass(dependencyClassNames[i]);
               descriptors.add((Descriptors.FileDescriptor)clazz.getField("descriptor").get(null));
            } catch (Exception var6) {
               Descriptors.logger.warning("Descriptors for \"" + dependencyFileNames[i] + "\" can not be found.");
            }
         }

         return descriptors.toArray(new Descriptors.FileDescriptor[0]);
      }

      @Deprecated
      public static void internalBuildGeneratedFileFrom(
         final String[] descriptorDataParts,
         final Descriptors.FileDescriptor[] dependencies,
         final Descriptors.FileDescriptor.InternalDescriptorAssigner descriptorAssigner
      ) {
         byte[] descriptorBytes = latin1Cat(descriptorDataParts);

         DescriptorProtos.FileDescriptorProto proto;
         try {
            proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
         } catch (InvalidProtocolBufferException var8) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var8);
         }

         Descriptors.FileDescriptor result;
         try {
            result = buildFrom(proto, dependencies, true);
         } catch (Descriptors.DescriptorValidationException var7) {
            throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto.getName() + "\".", var7);
         }

         ExtensionRegistry registry = descriptorAssigner.assignDescriptors(result);
         if (registry != null) {
            throw new RuntimeException("assignDescriptors must return null");
         }
      }

      public static Descriptors.FileDescriptor internalBuildGeneratedFileFrom(
         final String[] descriptorDataParts, final Descriptors.FileDescriptor[] dependencies
      ) {
         byte[] descriptorBytes = latin1Cat(descriptorDataParts);

         DescriptorProtos.FileDescriptorProto proto;
         try {
            proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
         } catch (InvalidProtocolBufferException var6) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var6);
         }

         try {
            return buildFrom(proto, dependencies, true, true);
         } catch (Descriptors.DescriptorValidationException var5) {
            throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto.getName() + "\".", var5);
         }
      }

      public static Descriptors.FileDescriptor internalBuildGeneratedFileFrom(
         final String[] descriptorDataParts, final Class<?> descriptorOuterClass, final String[] dependencyClassNames, final String[] dependencyFileNames
      ) {
         Descriptors.FileDescriptor[] dependencies = findDescriptors(descriptorOuterClass, dependencyClassNames, dependencyFileNames);
         return internalBuildGeneratedFileFrom(descriptorDataParts, dependencies);
      }

      public static void internalUpdateFileDescriptor(Descriptors.FileDescriptor descriptor, ExtensionRegistry registry) {
         ByteString bytes = descriptor.proto.toByteString();

         try {
            DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.parseFrom(bytes, registry);
            descriptor.setProto(proto);
         } catch (InvalidProtocolBufferException var4) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var4);
         }
      }

      private FileDescriptor(
         final DescriptorProtos.FileDescriptorProto proto,
         final Descriptors.FileDescriptor[] dependencies,
         final Descriptors.FileDescriptorTables tables,
         boolean allowUnknownDependencies
      ) throws Descriptors.DescriptorValidationException {
         this.tables = tables;
         this.proto = proto;
         this.dependencies = (Descriptors.FileDescriptor[])dependencies.clone();
         this.featuresResolved = false;
         HashMap<String, Descriptors.FileDescriptor> nameToFileMap = new HashMap<>();

         for (Descriptors.FileDescriptor file : dependencies) {
            nameToFileMap.put(file.getName(), file);
         }

         List<Descriptors.FileDescriptor> publicDependencies = new ArrayList<>();

         for (int i = 0; i < proto.getPublicDependencyCount(); i++) {
            int index = proto.getPublicDependency(i);
            if (index < 0 || index >= proto.getDependencyCount()) {
               throw new Descriptors.DescriptorValidationException(this, "Invalid public dependency index.");
            }

            String name = proto.getDependency(index);
            Descriptors.FileDescriptor file = nameToFileMap.get(name);
            if (file == null) {
               if (!allowUnknownDependencies) {
                  throw new Descriptors.DescriptorValidationException(this, "Invalid public dependency: " + name);
               }
            } else {
               publicDependencies.add(file);
            }
         }

         this.publicDependencies = new Descriptors.FileDescriptor[publicDependencies.size()];
         publicDependencies.toArray(this.publicDependencies);
         this.placeholder = false;
         tables.addPackage(this.getPackage(), this);
         this.messageTypes = proto.getMessageTypeCount() > 0 ? new Descriptors.Descriptor[proto.getMessageTypeCount()] : Descriptors.EMPTY_DESCRIPTORS;

         for (int i = 0; i < proto.getMessageTypeCount(); i++) {
            this.messageTypes[i] = new Descriptors.Descriptor(proto.getMessageType(i), this, null, i);
         }

         this.enumTypes = proto.getEnumTypeCount() > 0 ? new Descriptors.EnumDescriptor[proto.getEnumTypeCount()] : Descriptors.EMPTY_ENUM_DESCRIPTORS;

         for (int i = 0; i < proto.getEnumTypeCount(); i++) {
            this.enumTypes[i] = new Descriptors.EnumDescriptor(proto.getEnumType(i), this, null, i);
         }

         this.services = proto.getServiceCount() > 0 ? new Descriptors.ServiceDescriptor[proto.getServiceCount()] : Descriptors.EMPTY_SERVICE_DESCRIPTORS;

         for (int i = 0; i < proto.getServiceCount(); i++) {
            this.services[i] = new Descriptors.ServiceDescriptor(proto.getService(i), this, i);
         }

         this.extensions = proto.getExtensionCount() > 0 ? new Descriptors.FieldDescriptor[proto.getExtensionCount()] : Descriptors.EMPTY_FIELD_DESCRIPTORS;

         for (int i = 0; i < proto.getExtensionCount(); i++) {
            this.extensions[i] = new Descriptors.FieldDescriptor(proto.getExtension(i), this, null, i, true);
         }
      }

      FileDescriptor(String packageName, Descriptors.Descriptor message) throws Descriptors.DescriptorValidationException {
         this.tables = new Descriptors.FileDescriptorTables(new Descriptors.FileDescriptor[0], true);
         this.proto = DescriptorProtos.FileDescriptorProto.newBuilder()
            .setName(message.getFullName() + ".placeholder.proto")
            .setPackage(packageName)
            .addMessageType(message.toProto())
            .build();
         this.dependencies = new Descriptors.FileDescriptor[0];
         this.publicDependencies = new Descriptors.FileDescriptor[0];
         this.featuresResolved = false;
         this.messageTypes = new Descriptors.Descriptor[]{message};
         this.enumTypes = Descriptors.EMPTY_ENUM_DESCRIPTORS;
         this.services = Descriptors.EMPTY_SERVICE_DESCRIPTORS;
         this.extensions = Descriptors.EMPTY_FIELD_DESCRIPTORS;
         this.placeholder = true;
         this.tables.addPackage(packageName, this);
         this.tables.addSymbol(message);
      }

      public void resolveAllFeaturesImmutable() {
         try {
            this.resolveAllFeaturesInternal();
         } catch (Descriptors.DescriptorValidationException var2) {
            throw new IllegalArgumentException("Invalid features for \"" + this.proto.getName() + "\".", var2);
         }
      }

      private void resolveAllFeaturesInternal() throws Descriptors.DescriptorValidationException {
         if (!this.featuresResolved) {
            synchronized (this) {
               if (!this.featuresResolved) {
                  this.resolveFeatures(this.proto.getOptions().getFeatures());

                  for (Descriptors.Descriptor messageType : this.messageTypes) {
                     messageType.resolveAllFeatures();
                  }

                  for (Descriptors.EnumDescriptor enumType : this.enumTypes) {
                     enumType.resolveAllFeatures();
                  }

                  for (Descriptors.ServiceDescriptor service : this.services) {
                     service.resolveAllFeatures();
                  }

                  for (Descriptors.FieldDescriptor extension : this.extensions) {
                     extension.resolveAllFeatures();
                  }

                  this.featuresResolved = true;
               }
            }
         }
      }

      @Override
      DescriptorProtos.FeatureSet inferLegacyProtoFeatures() {
         if (this.getEdition().getNumber() >= DescriptorProtos.Edition.EDITION_2023.getNumber()) {
            return DescriptorProtos.FeatureSet.getDefaultInstance();
         } else {
            DescriptorProtos.FeatureSet.Builder features = null;
            if (this.getEdition() == DescriptorProtos.Edition.EDITION_PROTO2 && this.proto.getOptions().getJavaStringCheckUtf8()) {
               features = DescriptorProtos.FeatureSet.newBuilder();
               features.setExtension(
                  JavaFeaturesProto.java_,
                  JavaFeaturesProto.JavaFeatures.newBuilder().setUtf8Validation(JavaFeaturesProto.JavaFeatures.Utf8Validation.VERIFY).build()
               );
            }

            return features != null ? features.build() : DescriptorProtos.FeatureSet.getDefaultInstance();
         }
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         for (Descriptors.Descriptor messageType : this.messageTypes) {
            messageType.crossLink();
         }

         for (Descriptors.ServiceDescriptor service : this.services) {
            service.crossLink();
         }

         for (Descriptors.FieldDescriptor extension : this.extensions) {
            extension.crossLink();
         }
      }

      private synchronized void setProto(final DescriptorProtos.FileDescriptorProto proto) {
         this.proto = proto;
         this.options = null;

         try {
            this.resolveFeatures(proto.getOptions().getFeatures());

            for (int i = 0; i < this.messageTypes.length; i++) {
               this.messageTypes[i].setProto(proto.getMessageType(i));
            }

            for (int i = 0; i < this.enumTypes.length; i++) {
               this.enumTypes[i].setProto(proto.getEnumType(i));
            }

            for (int i = 0; i < this.services.length; i++) {
               this.services[i].setProto(proto.getService(i));
            }

            for (int i = 0; i < this.extensions.length; i++) {
               this.extensions[i].setProto(proto.getExtension(i));
            }
         } catch (Descriptors.DescriptorValidationException var3) {
            throw new IllegalArgumentException("Invalid features for \"" + proto.getName() + "\".", var3);
         }
      }

      @Deprecated
      public interface InternalDescriptorAssigner {
         ExtensionRegistry assignDescriptors(Descriptors.FileDescriptor root);
      }
   }

   private static final class FileDescriptorTables {
      private final Set<Descriptors.FileDescriptor> dependencies;
      private final boolean allowUnknownDependencies;
      private final Map<String, Descriptors.GenericDescriptor> descriptorsByName = new HashMap<>();

      FileDescriptorTables(final Descriptors.FileDescriptor[] dependencies, boolean allowUnknownDependencies) {
         this.dependencies = Collections.newSetFromMap(new IdentityHashMap<>(dependencies.length));
         this.allowUnknownDependencies = allowUnknownDependencies;

         for (Descriptors.FileDescriptor dependency : dependencies) {
            this.dependencies.add(dependency);
            this.importPublicDependencies(dependency);
         }

         for (Descriptors.FileDescriptor dependency : this.dependencies) {
            try {
               this.addPackage(dependency.getPackage(), dependency);
            } catch (Descriptors.DescriptorValidationException var7) {
               throw new AssertionError(var7);
            }
         }
      }

      private void importPublicDependencies(final Descriptors.FileDescriptor file) {
         for (Descriptors.FileDescriptor dependency : file.getPublicDependencies()) {
            if (this.dependencies.add(dependency)) {
               this.importPublicDependencies(dependency);
            }
         }
      }

      Descriptors.GenericDescriptor findSymbol(final String fullName) {
         return this.findSymbol(fullName, Descriptors.FileDescriptorTables.SearchFilter.ALL_SYMBOLS);
      }

      Descriptors.GenericDescriptor findSymbol(final String fullName, final Descriptors.FileDescriptorTables.SearchFilter filter) {
         Descriptors.GenericDescriptor result = this.descriptorsByName.get(fullName);
         if (result == null
            || filter != Descriptors.FileDescriptorTables.SearchFilter.ALL_SYMBOLS
               && (filter != Descriptors.FileDescriptorTables.SearchFilter.TYPES_ONLY || !this.isType(result))
               && (filter != Descriptors.FileDescriptorTables.SearchFilter.AGGREGATES_ONLY || !this.isAggregate(result))) {
            for (Descriptors.FileDescriptor dependency : this.dependencies) {
               result = dependency.tables.descriptorsByName.get(fullName);
               if (result != null
                  && (
                     filter == Descriptors.FileDescriptorTables.SearchFilter.ALL_SYMBOLS
                        || filter == Descriptors.FileDescriptorTables.SearchFilter.TYPES_ONLY && this.isType(result)
                        || filter == Descriptors.FileDescriptorTables.SearchFilter.AGGREGATES_ONLY && this.isAggregate(result)
                  )) {
                  return result;
               }
            }

            return null;
         } else {
            return result;
         }
      }

      boolean isType(Descriptors.GenericDescriptor descriptor) {
         return descriptor instanceof Descriptors.Descriptor || descriptor instanceof Descriptors.EnumDescriptor;
      }

      boolean isAggregate(Descriptors.GenericDescriptor descriptor) {
         return descriptor instanceof Descriptors.Descriptor
            || descriptor instanceof Descriptors.EnumDescriptor
            || descriptor instanceof Descriptors.FileDescriptorTables.PackageDescriptor
            || descriptor instanceof Descriptors.ServiceDescriptor;
      }

      Descriptors.GenericDescriptor lookupSymbol(
         final String name, final Descriptors.GenericDescriptor relativeTo, final Descriptors.FileDescriptorTables.SearchFilter filter
      ) throws Descriptors.DescriptorValidationException {
         Descriptors.GenericDescriptor result;
         String fullname;
         if (name.startsWith(".")) {
            fullname = name.substring(1);
            result = this.findSymbol(fullname, filter);
         } else {
            int firstPartLength = name.indexOf(46);
            String firstPart;
            if (firstPartLength == -1) {
               firstPart = name;
            } else {
               firstPart = name.substring(0, firstPartLength);
            }

            StringBuilder scopeToTry = new StringBuilder(relativeTo.getFullName());

            while (true) {
               int dotpos = scopeToTry.lastIndexOf(".");
               if (dotpos == -1) {
                  fullname = name;
                  result = this.findSymbol(name, filter);
                  break;
               }

               scopeToTry.setLength(dotpos + 1);
               scopeToTry.append(firstPart);
               result = this.findSymbol(scopeToTry.toString(), Descriptors.FileDescriptorTables.SearchFilter.AGGREGATES_ONLY);
               if (result != null) {
                  if (firstPartLength != -1) {
                     scopeToTry.setLength(dotpos + 1);
                     scopeToTry.append(name);
                     result = this.findSymbol(scopeToTry.toString(), filter);
                  }

                  fullname = scopeToTry.toString();
                  break;
               }

               scopeToTry.setLength(dotpos);
            }
         }

         if (result == null) {
            if (this.allowUnknownDependencies && filter == Descriptors.FileDescriptorTables.SearchFilter.TYPES_ONLY) {
               Descriptors.logger.warning("The descriptor for message type \"" + name + "\" cannot be found and a placeholder is created for it");
               Descriptors.GenericDescriptor var10 = new Descriptors.Descriptor(fullname);
               this.dependencies.add(var10.getFile());
               return var10;
            } else {
               throw new Descriptors.DescriptorValidationException(relativeTo, '"' + name + "\" is not defined.");
            }
         } else {
            return result;
         }
      }

      void addSymbol(final Descriptors.GenericDescriptor descriptor) throws Descriptors.DescriptorValidationException {
         validateSymbolName(descriptor);
         String fullName = descriptor.getFullName();
         Descriptors.GenericDescriptor old = this.descriptorsByName.put(fullName, descriptor);
         if (old != null) {
            this.descriptorsByName.put(fullName, old);
            if (descriptor.getFile() == old.getFile()) {
               int dotpos = fullName.lastIndexOf(46);
               if (dotpos == -1) {
                  throw new Descriptors.DescriptorValidationException(descriptor, '"' + fullName + "\" is already defined.");
               } else {
                  throw new Descriptors.DescriptorValidationException(
                     descriptor, '"' + fullName.substring(dotpos + 1) + "\" is already defined in \"" + fullName.substring(0, dotpos) + "\"."
                  );
               }
            } else {
               throw new Descriptors.DescriptorValidationException(
                  descriptor, '"' + fullName + "\" is already defined in file \"" + old.getFile().getName() + "\"."
               );
            }
         }
      }

      void addPackage(final String fullName, final Descriptors.FileDescriptor file) throws Descriptors.DescriptorValidationException {
         int dotpos = fullName.lastIndexOf(46);
         String name;
         if (dotpos == -1) {
            name = fullName;
         } else {
            this.addPackage(fullName.substring(0, dotpos), file);
            name = fullName.substring(dotpos + 1);
         }

         Descriptors.GenericDescriptor old = this.descriptorsByName.put(fullName, new Descriptors.FileDescriptorTables.PackageDescriptor(name, fullName, file));
         if (old != null) {
            this.descriptorsByName.put(fullName, old);
            if (!(old instanceof Descriptors.FileDescriptorTables.PackageDescriptor)) {
               throw new Descriptors.DescriptorValidationException(
                  file, '"' + name + "\" is already defined (as something other than a package) in file \"" + old.getFile().getName() + "\"."
               );
            }
         }
      }

      static void validateSymbolName(final Descriptors.GenericDescriptor descriptor) throws Descriptors.DescriptorValidationException {
         String name = descriptor.getName();
         if (name.length() == 0) {
            throw new Descriptors.DescriptorValidationException(descriptor, "Missing name.");
         } else {
            for (int i = 0; i < name.length(); i++) {
               char c = name.charAt(i);
               if (('a' > c || c > 'z') && ('A' > c || c > 'Z') && c != '_' && ('0' > c || c > '9' || i <= 0)) {
                  throw new Descriptors.DescriptorValidationException(descriptor, '"' + name + "\" is not a valid identifier.");
               }
            }
         }
      }

      private static final class PackageDescriptor extends Descriptors.GenericDescriptor {
         private final String name;
         private final String fullName;
         private final Descriptors.FileDescriptor file;

         @Override
         public Message toProto() {
            return this.file.toProto();
         }

         @Override
         public String getName() {
            return this.name;
         }

         @Override
         public String getFullName() {
            return this.fullName;
         }

         @Override
         Descriptors.GenericDescriptor getParent() {
            return this.file;
         }

         @Override
         public Descriptors.FileDescriptor getFile() {
            return this.file;
         }

         PackageDescriptor(final String name, final String fullName, final Descriptors.FileDescriptor file) {
            this.file = file;
            this.fullName = fullName;
            this.name = name;
         }
      }

      static enum SearchFilter {
         TYPES_ONLY,
         AGGREGATES_ONLY,
         ALL_SYMBOLS;
      }
   }

   public abstract static class GenericDescriptor {
      volatile DescriptorProtos.FeatureSet features;

      private GenericDescriptor() {
      }

      public abstract Message toProto();

      public abstract String getName();

      public abstract String getFullName();

      public abstract Descriptors.FileDescriptor getFile();

      abstract Descriptors.GenericDescriptor getParent();

      void resolveFeatures(DescriptorProtos.FeatureSet unresolvedFeatures) throws Descriptors.DescriptorValidationException {
         Descriptors.GenericDescriptor parent = this.getParent();
         DescriptorProtos.FeatureSet inferredLegacyFeatures = null;
         if (parent != null
            && unresolvedFeatures.equals(DescriptorProtos.FeatureSet.getDefaultInstance())
            && (inferredLegacyFeatures = this.inferLegacyProtoFeatures()).equals(DescriptorProtos.FeatureSet.getDefaultInstance())) {
            this.features = parent.features;
            this.validateFeatures();
         } else {
            boolean hasPossibleCustomJavaFeature = false;

            for (Descriptors.FieldDescriptor f : unresolvedFeatures.getExtensionFields().keySet()) {
               if (f.getNumber() == JavaFeaturesProto.java_.getNumber() && f != JavaFeaturesProto.java_.getDescriptor()) {
                  hasPossibleCustomJavaFeature = true;
                  break;
               }
            }

            boolean hasPossibleUnknownJavaFeature = !unresolvedFeatures.getUnknownFields().isEmpty()
               && unresolvedFeatures.getUnknownFields().hasField(JavaFeaturesProto.java_.getNumber());
            if (hasPossibleCustomJavaFeature || hasPossibleUnknownJavaFeature) {
               ExtensionRegistry registry = ExtensionRegistry.newInstance();
               registry.add(JavaFeaturesProto.java_);
               ByteString bytes = unresolvedFeatures.toByteString();

               try {
                  unresolvedFeatures = DescriptorProtos.FeatureSet.parseFrom(bytes, registry);
               } catch (InvalidProtocolBufferException var9) {
                  throw new Descriptors.DescriptorValidationException(this, "Failed to parse features with Java feature extension registry.", var9);
               }
            }

            DescriptorProtos.FeatureSet.Builder features;
            if (parent == null) {
               DescriptorProtos.Edition edition = this.getFile().getEdition();
               features = Descriptors.getEditionDefaults(edition).toBuilder();
            } else {
               features = parent.features.toBuilder();
            }

            if (inferredLegacyFeatures == null) {
               inferredLegacyFeatures = this.inferLegacyProtoFeatures();
            }

            features.mergeFrom(inferredLegacyFeatures);
            features.mergeFrom(unresolvedFeatures);
            this.features = Descriptors.internFeatures(features.build());
            this.validateFeatures();
         }
      }

      DescriptorProtos.FeatureSet inferLegacyProtoFeatures() {
         return DescriptorProtos.FeatureSet.getDefaultInstance();
      }

      void validateFeatures() throws Descriptors.DescriptorValidationException {
      }

      DescriptorProtos.FeatureSet getFeatures() {
         if (this.features == null
            && (
               this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO2 || this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO3
            )) {
            this.getFile().resolveAllFeaturesImmutable();
         }

         if (this.features == null) {
            throw new NullPointerException(String.format("Features not yet loaded for %s.", this.getFullName()));
         } else {
            return this.features;
         }
      }
   }

   public static final class MethodDescriptor extends Descriptors.GenericDescriptor {
      private final int index;
      private DescriptorProtos.MethodDescriptorProto proto;
      private volatile DescriptorProtos.MethodOptions options;
      private final String fullName;
      private final Descriptors.ServiceDescriptor service;
      private Descriptors.Descriptor inputType;
      private Descriptors.Descriptor outputType;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.MethodDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.service.file;
      }

      @Override
      Descriptors.GenericDescriptor getParent() {
         return this.service;
      }

      public Descriptors.ServiceDescriptor getService() {
         return this.service;
      }

      public Descriptors.Descriptor getInputType() {
         return this.inputType;
      }

      public Descriptors.Descriptor getOutputType() {
         return this.outputType;
      }

      public boolean isClientStreaming() {
         return this.proto.getClientStreaming();
      }

      public boolean isServerStreaming() {
         return this.proto.getServerStreaming();
      }

      public DescriptorProtos.MethodOptions getOptions() {
         if (this.options == null) {
            DescriptorProtos.MethodOptions strippedOptions = this.proto.getOptions();
            if (strippedOptions.hasFeatures()) {
               strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
            }

            synchronized (this) {
               if (this.options == null) {
                  this.options = strippedOptions;
               }
            }
         }

         return this.options;
      }

      private MethodDescriptor(final DescriptorProtos.MethodDescriptorProto proto, final Descriptors.ServiceDescriptor parent, final int index) throws Descriptors.DescriptorValidationException {
         this.index = index;
         this.proto = proto;
         this.service = parent;
         this.fullName = parent.getFullName() + '.' + proto.getName();
         this.service.file.tables.addSymbol(this);
      }

      private void resolveAllFeatures() throws Descriptors.DescriptorValidationException {
         this.resolveFeatures(this.proto.getOptions().getFeatures());
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         Descriptors.GenericDescriptor input = this.getFile()
            .tables
            .lookupSymbol(this.proto.getInputType(), this, Descriptors.FileDescriptorTables.SearchFilter.TYPES_ONLY);
         if (!(input instanceof Descriptors.Descriptor)) {
            throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getInputType() + "\" is not a message type.");
         } else {
            this.inputType = (Descriptors.Descriptor)input;
            Descriptors.GenericDescriptor output = this.getFile()
               .tables
               .lookupSymbol(this.proto.getOutputType(), this, Descriptors.FileDescriptorTables.SearchFilter.TYPES_ONLY);
            if (!(output instanceof Descriptors.Descriptor)) {
               throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getOutputType() + "\" is not a message type.");
            } else {
               this.outputType = (Descriptors.Descriptor)output;
            }
         }
      }

      private void setProto(final DescriptorProtos.MethodDescriptorProto proto) throws Descriptors.DescriptorValidationException {
         this.proto = proto;
         this.options = null;
         this.resolveFeatures(proto.getOptions().getFeatures());
      }
   }

   public static final class OneofDescriptor extends Descriptors.GenericDescriptor {
      private final int index;
      private DescriptorProtos.OneofDescriptorProto proto;
      private volatile DescriptorProtos.OneofOptions options;
      private final String fullName;
      private final Descriptors.Descriptor containingType;
      private int fieldCount;
      private Descriptors.FieldDescriptor[] fields;

      public int getIndex() {
         return this.index;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.containingType.getFile();
      }

      @Override
      Descriptors.GenericDescriptor getParent() {
         return this.containingType;
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      public Descriptors.Descriptor getContainingType() {
         return this.containingType;
      }

      public int getFieldCount() {
         return this.fieldCount;
      }

      public DescriptorProtos.OneofOptions getOptions() {
         if (this.options == null) {
            DescriptorProtos.OneofOptions strippedOptions = this.proto.getOptions();
            if (strippedOptions.hasFeatures()) {
               strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
            }

            synchronized (this) {
               if (this.options == null) {
                  this.options = strippedOptions;
               }
            }
         }

         return this.options;
      }

      public List<Descriptors.FieldDescriptor> getFields() {
         return Collections.unmodifiableList(Arrays.asList(this.fields));
      }

      public Descriptors.FieldDescriptor getField(int index) {
         return this.fields[index];
      }

      public DescriptorProtos.OneofDescriptorProto toProto() {
         return this.proto;
      }

      boolean isSynthetic() {
         return this.fields.length == 1 && this.fields[0].isProto3Optional;
      }

      private void resolveAllFeatures() throws Descriptors.DescriptorValidationException {
         this.resolveFeatures(this.proto.getOptions().getFeatures());
      }

      private void setProto(final DescriptorProtos.OneofDescriptorProto proto) throws Descriptors.DescriptorValidationException {
         this.proto = proto;
         this.options = null;
         this.resolveFeatures(proto.getOptions().getFeatures());
      }

      private OneofDescriptor(final DescriptorProtos.OneofDescriptorProto proto, final Descriptors.Descriptor parent, final int index) {
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(null, parent, proto.getName());
         this.index = index;
         this.containingType = parent;
         this.fieldCount = 0;
      }
   }

   public static final class ServiceDescriptor extends Descriptors.GenericDescriptor {
      private final int index;
      private DescriptorProtos.ServiceDescriptorProto proto;
      private volatile DescriptorProtos.ServiceOptions options;
      private final String fullName;
      private final Descriptors.FileDescriptor file;
      private Descriptors.MethodDescriptor[] methods;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.ServiceDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.file;
      }

      @Override
      Descriptors.GenericDescriptor getParent() {
         return this.file;
      }

      public DescriptorProtos.ServiceOptions getOptions() {
         if (this.options == null) {
            DescriptorProtos.ServiceOptions strippedOptions = this.proto.getOptions();
            if (strippedOptions.hasFeatures()) {
               strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
            }

            synchronized (this) {
               if (this.options == null) {
                  this.options = strippedOptions;
               }
            }
         }

         return this.options;
      }

      public List<Descriptors.MethodDescriptor> getMethods() {
         return Collections.unmodifiableList(Arrays.asList(this.methods));
      }

      public int getMethodCount() {
         return this.methods.length;
      }

      public Descriptors.MethodDescriptor getMethod(int index) {
         return this.methods[index];
      }

      public Descriptors.MethodDescriptor findMethodByName(final String name) {
         Descriptors.GenericDescriptor result = this.file.tables.findSymbol(this.fullName + '.' + name);
         return result instanceof Descriptors.MethodDescriptor ? (Descriptors.MethodDescriptor)result : null;
      }

      private ServiceDescriptor(final DescriptorProtos.ServiceDescriptorProto proto, final Descriptors.FileDescriptor file, final int index) throws Descriptors.DescriptorValidationException {
         this.index = index;
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, null, proto.getName());
         this.file = file;
         this.methods = new Descriptors.MethodDescriptor[proto.getMethodCount()];

         for (int i = 0; i < proto.getMethodCount(); i++) {
            this.methods[i] = new Descriptors.MethodDescriptor(proto.getMethod(i), this, i);
         }

         file.tables.addSymbol(this);
      }

      private void resolveAllFeatures() throws Descriptors.DescriptorValidationException {
         this.resolveFeatures(this.proto.getOptions().getFeatures());

         for (Descriptors.MethodDescriptor method : this.methods) {
            method.resolveAllFeatures();
         }
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         for (Descriptors.MethodDescriptor method : this.methods) {
            method.crossLink();
         }
      }

      private void setProto(final DescriptorProtos.ServiceDescriptorProto proto) throws Descriptors.DescriptorValidationException {
         this.proto = proto;
         this.options = null;
         this.resolveFeatures(proto.getOptions().getFeatures());

         for (int i = 0; i < this.methods.length; i++) {
            this.methods[i].setProto(proto.getMethod(i));
         }
      }
   }
}
