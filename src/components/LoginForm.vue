<template>
    <div>
        <el-card class="login_card">
            <el-form :model="login_form"
                     :rules="rules"
                     ref="ruleForm"
                     hide-required-asterisk="true"
                     size="large"
                     label-width="20%"
                     class="login_form">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="login_form.username"
                              placeholder="请输入用户名..."/>
                </el-form-item>
                <el-form-item label="密码" type="password" prop="password">
                    <el-input v-model="login_form.password"
                              type="password"
                              placeholder="请输入密码..."
                              show-password/>
                </el-form-item>
                <el-form-item label="验证码" prop="captcha">
                    <el-row style="width: 100%">
                        <el-col :span="14">
                            <el-input v-model="login_form.captcha"
                                      placeholder="请输入验证码..."/>
                        </el-col>
                        <el-col :span="10" @click="getImage">
                            <el-image class="captcha_image" :src="captcha_image">
                                <template #error>
                                    <el-icon><Picture /></el-icon>
                                </template>
                            </el-image>
                        </el-col>
                    </el-row>
                </el-form-item>
                <el-button type="primary" @click="submitLogin">确定</el-button>
            </el-form>
        </el-card>
    </div>
</template>

<script>
import { Picture } from '@element-plus/icons-vue'
import request from "@/http/http";
import {popMessage, validateForm} from "@/http/api"
import storage from "@/http/api";

export default {
    name: "LoginForm",
    data() {
        return {
            login_form: {
                username: "",
                password: "",
                captcha: ""
            },
            captcha_image: "",
            rules: {
                username: [{required: true, message: "请输入用户名", trigger: "blur"}],
                password: [{required: true, message: "请输入密码", trigger: "blur"}],
                captcha: [{required: true, message: "请输入验证码", trigger: "blur"}]
            }
        }
    },
    components: {
        Picture
    },
    mounted() {
        this.getImage()
    },
    methods: {
        getImage() {
            request.get("/auth/captcha",
                {responseType: "blob"},
                res => {
                    this.captcha_image = window.URL.createObjectURL(res)
                },
                err => {
                    console.log(err)
                })
        },
        submitLogin() {
            validateForm(this.$refs["ruleForm"]).then(
                () => {
                    return request.post("/auth/login", this.login_form,
                        res => {
                            storage.store("user", res.content)
                            this.$router.replace("/")
                        },
                        err => {
                            this.getImage()
                            popMessage("登录失败", err.message)
                        })
                }
            ).catch(() => { })
        }
    }
}
</script>

<style scoped>

.login_card {
    margin-left: 30%;
    margin-right: 30%;
    margin-top: 3%;
    border-radius: 5%;
}

.login_form {
    margin: 10% 15% 10% 10%;
}

.captcha_image {
    margin-left: 5%;
    width: 90%;
    height: 38px;
    border: 1px solid var(--el-border-color);
    border-radius: 5%;
    cursor: pointer;
}

</style>
