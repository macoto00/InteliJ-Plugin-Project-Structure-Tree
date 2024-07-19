package com.macoto.projectstructuretree;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class GenerateProjectStructureAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile file = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE);
        if (file != null && file.isDirectory()) {
            StringBuilder projectStructure = new StringBuilder();
            Set<VirtualFile> visited = new HashSet<>();
            generateStructure(file, projectStructure, 0, visited);
            String projectStructureStr = new String(projectStructure.toString().getBytes(), StandardCharsets.UTF_8);
            copyToClipboard(projectStructureStr);
            Messages.showInfoMessage("Project structure copied to clipboard", "Success");
        } else {
            Messages.showErrorDialog("Please select a folder.", "Error");
        }
    }

    private void generateStructure(VirtualFile directory, StringBuilder builder, int level, Set<VirtualFile> visited) {
        if (!visited.add(directory)) {
            return; // Avoid infinite recursion
        }

        String indent = "│   ".repeat(level);
        builder.append(indent).append("├── ").append(directory.getName()).append("/\n");

        VirtualFile[] children = directory.getChildren();
        for (int i = 0; i < children.length; i++) {
            VirtualFile child = children[i];
            boolean isLast = (i == children.length - 1);
            if (child.isDirectory()) {
                generateStructure(child, builder, level + 1, visited);
            } else {
                builder.append(indent).append(isLast ? "└── " : "├── ").append(child.getName()).append("\n");
            }
        }
    }

    private void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }
}
