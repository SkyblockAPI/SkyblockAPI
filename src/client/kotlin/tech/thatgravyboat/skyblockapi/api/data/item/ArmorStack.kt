package tech.thatgravyboat.skyblockapi.api.data.item

enum class ArmorStack(val char: Char = ' ') {
    AURORA('Ѫ'),
    TERROR('⁑'),
    HOLLOW('⚶'),
    FERVOR('҉'),
    CRIMSON('ᝐ'),
    ;

    companion object {
        fun fromString(string: String?): ArmorStack? {
            val char = string?.firstOrNull() ?: return null
            return entries.find { it.char == char }
        }
    }
}
