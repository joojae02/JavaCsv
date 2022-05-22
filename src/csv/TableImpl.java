package csv;

import java.security.KeyStore;
import java.util.*;
import java.util.function.Predicate;

class TableImpl implements Table {
    private List<ColumnImpl> columnList = new ArrayList<>();

    public TableImpl(List<List<String>> list)
    {
        List<List<String>> tmpList = new ArrayList<>();
        for(int i = 0; i < list.get(0).size();i++)//
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 1; j < list.size(); j++)
                tmp.add(list.get(j).get(i));
            tmpList.add(tmp);
        }
        for(int i = 0; i < list.get(0).size(); i++)
        {
            ColumnImpl column = new ColumnImpl(tmpList.get(i),list.get(0).get(i));
            columnList.add(column);
        }
    }

    @Override
    public String toString() {
        String result = this.getClass() +"@" + Integer.toHexString(this.hashCode()) + ">\n"
        + "RangeIndex: " + columnList.get(0).getSize() +" entries, 0 to " + (columnList.get(0).getSize() - 1) +"\n"
        + "Data columns" + "(total " +columnList.size() + "columns) :\n"
        + String.format(" %s |%11s | %6s %8s | %6s\n", "#","Columns", "Count", "Non-Null","Dtype");
        for(int i = 0; i< columnList.size(); i++)
        {
            result += String.format(" %d | %11s | %6s %8s | %6s\n", i,columnList.get(i).getHeader(), columnList.get(i).getSize(),
                    (columnList.get(i).getSize() != columnList.get(i).getNullCount()) ? "non-null":"null",columnList.get(i).getType());
        }
        return result;
    }

    @Override
    public void print() {

        for(int i = 0; i< columnList.size(); i++)
            System.out.printf(String.format(" %%%ds |",columnList.get(i).getLength()),columnList.get(i).getHeader());

        System.out.println();
        for(int i = 0; i< columnList.get(0).getSize(); i++)
        {
            String [] tmp = new String[columnList.size()];
            for(int j = 0; j < columnList.size();  j++)
            {
                if(columnList.get(j).getValue(i).isEmpty())
                    tmp[j] = null;
                else
                    tmp[j] = columnList.get(j).getValue(i);
                System.out.printf(String.format(" %%%ds |",columnList.get(j).getLength()),tmp[j]);
            }
            System.out.println();
        }
    }

    @Override
    public Table getStats() {

        List<List<String>> tmpList = new ArrayList<>();
        for(int i = 0; i< 9; i++)
        {
            tmpList.add(new ArrayList<>());
        }
        tmpList.get(0).add("");tmpList.get(1).add("count");
        tmpList.get(2).add("mean");tmpList.get(3).add("std");tmpList.get(4).add("min");
        tmpList.get(5).add("25%");tmpList.get(6).add("50%");tmpList.get(7).add("75%");tmpList.get(8).add("max");
        for(int i = 0; i< columnList.size(); i++) {
            try{
                tmpList.get(2).add(String.valueOf(columnList.get(i).getMean()));
                tmpList.get(3).add(String.valueOf(columnList.get(i).getStd()));
                tmpList.get(4).add(String.valueOf(columnList.get(i).getNumericMin()));
                tmpList.get(5).add(String.valueOf(columnList.get(i).getQ1()));
                tmpList.get(6).add(String.valueOf(columnList.get(i).getMedian()));
                tmpList.get(7).add(String.valueOf(columnList.get(i).getQ3()));
                tmpList.get(8).add(String.valueOf(columnList.get(i).getNumericMax()));
                tmpList.get(0).add(columnList.get(i).getHeader());
                tmpList.get(1).add(String.valueOf(columnList.get(i).getNumericCount()));
            }
            catch (NumberFormatException e) {
                System.out.println(i+ ", "+ e );
            }
        }

        Table states = new TableImpl(tmpList);
        return states;
    }

    @Override
    public Table head() {
        return head(5);
    }

    @Override
    public Table head(int lineCount) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = 0; i< columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());
        for (int i = 0; i< lineCount; i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 0; j< columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }

        Table head = new TableImpl(tmpList);
        return head;
    }

    @Override
    public Table tail() {
        return tail(5);
    }

    @Override
    public Table tail(int lineCount) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = 0; i< columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());
        int size = columnList.get(0).count();
        for (int i = size - lineCount; i< size; i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 0; j< columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table tail = new TableImpl(tmpList);
        return tail;
    }

    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = 0; i< columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i = beginIndex; i< endIndex; i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 0; j< columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;

    }

    @Override
    public Table selectRowsAt(int... indices) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = 0; i< columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i : indices)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = 0; j< columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = beginIndex; i< endIndex; i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i = 0; i< columnList.get(beginIndex).count(); i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j = beginIndex; j< endIndex; j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;
    }

    @Override
    public Table selectColumnsAt(int... indices) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i : indices)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i = 0; i< columnList.get(indices[0]).count(); i++)
        {
            List<String> tmp = new ArrayList<>();
            for(int j: indices) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        return null;
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        List<ColumnImpl> oriList = new ArrayList<>(columnList);

        Map<Integer, String> tmpMap = new LinkedHashMap<>();
        Map<Integer, String> nullMap = new LinkedHashMap<>();
        for (int i = 0; i < columnList.get(byIndexOfColumn).count(); i++) {
            if(columnList.get(byIndexOfColumn).getValue(i) == null)
            {
                nullMap.put(i,columnList.get(byIndexOfColumn).getValue(i));
            }
            else
            {
                tmpMap.put(i,columnList.get(byIndexOfColumn).getValue(i));
            }
        }
        for(int i = 0; i< tmpMap.size(); i++)
        {
            System.out.println(tmpMap.get(i));
        }
        List<Map.Entry<Integer, String>> entries = new LinkedList<>(tmpMap.entrySet());

        if(isAscending)
        {
            Collections.sort(entries, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        }
//        for(int i = 0; i< getRowCount(); i++)
//        {
//            for (int j = 0; j< getColumnCount(); j++)
//                System.out.print(entries.get(i).getKey() +"," + entries.get(i).getValue());
//            System.out.println();
//        }

        for(int i = 0; i< getRowCount(); i++)
            for (int j = 0; j< getColumnCount(); j++)
            {
                getColumn(j).setValue(i, oriList.get(j).getValue(entries.get(i).getKey()));
            }
        return this;
    }

    @Override
    public Table shuffle() {
        return null;
    }

    @Override
    public int getRowCount() {
        return columnList.get(0).count();
    }

    @Override
    public int getColumnCount() {
        return columnList.size();
    }

    @Override
    public Column getColumn(int index) {
        return columnList.get(index);
    }

    @Override
    public Column getColumn(String name) {
        for(int i = 0 ;i < columnList.size(); i++)
        {
            if(columnList.get(i).getHeader().equals(name))
                return columnList.get(i);
        }
        return null;
    }

    @Override
    public boolean fillNullWithMean() {
        return false;
    }

    @Override
    public boolean fillNullWithZero() {
        return false;
    }

    @Override
    public boolean standardize() {
        return false;
    }

    @Override
    public boolean normalize() {
        return false;
    }

    @Override
    public boolean factorize() {
        return false;
    }
}
