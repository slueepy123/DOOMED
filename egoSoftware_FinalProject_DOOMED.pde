import processing.sound.*;

int stage=0;

// Intro variables
float displayTime=60*3;
float introCountdown=displayTime;

// Menu variables
String username="";
boolean usernameInputActive=false;
int page=-1;
int storyPage=0;
PImage title_img;
int num_buttons=3;
Button[] buttons;
String instructionsText;
PImage doors;
PImage keys;
PImage exit;

// Story page
String[] storyLines1;
String[] storyLines2;
int currentLine=0;
int lineDelay=1000;
int lastLineTime=0;
boolean storyStarted1=false;
boolean storyStarted2=false;

// Game variables
Camera cam;
float time=3*60*60;
float countdown=time;

// Map loading variables
Table table;

void setup() {
  size(600,450,P3D);
  pixelDensity(displayDensity());
  windowTitle("DOOMED (Demo)");
  
  table=loadTable("map - output.csv"); //load map
  noSmooth();
  
  title_img=loadImage("assets/textures/title_page.jpg");
  title_img.resize(width,height);
  doors=loadImage("assets/textures/door_img.png");
  doors.resize(width/6,height/6);
  keys=loadImage("assets/textures/key_img.png");
  keys.resize(width/6,height/6);
  exit=loadImage("assets/textures/exit_img.png");
  exit.resize(width/6,height/6);
  
  buttons=new Button[num_buttons];
  buttons[0]=new Button(this, width/2,height-20,150,40,0,"Next Page",18.0);
  buttons[1]=new Button(this, width/2,height-20,150,40,0,"Instructions",18.0);
  buttons[2]=new Button(this, width/2,height-20,150,40,0,"Play again",18.0);
}

void draw() {
  background(255);
  run(stage);
}

void run(int x) {
  if (x==0) intro();
  else if (x==1) menus();
  else if (x==2) game();
}

void buttons() {
  for (int i=0; i<buttons.length; i++) {
    if (buttons[i].active) {
      buttons[i].x=width/2;
      buttons[i].y=height-20;
      buttons[i].display();
      if (buttons[i].hoverCheck()) {
        buttons[i].hovered=true;
      } else buttons[i].hovered=false;
    }
  }
}

void intro() {
  background(0);
  rectMode(CENTER);
  fill(255);
  stroke(255);
  rect(width/2,height/2,400,200);
  textSize(40);
  fill(0);
  textAlign(CENTER);
  text("ego Software", width/2, height/2);
  introCountdown--;
  if (introCountdown==0) stage++;
}

void menus() {
  hint(ENABLE_DEPTH_TEST);
  camera();
  ortho();
  textMode(MODEL);
  if (page==-1) title();
  else if (page==0) usernameInput();
  else if (page==1) story();
  else if (page==2) instructions();
  else if (page==3) video();
  else if (page==4) lose();
  else if (page==5) win();
  buttons();
}

void title() {
  background(0);
  image(title_img, 0, 0, width, height);
}

void usernameInput() {
  background(0);
  
  //text box
  float box_w=width*0.8;
  float box_h=height*0.6;
  float box_x=(width-box_w)/2;
  float box_y=(height-box_h)/2;
  stroke(183,225,205);
  strokeWeight(2);
  fill(0,50,0);
  rectMode(CORNER);
  rect(box_x, box_y, box_w, box_h);
  
  //text
  fill(183,225,205);
  noStroke();
  textSize(14);
  textAlign(CENTER, TOP);
  float text_y=box_y+10;
  float text_x=width/2;
  text("Please input your employee credentials in the space provided:", text_x, text_y);
  text_y+=40;
  
  float input_w=box_w*0.8;
  float input_h=40;
  float input_x=(width-input_w)/2;
  float input_y=text_y;
  stroke(183,225,205);
  strokeWeight(1);
  fill(0);
  rect(input_x, input_y, input_w, input_h);
  
  if (usernameInputActive) {
    stroke(183,225,205);
    strokeWeight(2);
    float cursor_x=input_x+textWidth(username)+5;
    line(cursor_x, input_y+5, cursor_x, input_y+input_h-5);
  }
  
  fill(183,225,205);
  noStroke();
  textAlign(LEFT, CENTER);
  text(username, input_x+10, input_y+input_h/2);
  
  textAlign(CENTER, TOP);
  text_y+=input_h+30;
  text("Click the button to continue", text_x, text_y);
  
  buttons[0].active=true;
}

