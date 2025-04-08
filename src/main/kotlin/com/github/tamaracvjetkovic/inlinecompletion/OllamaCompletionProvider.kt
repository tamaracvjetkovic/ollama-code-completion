package com.github.tamaracvjetkovic.inlinecompletion

import com.github.tamaracvjetkovic.clients.OllamaCompletionClient
import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.flow.flowOf
import java.net.http.HttpClient

class OllamaCompletionProvider: InlineCompletionProvider {
    override val id = InlineCompletionProviderID("OllamaCompletionProvider")

    /**
     * Returns a code completion suggestion based on the current editor context.
     *
     * The method retrieves the entire prefix of the current file up to the caret position, and then delegates the
     * work to the Ollama completion client, which generates a code completion based on the provided prefix.
     *
     * The completion is returned as an inline completion suggestion, which is, after a few (long) seconds, displayed in the editor.
     *
     * @param request The inline completion request that contains information about the editor and caret position.
     * @return An inline completion suggestion, which is then inserted into the editor.
     */
    override suspend fun getSuggestion(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val prefix = getPrefix(request.editor, request.endOffset)
        val completion = OllamaCompletionClient(HttpClient.newHttpClient()).getCompletion(prefix)

        return InlineCompletionSuggestion.Default(
            flowOf(InlineCompletionGrayTextElement(completion))
        )
    }

    override val insertHandler: InlineCompletionInsertHandler
        get() = object : InlineCompletionInsertHandler {
            override fun afterInsertion(
                environment: InlineCompletionInsertEnvironment,
                elements: List<InlineCompletionElement>
            ) {}
        }

    private fun getPrefix(editor: Editor, offset: Int): String {
        val document = editor.document
        return document.getText(TextRange.from(0, offset.coerceAtMost(document.textLength)))
    }

    override fun isEnabled(event: InlineCompletionEvent): Boolean {
        return event is InlineCompletionEvent.DocumentChange
    }
}