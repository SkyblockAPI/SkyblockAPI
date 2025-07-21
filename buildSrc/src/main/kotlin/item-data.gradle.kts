import tech.thatgravyboat.skyblockapi.item.CreateItemDataTask

val itemDataTask = tasks.register<CreateItemDataTask>("createItemData") {
    group = "skyblock-api"
}


tasks.withType<ProcessResources>().configureEach {
    dependsOn(itemDataTask.get())
    from(itemDataTask.get().outputs.files)
}
