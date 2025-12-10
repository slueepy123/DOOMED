import processing.core.*;
import processing.data.Table;
import processing.data.TableRow;
import static processing.core.PApplet.*;
import processing.sound.*;

public class Map {
  protected Camera parent;
  protected PApplet p;
  protected Table table;
  protected String[][] dataArray;
  protected int rows, columns;
  protected Item[] items;
  protected Item[] inventory;

  // Assets
  PImage keyRed, keyBlue;
  SoundFile pickup, footstep1, footstep2, door_open;
  SoundFile[] footsteps=new SoundFile[2];
  
  Map(Camera parent, Table input) {
    this.parent=parent;
    this.p=parent.parent;
    this.table=input;
    this.rows=table.getRowCount();
    this.columns=table.getColumnCount();
    this.dataArray=new String[rows][columns];
    this.items=new Item[0];
    this.inventory=new Item[0];
    for (int x=0; x<rows; x++) {
      for (int z=0; z<columns; z++) {
        dataArray[x][z]=table.getRow(x).getString(z);
        String[] cell=dataArray[x][z].split("_");
        if (cell[0].equals("item")) {
          String item_name="";
          if (cell.length>1) {
          for (int i=1; i<cell.length; i++) {
              if (i>1) item_name+="_";
              item_name+=cell[i];}
            items=(Item[]) PApplet.append(items, new Item(x,z,item_name));}
        }
      }
    }
    this.loadAssets();
  }
  
  //if you trigger the rest of these functions before setMap(), the game will crash and i will laugh at you
  
  void render() {
    for (int x=0; x<table.getRowCount(); x++) {
      for (int z=0; z<table.getColumnCount(); z++) {
        String[] cell=dataArray[x][z].split("_");
        if (cell[0].equals("0")) {drawFloor(x,z,"black");
          drawCeiling(x,z,"black");}
        else if (cell[0].equals("1")) drawWall(x,z,"darkest_gray");
        else if (cell[0].equals("2")) {
          drawFloor(x,z,"dried_blood");
          drawCeiling(x,z,"black");
        }
        else if (cell[0].equals("3")) {
          drawFloor(x,z,"green");
          drawCeiling(x,z,"white");
        }
        else if (cell[0].equals("item")) {
          drawCeiling(x,z,"black");
          if (cell[1].equals("key")) {
            if (cell[2].equals("red")) {
              drawFloor(x,z,"red");
              drawItem(x,z,keyRed);
            }
            else if (cell[2].equals("blue")) {
              drawFloor(x,z,"blue");
              drawItem(x,z,keyBlue);
            }
          }
          else drawFloor(x,z,"dark_yellow");
        }
        else if (cell[0].equals("door")) {
          if (cell[1].equals("closed")) {
            drawWall(x,z,cell[2]);
          }
          else if (cell[1].equals("open")) {
            drawFloor(x,z,"black");
            drawCeiling(x,z,cell[2]);
          }
        }
        else if (cell[0].equals("enemy")) {
          drawFloor(x,z,"red");
          drawCeiling(x,z,"red");
          
        }
      }
    }
  }
  
  void drawFloor(int x, int z, String clr) {
    p.noStroke();
    setColor(clr);
    p.pushMatrix();
    p.translate(x*200+100,300,z*200+100);
    p.box(200,1,200); //i spent an hour making up my own function for drawing cubes before i learned this exists
    p.popMatrix();
  }
  
  void drawFloor(int x, int z, int t, String clr) {
    p.noStroke();
    setColor(clr);
    p.pushMatrix();
    p.translate(x*200+100,300,z*200+100);
    p.box(t*2,1,t*2);
    p.popMatrix();
  }
  
  void drawCeiling(int x, int z, String clr) {
    p.noStroke();
    setColor(clr);
    p.pushMatrix();
    p.translate(x*200+100,-300,z*200+100);
    p.box(200,1,200);
    p.popMatrix();
  }
  
  void drawCeiling(int x, int z, int t, String clr) {
    p.noStroke();
    setColor(clr);
    p.pushMatrix();
    p.translate(x*200+100,-300,z*200+100);
    p.box(t*2,1,t*2);
    p.popMatrix();
  }
  
  void drawCube(int x, int z, String clr) {
    p.stroke(0);
    setColor(clr);
    p.pushMatrix();
    p.translate(x*200+100,0,z*200+100);
    p.box(200);
    p.popMatrix();
  }
  
  void drawCube(int x, int y, int z, int t, String clr) {
    p.stroke(0);
    setColor(clr);
    p.pushMatrix();
    p.translate(x*100,-(y-1)*100,z*100);
    p.box(t*2);
    p.popMatrix();
  }
  
  void drawWall(int x, int z, String clr) {
    p.stroke(0);
    setColor(clr);
    p.pushMatrix();
    p.translate(x*200+100,0,z*200+100);
    p.box(200,600,200);
    p.popMatrix();
  }
  
