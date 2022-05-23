package csv;

import java.io.*;
import java.util.*;

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
                {
                    tmp[i] = tmp[i].replaceAll("^\"|\"$", "");
                    tmp[i] = tmp[i].replaceAll("\"\"", "\"");
                }
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

        List<List<String>> oriList = new ArrayList<>();
        List<String> headerList = new ArrayList<>();
        for(int i = 0; i< table.getColumnCount(); i++)
            headerList.add(table.getColumn(i).getHeader());

        for(int i = 0; i< table.getColumnCount(); i++)
            oriList.add(new ArrayList<>());
        for(int i = 0; i< table.getRowCount(); i++)
            for(int j = 0; j< table.getColumnCount(); j++)
                oriList.get(j).add(table.getColumn(j).getValue(i));
        for(int i = 0; i< table.getRowCount(); i++)
        {
            for(int j = 0; j< table.getColumnCount(); j++)
                System.out.print(oriList.get(i).get(j) + " ");
            System.out.println();
        }

                List<List<String>> tableList = new ArrayList<>();
        tableList.add(headerList);
        tableList.addAll(oriList);
        Table resultTable = new TableImpl(tableList);
        resultTable.print();
        List<Map.Entry<Integer, String>> tmpList = new LinkedList<>();
        List<Map.Entry<Integer, String>> nulllist = new LinkedList<>();
        List<Map.Entry<Integer, String>> resultList = new LinkedList<>();

        for (int i = 0; i < resultTable.getColumn(byIndexOfColumn).count(); i++) {
            Map.Entry<Integer,String> entry= new AbstractMap.SimpleEntry<Integer, String>(i, resultTable.getColumn(byIndexOfColumn).getValue(i));
            if(entry.getValue().isEmpty())
            {
                nulllist.add(entry);
            }
            else
            {
                tmpList.add(entry);
            }
        }

        if(isAscending )
        {
            if(resultTable.getColumn(byIndexOfColumn).isNumericColumn())
            {
                Collections.sort(tmpList, new Comparator<Map.Entry<Integer, String>>() {
                    @Override
                    public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                        if (Double.parseDouble(o1.getValue()) < Double.parseDouble(o2.getValue()))
                            return -1;
                        else if (Double.parseDouble(o1.getValue()) > Double.parseDouble(o2.getValue()))
                            return 1;
                        else
                            return 0;
                    }
                });
            }
            else
            {
                Collections.sort(tmpList, new Comparator<Map.Entry<Integer, String>>() {
                    @Override
                    public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                        if (o1.getValue().compareTo(o2.getValue()) < 0)
                            return -1;
                        else if (o1.getValue().compareTo(o2.getValue()) > 0)
                            return 1;
                        else
                            return 0;
                    }
                });
            }
        }
        else
        {
            if(resultTable.getColumn(byIndexOfColumn).isNumericColumn())
            {
                Collections.sort(tmpList, new Comparator<Map.Entry<Integer, String>>() {
                    @Override
                    public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                        if (Double.parseDouble(o1.getValue()) < Double.parseDouble(o2.getValue()))
                            return 1;
                        else if (Double.parseDouble(o1.getValue()) > Double.parseDouble(o2.getValue()))
                            return -1;
                        else
                            return 0;
                    }
                });
            }
            else
            {
                Collections.sort(tmpList, new Comparator<Map.Entry<Integer, String>>() {
                    @Override
                    public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                        if (o1.getValue().compareTo(o2.getValue()) < 0)
                            return 1;
                        else if (o1.getValue().compareTo(o2.getValue()) > 0)
                            return -1;
                        else
                            return 0;
                    }
                });
            }
        }
        if(isNullFirst)
        {
            resultList.addAll(nulllist);
            resultList.addAll(tmpList);
        }
        else
        {
            resultList.addAll(tmpList);
            resultList.addAll(nulllist);
        }

        System.out.println(resultTable.getRowCount() +" " + resultTable.getColumnCount());
        for(int i = 0; i< table.getColumnCount(); i++)
            for (int j = 0; j< table.getRowCount(); j++)
                resultTable.getColumn(i).setValue(j, oriList.get(i).get(resultList.get(j).getKey()));

        return resultTable;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        return null;
    }
}
