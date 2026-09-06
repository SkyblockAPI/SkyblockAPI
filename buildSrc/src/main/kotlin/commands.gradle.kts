import org.gradle.kotlin.dsl.get
import org.gradle.plugins.ide.idea.model.IdeaModel
import tech.thatgravyboat.skyblockapi.task.CommandFileTask

plugins {
    kotlin("jvm")
    idea
}

val commandTask = tasks.register<CommandFileTask>("generateCommandFile") {
    maxDepth = 27
    packageName = "tech.thatgravyboat.skyblockapi.utils.command.dsl"
}

extensions.configure<IdeaModel>("idea") {
    this.module.generatedSourceDirs.add(commandTask.flatMap {
        it.output.asFile
    }.get())
}

tasks.compileKotlin {
    dependsOn(commandTask)
    mustRunAfter(commandTask)
}


sourceSets {
    main {
        kotlin {
            this.srcDirs(commandTask.map {
                it.output
            })
        }
    }
}

