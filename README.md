# Skyblock API

A collection of APIs for interacting with Hypixel SkyBlock.

## Using the Library

Hosted on:

```kts
maven("https://maven.teamresourceful.com/repository/maven-public/")
```

### In Cloche:

Cloche automatically handles modloader and minecraft version, which is why you don't need to specify them here.

```kts
dependencies {
    include("tech.thatgravyboat:skyblock-api:<version>") { isTransitive = false }
}
```

### Outside Cloche:

```kts
dependencies {
    val clocheAction: Action<ExternalModuleDependency> = Action {
        attributes {
            attribute(Attribute.of("earth.terrarium.cloche.modLoader", String::class.java), "fabric")
            attribute(Attribute.of("earth.terrarium.cloche.minecraftVersion", String::class.java), "<minecraft_version>")
        }
    }
    modImplementation("me.owdding:item-data-fixer:1.0.3", clocheAction)
    modImplementation("tech.thatgravyboat:skyblock-api:<version>") {
        exclude("me.owdding")
        clocheAction.execute(this)
    }
    include("tech.thatgravyboat:skyblock-api:<version>", clocheAction)
}
```

## Major Change Logs

- **v3.0.0**: Update to 1.21.9/1.21.10
- **v2.3.0**: Switch to kotlin.time vs kotlinx.datetime, required because of fabric kotlin version update.
- **v2.0.0**: Move to multi-version
- **v1.0.0**: Initial release
