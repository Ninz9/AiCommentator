package com.github.ninz9.ideaplugin.configuration

import com.github.ninz9.ideaplugin.llm.AiModel
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Service
class SecureTokenStorage() {
    val tokensMap = mutableMapOf<AiModel, String>()

    suspend fun getToken(aiModel: AiModel): String {
        if (tokensMap[aiModel] != null) {
            return tokensMap[aiModel] ?: ""
        }
        val credentialAttributes = createCredentialsAttributes(aiModel.displayedName)
        CoroutineScope(Dispatchers.IO).launch {
            val password = service<PasswordSafe>().getPassword(credentialAttributes)
            tokensMap[aiModel] = password ?: ""
        }.join()
        return tokensMap[aiModel] ?: ""
    }

    fun setToken(key: AiModel, token: String) {
        val prevToken = tokensMap[key] ?: ""
        tokensMap[key] = token

        if (token != prevToken) {
            CoroutineScope(Dispatchers.IO).launch {
                service<PasswordSafe>().setPassword(createCredentialsAttributes(key.displayedName), token)
            }
        }
    }

    private fun createCredentialsAttributes(key: String): CredentialAttributes {
        return CredentialAttributes(generateServiceName("IdeaPlugin", key))
    }
}
