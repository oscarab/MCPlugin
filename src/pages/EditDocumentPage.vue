<template>
    <el-dialog v-model="showDialog" title="添加协作者" @close="closeDialog">
        <el-row justify="center">
            <el-col :span="3">
                <span>用户名</span>
            </el-col>
            <el-col :span="10">
                <el-input
                    v-model="searchName"
                    placeholder="输入用户名..."
                />
            </el-col>
            <el-col :span="3">
                <el-button type="primary" @click="search">搜索</el-button>
            </el-col>
        </el-row>
        <el-row justify="center" style="margin-top: 2%">
            <el-select v-model="selectUser" placeholder="请选择用户" @change="allow=true">
                <el-option
                    v-for="item in searchResult"
                    :key="item.id"
                    :label="item.username"
                    :value="item.id"
                >
                    <el-avatar class="avatar"
                               style="float: left"
                               :src="item.avatar"
                               size="small">
                        <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt="">
                    </el-avatar>
                    <span style="float: right"> {{ item.username }} </span>
                </el-option>
            </el-select>
        </el-row>
        <el-row  justify="space-evenly" style="margin-top: 2%">
            <el-button type="primary"
                       :disabled="!allow"
                       @click="addAuthor(selectUser)">确定</el-button>
        </el-row>

    </el-dialog>

    <el-card class="big_card" v-loading="loading">
        <el-container>
            <el-aside width="20%">
                <el-menu
                    default-active="1"
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
                <WebEditor  style="margin-left: 2%; margin-right: 2%"
                            v-if="show === 1"
                            :value="document.explanation"
                            @input="document.explanation = $event"/>
                <WebEditor  style="margin-left: 2%; margin-right: 2%"
                            v-if="show === 2"
                            :value="document.api"
                            @input="document.api = $event"/>
                <WebEditor  style="margin-left: 2%; margin-right: 2%"
                            v-if="show === 3"
                            :value="document.faq"
                            @input="document.faq = $event"/>
            </el-main>
        </el-container>
        <el-col style="margin-left: 5%; margin-right: 2%"> 文档协作者 </el-col>
        <el-col>
            <el-tag
                v-for="tag in this.document.cooperators"
                :key="tag"
                style="font-size: medium"
                closable
                :disable-transitions="false"
                @close="removeAuthor(tag.id)"
            >
                {{ tag.username }}
            </el-tag>
        </el-col>
        <el-col>
            <el-button size="small"
                       style="margin-left: 5%"
                       v-if="hasRole"
                       @click="inputAuthor">
                + 共同协作
            </el-button>
        </el-col>
        <el-button type="primary"
                   style="margin-top: 2%"
                   @click="submit">确认</el-button>
    </el-card>
</template>

<script>
import http from "@/http/http";
import {
    Document,
    QuestionFilled,
    Files
} from '@element-plus/icons-vue'
import WebEditor from "@/components/WebEditor";
import storage, {popMessage} from "@/http/api";

export default {
    name: "EditDocumentPage",

    data() {
        return {
            document: {
                id: 0,
                explanation: "",
                api: "",
                faq: "",
                cooperators: [],
                plugin: {
                    id: 0
                }
            },
            show: 1,
            loading: true,

            showDialog: false,
            searchName: "",
            searchResult: [],
            selectUser: "",
            allow: false,
            hasRole: false
        }
    },

    mounted() {
        if (this.$route.query.plugin_id === 0 || this.$route.query.plugin_id === undefined) {
            this.$router.replace("/404")
            return
        }
        this.document.plugin.id = this.$route.query.plugin_id

        // 新建
        if (this.$route.params.id === "0") {
            this.document.cooperators.push(storage.load("user"))
            this.loading = false
            return
        }

        //编辑
        http.get("/document/" + this.$route.params.id + "/info", null,
            res => {
                this.document.id = res.content.id
                this.document.explanation = res.content.explanation
                this.document.api = res.content.api
                this.document.faq = res.content.faq
                this.document.cooperators = res.content.cooperators
                this.loading = false

                this.hasRole = true
            },
            err => {
                popMessage("错误", err.message)
                this.$router.replace("/404")
            })
    },

    methods: {
        showDetail() {
            this.show = 1
        },
        showApi() {
            this.show = 2
        },
        showFaq() {
            this.show = 3
        },
        submit() {
            if (this.document.id === 0) {
                http.post("/document/create", this.document,
                    () => {
                        popMessage("提示", "成功新建文档")
                        this.$router.back()
                    },
                    err => {
                        popMessage(err.message)
                    })
                return
            }
            http.post("/document/save", this.document,
                () => {
                    popMessage("提示", "成功编辑文档")
                    this.$router.back()
                },
                err => {
                    popMessage("错误", err.message)
                })
        },

        closeDialog() {
            this.showDialog = false
            this.searchResult = []
            this.searchName = ""
            this.selectUser = ""
            this.allow = false
        },
        inputAuthor() {
            this.showDialog = true
        },
        addAuthor(selected) {
            const result = this.searchResult.find( user => user.id === selected )
            this.document.cooperators.push(result)
            this.closeDialog()
        },
        removeAuthor(id) {
            if (!this.hasRole) return
            if (id === storage.load("user").id) {
                return
            }
            const result = this.document.cooperators.findIndex( user => user.id === id )
            this.document.cooperators.splice(result, 1)
        },
        search() {
            if (this.searchName.length === 0) return
            http.get("/user/search/" + this.searchName, null,
                res => {
                    this.searchResult = res.content
                    const id = storage.load("user").id
                    const result = this.searchResult.findIndex( user => user.id === id )
                    if (result !== -1)
                        this.searchResult.splice(result, 1)
                    this.document.cooperators.map(user => {
                        const result = this.searchResult.findIndex( u => u.id === user.id )
                        if (result !== -1)
                            this.searchResult.splice(result, 1)
                    })
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
    },

    components: {
        WebEditor,
        Document,
        QuestionFilled,
        Files
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
</style>
