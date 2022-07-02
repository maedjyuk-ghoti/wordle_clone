fun main() {
    val settings = Settings(isHardMode = true)
    val actual =  "apple"

    val history = mutableListOf<Round>()
    do {
        val guess = obtainGuess()
        // enforce hard mode
        if (guess.isValidInput(history, settings)) {
            val check = wordle(actual, guess)
            history.add(Round(guess, check))
        } else {
            println("invalid word: $guess")
        }

        display(history)
    } while (history.size < settings.guessCount &&
        !history.last().check.checkAllCorrect())

    if (history.size <= settings.guessCount &&
            history.last().check.checkAllCorrect()
    ) {
        println("You Win")
    } else if (history.size >= settings.guessCount) {
        println("You Lose")
    }
}

// CLI UI
fun obtainGuess(): String {
    return readLine() ?: ""
}

fun display(state: List<Round>) {
    print("\u001b[H\u001b[2J")
    state.forEach { round ->
        display(round)
    }
}

fun display(round: Round) {
    round.word.foldIndexed(
        initial = "",
        operation = { index, acc, c ->
            "$acc${ConsoleOutputStyle.Color.encode(" $c ", round.check[index])}"
        }
    ).let(::println)
}


// Status of a letter in a guess
enum class Status {
    Correct, Almost, Unused
}

// A record of a round played, the word guessed and the status of each letter in the word
data class Round(val word: String, val check: List<Status>)

enum class ConsoleOutputStyle {
    Color {
      override fun encode(character: String, status: Status): String =
          when (status) {
              Status.Correct -> stylize { background(green) { text(black) { format(bold) { character } } } }
              Status.Almost -> stylize { background(yellow) { text(black) { format(italic) { character } } } }
              Status.Unused -> stylize { background(black) { text(white) { format(dim) { character } } } }
          }
    },
    BlackAndWhite {
        override fun encode(character: String, status: Status): String =
            when (status) {
                Status.Correct -> stylize { format(bold) { character } }
                Status.Almost -> stylize { format(italic) { character } }
                Status.Unused -> stylize { format(dim) { character } }
            }
    };

    abstract fun encode(character: String, status: Status): String

    companion object {
        fun stylize(init: () -> String): String =
            "${init()}$reset"

        fun format(format: Int, init: () -> String): String =
            "\u001b[${format}m${init()}"

        fun text(color: Int, init: () -> String): String =
            "\u001b[${color}m${init()}"

        fun background(color: Int, init: () -> String): String =
            "\u001b[${color+10}m${init()}"

        val reset = "\u001b[0m"
        val bold = 1
        val dim = 2
        val italic = 3
        val black = 30
        val red = 31
        val green = 32
        val yellow = 33
        val blue = 34
        val purple = 35
        val cyan = 36
        val white = 37
    }
}

data class Settings(
    val isHardMode: Boolean,
    val wordLength: Int = 5,
    val guessCount: Int = 6,
    val outputStyle: ConsoleOutputStyle = ConsoleOutputStyle.BlackAndWhite
)

fun String.isValidInput(history: List<Round>, settings: Settings): Boolean =
    if (count() == settings.wordLength) {
        if (settings.isHardMode) {
            history.flatMap { round -> round.check.getIndexesOf(Status.Correct).map { round.word[it] } }
                .distinct()
                .fold(
                    initial = true,
                    operation = { isValid, letter -> isValid && contains(letter) }
                )
        } else true
    } else false

fun update(actual: String, guess: String, history: List<Round>): Pair<List<Round>, Boolean> {
    val check = wordle(actual, guess)

    return history.plus(Round(guess, check)) to check.checkAllCorrect()
}

fun List<Status>.checkAllCorrect(): Boolean =
    none { it != Status.Correct }



// check for provided word
fun wordle(actual: String, guess: String): List<Status> =
    fullCheck(naiveCheck(actual, guess), actual, guess)

fun naiveCheck(actual: String, guess: String): List<Status> =
    guess.mapIndexed { index, letter ->
        if (actual[index] ==  letter) Status.Correct
        else if (actual.contains(letter)) Status.Almost
        else Status.Unused
    }

private fun fullCheck(statuses: List<Status>, actual: String, guess: String): List<Status> =
    statuses.foldIndexed(
        initial = statuses.getFrequencyMapOf(Status.Correct, actual, guess) to listOf<Status>(),
        operation = { index, (frequencyMap, statuses), status ->
            when (status) {
                Status.Correct, Status.Unused -> frequencyMap to (statuses.plus(status))
                Status.Almost -> {
                    val letter = guess[index]
                    val count = frequencyMap.getOrDefault(letter, 0) // it will exist because it's "almost"

                    if (count > 0) frequencyMap.adjustFrequencyMap(letter) to (statuses.plus(Status.Almost))
                    else frequencyMap to (statuses.plus(Status.Unused))
                }
            }
        }
    ).second

fun Map<Char, Int>.adjustFrequencyMap(letter: Char): Map<Char, Int> =
    mapValues { (keyLetter, count) ->
        if (letter == keyLetter) count - 1
        else count
    }

fun List<Status>.getFrequencyMapOf(status: Status, actual: String, guess: String): Map<Char, Int> =
    getIndexesOf(status)
        .fold(
            initial = actual.groupingBy { it }.eachCount(),
            operation = { frequencyMap, index -> frequencyMap.adjustFrequencyMap(guess[index]) }
        )

fun List<Status>.getIndexesOf(status: Status): List<Int> =
    mapIndexedNotNull { i, s -> if (s == status) i else null }