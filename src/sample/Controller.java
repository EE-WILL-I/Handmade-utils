package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import javafx.scene.control.Button;

public class Controller implements Initializable {

    private class CopiedFileData {
        private File file;
        private Button label = new Button();
        private Button button = new Button();
        private HBox hbox = new HBox();

        public CopiedFileData(File f)
        {
            file = f;

            label.setStyle("-fx-text-fill:#ffffff; -fx-background-color:#19191a;");
            label.setId("copied_file_" + copiedFilesCount);
            label.setText(f.getName());
            label.setOnAction(e -> {
                try {
                    Desktop.getDesktop().open(new File((f.getAbsoluteFile().toString().replaceAll(f.getName(), ""))));
                } catch (IOException ex) {
                    System.out.println("Cannot open " + f.getName() + " file directory");
                }
            });

            button.setText("-");
            button.setStyle("-fx-background-color:#19191a");
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
            vBoxCopied.getChildren().remove(hbox);

        }
        public HBox getHbox()
        {
            return hbox;
        }
        public File getFile()
        {
            return  file;
        }
        public Button getLabel(){
            return label;
        }
        public Button getButton()
        {
            return  button;
        }
    }

    public Label labelFolderTo, labelExtension, labelFolderFrom;
    public TextField fieldFrom, fieldTo, fieldExtension;
    public VBox vBoxCopied;
    public static Stage PS;

    @FXML
    private Slider sliderRecursionLevel;
    private File directoryFrom = new File("C:/"), directoryTo = new File("C:/"), directoryToExtension;
    private DirectoryChooser dcFrom = new DirectoryChooser(), dcTo = new DirectoryChooser();
    private FileChooser fcExt = new FileChooser();
    @FXML
    private Label labelInfo;
    private String extension = ".exe";
    private int copiedFilesCount = 0, recursionLevel = 0;
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
        fieldExtension.setText("." + extension);
        System.out.println(extension);
    }

    private void getInnerDirectory(File dirFrom, int recLevel) throws IOException {
        if(dirFrom != directoryTo) {
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

    private void translate(File dirFrom) throws IOException {
        File file = new File("");
        File[] matches = dirFrom.listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.endsWith(extension);
            }
        });
        assert matches != null;
        for (File f : matches) {
            System.out.println(f.getName());
            File temp = new File(directoryTo.getAbsolutePath() + "/" + f.getName());
            temp = Renamer.Rename(temp, Renamer.RenamingType.PREFIX, "copyof_");
            if (temp.createNewFile()) {
                Files.copy(f.toPath(), temp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                copiedFileDataList.add(new CopiedFileData(temp));
                HBox hb = ((CopiedFileData)copiedFileDataList.toArray()[copiedFilesCount]).getHbox();
                if(!vBoxCopied.getChildren().contains(hb)) vBoxCopied.getChildren().add(hb);

                copiedFilesCount++;

                labelInfo.setText("Copied: " + copiedFilesCount + " files.");
                System.out.println(temp.getAbsolutePath());
            }
        }
    }
    private void removeAll() {
        if(copiedFilesCount > 0) {
            ArrayList<CopiedFileData> temp = copiedFileDataList;
            for(CopiedFileData data: copiedFileDataList) {
                data.remove();
            }
            copiedFileDataList.removeAll(temp);
            labelInfo.setText("Deleted: " + copiedFilesCount + " files.");
            copiedFilesCount = 0;
        }
    }
    private void removeCopiedFile(Button btn) {
        CopiedFileData temp = null;
        for(CopiedFileData data: copiedFileDataList) {
            if(data.getButton().equals(btn))
            {
                data.remove();
                temp = data;

                labelInfo.setText("Deleted file: " + temp.file.getName());
            }
        }
        if(temp != null) copiedFileDataList.remove(temp);
        copiedFilesCount--;
    }

    public void btnSetFolder_From(ActionEvent actionEvent) {
        directoryFrom = dcFrom.showDialog(PS);
        if (directoryFrom != null)
            setFolder(dcFrom, fieldFrom, directoryFrom);
    }

    public void btnSetFolder_To(ActionEvent actionEvent) {
        directoryTo = dcTo.showDialog(PS);
        if (directoryTo != null)
            setFolder(dcTo, fieldTo, directoryTo);
    }

    public void btnSetExtension(ActionEvent actionEvent) {
        directoryToExtension = fcExt.showOpenDialog(PS);
        if (directoryToExtension != null)
            setExtension(directoryToExtension.getAbsolutePath());
    }

    public void btmTranslate(ActionEvent actionEvent) throws IOException {
        if ((!dcFrom.getInitialDirectory().getAbsolutePath().isEmpty()) && (!dcTo.getInitialDirectory().getAbsolutePath().isEmpty()) && (!extension.isEmpty()))
            getInnerDirectory(directoryFrom, recursionLevel);
    }

    public void field_OnFolderFromChanged(KeyEvent inputMethodEvent) {
        setFolder(dcFrom, fieldFrom.getText());
    }

    public void field_OnExtensionChanged(KeyEvent inputMethodEvent) {
        extension = fieldExtension.getText();
        System.out.println(extension);
    }

    public void field_OnFolderToChanged(KeyEvent inputMethodEvent) {
        setFolder(dcTo, fieldTo.getText());
    }

    public void btnRemove(ActionEvent actionEvent) {
        removeAll();
    }

    public void sliderRecursionLevel(MouseEvent mouseEvent) {
        sliderRecursionLevel.setValue((int) sliderRecursionLevel.getValue());
        recursionLevel = (int) sliderRecursionLevel.getValue();
        System.out.println(recursionLevel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFolder(dcFrom, fieldFrom, directoryFrom);
        setFolder(dcTo, fieldTo, directoryTo);
        fieldExtension.setText(extension);
    }
}
