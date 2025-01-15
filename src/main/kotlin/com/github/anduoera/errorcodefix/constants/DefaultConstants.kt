package com.github.anduoera.errorcodefix.constants

import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

const val FOUNDATION_TAG = "foundation"
const val ERROR_CODE_BASE_DIR = "/model/exception"
const val ERROR_CODE_BASE_DIR_SKIP_FILE = "application.go"
const val ERROR_CODE_BASE_START = "ErrorCode"
const val ERROR_MESSAGE_BASE_START = "ErrorMessage"
const val ERROR_CODE_RESOLVE_START = "exception.ErrorCode"
const val ERROR_MESSAGE_RESOLVE_START = "exception.ErrorMessage"
val REPEAT_ERROR_CODE_HIGHLIGHT = TextAttributes(
    null, // 前景色（文本颜色）
    JBColor(Color(255, 157, 0, 70), Color(255, 157, 0, 70)),
    null, // 边框颜色
    null, // 边框效果
    Font.PLAIN // 字体样式
)