package oreilly.rxjava.chap06;


import java.util.List;

public interface Repository {
    void store(Record record);
    void storeAll(List<Record> records);
}
