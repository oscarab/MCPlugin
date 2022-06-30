<template>
    <el-card class="big_card" v-loading="loading">
        <template #header>
            <div> 搜索结果 </div>
        </template>
        <el-card v-for="(plugin, key) in plugins"
                 :key="key"
                 class="plugin"
                 @click="gotoInfo(plugin.id)">
            <el-row :gutter="1" justify="space-around">
                <el-col :span="5">
                    <span>{{ plugin.name }}</span>
                </el-col>
                <el-col :span="8">
                    <el-space wrap>
                        <el-tag> {{ plugin.serverType }} </el-tag>
                        <el-tag> {{ plugin.serverVersion }} </el-tag>
                        <el-tag> {{ plugin_type_map[plugin.pluginType] }} </el-tag>
                    </el-space>
                </el-col>
                <el-col :span="8">
                    <span class="time"> 更新时间: {{ plugin.updateTime }} <br/></span>
                    <span class="time"> 发表时间: {{ plugin.publishTime }} </span>
                </el-col>
            </el-row>
        </el-card>
        <el-empty v-if="!plugins" description="无结果" />
        <el-pagination background
                       layout="prev, pager, next"
                       :page-count="total"
                       v-model:current-page="page"
                       hide-on-single-page
                       class="pagination"/>
    </el-card>
</template>

<script>
import http from "@/http/http";
import {popMessage} from "@/http/api";

export default {
    name: "SearchPage",

    data() {
        return {
            loading: false,

            plugins: [],
            total: 1,
            page: 1,

            plugin_type_map: { "Fun": "娱乐", "RPG": "RPG", "Comprehensive": "综合",
                "Information": "信息", "Manage": "管理", "Economic": "经济",
                "Security": "安全", "API": "API", "Other" : "其他" },
        }
    },

    mounted() {
        this.fetchData()
    },

    watch: {
        page(val) {
            this.fetchData(val)
        }
    },

    methods: {
        fetchData() {
            this.loading = true
            const data = {
                info: this.$route.query.info,
                page: this.page - 1,
                size: 10,
            }
            http.post("/plugins/fuzzy", data,
                res => {
                    this.loading = false
                    this.plugins = res.content.page
                    this.total = res.content.total
                },
                err => {
                    popMessage("错误", err.message)
                    this.$router.replace("/404")
                })
        },
        gotoInfo(id) {
            this.$router.push("/plugin/" + id)
        },
    },
}
</script>

<style scoped>
.big_card {
    margin-left: 10%;
    margin-right: 10%;
    margin-top: 1%;
    height: 100vh;
    border-radius: 10px;
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
