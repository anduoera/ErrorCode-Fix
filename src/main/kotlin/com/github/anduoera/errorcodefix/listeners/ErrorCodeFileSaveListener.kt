package com.github.anduoera.errorcodefix.listeners

import com.github.anduoera.errorcodefix.utils.errorCodeReportCodeHighlight
import com.github.anduoera.errorcodefix.utils.getProjectFromFile
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.*

/**
 * ErrorCode文件保存触发
 */
class ErrorCodeFileSaveListener : FileDocumentManagerListener {

    override fun beforeDocumentSaving(document: Document) {
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        val project = getProjectFromFile(file) ?: return
        errorCodeReportCodeHighlight(file, project)
    }

}