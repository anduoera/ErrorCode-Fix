package com.github.anduoera.errorcodefix.utils

import com.github.anduoera.errorcodefix.constants.ErrorCodeListConstants
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiFile
import com.intellij.ui.components.JBList
import javax.swing.*


private val errorCodeList by lazy { ErrorCodeListConstants.instance }

class ErrorCodeJBList(
    private val project: Project,
    private val psiFile: PsiFile,
    private val actionEvent: AnActionEvent
) {
    private val listModel = DefaultListModel<String>()

    init {
        getErrorCodeFileList()
    }

    private fun getErrorCodeFileList() {
        errorCodeList.getErrorCodeListByProjectName(project.name).keys.toList().reversed().forEach {
            listModel.addElement(it)
        }
    }

    fun createJBList() {
        val jbList = JBList(listModel)
        val popup = JBPopupFactory.getInstance()
            .createListPopupBuilder(jbList)
            .setTitle("Choose ErrorCode File")
            .setItemChoosenCallback {
                generateErrorCodeToSelectFile(jbList.selectedValue, psiFile, project)
            }
            .setAutoselectOnMouseMove(true)
            .setFilterAlwaysVisible(true)
            .createPopup()
        popup.showInBestPositionFor(actionEvent.dataContext)
    }
}