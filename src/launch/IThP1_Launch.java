package launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.util.ArrayList;
import java.util.List;

public class IThP1_Launch {

	//A-Z und 0-9 Array in Morse
	static final String[] morse = {"._","_...","_._.","_..",".",".._.","__.","....","..",".___","_._","._..",
			"__","_.","___",".__.","__._","._.","...","_",".._","..._",".__","_.._","_.__","__..",
			"_____",".____","..___","...__","...._",".....","_....","__...","___..","____."}; 


	/**
	 * Main Methode, startet das Programm
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);

		boolean fallthrough = false; // Wenn der vom Programm erzeugte Binärcode direkt  
		// wieder zurückcodiert wird, "fällt" das Programm von case 1 in case 2. Diese Variable 
		// regelt, ob die Eingabe für den Binärcode angezeigt wird.
		boolean[] binarray = null;   // Hierfür wird auch das Array im Voraus initialisiert


		int e = 0;

		while (!(e == 1 || e == 2)) { // Nur 1 und 2 sind zulässige Eingaben, 
																	// ansonsten wird das Menü einfach nochmal aufgerufen
			System.out.println("====MORSECODE====");
			System.out.println("Bitte auswählen:\n1: Text2Morse2Bin\n2: Bin2Morse2Text");
			System.out.print("Eingabe: ");

			try {
				e = s.nextInt();

			} catch(Exception ee) {

			}
			s.nextLine();
		}





		switch (e)
		{
		case 1:

			int t2m = 0;
			String t2mtext = "";
			while(!(t2m == 1 || t2m == 2)) {
				System.out.println("Bitte auswählen:\n1: Text\n2: Textdatei\nEingabe: ");
				try {
					t2m = s.nextInt();
				} catch (Exception ee) {

				}

				s.nextLine();
			}

			if (t2m == 1) { // Text manuell eingeben
				System.out.println("Bitte Text eingeben: ");
				t2mtext = s.nextLine();
			}
			else if (t2m == 2) { // Datei einlesen
				t2mtext = t2mfilehandler();
			}


			String morsetext = text2morse(t2mtext);

			if(morsetext.contains("x")) { // Zeichen, die nicht in Morse umwandelbar sind, werden als x dargestellt,
																		// somit kann direkt abgebrochen werden
				System.err.println("Ein Zeichen konnte nicht in Morse konvertiert werden!");
				return;
			}

			System.out.println(morsetext);
			System.out.println("Codierung in Binär...");

			binarray = morse2bin(morsetext);
			for(boolean b:binarray) { // einfache Ausgabe des Arrays würde "true" und "false" ausgeben
				if(b) System.out.print(1); else System.out.print(0);
			}
			System.out.println("\nLänge: "+ binarray.length + " Bits");
			System.out.println("Vergleich mit ASCII:");

			boolean[] binarraycompare = text2ascii2bin(t2mtext); //Text wird zum Effizienzvergleich in ASCII codiert
			for(boolean b:binarraycompare) {
				if(b) System.out.print(1); else System.out.print(0);
			}
			System.out.println("\nLänge: "+ binarraycompare.length + " Bits");
			System.out.print("Ergebnis: ASCII Codierung ist ");

			if(binarray.length > binarraycompare.length) System.out.print("kürzer als ");
			else if(binarray.length < binarraycompare.length) System.out.print("länger als ");
			else System.out.print("genauso lang wie ");
			System.out.println("Morsecodierung.");

			String jn = "";
			while(!(jn.equals("j") || jn.equals("n"))) {
				System.out.print("Soll dieses Objekt zurückcodiert werden? (j/n): ");

				jn = s.nextLine();
			}

			if(jn.equals("n")) {
				break;
			}


			e = 2;
			fallthrough = true;



		case 2:

			boolean[] binarrayreverse; // mit diesem Array wird gearbeitet. Es wird entweder mit dem
																 // schon vorhandenen oder dem neu eingelesenen Array intitalisiert.
			if(!fallthrough) {
				boolean bingood = true; // Diese Variable wird genutzt, um zu überprüfen, 
																// ob die Eingabe erneut stattfinden muss
				do {
					bingood = true;
					System.out.println("Bitte Binärfolge als Folge von 0 und 1 eingeben:");
					String binin = s.nextLine();
					boolean[] binarrayin = new boolean[binin.length()];

					for(int i = 0; i < binin.length(); i++) {

						if(binin.charAt(i) == '0') {
							binarrayin[i] = false;
						} else if(binin.charAt(i) == '1') {
							binarrayin[i] = true;
						} else {
							System.err.println("Fehlerhafte Eingabe!"); // Anderer Charakter als 0 oder 1 erkannt
							bingood = false;
							break;
						}
					}
					binarrayreverse = binarrayin; //internes Array wird mit eingelesenem initialisiert
				}	while(!bingood); 

			} else {
				binarrayreverse = binarray; //internes Array wird mit vorher generiertem initialisiert
			}

			System.out.println("Codierung in Morse:");
			String morse = bin2morse(binarrayreverse);

			System.out.println(morse+ "\nCodierung in Text:");
			String text = morse2text(morse);

			if(text.contains("!") || text.equals("")) { // Ein Zeichen, das nicht als Morse erkannt wird, 
																									// wird als ! decoded
				System.err.println("Dies ist kein gültiger Morsetext!");
			} else {
				System.out.println(text);
			}

			break;
		}
	}



	/**
	 * Codiert einen Text in Morsezeichen nach folgendem Schema:
	 * 
	 * Punkt: '.'
	 * Strich: '_' 
	 * Pause zwischen Zeichen: ':' 
	 * Pause zwischen zwei Wörtern: '::' 
	 * Satzende: '*' .
	 * 
	 * Dies weicht leicht von der Aufgabenstellung ab, ist aber der effektivste Weg, 
	 * die einzelnen Morsezeichen auch im Binären eindeutig voneinander abzugrenzen.
	 * 
	 * Zulässige Zeichen sind {A-Za-z0-9}, wobei der Text intern in lowercase umgewandelt wird.
	 * 
	 * @param t2mtext Der Text, der in Morse umgewandelt werden soll
	 * @return Der Text in Morse codiert
	 */
	private static String text2morse(String t2mtext) {

		t2mtext = t2mtext.toLowerCase(); //da Morse keine Groß- und Kleinschreibung kennt, ist es einfacher, einheitlich zu arbeiten.
		String out = "";
		char c = 0;
		for(int i = 0; i < t2mtext.length(); i++) {
			c = t2mtext.charAt(i);
			out += t2mhelper(c);
			if(c != ' ') { // Hinter jedes Morsezeichen (außer dem Leerzeichen) wird ein : angehängt, um das Ende des Zeichens anzuzeigen.
				out += ":";
			}
		}

		return out;

	}

