import java.util.*

fun main() {
    val settings = Settings(isHardMode = true)
    val answers: List<String> = getAnswers().getOrThrow()
    val actual: String = answers[Random(System.currentTimeMillis()).nextInt(answers.count())]
    val valids: List<String> = answers + getValids().getOrThrow()

    val history = mutableListOf<Round>()

    // display initial board state and round info
    settings.interfaceStyle.display(history)

    while (history.size < settings.guessCount) {
        val guess = settings.interfaceStyle.getInput().uppercase()
        // enforce hard mode
        if (guess.isValidInput(history, settings, valids)) {
            val check = wordle(actual, guess)
            history.add(Round(guess, check))
        } else {
            println("invalid word: $guess")
        }

        settings.interfaceStyle.display(history)

        if (history.isNotEmpty() && history.last().check.checkAllCorrect()) {
            break
        }
    }

    if (history.size <= settings.guessCount &&
            history.last().check.checkAllCorrect()
    ) {
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


fun String.isValidInput(history: List<Round>, settings: Settings, wordList: List<String>): Boolean =
    if (count() == settings.wordLength && wordList.contains(this)) {
        if (settings.isHardMode){
            this.isValidInHardMode(history)
        } else{
            true
        }
    } else {
        false
    }

fun String.isValidInHardMode(history: List<Round>): Boolean =
    history.flatMap { round -> round.check.getIndexesOf(LetterStatus.Correct).map { it to round.word[it] } }
        .fold(
            initial = true,
            operation = { isValid, (index, letter) -> isValid && this[index] == letter }
        ) &&
            history.flatMap { round -> round.check.getIndexesOf(LetterStatus.Almost).map { round.word[it] } }
                .fold(
                    initial = true,
                    operation = { isValid, letter -> isValid && contains(letter) }
                )

data class Settings(
    val isHardMode: Boolean,
    val wordLength: Int = 5,
    val guessCount: Int = 6,
    val interfaceStyle: InterfaceStyle = CLI(ConsoleOutputStyle.Color)
)


// Status of a letter in a guess
enum class LetterStatus {
    Correct, Almost, Unused
}

fun List<LetterStatus>.getIndexesOf(letterStatus: LetterStatus): List<Int> =
    mapIndexedNotNull { i, s -> if (s == letterStatus) i else null }

fun List<LetterStatus>.checkAllCorrect(): Boolean =
    none { it != LetterStatus.Correct }


// A record of a round played, the word guessed and the status of each letter in the word
data class Round(val word: String, val check: List<LetterStatus>)
