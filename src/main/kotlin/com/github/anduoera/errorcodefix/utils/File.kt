package com.github.anduoera.errorcodefix.utils

import com.github.anduoera.errorcodefix.constants.*
import com.goide.psi.impl.GoConstDefinitionImpl
import com.goide.psi.impl.GoConstSpecImpl
import com.goide.psi.impl.GoStringLiteralImpl
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

private val errorCodeSet by lazy { ErrorCodeListConstants.instance }

/**
 * 确认在/model/exception文件夹下
 */
fun isErrorCodeFileInTargetPath(file: VirtualFile): Boolean {
    return file.path.contains(ERROR_CODE_BASE_DIR)
}

/**
 * 获取文件中重复的错误码
 */
fun getFileRepeatErrorCodeOffset(project: Project, virtualFile: VirtualFile): MutableList<ErrorCodeOffset> {
    val errorCodeOffsetList = mutableListOf<ErrorCodeOffset>()
    val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
    if (psiFile != null) {
        try {
            val goConstSpecList = PsiTreeUtil.findChildrenOfAnyType(psiFile, GoConstSpecImpl::class.java)
            goConstSpecList.forEach { goConstSpec ->
                try {
                    val goConstDefinition =
                        PsiTreeUtil.findChildOfType(goConstSpec, GoConstDefinitionImpl::class.java)
                    val goStringLiteral = PsiTreeUtil.findChildOfType(goConstSpec, GoStringLiteralImpl::class.java)

                    if (goConstDefinition != null) {
                        goConstDefinition.text?.let {
                            if (it.startsWith(ERROR_CODE_BASE_START)) {
                                val errorCodeValue = goStringLiteral?.decodedText
                                if (errorCodeValue.isNullOrEmpty() ||
                                    errorCodeSet.isErrorCodeDuplicate(project.name, errorCodeValue) ||
                                    errorCodeValue.length != 4 ||
                                    errorCodeValue.toLongOrNull() == null
                                ) {

                                    val errorCodeOffset = ErrorCodeOffset(
                                        startOffset = goConstSpec.startOffset,
                                        endOffset = goConstSpec.endOffset,
                                    )
                                    errorCodeOffsetList.add(errorCodeOffset)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // 捕获并处理异常，避免程序停止
                    println("Error processing GoConstSpec: ${e.message}")
                    e.printStackTrace() // 打印异常堆栈信息（可选）
                }
            }
        } catch (e: Exception) {
            println("Error processing file ${psiFile.name}: ${e.message}")
            e.printStackTrace()
        }
    }
    return errorCodeOffsetList
}

/**
 * 根据VirtualFile获取Project
 */
fun getProjectFromFile(file: VirtualFile): Project? {
    // 实现从文件获取项目的逻辑
    return ProjectManager.getInstance().openProjects.firstOrNull { project ->
        ProjectRootManager.getInstance(project).fileIndex.isInContent(file)
    }
}

/**
 * 根据 VirtualFile 获取 PSI 文件
 *
 * @param project 当前项目
 * @param file 虚拟文件
 * @return 对应的 PSI 文件，如果无法获取则返回 null
 */
fun getPsiFileForVirtualFile(project: Project, file: VirtualFile): PsiFile? {
    return PsiManager.getInstance(project).findFile(file)
}

/**
 * 获取指定文件的 TextEditor
 *
 * @param project 当前项目
 * @param file 虚拟文件
 * @return 对应的 TextEditor，如果无法获取则返回 null
 */
fun getTextEditorForVirtualFile(project: Project, file: VirtualFile): TextEditor? {
    val fileEditor = FileEditorManager.getInstance(project).getSelectedEditor(file)
    return fileEditor as? TextEditor
}

/**
 * 在编辑器中高亮显示指定的文本范围
 *
 * @param editor 编辑器实例
 * @param textRanges 需要高亮的文本范围列表
 * @param textAttributes 高亮样式
 */
fun highlightErrorCodes(
    editor: Editor,
    textRanges: MutableList<ErrorCodeOffset>,
    textAttributes: TextAttributes
) {
    val markupModel = editor.markupModel
    markupModel.removeAllHighlighters() // 清除旧的高亮标记

    textRanges.forEach { textRange ->
        markupModel.addRangeHighlighter(
            textRange.startOffset,
            textRange.endOffset,
            HighlighterLayer.ERROR,
            textAttributes,
            HighlighterTargetArea.EXACT_RANGE
        )
    }
}


/**
 * 处理重复的错误码并高亮，每次都会重新加载文件中所有的错误码
 */
fun errorCodeReportCodeHighlight(file: VirtualFile, project: Project) {

    if (checkIsErrorCodeFileInTargetPath(file, project)) {
        return
    }

    val psiFile = getPsiFileForVirtualFile(project, file) ?: return

    getErrorCodeMapConstants(listOf(psiFile), project.name)

    val textEditor = getTextEditorForVirtualFile(project, file) ?: return


    highlightErrorCodes(textEditor.editor, getFileRepeatErrorCodeOffset(project, file), REPEAT_ERROR_CODE_HIGHLIGHT)
}


data class ErrorCodeOffset(val startOffset: Int, val endOffset: Int)