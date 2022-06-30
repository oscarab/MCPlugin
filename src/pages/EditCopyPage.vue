<template>
    <el-card class="big_card" v-loading="loading">
        <el-container>
            <el-aside width="20%">
                <el-menu
                    default-active="1"
                >
                    <el-menu-item index="1" @click="show=1">
                        <el-icon><document /></el-icon>
                        <span>详细说明</span>
                    </el-menu-item>
                    <el-menu-item index="2" @click="show=2">
                        <el-icon><Files /></el-icon>
                        <span>API</span>
                    </el-menu-item>
                    <el-menu-item index="3" @click="show=3">
                        <el-icon><QuestionFilled /></el-icon>
                        <span>FAQ</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            <el-main>
                <WebEditor  style="margin-left: 2%; margin-right: 2%"
                            v-if="show === 1"
                            :value="copy.explanation"
                            @input="this.copy.explanation = $event"/>
                <WebEditor  style="margin-left: 2%; margin-right: 2%"
                            v-if="show === 2"
                            :value="copy.api"
                            @input="this.copy.api = $event"/>
                <WebEditor  style="margin-left: 2%; margin-right: 2%"
                            v-if="show === 3"
                            :value="copy.faq"
                            @input="this.copy.faq = $event"/>
            </el-main>
        </el-container>
        <el-button type="primary"
                   style="margin-top: 2%"
                   @click="submit">确认</el-button>
        <el-button type="primary"
                   style="margin-top: 2%"
                   @click="pullRequest">发起合并请求</el-button>
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
import {popMessage} from "@/http/api";

export default {
    name: "EditCopyPage",

    data() {
        return {
            loading: false,
            copy: {
                id: 0,
                explanation: "",
                api: "",
                faq: "",
                pluginDocument: {
                    id: 0
                }
            },
            show: 1,
        }
    },

    methods: {
        submit() {
            http.post("/copy/save", this.copy,
                () => {
                    popMessage("提示", "成功编辑文档副本")
                    this.$router.back()
                },
                err => {
                    popMessage("错误", err.message)
                })
        },

        pullRequest() {
            const data = {
                doc_id: this.copy.pluginDocument.id,
                copy_id: this.copy.id
            }
            http.post("/copy/request", data,
                () => {
                    popMessage("提示", "成功发起合并请求")
                    this.$router.back()
                },
                err => {
                    popMessage("错误", err.message)
                })
        }
    },

    mounted() {
        this.loading = true
        http.get("/copy/" + this.$route.params.id + "/info", null,
            res => {
                console.log(res.content)
                this.copy.id = res.content.id
                this.copy.explanation = res.content.explanation
                this.copy.api = res.content.api
                this.copy.faq = res.content.faq
                this.copy.pluginDocument.id = res.content.pluginDocument.id
                this.loading = false
            },
            err => {
                popMessage("错误", err.message)
                this.$router.replace("/404")
            })
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
