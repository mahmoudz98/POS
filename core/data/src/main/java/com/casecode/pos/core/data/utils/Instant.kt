package com.casecode.pos.core.data.utils

import com.google.firebase.Timestamp
import kotlinx.datetime.Instant

fun Instant.toFirestoreTimestamp(): Timestamp {
    return Timestamp(epochSeconds, nanosecondsOfSecond)
}

fun Timestamp.toKotlinInstant(): Instant {
    return Instant.fromEpochSeconds(seconds, nanoseconds.toLong())
}