void story() {
  background(0);
  
  //text box
  float box_w=width*0.8;
  float box_h=height*0.8;
  float box_x=(width-box_w)/2;
  float box_y=(height-box_h)/2;
  
  //border and fill
  stroke(183,225,205);
  strokeWeight(2);
  fill(0,50,0);
  rectMode(CORNER);
  rect(box_x, box_y, box_w, box_h);
  
  //text
  fill(183,225,205);
  noStroke();
  textSize(14);
  textAlign(CENTER, TOP);
  
  //text start pos
  float text_y=box_y+10;
  float text_x=width/2;
  
  if (storyPage==0) {
    if (storyLines1 == null) {
      storyLines1=new String[] {
        "It's 2147.",
        "For humanity, the moon is no longer a",
        "source of mystery, but a home. After moving",
        "there to find work, you were soon approached",
        "with a contract to work at a mysterious facility",
        "40 miles below the lunar surface.",
        "",
        "The pay was good,",
        "so you took the job.",
        "",
        "Now, you realize you shouldn't have.",
        "",
        ""
      };
      storyStarted1=true;
      lastLineTime=millis();
    }
    
    for (int i=0; i<=currentLine && i<storyLines1.length; i++) {
      text(storyLines1[i], text_x, text_y);
      text_y+=25;
    }
    
    if (storyStarted1 && millis()-lastLineTime>lineDelay && currentLine<storyLines1.length-1) {
      currentLine++;
      lastLineTime=millis();
    }
  }
  else if (storyPage==1) {
    if (storyLines2==null) {
      storyLines2=new String[] {
        "Comms have been down for the last 17.5 hours.",
        "Lights have been off for the last 10.23 hours.",
        "Life support goes off in 5.09 hours.",
        "God only knows what they were doing in the lower levels,",
        "but right now you have to try to escape.",
        "",
        "Every few minutes, they purge part of the facility",
        "as per the 'containment' protocol stated in your contract.",
        "You have 3 minutes left in this layer.",
        "Good luck.",
        ""
      };
      storyStarted2=true;
      lastLineTime=millis();
    }
    
    for (int i=0; i<=currentLine && i<storyLines2.length; i++) {
      text(storyLines2[i], text_x, text_y);
      text_y+=35;
    }
    
    if (storyStarted2 && millis()-lastLineTime>lineDelay && currentLine<storyLines2.length-1) {
      currentLine++;
      lastLineTime=millis();
    }
  } if (buttons[0].active==false) buttons[0].active=true;
}

void instructions() {
  background(0);
  
  //text box
  float box_w=width*0.8;
  float box_h=height*0.8;
  float box_x=(width-box_w)/2;
  float box_y=(height-box_h)/2;
  
  //border and fill
  stroke(183,225,205);
  strokeWeight(2);
  fill(0,50,0);
  rectMode(CORNER);
  rect(box_x, box_y, box_w, box_h);
  
  //text
  fill(183,225,205);
  noStroke();
  textSize(14);
  textAlign(CENTER, TOP);
  
  //text start pos
  float text_y=box_y+10;
  float text_x=width/2;
  instructionsText="Instructions:\nW & D to move forward\nA & D to turn\nALT to strafe w/ A & D\n"+
   "SHIFT to sprint/turn faster\nWalk over keys to pick them up\nWalk up to doors to unlock them w/ keys\n"+
   "Find the exit to escape\n"+"Don't run out of time";
  text(instructionsText,text_x,text_y);
}

void video() {
  background(0);
  imageMode(CENTER);
  textMode(CENTER);
  textSize(18);
  image(doors,width/4,height/4);
  text("Door",width/4,height/4+width/12);
  image(keys,2*width/4,2*height/4);
  text("Key",2*width/4,2*height/4+width/12);
  image(exit,3*width/4,3*height/4);
  text("Exit",3*width/4,3*height/4+width/12);
}

void lose() {
  background(0);
  textMode(CENTER);
  textSize(20);
  fill(255,0,0);
  text("You did not escape in time.\nIf you wish to try again, please re-read\nyour employee contract:",width/2,height/2);
}

void win() {
  background(0);
  textMode(CENTER);
  textSize(20);
  fill(0,200,0);
  text("Congratulations on your escape, "+username+"\nYour remaining time was "+(int)countdown/60+
   "\nOur top employee, Zach McCafferty, escaped with 162 seconds left.",width/2,height/2);
}

void game() {
  checkWinOrLose();
  cam.action();
  ui();
}

