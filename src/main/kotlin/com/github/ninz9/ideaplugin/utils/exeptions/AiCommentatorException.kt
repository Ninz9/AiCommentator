package com.github.ninz9.ideaplugin.utils.exeptions

import com.github.ninz9.ideaplugin.llm.AiModel

open class AiCommentatorException(
    val errorType: ErrorType,
    val aiModel: AiModel,
    override val message: String = "",
): Exception(message)