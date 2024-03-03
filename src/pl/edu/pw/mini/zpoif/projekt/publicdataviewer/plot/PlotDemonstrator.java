package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.plot;

import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions.ColumnNotNumericalException;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions.FileCanNotBeReadException;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser.CsvParser;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser.CsvReader;
import tech.tablesaw.api.Table;

import java.io.IOException;

public class PlotDemonstrator {
    public static void main(String[] args) throws IOException, FileCanNotBeReadException, ColumnNotNumericalException {

        Table readTable = CsvReader.readCsvFromURL("https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/bush.csv");

        Table houseData = CsvReader.readCsvFromURL("https://raw.githubusercontent.com/MI2-Education/2023Z-DataVisualizationTechniques/main/homeworks/hw1/house_data.csv");
        System.out.println("-------HOUSE_DATA_BASIC_INFO-------");
        CsvParser.getBasicTableInfo(houseData);

        // histogramy

        // nie działa, bo nienumeryczne wartości
        PlotGenerator.generateHistogram(readTable, "who");
        // działa
        PlotGenerator.generateHistogram(readTable, "approval");
        PlotGenerator.generateHistogram(houseData, "price");

        // wyskres typu scatter
        // nie działa, bo nienumeryczne kolumny
        PlotGenerator.generateScatter2D(readTable, "date", "approval");
        // próby dla houseData
        // floors jest skalą dyskretną
        PlotGenerator.generateScatter2D(houseData, "price", "floors");
        // sqft_above jest ciągła
        PlotGenerator.generateScatter2D(houseData, "price", "sqft_above");

    }
}
