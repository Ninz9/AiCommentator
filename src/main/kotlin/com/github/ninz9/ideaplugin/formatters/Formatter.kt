package com.github.ninz9.ideaplugin.formatters

/**
 * Interface defining a formatter for documentation comments.
 *
 * @property newLineTags Set of tags that should start on a new line.
 * @property linePrefix Prefix to be applied to each formatted line.
 * @property commentPrefix Prefix indicating the start of a comment block.
 * @property commentSuffix Suffix indicating the end of a comment block.
 */
interface Formatter {

    /**
     * A set of tags that are used to identify new lines in the documentation formatting process.
     *
     * These tags help determine where new paragraphs should begin when processing and formatting
     * documentation texts. Each tag in this set specifies a marker that, when encountered,
     * signifies the end of the current paragraph and the start of a new one.
     */
    val newLineTags: Set<String>

    /**
     * Defines a prefix that will be added to each line in a formatted comment.
     * Typically used to include characters such as '*' in multi-line comment blocks.
     */
    val linePrefix: String

    /**
     * The prefix used for comment markers in the documentation format.
     * Typically denotes the start of a documentation block.
     */
    val commentPrefix: String

    /**
     * The suffix string to be appended at the end of a formatted comment.
     *
     * Typically used to denote the end of a comment block in structured documentation comments.
     * This value is appended when assembling the final structured comment.
     */
    val commentSuffix: String

    /**
     * Validates the format and content of a documentation comment.
     *
     * @param doc The documentation string to be validated.
     * @param paramNames A list of parameter names that should be present in the documentation. Default is an empty list.
     * @param hasReturnValue Indicates if the documentation should include a return value description. Default is false.
     * @param exceptionNames A list of exception names that should be documented. Default is an empty list.
     * @param propertyNames A list of property names that should be included in the documentation. Default is an empty list.
     * @return True if the documentation is valid based on the provided criteria, false otherwise.
     */
    fun isValidDoc(
        doc: String,
        paramNames: List<String> = emptyList(),
        hasReturnValue: Boolean = false,
        exceptionNames: List<String> = emptyList(),
        propertyNames: List<String> = emptyList()
    ): Boolean


    /**
     * Formats the given document string into a structured comment with a specified maximum line length.
     *
     * @param doc The document string to be formatted.
     * @param maxLineLength The maximum length of each line in the formatted comment. Default is 80 characters.
     * @return The formatted document string as a structured comment.
     */
    fun formatDoc(doc: String, maxLineLength: Int = 80): String {
        val cleanedText = removeCommentMarkers(doc)
        println(cleanedText)
        val paragraphs = splitIntoParagraphs(cleanedText)
        val formattedParagraphs = formatParagraphs(paragraphs, maxLineLength)
        return assembleComment(formattedParagraphs)
    }

    /**
     * Removes comment markers, prefixes, and suffixes from the given comment string.
     *
     * @param comment The comment string from which markers, prefixes, and suffixes will be removed.
     * @return The cleaned comment string with markers, prefixes, and suffixes removed.
     */
    private fun removeCommentMarkers(comment: String): String {
        val regex = Regex("""\s*\Q$linePrefix\E\s?""")  // Match any leading spaces and linePrefix (e.g., '*')
        val regex1 =
            Regex("""\Q$commentPrefix\E|\Q$commentSuffix\E""")  // Match any occurrence of commentPrefix or commentSuffix

        return comment
            .replace("```", "")
            .replace(regex1, "")  // Remove all occurrences of commentPrefix (/**) and commentSuffix (*/)
            .split("\n")  // Split the string into lines
            .map { line ->
                line.replace(regex, "").trim()  // Remove linePrefix and trim each line
            }
            .joinToString(" ")  // Join lines with a space
            .replace(Regex("\\s+"), " ")  // Replace multiple spaces with a single space
            .trim()
    }


    /**
     * Splits the provided text into a list of paragraphs. Paragraphs are identified by two or more
     * consecutive newline characters or by specific newline tags.
     *
     * @param text The input string to be split into paragraphs.
     * @return A list of paragraphs extracted from the input text.
     */
    private fun splitIntoParagraphs(text: String): List<String> {
        return text.split(Regex("\n\\s*\n|(?=${newLineTags.joinToString("|") { Regex.escape(it) }})"))
    }

    /**
     * Formats a list of paragraphs to ensure that each line within the paragraphs does not exceed the specified maximum line length.
     *
     * @param paragraphs The list of paragraphs to format.
     * @param maxLineLength The maximum number of characters allowed per line.
     * @return A list of formatted paragraphs, where each line adheres to the maximum line length constraint.
     */
    private fun formatParagraphs(paragraphs: List<String>, maxLineLength: Int): List<String> {
        return paragraphs.map { formatParagraph(it, maxLineLength) }
    }

    /**
     * Formats a single paragraph into multiple lines, ensuring that each line does not exceed the specified maximum line length.
     *
     * @param paragraph The paragraph to be formatted.
     * @param maxLineLength The maximum number of characters allowed per line.
     * @return The formatted paragraph as a string with lines separated by newline characters.
     */
    private fun formatParagraph(paragraph: String, maxLineLength: Int): String {
        val words = paragraph.trim().split(Regex("\\s+"))
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()
        var isFirstLine = true

        for (word in words) {
            if (newLineTags.any { word.startsWith(it) } && !isFirstLine) {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString().trim())
                }
                currentLine = StringBuilder(word)
                isFirstLine = false
                continue
            }

            if (currentLine.length + word.length + 1 > maxLineLength - linePrefix.length) {
                lines.add(currentLine.toString().trim())
                currentLine = StringBuilder()
                isFirstLine = false
            }
            currentLine.append("$word ")
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString().trim())
        }

        return lines.joinToString("\n") { "$linePrefix $it" }
    }

    /**
     * Assembles a structured comment from a list of paragraphs.
     *
     * @param paragraphs The list of paragraphs to be included in the structured comment.
     * @return The assembled comment as a string.
     */
    private fun assembleComment(paragraphs: List<String>): String {
        val result = StringBuilder("$commentPrefix\n")
        for ((index, paragraph) in paragraphs.withIndex()) {
            val isTagLine = newLineTags.any { paragraph.startsWith("$linePrefix $it") }
            result.append(paragraph).append("\n")
            if (!isTagLine && index < paragraphs.size - 1) {
                result.append("$linePrefix\n")
            }
        }
        result.append(commentSuffix)
        return result.toString()
    }
}
