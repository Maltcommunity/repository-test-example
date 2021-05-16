package com.malt.test.time

import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class SettableClock : Clock() {

    private var delegate = systemDefaultZone()

    override fun getZone(): ZoneId = delegate.zone

    override fun withZone(zone: ZoneId): Clock = delegate.withZone(zone)

    override fun instant(): Instant = delegate.instant()

    fun stop(): SettableClock {
        set(instant())
        return this
    }

    fun set(dateTime: OffsetDateTime) = set(dateTime.toInstant())

    fun set(instant: Instant): SettableClock {
        delegate = fixed(instant, zone)
        return this
    }

    val offsetDateTime: OffsetDateTime get() = OffsetDateTime.now(this)
}