package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.content.Context;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Class for writing game results into excel sheet.
 */
public class Writer {
    public static final int STATE_NO_WRITING = 0;
    public static final int STATE_WRITING = 1;
    public static final int STATE_WRITING_FAILED = 2;
    private ArrayList<WriterObserver> observers;
    private Context context;
    private int currentState;
    private String error;

    public Writer(Context context) {
        this.context = context;
        setObservers(new ArrayList<WriterObserver>());
        setCurrentState(STATE_NO_WRITING);
        setError(null);
    }

    //Write into excel sheet with given "fileName", "sheetName", headers being the first row of sheet, and gameResults being the rest of the rows.
    public void writeExcel(String fileName, String sheetName, String[] headers, List<Object> gameResults) {
        setCurrentState(STATE_WRITING);
        //String directory = Environment.getExternalStorageDirectory() + "/Documents/";

        //Anonymous class which is run by a separate thread in order to prevent response problems in user interface. In other words, writing is done in a separate
        //thread so that user interface doesn't become unresponsive.

        class Run implements Runnable {
            private String fileName;
            private String sheetName;
            private String[] headers;
            private List<Object> gameResults;

            public Run(String fileName, String sheetName, String[] headers, List<Object> gameResults) {
                this.fileName = fileName;
                this.sheetName = sheetName;
                this.headers = headers;
                this.gameResults = gameResults;
            }

            public void run() {
                File excelFile = new File("/sdcard/" + fileName); //Create file in sd card.
                HSSFWorkbook workbook = new HSSFWorkbook(); //Use Apache POI library classes to create workbooks, and sheets.
                HSSFSheet sheet = workbook.createSheet(sheetName);
                writeHeaders(sheet, headers); //Firstly, write all the headers to the first row.
                writeGameResults(sheet, gameResults); //Then, write game results to the rest of the excel sheet.

        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/

                try {
                    if (!excelFile.exists()) {
                        excelFile.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(excelFile); //obtain file output stream which will be used to write into actual file.
                    workbook.write(fos);//write workbook which is stored in the memory to actual file in the disk.
                    fos.close(); //close output stream
                    ((Activity) getContext()).runOnUiThread(new Runnable() { //update Writer's status, so that background-job-dialog also closes or opens.
                        @Override
                        public void run() {
                            setCurrentState(STATE_NO_WRITING);
                        }
                    });
                } catch (Exception e) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCurrentState(STATE_WRITING_FAILED);
                        }
                    });

                    setError(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }

        new Thread(new Run(fileName, sheetName, headers, gameResults)).start();
    }

    private void writeHeaders(HSSFSheet sheet, String[] headers) {
        HSSFRow headerRow = sheet.createRow(0);
        int length = headers.length;

        HSSFCellStyle p1CellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.RED.index, true, sheet.getWorkbook());
        HSSFCellStyle p2CellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.LIGHT_BLUE.index, true, sheet.getWorkbook());
        HSSFCellStyle gameSettingsCellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.LIGHT_GREEN.index, true, sheet.getWorkbook());

        for (int i = 0; i < length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);

            if (-1 < i && i <= 1) {  //Style for columns containing game settings information.
                cell.setCellStyle(gameSettingsCellStyle);
            } else if (1 < i && i <= 5) {  //Style for columns containing player 1 information.
                cell.setCellStyle(p1CellStyle);
            } else if (5 < i && i <= 9) {  //Style for columns containing player 2 information.
                cell.setCellStyle(p2CellStyle);
            } else {  //Style for columns containing game settings information.
                cell.setCellStyle(gameSettingsCellStyle);
            }
        }
    }

    private void writeGameResults(HSSFSheet sheet, List<Object> gameResults) {
        int size = gameResults.size();
        HSSFCellStyle p1CellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.RED.index, true, sheet.getWorkbook());
        HSSFCellStyle p2CellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.LIGHT_BLUE.index, true, sheet.getWorkbook());
        HSSFCellStyle gameSettingsCellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.LIGHT_GREEN.index, true, sheet.getWorkbook());

        for (int i = 0; i < size; i++) {
            HSSFRow row = sheet.createRow(i + 1);
            StringTokenizer tokenizer = new StringTokenizer(gameResults.get(i).toString(), GameResult.SPLIT_WITH);
            int j = 0;

            while (tokenizer.hasMoreTokens()) {
                String cellValue = tokenizer.nextToken();
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(cellValue);

                if (-1 < j && j <= 1) { //Style for columns containing game settings information.
                    cell.setCellStyle(gameSettingsCellStyle);
                } else if (1 < j && j <= 5) { //Style for columns containing player 1 information.
                    cell.setCellStyle(p1CellStyle);
                } else if (5 < j && j <= 9) {  //Style for columns containing player 2 information.
                    cell.setCellStyle(p2CellStyle);
                } else { //Style for columns containing game settings information.
                    cell.setCellStyle(gameSettingsCellStyle);
                }

                j++;
            }
        }
    }

    public HSSFCellStyle createCellStyle(short foreground, short background, boolean wrapText, HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setColor(foreground);
        cellStyle.setFont(font);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(background);
        cellStyle.setWrapText(wrapText);
        return cellStyle;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void addObserver(WriterObserver observer) {
        if (!getObservers().contains(observer)) {
            getObservers().add(observer);
        }
    }

    public void removeObserver(WriterObserver observer) {
        getObservers().remove(observer);
    }

    public void notifyObservers() {
        int size = getObservers().size();

        for (int i = 0; i < size; i++) {
            getObservers().get(i).update(this);
        }
    }

    private ArrayList<WriterObserver> getObservers() {
        return observers;
    }

    private void setObservers(ArrayList<WriterObserver> observers) {
        this.observers = observers;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
        notifyObservers();
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
