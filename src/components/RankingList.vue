<template>
    <el-card class="rank">
        <template #header>
            <div> 友情链接 </div>
        </template>
        <el-space wrap direction="vertical">
            <el-link type="primary"
                     v-for="(link,i) in links"
                     :key="i"
                     :href="link.link">
                {{ link.name }}
            </el-link>
        </el-space>
    </el-card>
</template>

<script>
export default {
    name: "RankingList",

    data() {
        return {
            links: [
                {
                    name: "mcbbs",
                    link: "https://www.mcbbs.net"
                },
                {
                    name: "Minecraft wiki",
                    link: "https://minecraft.fandom.com/zh/wiki/Minecraft_Wiki"
                },
                {
                    name: "Minecraft插件百科",
                    link: "https://mineplugin.org/%E9%A6%96%E9%A1%B5"
                },
                {
                    name: "Minecraft百度贴吧",
                    link: "https://tieba.baidu.com/f?kw=Minecraft"
                }
            ]
        }
    }
}
</script>

<style scoped>
.rank {
    margin: 5% 2%;
    height: 100%;
    border-radius: 8px;
}
</style>
