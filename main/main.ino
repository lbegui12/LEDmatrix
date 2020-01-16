// Simple NeoPixel test.  Lights just a few pixels at a time so a
// 1m strip can safely be powered from Arduino 5V pin.  Arduino
// may nonetheless hiccup when LEDs are first connected and not
// accept code.  So upload code first, unplug USB, connect pixels
// to GND FIRST, then +5V and digital pin 6, then re-plug USB.
// A working strip will show a few pixels moving down the line,
// cycling between red, green and blue.  If you get no response,
// might be connected to wrong end of strip (the end wires, if
// any, are no indication -- look instead for the data direction
// arrows printed on the strip).

//////////////////////////////////////
/////////        LIBS        /////////
//////////////////////////////////////
#include <Adafruit_NeoPixel.h>

//////////////////////////////////////
/////////       GLOBAL       /////////
//////////////////////////////////////
#define POT_PIN  2    // PINS
#define PIN      6
#define N_LEDS 90     // MATRIX
#define N_X 10
#define N_Y 9
Adafruit_NeoPixel strip = Adafruit_NeoPixel(N_LEDS, PIN, NEO_GRB + NEO_KHZ800);
uint32_t count = 0;

//////////////////////////////////////
/////////       EXTERN       /////////
//////////////////////////////////////
int valPotent;

//////////////////////////////////////
/////////       EFFECTS      /////////
//////////////////////////////////////
uint32_t rgb_tunnel[5];

//////////////////////////////////////
/////////        SHAPE       /////////
//////////////////////////////////////
#define N_letters 24
int l0[] = {21, 23, 24, 38, 41, 58, 61, 62, 63, 64, 24, 25, 26, 27, 28, 31, 48, 51, 65, 66, 67, 68, 22, -1};  // 24
int l1[] = {-1, 38, 41, 42, 43, 44, 45, 46, 47, 48, -1, 31, 51, -1, -1, -1, -1, -1, -1 ,-1, -1, -1, -1, -1};
int l2[] = {21, 38, 41, 58, 61, 62, 63, 64, 55, 44, 35, 24, 25, 26, 27, 28, 31, 48, 51, 68 ,-1, -1, -1, -1};



//////////////////////////////////////
/////////       STRUCT       /////////
//////////////////////////////////////
struct Coord{
  uint32_t x;
  uint32_t y; 
};

struct L{
 String s;
 int letter[N_letters];  
};

//////////////////////////////////////
/////////       SET UP       /////////
//////////////////////////////////////
void setup() {
  Serial.begin(9600);
  strip.begin();
  randomSeed(analogRead(0));
  initLetters();
}

L zero;
L deux;
L espace = {};
static void initLetters(){
  zero.s ="0";
  for(byte i=0;i<N_letters;i++){
    zero.letter[i]=l0[i];  
  }
  
  deux.s ="2";
  for(byte i=0;i<N_letters;i++){
    deux.letter[i]=l2[i];  
  }

  espace.s = " ";

}

static void tunnelInit()
{
  
   uint32_t r;
   uint32_t g;
   uint32_t b;
   
   for (byte i=0; i<5; i=i+1){
    r = random(0, 255);
    g = random(0, 255);
    b = random(0, 255);
    rgb_tunnel[i] = strip.Color(r, g, b);
   }
   
   
}

//////////////////////////////////////
/////////        LOOP        /////////
//////////////////////////////////////
void loop() {

  if(!count)
  {
    tunnelInit();
  }
  //chase(strip.Color(255, 0, 0)); // Red

  int mode = 0;
  if(count%600<200){
    //barres(count);
    loopPrint(count, "2020 ");
  }
  else if(count%600<400){
    //barres(count);
    tunnel(count);
  }
  else{
    //barres(count);
    randColorIt();
  }

  count++;
}


//////////////////////////////////////
/////////        TOOLS       /////////
//////////////////////////////////////

uint32_t grid2line(uint32_t x, uint32_t y)
{
   
}
Coord line2grid(uint32_t l)
{
   Coord xy;
   xy.y = l/10;   // Y line
   xy.x = l%10;   // X col
   if(xy.y%2!=0)
   {
    xy.x=N_X-xy.x-1; 
   }
   return xy;
}

static void barres(int count)
{
  int b[N_Y];
  for(byte i=0; i<N_Y; i++){
    b[i] = N_X*(sin((count-i)%N_Y)+1)/2;
      
  }
  for(uint16_t k=0;k<N_Y;k++){
    for(uint16_t i=0; i<strip.numPixels(); i++) {
      Coord pos=line2grid(i);
      /*
      Serial.print("k ");
      Serial.println(k);
      
      Serial.print("i ");
      Serial.println(i);

      Serial.print("x ");
      Serial.print(pos.x);
      Serial.print("\t y ");
      Serial.println(pos.y);
      
      Serial.print("b = ");
      Serial.println(b[k]);
      */
      
      if(pos.y==k && (N_X-pos.x) < b[k])
      {
          uint32_t fore = strip.Color((9-pos.x)*2.5, 0, pos.x*2.5);
          strip.setPixelColor(i  , fore);  
      }
      else
      {
          uint32_t fore = strip.Color(0,0,0);
          strip.setPixelColor(i  , fore);  
      }
    }
  }  
  strip.show();
  delay(32);
}

