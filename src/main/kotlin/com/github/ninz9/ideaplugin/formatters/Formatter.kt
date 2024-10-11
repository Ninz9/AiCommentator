package com.github.ninz9.ideaplugin.formatters

interface Formatter {

    val newLineTags: Set<String>
    val linePrefix: String
    val commentPrefix: String
    val commentSuffix: String

    fun isValidDoc(
        doc: String,
//                   paramNames: List<String>,
//                   hasReturnValue: Boolean,
//                   exceptionNames: List<String>
    ): Boolean


    fun formatDoc(doc: String, maxLineLength: Int = 80): String {
        val cleanedText = removeCommentMarkers(doc)
        println(cleanedText)
        val paragraphs = splitIntoParagraphs(cleanedText)
        val formattedParagraphs = formatParagraphs(paragraphs, maxLineLength)
        return assembleComment(formattedParagraphs)
    }

//    private fun removeCommentMarkers(comment: String): String {
//        val regex = Regex("""\s*\Q$linePrefix\E\s?""")  // Match any leading spaces and linePrefix (e.g., '*')
//        val regex1 =
//            Regex("""\Q$commentPrefix\E|\Q$commentSuffix\E""")  // Match any occurrence of commentPrefix or commentSuffix
//
//        return comment
//            .replace(regex1, "")  // Remove all occurrences of commentPrefix (/**) and commentSuffix (*/)
//            .replace(Regex("""\n+"""), " ")  // Replace newlines with spaces
//            .replace(regex, "")  // Remove linePrefix (e.g., '*')
//            .trim()
//    }

    private fun removeCommentMarkers(comment: String): String {
    val regex = Regex("""\s*\Q$linePrefix\E\s?""")  // Match any leading spaces and linePrefix (e.g., '*')
    val regex1 = Regex("""\Q$commentPrefix\E|\Q$commentSuffix\E""")  // Match any occurrence of commentPrefix or commentSuffix

    return comment
        .replace(regex1, "")  // Remove all occurrences of commentPrefix (/**) and commentSuffix (*/)
        .split("\n")  // Split the string into lines
        .map { line ->
            line.replace(regex, "").trim()  // Remove linePrefix and trim each line
        }
        .joinToString(" ")  // Join lines with a space
        .replace(Regex("\\s+"), " ")  // Replace multiple spaces with a single space
        .trim()
}


    private fun splitIntoParagraphs(text: String): List<String> {
        return text.split(Regex("\n\\s*\n|(?=${newLineTags.joinToString("|") { Regex.escape(it) }})"))
    }

    private fun formatParagraphs(paragraphs: List<String>, maxLineLength: Int): List<String> {
        return paragraphs.map { formatParagraph(it, maxLineLength) }
    }

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
