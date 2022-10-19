package com.example.bplustreebackend.entity;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

@Service
public class BPlusTree<T, V extends Comparable<V>> implements Serializable {
    /**
     * 基本属性包括：
     * B+树的阶数bTreeOrder,
     * B+树中每个节点容纳的最大数据量maxLength=bTreeOrder+1,
     * 根节点root,
     * 最左叶子节点left
     */

    private Integer bTreeOrder;

    private Integer maxLength;

    public Node<T, V> root;

    public LeafNode<T,V> left;

    public BPlusTree(){
        this(3);
    }
    public BPlusTree(Integer bTreeOrder){
        this.bTreeOrder = bTreeOrder;
        this.maxLength = bTreeOrder+1;
        this.root = new LeafNode<T,V>();
        this.left = null;
    }
    //查找
    public T find(V key){
        T t = root.find(key);
        if (t==null)
            System.out.println(key+"----该值不存在");
        return t;
    }
    //插入
    public void insert(T t,V key){
        if (key==null)
            return;
        if (find(key)!=null)
            return;
        System.out.println("可以插入该值");
        Node<T,V> node = root.insert(t,key);
        if (node!=null)
            root=node;
        this.left = root.refreshLeft();
    }

    public void showAllLeaf(){
        left.showAllLeaf();
    }

    public void showBPlusTree(){
        Queue<Node> qNodes = new LinkedList<Node>();
        Integer tempLevelNodeNum = 1;
        Integer levelNodeNum = 0;
        qNodes.add(root);
        while (qNodes.size()>0){
            levelNodeNum = tempLevelNodeNum;
            tempLevelNodeNum = 0;
            for (int i=0;i<levelNodeNum;i++){
                Node<T, V> node = qNodes.remove();
                if (node!=null){
                    tempLevelNodeNum = node.showBPlusTree(qNodes,tempLevelNodeNum);
                    System.out.print("     ");
                }

            }
            System.out.println();
        }
    }

    public void delete(V key){
        if (key==null)
            return;
        Node<T,V> node = root.delete(key);
        if (node!=null)
            root = node;
        this.left = root.refreshLeft();

    }

    abstract public class Node<T, V extends Comparable<V>> implements Serializable {
        /**
         * 基本属性包括：
         * 指针集合keys
         * 父节点parent
         * 子节点集合child
         * 已经存入的数据量number
         */
        protected Object[] keys;
        protected Node<T, V> parent;
        public Node<T, V>[] child;
        public Integer number;
        public Node(){
            this.keys = new Object[maxLength];
            this.child = new Node[maxLength];
            this.number = 0;
            this.parent = null;
        }
        /**
         * 基本方法包括：
         * 查找find
         * 插入insert
         * 更新最左叶子节点refreshLeft
         */
        abstract public T find(V key);
        abstract public Node<T, V> insert(T value, V key);
        abstract public Node<T, V> delete(V key);
        abstract public LeafNode<T, V> refreshLeft();
        public Integer showBPlusTree(Queue<Node> qNodes,Integer levelNodeNum){
            if (this==null||this.number<=0){
                return levelNodeNum;
            }else{
                for (int i =0 ;i<number;i++){
                    System.out.print(keys[i]+" ");
                }
                for (int j=0;j<number;j++){
                    qNodes.add(this.child[j]);
                    levelNodeNum += 1;
                }
                return levelNodeNum;
            }
        };

        public String showKeys(){
            String str = " ";
            for (int i=0;i<number;i++){
                str = str + (keys[i]);
                if (i<number-1){
                    str=str+",";
                }
            }
            str = str+" ";
//            System.out.println(str);
            return str;
        }

    }
    public class BPlusNode<T, V extends Comparable<V>> extends Node<T, V>{
        public BPlusNode(){
            super();
        }

        /**
         * 非叶子节点的find函数：
         *     迭代寻找key在哪一个child中
         */
        @Override
        public T find(V key) {
            if (key==null||number<=0)
                return null;
            int i = 0;
            for (;i<number;i++){
                if (key.compareTo((V) keys[i])<=0)
                    break;
            }
            if (i==number)
                return null;
            return child[i].find(key);
        }

