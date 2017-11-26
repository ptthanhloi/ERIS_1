//Hardware pin definitions
int measurePin = A6;
int ledPower = 3;
 
int samplingTime = 280;
int deltaTime = 40;
int sleepTime = 9680;
 
float voMeasured = 0;
float calcVoltage = 0;
float dustDensity = 0;
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
  delayMicroseconds(samplingTime); 
  voMeasured = analogRead(measurePin);// read the dust value
 
  delayMicroseconds(deltaTime);
  digitalWrite(ledPower,HIGH); // turn the LED off
  delayMicroseconds(sleepTime); 
  // 0 - 3.3V mapped to 0 - 1023 integer values
  // recover voltage
  calcVoltage = voMeasured * (3.3 / 1024);
 
  // linear eqaution taken from http://www.howmuchsnow.com/arduino/airquality/
  // Chris Nafis (c) 2012
  dustDensity = 0.17 * calcVoltage - 0.1;
 
  //Serial.print("Raw Signal Value (0-1023): ");
 // Serial.print(voMeasured);
 
 // Serial.print(" - Voltage: ");
 // Serial.print(calcVoltage);
 
 // Serial.print(" - Dust Density: ");
 // Serial.println(dustDensity);
 
  //Use the 3.3V power pin as a reference to get a very accurate output value from sensor
  float outputVoltage = 3.3 / refLevel * uvLevel;
  
  float uvIntensity = mapfloat(outputVoltage, 0.99, 2.8, 0.0, 15.0); //Convert the voltage to a UV intensity level

   if (isnan(outputVoltage) || isnan( uvIntensity)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
   }
   
   //puts # before the values so our app knows what to do with the data
  Serial.print('#');
  Serial.print(uvIntensity); 

  if(dustDensity<0)
  {    
    Serial.print("0.00");
  
  }else
  {
    Serial.print(dustDensity);
 
    }
  if ( uvIntensity >=-0.5 &&  uvIntensity <=2.9)
  {
    Serial.print("that is safe! If you burn easily, cover up and use broad spectrum SPF 30+ sunscreen,Wear sunglasses on bright days.");
  
     if ( uvIntensity >2.9 &&  uvIntensity <=5.9)
     {
      Serial.print("moderate risk! You should wear coat, a wide-brimmed hat and sunglasses.Generously apply broad spectrum SPF 30+ sunscreen every 2 hours, even on cloudy days.");
   
      if ( uvIntensity >5.9 &&  uvIntensity <=7.9)
      {
         Serial.print("High risk!  Protection against skin and eye damage is needed, you should wear coat, a wide-brimmed hat and sunglasses.Generously apply broad spectrum SPF 30+ sunscreen every 2 hours, even on cloudy days.");
          Serial.print('+');
          if ( uvIntensity >7.9 &&  uvIntensity <=10.9)
      {
         Serial.print("Very high risk! Take extra precautions because unprotected skin and eyes will be damaged and can burn quickly, you should wear coat, a wide-brimmed hat and sunglasses.Generously apply broad spectrum SPF 30+ sunscreen every 2 hours, even on cloudy days.");
       
        }
        }
      }
  } else 
  {
     Serial.print("Extreme risk! Take all precautions because unprotected skin and eyes can burn in minutes,you should wear coat, a wide-brimmed hat and sunglasses.Generously apply broad spectrum SPF 30+ sunscreen every 2 hours, even on cloudy days.");
     
    }
  
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
