package sample;

import java.io.File;

public class Renamer {
    enum RenamingType { PREFIX, FULL_NAME, POSTFIX};
    static String lastFullName = "";
    static int fullNameCount = 0;
    public static File Rename(File file, RenamingType type, String value)
    {
        if(!value.isEmpty()) {
            System.out.println(value);
            String tmpName = "";
            switch (type) {
                case PREFIX: {
                    tmpName = value + file.getName();
                    break;
                }
                case FULL_NAME: {
                    if(lastFullName.equals(value))
                    {
                        fullNameCount++;
                    }
                    else {
                        lastFullName = value;
                        fullNameCount = 0;
                    }
                    String[] text = file.getName().split("\\.");
                    tmpName = value + fullNameCount + "." + text[1];
                    break;
                }
                case POSTFIX: {
                    String[] text = file.getName().split("\\.");
                    tmpName = text[0] + value + "." + text[1];
                    break;
                }
            }
            if (!tmpName.isEmpty()) try {
                return new File(file.getAbsolutePath().replaceAll(file.getName(), tmpName));
            } catch (Exception ex) {
                return file;
            }
            else return file;
        }
        else return file;
    }
}
