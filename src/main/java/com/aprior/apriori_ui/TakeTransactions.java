package com.aprior.apriori_ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TakeTransactions {
    List<Set<String>> itemsetList = new ArrayList<>();

    public List<Set<String>> Take(String fileName) throws FileNotFoundException {

        Scanner sc = new Scanner(new File(fileName));
        while (sc.hasNextLine()) {
            String tr = sc.nextLine();
            String[] splitTr = tr.split(",");
            itemsetList.add(new HashSet<>(Arrays.asList(splitTr)));
        }
        return itemsetList;
    }
}
