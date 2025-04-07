package com.github.tamaracvjetkovic.inlinecompletion

import com.github.tamaracvjetkovic.client.OllamaCompletionClient
import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.flow.flowOf

class OllamaCompletionProvider: InlineCompletionProvider {
    override val id = InlineCompletionProviderID("OllamaCompletionProvider")

    override suspend fun getSuggestion(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val prefix = getPrefix(request.editor, request.endOffset)
        val completion = OllamaCompletionClient.requestCompletion(prefix)

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