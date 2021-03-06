import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	String titleRegex = "[Tt][Ii][Tt][Ll][Ee]=[A-Za-z\\s0-9]+";
	String subtitleRegex = "[Ss][Uu][Bb][Tt][Ii][Tt][Ll][Ee]=[A-Za-z\\s0-9]+";
	String spacingRegex = "[Ss][Pp][Aa][Cc][Ii][Nn][Gg]=[\\s0-9.]+";

	String acceptedSymbols = "^[\\|\\*\\-<>0-9hps%]+$";
	String measureSeparators = "[\\|\\D\\%]";

	public Parser() {

	}

	private String subsituteSymbols(String sections) {// remove|,|| to|

		char repeatNum = '=';

		String out = sections;

		Matcher m = Pattern.compile("\\|[0-9]").matcher(sections);

		while (m.find()) {
			repeatNum = m.group().toString().charAt(1);

		}

		out = out.replaceAll("\\|\\|\\|", "T");
		out = out.replaceAll("\\|[0-9]", "(" + repeatNum + "%");
		out = out.replaceAll("<", "-");
		out = out.replaceAll("s", "/");
		out = out.replaceAll("\\s", ",");

		return out;
	}

	public Tablature readFile(File file) throws FileNotFoundException {

		Scanner s = new Scanner(file);
		Tablature returnTab = new Tablature();

		while (s.hasNext()) {
			String nextLine = s.nextLine();

			if (nextLine.isEmpty()) {// blank line
				continue;
			}

			if (readHeader(returnTab, nextLine))
				continue;

			if (nextLine.matches(acceptedSymbols)) {
				nextLine = subsituteSymbols(nextLine);
				StringTokenizer StrTkn = new StringTokenizer(nextLine,
						measureSeparators);
				if (StrTkn.countTokens() > 1) {
					returnTab.addMultiMeasureLine(StrTkn);

				} else {
					returnTab.addLineToLastMeasure(StrTkn.nextToken());
				}

			}

		}

		return returnTab;
	}
/*
	private void print(Object s) {
		System.out.println(s);
	}
*/
	private boolean readHeader(Tablature returnTab, String nextLine) {

		if (nextLine.matches(titleRegex)) {
			returnTab.set_Title(nextLine.substring(nextLine.indexOf("=") + 1));
			return true;
		}
		if (nextLine.matches(subtitleRegex)) {
			returnTab
					.set_Subtitle(nextLine.substring(nextLine.indexOf("=") + 1));
			return true;
		}
		if (nextLine.matches(spacingRegex)) {
			returnTab.set_Spacing(Float.valueOf(nextLine.replaceAll(
					"[A-Za-z=]+", "")));
			return true;
		}
		return false;
	}

}
