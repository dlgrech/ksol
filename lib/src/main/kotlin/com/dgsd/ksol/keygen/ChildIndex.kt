package com.dgsd.ksol.keygen

/**
 * Represents an indexed (hardened or not) in a Hierarchical Deterministic segment.
 */
internal sealed class ChildIndex(
    protected val index: Int,
) {

    init {
        require(index.and(1 shl 31) == 0) {
            "Index is too large. Must be a 31-bit number"
        }
    }

    /**
     * Returns a 32-bit `Int`, that represents a type (hardened or not) along with a 31-bit number.
     *
     * The first bit will be `1` if this index is hardened, or 0 otherwise.
     *
     * The remaining 31-bits represent the index value
     *
     * @see <a href="https://docs.rs/derivation-path/0.1.3/src/derivation_path/lib.rs.html#257-266">Rust implementation</a>
     *
     */
    abstract fun toBits(): Int

    class Hardened(index: Int) : ChildIndex(index) {
        override fun toBits(): Int {
            return (1 shl 31) or index
        }
    }

    class Normal(index: Int) : ChildIndex(index) {
        override fun toBits(): Int {
            return index
        }
    }
}