        /**
         * 非叶子节点的insert函数目的是
         *      寻找key键应插入的叶子节点位置
         */
        @Override
        public Node<T, V> insert(T value, V key) {
            int i = 0;
            for (;i<number;i++){
                if (key.compareTo((V) keys[i])<=0)
                    break;
            }
            if (i==number)
                i -= 1;
            return child[i].insert(value,key);
        }

        /**
         * 非叶子节点中递归插入节点
         *      首先应该判断旧指针的位置
         *      插入并判断是否需要递归
         */
        public Node<T, V> insertNode(Node<T, V> leftNode, Node<T, V> rightNode, V key){
            V oldKey = null;
            if (number>0){
                oldKey = (V)keys[number-1];
            }
            //当key为null或者number==0时，表示可以直接向其中插入该两个节点
            if (key==null||this.number<=0){
                keys[0] = leftNode.keys[leftNode.number-1];
                keys[1] = rightNode.keys[rightNode.number-1];
                child[0] = leftNode;
                child[1] = rightNode;
                number += 2;
                return this;
            }

            //旧指针的位置
            int i = 0;
            for (;i<number;i++){
                if (key.compareTo((V) keys[i])==0)
                    break;
            }
            Object[] tempKeys = new Object[maxLength];
            Object[] tempChild = new Node[maxLength];

            System.arraycopy(keys,0,tempKeys,0,i+1);
            System.arraycopy(child,0,tempChild,0,i+1);
            System.arraycopy(keys,i+1,tempKeys,i+2,number-i-1);
            System.arraycopy(child,i+1,tempChild,i+2,number-i-1);
            tempKeys[i] = leftNode.keys[leftNode.number-1];
            tempKeys[i+1] = rightNode.keys[rightNode.number-1];
            tempChild[i] = leftNode;
            tempChild[i+1] = rightNode;
            number++;

            //判断非叶子节点中元素数量是否超过B+树阶数
            //未超过
            if(number<=bTreeOrder){
                System.arraycopy(tempKeys,0,keys,0,number);
                System.arraycopy(tempChild,0,child,0,number);
                if (i+1==number-1&&this.parent!=null){
                    ((BPlusNode<T, V>)this.parent).updateKey(oldKey,(V)this.keys[number-1]);
                }
                return null;
            }
            //超过
            int leftNumber = number/2;
            int rightNumber = number-leftNumber;
            BPlusNode<T, V> tempRightNode = new BPlusNode<T, V>();
            //number赋值
            tempRightNode.number = rightNumber;
            this.number = leftNumber;
            //parent赋值
            tempRightNode.parent = this.parent;
            if (this.parent==null){
                BPlusNode<T, V> bPlusParentNode = new BPlusNode<T, V>();
                this.parent = bPlusParentNode;
                tempRightNode.parent = bPlusParentNode;
                oldKey = null;
            }
            //keys和child赋值
            this.keys = new Object[maxLength];
            this.child = new Node[maxLength];
            System.arraycopy(tempKeys,0,keys,0,leftNumber);
            System.arraycopy(tempChild,0,child,0,leftNumber);
            System.arraycopy(tempKeys,leftNumber,tempRightNode.keys,0,rightNumber);
            System.arraycopy(tempChild,leftNumber,tempRightNode.child,0,rightNumber);

            for (int k = 0;k<rightNumber;k++){
                tempRightNode.child[k].parent = tempRightNode;
            }

            return ((BPlusNode<T, V>)this.parent).insertNode(this,tempRightNode,oldKey);
        }

        /**
         * 非叶子节点中递归删除数据
         *      首先应该判断指针的位置
         */
        public Node<T, V> delete(V key){
            int i = 0;
            for (;i<number;i++){
                if (key.compareTo((V) keys[i])<=0)
                    break;
            }
            if (i==number)
                i -= 1;
            return child[i].delete(key);
        }

