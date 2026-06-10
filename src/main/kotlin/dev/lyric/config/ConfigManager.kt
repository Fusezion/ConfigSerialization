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

	fun get(identifier: String): ConfigSource? {
		return sources[identifier]
	}

	fun loadAll() {
		sources.values.forEach { source ->
			when (source) {
				is FileConfigSource<*> -> load(source)
				is FolderConfigSource<*> -> load(source)
			}
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

	inline fun <reified T : ConfigSource> get(identifier: String): T? {
		return get(identifier) as? T
	}


}