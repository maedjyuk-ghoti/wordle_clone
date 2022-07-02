import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WordleCloneTest {
    @Test
    fun `All letters are correct`() {
        assertThat(wordle("aaaaa", "aaaaa"))
            .isEqualTo(
                listOf(
                    Status.Correct,
                    Status.Correct,
                    Status.Correct,
                    Status.Correct,
                    Status.Correct,
                )
            )
    }

    @Test
    fun `All letters are unused`() {
        assertThat(wordle("aaaaa", "bbbbb"))
            .isEqualTo(
                listOf(
                    Status.Unused,
                    Status.Unused,
                    Status.Unused,
                    Status.Unused,
                    Status.Unused,
                )
            )
    }

    @Test
    fun `Letter in word but wrong spot`() {
        assertThat(wordle("abbbb", "ccacc"))
            .isEqualTo(
                listOf(
                    Status.Unused,
                    Status.Unused,
                    Status.Almost,
                    Status.Unused,
                    Status.Unused,
                )
            )
    }

    @Test
    fun `Letter only used once but in correct spot first occurrence`() {
        assertThat(wordle("abbbbb", "accac"))
            .isEqualTo(
                listOf(
                    Status.Correct,
                    Status.Unused,
                    Status.Unused,
                    Status.Unused,
                    Status.Unused,
                )
            )
    }

    @Test
    fun `Letter used twice, both in correct spot`() {
        assertThat(wordle("abbab", "accac"))
            .isEqualTo(
                listOf(
                    Status.Correct,
                    Status.Unused,
                    Status.Unused,
                    Status.Correct,
                    Status.Unused,
                )
            )
    }

    @Test
    fun `Letter used twice, first in correct spot second in wrong spot`() {
        assertThat(wordle("ababb", "accac"))
            .isEqualTo(
                listOf(
                    Status.Correct,
                    Status.Unused,
                    Status.Unused,
                    Status.Almost,
                    Status.Unused,
                )
            )
    }

    @Test
    fun `Letter only used once, but in correct spot second occurrence`() {
        assertThat(wordle("bbbab", "accac"))
            .isEqualTo(
                listOf(
                    Status.Unused,
                    Status.Unused,
                    Status.Unused,
                    Status.Correct,
                    Status.Unused,
                )
            )
    }
}