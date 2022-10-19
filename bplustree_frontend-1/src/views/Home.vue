<template>
  <div class="home">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">
        training data
      </el-menu-item>
      <el-menu-item index="2">Index placement</el-menu-item>
      <el-menu-item index="3">Comparison</el-menu-item>
    </el-menu>
    <div>
      <div v-if="activeIndex==='1'">
        <div class="train">
          <el-select v-model="selectedFileName" placeholder="请选择">
            <el-option
                v-for="item in fileNameList"
                :key="item"
                :label="item"
                :value="item">
            </el-option>
          </el-select>
          <el-button @click="startTraining" v-loading.fullscreen.lock="loadingShow">Start training</el-button>
        </div>
        <div v-if="resultImgShow" class="resultImg">
          <img :src="'data:image/png;base64,'+imgUrl" alt="" width="707" height="545">
        </div>

      </div>
      <div class="tap2" v-if="activeIndex==='2'">
        <div class="tap2_btn">
          <div>
            <el-select v-model="selectedServerIndex" placeholder="请选择">
              <el-option
                  v-for="(item,index) in serverNameList"
                  :key="index"
                  :label="item"
                  :value="index">
              </el-option>
            </el-select>
          </div>
          <div>
            <el-button type="primary" @click="selectedServerShow">Select server</el-button>
          </div>
          <div>
            <el-button type="success" @click="selectedServerTotalShow">Total servers</el-button>
          </div>
        </div>
        <div v-for="(item,index) in bPlusTrees" class="oneItem" >
          <div v-if="index>0&&(serverShowIndex===0||index===serverShowIndex)" class="oneItem_item">
            <div><span>server</span>{{index}}</div>
            <div><TreeChart :json="item" class="btree"/></div>
          </div>
        </div>
        <div class="tap2_table">
          <el-table
              class="elTable"
              :data="tableData"
              border
              height="300"
              style="width: 65%"
              @row-click="tableRowClick"
              :row-class-name="tableRowClassName"
              :cell-style="setCellStyle"
              :header-cell-style="setHeaderStyle"
          >
            <el-table-column
                prop="name"
                label="服务器"
                width="fit"
                align="center">
            </el-table-column>
            <el-table-column
                prop="leafTotalNum"
                label="叶子节点总数（N）"
                width="fit"
                align="center">
            </el-table-column>
            <el-table-column
                prop="leafNum"
                label="叶子节点树（n）"
                width="fit"
                align="center">
            </el-table-column>
            <el-table-column
                prop="serverTotalNum"
                label="server总数量（S）"
                width="fit"
                align="center">
            </el-table-column>
            <el-table-column
                prop="percentAvg"
                label="均衡占比（1/S）"
                width="fit"
                align="center">
            </el-table-column>
            <el-table-column
                prop="percentAvgCur"
                label="实际占比（n/N）"
                align="center"
            >
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div v-if="activeIndex==='3'">
        <div class="comparisonImg">
          <img :src="'data:image/png;base64,'+comparisonImgUrl">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import TreeChart from "../components/TreeChart";

