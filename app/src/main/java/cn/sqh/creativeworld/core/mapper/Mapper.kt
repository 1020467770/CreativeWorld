package cn.sqh.creativeworld.core.mapper

interface Mapper<in I, out O> {
    fun map(input: I): O
}