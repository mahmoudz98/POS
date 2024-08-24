package com.casecode.pos.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class LoginPreferencesSerializer
    @Inject
    constructor() : Serializer<LoginPreferences> {
        override val defaultValue: LoginPreferences = LoginPreferences.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): LoginPreferences =
            try {
                // readFrom is already called on the data store background thread
                LoginPreferences.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }

        override suspend fun writeTo(
            t: LoginPreferences,
            output: OutputStream,
        ) {
            // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}