        /**
         * 判断在指定key数据时，其所在节点的兄弟节点能否提供对应的叶子节点
         */
        public ValueNode<T, V> getBroNode(V oldKey){
            if (oldKey==null)
                return null;
            int i = 0;
            for (;i<number;i++){
                if (oldKey.compareTo((V) keys[i])==0){
                    break;
                }
            }
            if (i==number)
                return null;
            //首先判断左节点是否有
            if (i>0){
//                System.out.println("左节点删除index值为"+(child[i-1].number-1));
                if ((child[i-1].number-1)>=(bTreeOrder/2)){
                    //处理
                    //1将其从左节点中删除2返回
                    int index = child[i-1].number-1;
//                    System.out.println("左节点删除index值为"+index);
                    ValueNode<T, V> targetValueNode = ((LeafNode<T, V>)child[i-1]).deleteValueNode(index);
                    return targetValueNode;
                }
            }
            //再判断其右节点是否有
            if (i<number-1){
//                System.out.println("右节点删除index值为"+(child[i+1].number-1));
                if ((child[i+1].number-1)>=(bTreeOrder/2)){
                    //处理
                    int index = 0;
//                    System.out.println("右节点删除index值为"+index);
                    ValueNode<T, V> targetValueNode = ((LeafNode<T, V>)child[i+1]).deleteValueNode(index);
                    return targetValueNode;
                }
            }
            return null;
        }

