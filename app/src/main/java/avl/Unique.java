package avl;

/* Nicole Swierstra
 * 7/12
 * 
 * Modifidified:
 * changed return type of avlunique to just return the avl
 * added a both option that would run both tests and time them against each other
 * added getting the highest value from the avl and printing it
 */

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
public class Unique {

    /** Main program: prints the number of unique lines in a given file by one
     * of two methods */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Requires 2 arguments: naive or avl and a filename.");
			return;
		}
		try {
			File f = new File(args[1]);
			Scanner sc = new Scanner(f);
			System.out.println("Finding unique lines in " + args[1]);
			if (args[0].equals("naive")) {
				System.out.println("Naive:");
				System.out.println(naiveUnique(sc));
			} else if(args[0].equals("avl")) {
				System.out.println("AVL:");
				AVL a = avlUnique(sc);
				int mc = a.mostCommon().num;
				String mcstr = a.mostCommon().word;
				System.out.println(a.size());
				System.out.printf("Most common prefix: %s, with %d occurences.", mcstr, mc);
			} else {
				System.out.println("Running both avl and prefixes and timing them");
				
				System.out.println("AVL:");
				
				long ts = System.nanoTime();
				System.out.println(avlUnique(sc).size());
				long tavl = System.nanoTime() - ts;
			
				sc = new Scanner(f);

				System.out.println("Naive:");
				
				ts = System.nanoTime();
				System.out.println(naiveUnique(sc));
				long tnaive = System.nanoTime() - ts;

				System.out.printf("  Results: \n\n    AVL:   %5.2fs \n\n    Naive: %5.2fs\n\n", (double)tavl / 1000000000.0, (double)tnaive / 1000000000.0);

			}
		} catch (FileNotFoundException exc) {
			System.out.println("Could not find file " + args[1]);
		}
	}

	/** Return the number of unique lines availble to be read by sc */ 
	private static int naiveUnique(Scanner sc) {
		// unique lines seen so far
		ArrayList<String> seen = new ArrayList<String>();
		while (sc.hasNextLine()) {
			String line = sc.nextLine();

			// check if we've seen it:
			int i = 0;
			while (i < seen.size() && !line.equals(seen.get(i))) {
				i++;
			}
			if (i == seen.size()) {
				seen.add(line);
			}
		}
		return seen.size();
	}

	/** Returns the avl */ 
	private static AVL avlUnique(Scanner sc) {
		AVL seen = new AVL();
		int count = 0;
		while(sc.hasNextLine()){
			seen.avlInsert(sc.nextLine());
		}
		return seen;	
	}
}
