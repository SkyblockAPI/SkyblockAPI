versionCatalogs {
    entries[project] = ForwardingVersionCatalog(
        named("libs${project.name.replace(".", "")}"),
        named("libs")
    )
}
