package com.zxj.plugin.component;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by zhuxiujie
 */
public class MybatisComponent implements ApplicationComponent {

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "MybatisComponent";
    }

    public void sayHello() {
        //TODO Show dialog with message


    }
}
