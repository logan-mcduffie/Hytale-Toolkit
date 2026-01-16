#!/bin/bash
# Fix <unrepresentable> tokens and empty static blocks in decompiled source

echo "Fixing assertion tokens in decompiled source..."

# Detect sed variant (BSD vs GNU) and define a function
if sed --version 2>/dev/null | grep -q GNU; then
    # GNU sed
    sed_inplace() { sed -i "$@"; }
else
    # BSD sed (macOS)
    sed_inplace() { sed -i '' "$@"; }
fi

# Find all Java files that need fixing
files=$(find decompiled -name "*.java" -type f)
count=0

for file in $files; do
    modified=false

    # Replace <unrepresentable> with AssertionHelper
    if grep -q "<unrepresentable>" "$file"; then
        sed_inplace 's/<unrepresentable>/AssertionHelper/g' "$file"
        modified=true
    fi

    # Remove empty assertion static blocks (simplified - just replace the problematic pattern)
    if grep -q 'AssertionHelper\.\$assertionsDisabled' "$file"; then
        # Replace assertion checks with 'false' since they're not meaningful in decompiled code
        sed_inplace 's/AssertionHelper\.\$assertionsDisabled/false/g' "$file"
        modified=true
    fi

    if [ "$modified" = true ]; then
        echo "  Fixed: ${file#decompiled/}"
        ((count++))
    fi
done

echo ""
echo "Fixed $count files"
if [ $count -gt 0 ]; then
    echo "You can now run javadoc successfully!"
else
    echo "No files needed fixing."
fi
