<template>
    <el-card class="big_card" v-loading="loading">
        <el-container>
            <el-main>
                <el-button class="add"
                           type="primary"
                           v-if="role==='ROLE_dev' || role==='ROLE_admin'"
                           @click="addPlugin">发表插件</el-button>
                <div class="search">
                    <el-row>
                        <el-col :span="2">
                            <span>插件名称</span>
                        </el-col>
                        <el-col :span="3">
                            <el-input v-model="search_name"/>
                        </el-col>
                        <el-col :span="2">
                            <span>插件类型</span>
                        </el-col>
                        <el-col :span="3">
                            <el-select v-model="search_pluginType" placeholder="请选择">
                                <el-option
                                    v-for="item in plugin_type_option"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                />
                            </el-select>
                        </el-col>
                        <el-col :span="2">
                            <span>服务端类型</span>
                        </el-col>
                        <el-col :span="3">
                            <el-select v-model="search_serverType" placeholder="请选择">
                                <el-option
                                    v-for="item in type_option"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                />
                            </el-select>
                        </el-col>
                        <el-col :span="2">
                            <span>服务端版本</span>
                        </el-col>
                        <el-col :span="3">
                            <el-input v-model="search_serverVersion"/>
                        </el-col>
                        <el-col :span="3">
                            <el-button type="primary"
                                       @click="search(true)">搜索</el-button>
                        </el-col>

                    </el-row>
                </div>
                <el-divider/>
                <el-card v-for="(plugin, key) in plugins"
                         :key="key"
                         class="plugin"
                        @click="gotoInfo(plugin.id)">
                    <el-row :gutter="17" justify="space-around">
                        <el-col :span="5">
                            <span>{{ plugin.name }}</span>
                        </el-col>
                        <el-col :span="5">
                            <el-space wrap>
                                <el-tag> {{ plugin.serverType }} </el-tag>
                                <el-tag> {{ plugin.serverVersion }} </el-tag>
                                <el-tag> {{ plugin_type_map[plugin.pluginType] }} </el-tag>
                            </el-space>
                        </el-col>
                        <el-col :span="2">
                            <el-link v-for="(u, key) in plugin.authors" :key="key">
                                <el-icon><Avatar/></el-icon> {{ u.username }}
                            </el-link>
                        </el-col>
                        <el-col :span="5">
                            <span class="time"> 更新时间: {{ plugin.updateTime }} <br/></span>
                            <span class="time"> 发表时间: {{ plugin.publishTime }} </span>
                        </el-col>
                    </el-row>
                </el-card>
            </el-main>
            <el-footer>
                <el-pagination background
                               layout="prev, pager, next"
                               :page-count="total"
                               v-model:current-page="page"
                                class="pagination"/>
            </el-footer>
        </el-container>
    </el-card>
</template>

<script>
import http from "@/http/http";
import storage, {popMessage} from "@/http/api";
import { Avatar } from '@element-plus/icons-vue'

export default {
    name: "PluginsPage",

    data() {
        return {
            loading: false,
            in_search: false,
            role: "",

            page: 1,
            size: 5,
            total: 1,
            plugins: [],

            search_name: "",
            search_pluginType: "",
            search_serverType: "",
            search_serverVersion: "",

            plugin_type_map: { "Fun": "娱乐", "RPG": "RPG", "Comprehensive": "综合",
                "Information": "信息", "Manage": "管理", "Economic": "经济",
                "Security": "安全", "API": "API", "Other" : "其他" },
            type_option: [
                {
                    value: "0",
                    label: "CraftBukkit"
                },
                {
                    value: "1",
                    label: "Paper"
                },
                {
                    value: "2",
                    label: "Spigot"
                },
                {
                    value: "3",
                    label: "CatServer"
                },
                {
                    value: "4",
                    label: "Sponge"
                },
                {
                    value: "5",
                    label: "BungeeCord"
                },
                {
                    value: "6",
                    label: "其他"
                },
            ],
            plugin_type_option: [
                {
                    value: "0",
                    label: "娱乐"
                },
                {
                    value: "1",
                    label: "RPG"
                },
                {
                    value: "2",
                    label: "综合"
                },
                {
                    value: "3",
                    label: "信息"
                },
                {
                    value: "4",
                    label: "管理"
                },
                {
                    value: "5",
                    label: "经济"
                },
                {
                    value: "6",
                    label: "安全"
                },
                {
                    value: "7",
                    label: "API"
                },
                {
                    value: "8",
                    label: "其他"
                },
            ],
        }
    },

    mounted() {
        const user = storage.load("user")
        if (user.id !== 0)
            this.role = user.authorities[0].authority
        this.fetchData(this.page)
    },

    watch: {
        page(new_page) {
            if (this.in_search) {
                this.search(false)
            }
            else {
                this.fetchData(new_page)
            }
        },
    },

    methods: {
        gotoInfo(id) {
            this.$router.push("/plugin/" + id)
        },
        addPlugin() {
            this.$router.push("/plugin/0/edit")
        },
        fetchData(page) {
            this.loading = true
            http.get("/plugins/page/" + (page - 1) + "/" + this.size, null,
                res => {
                    this.loading = false
                    this.plugins = res.content.page
                    this.total = res.content.total
                }, err => {
                    popMessage("错误", err.message)
                })
        },
        search(init) {
            this.loading = true
            if (init) this.page = 1
            const data = {
                name: this.search_name,
                pluginType: this.search_pluginType,
                serverType: this.search_serverType,
                serverVersion: this.search_serverVersion,
                page: this.page - 1,
                size: this.size
            }
            http.post("/plugins/exact", data,
                res => {
                    this.loading = false
                    this.plugins = res.content.page
                    this.total = res.content.total
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
    },
    components: {
        Avatar
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

.el-container {
    min-height: 100vh;
}

.search {
    margin-bottom: 2%;
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

.add {
    margin-bottom: 1%;
    margin-left: 1%;
    display: flex;
}
</style>
