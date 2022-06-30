<template>
    <el-card class="big_card" v-loading="loading">
        <el-container v-if="!loading && plugin">
            <el-main>
                <PluginInfo :plugin="plugin"/>
                <CommentInfo v-for="(comment, key) in comments"
                             :comment="comment"
                             :key="key"
                            style="margin-top: 2%"/>
            </el-main>
            <el-footer>
                <el-pagination background
                               layout="prev, pager, next"
                               :page-count="total"
                               v-model:current-page="page"
                               class="pagination"/>
            </el-footer>
        </el-container>
        <el-skeleton v-else :rows="10" animated />
        <WebEditor style="margin-left: 2%; margin-right: 2%"
                   :value="content"
                   @input="content = $event"/>
        <el-button type="primary" style="margin-top: 2%"
                   :disabled="this.content.length === 0"
                    @click="addComment">
            回复
        </el-button>
    </el-card>
</template>

<script>
import http from "@/http/http";
import {popMessage} from "@/http/api";
import PluginInfo from "@/components/PluginInfo"
import CommentInfo from "@/components/CommentInfo";
import WebEditor from "@/components/WebEditor";

export default {
    name: "PluginInfoPage",
    data() {
      return {
          loading: false,

          page: 1,
          size: 5,
          total: 1,
          comments: [],

          plugin: null,
          content: ""
      }
    },

    mounted() {
        this.loading = true
        http.get("/plugins/" + this.$route.params.id + "/info", null ,
            res => {
                this.loading = false
                this.plugin = res.content
                this.plugin.authors.map((author) => {
                    author.avatar = "http://124.71.194.255:8081/image/" + author.avatar
                })
                this.fetchComment(this.page)
            },
            err => {
                popMessage("错误", err.message)
                this.$router.replace("/404")
            })

    },

    watch: {
        page(new_page) {
            this.fetchComment(new_page)
        },
    },

    methods: {
        fetchComment(new_page) {
            this.loading = true
            http.get("/comment/" + this.$route.params.id + "/page/" + (new_page - 1) + "/" +this.size,
                null,
                res => {
                    this.comments = res.content.page
                    this.total = res.content.total
                    this.loading = false
                    if (this.comments === undefined) {
                        this.comments = []
                    }
                    this.comments.map((comment) => {
                        comment.user.avatar = "http://124.71.194.255:8081/image/" + comment.user.avatar
                    })
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
        addComment() {
            const data = {
                content: this.content,
                plugin: {
                    id: this.plugin.id
                }
            }
            this.content = ""
            http.post("/comment/create", data,
                () => {
                    popMessage("提示", "成功添加评论")
                    this.$router.go(0)
                },
                err => {
                    popMessage("错误", err.message)
                })
        }
    },

    components: {
        WebEditor,
        CommentInfo,
        PluginInfo
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

.table {
    margin-left: 10%;
    margin-right: 10%;
}

.pagination {
    justify-content: center;
}

</style>
