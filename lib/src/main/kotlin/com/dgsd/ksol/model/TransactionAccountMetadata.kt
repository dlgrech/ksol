package com.dgsd.ksol.model

data class TransactionAccountMetadata(
    val publicKey: PublicKey,
    val isFeePayer: Boolean,
    val isSigner: Boolean,
    val isWritable: Boolean,
) : Comparable<TransactionAccountMetadata> {

    override fun compareTo(other: TransactionAccountMetadata): Int {
        // Prioritize signers at the front, follow by writable addresses

        return if (this.isSigner && !other.isSigner) {
            -1
        } else if (other.isSigner && !this.isSigner) {
            1
        } else if (this.isWritable && !other.isWritable) {
            -1
        } else if (other.isWritable && !this.isWritable) {
            1
        } else {
            0
        }
    }

    companion object {

        /**
         * Collapses any duplicate keys in the given list, such that each key only appears once
         */
        fun collapse(list: List<TransactionAccountMetadata>): List<TransactionAccountMetadata> {
            val output = mutableListOf<TransactionAccountMetadata>()

            for (meta in list) {
                val existingIndex = indexOf(output, meta.publicKey)
                if (existingIndex < 0) {
                    output.add(meta)
                } else {
                    val existing = output[existingIndex]
                    output[existingIndex] = TransactionAccountMetadata(
                        meta.publicKey,
                        isFeePayer = meta.isFeePayer || existing.isFeePayer,
                        isSigner = meta.isSigner || existing.isSigner,
                        isWritable = meta.isWritable || existing.isWritable
                    )
                }
            }


            return output.sorted()
        }

        internal fun indexOf(list: List<TransactionAccountMetadata>, key: PublicKey): Int {
            list.forEachIndexed { index, metadata ->
                if (key == metadata.publicKey) {
                    return index
                }
            }

            return -1
        }
    }
}
