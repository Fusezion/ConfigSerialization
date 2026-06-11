Read read read, if all you do is read how will you create, instead of reading use ai - Some random online

There's not much to really go over this library is very basic I just needed a non plugin dependent library for my projects (though the size is something blame kotlin)

Firstly before creating your first config class, as this uses kotlinxserialization and the YamlKT library to bridge yaml and kotlin you'll need to ensure your project not only
adds this project as a dependency but also includes the plugin for kotlin serialization in your project
```build.gradle.kts
plugins {
	kotlin("plugin.serialization") version "2.3.21"
	id("com.gradleup.shadow") version "9.4.2"
}

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation("com.github.Fusezion:ConfigSerialization:1.0.0")
}

task.shadowJar {
  // Don't forget to relocate to ensure this doesn't cause any isues you never know sadly ;(
  relocate("dev.lyric.config", "com.example.yourProject.libs.config")
}

```

# Creating your first config!
Luckily this is very simple firstly lets define the config data class, we'll just use my test class
```kts
@Serializable // As this is used for handling serialization its required
data class RootConfig(
  // This is commonly done as yamlkt doesn't handle `joinMessage` as `join-message` but we'll the latter in the .yml
  @SerialName("join-message")
  val joinMessage: String,
  @SerialName("leave-message")
  val leaveMessage: String,
  @SerialName("chat-format")
  val chatFormat: String
)
```

Yay! Our primary config file class is now completed! Let's quickly make the yaml
```yaml
# file name: config.yml
join-message: "%player% has joined the server!"
leave-message: "%player% has left the server! ;("
chat-fortmat: "%player%: %message%"
```

# Registering & Loading your config!
Now just a notice I am not going to include the listener for this you should know how those work already
alrightt now lets make the loading firstly we'll need a plugin class
```kts
class ExamplePlugin : JavaPlugin() {

  lateinit var configManager: ConfigManager

  @Override
  fun onEnable() {
    configManager = ConfigManager(this)
    // Now you don't need to store a val you can just pass it directly
    // Lets break down what we're doing here
    // 1. "config" - refers to the identifier used to get the file data, you can honestly just store the FileSource somehwere accessible tho
    // 2. File("config.yml") - references the literal file, it's automatically converted to `plugin.dataFolder, file.path` when loading
    // 3. RootConfig.serializer() is why we add `@Serializable` not only so it can be serialized but also to use said serializer to handle the loading and creation of the class
    // 4. now there is a fourth option to add a default instance of the RootConfig but that's only needed if you don't want to add the `resources/config.yml` to your plugin
    val fileSource = FileConfigSource("config", File("config.yml"), RootConfig.serializer())

    // Now we can simple register the fileSource for our config
    configManager.register(fileSource)
    // Now we just load all configs, if everything is setup in the config correctly you shouldn't have any issues
    configManager.loadAll()

    Bukkit.broadcastMessage("""
    Now this is just to showcase retrieving hopefully you already know how to read kotlin strings.
    joinMessage: ${fileSource.data.joinMessage}
    leaveMessage: ${fileSource.data.leaveMessage}
    chatFormat: ${fileSource.data.chatFormat}
    """)

    // Optionally you can use the below to get the RootConfig instance
    val rootConfig = configManager.getFile<RootConfig>("config")
    Bukkit.broadcastMessage("joinMessage: ${rootConfig.joinMessage}")
    Bukkit.broadcastMessage("leaveMessage: ${rootConfig.leaveMessage}")
    Bukkit.broadcastMessage("chatFormat: ${rootConfig.chatFormat}")
  }

}
```
