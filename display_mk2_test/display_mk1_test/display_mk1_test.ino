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

void setup(void) {
  // pro otočení displeje o 180 stupňů
  // stačí odkomentovat řádek níže
   mujOled.setRot180();
 bluetooth.begin(9600);
  bluetooth.println("Arduino zapnuto, test Bluetooth..");
  // nastavení pinu s LED diodou jako výstup
  pinMode(pinLED, OUTPUT);
}

void loop(void) {
String BluetoothData;
if (bluetooth.available() > 0) {
 
 BluetoothData=bluetooth.readString();
  // vytvoření proměnné s celou zprávou, která se bude vypisovat
  String zprava = BluetoothData;
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
    // řízení směru výpisu - jako první je směr vlevo
    
      // s každou iterací přičteme jedničku2
      pozice += 1;
      // pokud jsme na pozici posledního znaku zprávy
      // mínus 15 znaků (záleží na písmu), tak
      // změníme směr výpisu
      
    // zde je směr vpravo
  }  
  
  // zde je místo pro další příkazy pro Arduino
  
  // volitelná pauza 10 ms pro demonstraci
  // vykonání dalších příkazů

}
pozice =0;
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
