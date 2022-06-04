package csv;

import java.security.KeyStore;
import java.util.*;
import java.util.function.Predicate;

class TableImpl implements Table {
    private List<ColumnImpl> columnList = new ArrayList<>();

    public TableImpl(List<List<String>> list, boolean isFirstLineHeader) {
        List<List<String>> tmpList = new ArrayList<>();

        if(isFirstLineHeader)
        {
            for (int i = 0; i < list.get(0).size(); i++)//
            {
                List<String> tmp = new ArrayList<>();
                for (int j = 1; j < list.size(); j++)
                    tmp.add(list.get(j).get(i));
                tmpList.add(tmp);
            }
            for (int i = 0; i < list.get(0).size(); i++) {
                ColumnImpl column = new ColumnImpl(tmpList.get(i), list.get(0).get(i));
                columnList.add(column);
            }
        }
        else
        {
            for (int i = 0; i < list.get(0).size(); i++)//
            {
                List<String> tmp = new ArrayList<>();
                for (int j = 0; j < list.size(); j++)
                    tmp.add(list.get(j).get(i));
                tmpList.add(tmp);
            }
            for (int i = 0; i < list.get(0).size(); i++) {
                ColumnImpl column = new ColumnImpl(tmpList.get(i), null);
                columnList.add(column);
            }
        }

    }
    public TableImpl(List<List<String>> list) {
        List<List<String>> tmpList = new ArrayList<>();

        for (int i = 0; i < list.get(0).size(); i++)//
        {
            List<String> tmp = new ArrayList<>();
            for (int j = 1; j < list.size(); j++)
                tmp.add(list.get(j).get(i));
            tmpList.add(tmp);
        }
        for (int i = 0; i < list.get(0).size(); i++) {
            ColumnImpl column = new ColumnImpl(tmpList.get(i), list.get(0).get(i));
            columnList.add(column);
        }
    }

    @Override
    public String toString() {
        String result =  "<"+ this.getClass().getName() + "@" + Integer.toHexString(hashCode()) + ">\n"
                + "RangeIndex: " + getColumn(0).count() + " entries, 0 to " + (columnList.get(0).getSize() - 1) + "\n"
                + "Data columns" + "(total " + columnList.size() + " columns) :\n"
                + String.format(" %s | %11s | %6s %8s | %6s\n", " #", "Columns", "Count", "Non-Null", "Dtype");
        for (int i = 0; i < columnList.size(); i++) {
            result += String.format(" %2d | %11s | %6s %8s | %6s\n", i, columnList.get(i).getHeader(), columnList.get(i).getSize(),
                    (columnList.get(i).getSize() != columnList.get(i).getNullCount()) ? "non-null" : "null", columnList.get(i).getType());
        }
        return result;
    }

    @Override
    public void print() {

        for (int i = 0; i < columnList.size(); i++)
            System.out.printf(String.format(" %%%ds |", columnList.get(i).getLength()), columnList.get(i).getHeader());

        System.out.println();
        for (int i = 0; i < columnList.get(0).getSize(); i++) {
            String[] tmp = new String[columnList.size()];
            for (int j = 0; j < columnList.size(); j++) {
                if (columnList.get(j).getValue(i).isEmpty())
                    tmp[j] = null;
                else
                    tmp[j] = columnList.get(j).getValue(i);
                System.out.printf(String.format(" %%%ds |", columnList.get(j).getLength()), tmp[j]);
            }
            System.out.println();
        }
    }

