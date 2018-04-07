package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    //TODO: fancy character removal &asdf;

    /*s
     * scan for the highest number in folder or in messages.htm
     * */
    @FXML
    private TextField tfPath;
    @FXML
    private CheckBox IncludeGroupsChBx;
    @FXML
    private Label lblAccount;

    @FXML
    private Label lblTotalMessages;
    @FXML
    private Label lblCharsWritten;
    @FXML
    private Label lblTotalCalls;
    @FXML
    private Label lblSentMessages;
    @FXML
    private Label lblRecievedMessages;
    @FXML
    private Label lblTotalCallTime;
    @FXML
    private Label lblPagesWritten;

    @FXML
    private TableView<Touple> tableView;
    @FXML
    private TableColumn<Touple, String> tcName;
    @FXML
    private TableColumn<Touple, Integer> tcMessages;
    @FXML
    private TableColumn<Touple, Integer> tcSent;
    @FXML
    private TableColumn<Touple, Integer> tcRecieved;
    @FXML
    private TableColumn<Touple, Integer> tcMedia;
    @FXML
    private TableColumn<Touple, Integer> tcStickers;
    @FXML
    private TableColumn<Touple, Integer> tcCalls;
    @FXML
    private TableColumn<Touple, String> tcCallTime;


    private long totalMessages = 0, totalChars = 0, totalSent = 0, totalCallCount = 0, totalCallSeconds = 0;
    String thisUser;

    int jozgo = 0, andza = 0;

    Alert alert = new Alert(Alert.AlertType.ERROR);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tcName.setCellValueFactory(new PropertyValueFactory<Touple, String>("Name"));
        tcMessages.setCellValueFactory(new PropertyValueFactory<Touple, Integer>("Messages"));
        tcSent.setCellValueFactory(new PropertyValueFactory<Touple, Integer>("Sent"));
        tcRecieved.setCellValueFactory(new PropertyValueFactory<Touple, Integer>("Recieved"));
        tcMedia.setCellValueFactory(new PropertyValueFactory<Touple, Integer>("Media"));
        tcStickers.setCellValueFactory(new PropertyValueFactory<Touple, Integer>("Stickers"));
        tcCalls.setCellValueFactory(new PropertyValueFactory<Touple, Integer>("Calls"));
        tcCallTime.setCellValueFactory(new PropertyValueFactory<Touple, String>("CallTime"));

