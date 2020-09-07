package sample;

import java.io.File;

public class Renamer {
    enum RenamingType { PREFIX, FULL_NAME, POSTFIX};
    public static File Rename(File file, RenamingType type, String value)
    {
        String tmpName = "";
        switch (type)
        {
            case PREFIX: {
                tmpName = value + file.getName();
                break;
            }
            case FULL_NAME: {
                String[] text = file.getName().split("\\.");
                tmpName = value + "." + text[1];
                break;
            }
            case POSTFIX: {
                String[] text = file.getName().split("\\.");
                tmpName = text[0] + value + "." + text[1];
                break;
            }
        }
        if(!tmpName.isEmpty()) return new File(file.getAbsolutePath().replaceAll(file.getName(), tmpName));
        else return file;
    }
}
