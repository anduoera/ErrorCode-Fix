package com.github.anduoera.errorcodefix.listeners

import com.github.anduoera.errorcodefix.constants.*
import com.github.anduoera.errorcodefix.utils.findPsiFilesInDirectory
import com.github.anduoera.errorcodefix.utils.getErrorCodeMapConstants
import com.github.anduoera.errorcodefix.utils.hasPath
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * 项目打开启动
 */
class ProjectStartupListeners : ProjectActivity {

    override suspend fun execute(project: Project) {

        val projectName = project.name

        if (projectName.endsWith(FOUNDATION_TAG) || !hasPath(project, ERROR_CODE_BASE_DIR)) {
            return
        }

        val psiGoFiles = findPsiFilesInDirectory(project, ERROR_CODE_BASE_DIR)
        getErrorCodeMapConstants(psiGoFiles, project.name)
    }
}