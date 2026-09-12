package com.example.model

enum class ProcessingAction {
    REMOVE_BACKGROUND,
    ENHANCE_IMAGE,
    DO_BOTH,
    SKIP
}

data class ProcessingOptions(
    val action: ProcessingAction = ProcessingAction.DO_BOTH,
    val removeBackground: Boolean = true,
    val enhanceImage: Boolean = true
) {
    companion object {
        fun fromAction(action: ProcessingAction): ProcessingOptions = when (action) {
            ProcessingAction.REMOVE_BACKGROUND -> ProcessingOptions(action, removeBackground = true, enhanceImage = false)
            ProcessingAction.ENHANCE_IMAGE -> ProcessingOptions(action, removeBackground = false, enhanceImage = true)
            ProcessingAction.DO_BOTH -> ProcessingOptions(action, removeBackground = true, enhanceImage = true)
            ProcessingAction.SKIP -> ProcessingOptions(action, removeBackground = false, enhanceImage = false)
        }
    }
}
