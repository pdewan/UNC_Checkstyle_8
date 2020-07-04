package unc.tools.checkstyle.commits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ProjectHistoryCheckerSeparateProcessInvoker {
//  String[] command = {"java", "com.puppycrawl.tools.checkstyle.Main", "-c",  aConfigurationFileName ,   windowsName };
// java -Xms16000M -Xmx16000M -cp "D:\dewan_backup\Java\UNC_Checkstyle_8\target\classes;E:\My Drive\401-f15\Downloads\checkstyle-8.33-all.jar" unc.tools.checkstyle.TestProjectHistoryChecker
 
  public static void main (String[] args) {
    String[] command = new String[args.length + 6];
    command[0] = "java";
    command[1] = "-Xms8000M";
    command[2] = "-Xmx16000M";
    command[3] = "-cp";
    command[4] = "D:\\dewan_backup\\Java\\UNC_Checkstyle_8\\target\\classes;G:\\My Drive\\401-f15\\Downloads\\checkstyle-8.33-all.jar";
    command[5] = "unc.tools.checkstyle.PostProcessingMain";

    
    for (int aCommandIndex = 0; aCommandIndex < args.length; aCommandIndex++) {
      command[aCommandIndex + 6] = args[aCommandIndex];
    }
    try {
        System.out.println("Executing command:" + Arrays.toString(command));
        Process process = Runtime.getRuntime().exec(command);
     
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
     
        reader.close();
       reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
     
        reader.close();
     
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

}
