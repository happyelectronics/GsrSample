package com.hecz.androidgsr;

public class DirItemWithPath extends DirItem {
    public String fullPathOfFiles = new String();

    public void setDirItem(DirItem item) {
        fileDir = item.fileDir;
        fileSoundRecord = item.fileSoundRecord;
    }
}
