package com.example.bplustreebackend.service;

import com.example.bplustreebackend.entity.BPlusTree;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class BTreeService implements BTreeRepository {

    private static String path = "src/main/java/com/example/bplustreebackend/data/";

    @Override
    public void createBPlusTree(int index, int bTreeOrder) {
        BPlusTree<Integer,Integer> bPlusTree = new BPlusTree<Integer,Integer>(bTreeOrder);
        writeBPlusTree(index,bPlusTree);
    }

    @Override
    public BPlusTree<Integer,Integer> readBPlusTree(int index) {
        File file = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        BPlusTree<Integer, Integer> b = null;
        try {
            file = new File(path+index+".txt");
            if (!file.exists()){
                file.createNewFile();
            }
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            b = (BPlusTree<Integer, Integer>) ois.readObject();
            b.showBPlusTree();
            fis.close();
            ois.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            return b;
        }
    }

    @Override
    public void writeBPlusTree(int index, BPlusTree<Integer,Integer> bPlusTree) {
        File file = null;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            file = new File(path+index+".txt");
            if (!file.exists()){
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            //向文件中写入对象的数据
            oos.writeObject(bPlusTree);
            //清空缓冲区
            oos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                //关闭资源
                fos.close();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public BPlusTree<Integer,Integer> insert(int index, int value) {
        BPlusTree<Integer,Integer> bPlusTree = readBPlusTree(index);
        bPlusTree.insert(value,value);
        bPlusTree.showBPlusTree();
        writeBPlusTree(index,bPlusTree);
        return readBPlusTree(index);
    }

    @Override
    public BPlusTree<Integer,Integer> delete(int index, int key) {
        BPlusTree<Integer,Integer> bPlusTree = readBPlusTree(index);
        bPlusTree.delete(key);
        bPlusTree.showBPlusTree();
        writeBPlusTree(index,bPlusTree);
        return readBPlusTree(index);
    }

    public BPlusTree<Integer,Integer> insertAll(String all_query_file_text){

        int bTreeOrder = 4;
        int fileNum = 9;
        if (all_query_file_text!=null){
            String str = all_query_file_text;
            str = str.substring(1,str.length()-1);
            String[] list = str.split(", ");
            int[] intList = new int[list.length];
            for (int i=0;i<list.length;i++){
                intList[i] = Integer.parseInt(list[i]);
            }

            BPlusTree<Integer,Integer> bPlusTree = new BPlusTree<Integer,Integer>(bTreeOrder);
            for (int i=0;i<intList.length;i++){
                bPlusTree.insert(intList[i],intList[i]);
            }
            bPlusTree.showBPlusTree();

            for (int i=0;i<fileNum;i++){
                writeBPlusTree(i,bPlusTree);
            }
            return bPlusTree;
        }
        return null;
//        String path = "D:/Desktop/all_query_file_train.txt";
//        File file = new File(path);
//        StringBuffer insertBuffer = null;
//        if(file.isFile() && file.exists()){
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                insertBuffer = new StringBuffer();
//                String text = null;
//                while((text = bufferedReader.readLine()) != null){
//                    insertBuffer.append(text);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (insertBuffer!=null){
//            String str = insertBuffer.toString();
//            str = str.substring(1,str.length()-1);
//            String[] list = str.split(", ");
//            int[] intList = new int[list.length];
//            for (int i=0;i<list.length;i++){
//                intList[i] = Integer.parseInt(list[i]);
//            }
//
//            BPlusTree<Integer,Integer> bPlusTree = new BPlusTree<Integer,Integer>(bTreeOrder);
//            for (int i=0;i<intList.length;i++){
//                bPlusTree.insert(intList[i],intList[i]);
//            }
//            bPlusTree.showBPlusTree();
//
//            for (int i=0;i<fileNum;i++){
//                writeBPlusTree(i,bPlusTree);
//            }
//            return bPlusTree;
//        }
//        return null;


    }

    public List<List<Integer>> getDelList(String[] server_query_file_text){
//        String path = "D:/Desktop/server_query_file_train.txt";
//        File file = new File(path);
//        StringBuffer delBuffer = null;
//        if(file.isFile() && file.exists()){
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                delBuffer = new StringBuffer();
//                String text = null;
//                while((text = bufferedReader.readLine()) != null){
//                    delBuffer.append(text);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        List<List<Integer>> delLists = new ArrayList<>();
        for (int k=0;k<server_query_file_text.length;k++){
            String str = server_query_file_text[k];
            str = str.substring(3,str.length()-1);
            String[] list = str.split(", ");
            List<Integer> intList = new ArrayList<>();
            for (int i=0;i<list.length;i++){
                intList.add(Integer.parseInt(list[i]));
            }
            delLists.add(intList);
        }
        return delLists;
//        String str = delBuffer.toString();
//        str = str.replace("[","。");
//        str = str.replace("]","。");
//        str = str.substring(3,str.length()-1);
//        String[] initStrList = str.split(",。");
//        for (int i=0;i<initStrList.length-1;i++){
//            initStrList[i] = initStrList[i].split("。")[0];
//        }
//        List<List<Integer>> delLists = new ArrayList<>();
//        for (int i=0;i<initStrList.length;i++){
//            String[] strList = initStrList[i].split(", ");
//            List<Integer> tempList = new ArrayList<>();
//            for (int j=0;j<strList.length;j++){
//                tempList.add(Integer.parseInt(strList[j]));
//            }
//            delLists.add(tempList);
//        }
//        return delLists;
    }
    public List<BPlusTree<Integer,Integer>> getAll(String[] server_query_file_text){
        int numBPlusTree = 8;
        List<BPlusTree<Integer,Integer>> treeList = new ArrayList<BPlusTree<Integer,Integer>>();

        List<List<Integer>> delLists = getDelList(server_query_file_text);
        if (delLists.size()!=0){
            for (int i=0;i<delLists.size()+1;i++){
                BPlusTree<Integer,Integer> bPlusTree = readBPlusTree(i);
                if (i>0){
                    bPlusTree = delTree(bPlusTree,delLists.get(i-1));
                    writeBPlusTree(i,bPlusTree);
                }
                treeList.add(bPlusTree);
            }
        }
        return treeList;
    }
    public BPlusTree<Integer,Integer> delTree(BPlusTree<Integer,Integer> bPlusTree,List<Integer> delList){
        for (int i=0;i<delList.size();i++){
            bPlusTree.delete(delList.get(i));
        }
        bPlusTree.showBPlusTree();
        return bPlusTree;
    }

//    public BPlusTree<Integer,Integer> getDelBtreeByIndex(int index){
//        BPlusTree<Integer,Integer> bPlusTree = readBPlusTree(0);
//        List<Integer> delList = getDelList().get(index-1);
//        System.out.println("getDelBtreeByIndex: ------1-----");
//        bPlusTree.showBPlusTree();
//        System.out.println("getDelBtreeByIndex: ------1-----");
//        System.out.println("delList.size():  -------"+delList.size());
//        System.out.println();
//        for (int i=0;i<delList.size();i++){
//            System.out.print(delList.get(i)+",");
//        }
//        System.out.println();
//        for (int i=0;i<delList.size();i++){
//            bPlusTree.delete(delList.get(i));
//        }
//        bPlusTree.showBPlusTree();
//        return bPlusTree;
//    }
}





//
//package com.example.bplustreebackend.service;
//
//import com.example.bplustreebackend.entity.BPlusTree;
//import org.springframework.stereotype.Service;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class BTreeService implements BTreeRepository {
//
//    private static String path = "src/main/java/com/example/bplustreebackend/data/";
//
//    @Override
//    public void createBPlusTree(int index, int bTreeOrder) {
//        BPlusTree<Integer,Integer> bPlusTree = new BPlusTree<Integer,Integer>(bTreeOrder);
//        writeBPlusTree(index,bPlusTree);
//    }
//
//    @Override
//    public BPlusTree<Integer,Integer> readBPlusTree(int index) {
//        File file = null;
//        FileInputStream fis = null;
//        ObjectInputStream ois = null;
//        BPlusTree<Integer, Integer> b = null;
//        try {
//            file = new File(path+index+".txt");
//            if (!file.exists()){
//                file.createNewFile();
//            }
//            fis = new FileInputStream(file);
//            ois = new ObjectInputStream(fis);
//            b = (BPlusTree<Integer, Integer>) ois.readObject();
//            b.showBPlusTree();
//            fis.close();
//            ois.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }finally {
//            return b;
//        }
//    }
//
//    @Override
//    public void writeBPlusTree(int index, BPlusTree<Integer,Integer> bPlusTree) {
//        File file = null;
//        FileOutputStream fos = null;
//        ObjectOutputStream oos = null;
//        try {
//            file = new File(path+index+".txt");
//            if (!file.exists()){
//                file.createNewFile();
//            }
//            fos = new FileOutputStream(file);
//            oos = new ObjectOutputStream(fos);
//            //向文件中写入对象的数据
//            oos.writeObject(bPlusTree);
//            //清空缓冲区
//            oos.flush();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                //关闭资源
//                fos.close();
//                oos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public BPlusTree<Integer,Integer> insert(int index, int value) {
//        BPlusTree<Integer,Integer> bPlusTree = readBPlusTree(index);
//        bPlusTree.insert(value,value);
//        bPlusTree.showBPlusTree();
//        writeBPlusTree(index,bPlusTree);
//        return readBPlusTree(index);
//    }
//
//    @Override
//    public BPlusTree<Integer,Integer> delete(int index, int key) {
//        BPlusTree<Integer,Integer> bPlusTree = readBPlusTree(index);
//        bPlusTree.delete(key);
//        bPlusTree.showBPlusTree();
//        writeBPlusTree(index,bPlusTree);
//        return readBPlusTree(index);
//    }
//
//    public BPlusTree<Integer,Integer> insertAll(){
//
//        int bTreeOrder = 4;
//        int fileNum = 9;
//
//        String path = "/Users/fengxiaoyue/Desktop/all_query_file_train.txt";
//        File file = new File(path);
//        StringBuffer insertBuffer = null;
//        if(file.isFile() && file.exists()){
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                insertBuffer = new StringBuffer();
//                String text = null;
//                while((text = bufferedReader.readLine()) != null){
//                    insertBuffer.append(text);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (insertBuffer!=null){
//            String str = insertBuffer.toString();
//            str = str.substring(1,str.length()-1);
//            String[] list = str.split(", ");
//            int[] intList = new int[list.length];
//            for (int i=0;i<list.length;i++){
//                intList[i] = Integer.parseInt(list[i]);
//            }
//
//            BPlusTree<Integer,Integer> bPlusTree = new BPlusTree<Integer,Integer>(bTreeOrder);
//            for (int i=0;i<intList.length;i++){
//                bPlusTree.insert(intList[i],intList[i]);
//            }
//            bPlusTree.showBPlusTree();
//
//            for (int i=0;i<fileNum;i++){
//                writeBPlusTree(i,bPlusTree);
//            }
//            return bPlusTree;
//        }
//        return null;
//
//
//    }
//
//    public List<List<Integer>> getDelList(){
//        String path = "/Users/fengxiaoyue/Desktop/server_query_file_train.txt";
//        File file = new File(path);
//        StringBuffer delBuffer = null;
//        if(file.isFile() && file.exists()){
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                delBuffer = new StringBuffer();
//                String text = null;
//                while((text = bufferedReader.readLine()) != null){
//                    delBuffer.append(text);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        String str = delBuffer.toString();
//        str = str.replace("[","。");
//        str = str.replace("]","。");
//        str = str.substring(3,str.length()-1);
//        String[] initStrList = str.split(",。");
//        for (int i=0;i<initStrList.length-1;i++){
//            initStrList[i] = initStrList[i].split("。")[0];
//        }
//        List<List<Integer>> delLists = new ArrayList<>();
//        for (int i=0;i<initStrList.length;i++){
//            String[] strList = initStrList[i].split(", ");
//            List<Integer> tempList = new ArrayList<>();
//            for (int j=0;j<strList.length;j++){
//                tempList.add(Integer.parseInt(strList[j]));
//            }
//            delLists.add(tempList);
//        }
//        return delLists;
//    }
//    public List<BPlusTree<Integer,Integer>> getAll(){
//        int numBPlusTree = 8;
//        List<BPlusTree<Integer,Integer>> treeList = new ArrayList<BPlusTree<Integer,Integer>>();
//
//        List<List<Integer>> delLists = getDelList();
//        if (delLists.size()!=0){
//            for (int i=0;i<delLists.size()+1;i++){
//                BPlusTree<Integer,Integer> bPlusTree = readBPlusTree(i);
//                if (i>0){
//                    bPlusTree = delTree(bPlusTree,delLists.get(i-1));
//                    writeBPlusTree(i,bPlusTree);
//                }
//                treeList.add(bPlusTree);
//            }
//        }
//        return treeList;
//    }
//    public BPlusTree<Integer,Integer> delTree(BPlusTree<Integer,Integer> bPlusTree,List<Integer> delList){
//        for (int i=0;i<delList.size();i++){
//            bPlusTree.delete(delList.get(i));
//        }
//        bPlusTree.showBPlusTree();
//        return bPlusTree;
//    }
//
//    public BPlusTree<Integer,Integer> getDelBtreeByIndex(int index){
//        BPlusTree<Integer,Integer> bPlusTree = readBPlusTree(0);
//        List<Integer> delList = getDelList().get(index-1);
//        System.out.println("getDelBtreeByIndex: ------1-----");
//        bPlusTree.showBPlusTree();
//        System.out.println("getDelBtreeByIndex: ------1-----");
//        System.out.println("delList.size():  -------"+delList.size());
//        System.out.println();
//        for (int i=0;i<delList.size();i++){
//            System.out.print(delList.get(i)+",");
//        }
//        System.out.println();
//        for (int i=0;i<delList.size();i++){
//            bPlusTree.delete(delList.get(i));
//        }
//        bPlusTree.showBPlusTree();
//        return bPlusTree;
//    }
//}
