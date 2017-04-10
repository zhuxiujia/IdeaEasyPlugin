package com.zxj.plugin.component;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zhuxiujie
 */
public class MybatisProjectComponent implements ProjectComponent {
    Project project;

    public MybatisProjectComponent(Project project) {
        System.out.println("print project path:" + project.getBaseDir().toString());
        this.project = project;
    }

    @Override
    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    @Override
    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "MybatisProjectComponent";
    }

    @Override
    public void projectOpened() {
        // called when project is opened
    }

    @Override
    public void projectClosed() {
        // called when project is being closed
    }

}