        public Node<T,V> deleteByIndex(int index){
            //默认this.child[i]的类型为BPlusNode
            if (index>this.number-1)
                return null;
            V oldKey = null;
            if (index == number-1)
                oldKey = (V)keys[number-1];
//            System.out.println("deleteByIndex index:"+index);
            Object[] tempKeys = new Object[maxLength];
            Node[] tempChild = new Node[maxLength];
            System.arraycopy(keys,0,tempKeys,0,index);
            System.arraycopy(child,0,tempChild,0,index);
            System.arraycopy(keys,index+1,tempKeys,index,number-index-1);
            System.arraycopy(child,index+1,tempChild,index,number-index-1);
            Node<T, V> node = child[index];
            System.arraycopy(tempKeys,0,keys,0,number-1);
            System.arraycopy(tempChild,0,child,0,number-1);
            number--;
            if (oldKey!=null&&parent!=null){
                V newKey = (V)keys[number-1];
                ((BPlusNode)parent).updateKey(oldKey,newKey);
            }
            return node;
        }
        public Node<T, V> deleteChangeStr(Node<T, V> tempNode){
//            System.out.println(tempNode.keys[0]);
//            System.out.println(tempNode.parent.keys[0]);
            while (tempNode.parent!=null&&tempNode.parent.number>=bTreeOrder/2&&tempNode.number<bTreeOrder/2){
                V tempKey = (V)tempNode.keys[tempNode.number-1];
                int i = 0;
                for (;i< tempNode.parent.number;i++){
                    if (tempKey.compareTo((V) tempNode.parent.keys[i])==0){
                        break;
                    }
                }
//                System.out.println("deleteChangeStr i:"+i);
                //判断是合并类型还是借类型
                int num = 0;
                if (i>0&&tempNode.parent.child[i-1].number-1>=bTreeOrder/2){
                    num += tempNode.parent.child[i-1].number-bTreeOrder/2;
                }
                if (i<tempNode.parent.number-1&&tempNode.parent.child[i+1].number-1>=bTreeOrder/2){
                    num += tempNode.parent.child[i+1].number-bTreeOrder/2;
                }
                if (num+tempNode.number-bTreeOrder/2>=0){
                    while (i>0&&(tempNode.parent.child[i-1].number-1>=bTreeOrder/2)&&tempNode.number<bTreeOrder/2){
                        //条件：有左节点，且左节点能给，且自身结构未正常
//                        System.out.println("向左节点要");
                        int index = tempNode.parent.child[i-1].number-1;
                        Node<T, V> currentChild = ((BPlusNode<T, V>)tempNode.parent.child[i-1]).deleteByIndex(index);
                        //需要修改该节点的父亲节点指针
                        currentChild.parent = tempNode;

                        Object[] tempKeys = new Object[maxLength];
                        Node[] tempChild = new Node[maxLength];
                        tempKeys[0]  = currentChild.keys[currentChild.number-1];
                        tempChild[0] = currentChild;

                        System.arraycopy(tempNode.keys,0,tempKeys,1,tempNode.number);
                        System.arraycopy(tempNode.child,0,tempChild,1,tempNode.number);

                        System.arraycopy(tempKeys,0,tempNode.keys,0,tempNode.number+1);
                        System.arraycopy(tempChild,0,tempNode.child,0,tempNode.number+1);

                        tempNode.number++;

                    }
                    while (i<tempNode.parent.number-1&&tempNode.parent.child[i+1].number-1>=bTreeOrder/2&&tempNode.number<bTreeOrder/2){
                        //右节点借
//                        System.out.println("向右节点要");
                        int index = 0;
                        V oldKey = (V)tempNode.keys[number-1];
                        Node<T, V> currentChild = ((BPlusNode<T, V>)tempNode.parent.child[i+1]).deleteByIndex(index);
                        //需要修改该节点的父亲节点指针
                        currentChild.parent = tempNode;

                        tempNode.keys[tempNode.number]  = currentChild.keys[currentChild.number-1];
                        tempNode.child[tempNode.number] = currentChild;
                        tempNode.number++;
                        V newKey = (V)tempNode.keys[tempNode.number-1];
                        if (tempNode.parent!=null){
                            ((BPlusNode<T, V>)tempNode.parent).updateKey(oldKey,newKey);
                        }
                    }
                }
                else{
                    //合并
                    if (i>0){
                        //左合并
//                        System.out.println("左合并");
                        for (int j=0;j< tempNode.number;j++){
                            //依次向左节点添加的tempNode中的child和key

                            tempNode.parent.child[i-1].keys[tempNode.parent.child[i-1].number]=tempNode.keys[j];
                            if (tempNode.parent.child[i-1].child!=null){
                                //tempNode是非叶子节点
                                tempNode.parent.child[i-1].child[tempNode.parent.child[i-1].number]=tempNode.child[j];
                                tempNode.parent.child[i-1].child[tempNode.parent.child[i-1].number].parent = tempNode.parent.child[i-1];
                            }else{
//                            tempNode是叶子节点
                                ((LeafNode)tempNode.parent.child[i-1]).values[tempNode.parent.child[i-1].number]=((LeafNode)tempNode).values[j];
                            }


                            tempNode.parent.child[i-1].number++;
                            if (j==tempNode.number-1){
                                Object[] tempKeys = new Object[maxLength];
                                Node[] tempChild = new Node[maxLength];
                                //删除tempNode的parent节点keys中tempNode对应的key
                                System.arraycopy(tempNode.parent.keys,0,tempKeys,0,i);
                                System.arraycopy(tempNode.parent.keys,i+1,tempKeys,i,tempNode.parent.number-i-1);
                                tempKeys[i-1] = tempNode.keys[tempNode.number-1];
                                tempNode.parent.keys=new Object[maxLength];
                                System.arraycopy(tempKeys,0,tempNode.parent.keys,0,tempNode.parent.number-1);
                                //删除tempNode的parent节点child中tempNode
                                System.arraycopy(tempNode.parent.child,0,tempChild,0,i);
                                System.arraycopy(tempNode.parent.child,i+1,tempChild,i,tempNode.parent.number-i-1);
                                tempNode.parent.child=new Node[maxLength];
                                System.arraycopy(tempChild,0,tempNode.parent.child,0,tempNode.parent.number-1);

                                tempNode.parent.number--;
                            }
                        }
                    }else if (i==0&&tempNode.parent!=null&&tempNode.parent.number>1){
                        //右合并
//                        System.out.println("右合并");

                        for (int j=tempNode.number-1;j>=0;j--){
                            //依次向右节点添加的tempNode中的child和key
                            Object[] tempKeys1 = new Object[maxLength];
                            Node[] tempChild1 = new Node[maxLength];
                            Object[] tempValue1 = new Object[maxLength];
                            tempKeys1[0] = tempNode.keys[j];
                            System.arraycopy(tempNode.parent.child[i+1].keys,0,tempKeys1,1,tempNode.parent.child[i+1].number);
                            System.arraycopy(tempKeys1,0,tempNode.parent.child[i+1].keys,0,tempNode.parent.child[i+1].number+1);
                            tempChild1[0] = tempNode.child[j];

                            if (tempNode.parent.child[i+1].child!=null){
                                //如果tempNode是非叶子节点
                                System.arraycopy(tempNode.parent.child[i+1].child,0,tempChild1,1,tempNode.parent.child[i+1].number);
                                System.arraycopy(tempChild1,0,tempNode.parent.child[i+1].child,0,tempNode.parent.child[i+1].number+1);
                                tempNode.parent.child[i+1].child[0].parent = tempNode.parent.child[i+1];
                            }else {
                                //如果tempNode是叶子节点
                                tempValue1[0] = ((LeafNode)tempNode).values[j];
                                System.arraycopy(((LeafNode)tempNode.parent.child[i+1]).values,0,tempValue1,1,tempNode.parent.child[i+1].number);
                                System.arraycopy(tempValue1,0,((LeafNode)tempNode.parent.child[i+1]).values,0,tempNode.parent.child[i+1].number+1);
                            }
                            tempNode.parent.child[i+1].number++;
                            if (j==tempNode.number-1){
                                Object[] tempKeys2 = new Object[maxLength];
                                Node[] tempChild2 = new Node[maxLength];
                                //删除tempNode的parent节点keys中tempNode对应的key
                                System.arraycopy(tempNode.parent.keys,0,tempKeys2,0,i);
                                System.arraycopy(tempNode.parent.keys,i+1,tempKeys2,i,tempNode.parent.number-i-1);
                                tempNode.parent.keys=new Object[maxLength];
                                System.arraycopy(tempKeys2,0,tempNode.parent.keys,0,tempNode.parent.number-1);
                                //删除tempNode的parent节点child中tempNode
                                System.arraycopy(tempNode.parent.child,0,tempChild2,0,i);
                                System.arraycopy(tempNode.parent.child,i+1,tempChild2,i,tempNode.parent.number-i-1);
                                tempNode.parent.child=new Node[maxLength];
                                System.arraycopy(tempChild2,0,tempNode.parent.child,0,tempNode.parent.number-1);
                                tempNode.parent.number--;
                            }
                        }
                    }else {

                    }
                }


                tempNode = (BPlusNode<T, V>)tempNode.parent;
            }
//            if (tempNode.parent==null&&tempNode.number<bTreeOrder/2){
//                return tempNode.child[0];
//            }

            if (tempNode.number<bTreeOrder/2){
//                System.out.println(tempNode.number);
//                System.out.println(tempNode.child[0].child[0].number);
                tempNode.child[0].parent = null;
                return tempNode.child[0];
            }
            else {
                return null;
            }
        }

