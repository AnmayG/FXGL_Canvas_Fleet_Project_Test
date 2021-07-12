import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.animationBuilder;

/**
 it's honestly way more convenient to use this and automatically create hover commands
 This is also useful in case I want to create an animated pane.
 All you need to do is name a button animateButton1, animateButton2, animateButton3, etc.
 and then name the pane you want to animate the same number (animate1, animate2, animate3), and it'll
 automatically animate that pane whenever you want to do anything.
 The goal is to have the SceneBuilder integrate as smoothly as possible with FXGL.
 That means that I should be able to create FXGL features through SceneBuilder.
 That's going to be the "special part" about this project.
 Using an fxml file to edit these things is not only easy for me but also way better for this project because I can just
 add these things in without having to deal with all of the annoying stuff.
 Plus sometimes it's genuinely easier to just change specific commands in here rather than in the controller file
 especially because I can establish a theme (like how it changes to orange for everything)
 */



public class MainMenu extends FXGLMenu {
    private final ArrayList<Boolean> paneVisibility = new ArrayList<>();
    private final ArrayList<Point2D[]> animationRanges = new ArrayList<>();

    /** Creates a new main menu based on the mainMenu.fxml file.
     *  Automatically creates overlaying buttons that do things.
     */
    public MainMenu() {
        super(MenuType.MAIN_MENU);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("assets/mainMenu.fxml"));
            getContentRoot().getChildren().addAll(root.getChildrenUnmodifiable());
            //For all of the menu buttons, you have to add them manually using this
            //This command creates a start button at specific coordinates with certain dimensions, but transparent
            /*FIXME: Almas B created a great library and a great program here, but he put all the work into Geometry Wars.
            *   The ListView is (no offense but) stupidly re-skinned to have a cool techno-like black and blue background
            *   And that's perfect for Geometry Wars, where it has a cool techno-like black and blue background
            *   But it's not very perfect for FXFleet or literally anything else because my program has a clean white and grey background!
            *   That means that something as simple as a ListView now looks completely out of place in my program.
            *   Seriously, why are you re-skinning FXML elements just for the sake of one program?
            *   You don't even use a listview in Geometry Wars!
            *   And on top of that, you can't reset it back.
            *   Because it sets the skin after all the initialization functions run, the listview just has to stay that way.
            *   You are unable to create a listview that actually looks like a listview.
            *   I spent an entire day on this problem, troubleshooting it, until I finally just tried to create a listview in another project
            *   It was a day spent in frustration and me cursing at the computer.
            *   In conclusion, fix your thing Almas B, and please make it more generic for anyone to use.
            *   This is a rant just because curses aren't a good outlet for rage after a while.
            *   maybe I should get more sleep.
            * */
            ArrayList<Node> nodes = new ArrayList<>();
            addAllDescendents(getContentRoot().getParent(), nodes);
            for (Node n:nodes) {
                if (n.getId() == null) continue;
                if (n.getId().contains("animate") && !n.getId().contains("animateButton")) {
                    n.setTranslateX(n.getTranslateX() - n.prefWidth(1.0));
                    n.setVisible(false);
                    for (int i = paneVisibility.size(); i < Integer.parseInt(n.getId().substring(n.getId().length() - 1)); i++) {
                        paneVisibility.add(false);
                        animationRanges.add(new Point2D[]{new Point2D(-1, -1), new Point2D(-1, -1)});
                    }
                }
                if (n.getId().equals("startButton")) {
                    getContentRoot().getChildren().add(
                            makeButton(event -> {
                                        if (isVis(n)) {
                                            ((Button) n).fire();
                                            fireNewGame();
                                        }
                                    },
                                    (Button) n, new Color(1.0, 0.5, 0.0, 1.0), new Color(0, 0, 0.0, 1.0)));
                } else if (n.getId().equals("exitButton")) {
                    getContentRoot().getChildren().add(
                            makeButton(event -> {
                                        if (isVis(n)) {
                                            ((Button) n).fire();
                                            fireExit();
                                        }
                                    },
                                    (Button) n, new Color(1.0, 0.5, 0.0, 1.0), new Color(0, 0, 0.0, 1.0)));
                } else if (n.getId().contains("animateButton")) {
                    getContentRoot().getChildren().add(
                            makeButton(event -> {
                                        if (isVis(n)) {
                                            for (Node n2 : nodes) {
                                                if (n2.getId() == null) continue;
                                                if (n2.getId().equals("animate" + n.getId().substring(n.getId().length() - 1))) {
                                                    animatePane(n2, Integer.parseInt(n2.getId().substring(n2.getId().length() - 1)) - 1);
                                                    break;
                                                }
                                            }
                                        }
                                    },
                                    (Button) n, new Color(1.0, 0.5, 0.0, 1.0), new Color(0, 0, 0.0, 1.0)));
                }else{
                    /*
                    This is where you should keep any special things in.
                    For example, if you want to add a color changer just add it in here and it'll be done.
                    if(n.getId().equals("test")){
                        System.out.println("here");
                    }
                    This way you can create your own special features without issues.
                    Just download the MainMenu file & the MainMenuController file and you can do everything.
                    I trust that everyone will know how to create an FXML file as well, so this works.
                    */
                    if(n.getClass().equals(Button.class)){
                        getContentRoot().getChildren().add(
                                makeButton(event -> {
                                            if(isVis(n)){
                                                ((Button) n).fire();
                                            }
                                        },
                                        (Button)n, new Color(1.0, 0.5, 0.0, 1.0), new Color(0, 0, 0.0, 1.0)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //given a node and an index, it'll translate the
    public void animatePane(Node n, int num){
        if(animationRanges.get(num)[0].equals(new Point2D(-1, -1))){
            animationRanges.set(num, new Point2D[]{new Point2D(n.getTranslateX(), n.getTranslateY()),
                    new Point2D(n.getTranslateX()+n.prefWidth(1.0),n.getTranslateY())});
        }else{
            Point2D temp = animationRanges.get(num)[0];
            animationRanges.get(num)[0] = animationRanges.get(num)[1];
            animationRanges.get(num)[1] = temp;
        }
        noCoordsAnimatePane(n, num, true);
    }

    //this is going to be the full version of the code, and it includes things like from where it wants to transport to, etc.
    //this is going to be much more versatile than the other versions just because.
    //also method overloading for the win, this is way more useful than I thought it would be.
    public void animatePane(Node n, int num, Point2D from, Point2D to, boolean invis){
        if(animationRanges.get(num)[0].equals(new Point2D(-1, -1))){
            animationRanges.set(num, new Point2D[]{from, to});
        }else{
            Point2D temp = animationRanges.get(num)[0];
            animationRanges.get(num)[0] = animationRanges.get(num)[1];
            animationRanges.get(num)[1] = temp;
        }
        noCoordsAnimatePane(n, num, invis);
    }

    public void noCoordsAnimatePane(Node n, int num, boolean invis) {
        n.setVisible(true);
        animationBuilder(this)
                .duration(Duration.seconds(0.5))
                .onFinished(()-> {
                    paneVisibility.set(num, !paneVisibility.get(num));
                    if(invis) n.setVisible(paneVisibility.get(num));
                })
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(n)
                .from(animationRanges.get(num)[0])
                .to(animationRanges.get(num)[1])
                .buildAndPlay();
    }

    public static boolean isVis(Node n){
        if(n==null)return true;
        if(!n.isVisible()) return false;
        return isVis(n.getParent());
    }

    //https://stackoverflow.com/questions/24986776/how-do-i-get-all-nodes-in-a-parent-in-javafx
    public static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

    public static Button makeButton(EventHandler<ActionEvent> onAction, Button n, Color onHover, Color noHover){
        Button output = new Button();
        output.setOnAction(onAction);
        output.setOnMouseEntered(event -> n.setTextFill(onHover));
        output.setOnMouseExited(event -> n.setTextFill(noHover));
        output.setLayoutY(n.getLayoutY());
        output.setLayoutX(n.getLayoutX());
        output.setPrefWidth(n.prefWidth(1.0));
        output.setPrefHeight(n.prefHeight(1.0));
        output.setStyle("-fx-background-color: #ffffff00");
        return output;
    }
}
