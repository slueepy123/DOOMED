import processing.core.*;

public class Button {
  protected PApplet parent;
  protected float x, y, w, h, text_size;
  protected int function, cr, cg, cb, stroke, textStroke, textFill;
  protected String text;
  protected boolean active=false, hovered=false;
  
  Button(PApplet parent, float x, float y, float w, float h, int function, String text, float text_size) {
    this.parent=parent;
    this.x=x;
    this.y=y;
    this.w=w;
    this.h=h;
    this.function=function;
    this.text=text;
    this.text_size=text_size;
    this.cr=0;
    this.cg=0;
    this.cb=0;
    stroke=255;
    textStroke=0;
    textFill=255;
  }

  void display() {
    parent.fill(cr, cg, cb);
    parent.stroke(stroke);
    parent.strokeWeight(2);
    parent.rectMode(PApplet.CENTER);
    parent.rect(x, y, w, h);
    parent.fill(textFill);
    parent.stroke(textStroke);
    parent.strokeWeight(2);
    parent.textAlign(PApplet.CENTER,PApplet.CENTER);
    parent.textSize(text_size);
    parent.text(text, x,y);
  }
  
  boolean hoverCheck() {
    if (parent.mouseX>=this.x-w/2 && parent.mouseX<=this.x+w/2 && parent.mouseY>=this.y-h/2 && parent.mouseY<=this.y+h/2) return true;
    else return false;
  }
  
  void action() {
    display();
  }
}
