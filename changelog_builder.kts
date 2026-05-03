import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.reader

fun execute(vararg command: String): String {
    val process = Runtime.getRuntime().exec(command)
    while (process.isAlive) {
        Thread.sleep(20)
    }

    println(process.errorStream.readAllBytes().toString(Charsets.UTF_8))

    return process.inputStream.readAllBytes().toString(Charsets.UTF_8)
}

val lastTag = execute("git", "describe", "--tags", "--abbrev=0").removeSuffix("\n")
val out = StringBuilder()

val properties = Properties()
Path("gradle.properties").reader().use {
    properties.load(it)
}

out.append("# Release ${properties["version"] ?: "<ADD_VERSION_HERE>"}\n\n")
out.append("## Commits\n")

val commits = execute("git", "log", "$lastTag..HEAD", "--pretty=%H||%an||%s", "--no-abbrev-commit").split("\n").forEach {
    val split = it.split("||")
    if (split.size < 3) return@forEach
    val hash = split[0]
    val author = split[1]
    val message = split[2]
    if (author == "meowtomation") return@forEach

    out.append("- `$message` - $author ([${hash.take(8)}](https://github.com/SkyblockAPI/SkyblockAPI/commit/$hash))\n")
}

out.append("\n<@&1434211736126357588>")

println(out.toString())
