<template>
    <el-card class="my_card">
        <el-row>
            <el-col :span="24">
                <el-avatar class="avatar" :src="avatar">
                    <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt=""/>
                </el-avatar>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
                <div v-if=" username==='' ">
                    <el-button type="primary" @click="toLogin">登录</el-button>
                    <el-button type="primary" @click="toRegister">注册</el-button>
                </div>
                <div v-else>
                    <el-row><el-col :span="24">用户名：{{ username }}</el-col></el-row>
                    <el-row><el-col :span="24">用户组：{{ group }}</el-col></el-row>
                    <el-row><el-col :span="24">
                        <el-button class="logout" @click="gotoInfo">个人空间</el-button>
                        <el-button class="logout" @click="logout">登出</el-button>
                    </el-col></el-row>
                </div>
            </el-col>
        </el-row>

    </el-card>
</template>

<script>
import storage, {popMessage} from "@/http/api";
import request from "@/http/http";
import http from "@/http/http";

export default {
    name: "InfoCard",

    data() {
        return {
            username: "",
            avatar: "",
            group: ""
        }
    },

    mounted() {
        const user = storage.load("user")
        if (user.id === 0) return
        http.get("/user/" + user.id, null,
            res => {
                if (res.content.ip === undefined) {
                    storage.remove("user")
                } else {
                    user.username = res.content.username
                    user.avatar = "http://124.71.194.255:8081/image/" + res.content.avatar
                    user.role = res.content.role
                    this.username = user.username
                    this.avatar = user.avatar
                    this.group = user.role.name === "ROLE_server" ? "服主" :
                                user.role.name === "ROLE_dev" ? "开发者" : "管理员"
                    storage.store("user", user)
                }
            },
            err => {
                popMessage("错误", err.message)
            })
    },

    unmounted() {
        this.username = ""
        this.avatar = ""
    },

    methods: {
        toLogin() {
            this.$router.push("/login")
        },
        toRegister() {
            this.$router.push("/register")
        },
        logout() {
            request.get("/auth/logout").then(() => {
                this.username = ""
                this.avatar = ""
                storage.remove("user")
            }).catch((err) => {
                popMessage("登出失败", err.message)
            })
        },
        gotoInfo() {
            const user = storage.load("user")
            this.$router.push("/user/" + user.id)
        },

    }
}
</script>

<style scoped>
.my_card {
    margin: 1% 2%;
    height: 97%;
    border-radius: 6%;
}

.avatar {
    width: 130px;
    height: 130px;
    margin-bottom: 10%;
    border-radius: 8%;
}

.logout {
    margin-top: 3%;
}

</style>
