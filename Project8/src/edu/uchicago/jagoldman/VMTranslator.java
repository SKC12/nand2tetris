package edu.uchicago.jagoldman;

import java.io.File;
import java.util.ArrayList;

public class VMTranslator {

    public static void main(String[] args) {
        File fileName = new File(args[0]);
        File fileOut;
        ArrayList<File> files = new ArrayList<>();
        if (args.length != 1)
            throw new IllegalArgumentException("Inaccurate usage. Please enter in the following format: java VMTranslator (directory/filename)");
        else if (fileName.isFile() && !(args[0].endsWith(".vm")))
            throw new IllegalArgumentException("Not the correct file type. Please enter a .vm file or a directory containing .vm files. ");
        else {
            if (fileName.isFile() && args[0].endsWith(".vm")) {
                files.add(fileName);
                String firstPart = args[0].substring(0, args[0].length() - 3);
                fileOut = new File(firstPart + ".asm");
            } else // fileName is a directory - access all files in the directory
            {
                files = getVMFiles(fileName);
                fileOut = new File(fileName + ".asm");

            }
        }
        // construct CodeWriter to generate code into corresponding output file, only 1 codewriter
        CodeWriter codeWriter = new CodeWriter(fileOut);
        // comment out line below for the tests that do not require bootstrapping code
        codeWriter.writeInit();
        for (File file : files) {
            codeWriter.setFileName(file);
            // construct parser to parse VM input files - use a separate parser for handling each input file
            Parser parser = new Parser(file);
            // march through VM commands in input file, generate assembly code
            while (parser.hasMoreCommands()) {
                parser.advance();
                if (parser.commandType().equals("C_ARITHMETIC")) {
                    codeWriter.writeArithmetic(parser.arg1());
                } else if (parser.commandType().equals("C_PUSH") || parser.commandType().equals("C_POP")) {
                    codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                } else if (parser.commandType().equals("C_LABEL")) {
                    codeWriter.writeLabel(parser.arg1());
                } else if (parser.commandType().equals("C_GOTO")) {
                    codeWriter.writeGoto(parser.arg1());
                } else if (parser.commandType().equals("C_IF")) {
                    codeWriter.writeIf(parser.arg1());


                } else if (parser.commandType().equals("C_FUNCTION")) {
                    codeWriter.writeFunction(parser.arg1(), parser.arg2());
                } else if (parser.commandType().equals("C_RETURN")) {
                    codeWriter.writeReturn();
                } else if (parser.commandType().equals("C_CALL")) {
                    codeWriter.writeCall(parser.arg1(), parser.arg2());
                }

            }

        }

        codeWriter.close();

    }


    // gather all files in the directory argument into an arraylist
    public static ArrayList<File> getVMFiles(File directory) {
        File[] files = directory.listFiles();
        ArrayList<File> fResults = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".vm")) fResults.add(file);
            }
        }
        return fResults;

    }
}
