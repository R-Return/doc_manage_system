<template>
  <div>
    <div style="margin-bottom: 5px;">
      <el-input v-model="fileName" placeholder="请输入文件名" suffix-icon="el-icon-search" style="width: 200px;"
                @keyup.enter.native="loadPost"></el-input>

        <el-select v-model="selectedTags" multiple placeholder="请选择文件标签" style="margin-left: 5px;">
          <el-option v-for="tag in tags" :key="tag" :label="tag" :value="tag"></el-option>
        </el-select>

      <el-button type="primary" style="margin-left: 5px;" @click="loadPost">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
              border
    >
      <!--            <el-table-column prop="id" label="ID" width="60">-->
      <!--            </el-table-column>-->
      <el-table-column prop="filename" label="文件名" width="300">
      </el-table-column>
      <el-table-column prop="fileurl" label="路径">
      </el-table-column>
      <el-table-column prop="label" label="标签">
      </el-table-column>

      <el-table-column prop="operate" label="操作" width="210">
        <template slot-scope="scope">
          <el-button size="small" type="success" @click="downloadResult(scope.row)">下载</el-button>
          <el-button size="small" type="success" @click="showShareDialog(scope.row)">分享</el-button>
          <el-popconfirm
              title="确定删除吗？"
              @confirm="del(scope.row.id)"
              style="margin-left: 5px;"
          >
            <el-button slot="reference" size="small" type="danger">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20,30]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>
    <el-dialog :visible.sync="dialogVisible" title="分享" width="20%">
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="share">确定</el-button>
      </span>
      <div>
        <p>请输入要分享的用户：</p>
        <el-autocomplete
            v-model="searchValue"
            :fetch-suggestions="fetchUserSuggestions"
            placeholder="请输入用户名"
            :trigger-on-focus="false"
        ></el-autocomplete>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import fileDownload from "js-file-download";
import axios from "axios";

function request(config) {
  // 创建axios的实例
  const instance = axios.create({
    baseURL: 'http://localhost:8090', // 这个是后端的ip地址
  });
  return instance(config);
}

export function getResult(resultfile) {
  return request({
    url: 'comp/getResult',
    method: 'post',
    responseType: 'arraybuffer',
    data: {
      resultfile: resultfile,
      userId: JSON.parse(sessionStorage.getItem('CurUser')).id,
    }
  });
}

export default {
  name: "Home",
  data() {
    return {
      shareFile:'',
      selectedTags: [],
      tags: ["游戏", "学习", "学术", "娱乐", "工作"],
      searchValue: '',
      user: JSON.parse(sessionStorage.getItem('CurUser')),
      fileName: '',
      dialogVisible:false,
      shareUrl: '',
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
    }
  },
  methods: {
    fetchUserSuggestions(queryString, callback) {
      // 从后台获取用户名列表
      // 请根据你的实际情况修改下面的请求地址和参数
      const url = this.$httpUrl + '/user/getUserNameTop10';
      axios
          .get(url, { params: { userName: queryString } })
          .then(response => {
            const users = response.data.map(user => ({ value: user, label: user }));
            callback(users);
          })
          .catch(error => {
            console.error(error);
            callback([]);
          });
  },
    resetParam() {
      this.fileName = ''
      this.selectedTags =[]
    },
    downloadResult(row) {
      console.log("准备下载")
      console.log(row)
      getResult(row.filename).then(res => {
        fileDownload(res.data, row.filename); // 使用js-file-download来接收二进制流然后下载
      }).catch(err => {
        this.$message.error(err);
      });
    },
    del(id) {
      this.$axios.get(this.$httpUrl + '/comp/del?id=' + id).then(res => res.data).then(res => {
        console.log(res)
        if (res.code == 1) {

          this.$message({
            message: '操作成功！',
            type: 'success'
          });
          this.loadPost()
        } else {
          this.$message({
            message: '操作失败！',
            type: 'error'
          });
        }

      })
    },
    showShareDialog(row) {
      this.shareFile = row
      this.searchValue = ''
      this.dialogVisible = true
    },
    share() {
      // 在这里执行分享操作
      console.log(this.shareFile)
      this.shareFile.id = null
      this.$set(this.shareFile, 'userName', this.searchValue)
      this.$axios.post(this.$httpUrl + '/comp/saveShare', this.shareFile).then(res => res.data).then(res => {
         console.log(res)
          this.$message.success("分享成功")
           this.searchValue = ''
          this.shareFile = ''
      })
      this.dialogVisible = false;
    },
    searchUsers() {
      if (this.shareUserName.trim()) {
        this.$axios.get(`${this.$httpUrl}/user/search?name=${this.shareUserName.trim()}`).then(res => res.data).then(res => {
          if (res && res.length > 0) {
            const userList = res;
            this.$autocomplete('请选择要分享的用户', userList, {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
            }).then(({ value }) => {
              // 用户选择后更新选中的用户ID
              this.selectedUserId = value;
            }).catch(() => {
              this.selectedUserId = null; // 用户取消选择时清空选中的用户ID
            });
          } else {
            this.selectedUserId = null; // 未找到匹配用户时清空选中的用户ID
            // this.$message.warning('未找到匹配的用户');
          }
        });
      }
    },
    loadPost() {
      this.$axios.post(this.$httpUrl + '/comp/listPageC1', {
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        param: {
          userId: this.user.id,
          fileName: this.fileName,
          label: this.selectedTags
        }
      }).then(res => res.data).then(res => {
        console.log(res)
        if (res.code == 1) {
          this.tableData = res.data
          console.log(this.tableData)
          this.total = res.total
        } else {
          alert('获取数据失败')
        }

      })
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
      this.pageNum = 1
      this.pageSize = val
      this.loadPost()
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
      this.pageNum = val
      this.loadPost()
    },
  },
  mounted() {
    this.loadPost()
  }

}
</script>

<style scoped>

</style>
