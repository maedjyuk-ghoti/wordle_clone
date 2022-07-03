import StylizedStringBuilder.Style
import StylizedStringBuilder.TextColor
import StylizedStringBuilder.BackgroundColor

enum class ConsoleOutputStyle {
    Color {
        override fun encode(character: String, letterStatus: LetterStatus): String =
            when (letterStatus) {
                LetterStatus.Correct ->
                    StylizedStringBuilder(character)
                        .style(Style.Bold)
                        .text(TextColor.Black)
                        .background(BackgroundColor.Green)
                        .create()
                LetterStatus.Almost ->
                    StylizedStringBuilder(character)
                        .style(Style.Italic)
                        .text(TextColor.Black)
                        .background(BackgroundColor.Yellow)
                        .create()
                LetterStatus.Unused ->
                    StylizedStringBuilder(character)
                        .style(Style.Dim)
                        .text(TextColor.White)
                        .background(BackgroundColor.Black)
                        .create()
            }
    },
    BlackAndWhite {
        override fun encode(character: String, letterStatus: LetterStatus): String =
            when (letterStatus) {
                LetterStatus.Correct ->
                    StylizedStringBuilder(character)
                        .style(Style.Bold)
                        .create()
                LetterStatus.Almost ->
                    StylizedStringBuilder(character)
                        .style(Style.Italic)
                        .create()
                LetterStatus.Unused ->
                    StylizedStringBuilder(character)
                        .style(Style.Dim)
                        .create()
            }
    };

    abstract fun encode(character: String, letterStatus: LetterStatus): String
}