fun wordle(actual: String, guess: String): List<LetterStatus> =
    fullCheck(naiveCheck(actual, guess), actual, guess)

private fun naiveCheck(actual: String, guess: String): List<LetterStatus> =
    guess.mapIndexed { index, letter ->
        if (actual[index] ==  letter) LetterStatus.Correct
        else if (actual.contains(letter)) LetterStatus.Almost
        else LetterStatus.Unused
    }

private fun fullCheck(letterStatuses: List<LetterStatus>, actual: String, guess: String): List<LetterStatus> =
    letterStatuses.foldIndexed(
        initial = letterStatuses.getFrequencyMapOf(LetterStatus.Correct, actual, guess) to listOf<LetterStatus>(),
        operation = { index, (frequencyMap, statuses), status ->
            when (status) {
                LetterStatus.Correct, LetterStatus.Unused -> frequencyMap to (statuses.plus(status))
                LetterStatus.Almost -> {
                    val letter = guess[index]
                    val count = frequencyMap.getOrDefault(letter, 0) // it will exist because it's "almost"

                    if (count > 0) frequencyMap.adjustFrequencyMap(letter) to (statuses.plus(LetterStatus.Almost))
                    else frequencyMap to (statuses.plus(LetterStatus.Unused))
                }
            }
        }
    ).second

private fun Map<Char, Int>.adjustFrequencyMap(letter: Char): Map<Char, Int> =
    mapValues { (keyLetter, count) ->
        if (letter == keyLetter) count - 1
        else count
    }

private fun List<LetterStatus>.getFrequencyMapOf(letterStatus: LetterStatus, actual: String, guess: String): Map<Char, Int> =
    getIndexesOf(letterStatus)
        .fold(
            initial = actual.groupingBy { it }.eachCount(),
            operation = { frequencyMap, index -> frequencyMap.adjustFrequencyMap(guess[index]) }
        )