    @Override
    public Table getStats() {

        List<List<String>> tmpList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            tmpList.add(new ArrayList<>());
        }
        tmpList.get(0).add("");
        tmpList.get(1).add("count");
        tmpList.get(2).add("mean");
        tmpList.get(3).add("std");
        tmpList.get(4).add("min");
        tmpList.get(5).add("25%");
        tmpList.get(6).add("50%");
        tmpList.get(7).add("75%");
        tmpList.get(8).add("max");
        for (int i = 0; i < columnList.size(); i++) {
            try {
                tmpList.get(2).add(String.valueOf(Math.round(columnList.get(i).getMean() * 1000000) / 1000000.0));
                tmpList.get(3).add(String.valueOf(Math.round(columnList.get(i).getStd() * 1000000) / 1000000.0));
                tmpList.get(4).add(String.valueOf(columnList.get(i).getNumericMin()));
                tmpList.get(5).add(String.valueOf(columnList.get(i).getQ1()));
                tmpList.get(6).add(String.valueOf(columnList.get(i).getMedian()));
                tmpList.get(7).add(String.valueOf(columnList.get(i).getQ3()));
                tmpList.get(8).add(String.valueOf(columnList.get(i).getNumericMax()));
                tmpList.get(0).add(columnList.get(i).getHeader());
                tmpList.get(1).add(String.valueOf(columnList.get(i).getNumericCount()));
            } catch (NumberFormatException e) {
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
        for (int i = 0; i < columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());
        for (int i = 0; i < lineCount; i++) {
            List<String> tmp = new ArrayList<>();
            for (int j = 0; j < columnList.size(); j++) {
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
        for (int i = 0; i < columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());
        int size = columnList.get(0).count();
        for (int i = size - lineCount; i < size; i++) {
            List<String> tmp = new ArrayList<>();
            for (int j = 0; j < columnList.size(); j++) {
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
        for (int i = 0; i < columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i = beginIndex; i < endIndex; i++) {
            List<String> tmp = new ArrayList<>();
            for (int j = 0; j < columnList.size(); j++) {
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
        for (int i = 0; i < columnList.size(); i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i : indices) {
            List<String> tmp = new ArrayList<>();
            for (int j = 0; j < columnList.size(); j++) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;
    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        List<List<String>> tmpList = new ArrayList<>();
        tmpList.add(new ArrayList<>());
        for (int i = beginIndex; i < endIndex; i++)
            tmpList.get(0).add(columnList.get(i).getHeader());

        for (int i = 0; i < columnList.get(beginIndex).count(); i++) {
            List<String> tmp = new ArrayList<>();
            for (int j = beginIndex; j < endIndex; j++) {
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

        for (int i = 0; i < columnList.get(indices[0]).count(); i++) {
            List<String> tmp = new ArrayList<>();
            for (int j : indices) {
                tmp.add(columnList.get(j).getValue(i));

            }
            tmpList.add(tmp);
        }
        Table select = new TableImpl(tmpList);
        return select;
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        Column selectColumn = getColumn(columnName);
        List<Integer> rowList = new ArrayList<>();
        List<List<String>> returnList = new ArrayList<>();
        for (int i = 0; i < selectColumn.count(); i++) {
            String selectValue = selectColumn.getValue(i);
            try {
                if(selectValue.isEmpty())
                    selectValue = null;
                if (predicate.test((T) selectValue))
                    rowList.add(i);
            } catch (Exception e) {
                if (selectValue != null) {
                    try {
                        if (predicate.test((T) (Double) Double.parseDouble(selectColumn.getValue(i))))
                            rowList.add(i);
                    } catch (Exception e1) {
                        try {
                            if (predicate.test((T) (Integer) Integer.parseInt(selectColumn.getValue(i))))
                                rowList.add(i);
                        } catch (NumberFormatException e2) {
                        }
                    }
                }
            }
        }
        returnList.add(new ArrayList<>());
        for (int i = 0; i < getColumnCount(); i++)
            returnList.get(0).add(getColumn(i).getHeader());

        for (int j : rowList) {
            List<String> tmp = new ArrayList<>();
            for (int i = 0; i < getColumnCount(); i++) {
                tmp.add(columnList.get(i).getValue(j));

            }
            returnList.add(tmp);
        }
        Table select = new TableImpl(returnList);
        return select;
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        List<List<String>> oriList = new ArrayList<>();
        for (int i = 0; i < getColumnCount(); i++) {
            List<String> t = new ArrayList<>();
            for (int j = 0; j < getRowCount(); j++)
                t.add(columnList.get(i).getValue(j));
            oriList.add(t);
        }
        List<Map.Entry<Integer, String>> tmpList = new LinkedList<>();
        List<Map.Entry<Integer, String>> nulllist = new LinkedList<>();
        List<Map.Entry<Integer, String>> resultList = new LinkedList<>();

        for (int i = 0; i < columnList.get(byIndexOfColumn).count(); i++) {
            Map.Entry<Integer, String> entry = new AbstractMap.SimpleEntry<Integer, String>(i, columnList.get(byIndexOfColumn).getValue(i));
            if (entry.getValue().isEmpty()) {
                nulllist.add(entry);
            } else {
                tmpList.add(entry);
            }
        }

        if (isAscending) {
            if (columnList.get(byIndexOfColumn).isNumericColumn()) {
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
            } else {
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
        } else {
            if (columnList.get(byIndexOfColumn).isNumericColumn()) {
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
            } else {
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
        if (isNullFirst) {
            resultList.addAll(nulllist);
            resultList.addAll(tmpList);
        } else {
            resultList.addAll(tmpList);
            resultList.addAll(nulllist);
        }


        for (int i = 0; i < getColumnCount(); i++)
            for (int j = 0; j < getRowCount(); j++) {
                columnList.get(i).setValue(j, oriList.get(i).get(resultList.get(j).getKey()));
            }
        return this;
    }

    @Override
    public Table shuffle() {
        List<List<String>> oriList = new ArrayList<>();
        for (int i = 0; i < getColumnCount(); i++) {
            List<String> t = new ArrayList<>();
            for (int j = 0; j < getRowCount(); j++)
                t.add(columnList.get(i).getValue(j));
            oriList.add(t);
        }
        List<Map.Entry<Integer, String>> tmpList = new LinkedList<>();
        for (int i = 0; i < getColumn(0).count(); i++) {
            Map.Entry<Integer, String> entry = new AbstractMap.SimpleEntry<Integer, String>(i, getColumn(0).getValue(i));
            tmpList.add(entry);
        }
        Collections.shuffle(tmpList);
        for (int i = 0; i < getColumnCount(); i++)
            for (int j = 0; j < getRowCount(); j++) {
                columnList.get(i).setValue(j, oriList.get(i).get(tmpList.get(j).getKey()));
            }
        return this;
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
        for (int i = 0; i < columnList.size(); i++) {
            if (columnList.get(i).getHeader().equals(name))
                return columnList.get(i);
        }
        return null;
    }

    @Override
    public boolean fillNullWithMean() {
        boolean result = false;
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumn(i).fillNullWithMean())
                result = true;
        }
        return result;
    }

    @Override
    public boolean fillNullWithZero() {
        boolean result = false;
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumn(i).fillNullWithZero())
                result = true;
        }
        return result;
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public boolean standardize() {
        boolean result = false;
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumn(i).standardize())
                result = true;
        }
        return result;
    }

    @Override
    public boolean normalize() {

        boolean result = false;
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumn(i).normalize())
                result = true;
        }
        return result;
    }

    @Override
    public boolean factorize() {
        boolean result = false;
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumn(i).factorize())
                result = true;
        }
        return result;
    }
}
