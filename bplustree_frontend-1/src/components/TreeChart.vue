<template>
  <table v-if="treeData.name">
    <tr>
      <td :colspan="Array.isArray(treeData.children) ? treeData.children.length * 2 : 1"
          :class="{parentLevel: Array.isArray(treeData.children) && treeData.children.length, extend: Array.isArray(treeData.children) && treeData.children.length && treeData.extend}"
      >
        <div :class="{node: true, hasMate: treeData.mate}">
          <div class="person"
               :class="Array.isArray(treeData.class) ? treeData.class : []">
            <div class="name">{{treeData.name}}   </div>
          </div>
        </div>
        <div class="extend_handle" v-if="Array.isArray(treeData.children) && treeData.children.length" @click="toggleExtend(treeData)"></div>
      </td>
    </tr>
    <tr v-if="Array.isArray(treeData.children) && treeData.children.length && treeData.extend">
      <td v-for="(children, index) in treeData.children" :key="index" colspan="2" class="childLevel">
        <TreeChart :json="children" @click-node="$emit('click-node', $event)"/>
      </td>
    </tr>
  </table>



</template>

<script>
export default {
  name: "TreeChart",
  props: ["json"],
  data() {
    return {
      treeData: {}
    }
  },
  watch: {
    json: {
      handler: function(Props){
        let extendKey = function(jsonData){
          jsonData.extend = (jsonData.extend===void 0 ? true: !!jsonData.extend);
          if(Array.isArray(jsonData.children)){
            jsonData.children.forEach(c => {
              extendKey(c)
            })
          }
          return jsonData;
        }
        if(Props){
          this.treeData = extendKey(Props);
        }
      },
      immediate: true
    }
  },
  methods: {
    toggleExtend: function(treeData){
      treeData.extend = !treeData.extend;
      this.$forceUpdate();
    }
  }
}
</script>

<style scoped>
table{border-spacing: 0!important;}
td{position: relative;padding-bottom: 30px;text-align: center;}
.extend_handle{position: absolute;left:50%;bottom:15px; width:8px;height: 8px;padding:10px;transform: translate3d(-15px,0,0);cursor: pointer;}
.extend_handle:before{content:""; display: block; width:100%;height: 100%;box-sizing: border-box; border:2px solid;border-color:#ccc #ccc transparent transparent;
  transform: rotateZ(135deg);transform-origin: 50% 50% 0;transition: transform ease 300ms;}
.extend_handle:hover:before{border-color:#333 #333 transparent transparent;}
.extend::after{content: "";position: absolute;left:50%;bottom:18px;height:10px;border-left:2px solid #ccc;transform: translate3d(-2px,5px,0)}
.childLevel::before{content: "";position: absolute;left:50%;bottom:100%;height:14px;border-left:2px solid #ccc;transform: translate3d(-1px,0,0)}
.childLevel::after{content: "";position: absolute;left:0;right:0;top:-15px;border-top:2px solid #ccc;}
.childLevel:first-child:before, .childLevel:last-child:before{display: none;}
.childLevel:first-child:after{left:50%;height:10px; border:2px solid;border-color:#ccc transparent transparent #ccc;border-radius: 6px 0 0 0;transform: translate3d(1px,0,0)}
.childLevel:last-child:after{right:50%;height:10px; border:2px solid;border-color:#ccc #ccc transparent transparent;border-radius: 0 6px 0 0;transform: translate3d(-1px,0,0)}
.childLevel:first-child.childLevel:last-child::after{left:auto;border-radius: 0;border-color:transparent #ccc transparent transparent;transform: translate3d(1px,0,0)}
.node{position: relative; display: inline-block; text-align: center;}
.node:hover{color: #2d8cf0;cursor: pointer;}
.node .person{position: relative; display: inline-block;padding-right:6px;padding-left:6px;}
.node .person .name{width: fit-content;font-size: 10px;}

</style>
