package sample;

/** 
@author: Boyu Lu & Fanya Ma
@year 2019
*/

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.util.Date;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.CheckMenuItem;

import javafx.stage.Window;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.process.ProcessStarter;


public class Main extends Application {
    private Window window;
    private static final int W = 100;
    private static final int H = 100;

    @Override
    public void start(Stage primaryStage) {

        final FileChooser fileChooser = new FileChooser();
        final Button openButton = new Button("Open Image");

        openButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        setExtFilters(fileChooser);
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            openNewImageWindow(file);
                        }
                    }
                });

        StackPane root = new StackPane();
        root.getChildren().add(openButton);
        Scene scene = new Scene(root, 400, 150);
        primaryStage.setTitle("Choose Your Image");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setExtFilters(FileChooser chooser) {
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
    }

    private void openNewImageWindow(File file) {
        Stage secondStage = new Stage();

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Convert");
        MenuItem menuItem_Save = new MenuItem("Convert Image to");
        Menu menuFile1 = new Menu("Filter");
        Menu menuItem1_Save = new Menu("Black and White");
        menuItem1_Save.getItems().addAll(
                new CheckMenuItem("Confirm"));

        menuFile.getItems().addAll(menuItem_Save);
        menuFile1.getItems().addAll(menuItem1_Save);
        menuBar.getMenus().addAll(menuFile, menuFile1);

        Label name = new Label(file.getAbsolutePath());
        javaxt.io.Image imageInfo = new javaxt.io.Image(file.getAbsoluteFile());
        java.util.HashMap<Integer, Object> exif = imageInfo.getExifTags();
        System.out.println("Date: " + exif.get(0x0132));
        System.out.println("Camera: " + exif.get(0x0110));
        System.out.println("Focal Length: " + exif.get(0x920A));
        System.out.println("F-Stop: " + exif.get(0x829D));
        System.out.println("Shutter Speed: " + exif.get(0x829A));

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
            System.out.println("Location : " + name);
            System.out.println("Height = " + bufferedImage.getHeight());
            System.out.println("Width = " + bufferedImage.getWidth());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        Label height = new Label("Height = " + String.valueOf(bufferedImage.getHeight()));
        Label width = new Label("Width = " + String.valueOf(bufferedImage.getWidth()));
        Label Date = new Label("Date = " + String.valueOf(exif.get(0x0132)));
        Label Camera = new Label("Camera = " + String.valueOf(exif.get(0x0110)));
        Label FocalLength = new Label("Focal Length: = " + String.valueOf(exif.get(0x920A)));
        Label FStop = new Label("F-Stop = " + String.valueOf(exif.get(0x829D)));
        Label ShutterSpeed = new Label("Shutter Speed = " + String.valueOf(exif.get(0x829A)));
        ImageView imageView = new ImageView();

        final BufferedImage imageToWrite =
                new BufferedImage(
                        bufferedImage.getWidth(),
                        bufferedImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

        imageToWrite.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

        menuItem_Save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                        new FileChooser.ExtensionFilter("PNG", "*.png"));
                fileChooser.setInitialFileName(String.valueOf(file));
                File fileToBeSaved = fileChooser.showSaveDialog(window);
                String destination = fileToBeSaved.toString();
                String extention = destination.substring(destination.lastIndexOf("."));
                String sourcePath = file.toString();

                ConvertCmd cmd = new ConvertCmd();
                IMOperation op = new IMOperation();
                int width = W;
                int height = H;
                Info imageInfo;
                try {
                    imageInfo = new Info(sourcePath, false);
                    width = imageInfo.getImageWidth();
                    height = imageInfo.getImageHeight();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                op.addImage(sourcePath);
                op.resize(width, height);
                op.addImage(destination);

                if (file != null) {
                    try {
                        cmd.run(op);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }
        });

        final VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 10, 0, 10));
        vbox.getChildren().addAll(name, height, width, Date, Camera, FocalLength, FStop, ShutterSpeed, imageView);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        imageView.setSmooth(true);
        imageView.setCache(true);
        Scene scene = new Scene(new VBox(), 400, 600);
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, vbox);
        secondStage.setTitle(file.getName());
        secondStage.setScene(scene);
        secondStage.show();

        menuItem1_Save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");

                fileChooser.setInitialFileName(String.valueOf(file));
                fileChooser.setTitle("Save");

                String sourcePath = file.toString();

                Info imageInfo;
                try {
                    long time = new Date().getTime();
                    makeImageColorToBlackWhite(file);
                    System.out.println("The conversion time is " + (new Date().getTime()-time) + "ms");

                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });
    }
    public static void makeImageColorToBlackWhite(File file) {
        //String imagePath = file.getPath();
        BufferedImage newbi = null;
        int[][] result = getImageGRB(file);
        int[] rgb = new int[3];
        BufferedImage bi = new BufferedImage(result.length, result[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                rgb[0] = (result[i][j] & 0xff0000) >> 16;
                rgb[1] = (result[i][j] & 0xff00) >> 8;
                rgb[2] = (result[i][j] & 0xff);
                int color = (int) (rgb[0] * 0.3 + rgb[1] * 0.59 + rgb[2] * 0.11);
                bi.setRGB(i, j, (color << 16) | (color << 8) | color);
            }
        }

        try {

            ImageIO.write(bi, "jpeg", new File("result.jpeg"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[][] getImageGRB(File file) {
        int [][]result = null;
        try {
            BufferedImage bufImg = ImageIO.read(file);
            int height = bufImg.getHeight();
            int width = bufImg.getWidth();
            result = new int[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i][j] = bufImg.getRGB(i,j)&0xFFFFFF;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