	/**
	 * Helper für Text2Morse. Hier werden einzelne Buchstaben in Morse umgewandelt.
	 * @param c Der Buchstabe, der in Morse umgewandelt wird
	 * @return Das Morsezeichen
	 */
	private static String t2mhelper(char c) {
		try {
			//Punkte und Leerzeichen werden gesondert abgefragt
			if(c == '.') return "*";
			if(c == ' ') return ":";
			if(c - 60 < 0) { // Jeder char ist eine Zahl, die die Position des Zeichens in der ASCII-Tabelle beschreibt. Ist diese Zahl
				// kleiner als 60, ist das Zeichen garantiert eine Zahl von 0-9. Anderernfalls ist es ein Buchstabe.
				return morse[c - 22]; // Im morse[] Array beginnen die Zahlen ab Position 26 mit der 0, die einen ASCII Wert von 48 hat
			} else {
				return morse[c - 97]; // Die Buchstaben im Array beginnen bei 0, mit a, das einen ASCII Wert von 97 hat.
			}
		} catch (Exception e) {
			return "x"; //Wenn Fehler auftreten, wird ein X zurückgegeben, um dem Hauptprogramm zu signalisieren, dass es ein Problem gibt.
		}


	}

	/**
	 * Konvertiert Morsezeichen in eine binäre Folge nach dem folgenden Muster:
	 * '.': 00,
	 * '_': 01,
	 * '*': 10,
	 * ':': 11.
	 * 
	 * @param morsetext Der Text in Morsezeichen, der codiert werden soll
	 * @return boolean-Array der binären Daten
	 */
	private static boolean[] morse2bin(String morsetext) {

		int length = morsetext.length();
		boolean[] out = new boolean[length*2]; // Pro Morseelement 2 Bits

		for(int i = 0; i < length; i++) {
			switch(morsetext.charAt(i)) {
			case '.': //00
				out[2*i] = false;
				out[2*i + 1] = false;
				break;
			case '_': //01
				out[2*i] = false;
				out[2*i + 1] = true;
				break;
			case '*': //10
				out[2*i] = true;
				out[2*i + 1] = false;
				break;
			case ':': //11
				out[2*i] = true;
				out[2*i + 1] = true;
				break;
			}
		}

		return out;
	}


	/**
	 * Wandelt binäre Daten, die nach dem oben genannten Prinzip encodiert werden, in Morsezeichen um.
	 * @param binarray Die binären Daten
	 * @return Die Morsezeichen als String
	 */
	private static String bin2morse(boolean[] binarray) {
		String out = "";
		String temp = "";

		for(int i = 0; i < binarray.length / 2; i++) {
			temp += (binarray[2 * i] ? "1" : "0") + (binarray[2*i + 1] ? "1" : "0"); // Umwandlung in String für einfaches Vergleichen
			switch(temp) {
			case "00":
				out += ".";
				break;
			case "01":
				out += "_";
				break;
			case "10":
				out += "*";
				break;
			case "11":
				out += ":";
				break;
			}
			temp = "";

		}


		return out;


	}

