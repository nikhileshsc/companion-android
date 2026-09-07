package com.companion.astrodating.data.divineapi.data.mapper

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class DivineApiMsgDeserializer<T>(
    private val factory: (List<String>, List<String>) -> T
) : JsonDeserializer<T> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): T {
        if (json == null || json.isJsonNull) {
            return factory(emptyList(), emptyList())
        }

        return when {
            json.isJsonPrimitive -> factory(listOf(json.asString), emptyList())
            json.isJsonArray -> factory(json.asJsonArray.map { it.toMessagePart() }, emptyList())
            json.isJsonObject -> {
                val obj = json.asJsonObject
                val p1 = parseList(obj.get("p1"))
                val p2 = parseList(obj.get("p2"))
                factory(p1, p2)
            }
            else -> factory(emptyList(), emptyList())
        }
    }

    private fun parseList(element: JsonElement?): List<String> {
        if (element == null || element.isJsonNull) {
            return emptyList()
        }
        return when {
            element.isJsonPrimitive -> listOf(element.toMessagePart())
            element.isJsonArray -> element.asJsonArray.map { it.toMessagePart() }
            else -> listOf(element.toMessagePart())
        }
    }
}

private fun JsonElement.toMessagePart(): String {
    return if (isJsonPrimitive) {
        asString
    } else {
        toString()
    }
}
