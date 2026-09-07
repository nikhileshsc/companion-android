package com.companion.astrodating.data.divineapi.data.mapper

import com.companion.astrodating.util.APP_EMPTY_STRING

object DivineApiMsgFormatter {
    fun format(p1: List<String>?, p2: List<String>?): String {
        val combined = buildList {
            p1?.filter { it.isNotBlank() }?.let { addAll(it) }
            p2?.filter { it.isNotBlank() }?.let { addAll(it) }
        }
        return if (combined.isNotEmpty()) {
            combined.joinToString(", ")
        } else {
            APP_EMPTY_STRING
        }
    }
}
