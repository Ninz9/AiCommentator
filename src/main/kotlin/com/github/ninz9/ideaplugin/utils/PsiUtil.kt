package com.github.ninz9.ideaplugin.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

fun PsiElement.readText(): String {
    return ApplicationManager.getApplication().runReadAction<String> { this.text }
}


fun AnActionEvent.getEditor(): Editor? {
    return this.getData(CommonDataKeys.EDITOR)
}

fun AnActionEvent.getProject(): Project? {
    return this.getData(CommonDataKeys.PROJECT)
}

fun AnActionEvent.getFile(): PsiElement? {
    return this.getData(CommonDataKeys.PSI_FILE)
}

