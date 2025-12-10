import processing.core.*;
import processing.core.PApplet.*;

public class Item {
  protected float x, z;
  protected String name;
  protected String type, clr;
  
  public Item(int x, int z, String name) {
    this.x=x;
    this.z=z;
    this.name=name;
    
  }
}
