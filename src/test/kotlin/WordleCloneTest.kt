import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WordleCloneTest {
    @Test
    fun `All letters are correct`() {
        assertThat(wordle("aaaaa", "aaaaa"))
            .isEqualTo(
                listOf(
                    LetterStatus.Correct,
                    LetterStatus.Correct,
                    LetterStatus.Correct,
                    LetterStatus.Correct,
                    LetterStatus.Correct,
                )
            )
    }

    @Test
    fun `All letters are unused`() {
        assertThat(wordle("aaaaa", "bbbbb"))
            .isEqualTo(
                listOf(
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                )
            )
    }

    @Test
    fun `Letter in word but wrong spot`() {
        assertThat(wordle("abbbb", "ccacc"))
            .isEqualTo(
                listOf(
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Almost,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                )
            )
    }

    @Test
    fun `Letter only used once but in correct spot first occurrence`() {
        assertThat(wordle("abbbbb", "accac"))
            .isEqualTo(
                listOf(
                    LetterStatus.Correct,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                )
            )
    }

    @Test
    fun `Letter used twice, both in correct spot`() {
        assertThat(wordle("abbab", "accac"))
            .isEqualTo(
                listOf(
                    LetterStatus.Correct,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Correct,
                    LetterStatus.Unused,
                )
            )
    }

    @Test
    fun `Letter used twice, first in correct spot second in wrong spot`() {
        assertThat(wordle("ababb", "accac"))
            .isEqualTo(
                listOf(
                    LetterStatus.Correct,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Almost,
                    LetterStatus.Unused,
                )
            )
    }

    @Test
    fun `Letter only used once, but in correct spot second occurrence`() {
        assertThat(wordle("bbbab", "accac"))
            .isEqualTo(
                listOf(
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Unused,
                    LetterStatus.Correct,
                    LetterStatus.Unused,
                )
            )
    }
}