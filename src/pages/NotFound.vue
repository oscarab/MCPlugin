<template>
    <el-card class="big_card">
        <el-empty description="404" :image-size="300"/>
    </el-card>
</template>

<script>
export default {
    name: "NotFound"
}
</script>

<style scoped>
.big_card {
    margin-left: 10%;
    margin-right: 10%;
    margin-top: 2%;
    height: 100vh;
    border-radius: 10px;
}
</style>
