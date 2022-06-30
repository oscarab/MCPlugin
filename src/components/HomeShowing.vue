<template>
    <div class="show">
        <el-carousel trigger="click" indicator-position="inside">
            <el-carousel-item v-for="(item,i) in showing" :key="i">
                <el-image :src="item" fit="cover" class="show_img"/>
            </el-carousel-item>
        </el-carousel>
    </div>
</template>

<script>
export default {
    name: "HomeShowing",

    data() {
        return {
            showing: [
                "https://z3.ax1x.com/2021/08/22/hpVyZD.png",
                "https://attachment.mcbbs.net/data/myattachment/forum/202108/17/203025joq3yzoi3wo9zonx.png",
                "https://attachment.mcbbs.net/data/myattachment/forum/202203/06/003949ojo7xpf7jxo6lsj7.png"
            ]
        }
    }
}
</script>

<style scoped>

.show {
    margin: 1% 2%;
    border-radius: 8px;
    height: 100%;
}

.show_img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    cursor: pointer;
    border-radius: 8px;
}

</style>
