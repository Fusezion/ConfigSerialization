package dev.lyric.config.source

import kotlinx.serialization.KSerializer
import java.io.File

class FileConfigSource<T : Any>(
	override val identifier: String,
	val file: File,
	val serializer: KSerializer<T>,
	val defaultValue: T? = null,
) : ConfigSource {

	lateinit var data: T

}