package dev.lyric.config

import dev.lyric.config.source.ConfigSource
import dev.lyric.config.source.FileConfigSource
import dev.lyric.config.source.FolderConfigSource
import net.mamoe.yamlkt.Yaml
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ConfigManager(private val plugin: JavaPlugin) {

	private val sources = mutableMapOf<String, ConfigSource>()

	fun register(source: ConfigSource) {
		sources[source.identifier] = source
	}

	fun loadAll() {
		sources.values.forEach { source ->
			when (source) {
				is FileConfigSource<*> -> load(source)
				is FolderConfigSource<*> -> load(source)
			}
		}
	}

	fun <T : Any> load(source: FileConfigSource<T>) {
		val file = File(plugin.dataFolder, source.file.path)

		if (!file.exists()) {
			source.defaultValue?.let {
				source.data = it
				save(source)
				return
			}

			plugin.saveResource(source.file.invariantSeparatorsPath, false)
		}

		source.data = Yaml.decodeFromString(source.serializer, file.readText())

	}

	fun <T : Any> load(source: FolderConfigSource<T>) {

		val folder = File(plugin.dataFolder, source.folder.path)

		if (!folder.exists()) {
			folder.mkdirs()
			source.defaultFiles
				.filterNot { plugin.getResource(it) != null }
				.forEach { file ->
					plugin.saveResource("${source.folder.invariantSeparatorsPath}/$file", false)
				}
		}

		source.children.clear()

		folder.walkTopDown()
			.filter { it.isFile && it.extension == "yml" }
			.forEach { file ->
				val key = file.relativeTo(folder).invariantSeparatorsPath.removeSuffix(".yml")
				source.children[key] = Yaml.decodeFromString(source.serializer, file.readText())
			}
	}

	fun saveAll() {
		sources.values.forEach { source ->
			when (source) {
				is FileConfigSource<*> -> save(source)
				is FolderConfigSource<*> -> save(source)
			}
		}
	}

	fun <T : Any> save(source: FileConfigSource<T>) {
		val file = File(plugin.dataFolder, source.file.path)
		file.parentFile?.mkdirs()
		file.writeText(Yaml.encodeToString(source.serializer, source.data))
	}

	fun <T : Any> save(source: FolderConfigSource<T>) {
		val folder = File(plugin.dataFolder, source.folder.path)
		folder.mkdirs()

		source.children.forEach { (key, value) ->
			val file = File(folder, "$key.yml")
			file.parentFile?.mkdirs()
			file.writeText(Yaml.encodeToString(source.serializer,value))
		}
	}

	fun get(identifier: String): ConfigSource? {
		return sources[identifier]
	}

	inline fun <reified T : Any> requireFile(identifier: String): T {
		return getFile(identifier) ?: error("File config $identifier does not exist")
	}

	inline fun <reified T : Any> requireFolder(identifier: String, child: String): T {
		return getFolder(identifier, child) ?: error("Folder config $identifier with child $child does not exist")
	}

	inline fun <reified T : Any> getFile(identifier: String): T? {
		return (get(identifier) as? FileConfigSource<*>)?.data as? T
	}

	inline fun <reified T : Any> getFolder(identifier: String, child: String): T? {
		return (get(identifier) as? FolderConfigSource<*>)?.getChild(child) as? T
	}

	@Suppress("UNCHECKED_CAST")
	inline fun <reified T : Any> getFolderSource(identifier: String): FolderConfigSource<T>? {
		return get(identifier) as? FolderConfigSource<T>
	}

	@Suppress("UNCHECKED_CAST")
	inline fun <reified T : Any> getFileSource(identifier: String): FileConfigSource<T>? {
		return get(identifier) as? FileConfigSource<T>
	}


}