//        tableView.getItems().setAll(parseUserList());

        alert.setTitle("Error");
        alert.setHeaderText("Facebook data not found!");
        alert.setContentText("Make sure the folder path is correct.");
    }

    @FXML
    private void analyze() {

//   tableView.getItems().add(new Touple(Name, Messages, Sent, Media, Stickers, Calls, CallTime));
        totalMessages = 0;
        totalChars = 0;
        totalSent = 0;
        totalCallCount = 0;
        totalCallSeconds = 0;
        tableView.getItems().clear();
        BufferedReader br = null;
        FileReader fr = null;
        Boolean isAnalyzingGroups = IncludeGroupsChBx.isSelected();

        try {
            String path = tfPath.getText();

//            String path = "/Users/useuse/Downloads/facebook-jozoferko1";

            if(path.length()>0&&path.charAt(path.length() - 1) == '/')
                path= path.substring(0,path.length() - 1);


            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(path + "/html/messages.htm");
            br = new BufferedReader(fr);

            String sCurrentLine;
            StringBuilder text = new StringBuilder();

            while ((sCurrentLine = br.readLine()) != null) {
//                System.out.println(sCurrentLine);
                if (sCurrentLine.contains(".warning")) {
                    br.readLine();
                    br.readLine();
                    while ((sCurrentLine = br.readLine()) != null)
                        text.append(sCurrentLine);
                }
            }

            thisUser = text.substring(text.indexOf("<h1>") + 4, text.lastIndexOf("</h1>"));
            lblAccount.setText(thisUser);
            //System.out.println(thisUser);

            int files = 0;
            while (text.toString().contains("messages/" + (files + 1) +  ".html"))
                files++;

            //System.out.println(files);

            for (int i = 0; i <= files; i++) {
                br.close();
                fr.close();
                fr = new FileReader(path + "/messages/" + i + ".html");
                br = new BufferedReader(fr);
                Conversation conv = new Conversation(br);
                if (!conv.isGroup || isAnalyzingGroups) {
                    Touple tp = analyzeConversation(conv);
                    updateSummary(tp);
                    tableView.getItems().add(tp);
                    System.out.println("aaaaa: " + conv.name + " : " + tp.getCharacters());

                    if (i == 7)
                        System.out.println("testing(jozgo:andza) " + jozgo + " : " + andza);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            alert.showAndWait();
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private Touple analyzeConversation(Conversation conv) {
        jozgo = 0;
        andza = 0;

        Touple tp = new Touple(conv.name);

        for (String message : conv.messages) {
            //is call
            tp.incrementMessages();
            if (message.contains("<span style=\"float:right\">")) {
                if (!message.contains("<span style=\"float:right\"></span></p>")) {
                    tp.incrementCalls();
                    int startIndex = message.indexOf("<span style=\"float:right\">") + 36;
                    if (message.contains("minute"))
                        tp.incrementCallTime(60 * Integer.valueOf(message.substring(startIndex, message.indexOf(' ', startIndex + 1))));
                    else
                        tp.incrementCallTime(Integer.valueOf(message.substring(startIndex, message.indexOf(' ', startIndex + 1))));
                }
            } else {
                boolean isSender = message.substring(0, message.indexOf("</span></div></div>")).contains(thisUser);

                while (message.contains("img src=\"messages/stickers/")) {
                    tp.incrementStickers();
                    int startIndex = message.indexOf("img src=\"messages/stickers/");
                    message = message.replace(message.substring(startIndex, message.indexOf("\" />", startIndex) + 4), "s");
                }

                while (message.contains("<video src=\"")) {
                    tp.incrementMedia();
                    message = message.replace(message.substring(message.indexOf("<video src=\""), message.indexOf("</video>") + 8), "v");
                }

                while (message.contains("<audio src=\"")) {
                    tp.incrementMedia();
                    message = message.replace(message.substring(message.indexOf("<audio src=\""), message.indexOf("</audio>") + 8), "a");
                }
                while (message.contains("<img src=\"")) {
                    tp.incrementMedia();
                    int startIndex = message.indexOf("<img src=\"");
                    message = message.replace(message.substring(startIndex, message.indexOf("\" />", startIndex) + 4), "i");
                }
                if (isSender) {
                    tp.incrementSent();
                    //System.out.println(message);
                    if (message.contains("<p><p>")) {
                        tp.incrementCharacters(message.substring(message.indexOf("<p><p>") + 6, message.indexOf("</p></p>")).length());
                        jozgo += message.substring(message.indexOf("<p><p>") + 6, message.indexOf("</p></p>")).length();
                    } else {
                        tp.incrementCharacters(message.substring(message.indexOf("<p>") + 3, message.indexOf("</p>")).length());
                        jozgo += message.substring(message.indexOf("<p>") + 3, message.indexOf("</p>")).length();
                    }

                } else {
                    if (message.contains("<p><p>")) {
                        tp.incrementCharacters(message.substring(message.indexOf("<p><p>") + 6, message.indexOf("</p></p>")).length());
                        andza += message.substring(message.indexOf("<p><p>") + 6, message.indexOf("</p></p>")).length();
                    } else {
                        tp.incrementCharacters(message.substring(message.indexOf("<p>") + 3, message.indexOf("</p>")).length());
                        andza += message.substring(message.indexOf("<p>") + 3, message.indexOf("</p>")).length();
                    }
                }
            }

        }

        return tp;
    }


    private void updateSummary(Touple touple) {
        totalMessages += touple.getMessages();
        totalChars += touple.getCharacters();
        totalSent += touple.getSent();
        totalCallCount += touple.getCalls();
        totalCallSeconds += touple.getCallTimeSeconds();
        lblTotalMessages.setText(String.valueOf("Total messages: " + totalMessages));
        lblCharsWritten.setText(String.valueOf("Characters written: " + totalChars));
        lblPagesWritten.setText(String.valueOf("Pages written: " + Math.ceil(totalChars / 2500)));
        lblTotalCalls.setText(String.valueOf("Total calls: " + totalCallCount));
        lblSentMessages.setText(String.valueOf("Sent: " + totalSent));
        lblRecievedMessages.setText(String.valueOf("Recieved: " + (totalMessages - totalSent)));
        lblTotalCallTime.setText(String.valueOf("Total call time: " + totalCallSeconds / 86400 + "d " + (totalCallSeconds % 86400) / 3600 + "h " + (totalCallSeconds % 86400 % 3600) / 60 + "m " + (totalCallSeconds % 86400 % 3600 % 60) + "s"));
    }

//    private ConvData ParseConversation(BufferedReader br) throws IOException{
//
//    }
//    private String[] extract

    private class Conversation {
        private String name;
        private String[] messages;
        private Boolean isGroup;

        private Conversation(BufferedReader br) throws IOException {
            String sCurrentLine;
            StringBuilder text = new StringBuilder();

            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.contains(".warning")) {
                    br.readLine();
                    br.readLine();
                    while ((sCurrentLine = br.readLine()) != null)
                        text.append(sCurrentLine);
                }
            }
            if (text.indexOf("<p><span class=\"warning\">") != -1)
                name = text.substring(text.indexOf("</h3>Participants: ") + 19, text.indexOf("<p><span class=\"warning\">"));
            else
                name = text.substring(text.indexOf("</h3>Participants: ") + 19, text.indexOf("<div class=\"message\">"));
            isGroup = name.contains(",");
            text.delete(0, text.indexOf("<div class=\"message\">") + 21);
            text.delete(text.length() - 20, text.length());
            messages = text.toString().split("<div class=\"message\">");
        }
    }
}

