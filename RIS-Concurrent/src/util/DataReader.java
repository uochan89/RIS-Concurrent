package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

	public static long[] readData(String filePath) {
    	File file = new File(filePath);
        FileReader fileReader = null;
    	try {
    		fileReader = new FileReader(file);
    	} catch (FileNotFoundException e1) {
    		e1.printStackTrace();
    	}
    	
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<Integer> data = new ArrayList<Integer>();
        String line;
        try {
    		while ((line = bufferedReader.readLine()) != null) {
    		    data.add(Integer.parseInt(line));
    		}
    		bufferedReader.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
        return data.stream().mapToLong(i->i).toArray();
    }
	
}
