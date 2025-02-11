import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValidInputTest {
    companion object {
        val easySettings: Settings = Settings(isHardMode = false)
        val hardSettings: Settings = Settings(isHardMode = true)

        val testList: List<String> = listOf("aaaaa", "abcde", "bcdef")
    }

    @Test
    fun `isValidInput is false if the word is not valid`() {
        val validity = isValidInput(emptyList(), easySettings, testList, "bbbbb")

        assertEquals(GuessCheck.Invalid.NotInWordList, validity)
    }

    @Test
    fun `isValidInput is true if the word is in the list`() {
        val validity = isValidInput(emptyList(), easySettings, testList, "aaaaa")

        assertEquals(GuessCheck.Valid, validity)
    }

    @Test
    fun `isValidInHardMode is true for first word`() {
        val validity = isValidInput(emptyList(), hardSettings, testList, "aaaaa")

        assertEquals(GuessCheck.Valid, validity)
    }

    @Test
    fun `isValidInHardMode is false if a letter is missing`() {
        val history = listOf(
            Round(
                word = "abcde",
                check = listOf(LetterStatus.Almost, LetterStatus.Unused, LetterStatus.Unused, LetterStatus.Unused, LetterStatus.Unused)
            )
        )
        val validity = isValidInput(history, hardSettings, testList, "bcdef")

        assertEquals(GuessCheck.Invalid.MissingLetters(listOf('a')), validity)
    }
}