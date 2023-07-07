package org.tcs.ion.camera.util;

import java.io.File;
import java.util.Optional;

public class Read {
    public static void fromCsvOrExcel(String filePathString) {
        try {
            String ext = validateFile(filePathString);
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    private static String validateFile(String filePathString) throws Exception {
        File file = new File(filePathString);
        Optional<String> ext = getExtension(filePathString);

        if (!file.exists())
            throw new Exception("Excel or CSV file dose not exists.");
        else if (file.isDirectory())
            throw new Exception("Provide path of Excel or CSV file.");
        else if (ext.isPresent()) {
            String extStr = ext.get();
            if (extStr.equals("csv") || extStr.equals("xls") || extStr.equals("xlsx"))
                return extStr;
            else
                throw new Exception("Not a Excel or CSV file.");
        } else
            throw new Exception("Unable to determine file type.");
    }

    private static Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1)).map(String::toLowerCase);
    }
}
