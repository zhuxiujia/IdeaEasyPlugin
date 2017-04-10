package com.zxj.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.zxj.plugin.component.MybatisComponent;

/**
 * Created by zhuxiujie
 */
public class AddMybitsFlagAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here

        Application application = ApplicationManager.getApplication();
        MybatisComponent mybatisComponent=application.getComponent(MybatisComponent.class);
        mybatisComponent.sayHello();
    }
}
