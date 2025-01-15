package com.github.anduoera.errorcodefix.setting

import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class ErrorCodeGenerateAppSettingsComponent() {
    private var settingsPanel = JPanel()
    private val textFields: MutableList<JTextField> = mutableListOf()

    init {
        settingsPanel = settingsPanel.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        val addButton = JButton("addSetting").apply {
            actionCommand = "addSetting"
            addActionListener {
                addSettingComponent()
            }
        }

        // 创建按钮面板并添加到主面板
        val buttonPanel = JPanel().apply {
            layout = BorderLayout()
            add(addButton, BorderLayout.PAGE_START)
        }
        settingsPanel!!.add(buttonPanel)
    }

    private fun addSettingComponent(value: String = "ErrorCode") {
        // 创建单个配置项的面板
        val itemPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
        }

        // 创建并配置文本输入框
        val textField = JTextField(value, 10).apply {
            preferredSize = Dimension(200, 30)
            toolTipText = "Enter the ErrorCode here."
        }
        textFields.add(textField)

        // 将标签、间距和输入框添加到面板
        itemPanel.add(JLabel("item:"))
        itemPanel.add(Box.createHorizontalStrut(10))
        itemPanel.add(textField)

        // 将配置项面板插入到主面板的按钮面板之前
        settingsPanel!!.add(itemPanel, settingsPanel!!.componentCount - 1)
        settingsPanel!!.revalidate()
        settingsPanel!!.repaint()
    }

    fun getPanel(): JPanel {
        return settingsPanel
    }

    fun getAllErrorCode(): List<String> {
        return textFields.map { it.text }.toMutableList()
    }

    fun setErrorCode(value: String) {
        addSettingComponent(value)
    }
}