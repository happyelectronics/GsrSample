package com.hecz.androidgsr;

public class DirItem {
    public String fileDir;
    public String fileSoundRecord;

    public DirItem() {
        this.fileDir = new String();
        this.fileSoundRecord = new String();
    }

    public DirItem(String fileDir) {
        this.fileDir = fileDir;
        this.fileSoundRecord = new String();
    }

    public DirItem(String fileDir, String fileSoundRecord) {
        this.fileDir = fileDir;
        this.fileSoundRecord = fileSoundRecord;
    }

    @Override
    public String toString() {
        return (hasSoundRecord() == true ? "* " + fileDir : fileDir);
    }

    public boolean hasSoundRecord() {
        if (fileSoundRecord == null || fileSoundRecord.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
