<template>
    <el-card class="big_card" v-loading="copy.id===undefined">
        <el-tag type="info"> 副本 </el-tag>
        <div class="time"> 编辑时间: {{ copy.updateTime }} </div>
        <el-container v-if="copy.id!==undefined">
            <el-aside width="20%">
                <el-menu
                    default-active="1"
                    style="min-height: 100vh"
                >
                    <el-menu-item index="1" @click="this.show=this.copy.explanation">
                        <el-icon><document /></el-icon>
                        <span>详细说明</span>
                    </el-menu-item>
                    <el-menu-item index="2" @click="this.show=this.copy.api">
                        <el-icon><Files /></el-icon>
                        <span>API</span>
                    </el-menu-item>
                    <el-menu-item index="3" @click="this.show=this.copy.faq">
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
    </el-card>
</template>

<script>
import {
    Document,
    QuestionFilled,
    Files,
} from '@element-plus/icons-vue'
import http from "@/http/http";
import storage from "@/http/api";

export default {
    name: "CopyPage",

    data() {
        return {
            copy: {},
            show: "",
        }
    },

    mounted() {
        http.get("/copy/" + this.$route.params.id + "/info", null,
            res => {
                this.copy = res.content
                this.show = this.copy.explanation

                const user = storage.load("user")
                this.isAuthor = user.id === this.copy.author.id
            })
    },

    components: {
        Document,
        QuestionFilled,
        Files,
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

.time {
    font-size: small;
    color: gray;
}
</style>