        public Node<T,V> deleteLeafNode(LeafNode<T, V> currentLeafNode, V oldKey){
            //首先删除对应oldKey
            int i = 0;
            for (;i<number;i++){
                if (oldKey.compareTo((V) keys[i])==0){
                    break;
                }
            }
//            System.out.println("deleteLeafNode i: "+i);
            if (i<number){
                //删除oldKey
//                System.out.println("deleteLeafNode oldKey:"+oldKey);
                Object[] tempKeys = new Object[maxLength];
                Node[] tempChild = new Node[maxLength];
                System.arraycopy(keys,0,tempKeys,0,i);
                System.arraycopy(keys,i+1,tempKeys,i,number-i-1);
                System.arraycopy(child,0,tempChild,0,i);
                System.arraycopy(child,i+1,tempChild,i,number-i-1);
                keys = new Object[maxLength];
                child = new Node[maxLength];
                System.arraycopy(tempKeys,0,keys,0,number-1);
                System.arraycopy(tempChild,0,child,0,number-1);
                //更新
                if (i==number-1&&this.parent!=null){
                    number--;
                    ((BPlusNode<T, V>)this.parent).updateKey(oldKey,(V)keys[number-1]);
                }else {
                    number--;
                }
                //依次插入删除叶子节点的元素
                if (i>0){
                    for(int j=0;j< currentLeafNode.number;j++){
                        ((LeafNode<T,V>)child[i-1]).values[child[i-1].number] = (T)currentLeafNode.values[j];
                        child[i-1].keys[child[i-1].number]   = (V)currentLeafNode.keys[j];
                        child[i-1].number++;
                    }
                    oldKey = (V) keys[i-1];
                    keys[i-1]=child[i-1].keys[child[i-1].number-1];
                    if (i-1==number-1&&parent!=null){
                        ((BPlusNode<T, V>)this.parent).updateKey(oldKey,(V)keys[number-1]);
                    }
                }else{

                    for(int j=currentLeafNode.number-1;j>=0;j--){
                        tempKeys = new Object[maxLength];
                        Object[] tempValues = new Object[maxLength];
                        tempKeys[0] = (V)currentLeafNode.keys[j];
                        tempValues[0] = (T)currentLeafNode.values[j];
                        //当删除oldKey后，原来i+1位置的元素变成了现在的i位置;
                        System.arraycopy(child[i].keys,0,tempKeys,1,child[i].number);
                        System.arraycopy(((LeafNode<T,V>)child[i]).values,0,tempValues,1,child[i].number);


                        System.arraycopy(tempKeys,0,child[i].keys,0,child[i].number+1);
                        System.arraycopy(tempValues,0,((LeafNode<T,V>)child[i]).values,0,child[i].number+1);
                        child[i].number++;
                    }
                }
                //判断父亲节点是否需要调整结构
                return deleteChangeStr(this);
            }
            else {
                return null;
            }
        }
        @Override
        public LeafNode<T, V> refreshLeft() {
            if (number>0)
                return child[0].refreshLeft();
            return null;
        }

