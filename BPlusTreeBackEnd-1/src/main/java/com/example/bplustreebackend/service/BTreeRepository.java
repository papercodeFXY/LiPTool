package com.example.bplustreebackend.service;

import com.example.bplustreebackend.entity.BPlusTree;

public interface BTreeRepository {

    //创建B+树
    void createBPlusTree(int index,int bTreeOrder);
    //数据读取和写入
    BPlusTree<Integer,Integer> readBPlusTree(int index);
    void writeBPlusTree(int index,BPlusTree<Integer,Integer> bPlusTree);
    //插入
    BPlusTree<Integer,Integer> insert(int index,int value);
    //删除
    BPlusTree<Integer,Integer> delete(int index,int key);

}
