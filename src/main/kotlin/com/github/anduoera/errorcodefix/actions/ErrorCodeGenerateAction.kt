package com.github.anduoera.errorcodefix.actions

import com.github.anduoera.errorcodefix.constants.ERROR_CODE_BASE_DIR_SKIP_FILE
import com.github.anduoera.errorcodefix.constants.FOUNDATION_TAG
import com.github.anduoera.errorcodefix.utils.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class ErrorCodeGenerateAction : AnAction() {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.project ?: return
        val virtualFile = actionEvent.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        if (virtualFile.name == ERROR_CODE_BASE_DIR_SKIP_FILE || project.name.endsWith(FOUNDATION_TAG)) {
            return
        }
        val psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE) ?: return

        if (isErrorCodeFileInTargetPath(virtualFile)) {
            errorCodeFix(project, psiFile, virtualFile)
            errorCodeReportCodeHighlight(virtualFile, project)
        } else {
            ErrorCodeJBList(project, psiFile, actionEvent).createJBList()
        }

    }
}