        public void updateKey(V oldKey,V newKey){
            //查找key位置
//            System.out.println("updateKey oldKey: "+oldKey+" newKey:"+newKey);
            int i=0;
//            System.out.println("updateKey number:"+number);
//            for (int j=0;j<number;j++){
//                System.out.println("updateKey key: "+keys[j]);
//            }
            for (;i<number;i++){
//                System.out.println("updateKey"+keys[i]);
                if (oldKey.compareTo((V) keys[i])==0){
//                    System.out.println("updateKey i: "+i);
                    keys[i] = newKey;
                    break;
                }
            }
            //oldKey修改后
            if (i==number-1&&this.parent!=null){
//                System.out.println("需要更新");
                ((BPlusNode<T, V>)this.parent).updateKey(oldKey,(V)keys[number-1]);
            }
        }
    }
    public class LeafNode<T, V extends Comparable<V>> extends Node<T, V>{
        /**
         * 基本属性包括：
         * 继承父类
         * 左节点left
         * 右节点right
         * 存储的值集合values
         */
        public LeafNode<T, V> left;
        public LeafNode<T, V> right;
        public Object[] values;
        public LeafNode(){
            super();
            this.left = null;
            this.right = null;
            this.values = new Object[maxLength];
        }

        @Override
        public T find(V key) {

            //快排算法
            if(key==null||number<=0)
                return null;
            int leftIndex=0;
            int rightIndex=number-1;
            int midIndex = (leftIndex+rightIndex)/2;
            while (leftIndex<=rightIndex){
                if (key.compareTo((V) keys[midIndex])==0){
                    return (T)values[midIndex];
                }
                else if (key.compareTo((V) keys[midIndex])>0){
                    leftIndex = midIndex+1;

                }else {
                    rightIndex = midIndex-1;
                }
                midIndex = (leftIndex+rightIndex)/2;
            }
            return null;
        }

