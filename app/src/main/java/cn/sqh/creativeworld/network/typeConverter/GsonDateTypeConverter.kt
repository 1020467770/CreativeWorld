package cn.sqh.creativeworld.network.typeConverter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.util.*

class GsonDateTypeConverter : TypeAdapter<Date>() {
    override fun write(out: JsonWriter?, value: Date?) {
        out ?: return
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    override fun read(reader: JsonReader): Date {
        val dateStr = reader.nextString()
        return Date(dateStr)
    }
}