<template>
    <el-card class="big_card" v-loading="loading">
        <el-container v-if="user !== null">
            <el-aside width="20%">
                <el-menu
                    default-active="1"
                    style="min-height: 100vh"
                >
                    <el-menu-item index="1" @click="show = 1">
                        <el-icon><User /></el-icon>
                        <span>个人信息</span>
                    </el-menu-item>
                    <el-menu-item index="2" @click="show = 2">
                        <el-icon><Tools /></el-icon>
                        <span>发表的插件</span>
                    </el-menu-item>
                    <el-menu-item index="3" @click="show = 3">
                        <el-icon><Document /></el-icon>
                        <span>维护的文档</span>
                    </el-menu-item>
                    <el-menu-item index="4" v-if="isMe" @click="show = 4">
                        <el-icon><DocumentCopy /></el-icon>
                        <span>维护的文档副本</span>
                    </el-menu-item>
                    <el-menu-item index="5" v-if="isMe" @click="show = 5">
                        <el-icon><ChatSquare /></el-icon>
                        <span>通知</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            <el-main>
                <div v-if="show === 1">
                    <el-upload
                        class="avatar-uploader"
                        action=""
                        :show-file-list="false"
                        :http-request="upload"
                        :before-upload="beforeAvatarUpload"
                        v-if="isMe"
                    >
                        <el-tooltip content="点击上传头像" v-if="isMe">
                            <el-avatar class="avatar" size="large" :src="user.avatar">
                                <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt=""/>
                            </el-avatar>
                        </el-tooltip>
                    </el-upload>
                    <el-avatar class="avatar" size="large" :src="user.avatar" v-else>
                        <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt=""/>
                    </el-avatar>
                    <el-descriptions
                        class="table"
                        :column="1"
                        border
                    >
                        <el-descriptions-item label="用户名">
                            {{ user.username }}
                        </el-descriptions-item>
                        <el-descriptions-item label="注册时间">
                            {{ user.registerTime }}
                        </el-descriptions-item>
                        <el-descriptions-item label="最后登陆时间">
                            {{ user.loginTime }}
                        </el-descriptions-item>
                        <el-descriptions-item label="邮箱" v-if="isMe">
                            {{ user.email }}
                        </el-descriptions-item>
                        <el-descriptions-item label="登录Ip" v-if="isMe">
                            {{ user.ip }}
                        </el-descriptions-item>
                    </el-descriptions>
                    <el-button type="primary"
                               v-if="user.ip !== undefined && waiteToEdit !== ''"
                               @click="editUser"> 保存 </el-button>
                    <el-space wrap>
                        <el-select v-model="select_role" placeholder="请选择角色" v-if="role==='ROLE_admin' && !isMe">
                            <el-option
                                v-for="item in role_option"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                        <el-button type="primary"
                                   v-if="role==='ROLE_admin' && !isMe"
                                   @click="editRole"> 修改角色 </el-button>
                    </el-space>
                </div>
                <div v-else-if="show === 2 && plugins">
                    <el-card v-for="(plugin, key) in plugins"
                             :key="key"
                             class="plugin"
                             @click="gotoInfo(plugin.id)">
                        <el-row justify="first">
                            <el-col :span="8">
                                <span>{{ plugin.name }}</span>
                            </el-col>
                            <el-col :span="8">
                                <el-space wrap>
                                    <el-tag> {{ plugin.serverType }} </el-tag>
                                    <el-tag> {{ plugin.serverVersion }} </el-tag>
                                    <el-tag> {{ plugin.pluginType }} </el-tag>
                                </el-space>
                            </el-col>
                            <el-col :span="8">
                                <span class="time"> 更新时间: {{ plugin.updateTime }} <br/></span>
                                <span class="time"> 发表时间: {{ plugin.publishTime }} </span>
                            </el-col>
                        </el-row>
                    </el-card>
                    <el-pagination background
                                   layout="prev, pager, next"
                                   :page-count="plugin_total"
                                   v-model:current-page="plugin_page"
                                   class="pagination"/>
                </div>
                <div v-else-if="show === 3 && documents">
                    <el-card v-for="(document, key) in documents"
                             :key="key"
                             class="plugin"
                             @click="gotoDocInfo(document.id)">
                        <el-row justify="first">
                            <el-col :span="2">
                                <el-tag type="success"> 文档 </el-tag>
                            </el-col>
                            <el-col :span="10">
                                <span>{{ document.plugin.name }}</span>
                            </el-col>
                            <el-col :span="10">
                                <span class="time"> 更新时间: {{ document.updateTime }} </span>
                            </el-col>
                        </el-row>
                    </el-card>
                    <el-pagination background
                                   layout="prev, pager, next"
                                   :page-count="doc_total"
                                   v-model:current-page="doc_page"
                                   class="pagination"/>
                </div>
                <div v-else-if="show === 4 && copies">
                    <el-card v-for="(copy, key) in copies"
                             :key="key"
                             class="plugin"
                             @click="gotoCopyInfo(copy.id)">
                        <el-row justify="first">
                            <el-col :span="2">
                                <el-tag type="success"> 文档副本 </el-tag>
                            </el-col>
                            <el-col :span="10">
                                <span>{{ copy.pluginDocument.plugin.name }}</span>
                            </el-col>
                            <el-col :span="10">
                                <span class="time"> 更新时间: {{ copy.updateTime }} <br/></span>
                                <span class="time"> 创建时间: {{ copy.createdTime }} </span>
                            </el-col>
                        </el-row>
                    </el-card>
                    <el-pagination background
                                   layout="prev, pager, next"
                                   :page-count="copy_total"
                                   v-model:current-page="copy_page"
                                   class="pagination"/>
                </div>
                <div v-else-if="show === 5 && notifies">
                    <el-popover
                        placement="top-start"
                        title="操作"
                        :width="200"
                        trigger="click"
                        v-for="(notify, key) in notifies"
                        :key="key"
                    >
                        <template #reference>
                            <el-card  class="plugin">
                                <el-row justify="first">
                                    <el-col :span="2">
                                        <el-tag type="success" v-if="notify.state==='ACCEPT'"> 已合并 </el-tag>
                                        <el-tag type="danger" v-else-if="notify.state==='REJECT'"> 已拒绝 </el-tag>
                                        <el-tag type="info" v-else> 等待 </el-tag>
                                    </el-col>
                                    <el-col :span="10">
                                        <span>{{ notify.plugin.name }}</span>
                                    </el-col>
                                    <el-col :span="5">
                                        <span class="time"> 时间: {{ notify.time }} </span>
                                    </el-col>
                                    <el-col :span="5" v-if="notify.user.id!==user.id">
                                        <el-avatar :src="notify.user.avatar">
                                            <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt=""/>
                                        </el-avatar>
                                        <div class="time"> {{ notify.user.username }} </div>
                                    </el-col>
                                </el-row>
                            </el-card>
                        </template>
                        <el-space wrap>
                            <el-button type="success"
                                       v-if="notify.state==='WAITE' && notify.user.id!==user.id"
                                       @click="mergeHandel(notify.id, 1)">
                                接受
                            </el-button>
                            <el-button type="warning"
                                       v-if="notify.state==='WAITE' && notify.user.id!==user.id"
                                       @click="mergeHandel(notify.id, 0)">
                                拒绝
                            </el-button>
                            <el-button type="primary" @click="gotoCopy(notify.copy.id)">查看合并内容</el-button>
                            <el-button type="primary" @click="gotoInfo(notify.plugin.id)">查看插件</el-button>
                        </el-space>
                    </el-popover>
                    <el-pagination background
                                   layout="prev, pager, next"
                                   :page-count="notify_total"
                                   v-model:current-page="notify_page"
                                   class="pagination"/>
                </div>
                <div v-else>
                    <el-empty :image-size="200" />
                </div>
            </el-main>
        </el-container>
        <el-skeleton v-else :rows="10" animated />
    </el-card>
