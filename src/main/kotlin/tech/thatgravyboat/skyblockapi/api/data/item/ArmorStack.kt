package tech.thatgravyboat.skyblockapi.api.data.item

public enum class ArmorStack(public val char: Char) {
    AURORA('Ѫ'),
    TERROR('⁑'),
    HOLLOW('⚶'),
    FERVOR('҉'),
    CRIMSON('ᝐ'),
    ;

    public companion object {
        public fun fromString(string: String?): ArmorStack? {
            val char = string?.firstOrNull() ?: return null
            return entries.find { it.char == char }
        }
    }
}
