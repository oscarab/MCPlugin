<template>
    <div>
        <el-card class="register_card">
            <el-form :model="registerForm"
                     :rules="rules"
                     ref="ruleForm"
                     status-icon
                     size="large"
                     label-width="20%"
                     class="register_form">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="registerForm.username"
                              placeholder="请输入用户名..."/>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="registerForm.email"
                              placeholder="请输入邮箱..."/>
                </el-form-item>
                <el-form-item label="密码" type="password" prop="password">
                    <el-input v-model="registerForm.password"
                              type="password"
                              placeholder="请输入密码..."
                              show-password/>
                </el-form-item>
                <el-form-item label="确认密码" type="password" prop="check_password">
                    <el-input v-model="registerForm.check_password"
                              type="password"
                              placeholder="请再次输入密码..."
                              show-password/>
                </el-form-item>
                <el-form-item label="验证码" prop="captcha">
                    <el-row style="width: 100%">
                        <el-col :span="14">
                            <el-input v-model="registerForm.captcha"
                                      placeholder="请输入验证码..."/>
                        </el-col>
                        <el-col :span="10">
                            <el-button
                                style="width: 90%; margin-left: 10%"
                                :disabled="limited"
                                @click="sendEmail">
                                发送验证码 <el-tag v-if="limited" type="danger" size="small" round>{{ sendLimit }}</el-tag></el-button>
                        </el-col>
                    </el-row>
                </el-form-item>
                <el-button type="primary" @click="submitRegister">确定</el-button>
            </el-form>
        </el-card>
    </div>
</template>

<script>
import request from "@/http/http";
import {popMessage, validateField, validateForm} from "@/http/api";

export default {
    name: "RegisterForm",
    data() {
        return {
            registerForm: {
                username: "",
                email: "",
                password: "",
                captcha: ""
            },
            check_password: "",
            timer: "",
            sendLimit: 0,

            rules: {
                username: [{required: true, message: "请输入用户名", trigger: "blur"}],
                email: [{required: true, message: "请输入邮箱", trigger: "blur"},
                        {validator: this.validateEmail, trigger: "blur"}],
                password: [{required: true, message: "请输入密码", trigger: "blur"},
                            {min: 8, message: "密码至少8位", trigger: "blur"}],
                check_password: [{required: true, message: "请确认密码", trigger: "blur"},
                    {validator: this.validateCheckPassword, trigger: "blur"}],
                captcha: [{required: true, message: "请输入验证码", trigger: "blur"}]
            }
        }
    },
    computed: {
        limited() {
            return this.sendLimit > 0
        }
    },
    mounted() {
        this.timer = setInterval(() => {
            if (this.sendLimit > 0) this.sendLimit -= 1
        }, 1000)
    },
    beforeUnmount() {
        clearInterval(this.timer)
    },
    methods: {
        validateCheckPassword(rule, value, callback) {
            if (value !== this.registerForm.password) {
                callback(new Error("两次密码需一致"))
            } else {
                callback()
            }
        },
        validateEmail(rule, value, callback) {
            const reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
            if (!reg.test(value)) {
                callback(new Error("请输入正确格式的邮箱"))
            } else {
                callback()
            }
        },
        sendEmail() {
            validateField(this.$refs["ruleForm"], "email").then(() => {
                request.post("/auth/email", this.registerForm,
                    () => {
                        this.sendLimit = 60
                    },
                    (err) => {
                        popMessage("发送失败", err.message)
                    })
            }).catch( () => { })
        },
        submitRegister() {
            validateForm(this.$refs["ruleForm"]).then(() => {
                request.post("/auth/register", this.registerForm,
                    () => {
                        this.$router.replace("/login")
                    },
                    (err) => {
                        popMessage("注册失败", err.message)
                    })
            }).catch(() => {})
        }
    }
}
</script>

<style scoped>

.register_card {
    margin-left: 30%;
    margin-right: 30%;
    margin-top: 3%;
    border-radius: 5%;
}

.register_form {
    margin: 10% 15% 10% 10%;
}
</style>
