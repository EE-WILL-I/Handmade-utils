package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
//import javafx.event.EventHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.*;
//import org.omg.CORBA.portable.Delegate;

//import java.security.Key;
//import java.util.Timer;

//import java.awt.*;
//import java.beans.EventHandler;
import javafx.scene.control.Button;


public class Controller implements Initializable {

    public Label lbl_Folder_To, lbl_Extension, lbl_Folder_From;
    public TextField field_from, field_to, field_extension;
    public VBox vBox_copied;
    public static Stage PS;

    private Button btn_Set_From, btn_Set_To, btn_Translate;
    private File directory_from = new File("C:/"), directory_to = new File("C:/"), directory_ext;
    private DirectoryChooser dc_from = new DirectoryChooser(), dc_to = new DirectoryChooser();
    private FileChooser fc_ext = new FileChooser();
    private String extension = ".exe";
    private int copied_files_count = 0;
    private ArrayList<Label> lbl_list = new ArrayList<Label>();
    private ArrayList<File> files_copied_list = new ArrayList<File>();
    private ArrayList<Button> btn_list = new ArrayList<>();
    private ArrayList<HBox> hbox_list = new ArrayList<>();

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
        if (text[text.length - 1] != "") extension = text[text.length - 1];
        field_extension.setText("." + extension);
        System.out.println(extension);
    }

    private void translate() throws IOException {
        File file = new File("");
        File[] matches = directory_from.listFiles(new FilenameFilter() {
            public boolean accept(File directory_from, String name) {
                return name.endsWith(extension);
            }
        });
        assert matches != null;
        for (File f : matches) {
            System.out.println(f.getName());
            File temp = new File(directory_to.getAbsolutePath() + "/" + f.getName());
            if (temp.createNewFile()) {
                Files.copy(f.toPath(), temp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                files_copied_list.add(temp);

                Label lbl = new Label();
                lbl.setStyle("-fx-text-fill:#ffffff");
                lbl.setId("copied_file_" + copied_files_count);
                lbl.setText(temp.getName());
                lbl_list.add(lbl);

                Button btn = new Button();
                btn.setText("-");
                btn.setOnAction(e -> {this.Undone();});
                btn_list.add(btn);

                HBox hbox = new HBox();
                hbox.getChildren().add(lbl);
                hbox.getChildren().add(btn);
                hbox_list.add(hbox);

                if(!vBox_copied.getChildren().contains(hbox)) vBox_copied.getChildren().add(hbox);

                copied_files_count++;
                System.out.println(temp.getAbsolutePath());
            }
        }
    }
    private void Undone()
    {
        if(copied_files_count > 0) {

            for (File f: files_copied_list) {
                if(f.delete()) System.out.println("deleted");
            }
            for (Label lbl: lbl_list) {
              //  vBox_copied.getChildren().remove(lbl);
            }
            for(HBox box: hbox_list) {
                if (!box.getChildren().isEmpty()) {
                    box.getChildren().remove(1);
                    box.getChildren().remove(0);
                    vBox_copied.getChildren().remove(box);
                }
            }
            copied_files_count = 0;
        }
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
            translate();
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
        Undone();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFolder(dc_from, field_from, directory_from);
        setFolder(dc_to, field_to, directory_to);
        field_extension.setText(extension);
    }
}
