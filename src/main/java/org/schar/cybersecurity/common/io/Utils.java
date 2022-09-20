package org.schar.cybersecurity.common.io;

import org.schar.cybersecurity.client.Client;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Utils {

    public static String readFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder fileContent = new StringBuilder();

        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            fileContent.append(data);
        }

        return fileContent.toString();
    }

    public static File getFile(String fileName) throws FileNotFoundException, URISyntaxException {
        var resource = Client.class.getClassLoader().getResource(fileName);

        if (resource == null) {
            throw new FileNotFoundException("Could not find file " + fileName);
        }

        return new File(resource.toURI());
    }

}
