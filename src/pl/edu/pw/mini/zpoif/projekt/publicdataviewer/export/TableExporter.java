package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.export;

import tech.tablesaw.api.Table;

public class TableExporter {

    // eksportuje tabelę do CSV o podanej nazwie

    public static void exportCSV(Table t, String fileName){
        t.write().csv(fileName + ".csv");
    }
}
