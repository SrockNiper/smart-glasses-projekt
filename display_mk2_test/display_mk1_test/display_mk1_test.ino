// nastavení propojovacích pinů Bluetooth a LED diody
#define RX 11
#define TX 10
#define pinLED 13
// připojení knihovny SoftwareSerial
#include <SoftwareSerial.h>
// inicializace Bluetooth modulu z knihovny SoftwareSerial
SoftwareSerial bluetooth(TX, RX);

int i;
// OLED displej přes I2C 128x32 znaků
// řadič SSD1306

// připojení knihovny U8glib
#include "U8glib.h"

// inicializace OLED displeje z knihovny U8glib
U8GLIB_SSD1306_128X32 mujOled(U8G_I2C_OPT_NONE);

// proměnná pro uchování času poslední obnovy displeje
long int prepis = 0;
// proměnná pro uložení aktuální pozice a směru výpisu zprávy
int pozice = 0;
bool smer = 1;
String cas;
byte hodiny;
byte minuty;
byte sekundy;
long mezicas;
String BluetoothData;
void setup(void) {
  // pro otočení displeje o 180 stupňů
  // stačí odkomentovat řádek níže
   mujOled.setRot180();
 bluetooth.begin(9600);
  bluetooth.println("Arduino zapnuto, test Bluetooth..");
 Serial.begin(9600);
  // nastavení pinu s LED diodou jako výstup
  pinMode(pinLED, OUTPUT);

}

void loop(void) {

if (bluetooth.available() > 0) {
 
 BluetoothData=bluetooth.readString();
  // vytvoření proměnné s celou zprávou, která se bude vypisovat

  String zprava = BluetoothData;
  if(zprava.length() == 8 && zprava[2] == ':' && zprava[5] == ':'){
      hodiny = zprava.substring(0, 2).toInt();
      minuty = BluetoothData.substring(3, 5).toInt();
      sekundy = BluetoothData.substring(6, 8).toInt();
      mezicas = millis();
      
      mujOled.firstPage();
    do {
      // vykreslení zadané zprávy od zadané pozice
      vykresliText(pozice/5, (String(hodiny) + ":" + String(minuty) + ":" + String(sekundy)) );
    } while( mujOled.nextPage() );
}else{
  mezicas = millis();
  // porovnání uloženého a aktuálního času
  // při rozdílu větším než 100 ms se provede
  // přepis displeje, zde je to rychlost posunu zprávy
  for (i = 0; i < zprava.length()*5;i++) {
  
  
    // následující skupina příkazů
    // obnoví obsah OLED displeje
    mujOled.firstPage();
    do {
      // vykreslení zadané zprávy od zadané pozice
      vykresliText(pozice/5, zprava);
    } while( mujOled.nextPage() );
    // uložení posledního času obnovení
    prepis = millis();
    while (pozice == 0 && millis()- prepis <4000) {
    
    }
    // řízení směru výpisu - jako první je směr vlevo
    
      // s každou iterací přičteme jedničku2
      pozice += 1;
      // pokud jsme na pozici posledního znaku zprávy
      // mínus 15 znaků (záleží na písmu), tak
      // změníme směr výpisu
      
    // zde je směr vpravo
  }  
  }
  // zde je místo pro další příkazy pro Arduino
  
  // volitelná pauza 10 ms pro demonstraci
  // vykonání dalších příkazů

}else{
pozice =0;
if (mezicas <millis()-1000 ) {
  Serial.println("sekunda");
  sekundy += 1;
  if (sekundy == 60) {
  sekundy = 0;
  minuty += 1;
  }
  if (minuty == 60) {
  minuty = 0;
  hodiny += 1;
  }
  if (hodiny == 24) {
  hodiny = 0;
  }
  mezicas = millis();

}
cas = (String(hodiny) + ":" + String(minuty) + ":" + String(sekundy));
mujOled.firstPage();
    do {
      // vykreslení zadané zprávy od zadané pozice
      vykresliText(pozice/5,cas);
    } while( mujOled.nextPage() );
}
Serial.println("konec cyklu");
}
// funkce vykresliText pro výpis textu na OLED od zadané pozice
void vykresliText(int posun, String text) {
  // nastavení písma, další písma zde:
  // https://github.com/olikraus/u8glib/wiki/fontsize
  mujOled.setFont(u8g_font_fub14);
  // nastavení výpisu od souřadnic x=0, y=25; y záleží na velikosti písma
  mujOled.setPrintPos(10, 20);
  // uložení části zprávy - od znaku posun uložíme 15 znaků
  // např. na začátku uložíme znaky 0 až 15
  String vypis;
  vypis = text.substring(posun, posun+15);
  // výpis uložené části zprávy na OLED displej
  mujOled.print(vypis);
}