  void drawItem(int x, int z, PImage img) {
    p.pushMatrix();
    p.translate(x*200+100,0,z*200+100);
    p.rotateY(-parent.theta);
    p.noStroke();
    p.imageMode(CENTER);
    p.textureMode(IMAGE);
    p.beginShape();
    p.texture(img);
    p.vertex(x-50,x-50, 0, 0,     0);
    p.vertex(x+50,x-50, 0, 100,   0);
    p.vertex(x+50,x+50, 0, 100, 100);
    p.vertex(x-50,x+50, 0, 0,   100);
    p.endShape(CLOSE);
    p.popMatrix();
  }
  
  //continuous collision check
  boolean rayCastHit(float old_x, float old_z, float new_x, float new_z, float radius) {
    //ray casting directional prep
    float dx=new_x-old_x;
    float dz=new_z-old_z;
    float distance=PApplet.sqrt(dx*dx+dz*dz);
    
    //actual ray-casting process (finally, the moment you've all been waiting for)
    int steps=PApplet.max(1,(int)distance); //check every 10 units/pixels
    for (int i=0; i<=steps; i++) {
      float t=i/(float)steps;
      if (checkCollision(old_x+dx*t, old_z+dz*t, radius)) return true;
    } return false;
  }
  
    boolean checkCollision(float world_x, float world_z, float radius) {
    //find which grid cell the ray-casted point is in
    int grid_x=(int)Math.floor(world_x/200); //inspired by the snake game i made for lab 4
    int grid_z=(int)Math.floor(world_z/200);
    
    //check every cell adjacent to the player to see what the player can collide with
    for (int i=-1; i<=1; i++) {
      for (int j=-1; j<=1; j++) {
        int check_x=grid_x+i;
        int check_z=grid_z+j;
        
        //make sure player isn't out of bounds because this only checks within bounds
        if (check_x<0 || check_x>=rows || check_z<0 || check_z>=columns) continue;
        
        //check if the point is in a cell that has an obj w/ collision
        String cell=dataArray[check_x][check_z];
        if (cell.equals("1")
         || cell.equals("door_closed_red")
         || cell.equals("door_closed_blue")) {
          float cellCenter_x=check_x*200+100;
          float cellCenter_z=check_z*200+100;
          // if distance is less than obj t value, that's a collision
          if (PApplet.abs(world_x-cellCenter_x)<100+radius
           && PApplet.abs(world_z-cellCenter_z)<100+radius) {
            return true;
          }
        }
      }
    }
    return false;
  }
    
  //player collision radius
  float getPlayerRadius() {
    return (float)85.0; //player's hitbox radius
  }
  
  void updateParent(Camera update) {
    this.parent=update;
  }
  
  void pickUpItem(int x, int z, Item item) {
    if (dataArray[x][z]!="0") {
      inventory=(Item[]) PApplet.append(inventory, item);
      dataArray[x][z]="0";
      pickup.play();
    }
  }
  
  boolean searchInventory(String item_name) {
    for(int i=0; i<inventory.length; i++) {
      if (inventory[i].name.equals(item_name)) return true;
    } return false;
  }
  
  void loadAssets() {
    keyRed=p.loadImage("assets/textures/keyred.png");
    keyRed.resize(100,100);
    keyBlue=p.loadImage("assets/textures/keyblue.png");
    keyBlue.resize(100,100);
    
    pickup=new SoundFile(p, "assets/sounds/pickup.wav");
    footstep1=new SoundFile(p, "assets/sounds/footstep_1.wav");
    footstep2=new SoundFile(p, "assets/sounds/footstep_2.wav");
    door_open=new SoundFile(p, "assets/sounds/door_open.wav");
    door_open.amp((float)0.5);
  }
  
  void fill(int a) { //i'm too lazy to manually go in and fix all these ignorable errors one-by-one lol
    p.fill(a);
  }
  
  void fill(int a, int b, int c) {
    p.fill(a,b,c);
  }
  
  void setColor(String clr) {
    if (clr.equals("black")) fill(0,0,0);
    else if (clr.equals("white")) fill(255);
    else if (clr.equals("light_gray")) fill(200);
    else if (clr.equals("gray")) fill(150);
    else if (clr.equals("dark_gray")) fill(50);
    else if (clr.equals("darkest_gray")) fill(25);
    else if (clr.equals("red")) fill(255,0,0);
    else if (clr.equals("dark_red")) fill(150,0,0);
    else if (clr.equals("pale_red")) fill(255,153,204);
    else if (clr.equals("dried_blood")) fill(75,0,0);
    else if (clr.equals("green")) fill(0,255,0);
    else if (clr.equals("dark_green")) fill(0,150,0);
    else if (clr.equals("pale_green")) fill(183,225,205);
    else if (clr.equals("blue")) fill(0,0,255);
    else if (clr.equals("dark_blue")) fill(0,0,150);
    else if (clr.equals("pale_blue")) fill(19,104,244);
    else if (clr.equals("yellow")) fill(255,255,0);
    else if (clr.equals("dark_yellow")) fill(150,150,0);
    else if (clr.equals("cyan")) fill(0,255,255);
    else if (clr.equals("dark_cyan")) fill(0,150,150);
    else if (clr.equals("magenta")) fill(255,0,255);
    else if (clr.equals("dark_magenta")) fill(150,0,150);
    else {
      fill(150,150,150);
      PApplet.println("ERROR: Color "+clr+" is not valid.");
    }  
  }
}
