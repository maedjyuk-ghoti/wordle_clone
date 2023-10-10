// Status of a letter in a guess
enum class LetterStatus {
    Correct, Almost, Unused
}

fun List<LetterStatus>.getIndexesOf(letterStatus: LetterStatus): List<Int> =
    mapIndexedNotNull { i, s -> if (s == letterStatus) i else null }

fun List<LetterStatus>.checkAllCorrect(): Boolean =
    none { it != LetterStatus.Correct }
