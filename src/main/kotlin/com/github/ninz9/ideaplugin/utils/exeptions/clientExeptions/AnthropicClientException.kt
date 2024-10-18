package com.github.ninz9.ideaplugin.utils.exeptions.clientExeptions

import com.github.ninz9.ideaplugin.llm.AiModel
import com.github.ninz9.ideaplugin.utils.exeptions.AiCommentatorException
import com.github.ninz9.ideaplugin.utils.exeptions.ErrorType

class AnthropicClientException(errorType: ErrorType, override val message: String = ""): AiCommentatorException(errorType, AiModel.Anthropic)