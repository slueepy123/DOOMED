import processing.core.*;
import processing.data.Table;

class Camera {
  public float x, y, z, theta, fov, nearClip, farClip, sin, cos;
  public int grid_x, grid_z;
  
  // Player variables
  public boolean playerMovementEnabled, du, dd, dl, dr, sprint, strafe, shoot;
  public float walk, turn, walkDefault, turnDefault, walkBoost, turnBoost;
  protected boolean hasSpawned;
  protected Map map;
  protected PApplet parent;
  
  private boolean nextStepLeft=true;
  private int lastStep=0;
  private int stepInterval=600;
  private int sprintInterval=300;
  private int walkInterval=600;
  
  Camera(PApplet parent, Table map, int fov) {
    this.parent=parent;
    this.x=0;
    this.y=-50;
    this.z=0;
    this.theta=0;
    this.fov=(float)fov/180*PApplet.PI;
    this.nearClip=1;
    this.farClip=25000;
    this.playerMovementEnabled=true;
    this.du=false;
    this.dd=false;
    this.dl=false;
    this.dr=false;
    this.sprint=false;
    this.strafe=false;
    this.shoot=false;
    this.walkDefault=(float)10;
    this.turnDefault=(float)0.025;
    this.walk=walkDefault;
    this.turn=turnDefault;
    this.walkBoost=(float)10;
    this.turnBoost=(float)0.030;
    this.hasSpawned=false;
    this.map=new Map(this, map);
  }
  
  void movement() {
    parent.camera(x,y,z,x+PApplet.sin(theta),y,z-PApplet.cos(theta),0,1,0);
    
    if (sprint) {
      walk=walkDefault+walkBoost;
      turn=turnDefault+turnBoost;
      stepInterval=sprintInterval;
    }
    else {
      walk=walkDefault;
      turn=turnDefault;
      stepInterval=walkInterval;
    }
    
    boolean isMoving=(du||dd||(strafe&&(dl||dr)));
    
    if (isMoving&&playerMovementEnabled) {
      updateFootsteps();
    }
    
    //how much x and z should move without collision
    float moveX=0;
    float moveZ=0;
    
    if (du) {
      moveX=+walk*PApplet.sin(theta);
      moveZ=-walk*PApplet.cos(theta);
    }
    if (dd) {
      moveX=-walk*PApplet.sin(theta);
      moveZ=+walk*PApplet.cos(theta);
    }
    if (strafe && dl) {
      moveX=-walk*PApplet.cos(theta);
      moveZ=-walk*PApplet.sin(theta);
    }
    if (strafe && dr) {
      moveX=walk*PApplet.cos(theta);
      moveZ=walk*PApplet.sin(theta);
    }
    
    //but wait! we can't move just yet! we have to make sure we CAN move to where moveX and moveZ take us!
    
    //find where the player is
    float old_x=x;
    float old_z=z;
    float radius=map.getPlayerRadius();
    
    //find where the player will go next frame
    float new_x=x+moveX;
    float new_z=z+moveZ;
    
    //check the ray cast
    if (map.rayCastHit(old_x, old_z, new_x, new_z, radius)) { //if true, then there's a collision
      if (!map.rayCastHit(old_x, old_z, new_x, old_z, radius)) x=new_x; //see if collision is on x-axis
      if (!map.rayCastHit(old_x, old_z, x, new_z, radius)) z=new_z; //see if collision is on z-axis
    }
    else { //no collision, all clear
      x=new_x;
      z=new_z;
    }
    
    //apply rotation
    if (!strafe && dl) theta-=turn;
    if (!strafe && dr) theta+=turn;
    
    grid_x=(int)Math.floor(x/200);
    grid_z=(int)Math.floor(z/200);
    
    //check to see if we just picked up an object or reached a door we can open
    for(int i=0; i<map.items.length; i++) {
      if (map.items[i].x==grid_x && map.items[i].z==grid_z) map.pickUpItem(grid_x,grid_z,map.items[i]);
    }
    for (int i=-1; i<=1; i++) {
      for (int j=-1; j<=1; j++) {
        int check_x=grid_x+i;
        int check_z=grid_z+j;
        if (map.dataArray[check_x][check_z].equals("door_closed_red") && map.searchInventory("key_red")) {
          map.dataArray[check_x][check_z]=("door_open_red");
          map.door_open.play();
        }
        else if (map.dataArray[check_x][check_z].equals("door_closed_blue") && map.searchInventory("key_blue")) {
          map.dataArray[check_x][check_z]=("door_open_blue");
          map.door_open.play();
        }
      }
    }
  }
  
  boolean yesDadImWinning() {
    return (map.dataArray[grid_x][grid_z].equals("3"));
  }
  
  void updateFootsteps() {
    int t=parent.millis();
    
    //wait for interval
    if (t-lastStep>stepInterval) {
      //alternate the footstep sounds
      if (nextStepLeft && map.footstep1!=null) {
        map.footstep1.play();
      } else if (!nextStepLeft && map.footstep2!=null) {
        map.footstep2.play();
      }
      nextStepLeft=!nextStepLeft;
      lastStep=t;
    }
  }
  
  void actions() {
    /*
    if (shoot) {
      PApplet.print("Pew! ");
    }
    */ //i ran out of time for this :(
  }
  
  void action() {
    if (!hasSpawned) {
      for (int i=0; i<map.rows; i++) {
        for (int j=0; j<map.columns; j++) {
          if (map.dataArray[i][j].equals("2")) {
            x=i*200+100;
            z=j*200+100;
            hasSpawned=true;
          }
        }
      }
    }
    movement();
    actions();
    map.render();
  }
  
  void setCameraPos(float x, float z) {
    this.x=x;
    this.z=z;
  }
  
  void setCameraPos(float x, float z, float theta) {
    this.x=x;
    this.z=z;
    this.theta=theta;
  }
  
  void setMap(Map map) {
    this.map=map;
  }
}