export default {
  name: "Home",
  data(){
    return{
      //切换导航栏菜单标识符
      activeIndex: '1',
      //b+树数组
      bPlusTrees:[],
      //是否显示训练结果图片
      resultImgShow: false,
      //训练文件名数组——————已选择的训练文件
      fileNameList: ['file2','file3','file4','file5','file6'],
      serverNameList: ['servers','server1','server2','server3','server4','server5','server6','server7','server8'],
      selectedServerIndex: 1,
      serverShowIndex: 1,
      selectedFileName:'',
      imgUrl: null,
      comparisonImgUrl: null,
      loadingShow: false,
      tableData: [
        {
          name: 'server1',
          leafTotalNum: 50,
          leafNum: 12,
          serverTotalNum: 8,
          percentAvg: 0.125,
          percentAvgCur: 0.1254
        }, {
          name: 'server2',
          leafTotalNum: 50,
          leafNum: 12,
          serverTotalNum: 8,
          percentAvg: 0.125,
          percentAvgCur: 0.1254
        }, {
          name: 'server3',
          leafTotalNum: 50,
          leafNum: 12,
          serverTotalNum: 8,
          percentAvg: 0.125,
          percentAvgCur: 0.1254
        }, {
          name: 'server4',
          leafTotalNum: 50,
          leafNum: 12,
          serverTotalNum: 8,
          percentAvg: 0.125,
          percentAvgCur: 0.1254
        },{
          name: 'server5',
          leafTotalNum: 50,
          leafNum: 12,
          serverTotalNum: 8,
          percentAvg: 0.125,
          percentAvgCur: 0.1254
        },{
          name: 'server6',
          leafTotalNum: 50,
          leafNum: 12,
          serverTotalNum: 8,
          percentAvg: 0.125,
          percentAvgCur: 0.1254
        },{
          name: 'server7',
          leafTotalNum: 50,
          leafNum: 12,
          serverTotalNum: 8,
          percentAvg: 0.125,
          percentAvgCur: 0.1254
        },{
          name: 'server8',
          leafTotalNum: 50,
          leafNum: 12,
          serverTotalNum: 8,
          percentAvg: 0.125,
          percentAvgCur: 0.1254
        }],
    }
  },
  methods:{
    //设置表格样式
    setHeaderStyle(){
      return {color: '#222222', fontSize: '14px', fontWeight: 'normal', borderColor: '#ccc', borderWidth: '2px', borderLeftWidth: '0'}
    },
    setCellStyle(){
      return {borderColor: '#ccc', borderWidth: '2px', borderLeftWidth: '0'}
    },
    selectedServerTotalShow(){
      this.selectedServerIndex = 0;
      this.serverShowIndex = this.selectedServerIndex;
    },
    selectedServerShow(){
      this.serverShowIndex = this.selectedServerIndex;
    },
    //高亮显示
    tableRowClassName({row, rowIndex}) {
      row.index = rowIndex+1;
      if (row.index === this.serverShowIndex){
        return 'warningRow';
      }
    },
    tableRowClick(row, column, event){
      this.serverShowIndex = row.index;
      this.selectedServerIndex = row.index;
    },
    //开始训练
    async startTraining(){
      if (this.selectedFileName===""){
        this.$message('please choose training file');
      }else{
        this.loadingShow=true;
        //开始训练
        let result = await this.startTrainingAPI(this.selectedFileName);
        console.log("---------trainingResult-----------")
        console.log(result)
        console.log("---------trainingResult-----------")
        //判断显示图片的条件
        if (result!=null){
          setTimeout(()=>{
            this.resultImgShow=true;
            this.imgUrl = result;
            // this.imgUrl = require('C:/Desktop/123.jpg')
          },3000)
        }
        this.loadingShow = false;
      }
    },
    startTrainingAPI(selectedFileName){
      return new Promise((resolve,reject)=>{
        let formData = new FormData();
        formData.append("filename",selectedFileName)
        axios.post("http://localhost:50008/get_train_result",formData).then((resp)=>{
          resolve(resp.data)
        }).catch((error)=>{
          reject(error)
        })
      })
    },
    //all_query_file_text server_query_file_text
    getFileAPI(){
      return new Promise((resolve,reject)=>{
        axios.post("http://localhost:50008/get_indexFile").then((resp)=>{
          resolve(resp.data)
        }).catch((error)=>{
          reject(error)
        })
      })
    },
    //获取实验对比结果图片流
    getComparisonImgAPI(){
      return new Promise((resolve,reject)=>{
        axios.post("http://localhost:50008/get_comparison").then((resp)=>{
          resolve(resp.data)
        }).catch((error)=>{
          reject(error)
        })
      })
    },
    async getComparisonImg(){
      this.comparisonImgUrl = await this.getComparisonImgAPI();
    },

    //切换标签
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
      this.activeIndex = key;
      if (key==="1"){
        if (this.resultImgShow ===false){
          this.getFileNameList();
        }
      }else if (key==="2"){
        this.bPlusTrees=[],
            this.getBTreeAll();
      }else {
        this.getComparisonImg();
      }
    },
    //获取所有B+树结构
    getBTreeAllAPI(all_query_file_text,server_query_file_text){
      console.log(all_query_file_text)
      console.log("-----------server_query_file_text[0]-----------")
      console.log(server_query_file_text[0])
      console.log("-----------server_query_file_text[0]-----------")
      return new Promise((resolve,reject)=>{
        let formData = new FormData();
        formData.append("all_query_file_text",all_query_file_text);
        // formData.append("server_query_file_text",server_query_file_text);
        for(let i=0;i<server_query_file_text.length;i++){
          formData.append("server_query_file_text",server_query_file_text[i])
        }
        axios.post("http://localhost:8081/getAll",formData).then((resp)=>{
          resolve(resp.data)
        }).catch((error)=>{
          reject(error)
        })
      })
    },
    async getFileNameList(){
      let result = await this.getFileNameListAPI();
      console.log(result)
      if (result!=null){
        this.fileNameList = result;
      }
      // console.log(JSON.parse(JSON.stringify(this.bPlusTrees)))
    },
    //获取所有的文件名
    getFileNameListAPI(){
      return new Promise((resolve,reject)=>{
        axios.post("http://localhost:50008/workload_file_list").then((resp)=>{
          resolve(resp.data)
        }).catch((error)=>{
          reject(error)
        })
      })
    },
    async getBTreeAll(){
      let fileResult = await this.getFileAPI();
      console.log("------fileResult--------")
      console.log(fileResult)
      console.log("------------------------")
      console.log(fileResult[0][0])
      console.log(fileResult[1].length)
      let treeResult = await this.getBTreeAllAPI(fileResult[0][0],fileResult[1]);
      if (treeResult.numLists!=null){
        for (let i=0;i<treeResult.numLists.length;i++){
          if (i===0){
            // console.log(result.valLists[i])
            let leafTotalNum = treeResult.leafNumList[i];
            this.setLeafTotalNum(leafTotalNum)
          }else {
            this.tableData[i-1].leafNum = treeResult.leafNumList[i];
            this.tableData[i-1].percentAvgCur = parseFloat(this.tableData[i-1].leafNum/this.tableData[i-1].leafTotalNum).toFixed(3);
          }
          this.bPlusTrees.push(this.changeStr(treeResult.numLists[i],treeResult.valLists[i]))
        }
      }
      //console.log(JSON.parse(JSON.stringify(this.bPlusTrees)))
    },

    //修改tableData中的leafTotalNum
    setLeafTotalNum(leafTotalNum){
      for (let j=0;j<this.tableData.length;j++){
        this.tableData[j].leafTotalNum = leafTotalNum;
      }
    },

    //更改获取的b+树结构，使其能够在前端展示
    changeStr(numList,valList){
      let valIndex = 0;
      let numIndex = 0;
      let levelObj1  = []
      let levelObj2 = []
      let node = {}

      node.name = valList[valIndex];
      node.children=[]
      valIndex++;
      numIndex++;

      levelObj1.push(node)

      while (valIndex<valList.length){
        for (let curLevelObjIndex=0;curLevelObjIndex<levelObj1.length;curLevelObjIndex++){
          let currentNum = numList[numIndex];
          levelObj1[curLevelObjIndex].children=[];
          let curChildren = levelObj1[curLevelObjIndex].children;
          for (let singleIndex=0;singleIndex<currentNum;singleIndex++){
            let newNode = {}
            newNode.name = valList[valIndex]
            curChildren.push(newNode)
            levelObj2.push(curChildren[singleIndex])
            valIndex++
          }
          numIndex++;
        }
        levelObj1=levelObj2;
        levelObj2=[]
      }
      return node

    },
  },
  components: {
    TreeChart
  },
  created() {
    if (this.resultImgShow === false){
      this.getFileNameList();
    }
  }
}
</script>

