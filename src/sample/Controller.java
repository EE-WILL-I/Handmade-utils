package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
//import javafx.event.EventHandler;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
//import org.omg.CORBA.portable.Delegate;

//import java.security.Key;
//import java.util.Timer;

//import java.awt.*;
//import java.beans.EventHandler;
import javafx.scene.control.Button;


public class Controller implements Initializable {

    private class CopiedFileData {
        private File file;
        private Label label = new Label();
        private Button button = new Button();
        private HBox hbox = new HBox();

        public CopiedFileData(File f)
        {
            file = f;

            label.setStyle("-fx-text-fill:#ffffff");
            label.setId("copied_file_" + copied_files_count);
            label.setText(f.getName());

            button.setText("-");
            button.setStyle("-fx-background-color:#1e1e1e");
            button.setTextFill(Paint.valueOf("#ffe712"));
            button.setMaxHeight(10);
            button.setTextAlignment(TextAlignment.CENTER);
            button.setOnAction(e -> { removeCopiedFile(button);});

            hbox.getChildren().add(label);
            hbox.getChildren().add(button);
        }
        public void remove() {
            if(file.delete()) System.out.println("deleted");
            hbox.getChildren().remove(1);
            hbox.getChildren().remove(0);
            vBox_copied.getChildren().remove(hbox);

        }
        public HBox getHbox()
        {
            return hbox;
        }
        public File getFile()
        {
            return  file;
        }
        public Label getLabel(){
            return label;
        }
        public Button getButton()
        {
            return  button;
        }
    }

    public Label lbl_Folder_To, lbl_Extension, lbl_Folder_From;
    public TextField field_from, field_to, field_extension;
    public VBox vBox_copied;
    public static Stage PS;

    //private Button btn_Set_From, btn_Set_To, btn_Translate;
    @FXML
    private Slider slider_recursionLevel;
    private File directory_from = new File("C:/"), directory_to = new File("C:/"), directory_ext;
    private DirectoryChooser dc_from = new DirectoryChooser(), dc_to = new DirectoryChooser();
    private FileChooser fc_ext = new FileChooser();
    private String extension = ".exe";
    private int copied_files_count = 0, recursionLevel = 0;
    private ArrayList<CopiedFileData> copiedFileDataList = new ArrayList<>();

    private void setFolder(DirectoryChooser dc, String path) {
        File dir = new File(path);
        if (dir.isDirectory())
            dc.setInitialDirectory(dir);
        System.out.println(dc.getInitialDirectory().getAbsolutePath());
    }

    private void setFolder(DirectoryChooser dc, TextField tf, File dir) {
        dc.setInitialDirectory(dir);
        tf.setText(dir.getAbsolutePath());
        System.out.println(dir.getAbsolutePath());
    }

    private void setExtension(String ext) {
        File file = new File(ext);
        ext = file.getName();
        String[] text = ext.split("\\.");
        if (!text[text.length - 1].equals("")) extension = text[text.length - 1];
        field_extension.setText("." + extension);
        System.out.println(extension);
    }

    private void getInnerDirectory(File dirFrom, int recLevel) throws IOException {
        if(dirFrom != directory_to) {
            translate(dirFrom);
            System.out.println(dirFrom.getAbsolutePath());

            int _recursionLevel = recLevel;
            ArrayList<File> dirs = new ArrayList<>();
            if (_recursionLevel > 0) {
                File[] _dirs = dirFrom.listFiles(new FileFilter() {
                    public boolean accept(File dir) {
                        return dir.isDirectory();
                    }
                });
                if (_dirs != null) dirs.addAll(Arrays.asList(_dirs));
                _recursionLevel--;
                for (File d : dirs) {
                    int final_recursionLevel = _recursionLevel;

                    Platform.runLater(
                            () -> {
                                try {
                                    getInnerDirectory(d, final_recursionLevel);
                                } catch (IOException e) {
                                    System.out.println("Error in " + dirFrom.getAbsolutePath());
                                }
                            }
                    );
                }
            }
        }
    }

    private void translate(File dirfrom) throws IOException {
        File file = new File("");
        File[] matches = dirfrom.listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.endsWith(extension);
            }
        });
        assert matches != null;
        for (File f : matches) {
            System.out.println(f.getName());
            File temp = new File(directory_to.getAbsolutePath() + "/" + f.getName());
            if (temp.createNewFile()) {
                Files.copy(f.toPath(), temp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                copiedFileDataList.add(new CopiedFileData(temp));
                HBox hb = ((CopiedFileData)copiedFileDataList.toArray()[copied_files_count]).getHbox();
                if(!vBox_copied.getChildren().contains(hb)) vBox_copied.getChildren().add(hb);

                copied_files_count++;
                System.out.println(temp.getAbsolutePath());
            }
        }
    }
    private void undone() {
        if(copied_files_count > 0) {
            ArrayList<CopiedFileData> temp = copiedFileDataList;
           for(CopiedFileData data: copiedFileDataList) {
               data.remove();
           }
            copiedFileDataList.removeAll(temp);
            copied_files_count = 0;
        }
    }
    private void removeCopiedFile(Button btn) {
        CopiedFileData temp = null;
        for(CopiedFileData data: copiedFileDataList) {
            if(data.getButton().equals(btn))
            {
                data.remove();
                temp = data;
            }
        }
        if(temp != null) copiedFileDataList.remove(temp);
        copied_files_count--;
    }

    public void btnSetFolder_From(ActionEvent actionEvent) {
        directory_from = dc_from.showDialog(PS);
        if (directory_from != null)
            setFolder(dc_from, field_from, directory_from);
    }

    public void btnSetFolder_To(ActionEvent actionEvent) {
        directory_to = dc_to.showDialog(PS);
        if (directory_to != null)
            setFolder(dc_to, field_to, directory_to);
    }

    public void btnSetExtension(ActionEvent actionEvent) {
        directory_ext = fc_ext.showOpenDialog(PS);
        if (directory_ext != null)
            setExtension(directory_ext.getAbsolutePath());
    }

    public void btmTranslate(ActionEvent actionEvent) throws IOException {
        if ((!dc_from.getInitialDirectory().getAbsolutePath().isEmpty()) && (!dc_to.getInitialDirectory().getAbsolutePath().isEmpty()) && (!extension.isEmpty()))
            //translate(directory_from);
            getInnerDirectory(directory_from, recursionLevel);
    }

    public void field_OnFolderFromChanged(KeyEvent inputMethodEvent) {
        setFolder(dc_from, field_from.getText());
    }

    public void field_OnExtensionChanged(KeyEvent inputMethodEvent) {
        extension = field_extension.getText();
        System.out.println(extension);
    }

    public void field_OnFolderToChanged(KeyEvent inputMethodEvent) {
        setFolder(dc_to, field_to.getText());
    }

    public void btnUndone(ActionEvent actionEvent) {
        undone();
    }

    public void sliderRecursionLevel(MouseEvent mouseEvent) {
        slider_recursionLevel.setValue((int)slider_recursionLevel.getValue());
        recursionLevel = (int) slider_recursionLevel.getValue();
        System.out.println(recursionLevel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFolder(dc_from, field_from, directory_from);
        setFolder(dc_to, field_to, directory_to);
        field_extension.setText(extension);
    }
}
