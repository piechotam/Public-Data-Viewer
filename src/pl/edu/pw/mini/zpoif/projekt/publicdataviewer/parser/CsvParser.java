package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;

public class CsvParser {

    // mozliwe statystyki do obliczenia
    public enum StatisticsType {
        MEDIAN, MEAN, MAX, MIN, STANDARD_DEVIATION, VARIANCE, SUM
    }

    //wypisuje podstawowe info zeby user wiedzial co i jak
    //to tez wypisuje jaka kolumna ma jaki typ wiec tez wiadomo jak scastowac
    public static void getBasicTableInfo(Table t) {
        System.out.println(t.structure());
        System.out.println("Size of the table: " + t.shape());
    }

    //to samo co wyzej tylko zwraca stringa zamiast printa
    public static String getBasicTableInfoString(Table t) {
        return t.structure().toString();
    }

    //sprawdza czy podana kolumna ma liczbowe wartosci
    //wydaje mi sie ze czesto moze byc potrzebny ten kawalek kodu wiec robie osobna metode do tego
    public static boolean isColumnNumerical(ColumnType type) {
        ColumnType[] array = {ColumnType.DOUBLE, ColumnType.FLOAT, ColumnType.INTEGER, ColumnType.LONG, ColumnType.SHORT};
        List<ColumnType> numericTypes = new ArrayList<ColumnType>(Arrays.asList(array));

        return numericTypes.contains(type);
    }

    //Oblicza wybrane statystyki, po sprawdzeniu czy jest numeryczna kolumna
    public static double getStatisticsForNumericalColumns(Table t, String columnName, StatisticsType statisticsType) {
        if (!isColumnNumerical(t.column(columnName).type())) {
            System.out.println("Statistics are available only for numerical value columns.");
            return -1;
        } else {

            switch (statisticsType) {
                case MEDIAN:
                    return t.numberColumn(columnName).median();
                case MEAN:
                    return t.numberColumn(columnName).mean();
                case MAX:
                    return t.numberColumn(columnName).max();
                case MIN:
                    return t.numberColumn(columnName).min();
                case STANDARD_DEVIATION:
                    return t.numberColumn(columnName).standardDeviation();
                case VARIANCE:
                    return t.numberColumn(columnName).variance();
                case SUM:
                    return t.numberColumn(columnName).sum();
                default:
                    return 0;
            }


        }
    }

    //sortuje po kolumnie, flaga ascending zeby wiedziec czy rosnaco czy malejaco
    public static Table sortByColumn(Table t, String columnName, boolean ascending) {
        Table sortedTable = t;

        if (ascending) {
            sortedTable = t.sortAscendingOn(columnName);
        } else {
            sortedTable = t.sortDescendingOn(columnName);
        }
        
        return sortedTable;
    }


    //szuka wartosci w kolumnie, narazie tylko dla numerycznych stad ten brzydki else
    public static Table filterNumerical(Table t, String columnName, double value) {
        if (isColumnNumerical(t.column(columnName).type())) {
            NumericColumn<?> column = t.numberColumn(columnName);
            return t.where(column.isEqualTo(value));
        } else {
            return t;
        }
    }

    public static Table filter(Table t, String columnName, String value) {
        // stringi
        if (t.column(columnName).type().equals(ColumnType.STRING)) {
            return t.where(t.stringColumn(columnName).isEqualTo(value));
        } else if (t.column(columnName).type().equals(ColumnType.LOCAL_DATE)) {
            // daty
            return t.where(t.dateColumn(columnName).isEqualTo(LocalDate.parse(value)));
        } else if (isColumnNumerical(t.column(columnName).type())) {
            // liczby
            return filterNumerical(t, columnName, Double.parseDouble(value));
        } else {
            return t;
        }
    }


    //filtruje tylko te wiersze w ktorych wartosci danej kolumny sa z danego przedzialu
    public static Table filterByInterval(Table t, String columnName, double leftBound, double rightBound) {
        //jak ktos podal przedzial w stylu [1,1] to wyszukuje jedynki
        if (leftBound == rightBound) {
            return filterNumerical(t, columnName, rightBound);
        }

        if (isColumnNumerical(t.column(columnName).type())) {
            NumericColumn<?> column = t.numberColumn(columnName);
            return t.where(column.isGreaterThan(leftBound).and(column.isLessThan(rightBound)));
        } else {
            System.out.println("This method is used for numerical columns only. Check methods for filtering other column types.");
            return t;
        }
    }

    //to samo co poprzednik tylko po datach
    public static Table filterByIntervalDate(Table t, String columnName, LocalDate date1, LocalDate date2) {
        if (t.column(columnName).type().equals(ColumnType.LOCAL_DATE)) {
            DateColumn column = t.dateColumn(columnName);
            return t.where(column.isAfter(date1).and(column.isBefore(date2)));
        } else {
            System.out.println("This method is used for numerical columns only. Check methods for filtering other column types.");
            return t;
        }
    }

}