	/**
	 * Wandelt Morsezeichen in Text um.
	 * @param morse Der Text in Morsezeichen
	 * @return Der decodierte Text
	 */
	private static String morse2text(String morse) {

		String rest = morse;
		String zeichen = "";
		String out = "";
		char c = 'x';
		char prev = 'x'; // Damit zwischen :: und : unterschieden werden kann, 
										 // wird jeweils der vorherige Charakter aufgezeichnet.
		for(int i = 0; i < morse.length(); i++) {
			c = rest.charAt(0);
			if(rest.length() > 1) { // diese Abfrage stellt sicher, dass das letzte Zeichen 
															// interpretiert wird, auch ohne : am Ende
				rest = rest.substring(1); // löscht das erste Zeichen, das in c gespeichert ist

				if(c != ':') {

					zeichen += c;
				} else {
					if(prev == ':') {
						//Wort zuende
						out += " ";
					} else {
						//Zeichen zuende, an Interpreter senden

						out+= m2thelper(zeichen);

						zeichen = "";
					}
				}
				prev = c;
			} else { //letztes Zeichen

				if(c == '*') { // Letztes Zeichen interpretieren und Punkt anhängen
					out += m2thelper(zeichen);
					out += m2thelper(Character.toString(c));
				} else if(c == ':'){ // Kann entweder Leerzeichen oder Zeichenende sein
						if(prev == ':') { // Leerzeichen
							out += " ";
						} else { // Zeichenende, vorheriges Zeichen an Interpreter senden
							out += m2thelper(zeichen);
						}
				} else { // Letztes Zeichen ist Teil des Morsezeichen, anhängen und an Interpreter senden
					out += m2thelper(zeichen + c);
				}

			}
		}

		return out;


	}

	
	/**
	 * Helper für Morse2Text. Hier wird ein Morsezeichen in einen Buchstaben umgewandelt.
	 * @param zeichen Das Morsezeichen, das umgewandelt wird
	 * @return Der Buchstabe, für den das Morsezeichen steht
	 */
	private static char m2thelper (String zeichen) {
		if(zeichen.equals("*")) return '.'; // Punkt wird als Sonderfall vorher abgefragt
		if(zeichen.length() == 5) { // Nur Zahlen sind 5stellig
			for(int i = 26; i < 36; i++) { // Zahlen fangen im Morsearray bei 26 an
				if(morse[i].equals(zeichen)) {
					return (char)(22+i); //Die Zahl 0 hat den ASCII Wert 48, um diesen Wert zu erreichen wird 22 addiert
				}
			}
		} else {
			for(int i = 0; i < 26; i++) {
				if(morse[i].equals(zeichen)) {
					return (char)(97+i); // a hat den ASCII Wert 97, also wird dieser auf die Position im Array addiert.
				}
			}
		}


		return '!';

	}

/**
 * Diese Funktion dient dem Vergleich der Codierung in ASCII-Code mit der Morsecodierung
 * @param t2mtext Der Text, der umgewandelt werden soll.
 * @return Das binäre Array
 */
	private static boolean[] text2ascii2bin(String t2mtext) {

		char[] carr = t2mtext.toCharArray(); // Text als Array von Chars
		boolean[] out = new boolean[carr.length * 8]; // Ein char besteht aus 8 Bits
		String s;
		for(int i = 0; i < carr.length; i++) {
			s = Integer.toBinaryString(carr[i]); // Wandelt den Zahlenwert des Chars in binär um.
			s = String.format("%8s", s).replace(' ', '0'); // Auffüllen mit 0 am linken Rand, damit Zeichen immer
																										 // 8 bit lang sind
			for(int j = 0; j < 8; j++) {
				out[i * 8 + j] = s.charAt(j) == '1'; // Konvertieren des Binärstrings in das Array
			}
		}

		return out;
	}


	private static JFrame frame;
	
	/**
	 * Diese Methode erzeugt einen Filepicker und extrahiert den Text aus einem Textdokument
	 * @return Der Text eines Textdokuments
	 */
	private static String t2mfilehandler() {
		String out = "";
		File f = null;
		frame = new JFrame();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.ICONIFIED);
		frame.setExtendedState(JFrame.NORMAL);
		JFileChooser fc = new JFileChooser();
		if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)){
			frame.setVisible(false);
			f = fc.getSelectedFile();
		}else {
			System.out.println("Fehler.");
			System.exit(1);
		}	
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			for ( int c; ( c = fr.read() ) != -1; ) { // byteweises Einlesen des Dokuments
				out += (char) c; // anhängen des Bytes als Buchstaben an Output
			}
		} catch (Exception e) {

			e.printStackTrace();
			System.exit(1);
		} finally {
			try {fr.close();} catch (Exception e) {e.printStackTrace();}
		}
		return out;
	}

}
