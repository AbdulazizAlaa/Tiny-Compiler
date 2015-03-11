package Package;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class Main extends Application {

    Button compileButton;
    TextArea codeArea;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("editor_layout.fxml"));
        Scene scene = new Scene(root, 700, 500);
        primaryStage.setTitle("Code Bug");

        compileButton = (Button) scene.lookup("#editor_compile_button");
        codeArea = (TextArea) scene.lookup("#editor_code_text_area");

        compileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Controller.resultArea.setText("Compiling....\n");

                /*
                { Sample Program in Tiny language - computes factorial }
                read x; {input an integer }
                if 0 < x then { don't compute if x <= 0 }
                    fact := 1;
                    repeat
                        fact := fact * x;
                        x := x - 1;
                    until x = 0;
                    write fact { output factorial of x }
                end
                */

                ///*
                Scanner scanner = new Scanner(codeArea.getText(), Controller.resultArea);

                TokenType token = null;
                while (token != TokenType.EOF) {
                    token = scanner.getToken();
                    Controller.resultArea.setText(Controller.resultArea.getText()+token+"\n");
                    System.out.println(token);
                }
                //*/

            }
        });

        primaryStage.setScene(scene);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
