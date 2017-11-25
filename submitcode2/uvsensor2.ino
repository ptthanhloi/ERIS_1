//Hardware pin definitions


int ledPower = 3; 
int UVOUT = A0; //Output from the sensor
int REF_3V3 = A1; //3.3V power on the Arduino board
void setup()
{   
  Serial.begin(9600);
  pinMode(ledPower,OUTPUT);
  pinMode(UVOUT, INPUT);
  pinMode(REF_3V3, INPUT);
}
void loop()
{
  int t=0;
  char c;
  if(Serial.available())
  {
    c = Serial.read();
    if (c=='1')
    {
     t= 3;
    }
  while(t>2)  {
    readSensor();  
  if(Serial.available())
  {
    c = Serial.read();
    if (c=='0')
    {
     t = 1;
    }
    }
  }
  
}
}
void readSensor()
{
  int uvLevel = averageAnalogRead(UVOUT);
  int refLevel = averageAnalogRead(REF_3V3);  
  digitalWrite(ledPower,LOW); // power on the LED
  
  float outputVoltage = 3.3 / refLevel * uvLevel;
  
  float uvIntensity = mapfloat(outputVoltage, 0.99, 2.8, 0.0, 15.0); //Convert the voltage to a UV intensity level

   if (isnan(outputVoltage) || isnan( uvIntensity)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
   }
   
   //puts # before the values so our app knows what to do with the data
  Serial.print('#');
  Serial.print(uvIntensity); 
   Serial.print('~'); //used as an end of transmission character - used in app for string length
  Serial.println();
  
  delay(1000);
}

//Takes an average of readings on a given pin
//Returns the average
int averageAnalogRead(int pinToRead)
{
  byte numberOfReadings = 8;
  unsigned int runningValue = 0;

  for(int x = 0 ; x < numberOfReadings ; x++)
    runningValue += analogRead(pinToRead);
  runningValue /= numberOfReadings;

  return(runningValue);  
}

//The Arduino Map function but for floats
float mapfloat(float x, float in_min, float in_max, float out_min, float out_max)
{
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}
