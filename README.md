<h1 align="center">Skyblock API</h1>

<div align="center">
    
![Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.teamresourceful.com%2Frepository%2Fmaven-public%2Ftech%2Fthatgravyboat%2Fskyblock-api%2Fmaven-metadata.xml&strategy=highestVersion&filter=3.*&style=for-the-badge&label=Version)

</div>

A collection of APIs for interacting with Hypixel SkyBlock.

## Using the Library

Hosted on:

```kts
maven("https://maven.teamresourceful.com/repository/maven-public/")
```

```kts
dependencies {
    api("tech.thatgravyboat:skyblock-api:<version>") {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-<minecraft_version>") }
    }
    include("tech.thatgravyboat:skyblock-api:<version>") {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-<minecraft_version>-remapped") }
    }
}
```

<minecraft_version> is:

- `1.21.5` for 1.21.5
- `1.21.8` for 1.21.6-1.21.8
- `1.21.10` for 1.21.9-1.21.10
- `1.21.11` for 1.21.11


## Major Change Logs

- **v3.0.0**: Update to 1.21.9/1.21.10
- **v2.3.0**: Switch to kotlin.time vs kotlinx.datetime, required because of fabric kotlin version update.
- **v2.0.0**: Move to multi-version
- **v1.0.0**: Initial release
