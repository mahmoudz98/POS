/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.firebase

import com.google.firebase.Timestamp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A custom serializer for [com.google.firebase.Timestamp].
 *
 * This serializer converts a Timestamp object into a structured, serializable object
 * containing its seconds and nanoseconds components, and vice-versa. This is the most
 * robust way to ensure data fidelity for Timestamps when using kotlinx.serialization.
 */
object TimestampSerializer : KSerializer<Timestamp> {

    /**
     * A private, serializable "surrogate" class that acts as a substitute
     * for the Timestamp during serialization. It has the exact structure
     * we want in our JSON output.
     */
    @Serializable
    private data class TimestampSurrogate(val seconds: Long, val nanoseconds: Int)

    override val descriptor: SerialDescriptor = TimestampSurrogate.serializer().descriptor

    /**
     * Called to write a Timestamp object to a serial format (e.g., JSON).
     */
    override fun serialize(encoder: Encoder, value: Timestamp) {
        // Create an instance of our serializable surrogate from the Timestamp
        val surrogate = TimestampSurrogate(value.seconds, value.nanoseconds)
        // Ask the encoder to serialize the surrogate object instead
        encoder.encodeSerializableValue(TimestampSurrogate.serializer(), surrogate)
    }

    /**
     * Called to read a Timestamp object from a serial format.
     */
    override fun deserialize(decoder: Decoder): Timestamp {
        // Ask the decoder to deserialize the data into our surrogate object
        val surrogate = decoder.decodeSerializableValue(TimestampSurrogate.serializer())
        // Create and return a real Timestamp from the surrogate's data
        return Timestamp(surrogate.seconds, surrogate.nanoseconds)
    }
}