sealed interface InterfaceStyle {
    fun display(history: List<Round>)
    fun getInput(): String
}

data class CLI(private val style: ConsoleOutputStyle) : InterfaceStyle {
    companion object {
        private const val CLEAR_CONSOLE_STRING: String = "\u001b[H\u001b[2J"
    }

    override fun getInput(): String =
        readlnOrNull() ?: ""

    override fun display(history: List<Round>) =
        println(
            history.joinToString(
                separator = "\n",
                prefix = "$CLEAR_CONSOLE_STRING\n",
                transform = ::format
            )
        )

    private fun format(round: Round): String =
        round.word.mapIndexed { index, c -> c to round.check[index] }
            .joinToString(
                separator = "",
                transform = { (c, s) -> style.encode(" $c ", s) }
            )
}