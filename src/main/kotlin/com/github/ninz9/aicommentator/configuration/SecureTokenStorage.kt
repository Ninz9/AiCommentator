package com.github.ninz9.aicommentator.configuration

import com.github.ninz9.aicommentator.llm.AiModel
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Service class for secure storage and retrieval of tokens associated with different AI models.
 *
 * This class provides methods to store and retrieve tokens securely using the `PasswordSafe` service.
 *
 * @constructor Creates an instance of SecureTokenStorage.
 */
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
