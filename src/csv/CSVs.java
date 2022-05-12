package csv;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVs {
    /**
     * @param isFirstLineHeader csv 파일의 첫 라인을 헤더(타이틀)로 처리할까요?
     */
    public static Table createTable(File csv, boolean isFirstLineHeader) throws FileNotFoundException {
        List<List<String>> list = new ArrayList<List<String>>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csv));
            String line = "";

            while((line=bufferedReader.readLine()) != null) {
                String[] tmp = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                for(int i = 0; i< tmp.length; i++)
                    tmp[i] = tmp[i].replaceAll("\"", "");
                System.out.println();
                List<String> tmpList = new ArrayList<String>(Arrays.asList(tmp));
                list.add((tmpList));
            }

        }catch (IOException e) {
            return null;
        }

//        for(int i = 0; i < list.size();i++)
//        {
//            for(int j = 0; j < list.get(i).size(); j++)
//            {
//                System.out.print(list.get(i).get(j) + " / ");
//            }
//            System.out.println();
//        }
        Table table = new TableImpl(list);

        return table;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table sort(Table table, int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        return null;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        return null;
    }
}
