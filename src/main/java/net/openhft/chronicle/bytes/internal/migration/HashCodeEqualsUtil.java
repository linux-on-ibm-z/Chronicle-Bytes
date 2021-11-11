package net.openhft.chronicle.bytes.internal.migration;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.bytes.algo.BytesStoreHash;
import net.openhft.chronicle.bytes.internal.BytesInternal;
import net.openhft.chronicle.core.io.ReferenceOwner;

public final class HashCodeEqualsUtil {

    private HashCodeEqualsUtil() {
    }

    public static boolean equals(final Bytes<?> bytes,
                                 final Object other) {

        if (bytes == other) {
            return true;
        }
        if (!(other instanceof BytesStore)) {
            return false;
        }
        final BytesStore<?, ?> otherByteStore = (BytesStore<?, ?>) other;

        // Now we know that we have two different objects so we may
        // use the same owner for them both
        final ReferenceOwner owner = ReferenceOwner.temporary("equals");
        bytes.reserve(owner);
        try {
            otherByteStore.reserve(owner);
            try {
                final long remaining = bytes.readRemaining();
                try {
                    return (otherByteStore.readRemaining() == remaining) &&
                            BytesInternal.contentEqual(bytes, otherByteStore);
                } catch (IllegalStateException e) {
                    return false;
                }
            } finally {
                otherByteStore.release(owner);
            }
        } finally {
            bytes.release(owner);
        }
    }

    public static int hashCode(final Bytes<?> bytes) {
        // Reserving prevents illegal access to this Bytes object if released by another thread
        final ReferenceOwner owner = ReferenceOwner.temporary("hashCode");
        bytes.reserve(owner);
        try {
            return BytesStoreHash.hash32(bytes);
        } finally {
            bytes.release(owner);
        }
    }

}
