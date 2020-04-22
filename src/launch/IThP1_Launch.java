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
	
	static final String[] morse = {"._","_...","_._.","_..",".",".._.","__.","....","..",".___","_._","._..",
      "__","_.","___",".__.","__._","._.","...","_",".._","..._",".__","_.._","_.__","__..",
      "_____",".____","..___","...__","...._",".....","_....","__...","___..","____."}; 

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		boolean[] binarray = null;
		System.out.println("====MORSECODE====");
		System.out.println("Bitte auswählen:\n1: Text2Morse2Bin\n2: Bin2Morse2Text");
		System.out.print("Eingabe: ");
		int e = 0;
		
		
		e = s.nextInt();
		s.nextLine();
		
		if (e < 1 || e > 5) {
			System.out.println("Fehler");
			return;
		}
		
		
		boolean fallthrough = false;
		
		switch (e)
		{
		case 1:

			System.out.println("Bitte auswählen:\n1: Text\n2: Textdatei\nEingabe: ");
			int t2m = 0;
			String t2mtext = "";
			t2m = s.nextInt();
			s.nextLine();
			if (t2m == 1) {
				System.out.println("Bitte Text eingeben: ");
				t2mtext = s.nextLine();
			}
			else if (t2m == 2) {
				t2mtext = t2mfilehandler();
			}
			String morsetext = text2morse(t2mtext);
			
			if(morsetext.contains("x")) {
				System.err.println("Ein Zeichen konnte nicht in Morse konvertiert werden!");
				return;
			}
			System.out.println(morsetext);
			System.out.println("Codierung in Binär...");
			binarray = morse2bin(morsetext);
			for(boolean b:binarray) {
				if(b) System.out.print(1); else System.out.print(0);
			}
			System.out.println("\nLänge: "+ binarray.length);
			System.out.println("Vergleich mit ASCII:");
			
			boolean[] binarraycompare = text2ascii2bin(t2mtext);
			for(boolean b:binarraycompare) {
				if(b) System.out.print(1); else System.out.print(0);
			}
			System.out.println("\nLänge: "+ binarraycompare.length);
			System.out.print("Ergebnis: ASCII Codierung ist ");
			
			if(binarray.length > binarraycompare.length) System.out.print("kürzer als ");
			else if(binarray.length < binarraycompare.length) System.out.print("länger als ");
			else System.out.print("genauso lang wie ");
			System.out.println("Morsecodierung.");
			System.out.print("Soll dieses Objekt zurückcodiert werden? (j/n): ");
			String backcode = "";
			if(!s.nextLine().equals("j")) {
				break; //backcode = morse2text(bin2morse(binarray));
			}
			e = 2;
			fallthrough = true;
			
			

		
		
		
		case 2:
			
			boolean[] binarrayreverse;
			if(!fallthrough) {
				System.out.println("Bitte Binärfolge als Folge von 0 und 1 eingeben:");
				String binin = s.nextLine();
				boolean[] binarrayin = new boolean[binin.length()];

				for(int i = 0; i < binin.length(); i++) {
					
					if(binin.charAt(i) == '0') {
						binarrayin[i] = false;
					} else if(binin.charAt(i) == '1') {
						binarrayin[i] = true;
					} else {
						System.err.println("Fehlerhafte Eingabe: " + binin.charAt(i));
						return;
					}
				}
				binarrayreverse = binarrayin;
			} else {
				binarrayreverse = binarray;
			}
			
			System.out.println("Codierung in Morse:");
			String morse = bin2morse(binarrayreverse);
			System.out.println(morse+ "\nCodierung in Text:");
			String text = morse2text(morse);
			if(text.contains("!") || text.equals("")) {
				System.err.println("Dies ist kein gültiger Morsetext!");
				System.out.println(text);
			} else {
				System.out.println(text);
			}
			
			
			
			break;
		default:
			break;
		}
	}

	private static String morse2text(String morse) {

		String rest = morse;
		String zeichen = "";
		String out = "";
		char c = 'x';
		char prev = 'x';
		for(int i = 0; i < morse.length(); i++) {
			c = rest.charAt(0);
			if(rest.length() > 1) {
				rest = rest.substring(1);

				if(c != ':') {

					zeichen += c;
				} else {
					if(prev == ':') {
						//Wort zuende
						out += " ";
					} else {
						//Zeichen zuende

						out+= m2thelper(zeichen);

						zeichen = "";
					}
				}
				prev = c;
			} else {
				if(c == '*') {
					out += m2thelper(zeichen);
					out += m2thelper(Character.toString(c));
				} else if(c == ':'){
					out += m2thelper(zeichen);
				} else {
					out += m2thelper(zeichen + c);
				}
				
			}
		}
		
		return out;

		
	}
	
	private static char m2thelper (String zeichen) {
		if(zeichen.equals("*")) return '.';
		if(zeichen.length() == 5) {
			for(int i = 26; i < 36; i++) {
				if(morse[i].equals(zeichen)) {
					return (char)(22+i);
				}
			}
		} else {
			for(int i = 0; i < 25; i++) {
				if(morse[i].equals(zeichen)) {
					return (char)(97+i);
				}
			}
		}
		
		
		return '!';
		
	}

	private static String bin2morse(boolean[] binarray) {
		String out = "";
		String temp = "";

		for(int i = 0; i < binarray.length / 2; i++) {
			temp += (binarray[2 * i] ? "1" : "0") + (binarray[2*i + 1] ? "1" : "0");
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

	private static boolean[] text2ascii2bin(String t2mtext) {
		
		char[] carr = t2mtext.toCharArray();
		boolean[] out = new boolean[carr.length * 8];
		String s;
		for(int i = 0; i < carr.length; i++) {
			s = Integer.toBinaryString(carr[i]);
			s = String.format("%8s", s).replace(' ', '0'); //Auffüllen mit 0 am linken Rand
			for(int j = 0; j < 8; j++) {
				out[i * 8 + j] = s.charAt(j) == '1';
			}
		}
		
		return out;
	}

	private static boolean[] morse2bin(String morsetext) {
		
		int length = morsetext.length();
		boolean[] out = new boolean[length*2];
		
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

	/* Encoding: 
	 * Punkt: .
	 * Strich: _
	 * Pause zwischen Zeichen: :
	 * Pause zwischen zwei Wörtern: ::
	 * Satzende: *
	 * 
	 * Dies weicht leicht von der Aufgabenstellung ab, ist aber der effektivste Weg, die einzelnen Morsezeichen auch im Binären
	 * eindeutig voneinander abzugrenzen.
	 */
	
	private static String text2morse(String t2mtext) {
		
		t2mtext = t2mtext.toLowerCase();
		String out = "";
		char c = 0;
		for(int i = 0; i < t2mtext.length(); i++) {
			c = t2mtext.charAt(i);
			out += t2mhelper(c);
			if(c != ' ') {
				out += ":";
			}
		}
		
		return out;
		
	}
	
	private static String t2mhelper(char c) {
			try {
				if(c == '.') return "*";
				if(c == ' ') return ":";
				if(c - 60 < 0) { //Zahl
				return morse[c - 22];
			} else {
				return morse[c - 97];
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return "x";
		}
		
		
	}

	private static JFrame frame;
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
			for ( int c; ( c = fr.read() ) != -1; ) {
				out += (char) c;
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
