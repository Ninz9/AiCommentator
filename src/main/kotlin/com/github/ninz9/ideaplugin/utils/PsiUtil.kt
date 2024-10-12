package com.github.ninz9.ideaplugin.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement

fun AnActionEvent.getEditor(): Editor? {
    return this.getData(CommonDataKeys.EDITOR)
}

fun AnActionEvent.getFile(): PsiElement? {
    return this.getData(CommonDataKeys.PSI_FILE)
}

