<template>
    <el-card>
        <el-container>
            <el-aside width="20%">
                <el-card @click="gotoUser(comment.user.id)"
                         class="pointer">
                    <el-row justify="space-around">
                        <el-col :span="1">
                            <el-avatar class="avatar"
                                       :src="comment.user.avatar"
                                       size="large">
                                <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt="">
                            </el-avatar>
                        </el-col>
                        <el-col :span="2" style="margin-top: 8%">
                            <div style="font-size: large; font-weight: bold"> {{ comment.user.username }} </div>
                        </el-col>
                    </el-row>
                    <el-row justify="end">
                        <el-tag> {{ this.group }} </el-tag>
                    </el-row>
                </el-card>
            </el-aside>
            <el-main>
                <span style="font-size: small; color: darkgray; float: left">
                    发表于 {{ comment.publishTime }}
                </span>
                <br/>
                <el-divider border-style="dashed"/>
                <div style="width: 100%; text-align: left" v-html="comment.content"/>

                <el-popconfirm
                    confirm-button-text="确认"
                    cancel-button-text="取消"
                    title="确认要删除该评论吗？"
                    @confirm="deleteComment"
                >
                    <template #reference>
                        <el-link
                            style="float: right; font-size: small"
                            type="danger"
                            v-if="role==='ROLE_admin'">删除</el-link>
                    </template>
                </el-popconfirm>

            </el-main>
        </el-container>
    </el-card>
</template>

<script>
import storage, {popMessage} from "@/http/api";
import http from "@/http/http";

export default {
    name: "CommentInfo",
    props: {
        comment: Object
    },
    data() {
        return {
            role: "",
            group: ""
        }
    },
    mounted() {
        const user = storage.load("user")
        if (user.id !== 0)
            this.role = user.authorities[0].authority
        this.group = this.comment.user.role.id === 1 ? "服主" :
            this.comment.user.role.id === 2 ? "开发者" : "管理员"
    },
    methods: {
        gotoUser(id) {
            this.$router.push("/user/" + id)
        },
        deleteComment() {
            http.get("/admin/comment/" + this.comment.id, null,
                () => {
                    this.$router.go(0)
                    popMessage("提示", "删除成功")
                },
                err => {
                    popMessage("错误", err.message)
                })
        }
    }
}
</script>

<style scoped>
.pointer:hover {
    cursor: pointer;
}
</style>