static bool intInArry(int i, int c[])
{
  bool isIN=0;
  int N = (sizeof(c)/sizeof(c[0]));
  int k=0;
  //Serial.println(c);
  while(k<N_letters && !isIN)
  {
    //Serial.println(c[k]);
    isIN = (i==c[k]);
    k++;
  }  
  return isIN;
}

static void loopPrint(int count, String s)
{ 
  int n = s.length();
  int cTemps = count%30<15;
  
  if(count%50<10)               
  {
      Serial.println("Print 2");    // 2
      printm(count,deux);  
  }
  else if(count%50<20)
  {
    Serial.println("Print 0");      // 0
    printm(count,zero);  
  }
  else if(count%50<30)
  {
      Serial.println("Print 2");    // 2
      printm(count,deux);  
  }
  else if(count%50<40)
  {
    Serial.println("Print 0");      // 0
    printm(count,zero);  
  }
  else{
    Serial.println("Print space");  // 
    printm(count,espace);  
  }
}

static void printm(int count, L lettre)
{
  // r g b
  // b 255-g r
  uint32_t valPotent=analogRead(POT_PIN);  
  uint32_t r = 125*(1+sin(count));
  uint32_t g = 125*(1+sin(count/5));
  uint32_t b = 125*(1+sin(count/12));
  
  uint32_t fore = strip.Color(r, g, b);
  uint32_t back = strip.Color(b, 255-g, r);

  // loop through colors
  for(uint16_t i=0; i<strip.numPixels(); i++) {

    if(lettre.s!= " " && intInArry(i,lettre.letter))
    {
      //Serial.println("fore");
      strip.setPixelColor(i  , fore);
    }
    else
    {
      //Serial.println("back");
      strip.setPixelColor(i  , back);  
    }
     
  }
  strip.show();
  if(valPotent<30){
    valPotent=32;
  }
  delay(valPotent);
}

static void tunnel(uint32_t n)
{
  int val = analogRead(POT_PIN);
  float sint = 1+sin(n*val);  
  float f = (512.0-sint*val)/512.0;
  int blue = 25+100*(1+sin(f*3.14*count/6));
  int green = 25+100*(1+sin(f*3.14*count/12));
  int red = 25+100*(1+sin(f*3.14*count/18));
  
  uint32_t newColor = strip.Color(red, green, blue);
  valPotent=analogRead(POT_PIN);
  
  for(byte a=0; a<4; a++){
    rgb_tunnel[a]=rgb_tunnel[a+1];
  }
  rgb_tunnel[4]=newColor;

  
  // loop through colors
  for(byte k=0; k<5; k++) {
    for(uint16_t i=0; i<strip.numPixels(); i++) {
      // Find the condition to color the edge of a rectangle
      Coord pos=line2grid(i);
      bool cond =  pos.y==k  && pos.x>k && pos.x<N_X-k-1 ;            // top side
      bool cond2 = pos.x == k       &&  pos.y>=k && pos.y<=N_Y-k-1 ;  // left side
      bool cond3 = pos.x == N_X-k-1 &&  pos.y>=k && pos.y<=N_Y-k-1 ;  // right side
      bool cond4 = pos.y==N_Y-k-1  && pos.x>k && pos.x<N_X-k-1 ;      // bottom 
      if(cond||cond2||cond3||cond4)   
      {
        strip.setPixelColor(i  , rgb_tunnel[k]); 
      }  
    }
  }
  strip.show();
  if(valPotent<30){
    valPotent=32;
  }
  delay(valPotent);



  /*    TUNNEL EFFECT
 * x  x   x   x   x   x   x   x   x   x 
 * x  o   o   o   o   o   o   o   o   x   
 * x  o   v   v   v   v   v   v   o   x
 * x  o   v   n   n   n   n   v   o   x 
 * X  O   V   N   C   c   n   v   o   x   // rgb_tunnel = "XOVNC"
 * x  o   v   n   n   n   n   v   o   x 
 * x  o   v   v   v   v   v   v   o   x  
 * x  o   o   o   o   o   o   o   o   x 
 * x  x   x   x   x   x   x   x   x   x 
 */
}

// Random color
static void randColorIt()
{
  uint32_t randPos= random(0, 90);
  uint32_t randRed = random(0, 255);
  uint32_t randGreen = random(0, 255);
  uint32_t randBlue = random(0, 255);
  strip.setPixelColor(randPos, strip.Color(randRed, randGreen, randBlue));
  strip.show();
  delay(32);
}
