<template>
    <el-card class="tab">
        <el-tabs v-model="activate" class="plugin-tab" >
            <el-tab-pane label="最热门" name="first">
                <el-empty v-if="!hotPlugins || hotPlugins.length===0" description="暂无" />
                <el-card v-else v-for="(plugin, key) in hotPlugins"
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
            </el-tab-pane>
            <el-tab-pane label="最近更新" name="second">
                <el-empty v-if="!updatedPlugins || updatedPlugins.length===0" description="暂无" />
                <el-card v-for="(plugin, key) in updatedPlugins"
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
            </el-tab-pane>
        </el-tabs>
    </el-card>
</template>

<script>
import http from "@/http/http"
import {popMessage} from "@/http/api"

export default {
    name: "HomePluginList",
    data() {
        return {
            activate: "first",

            updatedPlugins: [],
            hotPlugins: [],

            plugin_type_map: { "Fun": "娱乐", "RPG": "RPG", "Comprehensive": "综合",
                "Information": "信息", "Manage": "管理", "Economic": "经济",
                "Security": "安全", "API": "API", "Other" : "其他" },
        }
    },

    mounted() {
        http.get("/plugins/page/0/3", null,
            res => {
                this.updatedPlugins = res.content.page
            },
            err => {
                popMessage("错误", err.message)
            })
        http.get("/plugins/hot", null,
            res => {
                this.hotPlugins = res.content
            },
            err => {
                popMessage("错误", err.message)
            })
    },

    methods: {
        gotoInfo(id) {
            this.$router.push("/plugin/" + id)
        },
    },

}
</script>

<style scoped>
.tab {
    margin: 2% 2% 5%;
    height: 100%;
    border-radius: 8px;
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
</style>
