package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser;

import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions.FileCanNotBeReadException;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvReader {
	//mozliwe separatory w pliku csv
	private static final List<String> possibleSeparators = new ArrayList<>() {
		private static final long serialVersionUID = 4371483360302400314L;

	{
		add(",");
        add(";");
        add("\t");
        add(":");
	}};
	
	
    // odczytuje plik CSV z linku
    // np. https://raw.githubusercontent.com/R-Ladies-Warsaw/PoweR/master/Część%202%20-%20Formatowanie%20danych/Python/data/vgsales.csv
    @SuppressWarnings("deprecation")
	public static Table readCsvFromURL(String link) throws MalformedURLException, FileCanNotBeReadException {
        URL url = null;
        url = new URL(link);
        
        CsvReadOptions.Builder builder = null;
        Character separator = detectSeparator(url);
        
        //jak niezdefiniowany separator to nie odczytamy
        if (separator.equals(null)) {
            throw new FileCanNotBeReadException();
        }
        
        
        try {
            builder = CsvReadOptions.builder(url)
                    .separator(separator)
                    .header(true)
                    .dateFormat("yyyy.MM.dd")
                    .commentPrefix('/');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CsvReadOptions options = builder.build();

        Table t = Table.read().usingOptions(options);

        return t;
    }
    
    //wykrywa separator w pliku csv
    private static Character detectSeparator(URL url) {
		try {
			Scanner s = new Scanner(url.openStream());
			String firstLine = s.nextLine();
	    	for (String separator : possibleSeparators) {
				if (firstLine.contains(separator)) {
					s.close();
					return separator.charAt(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }

}