void ui() {
  hint(DISABLE_DEPTH_TEST);
  camera();
  textSize(20);
  fill(255,0,0);
  textAlign(CENTER);
  text((int)countdown/60,width/2,30);
  countdown--;
  hint(ENABLE_DEPTH_TEST);
  camera(cam.x, cam.y, cam.z, cam.x+sin(cam.theta), cam.y, cam.z-cos(cam.theta), 0, 1, 0);
  perspective(cam.fov, float(width)/float(height), cam.nearClip, cam.farClip);
}

void checkWinOrLose() {
  if (countdown==0) {
    stage=1;
    page=4;
    buttons[1].active=true;
    countdown=time;
    cam=new Camera(this, table, 70);
  }
  if (areYaWinningSon()) {
    stage=1;
    page=5;
    buttons[2].active=true;
    if (time>30) time-=60*30;
  }
}

boolean areYaWinningSon() {
  if (cam.yesDadImWinning()) return true;
  else return false;
}

void keyPressed() {
  if (stage==1) {
    if (page==-1) page++;
    else if (page==0) {
      if (key==BACKSPACE) {
            if (username.length()>0) username=username.substring(0, username.length()-1);
          } else if (key==ENTER||key==RETURN) {
            if (username.length()>0) {
              page=1;
              usernameInputActive=false;
            }
          } else if (key!=CODED && key!=SHIFT && key!=CONTROL && key!=ALT) { //no unprintable keys
            if (username.length()<20) username+=key; //character limit is 20;
          }
    }
  } else if (stage==2) {
    if (key==CODED) {
      if (keyCode==SHIFT) cam.sprint=true;
      if (keyCode==ALT) cam.strafe=true;
      if (keyCode==UP) cam.du=true;
      if (keyCode==DOWN) cam.dd=true;
      if (keyCode==LEFT) cam.dl=true;
      if (keyCode==RIGHT) cam.dr=true;
    }
    if (key=='w'||key=='W') cam.du=true;
    if (key=='a'||key=='A') cam.dl=true;
    if (key=='s'||key=='S') cam.dd=true;
    if (key=='d'||key=='D') cam.dr=true;
    if (key==' ') cam.shoot=true;
  }
}

void keyReleased() {
  if (stage==2) {
    if (key==CODED) {
      if (keyCode==SHIFT) cam.sprint=false;
      if (keyCode==ALT) cam.strafe=false;
      if (keyCode==UP) cam.du=false;
      if (keyCode==DOWN) cam.dd=false;
      if (keyCode==LEFT) cam.dl=false;
      if (keyCode==RIGHT) cam.dr=false;
    }
    if (key=='w'||key=='W') cam.du=false;
    if (key=='a'||key=='A') cam.dl=false;
    if (key=='s'||key=='S') cam.dd=false;
    if (key=='d'||key=='D') cam.dr=false;
    if (key==' ') cam.shoot=false;
  }
}

void mousePressed() {
  if (stage==1) {
    if (buttons[0].active && buttons[0].hoverCheck()) {
      if (page==0) {
        page=1;
      }
      else if (page==1) {
        if (storyPage==0) {
          storyPage=1;
          storyLines1=null;
          storyLines2=null;
          storyStarted1=false;
          storyStarted2=false;
          currentLine=0;
          lastLineTime=millis();
          lineDelay=1000;
        }
        else if (storyPage==1) {
          storyLines1=null;
          storyLines2=null;
          storyStarted1=false;
          storyStarted2=false;
          currentLine=0;
          lastLineTime=millis();
          lineDelay=1000;
          page=2;
          storyPage=1;
        }
      } else if (page==2) page=3;
      else if (page==3) {
        page=4;
        buttons[0].active=false;
        stage=2;
        hint(ENABLE_DEPTH_TEST);
        cam=new Camera(this, table, 70);
        perspective(cam.fov,float(width)/float(height),cam.nearClip,cam.farClip); //sets up 3d environment
      }
    } else if (buttons[1].active && buttons[1].hoverCheck()) {
      if (page==4) {
        page=2;
        buttons[1].active=false;
        buttons[0].active=true;
      }
    } else if (buttons[2].active && buttons[2].hoverCheck()) {
      if (page==5) {
        buttons[2].active=false;
        stage=2;
        hint(ENABLE_DEPTH_TEST);
        cam=new Camera(this, table, 70);
        perspective(cam.fov,float(width)/float(height),cam.nearClip,cam.farClip);
        countdown=time;
      }
    }
  }
  else if (stage==2) {
    cam.shoot=true;
  }
}

void mouseReleased() {
  if (stage==2) {
    cam.shoot=false;
  }
}
