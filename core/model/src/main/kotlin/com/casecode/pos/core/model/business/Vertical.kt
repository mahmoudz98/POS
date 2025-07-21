package com.casecode.pos.core.model.business

enum class Vertical(val value: Int) {
    RETAIL(0),
    CAFE(1),
    PHARMACY(2);
    companion object{
        fun fromValue(value:Int): Vertical = entries.first { it.value == value }
    }
}
