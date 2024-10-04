package com.github.ninz9.ideaplugin.configuration

import com.github.ninz9.ideaplugin.llm.AiModel
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class SecureTokenStorage {
    val tokensMap = mutableMapOf<AiModel, String>()

    init {
        AiModel.entries.forEach {
            tokensMap[it] = getTokens(it)
        }
    }

    fun getTokens(aiModel: AiModel): String {
        if (tokensMap[aiModel] == "") {
            val credentialAttributes = createCredentialsAttributes(aiModel.displayedName)
            val password = service<PasswordSafe>().getPassword(credentialAttributes)
            tokensMap[aiModel] = password ?: ""
            return tokensMap[aiModel] ?: ""
        }
        return tokensMap[aiModel] ?: ""
    }

    fun setToken(key: AiModel, token: String) {
        val prevToken = tokensMap[key]
        tokensMap[key] = token

        if (token != prevToken) service<PasswordSafe>().setPassword(createCredentialsAttributes(key.displayedName), token)
    }

    private fun createCredentialsAttributes(key: String): CredentialAttributes {
        return CredentialAttributes(generateServiceName("IdeaPlugin", key))
    }
}
