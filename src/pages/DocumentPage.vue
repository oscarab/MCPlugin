<template>
    <el-card class="big_card" v-loading="document.id===undefined">
        <el-container v-if="document.id!=null">
            <el-aside width="20%">
                <el-menu
                    default-active="1"
                    style="min-height: 100vh"
                >
                    <el-menu-item index="1" @click="showDetail">
                        <el-icon><document /></el-icon>
                        <span>详细说明</span>
                    </el-menu-item>
                    <el-menu-item index="2" @click="showApi">
                        <el-icon><Files /></el-icon>
                        <span>API</span>
                    </el-menu-item>
                    <el-menu-item index="3" @click="showFaq">
                        <el-icon><QuestionFilled /></el-icon>
                        <span>FAQ</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            <el-main>
                <div style="width: 100%; text-align: left" v-html="show"/>
            </el-main>
        </el-container>
        <el-skeleton v-else :rows="10" animated />
        <el-divider/>
        <div style="font-size: small; color: darkgray; margin-bottom: 1%">
            编辑于 {{ document.updateTime }}
        </div>
        <el-button type="primary" @click="gotoEdit" v-if="isAuthor || role === 'ROLE_admin'">
            <el-icon><Edit /></el-icon>
             编辑
        </el-button>
        <el-button type="primary" v-if="isAuth && !isAuthor && role !== 'ROLE_admin'" @click="createCopy">
            <el-icon><DocumentCopy /></el-icon> 创建副本
        </el-button>
        <el-popconfirm
            confirm-button-text="确认"
            cancel-button-text="取消"
            title="确认要删除该文档吗？"
            @confirm="deleteDocument"
        >
            <template #reference>
                <el-button type="danger"
                           v-if="role === 'ROLE_admin'" >
                    删除
                </el-button>
            </template>
        </el-popconfirm>
        <div style="text-align: left; margin-left: 1%; margin-bottom: 1%; font-weight: bold"> 贡献者 </div>
        <el-col
            style="margin-bottom: 2%; margin-left: 1%"
            v-for="author in document.cooperators"
            :key="author.id">
            <el-card
                class="pointer"
                @click="gotoUser(author.id)">
                <el-avatar class="avatar"
                           :src="author.avatar"
                           size="small">
                    <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt="">
                </el-avatar>
                <div style="font-size: medium; font-weight: bold"> {{ author.username }} </div>
            </el-card>
        </el-col>
    </el-card>
</template>

<script>
import http from "@/http/http";
import {
    Document,
    QuestionFilled,
    Files,
    Edit,
    DocumentCopy
} from '@element-plus/icons-vue'
import storage, {popMessage} from "@/http/api";

export default {
    name: "DocumentPage",

    data() {
      return {
          document: {},
          show: "",
          isAuthor: false,
          role: "",
      }
    },

    computed: {
        isAuth() {
            const user = storage.load("user")
            return user.id !== 0
        }
    },

    mounted() {
        http.get("/document/" + this.$route.params.id + "/info", null,
            res => {
                this.document = res.content
                this.show = this.document.explanation

                const user = storage.load("user")
                if (user.id !== 0)
                    this.role = user.authorities[0].authority
                this.document.cooperators.map((author) => {
                    author.avatar = "http://124.71.194.255:8081/image/" + author.avatar
                    if (author.id === user.id) {
                        this.isAuthor = true
                    }
                })
            })
    },

    methods: {
        showDetail() {
            this.show = this.document.explanation
        },
        showApi() {
            this.show = this.document.api
        },
        showFaq() {
            this.show = this.document.faq
        },
        gotoEdit() {
            this.$router.push({
                path: "/document/" + this.document.id + "/edit",
                query: {
                    plugin_id: this.document.plugin.id
                }
            })
        },
        createCopy() {
            const data = {
                explanation: this.document.explanation,
                api: this.document.api,
                faq: this.document.faq,
                pluginDocument: {
                    id: this.document.id
                }
            }
            http.post("/copy/create", data,
                () => {
                    popMessage("提示", "创建成功")
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
        gotoUser(id) {
            this.$router.push("/user/" + id)
        },
        deleteDocument() {
            http.get("/admin/document/" + this.document.id, null,
                () => {
                    this.$router.back()
                    popMessage("提示", "删除成功")
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
    },

    components: {
        Document,
        QuestionFilled,
        Files,
        Edit,
        DocumentCopy
    }
}
</script>

<style scoped>
.big_card {
    margin-left: 10%;
    margin-right: 10%;
    margin-top: 1%;
    height: 100%;
    border-radius: 10px;
}

.pointer:hover {
    cursor: pointer;
}
</style>
