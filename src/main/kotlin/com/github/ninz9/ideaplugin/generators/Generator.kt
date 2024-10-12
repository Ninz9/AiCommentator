package com.github.ninz9.ideaplugin.generators

import com.github.ninz9.ideaplugin.utils.types.CodeStructure
import kotlinx.coroutines.flow.Flow

interface Generator {

    suspend fun generateCommentForFunction(code: CodeStructure): String

    suspend fun generateCommentForClass(code: CodeStructure): String

    suspend fun generateCommentForFunctionStream(code: CodeStructure): Flow<String>

    suspend fun generateCommentForClassStream(code: CodeStructure): Flow<String>
}
