package dev.lyric.config.source

import kotlinx.serialization.KSerializer
import java.io.File

class FolderConfigSource<T : Any>(
	override val identifier: String,
	val folder: File,
	val serializer: KSerializer<T>,
	val defaultFiles: List<String> = emptyList()
) : ConfigSource {

	val children = mutableMapOf<String, T>()

	fun getKeys(): Set<String> {
		return children.keys
	}

	fun getChildren(): Collection<T> {
		return children.values
	}

	fun getChild(key: String): T? {
		return children[key]
	}

	fun requireChild(key: String): T {
		return children[key]!!
	}

	fun setChild(key: String, value: T) {
		children[key] = value
	}

	fun hasChild(key: String): Boolean = children.containsKey(key)

}