        /**
         * 叶子节点的insert函数
         *      首先应该寻找合适位置将值插入进去
         *      其次应该判断是否超过B+树阶数
         *          超过则需要将叶子节点分裂，并交给父节点递归
         */
        @Override
        public Node<T, V> insert(T value, V key) {
            //保存原键
            V oldKey = null;
            if (this.number>0)
                oldKey = (V)keys[number-1];
            //寻找插入的位置i
            int i = 0;
            for (;i<number;i++){
                if (key.compareTo((V) keys[i])<=0)
                    break;
            }
            //获取新的tempKeys和tempValues
            Object[] tempKeys = new Object[maxLength];
            Object[] tempValues = new Object[maxLength];
            System.arraycopy(keys,0,tempKeys,0,i);
            System.arraycopy(values,0,tempValues,0,i);
            System.arraycopy(keys,i,tempKeys,i+1,number-i);
            System.arraycopy(values,i,tempValues,i+1,number-i);
            tempKeys[i]=key;
            tempValues[i]=value;
            number++;
            //判断叶子节点是否需要分裂
            //不需要分裂
            if (number<=bTreeOrder){
                System.arraycopy(tempKeys,0,keys,0,number);
                System.arraycopy(tempValues,0,values,0,number);
                //验证插入后所有节点里key的值
                Node node = this;
                while(node.parent!=null){
                    V tempKey = (V)node.keys[node.number-1];
                    if (tempKey.compareTo((V) node.parent.keys[node.parent.number-1])>0){
                        node.parent.keys[node.parent.number-1]=tempKey;
                        node = node.parent;
                    }else{
                        break;
                    }
                }
                return null;
            }
            //如果需要分裂
            //赋值number,parent,keys,values,left,right

            LeafNode<T, V> rightLeafNode = new LeafNode<T, V>();
            int leftNumber = number/2;
            int rightNumber = number-leftNumber;
            //赋值number
            rightLeafNode.number = rightNumber;
            this.number=leftNumber;
            //赋值parent,如果parent为空则需要再声明
            rightLeafNode.parent = this.parent;
            if (this.parent==null){
                BPlusNode<T, V> parentNode = new BPlusNode<T, V>();
                this.parent = parentNode;
                rightLeafNode.parent = parentNode;
                oldKey=null;
            }
            //赋值keys,values
            this.keys=new Object[maxLength];
            this.values=new Object[maxLength];
            System.arraycopy(tempKeys,0,this.keys,0,leftNumber);
            System.arraycopy(tempKeys,leftNumber,rightLeafNode.keys,0,rightNumber);
            System.arraycopy(tempValues,0,this.values,0,leftNumber);
            System.arraycopy(tempValues,leftNumber,rightLeafNode.values,0,rightNumber);

            //赋值right, left
            if (this.right!=null){
                this.right.left = rightLeafNode;
                rightLeafNode.right = this.right;
            }
            this.right = rightLeafNode;
            rightLeafNode.left = this;

            return ((BPlusNode<T, V>)this.parent).insertNode(this,rightLeafNode,oldKey);
        }

        @Override
        public LeafNode<T, V> refreshLeft() {
            if(number>0)
                return this;
            return null;
        }

