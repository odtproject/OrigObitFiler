import java.util.Scanner;

class EchoLine {

	public static void main(String[] args) {
		Scanner myScanner = new Scanner(System.in);
		
		System.out.println(myScanner.nextLine());;
		
		System.out.println("That's done.");
		
		System.out.println("Or is it?");

		myScanner.close();
	}

}
