<template>
    <el-card>
        <el-container>
            <el-aside width="20%">
                <el-tag style="font-size: medium"> 作者 </el-tag>
                <el-card v-for="(author, key) in plugin.authors" :key="key"
                         style="margin-top: 3%"
                         class="pointer"
                        @click="gotoUser(author.id)">
                    <el-row justify="space-around">
                        <el-col :span="1">
                            <div>
                                <el-avatar class="avatar"
                                           :src="author.avatar"
                                           size="large">
                                    <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt="">
                                </el-avatar>
                            </div>
                        </el-col>
                        <el-col :span="2" style="margin-top: 8%">
                            <div style="font-size: large; font-weight: bold"> {{ author.username }} </div>
                        </el-col>
                    </el-row>
                </el-card>
            </el-aside>
            <el-main>
                <el-descriptions
                    class="table"
                    :column="2"
                    border
                >
                    <el-descriptions-item label="插件名称">
                        {{ plugin.name }}
                    </el-descriptions-item>
                    <el-descriptions-item label="适用服务端">
                        {{ plugin.serverType }}
                    </el-descriptions-item>
                    <el-descriptions-item label="适用版本">
                        {{ plugin.serverVersion }}
                    </el-descriptions-item>
                    <el-descriptions-item label="插件类型">
                        {{ plugin_type_map[plugin.pluginType] }}
                    </el-descriptions-item>
                    <el-descriptions-item label="前置插件">
                        {{ plugin.depends }}
                    </el-descriptions-item>
                    <el-descriptions-item label="下载地址">
                        {{ plugin.download }}
                    </el-descriptions-item>
                </el-descriptions>
                <el-divider/>
                <div style="font-size: small; color: darkgray; margin-bottom: 1%">
                    发表于 {{ plugin.publishTime }}
                </div>
                <el-collapse>
                    <el-collapse-item title="前言">
                        <div style="width: 100%; text-align: left" v-html="plugin.preface"/>
                    </el-collapse-item>
                    <el-collapse-item title="功能列表">
                        <div style="width: 100%; text-align: left" v-html="plugin.functionList"/>
                    </el-collapse-item>
                    <el-collapse-item title="使用方法">
                        <div style="width: 100%; text-align: left" v-html="plugin.usageMethod"/>
                    </el-collapse-item>
                    <el-collapse-item title="更新记录">
                        <div style="width: 100%; text-align: left" v-html="plugin.updateRecord"/>
                    </el-collapse-item>
                </el-collapse>
                <el-divider/>
                <div style="font-size: small; color: darkgray">
                    编辑于 {{ plugin.updateTime }}
                </div>
                <el-button type="primary"
                           style="margin-top: 2%"
                           v-if="plugin.pluginDocument"
                           @click="gotoDocument">查阅插件文档</el-button>
                <el-button type="primary"
                           style="margin-top: 2%"
                           @click="editPlugin"
                           v-if="isAuthor || role==='ROLE_admin'">
                    <el-icon>
                        <Edit />
                    </el-icon>
                    编辑</el-button>
                <el-button type="primary"
                           style="margin-top: 2%"
                           @click="addDocument"
                           v-if="(isAuthor || role==='ROLE_admin') && !plugin.pluginDocument">
                    添加插件文档</el-button>
                <el-popconfirm
                    confirm-button-text="确认"
                    cancel-button-text="取消"
                    title="确认要删除该插件吗？"
                    @confirm="deletePlugin"
                >
                    <template #reference>
                        <el-button type="danger"
                                   style="margin-top: 2%"
                                   v-if="role === 'ROLE_admin'">删除</el-button>
                    </template>
                </el-popconfirm>
            </el-main>
        </el-container>
    </el-card>
</template>

<script>
import storage, {popMessage} from "@/http/api";
import {
    Edit
} from '@element-plus/icons-vue'
import http from "@/http/http";

export default {
    name: "PluginInfo",
    props: {
        plugin: Object
    },
    data() {
        return {
            isAuthor: false,
            role: "",

            plugin_type_map: { "Fun": "娱乐", "RPG": "RPG", "Comprehensive": "综合",
                "Information": "信息", "Manage": "管理", "Economic": "经济",
                "Security": "安全", "API": "API", "Other" : "其他" },
        }
    },

    mounted() {
        const user = storage.load("user")
        this.plugin.authors.map((author) => {
            if (author.id === user.id) {
                this.isAuthor = true
            }
        })
        if (user.id !== 0)
            this.role = user.authorities[0].authority
    },

    methods: {
        editPlugin() {
          this.$router.push("/plugin/" + this.plugin.id + "/edit")
        },
        gotoDocument() {
            this.$router.push("/document/" + this.plugin.pluginDocument.id)
        },
        addDocument() {
            this.$router.push({
                path: "/document/0/edit",
                query: {
                    plugin_id: this.plugin.id
                }})
        },
        deletePlugin() {
            http.get("/admin/plugin/" + this.plugin.id, null,
                () => {
                    this.$router.back()
                    popMessage("提示", "删除成功")
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
        gotoUser(id) {
            this.$router.push("/user/" + id)
        },
    },

    components: {
        Edit
    }

}
</script>

<style scoped>
.pointer:hover {
    cursor: pointer;
}
</style>