<style lang="less">
@BorderColor: #ccc;

@treeMarginLeft: 10%;
@treePadTop: 30px;

@BorderWeight: 2px;
@treeWidth: 65%;
@selectWidth: 120px;

.home{
  padding: 0 10px;
  box-sizing: border-box;
  width: 100vw;
  overflow: hidden;
}
.tap2{
  margin-left: @treeMarginLeft;
  //按钮行
  .tap2_btn{
    margin-top: 20px;
    margin-bottom: 20px;
    display: flex;
    flex-direction: row;
    .el-select{
      margin-left: 40px;
      width: @selectWidth;
    }
    //div:nth-child(2),div:last-child{
    //
    //}
    div:nth-child(2){
      margin-left: 40px;
    }
    div:nth-child(3){
      margin-left: 40px;
    }
  }
  //表格行
  .tap2_table{
    width: 100%;
    margin-top: 20px;
    margin-bottom: 20px;
    /*background-color: #000;*/
    .elTable{
      border-top: @BorderWeight solid @BorderColor;
      border-left: @BorderWeight solid @BorderColor;
      border-bottom: @BorderWeight solid @BorderColor;

    }
  }
  //树行
  .oneItem_item{
    border: @BorderWeight solid @BorderColor;
    max-height: 250px;
    padding-top: @treePadTop;
    width: @treeWidth;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
}
.tap2 .elTable .warningRow {
  background-color: oldlace;
}

.tap2 .elTable .successRow {
  background-color: #000;
}
.train{
  margin-top: 40px;
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  div{
    margin-right: 40px;
  }
  .resultImg{
    margin-top: 20px;
  }
  .el-select {
    width: 250px;
  }
}
.comparisonImg{
  margin-left: @treeMarginLeft;
  margin-top: 60px;
  width: 700px;
  img{
    width: 100%;
    height: auto;
  }
}
</style>

