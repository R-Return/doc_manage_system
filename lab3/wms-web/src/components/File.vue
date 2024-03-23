<script>
import axios from 'axios'



export function uploadResult(){ // 上传文件的方法
    return axios.create({
        withCredentials: true,
    });
}

export default {
    name: "File",
    data() {

        return {
          dialogVisible: false,
          tags: ["游戏", "学习", "学术", "娱乐", "工作"], // 可选择的标签
          selectedTags: [],
            user : JSON.parse(sessionStorage.getItem('CurUser')),
            fileList: [],
        }
    },
    computed:{

    },
    methods:{
      showDialog() {
        // 检验用户是否未选取文件就上传
        if(this.fileList.length === 0){
          this.$message.warning('请添加文件');
          return
        }
        this.selectedTags = []
        this.dialogVisible = true;
      },
      confirmSelection() {
        console.log("已选择的标签:", this.selectedTags);
        this.dialogVisible = false;
        this.submitUpload()
      },
        fileChange(file, fileList) {
            // 因为是限制为只能上传一个文件，所以前端选取文件时，如果选取第二个文件时，要把第一个文件给覆盖掉。
            this.fileList = [fileList[fileList.length - 1]];
            // 也可以这样写：
            // this.fileList = [file];
        },
        reset(){
            // 当对话框关闭时，清空已上传的文件列表，同时fileList重新设置为空数组
            this.$refs.upload.clearFiles(); // 这个是el-upload自带的清空文件列表方法
            this.fileList = [];
        },
        uploadHttpRequest(){
            const formData = new FormData();
            formData.append('file', this.fileList[0].raw); // 要上传的文件
            formData.append('userId', this.user.id); // 用户id
            const label = this.selectedTags.join(',');
          formData.append('label', label);
          // ${server.url}/comp/uploadResult 这个是后端接收的接口
            // withCredentials: true一定要设置，否则可能会上传不成功，这是个坑
            console.log("准备上传")
            uploadResult().post(this.$httpUrl+'/comp/uploadResult', formData).then(res => res.data) .then(res =>{
                console.log(res)
                if(res.code === 1){
                    this.$message.success('上传成功');
                    this.fileList = []; // 上传成功就初始化文件列表
                }else{
                    this.$message.error(); // 上传失败就输出失败信息
                }
            }).catch(err =>{
                this.$message.error(err);
            });
        },
        // 自定义上传功能
        submitUpload(){
            this.$refs.upload.submit(); // 这个会触发upload的http-request，手动上传文件
        },



    }
}
</script>

<template>
    <div style="text-align: center; margin-top:60px">
<!--        <el-dialog width="400px" top="8vh" title="上传文件" @close="closeUpload" :visible.sync="upload_visible">-->
            <el-upload :file-list="fileList"
                       :auto-upload="false"
                       :http-request="uploadHttpRequest"
                       class="upload-demo"
                       :on-change="fileChange"
                       ref="upload"
                       drag
                       action="#">
                <i class="el-icon-upload"/>
                <div class="el-upload__text">将文件拖到此处，或<em>点击选取文件</em></div>
                <div slot="tip" class="el-upload__tip">一次只能上传一个文件</div>
            </el-upload>
            <el-button type="danger" @click="reset" style="margin-left: 30px; margin-top: 20px">清空列表</el-button>
            <el-button type="success" @click="showDialog" style="margin-left: 30px; margin-top: 20px">上传</el-button>
<!--        </el-dialog>-->

      <el-dialog :visible.sync="dialogVisible" title="请选择文件标签">
        <el-checkbox-group v-model="selectedTags">
          <el-checkbox v-for="tag in tags" :label="tag" :key="tag">{{ tag }}</el-checkbox>
        </el-checkbox-group>
        <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelection">确定</el-button>
      </span>
      </el-dialog>
    </div>
</template>

<style scoped>

</style>
