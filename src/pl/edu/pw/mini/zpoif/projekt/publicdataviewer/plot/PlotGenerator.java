package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.plot;

import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions.ColumnNotNumericalException;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser.CsvParser;
import tech.tablesaw.plotly.*;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.ScatterPlot;
import tech.tablesaw.plotly.components.Figure;

public class PlotGenerator {

    public static void generateHistogram(Table t, String columnName) throws ColumnNotNumericalException{
        if (!CsvParser.isColumnNumerical(t.column(columnName).type())){
            throw new ColumnNotNumericalException();
        }
        else {
            Figure figure =  Histogram.create("Rozkład " + columnName,
                    t,
                    columnName);
            Plot.show(figure);
        }
    }

    public static void generateScatter2D(Table t, String column1, String column2){
        if ((!CsvParser.isColumnNumerical(t.column(column1).type())) || (!CsvParser.isColumnNumerical(t.column(column2).type()))){
            System.out.println("Histograms are available only for numerical value columns.");
        }
        else{
            Figure figure =  ScatterPlot.create("Zależność " + column2 + " od " + column1,
                    t,
                    column1,
                    column2);
            Plot.show(figure);
        }
    }
}
