import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
*	@Author Christopher Nixon
*	
*	Improvements/Current limatations: 
*		- Currently no limit is imposed on the input line lnegth,
*		even though Watson imposes a maximum length of a text value at 1024.
*		- Does not handle tabs or new line characters, these need to be escaped.
*		- Does not check that destination file already exists, it should check for this
*			and upon discovery that the file already exists present option of appending the file.
*			This would prevent data destruction.
*/
public class textCleaner {
 	/**
 	*	This receives two arguments; an input filepath, and a destination filename.
 	*	It calls clean() and exits.
 	*/
	public static void main(String[] args) {
		//Checking for correct arguments
		if (args.length != 2) {
			System.out.println(
					"Error: invalid amount of arguments.\nUsage: java textCleaner <inputFileName> <outputFileName>");
			System.exit(0);
		}
		textCleaner tc = new textCleaner();
		File input = null;
		File dest = null;
		try {
			String inputFilename = args[0];
			input = new File(inputFilename);
			String destFilename = args[1];
			dest = new File(destFilename);
			tc.clean(input, dest);
		} catch (Exception e) {
			System.out.println(e + "Usage: java textCleaner <inputFileName> <outputFileName>");
		}
	}
	/** 
	* 	@Param input 	An input file, with each line containing text to be formatted.
	*	@Param destination		 A destination filename, where the formatted data will be written to.
	*	 
	*	Reads the input file line by line, correctly formats the text, and writes 
	*	the formatted text to the destination file.	
	*/
	public void clean(File input, File destination) throws FileNotFoundException, IOException {
		FileWriter fw = new FileWriter(destination);
		BufferedWriter bw = new BufferedWriter(fw);
		Scanner scanner = new Scanner(input);
		StringBuilder sb;

		while (scanner.hasNextLine()) {
			String text = scanner.nextLine();
			sb = new StringBuilder(text);

			//Insert quotations around any quotations already contained in the text.
			String quotation = "\"";
			int index = sb.toString().indexOf(quotation);
			while (index >= 0) {
				// In situation where text begins with quotation mark
				if (index == 0)
					sb.insert(index, '"');
				// Adding quotation marks in front of any quotation marks in the text.
				if (index > 0 && index < sb.length() - 1)
					sb.insert(index, '"');
				index = sb.toString().indexOf(quotation, index + 2);
			}

			//Surround entire text with quotations
			sb.insert(sb.length(), '"');
			sb.insert(0, '"');

			//Write correctly formatted text to destination
			bw.write(sb.toString());
			bw.newLine();
		}

		bw.flush();
		bw.close();
		scanner.close();
	}

}
