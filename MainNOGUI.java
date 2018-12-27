import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainNOGUI {
	// Entry point for class
	public static void main(String args[]) {
		if(args.length != 1) {
			System.err.println("Must specify file (Usage: 6502Disassembler.jar file.nes)");
			System.exit(-1);
		}
		// Instructions from 0x8000 to 0xFFFF are saved as a string
		String instructions = "";

		// Dictionary for opcodes -> instruction name
		Map<String, String[]> dict = new HashMap<String, String[]>();

		// Load file
		File f = new File(args[0]);
		
		// Loading bytes of the nes file
		byte[] bytes = null;
		if (f.length() < Integer.MAX_VALUE) {
			bytes = new byte[(int) f.length()];
			try {
				InputStream in = new FileInputStream(f);
				in.read(bytes);
			} catch (Exception e) {
			}
		} else {
			System.err.println("File too big");
		}

		// Taking opcode to instruction from file.ssv
		try {
			BufferedReader br = new BufferedReader(new FileReader("file.ssv"));
			String a = null;
			do {
				a = br.readLine();
				dict.put(a.split(" ")[0], new String[] { a.split(" ")[1], a.split(" ")[2] });
			} while (a != null);
		} catch (Exception e) {}
		
		//read bytes from 0x8000 to 0xFFFF in nes file and map them + save to instruction string
		for (int i = 0x8010; i < 0x10010;) {
			String byt = (Integer.toHexString(Byte.toUnsignedInt(bytes[i]))).toUpperCase();
			String command = "0x" + Integer.toHexString(i - 0x10) + ":";
			if (byt.length() < 2) {
				byt = "0" + byt;
			}
			int inc = 1;
			try {
				inc = Integer.parseInt(dict.get(byt)[1]);
			} catch (Exception e) {

				System.err.println(Integer.toHexString(12));
				System.err.println(byt);
				System.err.println(command);
				System.exit(-1);
			}
			command += " " + dict.get(byt)[0] + " ";
			for (int j = 1; j < inc; j++) {
				if (Integer.toHexString(Byte.toUnsignedInt(bytes[i + (inc - j)])).toUpperCase().length() < 2) {
					command += "0";
				}
				command += Integer.toHexString(Byte.toUnsignedInt(bytes[i + (inc - j)])).toUpperCase();
			}
			instructions += command + "\n";
			i += inc;
		}
		
		// print instructions string
		for (String line : instructions.split("\n")) {
			System.out.println(line);
		}
	}
}