        /**
         * 叶子节点的delete函数
         *      首先应该寻找数据位置
         */
        public Node<T, V> delete(V key){
            V oldKey = null;
            //如果叶子节点为空，则直接返回null
            if (number<=0){
//                System.out.println("节点为空");
                return null;
            }
            //查找数据位置
            int i=0;
            for (;i<number;i++){
//                System.out.println(keys[i]);
                if (key.compareTo((V) keys[i])==0)
                    break;
            }
            //如果没有找到则直接返回null
            if (i==number){
//                System.out.println("该值不存在，无法删除");
                return null;
            }
            oldKey = (V) keys[number-1];

            //在新结构中将原数据删除
            Object[] tempKeys = new Object[maxLength];
            Object[] tempValues = new Object[maxLength];
            System.arraycopy(keys,0,tempKeys,0,i);
            System.arraycopy(values,0,tempValues,0,i);
            System.arraycopy(keys,i+1,tempKeys,i,number-i-1);
            System.arraycopy(values,i+1,tempValues,i,number-i-1);

            //判断删除后是否会影响结构，即判断（number-1）>=bTreeOrder/2或者本来就是根节点
            //如果成立则表明B+树结构不用改变，只需判断Key是否需要更新
            if (number-1>=bTreeOrder/2||parent==null){

                System.out.println("直接删除型");

                this.keys = new Object[maxLength];
                this.values = new Object[maxLength];
                System.arraycopy(tempKeys,0,keys,0,number-1);
                System.arraycopy(tempValues,0,values,0,number-1);

                number--;

                //需要更新
                if (i==number&&this.parent!=null){
                    System.out.println("delete 需要更新");
                    System.out.println("delete "+oldKey+" "+(V)keys[number-1]);
                    ((BPlusNode<T, V>)this.parent).updateKey(oldKey,(V)keys[number-1]);
                }

                return null;
            }
            //如果需要更新B+树结构
            //首先父节点判断是否能够提供兄弟节点的数据
            ValueNode<T, V> valueNode = ((BPlusNode<T, V>)this.parent).getBroNode(oldKey);
            //能提供兄弟节点的数据
            if (valueNode!=null){

                System.out.println("借用兄弟型");
                //删除
                this.keys = new Object[maxLength];
                this.values = new Object[maxLength];
                System.arraycopy(tempKeys,0,keys,0,number-1);
                System.arraycopy(tempValues,0,values,0,number-1);
                number--;

                //更新
                if (i==number&&this.parent!=null){
                    ((BPlusNode<T, V>)this.parent).updateKey(oldKey,(V)keys[number-1]);
                }

                oldKey = (V)keys[number-1];

                //判断插入位置
                int j = 0;
                for (;j<number;j++){
                    if (valueNode.getKey().compareTo((V) keys[j])<0){
                        break;
                    }
                }
                //更新values和keys
                tempKeys = new Object[maxLength];
                tempValues = new Object[maxLength];
                System.arraycopy(keys,0,tempKeys,0,j);
                System.arraycopy(values,0,tempValues,0,j);
                System.arraycopy(keys,j,tempKeys,j+1,number-j);
                System.arraycopy(values,j,tempValues,j+1,number-j);

                tempKeys[j] = valueNode.getKey();
                tempValues[j] = valueNode.getValue();

                System.arraycopy(tempKeys,0,keys,0,number+1);
                System.arraycopy(tempValues,0,values,0,number+1);
                number++;

                V newKey = (V)keys[number-1];

//                System.out.println("oldKey:"+oldKey+" newKey:"+newKey);

                //当新key和oldKey大小不同时，需要更新父节点的数值
                if (j==number-1&&this.parent!=null){
                    ((BPlusNode<T, V>)this.parent).updateKey(oldKey,newKey);
                }
                return null;
            }
            else {
                //当不能提供兄弟节点的数据时，需要考虑合并，并且得考虑合并后父节点的平衡问题
                //首先需要获取删除后的叶子节点

                System.out.println("合并型");


                this.keys = new Object[maxLength];
                this.values = new Object[maxLength];
                System.arraycopy(tempKeys,0,keys,0,number-1);
                number--;

                //调整指针
                if (this.left!=null)
                    this.left.right = this.right;
                if (this.right!=null)
                    this.right.left = this.left;

                //将父节点
                return ((BPlusNode<T, V>) this.parent).deleteLeafNode(this,oldKey);
            }

        }

        public ValueNode<T, V> deleteValueNode(int index){
            Object[] tempKeys = new Object[maxLength];
            Object[] tempValues = new Object[maxLength];
            System.arraycopy(keys,0,tempKeys,0,index);
            System.arraycopy(keys,index+1,tempKeys,index,number-index-1);
            System.arraycopy(values,0,tempValues,0,index);
            System.arraycopy(values,index+1,tempValues,index,number-index-1);

            T value = (T)values[index];
            V key = (V)keys[index];
            ValueNode<T, V> node = new ValueNode<T, V>(value, key);

            keys = new Object[maxLength];
            values = new Object[maxLength];
            System.arraycopy(tempKeys,0,keys,0,number-1);
            System.arraycopy(tempValues,0,values,0,number-1);

            number--;

            if (this.parent!=null&&index==number){
                ((BPlusNode<T, V>)this.parent).updateKey(key, (V)keys[number-1]);
            }
//            System.out.println("T:"+node.getValue()+" V:"+node.getKey());
            return node;
        }


        public void showAllLeaf(){
            if (this==null||number==0){
                return;
            }
            else {
                for (int i=0;i<number;i++){
                    System.out.print(keys[i]+" ");
                }
                System.out.println();
                if (right!=null){
                    right.showAllLeaf();
                }
            }

        }
    }
    class ValueNode<T, V extends Comparable<V>> implements Serializable {
        T value;
        V key;
        ValueNode(){
            value = null;
            key = null;
        }
        ValueNode(T value,V key){
            setT(value);
            setKey(key);
        }
        void setT(T value){
            this.value = value;
        }
        void setKey(V key){
            this.key = key;
        }
        T getValue(){
            return value;
        }
        V getKey(){
            return key;
        }
    }
}
