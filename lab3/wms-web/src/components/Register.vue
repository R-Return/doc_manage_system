<template>
    <div class="loginBody">
        <div class="loginDiv">
            <div class="login-content">
                <h1 class="login-title">用户注册</h1>
                <el-form :model="registerForm" label-width="100px"
                         :rules="rules" ref="registerForm">
                    <el-form-item label="账号" prop="no">
                        <el-input style="width: 200px" type="text" v-model="registerForm.no"
                                  autocomplete="off" size="small"></el-input>
                    </el-form-item>
                    <el-form-item label="名字" prop="name">
                        <el-input style="width: 200px" type="text" v-model="registerForm.name"
                                  autocomplete="off" size="small"></el-input>
                    </el-form-item>
                    <el-form-item label="密码" prop="password">
                        <el-input style="width: 200px" type="password" v-model="registerForm.password"
                                  show-password autocomplete="off" size="small" ></el-input>
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="cancel" :disabled="confirm_disabled">取 消</el-button>
                        <el-button type="primary" @click="register" :disabled="confirm_disabled">注 册</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>


<script>
export default {
    name: "register",
    data(){
        let checkDuplicateNo =(rule,value,callback)=>{
            this.$axios.get(this.$httpUrl+"/user/findByNo?no="+this.registerForm.no).then(res=>res.data).then(res=>{
                if(res.code!=1){
                    callback()
                }else{
                    callback(new Error('账号已经存在'));
                }
            })
        };
        let checkDuplicateName =(rule,value,callback)=>{
            this.$axios.get(this.$httpUrl+"/user/findByName?name="+this.registerForm.name).then(res=>res.data).then(res=>{
                if(res.code!=1){
                    callback()
                }else{
                    callback(new Error('名字已经存在'));
                }
            })
        };
        return{
            confirm_disabled:false,
            registerForm:{
                no:'',
                name:'',
                password:''
            },
            rules:{
                no: [
                    { required: true, message: '请输入账号', trigger: 'blur' },
                    {validator:checkDuplicateNo, trigger: 'blur'}
                ],
                name: [
                    { required: true, message: '请输入名字', trigger: 'blur' },
                    {validator:checkDuplicateName, trigger: 'blur'}
                ],
                password: [
                    { required: true, message: '请输密码', trigger: 'blur' },
                    {min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
                ]
            }
        }
    },
    methods:{

        register(){
            this.confirm_disabled=true;
            this.$refs.registerForm.validate((valid) => {
                if (valid) { //valid成功为true，失败为false
                    //去后台验证用户名密码
                    this.$axios.post(this.$httpUrl+'/user/register',this.registerForm).then(res=>res.data).then(res=>{
                        console.log(res)
                        if(res.code==1){
                            //跳转到登录
                            this.$message({message: '注册成功！', type: 'success'});
                            this.$router.replace('/');
                        }else{
                            this.confirm_disabled=false;
                            alert('注册失败！请重试');
                            return false;
                        }
                    });
                } else {
                    this.confirm_disabled=false;
                    console.log('注册失败');
                    return false;
                }
            });

        },
        cancel(){
            this.$router.replace('/');
        }
    }
}
</script>

<style scoped>
.loginBody {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: #B3C0D1;
}
.loginDiv {
    position: absolute;
    top: 50%;
    left: 50%;
    margin-top: -200px;
    margin-left: -250px;
    width: 450px;
    height: 330px;
    background: #fff;
    border-radius: 5%;

}
.login-title {
    margin: 20px 0;
    text-align: center;
}
.login-content {
    width: 400px;
    height: 250px;
    position: absolute;
    top: 25px;
    left: 25px;
}
</style>