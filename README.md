<h1 align="center">Skyblock API</h1>

<div align="center">
    
![Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.teamresourceful.com%2Frepository%2Fmaven-public%2Ftech%2Fthatgravyboat%2Fskyblock-api%2Fmaven-metadata.xml&strategy=highestVersion&filter=4.*&style=for-the-badge&label=Version)

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
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-<minecraft_version>") }
    }
}
```

<minecraft_version> is:

- `26.1` for 26.1.x
- `26.2` for 26.2.x


## Major Change Logs

- **v4.2.0**: Update to 26.2
- **v4.1.0**: Update to 26.1
- **v4.0.0**: Update to 1.21.11 and switch to stonecutter
- **v3.0.0**: Update to 1.21.9/1.21.10
- **v2.3.0**: Switch to kotlin.time vs kotlinx.datetime, required because of fabric kotlin version update.
- **v2.0.0**: Move to multi-version
- **v1.0.0**: Initial release
