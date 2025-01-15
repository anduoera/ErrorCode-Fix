package com.github.anduoera.errorcodefix.constants

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ErrorCodeListConstants private constructor() {

    companion object {
        val instance: ErrorCodeListConstants by lazy { ErrorCodeListConstants() }
    }

    private val errorCodeList: MutableMap<String, MutableMap<String, MutableList<String>>> = ConcurrentHashMap()
    private val errorCodeCountMap: MutableMap<String, MutableMap<String, Int>> = ConcurrentHashMap()

    /**
     * 根据项目名字获取所有
     */
    fun getErrorCodeListByProjectName(projectName: String): MutableMap<String, MutableList<String>> {
        return errorCodeList[projectName] ?: mutableMapOf()
    }

    /**
     * 添加一个错误码到指定文件和项目的 TreeSet 中
     */
    fun addErrorCode(projectName: String, fileName: String, errorCode: String) {
        // 添加到 List
        errorCodeList
            .computeIfAbsent(projectName) { ConcurrentHashMap() }
            .computeIfAbsent(fileName) { ArrayList() }
            .add(errorCode)

        // 更新错误码的出现次数
        errorCodeCountMap
            .computeIfAbsent(projectName) { ConcurrentHashMap() }
            .merge(errorCode, 1, Int::plus) // 如果错误码已存在，则累加次数
    }

    /**
     * 检查某个错误码在指定项目中是否重复
     */
    fun isErrorCodeDuplicate(projectName: String, errorCode: String): Boolean {
        return (errorCodeCountMap[projectName]?.get(errorCode) ?: 0) > 1
    }

    /**
     * 判断错误码是否存在
     */
    fun isErrorCodeExist(projectName: String, errorCode: String): Boolean {
        return (errorCodeCountMap[projectName]?.get(errorCode) ?: 0) > 0
    }

    /**
     * 检查某个错误码是否存在于指定项目的 TreeSet 中
     */
    fun checkErrorCodeExists(projectName: String, errorCode: String): Boolean {
        val projectMap = errorCodeList[projectName] ?: return false

        for (errorCodeSet in projectMap.values) {
            if (errorCodeSet.contains(errorCode)) {
                return true
            }
        }
        return false
    }

    /**
     * 获取指定项目和文件的错误码 TreeSet
     */
    fun getErrorCodes(projectName: String, fileName: String): List<String> {
        return errorCodeList[projectName]?.get(fileName) ?: emptyList()
    }

    /**
     * 根据 projectName 插入一组文件及其对应的错误码集合
     */
    fun addProjectErrorCode(projectName: String, errorCodes: MutableMap<String, MutableList<String>>) {
        errorCodeList
            .computeIfAbsent(projectName) { ConcurrentHashMap() }
            .putAll(errorCodes)

        // 更新错误码的出现次数
        errorCodes.values.forEach { errorCodeSet ->
            errorCodeSet.forEach { errorCode ->
                errorCodeCountMap
                    .computeIfAbsent(projectName) { ConcurrentHashMap() }
                    .merge(errorCode, 1, Int::plus) // 如果错误码已存在，则累加次数
            }
        }
    }

    /**
     * 获取所有文件的错误码 Map
     */
    fun getAllErrorCodes(): Map<String, Map<String, MutableList<String>>> {
        return errorCodeList
    }

    /**
     * 删除指定项目和文件中的错误码
     */
    fun removeErrorCode(projectName: String, fileName: String, errorCode: String): Boolean {
        val projectMap = errorCodeList[projectName] ?: return false
        val fileErrorCodes = projectMap[fileName] ?: return false

        val isRemoved = fileErrorCodes.remove(errorCode)

        if (isRemoved) {
            errorCodeCountMap[projectName]?.let { countMap ->
                countMap[errorCode]?.let { currentCount ->
                    if (currentCount > 1) {
                        countMap[errorCode] = currentCount - 1
                    } else {
                        countMap.remove(errorCode)
                    }
                }
            }

            if (fileErrorCodes.isEmpty()) {
                projectMap.remove(fileName)
            }

            if (projectMap.isEmpty()) {
                errorCodeList.remove(projectName)
            }
        }

        return isRemoved
    }

    /**
     * 清空所有错误码
     */
    fun clearAllErrorCodes() {
        errorCodeList.clear()
        errorCodeCountMap.clear()
    }

    /**
     * 删除指定项目和文件名下的所有错误码
     *
     * @param projectName 项目名称
     * @param fileName 文件名
     * @return 如果成功删除文件下的所有错误码，返回 true；否则返回 false
     */
    fun removeErrorCodesByFileName(projectName: String, fileName: String): Boolean {
        val projectMap = errorCodeList[projectName] ?: return false
        val fileErrorCodes = projectMap[fileName] ?: return false

        // 遍历文件中的所有错误码，更新错误码的出现次数
        fileErrorCodes.forEach { errorCode ->
            errorCodeCountMap[projectName]?.let { countMap ->
                countMap[errorCode]?.let { currentCount ->
                    if (currentCount > 1) {
                        countMap[errorCode] = currentCount - 1
                    } else {
                        countMap.remove(errorCode)
                    }
                }
            }
        }

        // 删除文件下的所有错误码
        projectMap.remove(fileName)

        // 如果项目中的文件集合为空，则删除项目
        if (projectMap.isEmpty()) {
            errorCodeList.remove(projectName)
        }

        return true
    }

    /**
     * 获取指定项目下，指定文件下的所有错误码最新不重复的错误码
     */
    fun getNewErrorCodes(projectName: String, fileName: String): String {
        val errorCodes = getErrorCodes(projectName, fileName)
        val maxErrorCode = errorCodes.maxOfOrNull { it.toInt() }
        if (maxErrorCode == null) {
            return "0000"
        }
        for (i in 0 until 100) {
            val nextNumber = maxErrorCode.plus(1)
            val errorCode = String.format("%04d", nextNumber)
            if (errorCode.isNotEmpty() && !isErrorCodeExist(projectName, errorCode)) {
                return errorCode
            }
        }
        return "0000"

    }
}