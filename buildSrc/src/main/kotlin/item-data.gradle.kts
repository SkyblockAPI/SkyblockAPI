import tech.thatgravyboat.skyblockapi.item.CreateItemDataTask

val itemDataTask = tasks.register<CreateItemDataTask>("createItemData")

tasks.withType<ProcessResources>().configureEach {
    dependsOn(itemDataTask.get())
    from(itemDataTask.get().outputs.files)
}
