<template>
    <NavigationBar/>
    <router-view/>
</template>

<script>
import NavigationBar from "@/components/NavigationBar";

export default {
    name: "WebHome",
    components: {
        NavigationBar
    }
}

</script>

<style scoped>

</style>
