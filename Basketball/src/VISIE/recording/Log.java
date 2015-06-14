/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.recording;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;


/**
 *
 * @author huang
 */
public class Log {

    public static void write(String filename, String contents){
      try{
      File file = new File(filename);
      FileWriter writer = new FileWriter(file, true);
      BufferedWriter bufferedWriter = new BufferedWriter(writer, 8192);
      bufferedWriter.write(contents);
      bufferedWriter.newLine();
      bufferedWriter.flush();
      bufferedWriter.close();
      }
      catch(IOException e){
        System.out.println(e);
      }
    }

    public static void clearFile(String filename){
     File file = new File(filename);
     if(file.exists())
         file.delete();
    }
    
}
