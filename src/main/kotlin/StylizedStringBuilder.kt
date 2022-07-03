data class StylizedStringBuilder(
    private val s: String
) {
    private val modifiers: MutableList<Modifier> = mutableListOf()

    fun create(): String =
        modifiers.joinToString(
            separator = ";",
            prefix = "\u001b[",
            postfix = "m$s$reset",
            transform = { it.v.toString() }
        )

    fun style(style: Style): StylizedStringBuilder {
        modifiers.add(style)
        return this
    }

    fun text(color: TextColor): StylizedStringBuilder {
        modifiers.add(color)
        return this
    }

    fun background(color: BackgroundColor): StylizedStringBuilder {
        modifiers.add(color)
        return this
    }

    companion object {
        private const val reset = "\u001b[0m"
    }

    sealed interface Modifier {
        val v: Int
    }

    enum class Style(override val v: Int) : Modifier {
        Bold(1),
        Dim(2),
        Italic(3),
    }

    enum class TextColor(override val v: Int) : Modifier {
        Black(30),
        White(37),
    }

    enum class BackgroundColor(override val v: Int) : Modifier {
        Black(40),
        Green(42),
        Yellow(43),
    }
}
