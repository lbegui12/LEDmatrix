int val;


void setup() {
  Serial.begin(9600);
}

void loop() {

  val = analogRead(2);    // read the input pin

  Serial.println(val);             // debug value

  
}
