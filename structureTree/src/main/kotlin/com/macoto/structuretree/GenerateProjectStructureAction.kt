package com.macoto.structuretree

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.nio.charset.StandardCharsets

class GenerateProjectStructureAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (file != null && file.isDirectory) {
            val projectStructure = StringBuilder()
            val visited = mutableSetOf<VirtualFile>()
            generateStructure(file, projectStructure, 0, visited)
            val projectStructureStr = String(projectStructure.toString().toByteArray(), StandardCharsets.UTF_8)
            copyToClipboard(projectStructureStr)
            Messages.showInfoMessage("Project structure copied to clipboard", "Success")
        } else {
            Messages.showErrorDialog("Please select a folder.", "Error")
        }
    }

    private fun generateStructure(directory: VirtualFile, builder: StringBuilder, level: Int, visited: MutableSet<VirtualFile>) {
        if (!visited.add(directory)) return

        val indent = "│   ".repeat(level)
        builder.append(indent).append("├─ ").append(directory.name).append("/\n")

        val children = directory.children
        for ((index, child) in children.withIndex()) {
            val isLast = index == children.size - 1
            if (child.isDirectory) {
                generateStructure(child, builder, level + 1, visited)
            } else {
                builder.append(indent).append(if (isLast) "└─ " else "├─ ").append(child.name).append("\n")
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val stringSelection = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, null)
    }
}
