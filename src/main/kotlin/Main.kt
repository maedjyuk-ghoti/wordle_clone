import java.util.*

fun main() {
    val settings = Settings()
    val answers: List<String> = getAnswers().getOrThrow()
    val actual: String = answers[Random(System.currentTimeMillis()).nextInt(answers.count())]
    val valids: List<String> = answers + getValids().getOrThrow()
    val history = mutableListOf<Round>()

    // display initial board state and round info
    settings.interfaceStyle.display(history)

    while (history.size < settings.guessCount) {
        val guess = settings.interfaceStyle.getInput().uppercase()
        when (val validity = isValidInput(history, settings, valids, guess)) {
            is GuessCheck.Invalid.MissingLetters -> println("missing letter: ${validity.letters}")
            GuessCheck.Invalid.NotInWordList -> println("invalid word: $guess")
            GuessCheck.Valid -> history.add(Round(guess, wordle(actual, guess)))
        }

        settings.interfaceStyle.display(history)

        if (history.isNotEmpty() && history.last().check.checkAllCorrect()) {
            break
        }
    }

    if (history.size <= settings.guessCount && history.last().check.checkAllCorrect()) {
        println("You Win")
    } else if (history.size >= settings.guessCount) {
        println("You Lose")
    }
}


private fun getAnswers(): Result<List<String>> =
    getWordsFrom("answers.txt")

private fun getValids(): Result<List<String>> =
    getWordsFrom("valids.txt")

private fun getWordsFrom(file: String): Result<List<String>> =
    file.asResource()
        .map { text ->
            text.split("\n")
                .map(String::uppercase)
        }

private fun String.asResource(): Result<String> =
    kotlin.runCatching { Thread.currentThread().contextClassLoader.getResource(this)?.readText() }
        .mapCatching { it ?: throw Throwable("No Text in $this") }


sealed interface GuessCheck {
    object Valid : GuessCheck
    sealed interface Invalid : GuessCheck {
        object NotInWordList : Invalid
        data class MissingLetters(val letters: List<Char>) : Invalid
    }
}

fun isValidInput(history: List<Round>, settings: Settings, wordList: List<String>, input: String): GuessCheck =
    if (input.count() == settings.wordLength && wordList.contains(input)) {
        if (settings.isHardMode){
            isValidInHardMode(history, input)
        } else{
            GuessCheck.Valid
        }
    } else {
        GuessCheck.Invalid.NotInWordList
    }

fun isValidInHardMode(history: List<Round>, input: String): GuessCheck =
    checkCorrect(history, input).plus(checkAlmost(history, input))
        .let { missingLetters ->
            if (missingLetters.isNotEmpty()) {
                GuessCheck.Invalid.MissingLetters(missingLetters)
            } else {
                GuessCheck.Valid
            }
        }

private fun checkCorrect(history: List<Round>, input: String): List<Char> =
    history.flatMap { round -> round.check.getIndexesOf(LetterStatus.Correct).map { it to round.word[it] } }
        .fold(
            initial = emptyList<Char>(),
            operation = { missingLetters, (index, letter) ->
                if (input[index] != letter) {
                    missingLetters.plus(letter)
                } else {
                    missingLetters
                }
            }
        )

private fun checkAlmost(history: List<Round>, input: String): List<Char> =
    history.flatMap { round -> round.check.getIndexesOf(LetterStatus.Almost).map { round.word[it] } }
        .fold(
            initial = emptyList<Char>(),
            operation = { missingLetters, letter ->
                if (!input.contains(letter)) {
                    missingLetters.plus(letter)
                } else {
                    missingLetters
                }
            }
        )

data class Settings(
    val isHardMode: Boolean = true,
    val wordLength: Int = 5,
    val guessCount: Int = 6,
    val interfaceStyle: InterfaceStyle = CLI(ConsoleOutputStyle.Color)
)

// A record of a round played, the word guessed and the status of each letter in the word
data class Round(val word: String, val check: List<LetterStatus>)
