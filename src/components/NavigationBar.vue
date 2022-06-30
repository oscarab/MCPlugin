<template>
    <div class="nav">
        <el-image src="/logo.png" alt="" class="logo" @click="to('/')"/>
        <el-input
            v-model="searchText"
            placeholder="搜索..."
            size="large"
            class="searchInput"
            @keyup.enter="search"
        >
            <template #prefix>
                <el-icon class="el-input__icon"><search /></el-icon>
            </template>
        </el-input>
<!--        导航栏         -->
        <el-menu
            mode="horizontal"
            class="menu-bar"
            text-color="#409eff">
            <el-menu-item v-for="(item,i) in navList"
                          :key="i"
                          :index="item.path"
                          class="menu-item"
                          @click="to(item.path)">
                {{ item.item }}
            </el-menu-item>
        </el-menu>
    </div>
</template>

<script>
import { Search } from '@element-plus/icons-vue'

export default {
    name: "NavigationBar",
    data() {
        return {
            navList: [
                {path: "/", item: "主页"},
                {path: "/plugins", item: "分类"}
            ],
            searchText: ""
        }
    },
    components: {
        Search
    },
    methods: {
        to(url) {
            this.$router.push(url)
        },

        search() {
            const text = this.searchText
            this.searchText = ""
            this.$router.push({
                path: "/search",
                query: {
                    info: text
                }
            })
        }
    }
}

</script>

<style scoped>

a {
    text-decoration: none;
}

.nav {
    margin-left: 10%;
    margin-right: 10%;
    margin-top: 0.5%;
}

.menu-bar {
    border-radius: 8px;
    box-shadow: 1px 2px 5px 0 rgba(0,0,0,.08);

    margin-top: 0.5%;
}

.menu-item {
    font-size: medium;
    font-weight: bold;
    width: 120px;
}

.menu-item:hover {
    background-color: rgba(64, 158, 255, 0.5) !important;
}

.searchInput {
    margin-left: 65%;
    margin-top: 2%;
    width: 20%;
}

.logo {
    float: left;
    width: 15%;
}

.logo:hover {
    cursor: pointer;
}

</style>
