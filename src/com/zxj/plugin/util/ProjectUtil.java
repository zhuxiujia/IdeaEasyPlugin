package com.zxj.plugin.util;

import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFileManager;

/**
 * @author zhuxiujie
 * @since 2017/4/10
 */

public class ProjectUtil {
    public static void invate(){
        FileDocumentManager.getInstance().saveAllDocuments();
        SaveAndSyncHandler.getInstance().refreshOpenFiles();
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }
}
