package com.example.bplustreebackend.control;


import com.alibaba.fastjson.JSONObject;
import com.example.bplustreebackend.entity.BPlusTree;
import com.example.bplustreebackend.service.BTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RestController
@CrossOrigin
public class Controller {

    @Autowired
    BPlusTree<Integer, Integer> bPlusTree;

    @Autowired
    BTreeService bTreeService;

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public void create(Integer number,Integer bTreeOrder){
        System.out.println(number);
        System.out.println(bTreeOrder);
        for (int i=1;i<number+1;i++){
            bTreeService.createBPlusTree(i,bTreeOrder);
        }
    }

    @RequestMapping(value = "/clear",method = RequestMethod.POST)
    public void clear(Integer number,Integer bTreeOrder){
        for (int i=1;i<number+1;i++){
            bTreeService.createBPlusTree(i,bTreeOrder);
        }
    }
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    public JSONObject insert(Integer index,Integer value){
        return treeToJSONObject(bTreeService.insert(index,value));
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public JSONObject delete(Integer index,Integer key){
        return treeToJSONObject(bTreeService.delete(index,key));
    }

    @RequestMapping(value = "/get",method = RequestMethod.POST)
    public JSONObject get(Integer index){
        return treeToJSONObject(bTreeService.readBPlusTree(index));
    }

    public JSONObject treeToJSONObject(BPlusTree bPlusTree){
        List<String> valList = new ArrayList<>();
        List<Integer> numList = new ArrayList<>();
        Queue<BPlusTree.Node> qNodes = new LinkedList<BPlusTree.Node>();
        numList.add(1);
        Integer index = 0;
        Integer levelNodeNum;
        qNodes.add(bPlusTree.root);
        while (qNodes.size()>0){
            levelNodeNum = numList.get(index);
            index++;
            for (int i=0;i<levelNodeNum;i++){
                BPlusTree.Node node = qNodes.remove();
                if (node!=null&&node.number>0){
                    valList.add(node.showKeys());
                }
                if (node!=null&&node.child[0]!=null){
                    numList.add(node.number);
                    for (int j=0;j<node.number;j++){
                        qNodes.add(node.child[j]);
                    }
                }
            }
        }
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(bPlusTree);
        jsonObject.put("valList",valList);
        jsonObject.put("numList",numList);
        return jsonObject;
    }

//
//    @RequestMapping(value = "/get2",method = RequestMethod.POST)
//    public JSONObject get(){
//        return treeToJSONObject(bTreeService.insertAll());
//    }

    @RequestMapping(value = "/getAll",method = RequestMethod.POST)
    public JSONObject getAll( String all_query_file_text, String[] server_query_file_text){
        System.out.println("----------------all_query_file_text-------------");
        System.out.println(all_query_file_text);
        System.out.println("----------------server_query_file_text-------------");
        System.out.println(server_query_file_text.length);
        System.out.println(server_query_file_text[0]);
        System.out.println("-----------------------------");
        bTreeService.insertAll(all_query_file_text);
        List<BPlusTree<Integer,Integer>> treeList = bTreeService.getAll(server_query_file_text);
        //数量
        int treeNum = treeList.size();
        JSONObject jsonObject = new JSONObject();
        List<List<String>> valLists = new ArrayList<>();
        List<List<Integer>> numLists = new ArrayList<>();
        List<Integer> leafNumList = new ArrayList<>();
        for (int i=0;i<treeNum;i++){
            BPlusTree<Integer,Integer> bPlusTree = treeList.get(i);
            List<String> valList = new ArrayList<>();
            List<Integer> numList = new ArrayList<>();
            Integer leftNum = 0;

            BPlusTree<Integer, Integer>.LeafNode<Integer, Integer> leave = bPlusTree.left;
            while (leave!=null){
                leftNum++;
                if (leave.right!=null){
                    leave = leave.right;
                }else{
                    break;
                }
            }
            leafNumList.add(leftNum);

            Queue<BPlusTree.Node> qNodes = new LinkedList<BPlusTree.Node>();
            numList.add(1);
            Integer index = 0;
            Integer levelNodeNum;
            qNodes.add(bPlusTree.root);
            while (qNodes.size()>0){
                levelNodeNum = numList.get(index);
                index++;
                for (int j=0;j<levelNodeNum;j++){
                    BPlusTree.Node node = qNodes.remove();
                    if (node!=null&&node.number>0){
                        valList.add(node.showKeys());
                    }
                    if (node!=null&&node.child[0]!=null){
                        numList.add(node.number);
                        for (int k=0;k<node.number;k++){
                            qNodes.add(node.child[k]);
                        }
                    }
                }
            }
            valLists.add(valList);
            numLists.add(numList);
        }
        jsonObject.put("valLists",valLists);
        jsonObject.put("numLists",numLists);
        jsonObject.put("leafNumList",leafNumList);
        return jsonObject;
    }
//
//    @RequestMapping(value = "/getDelByIndex",method = RequestMethod.POST)
//    public JSONObject getDelByIndex(int index){
//        return treeToJSONObject(bTreeService.getDelBtreeByIndex(index));
//    }
}






//package com.example.bplustreebackend.control;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.example.bplustreebackend.entity.BPlusTree;
//import com.example.bplustreebackend.service.BTreeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//
//@RestController
//@CrossOrigin
//public class Controller {
//
//    @Autowired
//    BPlusTree<Integer, Integer> bPlusTree;
//
//    @Autowired
//    BTreeService bTreeService;
//
//    @RequestMapping(value = "/create",method = RequestMethod.POST)
//    public void create(Integer number,Integer bTreeOrder){
//        System.out.println(number);
//        System.out.println(bTreeOrder);
//        for (int i=1;i<number+1;i++){
//            bTreeService.createBPlusTree(i,bTreeOrder);
//        }
//    }
//
//    @RequestMapping(value = "/clear",method = RequestMethod.POST)
//    public void clear(Integer number,Integer bTreeOrder){
//        for (int i=1;i<number+1;i++){
//            bTreeService.createBPlusTree(i,bTreeOrder);
//        }
//    }
//    @RequestMapping(value = "/insert",method = RequestMethod.POST)
//    public JSONObject insert(Integer index,Integer value){
//        return treeToJSONObject(bTreeService.insert(index,value));
//    }
//
//    @RequestMapping(value = "/delete",method = RequestMethod.POST)
//    public JSONObject delete(Integer index,Integer key){
//        return treeToJSONObject(bTreeService.delete(index,key));
//    }
//
//    @RequestMapping(value = "/get",method = RequestMethod.POST)
//    public JSONObject get(Integer index){
//        return treeToJSONObject(bTreeService.readBPlusTree(index));
//    }
//
//    public JSONObject treeToJSONObject(BPlusTree bPlusTree){
//        List<String> valList = new ArrayList<>();
//        List<Integer> numList = new ArrayList<>();
//        Queue<BPlusTree.Node> qNodes = new LinkedList<BPlusTree.Node>();
//        numList.add(1);
//        Integer index = 0;
//        Integer levelNodeNum;
//        qNodes.add(bPlusTree.root);
//        while (qNodes.size()>0){
//            levelNodeNum = numList.get(index);
//            index++;
//            for (int i=0;i<levelNodeNum;i++){
//                BPlusTree.Node node = qNodes.remove();
//                if (node!=null&&node.number>0){
//                    valList.add(node.showKeys());
//                }
//                if (node!=null&&node.child[0]!=null){
//                    numList.add(node.number);
//                    for (int j=0;j<node.number;j++){
//                        qNodes.add(node.child[j]);
//                    }
//                }
//            }
//        }
//        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(bPlusTree);
//        jsonObject.put("valList",valList);
//        jsonObject.put("numList",numList);
//        return jsonObject;
//    }
//
//
//    @RequestMapping(value = "/get2",method = RequestMethod.POST)
//    public JSONObject get(){
//        return treeToJSONObject(bTreeService.insertAll());
//    }
//
//    @RequestMapping(value = "/getAll",method = RequestMethod.POST)
//    public JSONObject getAll(){
//        bTreeService.insertAll();
//        List<BPlusTree<Integer,Integer>> treeList = bTreeService.getAll();
//        //数量
//        int treeNum = treeList.size();
//        JSONObject jsonObject = new JSONObject();
//        List<List<String>> valLists = new ArrayList<>();
//        List<List<Integer>> numLists = new ArrayList<>();
//        List<Integer> leafNumList = new ArrayList<>();
//        for (int i=0;i<treeNum;i++){
//            BPlusTree<Integer,Integer> bPlusTree = treeList.get(i);
//            List<String> valList = new ArrayList<>();
//            List<Integer> numList = new ArrayList<>();
//            Integer leftNum = 0;
//
//            BPlusTree<Integer, Integer>.LeafNode<Integer, Integer> leave = bPlusTree.left;
//            while (leave!=null){
//                leftNum++;
//                if (leave.right!=null){
//                    leave = leave.right;
//                }else{
//                    break;
//                }
//            }
//            leafNumList.add(leftNum);
//
//            Queue<BPlusTree.Node> qNodes = new LinkedList<BPlusTree.Node>();
//            numList.add(1);
//            Integer index = 0;
//            Integer levelNodeNum;
//            qNodes.add(bPlusTree.root);
//            while (qNodes.size()>0){
//                levelNodeNum = numList.get(index);
//                index++;
//                for (int j=0;j<levelNodeNum;j++){
//                    BPlusTree.Node node = qNodes.remove();
//                    if (node!=null&&node.number>0){
//                        valList.add(node.showKeys());
//                    }
//                    if (node!=null&&node.child[0]!=null){
//                        numList.add(node.number);
//                        for (int k=0;k<node.number;k++){
//                            qNodes.add(node.child[k]);
//                        }
//                    }
//                }
//            }
//            valLists.add(valList);
//            numLists.add(numList);
//        }
//        jsonObject.put("valLists",valLists);
//        jsonObject.put("numLists",numLists);
//        jsonObject.put("leafNumList",leafNumList);
//        return jsonObject;
//    }
//
//    @RequestMapping(value = "/getDelByIndex",method = RequestMethod.POST)
//    public JSONObject getDelByIndex(int index){
//        return treeToJSONObject(bTreeService.getDelBtreeByIndex(index));
//    }
//}
