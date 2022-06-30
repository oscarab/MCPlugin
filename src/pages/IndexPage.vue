<template>
    <el-card class="big_card">
        <div style="margin-bottom: 2%">
            <el-row class="el-row-inline">
                <el-col :span="17">
                    <HomeShowing/>
                </el-col>
                <el-col :span="7">
                    <InfoCard/>
                </el-col>
            </el-row>
            <el-row class="el-row-inline">
                <el-col :span="17">
                    <HomePluginList/>
                </el-col>
                <el-col :span="7">
                    <RankingList/>
                </el-col>
            </el-row>
        </div>
    </el-card>
</template>

<script>
import InfoCard from "@/components/InfoCard"
import HomePluginList from "@/components/HomePluginList"
import HomeShowing from "@/components/HomeShowing";
import RankingList from "@/components/RankingList";

export default {
    name: "IndexPage",
    components: {
        RankingList,
        HomeShowing,
        InfoCard,
        HomePluginList
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

.el-row-inline {
    display: flex;
    flex-wrap: wrap;
}
</style>
