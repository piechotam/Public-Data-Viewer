package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser;


import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions.FileCanNotBeReadException;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.export.TableExporter;
import tech.tablesaw.api.Table;

import java.io.IOException;

public class ParserDemonstrator {

    public static void main(String[] args) throws IOException {

        Table readTable = null;
        try {
            readTable = CsvReader.readCsvFromURL("https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/bush.csv");
        } catch (FileCanNotBeReadException e) {
            throw new RuntimeException(e);
        }

        System.out.println("-------BASIC_INFO-------");
        CsvParser.getBasicTableInfo(readTable);
        System.out.println("-------STATS_FOR_COLUMNS-------");
        CsvParser.getStatisticsForNumericalColumns(readTable, "date", CsvParser.StatisticsType.MEAN);
        CsvParser.getStatisticsForNumericalColumns(readTable, "approval", CsvParser.StatisticsType.MAX);
        System.out.println("-------SORTING-------");
        CsvParser.sortByColumn(readTable, "approval", false);
        System.out.println("-------FILTERING-------");
        System.out.println(CsvParser.filter(readTable, "approval", "87"));
        System.out.println(CsvParser.filterByInterval(readTable, "approval", 80, 85));
        System.out.println(CsvParser.filterByInterval(readTable, "approval", 80, 80));

        System.out.println("------------ EXPORT ------------------");
        TableExporter.exportCSV(readTable, "readTableExample");
    }
}