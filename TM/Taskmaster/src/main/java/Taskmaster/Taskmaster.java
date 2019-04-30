package Taskmaster;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.Background;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.stage.FileChooser;
import javafx.scene.media.*;
import java.util.Date;
import java.time.LocalDate;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author David
 */
public class Taskmaster extends Application{
    
    private ArrayList<MyTask> taskList = new ArrayList<>();
    private ArrayList<Chara> cList = new ArrayList<>();
    private ArrayList<Button> bList = new ArrayList<>();
    private ArrayList<String> fList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaClick;
    Button selected = null;
    StackPane root;
    
    //Here's the google stuff
    private static final String APPLICATION_NAME = "Google Tasks API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(TasksScopes.TASKS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Taskmaster.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    //Google ends here
    
    //Start Taskmaster.
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException,IOException,ClassNotFoundException{
        root = new StackPane();
        
        //Save the taskList when the stage is closed.
        primaryStage.setOnCloseRequest(event -> {
            try{
                save();
            }
            catch(FileNotFoundException e){
                System.out.println("File not found");
            }
            catch(IOException i){
                System.out.println("IOException");
            }
        });
        
        File tmp = new File("t.tmp");
        //If a prior list exists, load it.
        if(tmp.exists()){
            FileInputStream fis = new FileInputStream("t.tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            taskList = (ArrayList<MyTask>) ois.readObject();
            ois.close();
            
            for(int i = 0; i < 10; i++){
                cList.add(new Chara(root, this));
                if(taskList.get(i).getDeadline() != null)
                    cList.get(i).start();
            }
        }
        else{
            for(int i = 0; i < 10; i++){
                taskList.add(new MyTask());
                cList.add(new Chara(root, this));
            }
        }
        
        File custmp = new File("f.tmp");
        //If a prior list exists, load it.
        if(custmp.exists()){
            FileInputStream fis2 = new FileInputStream("f.tmp");
            ObjectInputStream ois2 = new ObjectInputStream(fis2);
            fList = (ArrayList<String>) ois2.readObject();
            ois2.close();
        }
        
        //Create the MyTask creation buttons.
        createButtons(root, primaryStage);
        
        Scene scene = new Scene(root, 1000, 800);
        
        //Create and set the background image for the main screen.
        BackgroundImage myBI= new BackgroundImage(new Image("bgbig.png",1000,800,false,true),
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));
        
        
        Media theme = new Media(new File("src/main/resources/toro.mp3").toURI().toString());
        MediaPlayer mediaTheme = new MediaPlayer(theme);
        mediaTheme.setVolume(.2);
        mediaTheme.play();
        
        
        Slider s = new Slider(0,1,.2);
        s.setOrientation(Orientation.HORIZONTAL);
        s.setMaxWidth(200);
        s.setShowTickMarks(true);
        s.setMajorTickUnit(5);
        s.setMinorTickCount(3);
        s.setShowTickLabels(false);
        
        s.valueProperty().addListener(
            (observable, oldvalue, newvalue) ->
            {
                double i = newvalue.doubleValue();
                mediaTheme.setVolume(i);
            } );
        root.getChildren().add(s);
        s.setTranslateY(375);
        s.setTranslateX(-380);

        primaryStage.setTitle("Taskmaster");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    //Method for creating and establishing the functions of the task creation buttons.
    public void createButtons(StackPane s, Stage stage){
        FileChooser fileChooser = new FileChooser();
        for(int i = 0; i < 10; i++){
            Button add = new Button();
            if(taskList.get(i).getDeadline() != null)
                add.setText(taskList.get(i).getName() + "\n" + taskList.get(i).getDesc() + "\n" + "Deadline: " + taskList.get(i).getDeadline());
            else
                add.setText("Create new task");
            
            int id = i;
            add.setStyle("-fx-background-image: url('button.png');" + "-fx-font-family: 'Abel';" + "-fx-font-size: 15;");
            
            //The task buttons open up a window for editing the specified task slot.
            add.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event){
                    Media sound2 = new Media(new File("src/main/resources/click2.wav").toURI().toString());
                    mediaClick = new MediaPlayer(sound2);
                    mediaClick.setVolume(.2);
                    mediaClick.play();
                    
                    StackPane makeTask = new StackPane(); 
                    Scene scene = new Scene(makeTask, 400, 400); 
                    Stage stage = new Stage();
                    //Create and set the background image for the main screen.
                    BackgroundImage myBI= new BackgroundImage(new Image("bg2.png",400,400,false,true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    makeTask.setBackground(new Background(myBI));
                    stage.setScene(scene); 
                    stage.setTitle("Create a new Task!");

                    //Create and show the Name text field and label.
                    TextField nameInput = new TextField();
                    Label nameLabel = new Label("Name: ");
                    nameInput.setMaxWidth(160);
                    nameInput.setTranslateX(40);
                    nameInput.setTranslateY(-140);
                    nameInput.setText(taskList.get(id).getName());
                    nameLabel.setTranslateX(-117);
                    nameLabel.setTranslateY(-140);
                    makeTask.getChildren().add(nameLabel);
                    makeTask.getChildren().add(nameInput);
                    
                    //Create and show the Description text field and label.
                    TextArea descInput = new TextArea();
                    Label descLabel = new Label("Description: ");
                    descInput.setMaxWidth(150);
                    descInput.setTranslateX(40);
                    descInput.setTranslateY(-60);
                    descInput.setText(taskList.get(id).getDesc());
                    descLabel.setTranslateX(-100);
                    descLabel.setTranslateY(-115);
                    descInput.setMaxHeight(120);
                    descInput.setMinWidth(160);
                    descInput.setWrapText(true);
                    makeTask.getChildren().add(descLabel);
                    makeTask.getChildren().add(descInput);
                    
                    //Create and show the Deadline label and date picker.
                    DatePicker datePicker = new DatePicker();
                    Label deadLabel = new Label("Deadline: ");
                    datePicker.setTranslateX(40);
                    datePicker.setTranslateY(20);
                    datePicker.setValue(taskList.get(id).getDeadline());
                    deadLabel.setTranslateX(-110);
                    deadLabel.setTranslateY(20);
                    makeTask.getChildren().add(datePicker);
                    makeTask.getChildren().add(deadLabel);

                    Button create = new Button();
                    create.setText("Set Task");
                    create.setOnAction(new EventHandler<ActionEvent>(){
                        @Override
                        public void handle(ActionEvent t){
                            Media sound2 = new Media(new File("src/main/resources/click2.wav").toURI().toString());
                            mediaClick = new MediaPlayer(sound2);
                            mediaClick.setVolume(.2);
                            mediaClick.play();
                            
                            if(nameInput.getText() != "" && descInput.getText() != "" && datePicker.getValue() != null){
                                taskList.get(id).setName(nameInput.getText());
                                taskList.get(id).setDesc(descInput.getText());
                                taskList.get(id).setDate(datePicker.getValue());
                                add.setText(taskList.get(id).getName() + "\n" + taskList.get(id).getDesc() + "\n" + "Deadline: " + taskList.get(id).getDeadline());
                                if(cList.get(id).healed == false)
                                    cList.get(id).start();
                                else
                                    cList.get(id).reset();
                                stage.close();
                            }
                            else{
                                Label blank = new Label("One of the fields was not entered.");
                                blank.setTranslateY(60);
                                blank.setTranslateX(10);
                                makeTask.getChildren().add(blank);
                            }
                        }
                    });
                    create.setStyle("-fx-background-image: url('button2.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
                    create.setTranslateX(-125);
                    create.setTranslateY(110);
                    create.setMaxHeight(72);
                    create.setMaxWidth(120);
                    makeTask.getChildren().add(create);
                    
                    Button complete = new Button();
                    complete.setText("Complete!");
                    complete.setOnAction(new EventHandler<ActionEvent>(){
                        @Override
                        public void handle(ActionEvent t){
                            taskList.set(id, new MyTask());
                            add.setText("Create new task.");
                            cList.get(id).heal();
                            
                            Media sound = new Media(new File("src/main/resources/clear.wav").toURI().toString());
                            mediaPlayer = new MediaPlayer(sound);
                            mediaPlayer.setVolume(.2);
                            mediaPlayer.play();
                            stage.close();
                        }
                    });
                    complete.setStyle("-fx-background-image: url('button2.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
                    complete.setTranslateX(0);
                    complete.setTranslateY(110);
                    complete.setMaxHeight(72);
                    complete.setMaxWidth(120);
                    makeTask.getChildren().add(complete);
                    
                    Button goog = new Button();
        
                    goog.setOnAction(new EventHandler<ActionEvent>(){
                            @Override
                            public void handle(ActionEvent event) {
                                Media sound2 = new Media(new File("src/main/resources/click2.wav").toURI().toString());
                                mediaClick = new MediaPlayer(sound2);
                                mediaClick.setVolume(.2);
                                mediaClick.play();
                                StackPane showGoog = new StackPane(); 
                                Scene gscene = new Scene(showGoog, 400, 800); 
                                Stage gstage = new Stage();
                                //Create and set the background image for the main screen.
                                BackgroundImage myBI= new BackgroundImage(new Image("bg4.png",400,800,false,true),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    BackgroundSize.DEFAULT);
                                showGoog.setBackground(new Background(myBI));
                                gstage.setScene(gscene); 
                                gstage.setTitle("Pick a Google Task");
                                try{
                                    // Build a new authorized API client service.
                                    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                                    Tasks service = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                                        .setApplicationName(APPLICATION_NAME)
                                        .build();

                                    List<Task> tasks = service.tasks().list("@default").setMaxResults(10L).execute().getItems();
                                    int h = 0;
                                    for (Task task : tasks){
                                        Label gt = new Label(task.getTitle());
                                        Label gdesc = new Label(task.getNotes());
                                        Date d = new Date(task.getDue().getValue());
                                        LocalDate l = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                        Label gdue = new Label(l + "");
                                        Button imp = new Button("Import");
                                        imp.setOnAction(new EventHandler<ActionEvent>(){
                                            @Override
                                            public void handle(ActionEvent t){
                                                Media sound2 = new Media(new File("src/main/resources/click2.wav").toURI().toString());
                                                mediaClick = new MediaPlayer(sound2);
                                                mediaClick.setVolume(.2);
                                                mediaClick.play();
                                                nameInput.setText(task.getTitle());
                                                descInput.setText(task.getNotes());
                                                datePicker.setValue(l);
                                                gstage.close();
                                            }
                                        });
                                        gt.setTranslateX(-110);
                                        gt.setTranslateY(-320 + 90 * h);
                                        showGoog.getChildren().add(gt);
                                        
                                        gdesc.setTranslateX(-110);
                                        gdesc.setTranslateY(-300 + 90 * h);
                                        showGoog.getChildren().add(gdesc);
                                        
                                        gdue.setTranslateX(-110);
                                        gdue.setTranslateY(-280 + 90 * h);
                                        showGoog.getChildren().add(gdue);
                                        
                                        imp.setTranslateX(100);
                                        imp.setTranslateY(-300 + 85 * h);
                                        imp.setStyle("-fx-background-image: url('button2.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
                                        imp.setMaxHeight(72);
                                        imp.setMaxWidth(120);
                                        showGoog.getChildren().add(imp);
                                        h++;
                                    }
                                }
                                catch(IOException e){
                                    e.printStackTrace();
                                }
                                catch(GeneralSecurityException g){
                                    g.printStackTrace();
                                }
                                gstage.show();
                            }
                    });
                    goog.setText("Google");
                    goog.setStyle("-fx-background-image: url('button2.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
                    goog.setTranslateX(125);
                    goog.setTranslateY(110);
                    goog.setMaxHeight(72);
                    goog.setMaxWidth(120);
                    
                    makeTask.getChildren().add(goog);
                    stage.show();
                }
            });
            
            if(i < 5)
                add.setTranslateX(-390 + id * 195);
            else
                add.setTranslateX(-390 + (id - 5) * 195);
            
            if(i < 5)
                add.setTranslateY(310);
            else
                add.setTranslateY(-320);
            add.setMaxHeight(87);
            add.setMaxWidth(186);
            bList.add(add);
            s.getChildren().add(add);
        }

        Button choose = new Button();
        
        choose.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event){
                    Media sound2 = new Media(new File("src/main/resources/click2.wav").toURI().toString());
                    mediaClick = new MediaPlayer(sound2);
                    mediaClick.setVolume(.2);
                    mediaClick.play();
                    File selectedFile = fileChooser.showOpenDialog(stage);
                    fList.add(selectedFile.toURI().toString());
                }
        });
        
        Button charas = new Button();
        
        charas.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event){
                Media sound2 = new Media(new File("src/main/resources/click2.wav").toURI().toString());
                mediaClick = new MediaPlayer(sound2);
                mediaClick.setVolume(.2);
                mediaClick.play();
                StackPane chaList = new StackPane(); 
                Scene scene = new Scene(chaList, 400, 800); 
                Stage stage = new Stage();
                //Create and set the background image for the main screen.
                BackgroundImage myBI= new BackgroundImage(new Image("bg4.png",400,800,false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
                chaList.setBackground(new Background(myBI));
                
                for(int i = 0; i < fList.size(); i++){
                    String curimg = fList.get(i);
                    Image l = new Image(fList.get(i), 60, 60, false, false);
                    ImageView lv = new ImageView();
                    lv.setImage(l);
                    chaList.getChildren().add(lv);
                    lv.setTranslateY(-300 + i * 85);
                    lv.setTranslateX(-110);
                    
                    Button del = new Button("Delete");
                    
                    del.setOnAction(new EventHandler<ActionEvent>(){
                        @Override
                        public void handle(ActionEvent event){
                            Media sound2 = new Media(new File("src/main/resources/click2.wav").toURI().toString());
                            mediaClick = new MediaPlayer(sound2);
                            mediaClick.setVolume(.2);
                            mediaClick.play();
                            fList.remove(curimg);
                            lv.setVisible(false);
                            del.setVisible(false);
                        }
                    });
                    
                    del.setTranslateX(100);
                    del.setTranslateY(-300 + 85 * i);
                    del.setStyle("-fx-background-image: url('button2.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
                    del.setMaxHeight(72);
                    del.setMaxWidth(120);
                    chaList.getChildren().add(del);
                }
                stage.setScene(scene); 
                stage.setTitle("Custom Faces");
                stage.show();
            }
        });
        
        Button logout = new Button();
        
        logout.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event){
                    Media sound2 = new Media(new File("src/main/resources/click2.wav").toURI().toString());
                    mediaClick = new MediaPlayer(sound2);
                    mediaClick.setVolume(.2);
                    mediaClick.play();
                    
                    File file = new File("tokens/StoredCredential"); 
          
                    if(file.delete()) 
                    { 
                        System.out.println("File deleted successfully"); 
                    } 
                    else
                    { 
                        System.out.println("Failed to delete the file"); 
                    } 
                }
        });
        
        s.getChildren().add(choose);
        choose.setText("Upload");
        choose.setStyle("-fx-background-image: url('choose.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
        choose.setTranslateX(390);
        choose.setTranslateY(372);
        choose.setMaxHeight(29);
        choose.setMinHeight(29);
        choose.setMaxWidth(150);
        
        s.getChildren().add(logout);
        logout.setText("Log Out");
        logout.setStyle("-fx-background-image: url('choose.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
        logout.setTranslateX(60);
        logout.setTranslateY(372);
        logout.setMaxHeight(29);
        logout.setMinHeight(29);
        logout.setMaxWidth(150);
        
        s.getChildren().add(charas);
        charas.setText("Custom");
        charas.setStyle("-fx-background-image: url('choose.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
        charas.setTranslateX(225);
        charas.setTranslateY(372);
        charas.setMaxHeight(29);
        charas.setMinHeight(29);
        charas.setMaxWidth(150);
    }
    
    public void save() throws FileNotFoundException, IOException{
        FileOutputStream fos = new FileOutputStream("t.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(taskList);
        oos.close();
        
        FileOutputStream fos2 = new FileOutputStream("f.tmp");
        ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
        oos2.writeObject(fList);
        oos2.close();
    }
    
    public void select(Chara c){
        if(selected != null)
            selected.setStyle("-fx-background-image: url('button.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
        bList.get(cList.indexOf(c)).setStyle("-fx-background-image: url('select.png');" + "-fx-font-family: 'Comic Sans MS';" + "-fx-font-size: 15;");
        selected = bList.get(cList.indexOf(c));
    }
    
    public ArrayList<MyTask> getTlist(){
        return taskList;
    }
    
    public ArrayList<Chara> getClist(){
        return cList;
    }
    
    public ArrayList<String> getFlist(){
        return fList;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        launch(args);
    }
}
