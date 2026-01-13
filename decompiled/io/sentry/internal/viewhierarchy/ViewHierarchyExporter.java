package io.sentry.internal.viewhierarchy;

import io.sentry.protocol.ViewHierarchyNode;
import org.jetbrains.annotations.NotNull;

public interface ViewHierarchyExporter {
   boolean export(@NotNull ViewHierarchyNode var1, @NotNull Object var2);
}
