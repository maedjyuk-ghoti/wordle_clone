sealed interface InterfaceStyle {
    fun getInput(): String
    fun display(history: List<Round>)
    fun display(state: IntermediateRound)
    fun displayWin()
    fun displayLose()
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

    override fun display(state: IntermediateRound) {
        when (state.validity) {
            is GuessCheck.Invalid.MissingLetters -> println("missing letter: ${state.validity.letters}")
            GuessCheck.Invalid.NotInWordList -> println("invalid word: ${state.guess}")
            GuessCheck.Valid -> display(state.history)
        }
    }

    override fun displayWin() {
        println("You Win")
    }

    override fun displayLose() {
        println("You Lose")
    }

}