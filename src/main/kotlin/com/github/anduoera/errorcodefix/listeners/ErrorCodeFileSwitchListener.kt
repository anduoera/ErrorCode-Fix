package com.github.anduoera.errorcodefix.listeners

import com.github.anduoera.errorcodefix.constants.*
import com.github.anduoera.errorcodefix.utils.checkIsErrorCodeFileInTargetPath
import com.github.anduoera.errorcodefix.utils.getFileRepeatErrorCodeOffset
import com.github.anduoera.errorcodefix.utils.highlightErrorCodes
import com.intellij.openapi.fileEditor.*

/**
 * 文件切换
 */
class ErrorCodeFileSwitchListener : FileEditorManagerListener {
    override fun selectionChanged(event: FileEditorManagerEvent) {
        val newFile = event.newFile
        val source = event.manager

        if (newFile == null || checkIsErrorCodeFileInTargetPath(newFile, source.project)) {
            return
        }

        val newEditor = event.newEditor
        if (newEditor !is TextEditor) return
        highlightErrorCodes(
            newEditor.editor,
            getFileRepeatErrorCodeOffset(source.project, newFile),
            REPEAT_ERROR_CODE_HIGHLIGHT
        )
    }

}