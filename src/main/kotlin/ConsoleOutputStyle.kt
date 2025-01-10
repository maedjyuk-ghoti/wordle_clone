import StylizedStringBuilder.Style
import StylizedStringBuilder.TextColor
import StylizedStringBuilder.BackgroundColor

enum class ConsoleOutputStyle {
    Color {
        override fun encode(character: String, letterStatus: LetterStatus): String =
            when (letterStatus) {
                LetterStatus.Correct ->
                    createStylizedString(character, Style.Bold, TextColor.Black, BackgroundColor.Green)
                LetterStatus.Almost ->
                    createStylizedString(character, Style.Italic, TextColor.Black, BackgroundColor.Yellow)
                LetterStatus.Unused ->
                    createStylizedString(character, Style.Dim, TextColor.White, BackgroundColor.Black)
            }

        private fun createStylizedString(character: String, style: Style, textColor: TextColor, backgroundColor: BackgroundColor): String =
            StylizedStringBuilder(character)
                .style(style)
                .text(textColor)
                .background(backgroundColor)
                .create()
    },
    BlackAndWhite {
        override fun encode(character: String, letterStatus: LetterStatus): String =
            when (letterStatus) {
                LetterStatus.Correct ->
                    createStylizedString(character, Style.Bold)
                LetterStatus.Almost ->
                    createStylizedString(character, Style.Italic)
                LetterStatus.Unused ->
                    createStylizedString(character, Style.Dim)
            }

        private fun createStylizedString(character: String, style: Style): String =
            StylizedStringBuilder(character)
                .style(style)
                .create()
    };

    abstract fun encode(character: String, letterStatus: LetterStatus): String
}