</template>

<script>
import http from "@/http/http";
import storage, {popMessage} from "@/http/api";
import {
    ChatSquare,
    Document,
    User,
    Tools,
    DocumentCopy,
} from '@element-plus/icons-vue'

export default {
    name: "UserPage",

    data() {
        return {
            loading: false,
            user: null,
            plugins: [],
            documents: [],
            copies: [],
            notifies: [],
            plugin_page: 1,
            doc_page: 1,
            copy_page: 1,
            notify_page: 1,
            plugin_total: 5,
            doc_total: 5,
            copy_total: 5,
            notify_total: 5,
            size: 5,
            isMe: false,
            role: "",
            show: 1,

            waiteToEdit: "",
            select_role: "",

            role_option: [
                {
                    value: "ROLE_server",
                    label: "服主"
                },
                {
                    value: "ROLE_dev",
                    label: "开发者"
                },
                {
                    value: "ROLE_admin",
                    label: "管理员"
                },
            ]
        }
    },

    mounted() {
        this.loading = true
        http.get("/user/" + this.$route.params.id, null,
            res => {
                this.user = res.content
                this.user.avatar = "http://124.71.194.255:8081/image/" + this.user.avatar

                const me = storage.load("user")
                this.isMe = me.id === this.user.id
                if (me.id !== 0)
                    this.role = me.authorities[0].authority
                this.select_role = this.user.role.name
                this.loading = false
                this.fetchData()
            },
            err => {
                popMessage("错误", err.message)
                this.$router.replace("/404")
            })
    },

    watch: {
        plugin_page(val) {
            this.fetchPlugin(val)
        },
        doc_page(val) {
            this.fetchDocument(val)
        },
        copy_page(val) {
            this.fetchCopy(val)
        },
        notify_page(val) {
            this.fetchNotify(val)
        },
    },

    methods: {
        fetchData() {
            this.fetchPlugin()
            this.fetchDocument()
            if (this.isMe) {
                this.fetchCopy()
                this.fetchNotify()
            }
        },
        fetchPlugin() {
            const id = this.user.id
            const page = this.plugin_page - 1
            this.loading = true
            http.get("/plugins/user/" + id + "/ " + page + "/" + this.size, null,
                res => {
                    this.plugins = res.content.page
                    this.plugin_total = res.content.total
                    this.loading = false
                },
                err => {
                    popMessage("错误", err.message)
                    this.$router.replace("/404")
                })

        },
        fetchDocument() {
            const id = this.user.id
            const page = this.doc_page - 1
            this.loading = true
            http.get("/document/user/" + id + "/ " + page + "/" + this.size, null,
                res => {
                    this.documents = res.content.page
                    this.doc_total = res.content.total
                    this.loading = false
                },
                err => {
                    popMessage("错误", err.message)
                    this.$router.replace("/404")
                })
        },
        fetchCopy() {
            const id = this.user.id
            const page = this.copy_page - 1
            this.loading = true
            http.get("/copy/user/" + id + "/" + page + "/" + this.size, null,
                res => {
                    this.copies = res.content.page
                    this.copy_total = res.content.total
                    this.loading = false
                },
                err => {
                    popMessage("错误", err.message)
                    this.$router.replace("/404")
                })
        },
        fetchNotify() {
            const page = this.notify_page - 1
            this.loading = true
            http.get("/notify/" + page + "/" + this.size, null,
                res => {
                    this.notifies = res.content.page
                    this.notify_total = res.content.total
                    this.loading = false
                    if (this.notifies)
                        this.notifies.map((notify) => {
                            notify.user.avatar = "http://124.71.194.255:8081/image/" + notify.user.avatar
                        })
                },
                err => {
                    popMessage("错误", err.message)
                    this.$router.replace("/404")
                })
        },
        gotoInfo(id) {
            this.$router.push("/plugin/" + id)
        },
        gotoDocInfo(id) {
            this.$router.push("/document/" + id)
        },
        gotoCopyInfo(id) {
            this.$router.push("/copy/" + id + "/edit")
        },

        mergeHandel(id, acc) {
            const data = {
                notify_id: id,
                accept: acc
            }
            http.post("/copy/merge", data,
                () => {
                    popMessage("提示", "合并成功")
                    this.$router.go(0)
                },
                err => {
                    popMessage("错误", err.message)
                    this.$router.go(0)
                })
        },
        gotoCopy(id) {
            this.$router.push("/copy/" + id)
        },

        beforeAvatarUpload(rawFile) {
            if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
                popMessage("错误", "必须是jpg或png图片")
                return false
            } else if (rawFile.size / 1024 / 1024 > 2) {
                popMessage("错误", "不得大于2MB")
                return false
            }
            return true
        },
        upload(param) {
            http.upload("/upload", param.file)
                .then(res => {
                    this.user.avatar = "http://124.71.194.255:8081/image/" + res.data.content.file
                    this.waiteToEdit = res.data.content.file
                    popMessage("提示", "上传成功")
                })
                .catch(err => {
                    popMessage("错误", err.message)
                })
        },
        editUser() {
            const data = {
                avatar: this.waiteToEdit
            }
            http.post("/user/edit", data,
                () => {
                    this.waiteToEdit = ""
                    popMessage("提示", "修改成功")
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
        editRole() {
            const data = {
                id: this.user.id,
                role: this.select_role
            }
            http.post("/admin/role", data,
                () => {
                    popMessage("提示", "修改成功")
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
    },

    components: {
        ChatSquare,
        Document,
        User,
        Tools,
        DocumentCopy,
    },
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
    margin: 5%;
}

.avatar {
    width: 100px;
    height: 100px;
}

.plugin {
    margin-bottom: 1%;
}

.plugin:hover{
    cursor: pointer;
    background-color: rgba(135, 206, 235, 0.2);
}

.time {
    font-size: small;
    color: gray;
}

.pagination {
    justify-content: center;
}
</style>
