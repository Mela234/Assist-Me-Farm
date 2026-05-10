package com.cropdoc.app.ui.screens;

@kotlin.Metadata(mv = {2, 3, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\bH\u0003\u001a\u0010\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\b\u0010\f\u001a\u00020\u0001H\u0003\u001a`\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000b2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u00a8\u0006\u0017"}, d2 = {"ChatScreen", "", "viewModel", "Lcom/cropdoc/app/viewmodel/ChatViewModel;", "onNavigateBack", "Lkotlin/Function0;", "ChatBubble", "message", "Lcom/cropdoc/app/data/model/ChatMessage;", "StreamingBubble", "text", "", "TypingIndicator", "ChatInputBar", "inputText", "onInputChange", "Lkotlin/Function1;", "attachedImageUri", "isTyping", "", "onSend", "onAttachImage", "onRemoveAttachment", "app_debug"})
public final class ChatScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ChatScreen(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.ChatViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ChatBubble(com.cropdoc.app.data.model.ChatMessage message) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StreamingBubble(java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TypingIndicator() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ChatInputBar(java.lang.String inputText, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onInputChange, java.lang.String attachedImageUri, boolean isTyping, kotlin.jvm.functions.Function0<kotlin.Unit> onSend, kotlin.jvm.functions.Function0<kotlin.Unit> onAttachImage, kotlin.jvm.functions.Function0<kotlin.Unit> onRemoveAttachment) {
    }
}