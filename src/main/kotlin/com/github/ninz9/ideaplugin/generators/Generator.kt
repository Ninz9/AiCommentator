package com.github.ninz9.ideaplugin.generators

import com.github.ninz9.ideaplugin.utils.types.MethodStructure
import kotlinx.coroutines.flow.Flow

interface Generator {

    suspend fun generateCommentForFunction(code: MethodStructure): String

    suspend fun generateCommentForClass(code: MethodStructure): String

    suspend fun generateCommentForFunctionStream(code: MethodStructure): Flow<String>

    suspend fun generateCommentForClassStream(code: MethodStructure): Flow<String>
}
