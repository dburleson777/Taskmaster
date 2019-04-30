/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Taskmaster;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.animation.*;
import javafx.util.Duration;
import java.util.Random;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @author David
 */
public class Chara {
    ImageView visimage;
    ImageView custom;
    double cHeight = 260;
    double cWidth = 329;
    Image good = new Image("hero.png", cWidth, cHeight, false, false);
    Image bad = new Image("enemy.png", cWidth, cHeight, false, false);
    TranslateTransition left = new TranslateTransition();
    TranslateTransition right = new TranslateTransition();
    Taskmaster g;
    int id;
    volatile boolean healed = false;
    StackPane root;
    long until;
    
    Image stepa = new Image("enemya.png", 329, 260, false, false);
    Image step1 = new Image("enemy1.png", 329, 260, false, false);
    Image step2 = new Image("enemy2.png", 329, 260, false, false);
    Image step3 = new Image("enemy2a.png", 329, 260, false, false);
    Image step4 = new Image("enemy3.png", 329, 260, false, false);
    Image step5 = new Image("enemy4.png", 329, 260, false, false);
    Image step6 = new Image("enemy3.png", 329, 260, false, false);
    Image step7 = new Image("enemy2a.png", 329, 260, false, false);
    Image step8 = new Image("enemy2.png", 329, 260, false, false);
    Image step9 = new Image("enemy1.png", 329, 260, false, false);
    Image step9a = new Image("enemya.png", 329, 260, false, false);
    Image step10 = new Image("enemy.png", 329, 260, false, false);
    ArrayList<Image> images = new ArrayList<>();
    
    Image hstep1 = new Image("hero.png", cWidth, cHeight, false, false);
    Image hstep2 = new Image("hero2.png", cWidth, cHeight, false, false);
    Image hstep3 = new Image("hero.png", cWidth, cHeight, false, false);
    Image hstep4 = new Image("hero2.png", cWidth, cHeight, false, false);
    Image hstep5 = new Image("hero.png", cWidth, cHeight, false, false);
    Image hstep6 = new Image("hero2.png", cWidth, cHeight, false, false);
    Image hstep7 = new Image("hero.png", cWidth, cHeight, false, false);
    Image hstep8 = new Image("hero2.png", cWidth, cHeight, false, false);
    ArrayList<Image> images2 = new ArrayList<>();
    
    TranslateTransition cleft = new TranslateTransition();
    TranslateTransition cright = new TranslateTransition();

    Transition animation = new Transition() {
        {
            setCycleDuration(Duration.millis(1800)); // total time for animation
        }

        @Override
        protected void interpolate(double fraction) {
            int index = (int) (fraction*(images.size()-1));
            visimage.setImage(images.get(index)); 
        }
    };
    
    Transition animation2 = new Transition() {
        {
            setCycleDuration(Duration.millis(1800)); // total time for animation
        }

        @Override
        protected void interpolate(double fraction) {
            int index = (int) (fraction*(images2.size()-1));
            visimage.setImage(images2.get(index)); 
        }
    };
    
    public Chara(StackPane root, Taskmaster t){
        g = t;
        this.root = root;
        
        images.add(stepa);
        images.add(step1);
        images.add(step2);
        images.add(step3);
        images.add(step4);
        images.add(step5);
        images.add(step6);
        images.add(step7);
        images.add(step8);
        images.add(step9);
        images.add(step9a);
        images.add(step10);
        
        images2.add(hstep1);
        images2.add(hstep2);
        images2.add(hstep3);
        images2.add(hstep4);
        images2.add(hstep5);
        images2.add(hstep6);
        images2.add(hstep7);
        images2.add(hstep8);
        
        visimage = new ImageView();
        visimage.setImage(bad);
        visimage.setTranslateY(200);
        visimage.setTranslateX(340);
        visimage.setVisible(false);
        root.getChildren().addAll(visimage);
        
        left.setDuration(Duration.millis(1800));
        left.setNode(visimage);
        left.setByX(-80);
        left.setCycleCount(1);
        left.setAutoReverse(false);
        
        right.setDuration(Duration.millis(1800));
        right.setNode(visimage);
        right.setByX(80);
        right.setCycleCount(1);
        right.setAutoReverse(false);
        
        custom = new ImageView();
        
        cleft.setDuration(Duration.millis(1800));
        cleft.setNode(custom);
        cleft.setByX(-80);
        cleft.setCycleCount(1);
        cleft.setAutoReverse(false);
        
        cright.setDuration(Duration.millis(1800));
        cright.setNode(custom);
        cright.setByX(80);
        cright.setCycleCount(1);
        cright.setAutoReverse(false);
        
        visimage.setOnMouseClicked(e->{
		 g.select(this);
	});
    }
    
    public void start(){
        id = g.getClist().indexOf(this);
        LocalDate n = LocalDate.now();
        LocalDate dead = g.getTlist().get(id).getDeadline();
        until = ChronoUnit.DAYS.between(n, dead);
        visimage.setFitHeight(cHeight + (80 - (3 * until)));
        visimage.setFitWidth(cWidth + (80 - (3 * until)));
        if(until > 5)
            visimage.setTranslateY(200 + until/3);
        
        visimage.setVisible(true);
        Thread t1 = new Thread(){
            @Override
            public void run(){
                move();
            } 
        };
        t1.setDaemon(true);
        t1.start();
    }
    
    public void heal(){
        visimage.setImage(good);
        visimage.setTranslateY(160);
        if(!g.getFlist().isEmpty()){
            Random r = new Random();
            int cus = r.nextInt(g.getFlist().size());
            Image i = new Image(g.getFlist().get(cus), 80 - until, 80 - until, false, false);
            custom.setImage(i);
            custom.setTranslateY(45 + until);
            custom.setTranslateX(600);
            visimage.setTranslateX(600);
            custom.setVisible(true);
            root.getChildren().addAll(custom);
        }
        healed = true;
    }
    
    public void reset(){
        visimage.setImage(bad);
        healed = false;
        id = g.getClist().indexOf(this);
        LocalDate n = LocalDate.now();
        LocalDate dead = g.getTlist().get(id).getDeadline();
        long until = ChronoUnit.DAYS.between(n, dead);
        visimage.setFitHeight(cHeight + (80 - (3 * until)));
        visimage.setFitWidth(cWidth + (80 - (3 * until)));
        visimage.setTranslateY(200 + (2 * until));
        root.getChildren().remove(custom);
    }
    
    public void move(){
        boolean[] possible = {false,false};
        boolean exit = false;
        int check = 0;
        while(true){
            if(visimage.getBoundsInParent().getMaxX() < 800)
                possible[0] = true;
            if(visimage.getBoundsInParent().getMinX() > 200)
                possible[1] = true;
            
            while(exit != true){
                Random rand = new Random();
                check = rand.nextInt(2);
                if(possible[check])
                    exit = true;
            }
            exit = false;
            for(int i = 0; i < possible.length; i++)
                possible[i] = false;
            
            switch(check){
                case(0):
                    right.play();
                    if(!healed){
                        animation.play();
                    }
                    else{
                        cright.play();
                        animation2.play();
                    }
                    break;
                case(1):
                    left.play();
                    if(!healed){
                        animation.play();
                    }
                    else{
                        cleft.play();
                        animation2.play();
                    }
                    break;
            }
            try{
                Thread.sleep(4000);
            } catch (Exception e){}
        } 
    }
}
