package com.github.ninz9.ideaplugin.generators

import com.github.ninz9.ideaplugin.utils.types.MethodStructure
import kotlinx.coroutines.flow.Flow

interface Generator {

    suspend fun generateCommentForFunction(code: PsiElement): String

    suspend fun generateCommentForClass(code: PsiElement): String

    suspend fun generateCommentForFunctionStream(code: PsiElement): Flow<String>

    suspend fun generateCommentForClassStream(code: PsiElement): Flow<String>
}
